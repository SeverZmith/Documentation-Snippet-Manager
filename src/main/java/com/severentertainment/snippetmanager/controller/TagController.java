package com.severentertainment.snippetmanager.controller;

import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.dto.TagResponseDto;
import com.severentertainment.snippetmanager.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Handles HTTP POST requests for creating a new tag or retrieving an existing one with the same name.
     * The tag data is expected in the request body as JSON.
     * Tag names are normalized (trimmed, lowercase) by the service.
     *
     * @param tag The {@link Tag} object derived from the JSON request body.
     * @return A {@link ResponseEntity} containing the created or existing {@link Tag}
     * and an HTTP status code of 201 (Created) if a new tag was created
     * or 200 (OK) if an existing tag was found.
     */
    @PostMapping
    public ResponseEntity<TagResponseDto> createOrGetTag(@RequestBody Tag tag) {
        TagResponseDto resultTag = tagService.createOrGetTag(tag);
        return new ResponseEntity<>(resultTag, HttpStatus.CREATED);
    }

    /**
     * Handles HTTP GET requests for retrieving all tags.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link Tag} objects
     * and an HTTP status code of 200 (OK). The list may be empty if no tags exist.
     */
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<TagResponseDto> tags = tagService.getAllTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    /**
     * Handles HTTP GET requests to retrieve a tag by its ID.
     *
     * @param id The unique ID of the tag to retrieve.
     * @return A {@link ResponseEntity} containing the {@link Tag} object if found,
     * and an HTTP status code of 200 (OK) or 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        Optional<TagResponseDto> tagOptional = tagService.getTagById(id);
        return tagOptional
                .map(tag -> new ResponseEntity<>(tag, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Handles HTTP PUT requests for updating an existing tag.
     * The ID of the tag to update is extracted from the URL path.
     *
     * @param id The ID of the tag to update.
     * @param tagDetails A {@link Tag} object derived from the JSON request body.
     * @return A {@link ResponseEntity} containing the updated {@link Tag} object if successful,
     * and an HTTP status code of 200 (OK) or 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable Long id, @RequestBody Tag tagDetails) {
        Optional<TagResponseDto> updatedTagOptional = tagService.updateTag(id, tagDetails);
        return updatedTagOptional
                .map(tag -> new ResponseEntity<>(tag, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Handles HTTP DELETE requests for deleting a tag by its ID.
     *
     * @param id The ID of the tag to delete.
     * @return A {@link ResponseEntity} with an HTTP status code of 204 (No Content) if the tag
     * was found and successfully deleted, or 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        boolean deleted = tagService.deleteTag(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
