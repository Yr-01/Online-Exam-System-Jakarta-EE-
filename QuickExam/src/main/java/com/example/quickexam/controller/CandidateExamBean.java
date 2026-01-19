package com.example.quickexam.controller;

import com.example.quickexam.entity.CandidateExam;
import com.example.quickexam.entity.CodeSession;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.repository.UserDAO;
import com.example.quickexam.service.CodeService;
import com.example.quickexam.service.MailService;
import com.example.quickexam.session.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class CandidateExamBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Inject
    private ExamDAO examDAO;
    @Inject
    private UserSession userSession;
    private Long examId = null;
    private String examName = null;
    private List<Object[]> unconfirmedUsers = new ArrayList<>();
    private List<Object[]> confirmedUsers = new ArrayList<>();
    private List<Object[]> passedUsers = new ArrayList<>();
    private boolean passed = false;
    private boolean unconfirmed = true;
    private boolean confirmed = false;
    @Inject
    private CodeService codeService;
    @Inject
    private MailService mailService;
    private CodeSession codeSession =  new CodeSession();
    @Inject
    private UserDAO userDAO;
    private LocalDate date = LocalDate.now() ;
    private LocalTime startTime = LocalTime.now() ;
    private LocalTime endTime;
    private Boolean showRegisterForm = false;
    private Long selectedExamId  = null;
    private Integer selectedExamTime  = null;
    private String selectedExamName = null;
    private CandidateExam candidateExam = new CandidateExam();
    private String selectedExamDuration;
    private String selectedExamTheme;





    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();

        Object flashExamId = context.getExternalContext().getFlash().get("examId");
        Object flashExamName = context.getExternalContext().getFlash().get("examName");
        if (flashExamId != null && flashExamName != null) {
            examId = (Long) flashExamId;
            examName = (String) flashExamName;
            loadCandidate(examId);
        } else {
            // check session
            Object sessionExamId = context.getExternalContext().getSessionMap().get("examId");
            Object sessionExamName = context.getExternalContext().getSessionMap().get("examName");
            if (sessionExamId != null  && sessionExamName != null) {
                examId = (Long) sessionExamId;
                examName = (String) sessionExamName;
                loadCandidate(examId);

                context.getExternalContext().getSessionMap().remove("examId");
                context.getExternalContext().getSessionMap().remove("examName");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No exam selected for candidate. Please select an exam from the list."));
            }
        }
    }


    public void loadCandidate(Long examId) {
        unconfirmedUsers = examDAO.getUnconfirmedCandidatesForExam(examId);
        confirmedUsers = examDAO.getConfirmedCandidatesForExam(examId);
        passedUsers = examDAO.getPassedCandidatesForExam(examId);
    }

    public String markUnconfirmed() {
        setUnconfirmed(true);
        setConfirmed(false);
        setPassed(false);
        return null;
    }
    public String markConfirmed() {
        setUnconfirmed(false);
        setConfirmed(true);
        setPassed(false);
        return null;
    }
    public String markPassed() {
        setUnconfirmed(false);
        setConfirmed(false);
        setPassed(true);
        return null;
    }

    public void confirmCandidate(Long candidateId, String candidateName, String candidateEmail,String date, String heureDebut,String heureFin) {
        String code = codeService.generateCodeExam();
        if(code.isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Can't generate code for exam "));
            return;
        }else {
            try{
                mailService.sendCodeExamSession(candidateEmail,candidateName,examName,code,date,heureDebut,heureFin);
                codeSession.setCode(code);
                codeSession.setCandidateExam(examDAO.getCandidateExam(candidateId, examId));
                codeSession.setCandidate(userDAO.getUserById(candidateId));
                codeSession.setExam(examDAO.getExamById(examId));
                examDAO.setCodeSession(codeSession);
                loadCandidate(examId);
            }catch (Exception e){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Error in send Mail"));
                return;
            }
        }
    }

    public void showRegisterDetailForm(Long examId,Integer examTime,String selectedExamName, String selectedExamDuration,String selectedExamTheme) {
        this.selectedExamDuration = selectedExamDuration;
        this.selectedExamTheme = selectedExamTheme;
        this.selectedExamName  = selectedExamName;
        this.selectedExamId = examId;
        this.selectedExamTime = examTime;
        this.showRegisterForm = true;
    }
    public void calculateEndTime() {
        if (startTime != null && selectedExamTime != null) {
            endTime = startTime.plusMinutes(selectedExamTime);
        }
    }
    public String saveRegisterForm(){
        if(showRegisterForm){
            mailService.sendRegisterExam(userSession.getCurrentUser().getEmail(),
                    userSession.getCurrentUser().getNom()+' '+ userSession.getCurrentUser().getPrenom(),
                    selectedExamName, date.toString(), startTime.toString(), endTime.toString());
            candidateExam.setCandidate(userSession.getCurrentUser());
            candidateExam.setExam(examDAO.getExamById(selectedExamId));
            candidateExam.setDate((date));
            candidateExam.setHeureDebut((startTime));
            candidateExam.setHeureFin((endTime));
            examDAO.setCandidateExam(candidateExam);
            this.showRegisterForm = false;
        }
        return null;
    }
    public String cancelRegisterForm(){
        this.showRegisterForm = false;
        return null;
    }

    public String goToExamList() {
        return "my-exams?faces-redirect=true";
    }

    public List<Object[]> getUnconfirmedUsers() {return unconfirmedUsers;}
    public void setUnconfirmedUsers(List<Object[]> unconfirmedUsers) {this.unconfirmedUsers = unconfirmedUsers;}
    public List<Object[]> getConfirmedUsers() {return confirmedUsers;}
    public void setConfirmedUsers(List<Object[]> confirmedUsers) {this.confirmedUsers = confirmedUsers;}
    public List<Object[]> getPassedUsers() {return passedUsers;}
    public void setPassedUsers(List<Object[]> passedUsers) {this.passedUsers = passedUsers;}
    public boolean isPassed() {return passed;}
    public void setPassed(boolean passed) {this.passed = passed;}
    public boolean isUnconfirmed() {return unconfirmed;}
    public void setUnconfirmed(boolean unconfirmed) {this.unconfirmed = unconfirmed;}
    public boolean isConfirmed() {return confirmed;}
    public void setConfirmed(boolean confirmed) {this.confirmed = confirmed;}
    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}
    public LocalTime getStartTime() {return startTime;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}
    public LocalTime getEndTime() {return endTime;}
    public void setEndTime(LocalTime endTime) {this.endTime = endTime;}
    public Boolean getShowRegisterForm() {return showRegisterForm;}
    public void setShowRegisterForm(Boolean showRegisterForm) {this.showRegisterForm = showRegisterForm;}

    public String getSelectedExamDuration() {
        return selectedExamDuration;
    }

    public void setSelectedExamDuration(String selectedExamDuration) {
        this.selectedExamDuration = selectedExamDuration;
    }

    public String getSelectedExamTheme() {
        return selectedExamTheme;
    }

    public void setSelectedExamTheme(String selectedExamTheme) {
        this.selectedExamTheme = selectedExamTheme;
    }
}
