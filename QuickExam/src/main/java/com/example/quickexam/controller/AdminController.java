package com.example.quickexam.controller;

import com.example.quickexam.entity.CreatorRequest;
import com.example.quickexam.entity.Exam;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.session.UserSession;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@RequestScoped
public class AdminController implements Serializable {

    @Inject
    private UserSession userSession;

    @Inject
    private ExamDAO examDAO;

    private Long totalUsers;
    private Long totalCandidates;
    private Long totalExamCreators;
    private Long totalExams;
    private Long totalCompletedExams;
    private Long totalScheduledExams;
    private String completionRateFormatted;
    private Long pendingCreatorRequests;
    private Long activeExamsToday;
    private Long usersRegisteredThisMonth;
    private Long examsCreatedThisMonth;
    private Double averageScoreOverall;
    private LocalDate currentDate = LocalDate.now();

    private List<CreatorRequest> pendingCreatorRequestsList = new ArrayList<>();
    private List<Exam> latestExams = new ArrayList<>();


    public Boolean isLoggedIn() {
        return userSession.isLoggedIn() && "ADMINISTRATOR".equals(userSession.getRole());
    }

    public String profile() {
        return "profile?faces-redirect=true";
    }

    public String creators() {
        return "creators?faces-redirect=true";
    }


    public Long getTotalUsers() {
        if (totalUsers == null) {
            totalUsers = examDAO.getTotalCandidate() + examDAO.getTotalCreator() + 1L;
        }
        return totalUsers;
    }

    public Long getTotalCandidates() {
        if (totalCandidates == null) {
            totalCandidates = examDAO.getTotalCandidate();
        }
        return totalCandidates;
    }

    public Long getTotalExamCreators() {
        if (totalExamCreators == null) {
            totalExamCreators = examDAO.getTotalCreator();
        }
        return totalExamCreators;
    }

    public Long getTotalExams() {
        if (totalExams == null) {
            totalExams = examDAO.getTotalExams();
        }
        return totalExams;
    }

    public Long getTotalCompletedExams() {
        if (totalCompletedExams == null) {
            totalCompletedExams = examDAO.getTotalCompletedExams();
        }
        return totalCompletedExams;
    }

    public String getCompletionRateFormatted() {
        if (completionRateFormatted == null) {
            Long scheduled = examDAO.getTotalScheduledExams();
            Long completed = examDAO.getTotalCompletedExams();

            if (scheduled == null || scheduled == 0 || completed == null) {
                completionRateFormatted = "0%";
            } else {
                double rate = (completed.doubleValue() / scheduled.doubleValue()) * 100;
                completionRateFormatted = String.format("%.1f%%", rate);
            }
        }
        return completionRateFormatted;
    }

    public Long getPendingCreatorRequests() {
        if (pendingCreatorRequests == null) {
            pendingCreatorRequests = examDAO.getTotalPendingCreators();
        }
        return pendingCreatorRequests;
    }

    public Long getActiveExamsToday() {
        if (activeExamsToday == null) {
            activeExamsToday = examDAO.getActiveExamsToday();
        }
        return activeExamsToday;
    }

    public Long getUsersRegisteredThisMonth() {
        if (usersRegisteredThisMonth == null) {
            usersRegisteredThisMonth = examDAO.getUsersRegisteredThisMonth();
        }
        return usersRegisteredThisMonth;
    }

    public Long getExamsCreatedThisMonth() {
        if (examsCreatedThisMonth == null) {
            examsCreatedThisMonth = examDAO.getExamsCreatedThisMonth();
        }
        return examsCreatedThisMonth;
    }

    public Double getAverageScoreOverall() {
        if (averageScoreOverall == null) {
            averageScoreOverall = examDAO.getAverageScoreOverall();
            if (averageScoreOverall == null) {
                averageScoreOverall = 0.0;
            }
        }
        return averageScoreOverall;
    }

    public List<CreatorRequest> getPendingCreatorRequestsList() {
        if (pendingCreatorRequestsList.isEmpty()) {
            pendingCreatorRequestsList = examDAO.getPendingCreatorRequestsList(10);
        }
        return pendingCreatorRequestsList;
    }

    public List<Exam> getLatestExams() {
        if (latestExams.isEmpty()) {
            latestExams = examDAO.findAllExamsOrderedByCreatedAtDesc(6);
        }
        return latestExams;
    }
    public LocalDate getCurrentDate() {
        return currentDate;
    }
}