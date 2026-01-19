package com.example.quickexam.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Column(name = "type_question")
    private String typeQuestion;

    @Column(name = "nbr_question")
    private Integer nbrQuestion = 0;

    @Column(name = "question_time")
    private Integer questionTime = 2; // minutes

    @Column(name = "exam_time")
    private Integer examTime;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Question> questions = new ArrayList<>();
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CandidateExamScore> candidateExamScores = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void calculateExamTime() {
        if (nbrQuestion != null && questionTime != null) {
            this.examTime = nbrQuestion * questionTime;
        }
        this.createdAt = LocalDateTime.now();
    }

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
        question.setExam(this);
    }

    public Long getExamId() {return examId;}
    public void setExamId(Long examId) {this.examId = examId;}

    public String getTheme() {return theme;}
    public void setTheme(String theme) {this.theme = theme;}

    public String getTypeQuestion() {return typeQuestion;}
    public void setTypeQuestion(String typeQuestion) {this.typeQuestion = typeQuestion;}

    public Integer getNbrQuestion() {return nbrQuestion;}
    public void setNbrQuestion(Integer nbrQuestion) {this.nbrQuestion = nbrQuestion;}

    public Integer getQuestionTime() {return questionTime;}
    public void setQuestionTime(Integer questionTime) {this.questionTime = questionTime;}

    public Integer getExamTime() {return examTime;}
    public void setExamTime(Integer examTime) {this.examTime = examTime;}

    public User getCreatedBy() {return createdBy;}
    public void setCreatedBy(User createdBy) {this.createdBy = createdBy;}

    public List<Question> getQuestions() {return questions;}
    public void setQuestions(List<Question> questions) {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        } else {
            this.questions.clear();
        }
        if (questions != null) {
            for (Question question : questions) {
                addQuestion(question);
            }
        }
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}