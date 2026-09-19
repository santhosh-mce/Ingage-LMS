package com.lms.Backend.explore.controller;

import com.lms.Backend.career.entity.Career;
import com.lms.Backend.career.repository.CareerRepository;
import com.lms.Backend.explore.dto.ExploreResponse;
import com.lms.Backend.project.entity.Project;
import com.lms.Backend.project.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/explore")
public class ExploreController {

    private final CareerRepository careerRepository;
    private final ProjectRepository projectRepository;

    public ExploreController(CareerRepository careerRepository, ProjectRepository projectRepository) {
        this.careerRepository = careerRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public ResponseEntity<ExploreResponse> getExploreData() {
        List<Career> careers = careerRepository.findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscTitleAsc();
        List<Project> projects = projectRepository.findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscTitleAsc();

        List<ExploreResponse.CareerExploreItem> careerItems = careers.stream()
            .map(c -> new ExploreResponse.CareerExploreItem(
                c.getId(),
                c.getTitle(),
                c.getSlug(),
                c.getCategory(),
                c.isPopular() || c.isFeatured(),
                c.getIcon()
            ))
            .collect(Collectors.toList());

        List<ExploreResponse.ProjectExploreItem> projectItems = projects.stream()
            .map(p -> new ExploreResponse.ProjectExploreItem(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getCategory(),
                p.getIndustry()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ExploreResponse(careerItems, projectItems));
    }
}
