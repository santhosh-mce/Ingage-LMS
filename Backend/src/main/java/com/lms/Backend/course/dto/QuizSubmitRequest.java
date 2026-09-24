package com.lms.Backend.course.dto;

public class QuizSubmitRequest {
    private Long questionId;
    private Integer selectedOptionIndex;

    public QuizSubmitRequest() {}

    public QuizSubmitRequest(Long questionId, Integer selectedOptionIndex) {
        this.questionId = questionId;
        this.selectedOptionIndex = selectedOptionIndex;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(Integer selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }
}
