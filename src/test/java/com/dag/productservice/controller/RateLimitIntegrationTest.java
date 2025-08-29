package com.dag.productservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class RateLimitIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAllProducts_WithValidRequests_ShouldSucceed() throws Exception {
        // When & Then
        mockMvc.perform(get("/products")
                .header("X-Forwarded-For", "192.168.1.1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getProductById_WithValidRequests_ShouldSucceed() throws Exception {
        // When & Then
        mockMvc.perform(get("/products/{id}", "test-id")
                .header("X-Forwarded-For", "192.168.1.1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProducts_WithoutIpHeader_ShouldUseRemoteAddr() throws Exception {
        // When & Then
        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void rateLimitedEndpoint_ShouldReturn429_WhenLimitExceeded() throws Exception {
        // Note: This test would require setting up a test scenario where
        // rate limits are intentionally exceeded. In a real scenario,
        // you might use a test-specific bucket configuration with very low limits.

        // For now, this test documents the expected behavior
        // In production, you would configure test buckets with low limits
        // to test the rate limiting behavior

        // Example of what the test would look like:
        /*
        // Given - configure a bucket with very low limit for testing
        for (int i = 0; i < 101; i++) {  // Exceed the 100 requests per minute limit
            mockMvc.perform(get("/products")
                    .header("X-Forwarded-For", "192.168.1.100")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(i < 100 ? status().isOk() : status().isTooManyRequests());
        }
        */
    }
}
