package com.severentertainment.snippetmanager.controller;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.dto.SnippetResponseDto;
import com.severentertainment.snippetmanager.dto.TagResponseDto;
import com.severentertainment.snippetmanager.service.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    @Autowired
    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    /**
     * Handles HTTP POST requests for creating a new snippet.
     * The snippet data is expected in the request body as JSON.
     *
     * @param snippet The {@link Snippet} object derived from the JSON request body.
     *                The ID, creationDate, and lastModifiedData fields should be null or will be ignored.
     * @return A {@link ResponseEntity} containing the created {@link Snippet} and an HTTP status code of 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Snippet> createSnippet(@RequestBody Snippet snippet) {
        Snippet createdSnippet = snippetService.createSnippet(snippet);
        return new ResponseEntity<>(createdSnippet, HttpStatus.CREATED); // 201 Created
    }

    /**
     * Handles HTTP GET requests for retrieving all snippets.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link Snippet} objects
     * and an HTTP status code of 200 (OK). The list may be empty if no snippets exist.
     */
    @GetMapping
    public ResponseEntity<List<Snippet>> getAllSnippets() {
        List<Snippet> snippets = snippetService.getAllSnippets();
        return new ResponseEntity<>(snippets, HttpStatus.OK); // 200 OK
    }

    /**
     * Handles HTTP GET requests for retrieving a snippet by its ID.
     * The ID of the snippet is extracted from the URL path.
     *
     * @param id The unique ID of the snippet to retrieve.
     * @return A {@link ResponseEntity} containing the {@link Snippet} object if found,
     * and an HTTP status code of 200 (OK) or 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Snippet> getSnippetById(@PathVariable Long id) {
        Optional<Snippet> snippetOptional = snippetService.getSnippetById(id);
        return snippetOptional
                .map(snippet -> new ResponseEntity<>(snippet, HttpStatus.OK)) // 200 OK
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    /**
     * Handles HTTP PUT requests for updating an existing snippet.
     * The ID of the snippet to update is extracted from the URL path.
     * The snippet data is expected in the request body as JSON.
     *
     * @param id The unique ID of the snippet to update.
     * @param snippetDetails A {@link Snippet} object derived from the JSON request body,
     *                       containing the new details for the snippet.
     * @return A {@link ResponseEntity} containing the updated {@link Snippet} object if successful,
     * and an HTTP status code of 200 (OK) or 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Snippet> updateSnippet(@PathVariable Long id, @RequestBody Snippet snippetDetails) {
        Optional<Snippet> updatedSnippetOptional = snippetService.updateSnippet(id, snippetDetails);
        return updatedSnippetOptional
                .map(snippet -> new ResponseEntity<>(snippet, HttpStatus.OK)) // 200 OK
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    /**
     * Handles HTTP DELETE requests for deleting a snippet by its ID.
     * The ID of the snippet is extracted from the URL path.
     *
     * @param id The unique ID of the snippet to delete.
     * @return A {@link ResponseEntity} with an HTTP status code of 204 (No Content) if the snippet
     * was found and successfully deleted, or 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        boolean deleted = snippetService.deleteSnippet(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    /**
     * Handles HTTP GET requests for retrieving all tags associated with a snippet.
     *
     * @param snippetId The ID of the snippet.
     * @return A {@link ResponseEntity} containing the {@link Set} of {@link Tag} objects
     * and an HTTP status code of 200 (OK) if the snippet was found.
     * Otherwise, an HTTP status code of 404 (Not Found) is returned if the snippet does not exist.
     */
    @GetMapping("/{snippetId}/tags")
    public ResponseEntity<Set<TagResponseDto>> getTagsForSnippet(@PathVariable Long snippetId) {
        Optional<Set<TagResponseDto>> tagsOptional = snippetService.getTagsForSnippet(snippetId);
        return tagsOptional
                .map(tags -> new ResponseEntity<>(tags, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Handles HTTP POST requests for associating a tag with a snippet.
     *
     * @param snippetId The ID of the snippet to associate with.
     * @param tagId The ID of the tag to associate.
     * @return A {@link ResponseEntity} with an HTTP status code of 200 (OK) if the association was successful,
     * or an HTTP status code of 404 (Not Found) if the snippet or tag could not be found.
     */
    @PostMapping("/{snippetId}/tags/{tagId}")
    public ResponseEntity<SnippetResponseDto> associateTagWithSnippet(@PathVariable Long snippetId, @PathVariable Long tagId) {
        Optional<SnippetResponseDto> updatedSnippetDtoOptional = snippetService.addTagToSnippet(snippetId, tagId);
        return updatedSnippetDtoOptional
                .map(snippet -> new ResponseEntity<>(snippet, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Handles HTTP DELETE requests for disassociating a tag from a snippet.
     *
     * @param snippetId The ID of the snippet to disassociate from.
     * @param tagId The ID of the tag to disassociate.
     * @return A {@link ResponseEntity} with an HTTP status code of 200 (OK) if the disassociation was successful,
     * or an HTTP status code of 404 (Not Found) if the snippet or tag could not be found
     * or if the tag was not associated with the snippet.
     */
    @DeleteMapping("/{snippetId}/tags/{tagId}")
    public ResponseEntity<SnippetResponseDto> disassociateTagFromSnippet(@PathVariable Long snippetId, @PathVariable Long tagId) {
        Optional<SnippetResponseDto> updatedSnippetDtoOptional = snippetService.removeTagFromSnippet(snippetId, tagId);
        return updatedSnippetDtoOptional
                .map(snippet -> new ResponseEntity<>(snippet, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
