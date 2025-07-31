package com.lifeverse.resume.openApi;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiHelper {

    private final OpenAiService openAiService;

    public String sendToGPT(String prompt) {
        log.info("🧠 Sending prompt to OpenAI");
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo") // Or gpt-4
                .messages(List.of(new ChatMessage("user", prompt)))
                .temperature(0.7)
                .maxTokens(1000)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);

        String response = result.getChoices().get(0).getMessage().getContent();
        log.info("✅ GPT Response: \n{}", response);

        return response;
    }
}



