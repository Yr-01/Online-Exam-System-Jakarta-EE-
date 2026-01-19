package com.example.quickexam.service;

import jakarta.ejb.Stateless;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Stateless
public class HashService {


    public String hashPassword(String password) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            hashedPassword = Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashedPassword;
    }

    public boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException {

        if (storedHash.equals(hashPassword(password))) {
            return true;
        }
        return false;
    }
}
