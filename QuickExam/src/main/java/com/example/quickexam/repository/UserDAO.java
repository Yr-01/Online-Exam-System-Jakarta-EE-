package com.example.quickexam.repository;

import com.example.quickexam.entity.CreatorRequest;
import com.example.quickexam.entity.User;
import com.example.quickexam.entity.ValidateUser;
import com.example.quickexam.service.HashService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class UserDAO {
    @PersistenceContext
    private EntityManager em;
    @Inject
    private HashService hashService;
    public User loginUser(String email){
        try {
            return em.createQuery("select u from User u where u.email = :email",User.class)
                    .setParameter("email",email)
                    .getSingleResult();
        }catch (Exception e){
            return  null;
        }
    }
    public void setVerificationCode(String code,User user){
        ValidateUser v = new ValidateUser();
        v.setCode(code);
        v.setUser(user);
        v.setUsed(false);
        em.persist(v);
    }
    public User registerUser(User user){
        if(user.getUserId()==null){
            em.persist(user);
            return loginUser(user.getEmail());
        }else {
            em.merge(user);
            return user;
        }
    }
    public ValidateUser getVerificationCode(User user){
        try {
            return em.createQuery("select vu from ValidateUser vu where vu.user = :user order by vu.validateUserId desc ", ValidateUser.class)
                    .setParameter("user",user)
                    .setMaxResults(1)
                    .getSingleResult();
        }catch (Exception e){
            return  null;
        }
    }
    public void updateVerificationCode(ValidateUser validateUser){
        em.merge(validateUser);
    }
    public void updateUser(User user){
        em.merge(user);
    }
    public User getUserById(Long id){
        return em.find(User.class,id);
    }
    public List<CreatorRequest> getMyCreatorRequest(Long candidateId) {
        try {
            return em.createQuery("select cr from CreatorRequest cr where cr.user.userId = :candidateId",CreatorRequest.class)
                    .setParameter("candidateId",candidateId)
                    .getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public void saveCreatorRequest(CreatorRequest creatorRequest){
        if(creatorRequest.getCreatorRequestId()==null){
            em.persist(creatorRequest);
        }else {
            em.merge(creatorRequest);
        }
    }
    public CreatorRequest getCreatorRequest(Long creatorRequestId){
        return em.createQuery("SELECT cr FROM CreatorRequest cr JOIN FETCH cr.user where cr.creatorRequestId =:creatorRequestId",CreatorRequest.class)
                .setParameter("creatorRequestId",creatorRequestId)
                .getSingleResult();
    }
    public List<CreatorRequest> getAllCreatorRequest(){
        return em.createQuery("SELECT cr FROM CreatorRequest cr JOIN FETCH cr.user", CreatorRequest.class).getResultList();
    }

}
