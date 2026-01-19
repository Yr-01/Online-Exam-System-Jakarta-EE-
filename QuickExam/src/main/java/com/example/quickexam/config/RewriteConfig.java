package com.example.quickexam.config;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import jakarta.servlet.ServletContext;

@RewriteConfiguration
public class RewriteConfig extends HttpConfigurationProvider {

    @Override
    public Configuration getConfiguration(ServletContext context) {
        return ConfigurationBuilder.begin()
                // Authentication routes
                .addRule(Join.path("/login").to("/views/auth/login.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/auth/login.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/login"))

                .addRule(Join.path("/register").to("/views/auth/register.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/auth/register.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/register"))

                .addRule(Join.path("/verify").to("/views/auth/verify.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/auth/verify.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/verify"))

                // Error routes
                .addRule(Join.path("/403").to("/views/error/403.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/error/403.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/403"))

                .addRule(Join.path("/404").to("/views/error/404.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/error/404.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/404"))

                .addRule(Join.path("/500").to("/views/error/500.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/error/500.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/500"))

                // Exam routes
                .addRule(Join.path("/exams").to("/views/exam/exams.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/exam/exams.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/exams"))

                .addRule(Join.path("/access-exam").to("/views/exam/access.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/exam/access.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/access-exam"))

                .addRule(Join.path("/access-exam/register-date").to("/views/exam/register-date.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/exam/register-date.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/access-exam/register-date"))

                .addRule(Join.path("/take-exam").to("/views/exam/take-exam.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/exam/take-exam.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/take-exam"))

                .addRule(Join.path("/time-remaining").to("/views/exam/time-remaining-in-exam.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/exam/time-remaining-in-exam.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/time-remaining"))

                // Candidate dashboard routes
                .addRule(Join.path("/dashboard").to("/views/candidate/dashboard.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/candidate/dashboard.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/dashboard"))

                .addRule(Join.path("/dashboard/exam-session").to("/views/candidate/exam-session.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/candidate/exam-session.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/dashboard/exam-session"))

                .addRule(Join.path("/dashboard/promote-to-creator").to("/views/candidate/promote-to-creator.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/candidate/promote-to-creator.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/dashboard/promote-to-creator"))

                .addRule(Join.path("/dashboard/my-profile").to("/views/candidate/profile.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/candidate/profile.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/dashboard/my-profile"))

                // Creator dashboard routes
                .addRule(Join.path("/creator-dashboard").to("/views/creator/dashboard.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/dashboard.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard"))

                .addRule(Join.path("/creator-dashboard/candidate-list").to("/views/creator/candidate-list.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/candidate-list.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard/candidate-list"))

                .addRule(Join.path("/creator-dashboard/create-exam").to("/views/creator/create-exam.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/create-exam.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard/create-exam"))

                .addRule(Join.path("/creator-dashboard/update-exam").to("/views/creator/update-exam.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/update-exam.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard/update-exam"))

                .addRule(Join.path("/creator-dashboard/exam-session").to("/views/creator/exam-session.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/exam-session.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard/exam-session"))

                .addRule(Join.path("/creator-dashboard/my-exams").to("/views/creator/my-exams.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/my-exams.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard/my-exams"))

                .addRule(Join.path("/creator-dashboard/my-profile").to("/views/creator/profile.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/creator/profile.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/creator-dashboard/my-profile"))

                // Admin dashboard routes
                .addRule(Join.path("/admin-dashboard").to("/views/admin/dashboard.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/dashboard.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard"))

                .addRule(Join.path("/admin-dashboard/candidate-list").to("/views/admin/candidate-list.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/candidate-list.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/candidate-list"))

                .addRule(Join.path("/admin-dashboard/create-exam").to("/views/admin/create-exam.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/create-exam.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/create-exam"))

                .addRule(Join.path("/admin-dashboard/update-exam").to("/views/admin/update-exam.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/update-exam.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/update-exam"))

                .addRule(Join.path("/admin-dashboard/creators").to("/views/admin/creators.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/creators.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/creators"))

                .addRule(Join.path("/admin-dashboard/exam-session").to("/views/admin/exam-session.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/exam-session.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/exam-session"))

                .addRule(Join.path("/admin-dashboard/my-exams").to("/views/admin/my-exams.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/my-exams.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/my-exams"))

                .addRule(Join.path("/admin-dashboard/my-profile").to("/views/admin/profile.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/views/admin/profile.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/admin-dashboard/my-profile"))

                // Home route
                .addRule(Join.path("/home").to("/index.xhtml").withInboundCorrection())
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/faces/index.xhtml")))
                .perform(Redirect.permanent(context.getContextPath() + "/home"));
    }

    @Override
    public int priority() {
        return 10;
    }
}