package com.example.quickexam.controller;

import com.example.quickexam.entity.Exam;
import com.example.quickexam.entity.Question;
import com.example.quickexam.entity.QuestionOption;
import com.example.quickexam.repository.ExamDAO;
import com.example.quickexam.session.UserSession;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class CreateExamBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ExamDAO examDAO;

    @Inject
    private UserSession userSession;

    private Exam examDetails = new Exam();
    private List<Question> questions = new ArrayList<>();
    private Question currentQuestion = null;
    private String currentOptionText = "";
    private Boolean currentOptionCorrect = false;
    private boolean addingNewQuestion = false;
    private boolean editingQuestion = false;
    private int currentQuestionIndex = -1;

    public void createNewQuestion() {
        currentQuestion = new Question();
        currentQuestion.setOptions(new ArrayList<>());
        addingNewQuestion = true;
        editingQuestion = false;
        currentQuestionIndex = -1;
        currentOptionText = "";
        currentOptionCorrect = false;
    }

    public void editQuestion(Question question) {
        try {
            currentQuestionIndex = questions.indexOf(question);

            currentQuestion = new Question();
            currentQuestion.setQuestionId(question.getQuestionId());
            currentQuestion.setQuestionText(question.getQuestionText());

            List<QuestionOption> optionsCopy = new ArrayList<>();
            if (question.getOptions() != null) {
                for (QuestionOption originalOption : question.getOptions()) {
                    QuestionOption optionCopy = new QuestionOption();
                    optionCopy.setQuestionOptionId(originalOption.getQuestionOptionId());
                    optionCopy.setOptionText(originalOption.getOptionText());
                    optionCopy.setCorrect(originalOption.getCorrect());
                    optionCopy.setQuestion(currentQuestion);
                    optionsCopy.add(optionCopy);
                }
            }
            currentQuestion.setOptions(optionsCopy);

            editingQuestion = true;
            addingNewQuestion = false;
            currentOptionText = "";
            currentOptionCorrect = false;

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to edit question: " + e.getMessage()));
        }
    }

    public void cancelQuestionEdit() {
        currentQuestion = null;
        addingNewQuestion = false;
        editingQuestion = false;
        currentQuestionIndex = -1;
        currentOptionText = "";
        currentOptionCorrect = false;
    }

    public void addOptionToCurrentQuestion() {
        try {
            if (currentQuestion != null) {
                if (currentQuestion.getOptions() == null) {
                    currentQuestion.setOptions(new ArrayList<>());
                }

                if (currentOptionText == null || currentOptionText.trim().isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                    "Option text cannot be empty"));
                    return;
                }

                QuestionOption newOption = new QuestionOption();
                newOption.setOptionText(currentOptionText.trim());
                newOption.setCorrect(currentOptionCorrect != null ? currentOptionCorrect : false);
                newOption.setQuestion(currentQuestion);

                currentQuestion.getOptions().add(newOption);

                currentOptionText = "";
                currentOptionCorrect = false;

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                "Option added successfully"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No question selected to add option to"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to add option: " + e.getMessage()));
        }
    }

    public void removeOptionFromCurrentQuestion(QuestionOption option) {
        try {
            if (currentQuestion != null && currentQuestion.getOptions() != null) {
                boolean removed = currentQuestion.getOptions().remove(option);
                if (removed) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info",
                                    "Option removed"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to remove option: " + e.getMessage()));
        }
    }

    public void saveOrUpdateQuestion() {
        try {
            if (currentQuestion == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No question to save"));
                return;
            }

            if (currentQuestion.getQuestionText() == null ||
                    currentQuestion.getQuestionText().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Question text cannot be empty"));
                return;
            }

            if (currentQuestion.getOptions() == null ||
                    currentQuestion.getOptions().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Question must have at least one option"));
                return;
            }

            boolean hasCorrectOption = false;
            for (QuestionOption option : currentQuestion.getOptions()) {
                if (Boolean.TRUE.equals(option.getCorrect())) {
                    hasCorrectOption = true;
                    break;
                }
            }

            if (!hasCorrectOption) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "At least one option must be marked as correct"));
                return;
            }

            if (editingQuestion && currentQuestionIndex >= 0 &&
                    currentQuestionIndex < questions.size()) {
                questions.set(currentQuestionIndex, currentQuestion);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                "Question updated successfully"));
            } else {
                questions.add(currentQuestion);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                "Question added successfully"));
            }

            cancelQuestionEdit();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to save question: " + e.getMessage()));
        }
    }

    public void removeQuestion(Question question) {
        try {
            boolean removed = questions.remove(question);
            if (removed) {
                if (editingQuestion && currentQuestionIndex >= 0 &&
                        currentQuestionIndex < questions.size() &&
                        questions.get(currentQuestionIndex).equals(question)) {
                    cancelQuestionEdit();
                }
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                "Question removed successfully"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to remove question: " + e.getMessage()));
        }
    }

    public void saveExam() {
        try {
            if (examDetails.getTheme() == null ||
                    examDetails.getTheme().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Exam theme is required"));
                return;
            }

            if (questions.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Exam must have at least one question"));
                return;
            }

            if (examDetails.getNbrQuestion() > questions.size()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning",
                                "Number of questions to display (" + examDetails.getNbrQuestion() +
                                        ") is greater than total questions (" + questions.size() +
                                        "). Setting to " + questions.size()));
                examDetails.setNbrQuestion(questions.size());
            }

            examDetails.setCreatedBy(userSession.getCurrentUser());

            for (Question question : questions) {
                question.setExam(examDetails);
                // Link options to question
                if (question.getOptions() != null) {
                    for (QuestionOption option : question.getOptions()) {
                        option.setQuestion(question);
                    }
                }
            }

            examDetails.setQuestions(questions);

            Exam savedExam = examDAO.saveExam(examDetails);

            if (savedExam != null && savedExam.getExamId() != null) {

                examDetails = new Exam();
                questions = new ArrayList<>();
                cancelQuestionEdit();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                "Exam saved successfully with ID: " + savedExam.getExamId() +
                                        ". Total questions: " + savedExam.getQuestions().size() +
                                        ", Questions to display: " + savedExam.getNbrQuestion()));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Failed to save exam - no ID returned"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error saving exam: " + e.getMessage()));
        }
    }

    public String goToExamList() {
        return "my-exams?faces-redirect=true";
    }


    public Exam getExamDetails() { return examDetails; }
    public void setExamDetails(Exam examDetails) { this.examDetails = examDetails; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public Question getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(Question currentQuestion) { this.currentQuestion = currentQuestion; }

    public String getCurrentOptionText() { return currentOptionText; }
    public void setCurrentOptionText(String currentOptionText) { this.currentOptionText = currentOptionText; }

    public Boolean getCurrentOptionCorrect() { return currentOptionCorrect; }
    public void setCurrentOptionCorrect(Boolean currentOptionCorrect) { this.currentOptionCorrect = currentOptionCorrect; }

    public boolean isAddingNewQuestion() { return addingNewQuestion; }
    public void setAddingNewQuestion(boolean addingNewQuestion) { this.addingNewQuestion = addingNewQuestion; }

    public boolean isEditingQuestion() { return editingQuestion; }
    public void setEditingQuestion(boolean editingQuestion) { this.editingQuestion = editingQuestion; }
}