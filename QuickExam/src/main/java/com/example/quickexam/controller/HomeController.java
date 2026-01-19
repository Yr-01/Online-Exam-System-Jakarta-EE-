package com.example.quickexam.controller;

import com.example.quickexam.session.UserSession;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class HomeController {

    @Inject
    private UserSession userSession;

    public String login() {return "/views/auth/login?faces-redirect=true";}
    public String register() {return "/views/auth/register?faces-redirect=true";}
    public String dashboard() {return redirect();}
    public String exams() {return "/views/exam/exams?faces-redirect=true";}
    public String passExam() {return "/views/exam/access?faces-redirect=true";}
    public String profile(){return "profile?faces-redirect=true";}
    public String promoteToCreator(){return "promote-to-creator?faces-redirect=true";}
    public String logout(){
        userSession.logout();
        return "/index?faces-redirect=true";
    }
    private String redirect(){
        if(userSession.isLoggedIn()&&userSession.getRole().equals("ADMINISTRATOR")){
            return "/views/admin/dashboard?faces-redirect=true";
        }else if(userSession.isLoggedIn()&&userSession.getRole().equals("EXAM_CREATOR")){
            return "/views/creator/dashboard?faces-redirect=true";
        }else if(userSession.isLoggedIn()&&userSession.getRole().equals("CANDIDATE")){
            return "/views/candidate/dashboard?faces-redirect=true";
        }
        else {
            return "/views/auth/login?faces-redirect=true";
        }
    }
    public String goToHome(){return "/index?faces-redirect=true";}




}