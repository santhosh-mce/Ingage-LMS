package com.lms.Backend.course.dto;

import java.util.List;

public class QuizQuestionDto {
    private Long id;
    private Long lessonId;
    private String questionText;
    private List<String> options;
    private Integer displayOrder;

    public QuizQuestionDto() {}

    public QuizQuestionDto(Long id, Long lessonId, String questionText, List<String> options, Integer displayOrder) {
        this.id = id;
        this.lessonId = lessonId;
        this.questionText = questionText;
        this.options = options;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
