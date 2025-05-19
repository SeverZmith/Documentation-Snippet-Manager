package com.severentertainment.snippetmanager.service;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.repository.SnippetRepository;
import com.severentertainment.snippetmanager.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SnippetService {

    private final SnippetRepository snippetRepository;
    private final TagRepository tagRepository;

    @Autowired
    public SnippetService(SnippetRepository snippetRepository, TagRepository tagRepository) {
        this.snippetRepository = snippetRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Creates and saves new snippet.
     *
     * @param snippet The {@link Snippet} object to be created and saved.
     * @return The saved {@link Snippet} object, including its generated ID and timestamps.
     */
    @Transactional
    public Snippet createSnippet(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    /**
     * Retrieves all snippets from the database.
     *
     * @return A list of all {@link Snippet} objects.
     */
    @Transactional(readOnly = true)
    public List<Snippet> getAllSnippets() {
        return snippetRepository.findAll();
    }

    /**
     * Retrieves a snippet by its ID.
     *
     * @param id The ID of the snippet to retrieve.
     * @return An {@link Optional} containing the {@link Snippet} if found, or an empty {@link Optional} if not.
     */
    @Transactional(readOnly = true)
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
    @Transactional
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
    @Transactional
    public boolean deleteSnippet(Long id) {
        if (snippetRepository.existsById(id)) {
            snippetRepository.deleteById(id);
            return true; // Deletion successful
        }

        return false; // Snippet not found
    }

    /**
     * Associates a tag with a snippet.
     *
     * @param snippetId The ID of the snippet.
     * @param tagId The ID of the tag.
     * @return An {@link Optional} containing the updated {@link Snippet} if association was successful,
     * or an empty {@link Optional} if not.
     */
    @Transactional
    public Optional<Snippet> addTagToSnippet(Long snippetId, Long tagId) {
        Optional<Snippet> snippetOptional = snippetRepository.findById(snippetId);
        Optional<Tag> tagOptional = tagRepository.findById(tagId);

        if (snippetOptional.isPresent() && tagOptional.isPresent()) {
            Snippet snippet = snippetOptional.get();
            Tag tag = tagOptional.get();

            snippet.getTags().add(tag); // Add tag to the snippet's set of tags

            return Optional.of(snippetRepository.save(snippet));
        }

        return Optional.empty(); // Snippet or Tag not found
    }

    /**
     * Removes a tag association from a snippet.
     *
     * @param snippetId The ID of the snippet.
     * @param tagId The ID of the tag.
     * @return An {@link Optional} containing the updated {@link Snippet} if disassociation was successful,
     * or an empty {@link Optional} if not.
     */
    @Transactional
    public Optional<Snippet> removeTagFromSnippet(Long snippetId, Long tagId) {
        Optional<Snippet> snippetOptional = snippetRepository.findById(snippetId);
        Optional<Tag> tagOptional = tagRepository.findById(tagId);

        if (snippetOptional.isPresent() && tagOptional.isPresent()) {
            Snippet snippet = snippetOptional.get();
            Tag tag = tagOptional.get();

            boolean removed = snippet.getTags().remove(tag); // Remove tag from the snippet's set of tags
            if (removed) {
                return Optional.of(snippetRepository.save(snippet));
            } else {
                // TODO: Determine appropriate action in this case
                //  - return snippet as is (successful operation)
                //  OR
                //  - return an empty optional (unsuccessful operation)
                return snippetOptional;
            }
        }

        return Optional.empty();
    }

    /**
     * Retrieves all tags associated with a snippet.
     *
     * @param snippetId The ID of the snippet.
     * @return An {@link Optional} containing the {@link Set} of {@link Tag} objects.
     */
    @Transactional(readOnly = true)
    public Optional<Set<Tag>> getTagsForSnippet(Long snippetId) {
        return snippetRepository.findById(snippetId)
                .map(Snippet::getTags);
    }

}
