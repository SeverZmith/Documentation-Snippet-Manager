package com.severentertainment.snippetmanager.service;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.repository.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SnippetService {

    private final SnippetRepository snippetRepository;

    @Autowired
    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    // Create a new snippet
    public Snippet createSnippet(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    // Get all snippets
    public List<Snippet> getAllSnippets() {
        return snippetRepository.findAll();
    }

    // Get a single snippet by its ID
    public Optional<Snippet> getSnippetById(Long id) {
        return snippetRepository.findById(id);
    }

    // Update an existing snippet
    public Optional<Snippet> updateSnippet(Long id, Snippet snippetDetails) {
        return snippetRepository.findById(id) // Find an existing snippet...
                .map(existingSnippet -> { // If it exists...
                    existingSnippet.setTitle(snippetDetails.getTitle());
                    existingSnippet.setContent(snippetDetails.getContent());
                    return snippetRepository.save(existingSnippet); // Save and return the updated snippet
                });
    }

    // Delete a snippet
    public boolean deleteSnippet(Long id) {
        if (snippetRepository.existsById(id)) {
            snippetRepository.deleteById(id);
            return true; // Deletion successful
        }

        return false; // Snippet not found
    }
}
