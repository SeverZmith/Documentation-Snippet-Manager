package com.severentertainment.snippetmanager.controller;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.service.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    @Autowired
    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    // Endpoint to create a new snippet
    // HTTP POST to /api/v1/snippets
    @PostMapping
    public ResponseEntity<Snippet> createSnippet(@RequestBody Snippet snippet) {
        Snippet createdSnippet = snippetService.createSnippet(snippet);
        return new ResponseEntity<>(createdSnippet, HttpStatus.CREATED); // 201 Created
    }

    // Endpoint to get all snippets
    // HTTP GET to /api/v1/snippets
    @GetMapping
    public ResponseEntity<List<Snippet>> getAllSnippets() {
        List<Snippet> snippets = snippetService.getAllSnippets();
        return new ResponseEntity<>(snippets, HttpStatus.OK); // 200 OK
    }

    // Endpoint to get a single snippet by its ID
    // HTTP GET to /api/v1/snippets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Snippet> getSnippetById(@PathVariable Long id) {
        Optional<Snippet> snippetOptional = snippetService.getSnippetById(id);
        return snippetOptional
                .map(snippet -> new ResponseEntity<>(snippet, HttpStatus.OK)) // 200 OK
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    // Endpoint to update an existing snippet
    // HTTP PUT to /api/v1/snippets/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Snippet> updateSnippet(@PathVariable Long id, @RequestBody Snippet snippetDetails) {
        Optional<Snippet> updatedSnippetOptional = snippetService.updateSnippet(id, snippetDetails);
        return updatedSnippetOptional
                .map(snippet -> new ResponseEntity<>(snippet, HttpStatus.OK)) // 200 OK
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    // Endpoint to delete a snippet
    // HTTP DELETE to /api/v1/snippets/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        boolean deleted = snippetService.deleteSnippet(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}
