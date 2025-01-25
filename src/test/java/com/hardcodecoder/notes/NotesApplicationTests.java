package com.hardcodecoder.notes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class NotesApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void shouldHavePublicAccessToSignup() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.post("/auth/signup"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().bytes("Success".getBytes()));
    }

    @Test
    void shouldHavePublicAccessForObtainingToken() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/auth/token"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().bytes("Token".getBytes()));
    }

    @Test
    void shouldNotHavePublicAccessToRoot() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/auth"))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}