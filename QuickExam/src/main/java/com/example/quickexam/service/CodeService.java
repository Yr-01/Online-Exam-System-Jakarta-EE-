package com.example.quickexam.service;

import com.example.quickexam.repository.ExamDAO;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CodeService {

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int CODE_LENGTH_EXAM = 10;
    @Inject
    private ExamDAO examDAO;

    private final SecureRandom random = new SecureRandom();

    public String generateCodeEmail() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CODE_CHARS.length());
            code.append(CODE_CHARS.charAt(index));
        }
        return code.toString();
    }

    public String generateCodeExam() {
        String code;
        int attempts = 0;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH_EXAM);
            for (int i = 0; i < CODE_LENGTH_EXAM; i++) {
                int index = random.nextInt(CODE_CHARS.length());
                sb.append(CODE_CHARS.charAt(index));
            }
            code = sb.toString();
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Failed to generate unique code after 100 attempts");
            }
        } while (codeExistsInDatabase(code));
        return code;
    }
    private boolean codeExistsInDatabase(String code) {
        try {
            Long count = examDAO.checkIfCodeExists(code);
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
