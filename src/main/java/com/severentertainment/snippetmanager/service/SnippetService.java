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
    public Snippet updateSnippet(Long id, Snippet snippetDetails) {
        Optional<Snippet> optionalSnippet = snippetRepository.findById(id);
        if (optionalSnippet.isPresent()) {
            Snippet existingSnippet = optionalSnippet.get();
            existingSnippet.setTitle(snippetDetails.getTitle());
            existingSnippet.setContent(snippetDetails.getContent());
            return snippetRepository.save(existingSnippet);
        } else {
            return null;
        }
    }

    // Delete a snippet
    public void deleteSnippet(Long id) {
        snippetRepository.deleteById(id);
    }
}
