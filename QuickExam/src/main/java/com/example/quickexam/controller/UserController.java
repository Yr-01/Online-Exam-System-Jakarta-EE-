package com.example.quickexam.controller;

import com.example.quickexam.entity.*;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.session.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UserController implements Serializable {

    @Inject
    private UserSession userSession;

    @Inject
    private ExamDAO examDAO;

    private User currentUser;
    private List<Exam> myRecentExams;
    private List<CandidateExam> recentExamSessions;
    private long totalMyExams;
    private long totalRegisteredCandidates;
    private long totalPassedCandidates;
    private long totalCompletedExams;
    private Exam exam;



    @PostConstruct
    public void init() {
        currentUser = userSession.getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals(UserRole.EXAM_CREATOR)) {
            return;
        }

        loadDashboardData();
    }

    private void loadDashboardData() {
        totalMyExams = examDAO.getTotalMyExams(currentUser);

        myRecentExams = examDAO.findMyRecentExams(currentUser, 5);

        recentExamSessions = examDAO.findRecentCandidateExamsByCreator(currentUser, 6);

        totalRegisteredCandidates = examDAO.countTotalCandidatesInMyExams(currentUser);

        totalPassedCandidates = examDAO.countPassedCandidatesInMyExams(currentUser);

        totalCompletedExams = examDAO.countCompletedExamsByCreator(currentUser);
    }


    public Boolean isCreator() {
        return checkCreatorLogin();
    }
    private Boolean checkCreatorLogin(){
        if(userSession.getCurrentUser().getRole().equals(UserRole.EXAM_CREATOR)){
            return true;
        }else{
            return false;
        }
    }

    public Boolean isCandidate() {
        return checkCandidateLogin();
    }
    private Boolean checkCandidateLogin(){
        if(userSession.getCurrentUser().getRole().equals(UserRole.CANDIDATE)){
            return true;
        }else{
            return false;
        }
    }

    public List<Exam> getMyRecentExams() { return myRecentExams; }
    public List<CandidateExam> getRecentExamSessions() { return recentExamSessions; }
    public long getTotalMyExams() { return totalMyExams; }
    public long getTotalRegisteredCandidates() { return totalRegisteredCandidates; }
    public long getTotalPassedCandidates() { return totalPassedCandidates; }
    public long getTotalCompletedExams() { return totalCompletedExams; }
    public User getCurrentUser() { return currentUser; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }


}