package edu.ucsb.cs156.example.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequests;
import edu.ucsb.cs156.example.repositories.RecommendationRequestsRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;

@WebMvcTest(controllers = RecommendationRequestsController.class)
@Import(TestConfig.class)
public class RecommendationRequestsControllerTests extends ControllerTestCase {

    @MockBean
    RecommendationRequestsRepository recommendationRequestsRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/recommendationrequests/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_recommendationrequests() throws Exception {

        // arrange
        LocalDateTime dateRequested1 = LocalDateTime.parse("2022-01-03T00:00:00");
        LocalDateTime dateNeeded1 = LocalDateTime.parse("2022-01-03T00:00:00");
        Boolean done1 = true;

        RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                .requesterEmail("requester1@ucsb.edu")
                .professorEmail("professor1@ucsb.edu")
                .explanation("Explanation 1")
                .dateRequested(dateRequested1)
                .dateNeeded(dateNeeded1)
                .done(done1)
                .build();

        LocalDateTime dateRequested2 = LocalDateTime.parse("2022-01-03T00:00:00");
        LocalDateTime dateNeeded2 = LocalDateTime.parse("2022-01-03T00:00:00");
        Boolean done2 = true;

        RecommendationRequests recommendationRequests2 = RecommendationRequests.builder()
                .requesterEmail("requester2@ucsb.edu")
                .professorEmail("professor2@ucsb.edu")
                .explanation("Explanation 2")
                .dateRequested(dateRequested2)
                .dateNeeded(dateNeeded2)
                .done(done2)
                .build();

        ArrayList<RecommendationRequests> expectedRecommendations = new ArrayList<>();
        expectedRecommendations.addAll(Arrays.asList(recommendationRequests1, recommendationRequests2));

        when(recommendationRequestsRepository.findAll()).thenReturn(expectedRecommendations);

        // act
        MvcResult response = mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(recommendationRequestsRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedRecommendations);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/recommendationrequests/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequests/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequests/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_recommendationrequest() throws Exception {
        // arrange

        LocalDateTime dateRequested1 = LocalDateTime.parse("2022-01-03T00:00:00");
        LocalDateTime dateNeeded1 = LocalDateTime.parse("2022-01-03T00:00:00");
        Boolean done1 = true;

        RecommendationRequests recommendationRequests1 = RecommendationRequests.builder()
                .requesterEmail("requester1@ucsb.edu")
                .professorEmail("professor1@ucsb.edu")
                .explanation("explanation1")
                .dateRequested(dateRequested1)
                .dateNeeded(dateNeeded1)
                .done(done1)
                .build();

        when(recommendationRequestsRepository.save(eq(recommendationRequests1))).thenReturn(recommendationRequests1);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/recommendationrequests/post?requesterEmail=requester1@ucsb.edu&professorEmail=professor1@ucsb.edu&explanation=explanation1&dateRequested=2022-01-03T00:00:00&dateNeeded=2022-01-03T00:00:00&done=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestsRepository, times(1)).save(recommendationRequests1);
        String expectedJson = mapper.writeValueAsString(recommendationRequests1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

}
