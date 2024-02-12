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
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {

        @MockBean
        RecommendationRequestRepository recommendationRequestRepository;

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
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                                .requesterEmail("requester1@ucsb.edu")
                                .professorEmail("professor1@ucsb.edu")
                                .explanation("PHD")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt1)
                                .done(true)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                                .requesterEmail("requester2@ucsb.edu")
                                .professorEmail("professor2@ucsb.edu")
                                .explanation("Masters")
                                .dateRequested(ldt2)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                ArrayList<RecommendationRequest> expectedRecommendationRequests = new ArrayList<>();
                expectedRecommendationRequests.addAll(Arrays.asList(recommendationRequest1, recommendationRequest2));

                when(recommendationRequestRepository.findAll()).thenReturn(expectedRecommendationRequests);

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(recommendationRequestRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRecommendationRequests);
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
        public void an_admin_user_can_post_a_new_recommendation_request() throws Exception {
                // arrange

                LocalDateTime dateRequested = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime dateNeeded = LocalDateTime.parse("2022-01-03T00:00:00");

                RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                                .requesterEmail("requester1@ucsb.edu")
                                .professorEmail("professor1@ucsb.edu")
                                .explanation("Masters")
                                .dateRequested(dateRequested)
                                .dateNeeded(dateNeeded)
                                .done(true)
                                .build();

                when(recommendationRequestRepository.save(eq(recommendationRequest1)))
                                .thenReturn(recommendationRequest1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/recommendationrequests/post?requesterEmail=requester1@ucsb.edu&professorEmail=professor1@ucsb.edu&explanation=Masters&dateRequested=2022-01-03T00:00:00&dateNeeded=2022-01-03T00:00:00&done=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).save(recommendationRequest1);
                String expectedJson = mapper.writeValueAsString(recommendationRequest1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for GET /api/recommendationrequests?id=...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime dateRequested = LocalDateTime.parse("2022-01-03T00:00:00");
                LocalDateTime dateNeeded = LocalDateTime.parse("2022-01-03T00:00:00");

                RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                                .requesterEmail("requester1@ucsb.edu")
                                .professorEmail("professor1@ucsb.edu")
                                .explanation("Masters")
                                .dateRequested(dateRequested)
                                .dateNeeded(dateNeeded)
                                .done(true)
                                .build();

                when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest));

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(recommendationRequestRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(recommendationRequest);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(recommendationRequestRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
        }

}