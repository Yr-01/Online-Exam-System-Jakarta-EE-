package com.example.quickexam.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "creator_requests")
public class CreatorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creator_request_id")
    private Long creatorRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "portfolio_link")
    private String portfolioLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "admin_feedback", columnDefinition = "TEXT")
    private String adminFeedback;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getCreatorRequestId() {return creatorRequestId;}
    public void setCreatorRequestId(Long creatorRequestId) {this.creatorRequestId = creatorRequestId;}
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public String getSpecialty() {return specialty;}
    public void setSpecialty(String specialty) {this.specialty = specialty;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getPortfolioLink() {return portfolioLink;}
    public void setPortfolioLink(String portfolioLink) {this.portfolioLink = portfolioLink;}
    public RequestStatus getStatus() {return status;}
    public void setStatus(RequestStatus status) {this.status = status;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public String getAdminFeedback() {return adminFeedback;}
    public void setAdminFeedback(String adminFeedback) {this.adminFeedback = adminFeedback;}
}