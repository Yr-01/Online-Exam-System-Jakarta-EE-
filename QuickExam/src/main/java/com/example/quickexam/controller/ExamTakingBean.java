package com.example.quickexam.controller;

import com.example.quickexam.entity.*;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.service.MailService;
import com.example.quickexam.session.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class ExamTakingBean implements Serializable {

    private Long examId;
    private Exam exam = new Exam();
    @Inject
    private MailService  mailService;
    @Inject
    private ExamDAO examDAO;
    private Integer timeQuestion;
    private List<Question> questions = new ArrayList<>();
    private List<Question> displayQuestions = new ArrayList<>();
    private List<Question> copyDisplayQuestions;
    private String examName = null;
    private String questionText = null;
    private List<QuestionOption> questionOptions = new ArrayList<>();
    private List<CandidateAnswer> candidateAnswers = new ArrayList<>();
    private CandidateAnswer candidateAnswer;
    @Inject
    private UserSession userSession;
    private Boolean displayScore = false;
    private Integer score = 0;
    private Integer nbrQuestions = 0;
    private String timeRemaining ;
    private Integer currentSecondsLeft;



    @PostConstruct
    private void init() {

        FacesContext context = FacesContext.getCurrentInstance();

        Object flashExamId = context.getExternalContext().getFlash().get("passExamId");
        if (flashExamId != null) {
            examId = (Long) flashExamId;
            loadExam(examId);
        } else {
            Object sessionExamId = context.getExternalContext().getSessionMap().get("passExamId");
            if (sessionExamId != null) {
                examId = (Long) sessionExamId;
                loadExam(examId);
                context.getExternalContext().getSessionMap().remove("passExamId");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No exam was found. Please enter a valid code or contact the administrator."));
            }
        }
    }

    private void loadExam(Long examId) {
        displayScore = false;
        exam = examDAO.getExamById(examId);
        examName = exam.getTheme();
        nbrQuestions = exam.getNbrQuestion();
        if(exam.getQuestionTime() < 1){
            timeQuestion = exam.getQuestionTime();
        }else {
            timeQuestion = exam.getQuestionTime()*60;
        }

        questions = exam.getQuestions();

        Collections.shuffle(questions);

        this.displayQuestions = questions.stream()
                .limit(exam.getNbrQuestion())
                .collect(Collectors.toList());

        this.copyDisplayQuestions = createDeepCopy(displayQuestions);
        this.currentSecondsLeft = timeQuestion;
        updateTimeRemainingString();
        getQuestions();
    }

    private List<Question> createDeepCopy(List<Question> originalList) {
        List<Question> copy = new ArrayList<>();

        for (Question original : originalList) {
            Question questionCopy = new Question();
            questionCopy.setQuestionId(original.getQuestionId());
            questionCopy.setQuestionText(original.getQuestionText());
            questionCopy.setExam(original.getExam());

            // Deep copy options
            List<QuestionOption> optionsCopy = new ArrayList<>();
            if (original.getOptions() != null) {
                for (QuestionOption originalOption : original.getOptions()) {
                    QuestionOption optionCopy = new QuestionOption();
                    optionCopy.setQuestionOptionId(originalOption.getQuestionOptionId());
                    optionCopy.setOptionText(originalOption.getOptionText());
                    optionCopy.setCorrect(originalOption.getCorrect());
                    optionCopy.setQuestion(questionCopy);
                    optionsCopy.add(optionCopy);
                }
            }
            questionCopy.setOptions(optionsCopy);
            copy.add(questionCopy);
        }

        return copy;
    }

    private void getQuestions() {
        if(displayQuestions.isEmpty()) {
            questionText = null;
            questionOptions = null;
            candidateAnswer = null;
            getScoreExam();
        }else {
            questionText = displayQuestions.getFirst().getQuestionText();
            questionOptions = displayQuestions.getFirst().getOptions().stream()
                    .peek(questionOption -> questionOption.setCorrect(false))
                    .collect(Collectors.toList());
        }
    }

    public void nextQuestion() {

        this.currentSecondsLeft = timeQuestion;
        updateTimeRemainingString();

        for (QuestionOption questionOption: questionOptions) {

            candidateAnswer =  new CandidateAnswer();
            candidateAnswer.setCandidate(userSession.getCurrentUser());
            candidateAnswer.setExam(exam);
            candidateAnswer.setQuestion(displayQuestions.getFirst());
            candidateAnswer.setSelectedOption(questionOption);
            candidateAnswer.setCorrect(questionOption.getCorrect());
            candidateAnswers.add(candidateAnswer);
        }
        displayQuestions.removeFirst();
        getQuestions();
    }

    public void calculateTimeRemaining() {
        if (currentSecondsLeft > 0) {
            currentSecondsLeft--;
            updateTimeRemainingString();
        } else {
            nextQuestion();
        }
    }
    private void updateTimeRemainingString() {
        int mins = currentSecondsLeft / 60;
        int secs = currentSecondsLeft % 60;
        this.timeRemaining = String.format("%02d:%02d", mins, secs);
    }

    private void getScoreExam() {
        this.score = 0;

        for (Question question : copyDisplayQuestions) {
            boolean allOptionsMatch = true;

            for (QuestionOption correctOption : question.getOptions()) {
                // Find if candidate selected this option
                CandidateAnswer candidateSelection = candidateAnswers.stream()
                        .filter(ca -> ca.getQuestion().getQuestionId().equals(question.getQuestionId()))
                        .filter(ca -> ca.getSelectedOption().getQuestionOptionId().equals(correctOption.getQuestionOptionId()))
                        .findFirst()
                        .orElse(null);

                boolean candidateSelected = candidateSelection != null && candidateSelection.getCorrect();
                boolean shouldBeSelected = correctOption.getCorrect();

                if (candidateSelected != shouldBeSelected) {
                    allOptionsMatch = false;
                    break;
                }
            }

            if (allOptionsMatch) {
                this.score++;
            }
        }

        saveScoreAndAnswers();
        displayScore = true;
    }

    private void saveScoreAndAnswers(){
        for (CandidateAnswer candidateAnswer:candidateAnswers) {
            examDAO.saveCandidateAnswer(candidateAnswer);
        }
        CandidateExamScore candidateExamScore = new CandidateExamScore();
        candidateExamScore.setExam(exam);
        candidateExamScore.setCandidate(userSession.getCurrentUser());
        candidateExamScore.setScore(score);
        examDAO.saveCandidateExamScore(candidateExamScore);
        mailService.sendFinishedExam(userSession.getCurrentUser().getEmail(),userSession.getCurrentUser().getPrenom()+' '+userSession.getCurrentUser().getNom(),score,exam.getTheme(),exam.getNbrQuestion());

    }

    public String getExamName() {return examName;}
    public void setExamName(String examName) {this.examName = examName;}
    public String getQuestionText() {return questionText;}
    public void setQuestionText(String questionText) {this.questionText = questionText;}
    public List<QuestionOption> getQuestionOptions() {return questionOptions;}
    public void setQuestionOptions(List<QuestionOption> questionOptions) {this.questionOptions = questionOptions;}
    public Integer getTimeQuestion() {return timeQuestion;}
    public void setTimeQuestion(Integer timeQuestion) {this.timeQuestion = timeQuestion;}
    public Boolean getDisplayScore() {return displayScore;}
    public void setDisplayScore(Boolean displayScore) {this.displayScore = displayScore;}
    public Integer getNbrQuestions() {return nbrQuestions;}
    public void setNbrQuestions(Integer nbrQuestions) {this.nbrQuestions = nbrQuestions;}
    public Integer getScore() {return score;}
    public String getTimeRemaining() {return timeRemaining;}
}
