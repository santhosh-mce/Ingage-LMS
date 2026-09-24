package com.lms.Backend.course.dto;

public class QuizSubmitResponse {
    private boolean correct;
    private String explanation;
    private Integer correctOptionIndex;
    private boolean quizCompleted;
    private int totalQuestions;
    private int answeredQuestions;
    private String message;

    public QuizSubmitResponse() {}

    public QuizSubmitResponse(boolean correct, String explanation, Integer correctOptionIndex, boolean quizCompleted, int totalQuestions, int answeredQuestions, String message) {
        this.correct = correct;
        this.explanation = explanation;
        this.correctOptionIndex = correctOptionIndex;
        this.quizCompleted = quizCompleted;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.message = message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public boolean isQuizCompleted() {
        return quizCompleted;
    }

    public void setQuizCompleted(boolean quizCompleted) {
        this.quizCompleted = quizCompleted;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(int answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
