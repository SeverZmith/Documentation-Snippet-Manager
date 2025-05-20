package com.severentertainment.snippetmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetResponseDto {

    private Long id;
    private String title;
    private String content;
    private Instant creationDate;
    private Instant lastModifiedDate;
    private Set<TagResponseDto> tags = new HashSet<>();

}
