package com.example.quickexam.controller;

import com.example.quickexam.entity.User;
import com.example.quickexam.entity.ValidateUser;
import com.example.quickexam.repository.UserDAO;
import com.example.quickexam.service.HashService;
import com.example.quickexam.service.MailService;
import com.example.quickexam.session.UserSession;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.html.HtmlInputText;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

@Named
@RequestScoped
public class AuthController implements Serializable {

    @Inject
    private UserDAO userDAO;
    @Inject
    private HashService hashService;
    @Inject
    private MailService mailService;
    @Inject
    private UserSession userSession;
    private ValidateUser validateUser;
    private User currentUser;
    private String email;
    private String password;
    private String confirmPassword;
    private String nom;
    private String prenom;
    private String ecole;
    private String filiere;
    private String gsm;
    private String codeVerifier;
    private String errorMessage;
    private LocalDate currentDate = LocalDate.now();


    public String login() {
        try {
            currentUser = userDAO.loginUser(email);
            if (currentUser != null) {
                try {
                    if(hashService.verifyPassword(password, currentUser.getPassword())){
                        userSession.setCurrentUser(currentUser);
                        if(!currentUser.getValide()){
                            sendVerificationMail();
                            return "verify?faces-redirect=true";
                        }
                        return "/index?faces-redirect=true";
                    }
                }catch (Exception e) {
                    currentUser = null;
                    errorMessage = "Incorrect password";
                }
            }else{
                errorMessage = "Incorrect Email";
            }
        } catch (Exception e) {
            currentUser = null;
            return null;
        }
        currentUser = null;
        return null;
    }
    public String register() {
        if(!this.password.equals(this.confirmPassword)){
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "The passwords do not match", null));
            return null;
        }
        currentUser = new User();
        currentUser.setNom(this.nom);
        currentUser.setPrenom(this.prenom);
        currentUser.setEcole(this.ecole);
        currentUser.setFiliere(this.filiere);
        currentUser.setGsm(this.gsm);
        currentUser.setEmail(this.email);
        currentUser.setPassword(hashService.hashPassword(this.password));
        currentUser = userDAO.registerUser(currentUser);
        userSession.setCurrentUser(currentUser);
        sendVerificationMail();
        return "verify?faces-redirect=true";
    }
    public String verify() {
        if(!codeVerifier.isEmpty()){
            currentUser = userSession.getCurrentUser();
            validateUser = userDAO.getVerificationCode(currentUser) ;
            if(validateUser != null){
                if(codeVerifier.equals(validateUser.getCode())){
                    currentUser.setValide(true);
                    userDAO.updateUser(currentUser);
                    validateUser.setUsed(true);
                    userDAO.updateVerificationCode(validateUser);
                    return "/index?faces-redirect=true";
                }else {
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "The code is invalid", null));
                }
            }
        }else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Code Required", null));
        }
        return null;
    }
    public void sendVerificationMail(){
        currentUser = userSession.getCurrentUser();
        mailService.sendVerificationMail(currentUser);
        return;
    }

    public String goToValidAccount(){
        sendVerificationMail();
        return "/views/auth/verify?faces-redirect=true";
    }

    public void validateEmail(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        if(userDAO.loginUser((String) value) != null) {
            HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
            FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel()+"This Email already exists.");
            throw new ValidatorException(facesMessage);
        }
    }
    public String updateProfile(){
        if(!this.confirmPassword.isEmpty()&&!this.password.isEmpty()){
            if(this.password.equals(this.confirmPassword)){
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "The passwords do not match", null));
                return null;
            }else {
                userSession.getCurrentUser().setPassword(hashService.hashPassword(this.password));
                userSession.setCurrentUser(userDAO.registerUser(userSession.getCurrentUser()));
                return "profile?faces-redirect=true";
            }
        }else {
            userSession.setCurrentUser(userDAO.registerUser(userSession.getCurrentUser()));
            return "profile?faces-redirect=true";
        }
    }

    public String logout(){
        userSession.logout();
        return "/index?faces-redirect=true" ;
    }
    public boolean isLoggedIn() {return currentUser != null;}
    public void setCurrentUser(User user) {this.currentUser = user;}
    public User getCurrentUser() { return currentUser; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getErrorMessage() { return errorMessage; }
    public String getNom() {return nom;}
    public void setNom(String nom) {this.nom = nom;}
    public String getPrenom() {return prenom;}
    public void setPrenom(String prenom) {this.prenom = prenom;}
    public String getEcole() {return ecole;}public void setEcole(String ecole) {this.ecole = ecole;}
    public String getFiliere() {return filiere;}
    public void setFiliere(String filiere) {this.filiere = filiere;}
    public String getGsm() {return gsm;}
    public void setGsm(String gsm) {this.gsm = gsm;}
    public void setErrorMessage(String errorMessage) {this.errorMessage = errorMessage;}
    public String getConfirmPassword() {return confirmPassword;}
    public void setConfirmPassword(String confirmPassword) {this.confirmPassword = confirmPassword;}
    public String getCodeVerifier() {return codeVerifier;}
    public void setCodeVerifier(String codeVerifier) {this.codeVerifier = codeVerifier;}

    public LocalDate getCurrentDate() {
        return currentDate;
    }
}