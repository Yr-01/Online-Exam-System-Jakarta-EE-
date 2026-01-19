package com.example.quickexam.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "validate_users")
public class ValidateUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "validate_user_id")
    private Long validateUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", nullable = false)
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "used")
    private Boolean used = false;


    public Long getValidateUserId() {return validateUserId;}
    public void setValidateUserId(Long validateUserId) {this.validateUserId = validateUserId;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}

    public Date getCreatedAt() {return createdAt;}
    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}

    public Boolean getUsed() {return used;}
    public void setUsed(Boolean used) {this.used = used;}
}