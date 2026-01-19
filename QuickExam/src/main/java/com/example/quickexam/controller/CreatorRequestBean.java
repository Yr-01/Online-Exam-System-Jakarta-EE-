package com.example.quickexam.controller;

import com.example.quickexam.entity.CreatorRequest;
import com.example.quickexam.entity.RequestStatus;
import com.example.quickexam.entity.User;
import com.example.quickexam.entity.UserRole;
import com.example.quickexam.repository.UserDAO;
import com.example.quickexam.service.MailService;
import com.example.quickexam.session.UserSession;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class CreatorRequestBean implements Serializable {
    @Inject
    private UserSession userSession;
    private CreatorRequest creatorRequest;
    private List<CreatorRequest> myRequests;
    private List<CreatorRequest> allCreatorRequests;
    private boolean displayFrom = false;
    private boolean displayDetail = false;
    private CreatorRequest userCreatorRequest;
    @Inject
    private UserDAO userDAO;
    private String specialty;
    private String description;
    private String portfolioLink;
    @Inject
    private MailService mailService;
    private String filterStatus = RequestStatus.PENDING.toString();

    private String searchQuery = "";
    private List<CreatorRequest> filteredRequests;

    @PostConstruct
    private void init() {
        allCreatorRequests = userDAO.getAllCreatorRequest();
        applyFilters();
    }
    public void applyFilters() {
        this.filteredRequests = allCreatorRequests.stream()
                .filter(req -> (filterStatus == null || req.getStatus().toString().equals(filterStatus)))
                .filter(req -> (searchQuery == null || searchQuery.isEmpty() ||
                        req.getUser().getNom().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        req.getUser().getEmail().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        req.getSpecialty().toLowerCase().contains(searchQuery.toLowerCase())))
                .collect(Collectors.toList());
    }
    public void setStatusFilter(String status) {
        if(status == null) {
            this.filteredRequests = allCreatorRequests;
        }else {
            this.filterStatus = status;
            applyFilters();
        }
    }

    public List<CreatorRequest> getMyRequests() {
        myRequests = userDAO.getMyCreatorRequest(userSession.getCurrentUser().getUserId());
        return myRequests;
    }

    public void addCreatorRequest() {
        displayFrom = true;
    }
    public String cancelCreatorRequest() {
        portfolioLink = null;
        specialty = null;
        description = null;
        creatorRequest = null;
        displayFrom = false;
        return null;
    }
    public String saveCreatorRequest() {
        saveRequest();
        portfolioLink = null;
        specialty = null;
        description = null;
        creatorRequest = null;
        displayFrom = false;
        return null;
    }
    private void saveRequest(){
        creatorRequest = new CreatorRequest();
        creatorRequest.setDescription(description);
        creatorRequest.setPortfolioLink(portfolioLink);
        creatorRequest.setSpecialty(specialty);
        creatorRequest.setUser(userSession.getCurrentUser());
        userDAO.saveCreatorRequest(creatorRequest);
        mailService.sendPendingApprovalRequest(userSession.getCurrentUser().getEmail(), userSession.getCurrentUser().getPrenom()+' '+userSession.getCurrentUser().getNom());
    }

    public void goToRequestDetail(Long id) {
        userCreatorRequest = userDAO.getCreatorRequest(id);
        displayDetail = true;
    }
    public String cancelRequestDetail() {
        displayDetail = false;
        return null;
    }
    public String saveRequestDetail() {
        saveRequestDetails();
        displayDetail = false;
        init();
        return null;
    }

    private void saveRequestDetails(){
        if(userCreatorRequest.getStatus().equals(RequestStatus.APPROVED)){
            User user = userDAO.getUserById(userCreatorRequest.getUser().getUserId());
            user.setRole(UserRole.EXAM_CREATOR);
            userDAO.updateUser(user);
            mailService.sendApproveCreatorRequest(user.getEmail(), user.getPrenom()+' '+user.getNom());
            userDAO.saveCreatorRequest(userCreatorRequest);
        }else if(userCreatorRequest.getStatus().equals(RequestStatus.REJECTED)){
            mailService.sendRejectCreatorRequest(userCreatorRequest.getUser().getEmail(), userCreatorRequest.getUser().getPrenom()+' '+userCreatorRequest.getUser().getNom());
            userDAO.saveCreatorRequest(userCreatorRequest);
        }else if(userCreatorRequest.getStatus().equals(RequestStatus.PENDING)){
            userDAO.saveCreatorRequest(userCreatorRequest);
        }
    }

    public void clearSearch() {
        this.searchQuery = "";
        applyFilters();
    }

    public void clearFilters() {
        this.searchQuery = "";
        this.filterStatus = null;
        applyFilters();
    }

    public boolean isDisplayFrom() {return displayFrom;}
    public String getSpecialty() {return specialty;}
    public void setSpecialty(String specialty) {this.specialty = specialty;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getPortfolioLink() {return portfolioLink;}
    public void setPortfolioLink(String portfolioLink) {this.portfolioLink = portfolioLink;}
    public List<CreatorRequest> getAllCreatorRequests() {return allCreatorRequests;}
    public boolean isDisplayDetail() {return displayDetail;}
    public CreatorRequest getUserCreatorRequest() {return userCreatorRequest;}
    public void setUserCreatorRequest(CreatorRequest userCreatorRequest) {this.userCreatorRequest = userCreatorRequest;}
    public List<CreatorRequest> getFilteredRequests() {return filteredRequests;}
    public String getSearchQuery() {return searchQuery;}
    public void setSearchQuery(String searchQuery) {this.searchQuery = searchQuery;}
    public String getFilterStatus() {return filterStatus;}
}
