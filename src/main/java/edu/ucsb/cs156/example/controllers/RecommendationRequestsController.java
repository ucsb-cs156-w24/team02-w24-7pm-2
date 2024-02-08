package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.example.entities.RecommendationRequests;
import edu.ucsb.cs156.example.repositories.RecommendationRequestsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "RecommendationRequests")
@RequestMapping("/api/recommendationrequests")
@RestController
@Slf4j
public class RecommendationRequestsController extends ApiController {

    @Autowired
    RecommendationRequestsRepository recommendationRequestsRepository;

    @Operation(summary = "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequests> allRecommendationRequests() {
        Iterable<RecommendationRequests> reqs = recommendationRequestsRepository.findAll();
        return reqs;
    }

    @Operation(summary = "Create a new request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequests postRecommendationRequests(

            @Parameter(name = "requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name = "professorEmail") @RequestParam String professorEmail,
            @Parameter(name = "dateRequested") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateRequested,
            @Parameter(name = "dateNeeded") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateNeeded,
            @Parameter(name = "done") @RequestParam Boolean done)
            throws JsonProcessingException {

        RecommendationRequests recommendationRequests = new RecommendationRequests();
        recommendationRequests.setRequesterEmail(requesterEmail);
        recommendationRequests.setProfessorEmail(professorEmail);
        recommendationRequests.setExplanation(professorEmail);
        recommendationRequests.setDateRequested(dateRequested);
        recommendationRequests.setDateNeeded(dateNeeded);
        recommendationRequests.setDone(done);

        RecommendationRequests savedRecommendationRequests = recommendationRequestsRepository
                .save(recommendationRequests);

        return savedRecommendationRequests;
    }

}