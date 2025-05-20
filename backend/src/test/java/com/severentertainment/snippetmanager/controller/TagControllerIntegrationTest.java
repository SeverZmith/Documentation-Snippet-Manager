package com.severentertainment.snippetmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.dto.TagResponseDto;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createOrGetTag_shouldReturn201AndTagDto_whenNewValidTag() throws Exception {
        // Simulate new tag to create
        Tag newTagRequest = new Tag();
        newTagRequest.setName("  TeSt   TaG  ");

        String expectedNormalizedName = "test tag";

        // 1. Create tag
        mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newTagRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(expectedNormalizedName));
    }

    @Test
    public void createOrGetTag_shouldReturn201AndExistingTagDto_whenTagNameExists() throws Exception {
        // Simulate initial tag
        Tag initialTagRequest = new Tag();
        initialTagRequest.setName("Test Tag");

        MvcResult postResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(initialTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO, id, and name
        String initialTagResponseString = postResult.getResponse().getContentAsString();
        TagResponseDto initialTagDto = objectMapper.readValue(initialTagResponseString, TagResponseDto.class);
        Long existingTagId = initialTagDto.getId();
        String normalizedExistingTagName = initialTagDto.getName();

        // 1. Check that tag exists
        assertNotNull(existingTagId);
        assertEquals("test tag", normalizedExistingTagName);

        // 2. Create tag with same name
        Tag duplicateTagRequest = new Tag();
        duplicateTagRequest.setName("Test Tag");

        // 3. Try to create duplicate tag
        //  - should receive the existing tag
        mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicateTagRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingTagId))
                .andExpect(jsonPath("$.name").value(normalizedExistingTagName));
    }

    @Test
    public void getAllTags_shouldReturn200AndEmptyList_whenNoTagsExist() throws Exception {
        // 1. Get all tags
        mockMvc.perform(get("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllTags_shouldReturn200AndTagDtoList_whenTagsExist() throws Exception {
        // Simulate two created tags
        Tag createTag1Request = new Tag();
        createTag1Request.setName("Test Tag 1");

        Tag createTag2Request = new Tag();
        createTag2Request.setName("Test Tag 2");

        mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTag1Request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTag2Request)))
                .andExpect(status().isCreated());

        // 1. Get all tags
        mockMvc.perform(get("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        "test tag 1",
                        "test tag 2"
                )));
    }

    @Test
    public void getTagById_shouldReturn200AndTagDto_whenTagExists() throws Exception {
        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        String expectedNormalizedName = "test tag";

        MvcResult postResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO and id
        String postResponseString = postResult.getResponse().getContentAsString();
        TagResponseDto createdTagDto = objectMapper.readValue(postResponseString, TagResponseDto.class);
        Long existingTagId = createdTagDto.getId();
        assertNotNull(existingTagId);

        // 1. Get tag by ID
        mockMvc.perform(get("/api/v1/tags/" + existingTagId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingTagId))
                .andExpect(jsonPath("$.name").value(expectedNormalizedName));
    }

    @Test
    public void getTagById_shouldReturn404_whenTagDoesNotExist() throws Exception {
        Long nonExistentId = 1L;

        // 1. Get tag by ID
        mockMvc.perform(get("/api/v1/tags/" + nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTag_shouldReturn200AndUpdatedTagDto_whenTagExists() throws Exception {
        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        MvcResult postResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        TagResponseDto initialTagDto = objectMapper.readValue(
                postResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        // Check that tag exists and has expected name
        Long tagIdToUpdate = initialTagDto.getId();
        assertNotNull(tagIdToUpdate);
        assertEquals("test tag", initialTagDto.getName());

        // 1. Update tag
        Tag updateTagRequest = new Tag();
        updateTagRequest.setName("  UpdATed    TaG  ");

        String expectedNormalizedName = "updated tag";

        mockMvc.perform(put("/api/v1/tags/" + tagIdToUpdate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tagIdToUpdate))
                .andExpect(jsonPath("$.name").value(expectedNormalizedName));
    }

    @Test
    public void updateTag_shouldReturn404_whenTagDoesNotExist() throws Exception {
        Long nonExistentId = 1L;
        Tag updateTagRequest = new Tag();
        updateTagRequest.setName("Updated Tag");

        // 1. Update tag
        mockMvc.perform(put("/api/v1/tags/" + nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTagRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTag_shouldReturn404_whenNameConflictsWithAnotherTag() throws Exception {
        // Simulate two created tags
        Tag createTag1Request = new Tag();
        createTag1Request.setName("Test Tag 1");

        Tag createTag2Request = new Tag();
        createTag2Request.setName("Test Tag 2");

        MvcResult post1Result = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTag1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult post2Result = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTag2Request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTOs, id, and name
        TagResponseDto createdTagDto1 = objectMapper.readValue(
                post1Result.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        TagResponseDto createdTagDto2 = objectMapper.readValue(
                post2Result.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tag1IdToUpdate = createdTagDto1.getId();
        String originalTagName = createdTagDto1.getName();

        // 1. Try to update tag with conflicting name
        Tag updateTagRequest = new Tag();
        updateTagRequest.setName("teST    TaG  2   ");

        mockMvc.perform(put("/api/v1/tags/" + tag1IdToUpdate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTagRequest)))
                .andExpect(status().isNotFound());

        // 2. Verify that tag was not updated
        mockMvc.perform(get("/api/v1/tags/" + tag1IdToUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(originalTagName));
    }

    @Test
    public void updateTag_shouldReturnInternalServerError_whenNameIsEmpty() throws Exception {
        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        MvcResult postResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO and id
        TagResponseDto initialTagDto = objectMapper.readValue(
                postResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tagIdToUpdate = initialTagDto.getId();

        // 1. Try to update tag with empty name
        Tag updateTagRequest = new Tag();
        updateTagRequest.setName("");

        ServletException thrownException = assertThrows(ServletException.class, () -> {
            mockMvc.perform(put("/api/v1/tags/" + tagIdToUpdate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTagRequest)));
        }, "Expected ServletException to be thrown" );

        // 2. Verify IllegalArgumentException was thrown
        Throwable cause = thrownException.getCause();
        assertNotNull(cause, "Expected ServletException to have a cause");
        assertInstanceOf(IllegalArgumentException.class, cause, "Expected ServletException cause to be an IllegalArgumentException");
        assertEquals("Tag name for update cannot be null or empty", cause.getMessage());
    }

    @Test
    public void deleteTag_shouldReturn204AndTagIsDeleted_whenTagExists() throws Exception {
        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        MvcResult postResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO and id
        TagResponseDto createdTagDto = objectMapper.readValue(
                postResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tagIdToDelete = createdTagDto.getId();
        assertNotNull(tagIdToDelete, "Expected tag ID to be non-null");

        // 1. Delete tag
        mockMvc.perform(delete("/api/v1/tags/" + tagIdToDelete))
                .andExpect(status().isNoContent());

        // 2. Verify tag is deleted
        mockMvc.perform(get("/api/v1/tags/" + tagIdToDelete)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteTag_shouldReturn404_whenTagDoesNotExist() throws Exception {
        Long nonExistentId = 1L;

        // 1. Delete tag
        mockMvc.perform(delete("/api/v1/tags/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

}
