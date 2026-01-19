package com.example.quickexam.controller;

import com.example.quickexam.entity.CandidateExam;
import com.example.quickexam.entity.CodeSession;
import com.example.quickexam.entity.Exam;
import com.example.quickexam.entity.User;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.session.UserSession;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.Null;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Named
@RequestScoped
public class ExamController {
    @Inject
    private UserSession userSession;
    @Inject
    ExamDAO examDAO;
    private List<Exam> myExams;
    private List<Object[]> allExams;
    private String code = null;
    private CodeSession codeSession = new CodeSession();
    private List<Object[]> myExamSessions;
    private List<Object[]> myExamSessionsLimit;
    private boolean registrationDate = false;
    private boolean displayTimeRemaing = false;
    private int pageNumber = 1;
    private int pageSize = 10;



    public void init(){
    }

    public String goToMyExams() {return "my-exams?faces-redirect=true";}
    public String goToCreateExam() {return "create-exam?faces-redirect=true";}
    public String viewExamSession(){return "exam-session?faces-redirect=true";}

    public String goToUpdateExam(Long examId) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash()
                .put("examIdToUpdate", examId);
        return "update-exam?faces-redirect=true";
    }
    public String goToCandidate(Long examId, String examName) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash()
                .put("examId", examId);
        FacesContext.getCurrentInstance().getExternalContext().getFlash()
                .put("examName", examName);
        return "candidate-list?faces-redirect=true";
    }

    public List<Exam> getMyExams(){
        try {
            myExams = examDAO.getMyExams(userSession.getCurrentUser());
            return myExams;
        }catch (Exception e){
            return null;
        }
    }
    public List<Object[]> getMyExamSessions(){
        try {
            myExamSessions = examDAO.getExamScores(userSession.getCurrentUser().getUserId());
            return myExamSessions;
        }catch (Exception e){
            return null;
        }
    }
    public List<Object[]> getMyExamSessionsLimit(){
        try {
            myExamSessionsLimit = examDAO.getExamScores(userSession.getCurrentUser().getUserId());
            myExamSessionsLimit = myExamSessionsLimit.stream().limit(4).toList();
            return myExamSessionsLimit;
        }catch (Exception e){
            return null;
        }
    }


    public Long getConfirmedCount(Long examId) {
        return examDAO.countConfirmedUsers(examId);
    }
    public Long getUnConfirmedCount(Long examId) {
        return examDAO.countUnconfirmedUsers(examId);
    }
    public Long getPassCount(Long examId) {
        return examDAO.countUsersWhoPassed(examId);
    }

    public void deleteExam(Long examId) {
        try {
            List<Exam> exams = examDAO.getMyExams(userSession.getCurrentUser());
            Exam examToDelete = null;
            for (Exam exam : exams) {
                if (exam.getExamId().equals(examId)) {
                    examToDelete = exam;
                    break;
                }
            }

            if (examToDelete != null) {
                examDAO.deleteExam(examToDelete);
                FacesContext.getCurrentInstance().addMessage(null,
                        new jakarta.faces.application.FacesMessage(
                                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                                "Success", "Exam deleted successfully"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                            jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                            "Error", "Failed to delete exam: " + e.getMessage()));
        }
    }

    public String validateAndStartExam() {

        registrationDate = false;

        codeSession = examDAO.getCandiateCodeSession(userSession.getCurrentUser().getUserId(), code);
        System.out.println(code);
        System.out.println("codeSession = " + codeSession.getCandidateExam().getHeureDebut());
        if (codeSession == null || Boolean.TRUE.equals(codeSession.getUsed())) {
            FacesContext.getCurrentInstance().addMessage("codeForm:code",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Invalid or already used code."));
            return null;
        }

        CandidateExam candidateExam = examDAO.getCandidateExamWithId(codeSession.getCandidateExam().getCandidateExamId());

        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        LocalDate examDate = candidateExam.getDate();
        LocalTime startTime = candidateExam.getHeureDebut();
        LocalTime endTime = candidateExam.getHeureFin();

        if (today.isAfter(examDate) ||
                (today.isEqual(examDate) && nowTime.isAfter(startTime))) {

            prepareFlash(codeSession);
            return "register-date?faces-redirect=true";
        }

        if (today.isBefore(examDate) ||
                (today.isEqual(examDate) && nowTime.isBefore(startTime))) {

            prepareFlash(codeSession);
            return "time-remaining-in-exam?faces-redirect=true";
        }

        codeSession.setUsed(true);
        examDAO.updateCodeSession(codeSession);

        FacesContext.getCurrentInstance().getExternalContext()
                .getFlash().put("passExamId",
                        codeSession.getExam().getExamId());

        return "take-exam?faces-redirect=true";
    }

    private void prepareFlash(CodeSession session) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash()
                .put("examIdForRegister", session.getExam().getExamId());
        FacesContext.getCurrentInstance().getExternalContext().getFlash()
                .put("codeSession", session.getCodeSessionId());
    }


    public List<Object[]> getAllExams() {
        allExams = examDAO.getAllExams();
        return allExams;
    }

    public void setAllExams(List<Object[]> allExams) {this.allExams = allExams;}

    public String getCode() {return code;}

    public void setCode(String code) {this.code = code;}
    public boolean isRegistrationDate() {return registrationDate;}
    public boolean isDisplayTimeRemaing() {return displayTimeRemaing;}
    public int getPageStart() {return (pageNumber - 1) * pageSize;}
    public int getPageEnd() {return pageNumber * pageSize;}
    public void nextPage() {if (pageNumber < getTotalPages()) pageNumber++;}
    public void previousPage() {if (pageNumber > 1) pageNumber--;}
    public int getTotalPages() {return (int) Math.ceil((double) myExams.size() / pageSize);}
    public int getPageNumber() {return pageNumber;}
    public int getPageSize() {return pageSize;}
    public void setPageSize(int pageSize) {this.pageSize = pageSize;}
    public void setPageNumber(int pageNumber) {this.pageNumber = pageNumber;}

}