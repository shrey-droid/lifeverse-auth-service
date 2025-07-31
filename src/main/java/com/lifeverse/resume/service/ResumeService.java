package com.lifeverse.resume.service;

import com.lifeverse.auth.model.User;
import com.lifeverse.auth.repository.UserRepository;
import com.lifeverse.resume.dto.ResumeScoreResponse;
import com.lifeverse.resume.entity.ResumeScore;
import com.lifeverse.resume.openApi.OpenAiHelper;
import com.lifeverse.resume.repository.ResumeScoreRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private static final List<String> KEYWORDS = List.of(
            "java", "spring", "spring boot", "react", "aws", "docker",
            "kubernetes", "microservices", "sql", "mongodb", "rest api", "git", "jira"
    );

    private final ResumeScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final OpenAiHelper openAiHelper;


    /**
     * ✅ Process resume file, extract keywords, compute score, and save to DB
     */
    public ResumeScoreResponse processResume(MultipartFile file, String email) {
        try (InputStream inputStream = file.getInputStream(); PDDocument doc = PDDocument.load(inputStream)) {
            String text = new PDFTextStripper().getText(doc).toLowerCase();

            int matched = 0;
            List<String> matchedKeywords = new ArrayList<>();
            for (String keyword : KEYWORDS) {
                if (text.contains(keyword)) {
                    matched++;
                    matchedKeywords.add(keyword);
                }
            }

            int score = (int) (((double) matched / KEYWORDS.size()) * 100);
            String matchedStr = String.join(",", matchedKeywords);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ResumeScore resumeScore = ResumeScore.builder()
                    .score(score)
                    .matchedKeywords(matchedStr)
                    .timestamp(LocalDateTime.now())
                    .user(user)
                    .build();

            scoreRepository.save(resumeScore);

            return new ResumeScoreResponse(score, matchedKeywords, resumeScore.getTimestamp().toString());

        } catch (Exception e) {
            throw new RuntimeException("Error parsing resume", e);
        }
    }

    /**
     * ✅ Get all past scores for a user
     */
    public List<ResumeScore> getScoresByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return scoreRepository.findByUser(user);
    }

    /**
     * ✅ Compare resume and job description, return matched/missing keywords + score
     */
    public String tailorResumeToJob(MultipartFile resumeFile, MultipartFile jobFile) {
        try (InputStream resumeStream = resumeFile.getInputStream();
             InputStream jobStream = jobFile.getInputStream();
             PDDocument resumeDoc = PDDocument.load(resumeStream);
             PDDocument jobDoc = PDDocument.load(jobStream)) {

            String resumeText = new PDFTextStripper().getText(resumeDoc).toLowerCase();
            String jobText = new PDFTextStripper().getText(jobDoc).toLowerCase();

            List<String> present = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (String keyword : KEYWORDS) {
                if (jobText.contains(keyword)) {
                    if (resumeText.contains(keyword)) {
                        present.add(keyword);
                    } else {
                        missing.add(keyword);
                    }
                }
            }

            int totalMatchedKeywords = present.size();
            int totalJobKeywords = present.size() + missing.size();
            int score = totalJobKeywords == 0 ? 0 : (int) (((double) totalMatchedKeywords / totalJobKeywords) * 100);

            // 🧠 Step 1: Build prompt
            String prompt = "You're an expert resume writer. Please tailor the following resume to better match the job description. "
                    + "Reword the summary, emphasize relevant experience, and insert missing keywords where appropriate. "
                    + "Return only the new tailored resume content.\n\n"
                    + "Resume:\n" + resumeText + "\n\n"
                    + "Job Description:\n" + jobText;

            // 🧠 Step 2: Call OpenAI to generate tailored resume
            String tailoredResume = openAiHelper.sendToGPT(prompt); // or use generateTailoredResume()

            // 🧠 Step 3: Append keyword insight (optional)
            String insights = "\n\n✅ Matched: " + String.join(", ", present)
                    + "\n❌ Missing: " + String.join(", ", missing)
                    + "\n🎯 Resume Match Score: " + score + "%";

            return tailoredResume + insights;

//            return "✅ Matched: " + String.join(", ", present)
//                    + "\n❌ Missing: " + String.join(", ", missing)
//                    + "\n🎯 Resume Match Score: " + score + "%";

        } catch (Exception e) {
            throw new RuntimeException("Error tailoring resume", e);
        }
    }

    /**
     * ✅ Specialized resume parser for LinkedIn-style resumes
     */
    public ResumeScoreResponse processLinkedInResume(MultipartFile file, String email) {
        try (InputStream inputStream = file.getInputStream(); PDDocument doc = PDDocument.load(inputStream)) {
            String text = new PDFTextStripper().getText(doc).toLowerCase();

            // Heuristic: focus on section after "skills"
            String[] sections = text.split("skills");
            String skillsSection = sections.length > 1 ? sections[1] : text;

            int matched = 0;
            List<String> matchedKeywords = new ArrayList<>();
            for (String keyword : KEYWORDS) {
                if (skillsSection.contains(keyword)) {
                    matched++;
                    matchedKeywords.add(keyword);
                }
            }

            int score = (int) (((double) matched / KEYWORDS.size()) * 100);

            return new ResumeScoreResponse(score, matchedKeywords, LocalDateTime.now().toString());

        } catch (Exception e) {
            throw new RuntimeException("Error processing LinkedIn resume", e);
        }
    }

    public String fixResumeWithAI(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             PDDocument doc = PDDocument.load(inputStream)) {

            String resumeText = new PDFTextStripper().getText(doc);
            String prompt = "You're an expert resume coach. Please analyze the following resume and provide suggestions to improve formatting, structure, and keyword relevance:\n\n" + resumeText;

            return openAiHelper.sendToGPT(prompt);  // ✅ Works now

        } catch (Exception e) {
            throw new RuntimeException("Error fixing resume with AI", e);
        }
    }
}

