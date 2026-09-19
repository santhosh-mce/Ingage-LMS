package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.Career;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long>, JpaSpecificationExecutor<Career> {

    Optional<Career> findBySlugAndActiveTrue(String slug);

    Optional<Career> findBySlugAndActiveTrueAndPublishedTrue(String slug);

    Optional<Career> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Career> findByActiveTrueOrderByDisplayOrderAscIdAsc();

    List<Career> findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscTitleAsc();

    @Query("SELECT DISTINCT c.category FROM Career c WHERE c.active = true ORDER BY c.category ASC")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT c.level FROM Career c WHERE c.active = true ORDER BY c.level ASC")
    List<String> findDistinctLevels();

    @Query("SELECT c FROM Career c WHERE c.active = true AND (" +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS (SELECT s FROM CareerSkill s WHERE s.career = c AND LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Career> searchCareers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Career c WHERE c.active = true AND " +
           "(:category IS NULL OR :category = '' OR :category = 'All' OR c.category = :category) AND " +
           "(:level IS NULL OR :level = '' OR :level = 'All' OR c.level = :level) AND " +
           "(:featured IS NULL OR c.featured = :featured) AND " +
           "(:popular IS NULL OR c.popular = :popular) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "  LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  EXISTS (SELECT s FROM CareerSkill s WHERE s.career = c AND LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Career> findFilteredCareers(
        @Param("keyword") String keyword,
        @Param("category") String category,
        @Param("level") String level,
        @Param("featured") Boolean featured,
        @Param("popular") Boolean popular,
        Pageable pageable
    );
}
