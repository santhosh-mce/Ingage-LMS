package com.lms.Backend.course.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    private CourseLesson lesson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", nullable = false)
    @OrderColumn(name = "option_order")
    private List<String> options = new ArrayList<>();

    @Column(nullable = false)
    private Integer correctOptionIndex;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    public QuizQuestion() {}

    public QuizQuestion(CourseLesson lesson, String questionText, List<String> options, int correctOptionIndex, String explanation, int displayOrder) {
        this.lesson = lesson;
        this.questionText = questionText;
        this.options = options != null ? options : new ArrayList<>();
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseLesson getLesson() {
        return lesson;
    }

    public void setLesson(CourseLesson lesson) {
        this.lesson = lesson;
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

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
