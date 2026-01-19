package com.example.quickexam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidate_answers")
public class CandidateAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_answer_id")
    private Long candidateAnswerId;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "question_option_id")
    private QuestionOption selectedOption;

    @Column(name = "candidate_answer_question")
    private Boolean isCorrect = false;


    public Long getCandidateAnswerId() {return candidateAnswerId;}
    public void setCandidateAnswerId(Long candidateAnswerId) {this.candidateAnswerId = candidateAnswerId;}

    public Exam getExam() {return exam;}
    public void setExam(Exam exam) {this.exam = exam;}

    public User getCandidate() {return candidate;}
    public void setCandidate(User candidate) {this.candidate = candidate;}

    public Question getQuestion() {return question;}
    public void setQuestion(Question question) {this.question = question;}

    public QuestionOption getSelectedOption() {return selectedOption;}
    public void setSelectedOption(QuestionOption selectedOption) {this.selectedOption = selectedOption;}

    public Boolean getCorrect() {return isCorrect;}
    public void setCorrect(Boolean correct) {isCorrect = correct;}

}