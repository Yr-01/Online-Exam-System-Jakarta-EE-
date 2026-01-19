package com.example.quickexam.session;

import com.example.quickexam.entity.User;
import com.example.quickexam.entity.UserRole;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private User currentUser;
    private String roleName;

    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {this.currentUser = currentUser;}
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    public String getRole(){return currentUser.getRole().toString();}
    public void logout() {
        currentUser = null;
    }
    public String getRoleName(){
        if(currentUser.getRole().toString().equals(UserRole.ADMINISTRATOR.toString())){
            return "Admin";
        }else if(currentUser.getRole().toString().equals(UserRole.CANDIDATE.toString())){
            return "Student";
        }else if(currentUser.getRole().toString().equals(UserRole.EXAM_CREATOR.toString())){
            return "Creator";
        }
        return null;
    }
    public Boolean isAdmin(){
        return currentUser.getRole().equals(UserRole.ADMINISTRATOR);
    }
    public Boolean isValidated(){
        return currentUser.getValide();
    }
    public Boolean isCandidate(){
        return currentUser.getRole().equals(UserRole.CANDIDATE);
    }
    public Boolean isCreator(){
        return currentUser.getRole().equals(UserRole.EXAM_CREATOR);
    }

}
