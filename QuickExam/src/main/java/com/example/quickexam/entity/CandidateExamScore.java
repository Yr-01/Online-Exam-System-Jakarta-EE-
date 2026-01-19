package com.example.quickexam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_exam_scores")
public class CandidateExamScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_exam_score_id")
    private Long candidateExamScoreId;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "score", nullable = false)
    private Integer score;

    // Added the date column
    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishedAt;

    public CandidateExamScore() {}

    @PrePersist
    protected void onCreate() {
        this.finishedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getCandidateExamScoreId() { return candidateExamScoreId; }
    public void setCandidateExamScoreId(Long candidateExamScoreId) { this.candidateExamScoreId = candidateExamScoreId; }

    public User getCandidate() { return candidate; }
    public void setCandidate(User candidate) { this.candidate = candidate; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
}