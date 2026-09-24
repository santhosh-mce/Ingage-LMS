package com.lms.Backend.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to serve the backend information and interactive API documentation dashboard.
 * Maps root URL ("/") and documentation paths ("/docs", "/api-docs") to templates/index.html.
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/docs", "/api-docs"})
    public String home() {
        return "index";
    }
}
