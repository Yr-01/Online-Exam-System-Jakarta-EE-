package com.example.quickexam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_options")
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_option_id")
    private Long questionOptionId;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;


    public Long getQuestionOptionId() {return questionOptionId;}
    public void setQuestionOptionId(Long questionOptionId) {this.questionOptionId = questionOptionId;}

    public String getOptionText() {return optionText;}
    public void setOptionText(String optionText) {this.optionText = optionText;}

    public Boolean getCorrect() {return isCorrect;}
    public void setCorrect(Boolean correct) {isCorrect = correct;}

    public Question getQuestion() {return question;}
    public void setQuestion(Question question) {this.question = question;}
}
