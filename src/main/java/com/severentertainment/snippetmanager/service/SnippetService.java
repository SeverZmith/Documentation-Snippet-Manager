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

    /**
     * Creates and saves new snippet.
     *
     * @param snippet The {@link Snippet} object to be created and saved.
     * @return The saved {@link Snippet} object, including its generated ID and timestamps.
     */
    public Snippet createSnippet(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    /**
     * Retrieves all snippets from the database.
     *
     * @return A list of all {@link Snippet} objects.
     */
    public List<Snippet> getAllSnippets() {
        return snippetRepository.findAll();
    }

    /**
     * Retrieves a snippet by its ID.
     *
     * @param id The ID of the snippet to retrieve.
     * @return An {@link Optional} containing the {@link Snippet} if found, or an empty {@link Optional} if not.
     */
    public Optional<Snippet> getSnippetById(Long id) {
        return snippetRepository.findById(id);
    }

    /**
     * Updates an existing snippet.
     * If a snippet with the given ID is found, its title and content are updated.
     * The lastModifiedData timestamp is automatically updated by Hibernate's @UpdateTimestamp.
     *
     * @param id The ID of the snippet to update.
     * @param snippetDetails A {@link Snippet} object containing the new title and content for the snippet.
     * @return An {@link Optional} containing the updated {@link Snippet} if successful, or an empty {@link Optional} if not.
     */
    public Optional<Snippet> updateSnippet(Long id, Snippet snippetDetails) {
        return snippetRepository.findById(id) // Find an existing snippet...
                .map(existingSnippet -> { // If it exists...
                    existingSnippet.setTitle(snippetDetails.getTitle());
                    existingSnippet.setContent(snippetDetails.getContent());
                    return snippetRepository.save(existingSnippet); // Save and return the updated snippet
                });
    }

    /**
     * Deletes a snippet by its ID.
     *
     * @param id The ID of the snippet to delete.
     * @return {@code true} if the deletion was successful, {@code false} if not.
     */
    public boolean deleteSnippet(Long id) {
        if (snippetRepository.existsById(id)) {
            snippetRepository.deleteById(id);
            return true; // Deletion successful
        }

        return false; // Snippet not found
    }

}
