package com.severentertainment.snippetmanager.dto;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.domain.Tag;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityToDtoMapper {

    /**
     * Converts a {@link Tag} entity to a {@link TagResponseDto}.
     *
     * @param tag The Tag entity to convert.
     * @return The corresponding {@link TagResponseDto}, or null if the input tag is null.
     */
    public static TagResponseDto tagToTagResponseDto(Tag tag) {
        if (tag == null) {
            return null;
        }

        return new TagResponseDto(tag.getId(), tag.getName());
    }

    /**
     * Converts a Set of {@link Tag} entities to a Set of {@link TagResponseDto}.
     *
     * @param tags The Set of Tag entities to convert.
     * @return A Set of corresponding {@link TagResponseDto} objects; returns an empty set if the input is null or empty.
     */
    public static Set<TagResponseDto> tagsToTagResponseDtos(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }

        return tags.stream()
                .map(EntityToDtoMapper::tagToTagResponseDto)
                .collect(Collectors.toSet());
    }

    /**
     * Converts a {@link Snippet} entity to a {@link SnippetResponseDto}.
     * This includes converting its associated Set of Tags to a Set of {@link TagResponseDto}.
     *
     * @param snippet The snippet entity to convert.
     * @return The corresponding {@link SnippetResponseDto}, or null if the input snippet is null.
     */
    public static SnippetResponseDto snippetToSnippetResponseDto(Snippet snippet) {
        if (snippet == null) {
            return null;
        }

        return new SnippetResponseDto(
                snippet.getId(),
                snippet.getTitle(),
                snippet.getContent(),
                snippet.getCreationData(),
                snippet.getLastModifiedData(),
                tagsToTagResponseDtos(snippet.getTags())
        );
    }

}
