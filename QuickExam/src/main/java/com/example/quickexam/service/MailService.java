package com.example.quickexam.service;

import com.example.quickexam.entity.User;
import com.example.quickexam.repository.UserDAO;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class MailService {
    private static final Logger LOGGER = Logger.getLogger(MailService.class.getName());

    private static final String LOGO_PATH = "/resources/logo/icon.png";
    private String logoBase64 = "";

    @Inject
    private CodeService codeService;
    private String code;
    @Inject
    private UserDAO userDAO;

    final String username = "yourgmail@gmail.com";
    final String password = "yourappcode";

    private void initLogo() {
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get(LOGO_PATH));
            logoBase64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            LOGGER.warning("Could not load logo: " + e.getMessage());
            logoBase64 = "";
        }
    }

    public void sendVerificationMail(User toUser) {
        code = codeService.generateCodeEmail();
        userDAO.setVerificationCode(code, toUser);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "QuickExam"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toUser.getEmail()));
            message.setSubject("✅ Welcome to QuickExam - Verify Your Account");

            String htmlContent = buildVerificationEmail(toUser.getPrenom()+' '+toUser.getNom(), code);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("Verification email sent successfully to " + toUser.getEmail());

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send verification email", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCodeExamSession(String candidateEmail, String candidateName,
                                    String examName, String examCode,
                                    String date, String heureDebut, String heureFin) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "QuickExam Platform"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(candidateEmail));
            message.setSubject("📝 Exam Access Code: " + examName);

            String htmlContent = buildExamSessionEmail(candidateName, examName, examCode, date, heureDebut, heureFin);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("Exam session email sent successfully to " + candidateEmail);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send exam session email", e);
        }
    }

    public void sendRegisterExam(String candidateEmail, String candidateName,
                                 String examName,
                                 String date, String heureDebut, String heureFin) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "QuickExam Platform"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(candidateEmail));
            message.setSubject("✅ Registration Confirmed: " + examName);

            String htmlContent = buildExamRegisterEmail(candidateName, examName, date, heureDebut, heureFin);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("Exam registration email sent successfully to " + candidateEmail);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send exam registration email", e);
        }
    }

    public void sendRejectCreatorRequest(String candidateEmail, String candidateName) {
        sendCreatorStatusEmail(candidateEmail, candidateName,
                "❌ Creator Request Declined",
                "rejected",
                "does not meet our current requirements.\n" +
                        "For more details about the rejection, please visit our website.\n",
                "You can submit a new request."
        );
    }

    public void sendApproveCreatorRequest(String candidateEmail, String candidateName) {
        sendCreatorStatusEmail(candidateEmail, candidateName,
                "✅ Creator Request Approved!",
                "approved",
                "meets all our requirements",
                "You can now create and manage exams on our platform."
        );
    }

    public void sendPendingApprovalRequest(String candidateEmail, String candidateName) {
        sendCreatorStatusEmail(candidateEmail, candidateName,
                "⏳ Creator Request Under Review",
                "pending review",
                "is currently being reviewed by our team",
                "We'll notify you once a decision is made."
        );
    }

    public void sendFinishedExam(String candidateEmail, String candidateName,
                                 int score, String themeExam, int nbrQuestion) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "QuickExam Platform"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(candidateEmail));
            message.setSubject("📊 Exam Results: " + themeExam);

            String htmlContent = buildFinishedExamEmail(candidateName, score, themeExam, nbrQuestion);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("Exam results email sent successfully to " + candidateEmail);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send exam results email", e);
        }
    }


    private String buildVerificationEmail(String userName, String verificationCode) {
        initLogo();
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Verify Your Account</title>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }" +
                "        .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.1); }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 20px; text-align: center; }" +
                "        .logo { max-width: 120px; margin-bottom: 20px; border-radius: 12px; }" +
                "        .content { padding: 40px; }" +
                "        .code-container { background: #f8f9fa; border-radius: 12px; padding: 30px; margin: 30px 0; text-align: center; border: 2px dashed #dee2e6; }" +
                "        .verification-code { font-size: 42px; font-weight: bold; letter-spacing: 8px; color: #667eea; font-family: 'Courier New', monospace; margin: 20px 0; }" +
                "        .instructions { background: #e8f4fd; border-radius: 12px; padding: 25px; margin: 25px 0; border-left: 5px solid #1a73e8; }" +
                "        .instructions h3 { color: #1a73e8; margin-top: 0; }" +
                "        .footer { background: #f8f9fa; text-align: center; padding: 25px; color: #6c757d; font-size: 14px; border-top: 1px solid #dee2e6; }" +
                "        .highlight { color: #667eea; font-weight: bold; }" +
                "        .step { display: flex; align-items: center; margin: 15px 0; }" +
                "        .step-number { background: #667eea; color: white; width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 15px; }" +
                "        .icon { font-size: 24px; margin-right: 10px; }" +
                "        .expiry { color: #dc3545; font-weight: bold; }" +
                "        @media (max-width: 600px) { .verification-code { font-size: 32px; letter-spacing: 5px; } .content { padding: 20px; } }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                (logoBase64.isEmpty() ?
                        "            <h1 style='margin: 0; font-size: 28px;'>QuickExam</h1>" :
                        "            <img src='data:image/png;base64," + logoBase64 + "' alt='QuickExam Logo' class='logo'/>") +
                "            <h2 style='margin: 10px 0 0 0; font-weight: 300;'>Verify Your Account</h2>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Hello <span class='highlight'>" + userName + "</span>,</p>" +
                "            <p>Welcome to QuickExam! We're excited to have you on board.</p>" +
                "            <p>To complete your registration and activate your account, please use the verification code below:</p>" +
                "            " +
                "            <div class='code-container'>" +
                "                <div style='font-size: 14px; color: #6c757d; margin-bottom: 10px;'>Your verification code:</div>" +
                "                <div class='verification-code'>" + verificationCode + "</div>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>© " + java.time.Year.now() + " QuickExam Platform. All rights reserved.</p>" +
                "            <p>Ensuring secure and reliable exam experiences</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildExamSessionEmail(String candidateName, String examName, String examCode,
                                         String date, String startTime, String endTime) {
        initLogo();
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Exam Session Details</title>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 20px; }" +
                "        .container { max-width: 650px; margin: 0 auto; background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 25px 50px rgba(0,0,0,0.15); }" +
                "        .header { background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%); color: white; padding: 40px 30px; text-align: center; }" +
                "        .logo { max-width: 100px; margin-bottom: 20px; border-radius: 10px; }" +
                "        .content { padding: 40px; }" +
                "        .exam-card { background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 15px; padding: 25px; margin: 25px 0; border-left: 5px solid #4f46e5; }" +
                "        .access-code { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 25px; text-align: center; font-size: 36px; font-weight: bold; letter-spacing: 8px; border-radius: 12px; margin: 30px 0; font-family: 'Courier New', monospace; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }" +
                "        .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 20px; margin: 30px 0; }" +
                "        .info-item { background: white; border-radius: 12px; padding: 20px; text-align: center; box-shadow: 0 5px 15px rgba(0,0,0,0.05); border: 2px solid #e9ecef; transition: transform 0.3s, box-shadow 0.3s; }" +
                "        .info-item:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(0,0,0,0.1); }" +
                "        .info-label { font-size: 13px; color: #6c757d; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 8px; }" +
                "        .info-value { font-size: 20px; font-weight: bold; color: #333; }" +
                "        .instructions { background: #f0f9ff; border-radius: 15px; padding: 25px; margin: 30px 0; border: 2px solid #b3e0ff; }" +
                "        .instructions h3 { color: #1a73e8; margin-top: 0; display: flex; align-items: center; }" +
                "        .requirements { background: #fff3cd; border-radius: 15px; padding: 25px; margin: 30px 0; border: 2px solid #ffd666; }" +
                "        .requirements h3 { color: #e6a700; margin-top: 0; display: flex; align-items: center; }" +
                "        .footer { background: #f8f9fa; text-align: center; padding: 25px; color: #6c757d; font-size: 14px; border-top: 1px solid #dee2e6; }" +
                "        ul { padding-left: 20px; }" +
                "        li { margin-bottom: 12px; padding-left: 10px; }" +
                "        .highlight { color: #4f46e5; font-weight: bold; }" +
                "        .icon { margin-right: 10px; font-size: 20px; }" +
                "        @media (max-width: 600px) { .content { padding: 25px; } .access-code { font-size: 28px; letter-spacing: 5px; } .info-grid { grid-template-columns: 1fr; } }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                (logoBase64.isEmpty() ?
                        "            <h1 style='margin: 0 0 10px 0; font-size: 32px;'>QuickExam</h1>" :
                        "            <img src='data:image/png;base64," + logoBase64 + "' alt='QuickExam Logo' class='logo'/>") +
                "            <h2 style='margin: 0; font-weight: 300; font-size: 24px;'>Exam Session Invitation</h2>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Hello <span class='highlight'>" + candidateName + "</span>,</p>" +
                "            <p>You have been scheduled to take the following exam. Please find your session details below:</p>" +
                "            " +
                "            <div class='exam-card'>" +
                "                <h3 style='margin: 0 0 15px 0; color: #333; font-size: 24px;'>📚 " + examName + "</h3>" +
                "                <p style='margin: 0; color: #6c757d;'>Use the access code below to join your exam session</p>" +
                "            </div>" +
                "            " +
                "            <div class='access-code'>" + examCode + "</div>" +
                "            " +
                "            <h3 style='color: #333; margin-bottom: 20px;'>📅 Exam Schedule</h3>" +
                "            <div class='info-grid'>" +
                "                <div class='info-item'>" +
                "                    <div class='info-label'>📅 Exam Date</div>" +
                "                    <div class='info-value'>" + date + "</div>" +
                "                </div>" +
                "                <div class='info-item'>" +
                "                    <div class='info-label'>⏰ Start Time</div>" +
                "                    <div class='info-value'>" + startTime + "</div>" +
                "                </div>" +
                "                <div class='info-item'>" +
                "                    <div class='info-label'>⏱️ End Time</div>" +
                "                    <div class='info-value'>" + endTime + "</div>" +
                "                </div>" +
                "                <div class='info-item'>" +
                "                    <div class='info-label'>⏳ Duration</div>" +
                "                    <div class='info-value'>" + calculateDuration(startTime, endTime) + "</div>" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div class='instructions'>" +
                "                <h3><span class='icon'>📋</span> Important Instructions</h3>" +
                "                <ul>" +
                "                    <li>Join the exam session <span class='highlight'>15 minutes</span> before the scheduled start time</li>" +
                "                    <li>Ensure you have a <span class='highlight'>stable internet connection</span> throughout the exam</li>" +
                "                    <li>Use a <span class='highlight'>desktop or laptop</span> with updated browser</li>" +
                "                    <li>Close all unnecessary applications and browser tabs</li>" +
                "                </ul>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>© " + java.time.Year.now() + " QuickExam Platform. All exam sessions are monitored and recorded.</p>" +
                "            <p>Confidential & Proprietary Information</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildExamRegisterEmail(String candidateName, String examName,
                                          String date, String startTime, String endTime) {
        initLogo();
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Exam Registration Confirmation</title>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f0f2f5; margin: 0; padding: 20px; }" +
                "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 15px 35px rgba(0,0,0,0.1); }" +
                "        .header { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; padding: 40px 30px; text-align: center; }" +
                "        .logo { max-width: 100px; margin-bottom: 20px; border-radius: 10px; }" +
                "        .success-icon { font-size: 60px; margin-bottom: 20px; }" +
                "        .content { padding: 40px; }" +
                "        .confirmation-card { background: #f0fdf4; border-radius: 12px; padding: 30px; margin: 25px 0; border: 2px solid #86efac; text-align: center; }" +
                "        .details-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin: 30px 0; }" +
                "        .detail-item { background: #f8f9fa; border-radius: 10px; padding: 20px; }" +
                "        .detail-label { font-size: 14px; color: #6c757d; margin-bottom: 5px; }" +
                "        .detail-value { font-size: 18px; font-weight: 600; color: #333; }" +
                "        .next-steps { background: #e0f2fe; border-radius: 12px; padding: 25px; margin: 25px 0; }" +
                "        .footer { background: #f8f9fa; text-align: center; padding: 25px; color: #6c757d; font-size: 14px; border-top: 1px solid #dee2e6; }" +
                "        .button { display: inline-block; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; padding: 14px 35px; text-decoration: none; border-radius: 8px; font-weight: 600; margin: 20px 0; box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3); transition: transform 0.3s, box-shadow 0.3s; }" +
                "        .button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4); }" +
                "        @media (max-width: 600px) { .details-grid { grid-template-columns: 1fr; } .content { padding: 25px; } }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                (logoBase64.isEmpty() ?
                        "            <h1 style='margin: 0 0 10px 0; font-size: 32px;'>QuickExam</h1>" :
                        "            <img src='data:image/png;base64," + logoBase64 + "' alt='QuickExam Logo' class='logo'/>") +
                "            <div class='success-icon'>✅</div>" +
                "            <h2 style='margin: 0; font-weight: 300; font-size: 24px;'>Registration Confirmed!</h2>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Hello <strong>" + candidateName + "</strong>,</p>" +
                "            <p>Your registration for the following exam has been successfully confirmed. We look forward to having you take the exam!</p>" +
                "            " +
                "            <div class='confirmation-card'>" +
                "                <h3 style='margin: 0 0 15px 0; color: #047857; font-size: 22px;'>" + examName + "</h3>" +
                "                <p style='color: #059669; margin: 0;'>🎉 Registration Successful!</p>" +
                "            </div>" +
                "            " +
                "            <h3 style='color: #333;'>📅 Exam Details</h3>" +
                "            <div class='details-grid'>" +
                "                <div class='detail-item'>" +
                "                    <div class='detail-label'>Exam Date</div>" +
                "                    <div class='detail-value'>" + date + "</div>" +
                "                </div>" +
                "                <div class='detail-item'>" +
                "                    <div class='detail-label'>Start Time</div>" +
                "                    <div class='detail-value'>" + startTime + "</div>" +
                "                </div>" +
                "                <div class='detail-item'>" +
                "                    <div class='detail-label'>End Time</div>" +
                "                    <div class='detail-value'>" + endTime + "</div>" +
                "                </div>" +
                "                <div class='detail-item'>" +
                "                    <div class='detail-label'>Duration</div>" +
                "                    <div class='detail-value'>" + calculateDuration(startTime, endTime) + "</div>" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div class='next-steps'>" +
                "                <h3 style='margin-top: 0; color: #0369a1;'>📋 What's Next?</h3>" +
                "                <ol style='padding-left: 20px;'>" +
                "                    <li>Check your email for the exam invitation</li>" +
                "                    <li>Ensure your system meets technical requirements</li>" +
                "                    <li>Join the session 15 minutes before start time</li>" +
                "                </ol>" +
                "            </div>" +
                "            <p style='text-align: center; color: #6c757d; font-size: 12px;'>This confirmation is valid and cannot be transferred to another person.</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>© " + java.time.Year.now() + " QuickExam Platform</p>" +
                "            <p>Thank you for choosing QuickExam for your assessment needs</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildFinishedExamEmail(String candidateName, int score,
                                          String themeExam, int nbrQuestion) {
        initLogo();
        double percentage = (double) score / nbrQuestion * 100;
        String performance = getPerformanceMessage(score, nbrQuestion);

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Exam Results</title>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%); margin: 0; padding: 20px; }" +
                "        .container { max-width: 650px; margin: 0 auto; background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 25px 50px rgba(0,0,0,0.15); }" +
                "        .header { background: linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%); color: white; padding: 40px 30px; text-align: center; }" +
                "        .logo { max-width: 100px; margin-bottom: 20px; border-radius: 10px; }" +
                "        .content { padding: 40px; }" +
                "        .score-card { background: linear-gradient(135deg, " + getScoreColor(percentage) + "); color: white; border-radius: 15px; padding: 40px; text-align: center; margin: 30px 0; box-shadow: 0 15px 30px rgba(0,0,0,0.1); }" +
                "        .score-number { font-size: 72px; font-weight: bold; margin: 10px 0; text-shadow: 0 5px 15px rgba(0,0,0,0.2); }" +
                "        .score-percentage { font-size: 36px; font-weight: 600; margin: 10px 0; }" +
                "        .stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin: 30px 0; }" +
                "        .stat-item { background: #f8f9fa; border-radius: 12px; padding: 25px; text-align: center; border: 2px solid #e9ecef; }" +
                "        .stat-value { font-size: 32px; font-weight: bold; color: #4f46e5; margin: 10px 0; }" +
                "        .stat-label { font-size: 14px; color: #6c757d; text-transform: uppercase; letter-spacing: 1.5px; }" +
                "        .performance { background: #f0f9ff; border-radius: 15px; padding: 30px; margin: 30px 0; border-left: 5px solid #3b82f6; }" +
                "        .feedback { background: #fef3c7; border-radius: 15px; padding: 30px; margin: 30px 0; border-left: 5px solid #f59e0b; }" +
                "        .footer { background: #f8f9fa; text-align: center; padding: 25px; color: #6c757d; font-size: 14px; border-top: 1px solid #dee2e6; }" +
                "        .medal { font-size: 48px; margin: 20px 0; }" +
                "        @media (max-width: 600px) { .stats-grid { grid-template-columns: 1fr; } .score-number { font-size: 56px; } .content { padding: 25px; } }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                (logoBase64.isEmpty() ?
                        "            <h1 style='margin: 0 0 10px 0; font-size: 32px;'>QuickExam</h1>" :
                        "            <img src='data:image/png;base64," + logoBase64 + "' alt='QuickExam Logo' class='logo'/>") +
                "            <h2 style='margin: 0; font-weight: 300; font-size: 24px;'>Exam Results</h2>" +
                "            <p style='margin: 10px 0 0 0; opacity: 0.9;'>" + themeExam + "</p>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Dear <strong>" + candidateName + "</strong>,</p>" +
                "            <p>Congratulations on completing your exam! Your results have been processed and are ready for review.</p>" +
                "            " +
                "            <div class='score-card'>" +
                "                <div class='medal'>" + getMedalEmoji(percentage) + "</div>" +
                "                <div class='score-number'>" + score + "</div>" +
                "                <div class='score-percentage'>" + String.format("%.1f", percentage) + "%</div>" +
                "                <h3 style='margin: 15px 0; font-size: 22px;'>" + performance + "</h3>" +
                "                <p>out of " + nbrQuestion + " questions</p>" +
                "            </div>" +
                "            " +
                "            <h3 style='color: #333;'>📊 Performance Breakdown</h3>" +
                "            <div class='stats-grid'>" +
                "                <div class='stat-item'>" +
                "                    <div class='stat-label'>Correct Answers</div>" +
                "                    <div class='stat-value'>" + score + "</div>" +
                "                </div>" +
                "                <div class='stat-item'>" +
                "                    <div class='stat-label'>Total Questions</div>" +
                "                    <div class='stat-value'>" + nbrQuestion + "</div>" +
                "                </div>" +
                "                <div class='stat-item'>" +
                "                    <div class='stat-label'>Accuracy Rate</div>" +
                "                    <div class='stat-value'>" + String.format("%.1f", percentage) + "%</div>" +
                "                </div>" +
                "                <div class='stat-item'>" +
                "                    <div class='stat-label'>Exam Theme</div>" +
                "                    <div class='stat-value' style='font-size: 20px;'>" + themeExam + "</div>" +
                "                </div>" +
                "            </div>" +
                "            " +
                "            <div class='performance'>" +
                "                <h3 style='margin-top: 0; color: #1e40af;'>📈 Performance Analysis</h3>" +
                "                <p>" + getDetailedFeedback(percentage) + "</p>" +
                "                <p>Your score places you in the <strong>" + getPercentile(percentage) + " percentile</strong></p>" +
                "            </div>" +
                "            " +
                "            <div class='feedback'>" +
                "                <h3 style='margin-top: 0; color: #92400e;'>💡 Recommendations</h3>" +
                "                <p>" + getRecommendations(percentage) + "</p>" +
                "                <p>Consider reviewing topics where you lost points to improve future performance.</p>" +
                "            </div>" +
                "            " +
                "            <div style='text-align: center; margin: 40px 0 20px 0;'>" +
                "                <p>Your detailed score report is available in your QuickExam dashboard.</p>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>© " + java.time.Year.now() + " QuickExam Platform</p>" +
                "            <p>Continuing education and assessment excellence</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }


    private void sendCreatorStatusEmail(String candidateEmail, String candidateName,
                                        String subject, String status,
                                        String reason, String nextSteps) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "QuickExam Platform"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(candidateEmail));
            message.setSubject(subject);

            String htmlContent = buildCreatorStatusEmail(candidateName, status, reason, nextSteps);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("Creator status email sent successfully to " + candidateEmail);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send creator status email", e);
        }
    }

    private String buildCreatorStatusEmail(String candidateName, String status,
                                           String reason, String nextSteps) {
        initLogo();
        String icon = getStatusIcon(status);
        String bgColor = getStatusBackground(status);

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Creator Request Status</title>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f8fafc; margin: 0; padding: 20px; }" +
                "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 15px 35px rgba(0,0,0,0.08); }" +
                "        .header { background: " + bgColor + "; color: white; padding: 40px 30px; text-align: center; }" +
                "        .logo { max-width: 100px; margin-bottom: 20px; border-radius: 10px; }" +
                "        .status-icon { font-size: 60px; margin-bottom: 20px; }" +
                "        .content { padding: 40px; }" +
                "        .status-card { background: " + bgColor.replace(")", ", 0.1)").replace("rgb", "rgba") + "; border-radius: 12px; padding: 30px; margin: 25px 0; border: 2px solid " + bgColor + "; }" +
                "        .info-box { background: #f8f9fa; border-radius: 10px; padding: 25px; margin: 25px 0; }" +
                "        .next-steps { background: #f0f9ff; border-radius: 12px; padding: 25px; margin: 25px 0; border-left: 5px solid #0ea5e9; }" +
                "        .footer { background: #f8f9fa; text-align: center; padding: 25px; color: #6c757d; font-size: 14px; border-top: 1px solid #dee2e6; }" +
                "        @media (max-width: 600px) { .content { padding: 25px; } }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                (logoBase64.isEmpty() ?
                        "            <h1 style='margin: 0 0 10px 0; font-size: 32px;'>QuickExam</h1>" :
                        "            <img src='data:image/png;base64," + logoBase64 + "' alt='QuickExam Logo' class='logo'/>") +
                "            <div class='status-icon'>" + icon + "</div>" +
                "            <h2 style='margin: 0; font-weight: 300; font-size: 24px;'>Creator Request " + status.toUpperCase() + "</h2>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Dear <strong>" + candidateName + "</strong>,</p>" +
                "            <p>Thank you for your interest in becoming a creator on QuickExam. We have reviewed your application.</p>" +
                "            " +
                "            <div class='status-card'>" +
                "                <h3 style='margin: 0 0 15px 0; color: " + bgColor + "; font-size: 22px;'>Status: " + status.toUpperCase() + "</h3>" +
                "                <p style='margin: 0; font-size: 18px;'>Your request has been <strong>" + status + "</strong>.</p>" +
                "            </div>" +
                "            " +
                "            <div class='info-box'>" +
                "                <h4 style='margin-top: 0; color: #333;'>Review Details:</h4>" +
                "                <p>Your application " + reason + ".</p>" +
                "                <p>Our team has carefully evaluated your qualifications and background.</p>" +
                "            </div>" +
                "            " +
                "            <div class='next-steps'>" +
                "                <h4 style='margin-top: 0; color: #0369a1;'>Next Steps:</h4>" +
                "                <p>" + nextSteps + "</p>" +
                "                " + (status.equals("approved") ?
                "                <p>If you are currently logged in to our website, please log out and sign in again to access the creator features.</p>" : "") +
                "            </div>" +
                "            " +
                "            <p style='text-align: center; color: #6c757d; font-size: 12px;'>This is an automated status update. Please do not reply to this email.</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>© " + java.time.Year.now() + " QuickExam Creator Network</p>" +
                "            <p>Empowering educators and content creators worldwide</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String calculateDuration(String startTime, String endTime) {
        try {
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);
            long minutes = java.time.Duration.between(start, end).toMinutes();
            return (minutes / 60) + "h " + (minutes % 60) + "m";
        } catch (Exception e) {
            return "Duration not specified";
        }
    }

    private String getScoreColor(double percentage) {
        if (percentage >= 90) return "#10b981 0%, #059669 100%";
        if (percentage >= 75) return "#3b82f6 0%, #1d4ed8 100%";
        if (percentage >= 60) return "#f59e0b 0%, #d97706 100%";
        return "#ef4444 0%, #dc2626 100%";
    }

    private String getPerformanceMessage(int score, int total) {
        double percentage = (double) score / total * 100;
        if (percentage >= 90) return "Outstanding Performance! 🏆";
        if (percentage >= 75) return "Excellent Work! ⭐";
        if (percentage >= 60) return "Good Job! 👍";
        if (percentage >= 50) return "Satisfactory Performance";
        return "Needs Improvement";
    }

    private String getMedalEmoji(double percentage) {
        if (percentage >= 90) return "🏆";
        if (percentage >= 75) return "🥈";
        if (percentage >= 60) return "🥉";
        return "📝";
    }

    private String getDetailedFeedback(double percentage) {
        if (percentage >= 90) return "Exceptional performance! You have demonstrated mastery of the subject material with outstanding comprehension and application skills.";
        if (percentage >= 75) return "Strong performance! You have a solid understanding of the key concepts with room for refinement in specific areas.";
        if (percentage >= 60) return "Satisfactory performance! You have grasped the fundamental concepts but may benefit from additional review of complex topics.";
        return "Below expectations. Consider reviewing the core concepts and seeking additional study materials to improve your understanding.";
    }

    private String getPercentile(double percentage) {
        if (percentage >= 90) return "90th";
        if (percentage >= 75) return "75th";
        if (percentage >= 60) return "60th";
        if (percentage >= 50) return "40th";
        return "20th";
    }

    private String getRecommendations(double percentage) {
        if (percentage >= 90) return "Maintain your excellent study habits and consider mentoring others or tackling advanced topics.";
        if (percentage >= 75) return "Focus on areas where you lost points to achieve excellence. Review complex topics for deeper understanding.";
        if (percentage >= 60) return "Review incorrect answers thoroughly. Consider additional practice questions on challenging topics.";
        return "We recommend revisiting the foundational concepts. Consider our study guides and practice exams for improvement.";
    }

    private String getStatusIcon(String status) {
        switch (status.toLowerCase()) {
            case "approved": return "✅";
            case "rejected": return "❌";
            case "pending review": return "⏳";
            default: return "📄";
        }
    }

    private String getStatusBackground(String status) {
        switch (status.toLowerCase()) {
            case "approved": return "linear-gradient(135deg, #10b981 0%, #059669 100%)";
            case "rejected": return "linear-gradient(135deg, #ef4444 0%, #dc2626 100%)";
            case "pending review": return "linear-gradient(135deg, #f59e0b 0%, #d97706 100%)";
            default: return "linear-gradient(135deg, #6b7280 0%, #4b5563 100%)";
        }
    }
}