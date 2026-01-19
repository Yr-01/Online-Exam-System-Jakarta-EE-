package com.example.quickexam.controller;

import com.example.quickexam.entity.CandidateExam;
import com.example.quickexam.entity.CodeSession;
import com.example.quickexam.entity.Exam;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.service.MailService;
import com.example.quickexam.session.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Named
@ViewScoped
public class AccessExamBean implements Serializable {
    private LocalDate date = LocalDate.now();
    private LocalTime startTime = LocalTime.now();
    private LocalTime endTime = LocalTime.now();
    private Long examId ;
    @Inject
    private ExamDAO examDAO;
    private Exam exam;
    private CandidateExam candidateExam;
    @Inject
    private UserSession userSession;
    @Inject
    private MailService mailService;
    private String timeRemaining;
    private Long codeSessionId;

    @PostConstruct
    private void init(){
        FacesContext context = FacesContext.getCurrentInstance();

        Object flashExamId = context.getExternalContext().getFlash().get("examIdForRegister");
        Object flashCodeId = context.getExternalContext().getFlash().get("codeSession");
        if (flashExamId != null || flashCodeId != null) {
            examId = (Long) flashExamId;
            codeSessionId = (Long) flashCodeId;
            loadExam(examId);
        } else {
            // check session
            Object sessionExamId = context.getExternalContext().getSessionMap().get("examIdForRegister");
            Object sessionCodeId = context.getExternalContext().getSessionMap().get("codeSession");
            if (sessionExamId != null || sessionCodeId != null) {
                examId = (Long) sessionExamId;
                codeSessionId = (Long) sessionCodeId;
                loadExam(examId);
                // Clear from session after use
                context.getExternalContext().getSessionMap().remove("examIdForRegister");
                context.getExternalContext().getSessionMap().remove("codeSession");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No exam selected for update. Please select an exam from the list."));
            }
        }
    }
    private void loadExam(Long examId){
        exam = examDAO.getExamById(examId);
        CodeSession codeSession = examDAO.getCodeSessionWithId(codeSessionId);
        candidateExam = examDAO.getCandidateExamWithId(codeSession.getCandidateExam().getCandidateExamId());
        calculateEndTime();
    }

    public void calculateEndTime(){
        endTime = startTime.plusMinutes(exam.getExamTime());
        System.out.println(endTime.toString());
        System.out.println(examId);
    }

    public void calculateTimeRemaining() {
        timeRemaining = "0 days 00:00:00";

        if (candidateExam == null || candidateExam.getDate() == null || candidateExam.getHeureDebut() == null) {
            return;
        }

        LocalDate examDate = candidateExam.getDate();
        LocalTime examStartTime = candidateExam.getHeureDebut();

        LocalDateTime examStartDateTime = LocalDateTime.of(examDate, examStartTime);

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(examStartDateTime)) {

            Duration duration = Duration.between(now, examStartDateTime);

            long days = duration.toDays();
            long hours = duration.toHoursPart();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            if (days > 0) {
                timeRemaining = String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
            } else {
                timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
        }
        else {
            timeRemaining = "0 days 00:00:00";
            startExam();
        }
    }
    private void startExam(){
        try {
            CodeSession codeSession = examDAO.getCodeSessionWithId(codeSessionId);
            codeSession.setUsed(true);
            examDAO.updateCodeSession(codeSession);
            FacesContext.getCurrentInstance().getExternalContext().getFlash()
                    .put("passExamId", codeSession.getExam().getExamId());
            FacesContext.getCurrentInstance().getExternalContext().redirect("faces/views/exam/take-exam.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String registerForExam(){
        mailService.sendRegisterExam(userSession.getCurrentUser().getEmail(),
                userSession.getCurrentUser().getNom()+' '+ userSession.getCurrentUser().getPrenom(),
                exam.getTheme(), date.toString(), startTime.toString(), endTime.toString());
        candidateExam.setCandidateExamId(null);
        candidateExam.setDate((date));
        candidateExam.setHeureDebut((startTime));
        candidateExam.setHeureFin((endTime));
        examDAO.setCandidateExam(candidateExam);
        examDAO.updateCodeSessionWithId(codeSessionId);
        return "access?faces-redirect=true";
    }
    public String cancel(){
        return "access?faces-redirect=true";
    }

    public LocalTime getEndTime() {return endTime;}
    public LocalDate getDate() {return date;}
    public LocalTime getStartTime() {return startTime;}
    public void setDate(LocalDate date) {this.date = date;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}
    public CandidateExam getCandidateExam() {return candidateExam;}
    public String getTimeRemaining() {return timeRemaining;}
}
