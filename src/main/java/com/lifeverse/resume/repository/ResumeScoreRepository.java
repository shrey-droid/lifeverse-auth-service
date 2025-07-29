package com.lifeverse.resume.repository;

import com.lifeverse.resume.entity.ResumeScore;
import com.lifeverse.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // ✅ Add this if missing
public interface ResumeScoreRepository extends JpaRepository<ResumeScore, Long> {
    List<ResumeScore> findByUser(User user);
}
