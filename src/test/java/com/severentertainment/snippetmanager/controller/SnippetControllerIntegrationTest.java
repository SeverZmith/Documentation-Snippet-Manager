package com.severentertainment.snippetmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.dto.SnippetResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class SnippetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createSnippet_shouldReturn201AndSnippetData_whenValidSnippet() throws Exception {
        // TODO: Controller accepts a Snippet but should accept a DTO
        // Simulate snippet to create
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        mockMvc.perform(post("/api/v1/snippets") // POST
                    .contentType(MediaType.APPLICATION_JSON) // Content type is JSON
                    .content(objectMapper.writeValueAsString(createSnippetRequest))) // Set request body as JSON string
                .andExpect(status().isCreated()) // Expect HTTP 201 Created status
                .andExpect(jsonPath("$.id").exists()) // Expect ID to exist
                .andExpect(jsonPath("$.title").value("Test Title")) // Expect title to match simulated title
                .andExpect(jsonPath("$.content").value("Test Content")) // Expect content to match simulated title
                .andExpect(jsonPath("$.creationDate").exists()) // Expect creation date to exist
                .andExpect(jsonPath("$.lastModifiedDate").exists()) // Expect last modified date to exist
                .andExpect(jsonPath("$.tags").isArray()) // Expect tags to be an array
                .andExpect(jsonPath("$.tags").isEmpty()); // // Expect tags array to be empty
    }

    @Test
    public void getSnippetById_shouldReturn200AndSnippetDto_whenSnippetExists() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult postResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        String postResponseString = postResult.getResponse().getContentAsString();
        SnippetResponseDto createdSnippetDto = objectMapper.readValue(postResponseString, SnippetResponseDto.class);
        Long existingSnippetId = createdSnippetDto.getId();
        assertNotNull(existingSnippetId);

        // 1. Get snippet by ID
        mockMvc.perform(get("/api/v1/snippets/" + existingSnippetId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingSnippetId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").isEmpty());
    }

    @Test
    public void getSnippetById_shouldReturn404_whenSnippetDoesNotExist() throws Exception {
        Long nonExistentId = 1L;

        mockMvc.perform(get("/api/v1/snippets/" + nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllSnippets_shouldReturn200AndEmptyList_whenNoSnippetsExist() throws Exception {
        mockMvc.perform(get("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void getAllSnippets_shouldReturn200AndSnippetDtoList_whenSnippetsExist() throws Exception {
        // Simulate two created snippets
        Snippet createSnippet1Request = new Snippet();
        createSnippet1Request.setTitle("Test Title 1");
        createSnippet1Request.setContent("Test Content 1");

        Snippet createSnippet2Request = new Snippet();
        createSnippet2Request.setTitle("Test Title 2");
        createSnippet2Request.setContent("Test Content 2");

        mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippet1Request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippet2Request)))
                .andExpect(status().isCreated());

        // 1. Get snippets
        mockMvc.perform(get("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Test Title 1",
                        "Test Title 2"
                )));
    }

    @Test
    public void updateSnippet_shouldReturn200AndUpdatedSnippetDto_whenSnippetExists() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult postResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto initialSnippetDto = objectMapper.readValue(
                postResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetIdToUpdate = initialSnippetDto.getId();
        Instant initialLastModifiedDate = initialSnippetDto.getLastModifiedDate();

        // 1. Update snippet
        Snippet updateSnippetRequest = new Snippet();
        updateSnippetRequest.setTitle("Updated Title");
        updateSnippetRequest.setContent("Updated Content");

        MvcResult putResult = mockMvc.perform(put("/api/v1/snippets/" + snippetIdToUpdate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateSnippetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(snippetIdToUpdate))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.creationDate").exists())
                .andExpect(jsonPath("$.lastModifiedDate").exists())
                .andReturn();

        // 2. Check that lastModifiedDate was updated
        String putResponseString = putResult.getResponse().getContentAsString();
        SnippetResponseDto updatedSnippetDto = objectMapper.readValue(putResponseString, SnippetResponseDto.class);

        assertNotNull(updatedSnippetDto.getLastModifiedDate(), "Last modified date should not be null");
        assertTrue(updatedSnippetDto.getLastModifiedDate().compareTo(initialLastModifiedDate) >= 0,
                "Last modified date should be greater than or equal to the initial last modified date");
    }

    @Test
    public void updateSnippet_shouldReturn404_whenSnippetDoesNotExist() throws Exception {
        Long nonExistentId = 1L;

        // 1. Update snippet
        Snippet updateSnippetRequest = new Snippet();
        updateSnippetRequest.setTitle("Test Title");
        updateSnippetRequest.setContent("Test Content");

        mockMvc.perform(put("/api/v1/snippets/" + nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateSnippetRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteSnippet_shouldReturn204_whenSnippetExists() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult postResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto createdSnippetDto = objectMapper.readValue(
                postResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetIdToDelete = createdSnippetDto.getId();
        assertNotNull(snippetIdToDelete);

        // 1. Delete snippet
        mockMvc.perform(delete("/api/v1/snippets/" + snippetIdToDelete))
                .andExpect(status().isNoContent());

        // 2. Verify that snippet was deleted
        mockMvc.perform(get("/api/v1/snippets/" + snippetIdToDelete)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteSnippet_shouldReturn404_whenSnippetDoesNotExist() throws Exception {
        Long nonExistentId = 1L;

        // 1. Delete snippet
        mockMvc.perform(delete("/api/v1/snippets/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

}
