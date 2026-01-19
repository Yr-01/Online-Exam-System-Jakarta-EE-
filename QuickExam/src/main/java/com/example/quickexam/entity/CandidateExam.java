package com.example.quickexam.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "candidate_exams")
public class CandidateExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_exam_id")
    private Long candidateExamId;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private LocalDate date;

    @Temporal(TemporalType.TIME)
    @Column(name = "heure_debut")
    private LocalTime heureDebut;

    @Temporal(TemporalType.TIME)
    @Column(name = "heure_fin")
    private LocalTime heureFin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.createdAt = this.createdAt.plusHours(1);
    }

    public Long getCandidateExamId() {return candidateExamId;}
    public void setCandidateExamId(Long candidateExamId) {this.candidateExamId = candidateExamId;}
    public Exam getExam() {return exam;}
    public void setExam(Exam exam) {this.exam = exam;}
    public User getCandidate() {return candidate;}
    public void setCandidate(User candidate) {this.candidate = candidate;}
    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}
    public LocalTime  getHeureDebut() {return heureDebut;}
    public void setHeureDebut(LocalTime  heureDebut) {this.heureDebut = heureDebut;}
    public LocalTime  getHeureFin() {return heureFin;}
    public void setHeureFin(LocalTime  heureFin) {this.heureFin = heureFin;}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}