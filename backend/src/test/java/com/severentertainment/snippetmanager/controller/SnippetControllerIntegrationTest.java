package com.severentertainment.snippetmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.dto.SnippetResponseDto;
import com.severentertainment.snippetmanager.dto.TagResponseDto;
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
    public void createSnippet_shouldReturn201AndSnippetDto_whenValidSnippet() throws Exception {
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

    @Test
    public void associateTagWithSnippet_shouldReturn200AndUpdatedSnippetDtoWithTag_whenSnippetAndTagExists() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto initialSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetId = initialSnippetDto.getId();
        assertNotNull(snippetId);

        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        MvcResult tagPostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        TagResponseDto initialTagDto = objectMapper.readValue(
                tagPostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tagId = initialTagDto.getId();
        String expectedTagName = initialTagDto.getName();
        assertNotNull(tagId);

        // 1. Associate tag with snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, tagId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(snippetId))
                .andExpect(jsonPath("$.title").value(initialSnippetDto.getTitle()))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags", hasSize(1)))
                .andExpect(jsonPath("$.tags[0].id").value(tagId))
                .andExpect(jsonPath("$.tags[0].name").value(expectedTagName));

        // 2. Check that tag was associated with snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", snippetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(tagId))
                .andExpect(jsonPath("$[0].name").value(expectedTagName));
    }

    @Test
    public void associateTagWithSnippet_shouldReturn404_whenSnippetDoesNotExist() throws Exception {
        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        MvcResult tagPostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        TagResponseDto createdTagDto = objectMapper.readValue(
                tagPostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long validTagId = createdTagDto.getId();
        Long nonExistentSnippetId = 1L;

        // 1. Associate tag with non-existent snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", nonExistentSnippetId, validTagId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void associateTagWithSnippet_shouldReturn404_whenTagDoesNotExist() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto initialSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long validSnippetId = initialSnippetDto.getId();
        Long nonExistentTagId = 1L;

        // 1. Associate a non-existent tag with snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", validSnippetId, nonExistentTagId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTagsForSnippet_shouldReturn200AndTagDtoList_whenSnippetExistsAndHasTag() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto initialSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetId = initialSnippetDto.getId();

        // Simulate created tags
        Tag createTag1Request = new Tag();
        createTag1Request.setName("Test Tag 1");

        Tag createTag2Request = new Tag();
        createTag2Request.setName("Test Tag 2");

        MvcResult tag1PostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTag1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult tag2PostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTag2Request)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTOs and ids
        TagResponseDto initialTag1Dto = objectMapper.readValue(
                tag1PostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        TagResponseDto initialTag2Dto = objectMapper.readValue(
                tag2PostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tag1Id = initialTag1Dto.getId();
        Long tag2Id = initialTag2Dto.getId();

        // 1. Associate 1st tag with snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, tag1Id))
                .andExpect(status().isOk());

        // 2. Associate 2nd tag with snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, tag2Id))
                .andExpect(status().isOk());

        // 3. Get tags for snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", snippetId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        "test tag 1",
                        "test tag 2"
                )))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                        tag1Id.intValue(),
                        tag2Id.intValue()
                )));
    }

    @Test
    public void getTagsForSnippet_shouldReturn200AndEmptyList_whenSnippetExistsAndHasNoTags() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto createdSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetId = createdSnippetDto.getId();

        // 1. Get tags for snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", snippetId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getTagsForSnippet_shouldReturn404_whenSnippetDoesNotExist() throws Exception {
        Long nonExistentId = 1L;

        // 1. Get tags for non-existent snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void disassociateTagFromSnippet_shouldReturn200AndTagIsRemoved_whenAssociated() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto createdSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetId = createdSnippetDto.getId();

        // Simulate created tag
        Tag createTagRequest = new Tag();
        createTagRequest.setName("Test Tag");

        MvcResult tagPostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        TagResponseDto createdTagDto = objectMapper.readValue(
                tagPostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tagIdToRemove = createdTagDto.getId();

        // 1. Associate tag with snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, tagIdToRemove))
                .andExpect(status().isOk());

        // 2. Check that tag was associated with snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", snippetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(tagIdToRemove));

        // 3. Disassociate tag from snippet
        mockMvc.perform(delete("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, tagIdToRemove))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(snippetId))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags", hasSize(0)));

        // 4. Check that tag was disassociated from snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", snippetId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void disassociateTagFromSnippet_shouldReturn200AndSnippetUnchanged_whenNotAssociated() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto createdSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long snippetId = createdSnippetDto.getId();

        // Simulate associated tag
        Tag createAssociatedTagRequest = new Tag();
        createAssociatedTagRequest.setName("Test Tag 1");

        MvcResult associatedTagPostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createAssociatedTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        TagResponseDto createdAssociatedTagDto = objectMapper.readValue(
                associatedTagPostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long tagIdToRemove = createdAssociatedTagDto.getId();

        // Simulate non-associated tag
        Tag createNonAssociatedTagRequest = new Tag();
        createNonAssociatedTagRequest.setName("Test Tag 2");

        MvcResult nonAssociatedTagPostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createNonAssociatedTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        TagResponseDto createdNonAssociatedTagDto = objectMapper.readValue(
                nonAssociatedTagPostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long nonAssociatedTagId = createdNonAssociatedTagDto.getId();

        // 1. Associate tag with snippet
        mockMvc.perform(post("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, tagIdToRemove))
                .andExpect(status().isOk());

        // 2. Delete non-associated tag from snippet
        MvcResult deleteResult = mockMvc.perform(delete("/api/v1/snippets/{snippetId}/tags/{tagId}", snippetId, nonAssociatedTagId))
                .andExpect(status().isOk())
                .andReturn();

        String deleteResponseString = deleteResult.getResponse().getContentAsString();

        SnippetResponseDto updatedSnippetDto = objectMapper.readValue(deleteResponseString, SnippetResponseDto.class);

        // 3. Check that tag association has not changed
        assertEquals(snippetId, updatedSnippetDto.getId());
        assertEquals(1, updatedSnippetDto.getTags().size(), "Snippet should still have 1 tag");
        assertTrue(updatedSnippetDto.getTags().stream().anyMatch(tagDto -> tagDto.getId().equals(tagIdToRemove)),
                "Tag should still be associated with snippet");
        assertFalse(updatedSnippetDto.getTags().stream().anyMatch(tagDto -> tagDto.getId().equals(nonAssociatedTagId)),
                "Tag should not be associated with snippet");

        // 4. Check that tag was NOT associated with snippet
        mockMvc.perform(get("/api/v1/snippets/{snippetId}/tags", snippetId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(tagIdToRemove));
    }

    @Test
    public void disassociateTagFromSnippet_shouldReturn404_whenSnippetDoesNotExist() throws Exception {
        // Simulate created tag
        Tag createdTagRequest = new Tag();
        createdTagRequest.setName("Test Tag");

        MvcResult tagPostResult = mockMvc.perform(post("/api/v1/tags")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createdTagRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        TagResponseDto createdTagDto = objectMapper.readValue(
                tagPostResult.getResponse().getContentAsString(),
                TagResponseDto.class
        );

        Long validTagId = createdTagDto.getId();
        Long nonExistentSnippetId = 1L;

        // 1. Disassociate tag from non-existent snippet
        mockMvc.perform(delete("/api/v1/snippets/{snippetId}/tags/{tagId}", nonExistentSnippetId, validTagId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void disassociateTagFromSnippet_shouldReturn404_whenTagDoesNotExist() throws Exception {
        // Simulate created snippet
        Snippet createSnippetRequest = new Snippet();
        createSnippetRequest.setTitle("Test Title");
        createSnippetRequest.setContent("Test Content");

        MvcResult snippetPostResult = mockMvc.perform(post("/api/v1/snippets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createSnippetRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract DTO
        SnippetResponseDto createdSnippetDto = objectMapper.readValue(
                snippetPostResult.getResponse().getContentAsString(),
                SnippetResponseDto.class
        );

        Long validSnippetId = createdSnippetDto.getId();

        Long nonExistentTagId = 1L;

        // 1. Disassociate a non-existent tag from snippet
        mockMvc.perform(delete("/api/v1/snippets/{snippetId}/tags/{tagId}", validSnippetId, nonExistentTagId))
                .andExpect(status().isNotFound());
    }

}
