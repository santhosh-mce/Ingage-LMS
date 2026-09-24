package com.lms.Backend.course.repository;

import com.lms.Backend.course.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByLessonIdOrderByDisplayOrderAscIdAsc(Long lessonId);
    long countByLessonId(Long lessonId);
}
