package com.severentertainment.snippetmanager.service;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.dto.EntityToDtoMapper;
import com.severentertainment.snippetmanager.dto.TagResponseDto;
import com.severentertainment.snippetmanager.repository.SnippetRepository;
import com.severentertainment.snippetmanager.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Creates a new tag or returns an existing one with the same name (case-insensitive).
     * Tag names are stored in lowercase.
     *
     * @param tag The tag object containing the name for the Tag to be created.
     * @return The created or existing Tag.
     */
    @Transactional
    public TagResponseDto createOrGetTag(Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name for create cannot be null or empty");
        }

        // Normalize the tag name: trim whitespace and convert to lowercase
        String normalizedTagName = tag.getName().trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");

        // Check if a tag with this name already exists
        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(normalizedTagName);

        if (existingTag.isPresent()) {
            return EntityToDtoMapper.tagToTagResponseDto(existingTag.get());
        } else { // If a tag doesn't exist, create a new one
            Tag newTag = new Tag();
            newTag.setName(normalizedTagName);

            Tag savedTag = tagRepository.save(newTag);
            return EntityToDtoMapper.tagToTagResponseDto(savedTag);
        }
    }

    /**
     * Retrieves all tags.
     *
     * @return A list of all tags.
     */
    @Transactional(readOnly = true)
    public List<TagResponseDto> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(EntityToDtoMapper::tagToTagResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a tag by its ID.
     *
     * @param id The ID of the tag to retrieve.
     * @return An Optional containing the Tag if found, or an empty Optional if not.
     */
    @Transactional(readOnly = true)
    public Optional<TagResponseDto> getTagById(Long id) {
        return tagRepository.findById(id)
                .map(EntityToDtoMapper::tagToTagResponseDto);
    }

    /**
     * Updates an existing tag.
     * Tag names are normalized (trimmed, lowercase).
     * Prevents updating to a name that already exists on another tag.
     *
     * @param id The ID of the tag to update.
     * @param tagDetails A Tag object containing the new name for the tag.
     * @return An Optional containing the updated Tag if successful, or an empty Optional if not.
     */
    @Transactional
    public Optional<TagResponseDto> updateTag(Long id, Tag tagDetails) {
        if (tagDetails.getName() == null || tagDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name for update cannot be null or empty");
        }

        Optional<Tag> existingTagOptional = tagRepository.findById(id);
        if (existingTagOptional.isEmpty()) {
            return Optional.empty(); // Tag to update not found
        }

        String newNormalizedName = tagDetails.getName().trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");

        // Check if a tag with this name already exists, excluding the one to update
        Optional<Tag> conflictingTag = tagRepository.findByNameIgnoreCase(newNormalizedName);
        if (conflictingTag.isPresent() && !conflictingTag.get().getId().equals(id)) {
            return Optional.empty(); // Indicate conflict / inability to update
        }

        Tag tagToUpdate = existingTagOptional.get();
        tagToUpdate.setName(newNormalizedName);

        Tag savedTag = tagRepository.save(tagToUpdate);
        return Optional.of(EntityToDtoMapper.tagToTagResponseDto(savedTag));
    }

    /**
     * Deletes a tag by its ID.
     *
     * @param id The ID of the tag to delete.
     * @return True if the tag was deleted, false if not.
     */
    @Transactional
    public boolean deleteTag(Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            return true;
        }

        return false;
    }

}
