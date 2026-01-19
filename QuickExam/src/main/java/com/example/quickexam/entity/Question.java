package com.example.quickexam.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<QuestionOption> options = new ArrayList<>();


    public void addOption(QuestionOption option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
        option.setQuestion(this);
    }

    public Long getQuestionId() {return questionId;}
    public void setQuestionId(Long questionId) {this.questionId = questionId;}

    public String getQuestionText() {return questionText;}
    public void setQuestionText(String questionText) {this.questionText = questionText;}

    public Exam getExam() {return exam;}
    public void setExam(Exam exam) {this.exam = exam;}

    public List<QuestionOption> getOptions() {return options;}
    public void setOptions(List<QuestionOption> options) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        } else {
            this.options.clear();
        }
        if (options != null) {
            for (QuestionOption option : options) {
                addOption(option);
            }
        }
    }
}