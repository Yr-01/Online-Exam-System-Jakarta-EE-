package com.example.quickexam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "code_sessions")
public class CodeSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_session_id")
    private Long codeSessionId;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    @ManyToOne
    @JoinColumn(name = "candidate_exam_id", nullable = false)
    private CandidateExam candidateExam;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();
    @Column(name = "used")
    private Boolean used = false;

    public Long getCodeSessionId() {return codeSessionId;}
    public void setCodeSessionId(Long codeSessionId) {this.codeSessionId = codeSessionId;}

    public User getCandidate() {return candidate;}
    public void setCandidate(User candidate) {this.candidate = candidate;}

    public Exam getExam() {return exam;}
    public void setExam(Exam exam) {this.exam = exam;}
    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public Date getCreatedAt() {return createdAt;}
    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}
    public Boolean getUsed() {return used;}
    public void setUsed(Boolean used) {this.used = used;}
    public CandidateExam getCandidateExam() {return candidateExam;}
    public void setCandidateExam(CandidateExam candidateExam) {this.candidateExam = candidateExam;}
}