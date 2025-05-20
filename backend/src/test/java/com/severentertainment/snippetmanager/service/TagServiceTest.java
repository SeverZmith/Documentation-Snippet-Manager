package com.severentertainment.snippetmanager.service;

import com.severentertainment.snippetmanager.domain.Tag;
import com.severentertainment.snippetmanager.dto.TagResponseDto;
import com.severentertainment.snippetmanager.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepositoryMock;

    @InjectMocks
    private TagService tagService;

    @Test
    public void createOrGetTag_shouldCreateNewTag_whenNameDoesNotExist() {
        // Input Tag object with a name that requires normalization
        Tag tagInput = new Tag();
        tagInput.setName("  New Tag NaMe  ");

        // The expected normalized name
        String normalizedName = "new tag name";

        // What to expect from the repository's save method
        Tag savedTagFromRepo = new Tag();
        savedTagFromRepo.setId(1L);
        savedTagFromRepo.setName(normalizedName);

        // Simulate expected tag DTO
        TagResponseDto expectedTagDto = new TagResponseDto(
                1L,
                normalizedName
        );

        // Configure the mock repository behavior:
        //  - findByNameIgnoreCase should return an empty optional (tag doesn't exist)
        when(tagRepositoryMock.findByNameIgnoreCase(normalizedName)).thenReturn(Optional.empty());
        //  - save should return the saved tag from the repository when any Tag is passed
        when(tagRepositoryMock.save(any(Tag.class))).thenReturn(savedTagFromRepo);

        // Call the method under test
        TagResponseDto actualTagDto = tagService.createOrGetTag(tagInput);

        // 1. Check the returned tag's properties
        assertNotNull(actualTagDto, "The returned tag should not be null");
        assertEquals(expectedTagDto.getId(), actualTagDto.getId(), "ID of the returned tag should match the ID of the saved tag");
        assertEquals(expectedTagDto.getName(), actualTagDto.getName(), "Name of the returned tag should match the normalized name of the input tag");

        // 2. Verify that findByNameIgnoreCase was called once with the normalized name
        verify(tagRepositoryMock, times(1)).findByNameIgnoreCase(normalizedName);

        // 3. Verify that save was called once with the correct tag
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepositoryMock, times(1)).save(tagArgumentCaptor.capture());

        assertEquals(normalizedName, tagArgumentCaptor.getValue().getName(), "The tag passed to save should have the normalized name");
        assertNull(tagArgumentCaptor.getValue().getId(), "The tag passed to save should not have an ID");
    }

    @Test
    public void createOrGetTag_shouldReturnExistingTag_whenNameExists() {
        // Input Tag object with a name that requires normalization
        Tag tagInput = new Tag();
        tagInput.setName(" EXIstING TaG ");

        // The expected normalized name
        String normalizedName = "existing tag";

        // Simulate an existing tag in the repository
        Tag existingTag = new Tag();
        existingTag.setId(1L);
        existingTag.setName(normalizedName);

        // Simulate expected tag DTO
        TagResponseDto expectedTagDto = new TagResponseDto(
                1L,
                normalizedName
        );

        // Configure the mock repository to return the existing tag
        when(tagRepositoryMock.findByNameIgnoreCase(normalizedName)).thenReturn(Optional.of(existingTag));

        // Call the method under test
        TagResponseDto actualTagDto = tagService.createOrGetTag(tagInput);

        // 1. Check that the returned tag is not null
        assertNotNull(actualTagDto, "The returned tag should not be null");

        // 2. Check that the returned tag matches the existing tag
        assertEquals(expectedTagDto.getId(), actualTagDto.getId(), "ID of the returned tag should match the ID of the existing tag");
        assertEquals(expectedTagDto.getName(), actualTagDto.getName(), "Name of the returned tag should match the name of the existing tag");

        // 3. Verify interactions with the repository:
        //  - findByNameIgnoreCase should be called once with the normalized name
        verify(tagRepositoryMock, times(1)).findByNameIgnoreCase(normalizedName);
        //  - save should NOT be called
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void createOrGetTag_shouldThrowIllegalArgumentException_whenNameIsNull() {
        // Input Tag object with a null name
        Tag tagWithNullName = new Tag();
        tagWithNullName.setName(null);

        // The expected error message
        String expectedErrorMessage = "Tag name for create cannot be null or empty";

        // Call the method under test and check that it throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.createOrGetTag(tagWithNullName);
        }, "Should throw an IllegalArgumentException when the tag name is null");

        // 1. Check that the exception message matches the expected message
        assertEquals(expectedErrorMessage, exception.getMessage(), "The exception message should match the expected message");

        // 2. Verify no repository methods were called
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void createOrGetTag_shouldThrowIllegalArgumentException_whenNameIsEmpty() {
        // Input Tag object with an empty name
        Tag tagWithEmptyName = new Tag();
        tagWithEmptyName.setName("");

        // The expected error message
        String expectedErrorMessage = "Tag name for create cannot be null or empty";

        // Call the method under test and check that it throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.createOrGetTag(tagWithEmptyName);
        }, "Should throw an IllegalArgumentException when the tag name is empty");

        // 1. Check that the exception message matches the expected message
        assertEquals(expectedErrorMessage, exception.getMessage(), "The exception message should match the expected message");

        // 2. Verify no repository methods were called
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void createOrGetTag_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        // Input Tag object with a blank name
        Tag tagWithBlankName = new Tag();
        tagWithBlankName.setName(" ");

        // The expected error message
        String expectedErrorMessage = "Tag name for create cannot be null or empty";

        // Call the method under test and check that it throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.createOrGetTag(tagWithBlankName);
        }, "Should throw an IllegalArgumentException when the tag name is blank");

        // 1. Check that the exception message matches the expected message
        assertEquals(expectedErrorMessage, exception.getMessage(), "The exception message should match the expected message");

        // 2. Verify no repository methods were called
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void getAllTags_shouldReturnEmptyList_whenNoTagsExist() {
        // Configure the mock repository to return an empty list when findAll is called
        when(tagRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Call the method under test
        List<TagResponseDto> actualTagDtos = tagService.getAllTags();

        // 1. Check that the returned list is empty
        assertNotNull(actualTagDtos, "The returned list of tags should not be null");
        assertTrue(actualTagDtos.isEmpty(), "The returned list of tags should be empty");

        // 2. Verify that findAll was called once
        verify(tagRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getAllTags_shouldReturnListOfTags_whenTagsExist() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("First Tag");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Second Tag");

        List<Tag> expectedTags = Arrays.asList(tag1, tag2);

        // Simulate tag DTOs
        TagResponseDto expectedTagDto1 = new TagResponseDto(
                tag1.getId(),
                tag1.getName()
        );

        TagResponseDto expectedTagDto2 = new TagResponseDto(
                tag2.getId(),
                tag2.getName()
        );

        List<TagResponseDto> expectedTagDtos = Arrays.asList(expectedTagDto1, expectedTagDto2);

        // Configure the mock repository to return a list of tags when findAll is called
        when(tagRepositoryMock.findAll()).thenReturn(expectedTags);

        // Call the method under test
        List<TagResponseDto> actualTagDtos = tagService.getAllTags();

        // 1. Check that the returned list is not empty
        assertNotNull(actualTagDtos, "The returned list of tags should not be null");
        assertFalse(actualTagDtos.isEmpty(), "The returned list of tags should not be empty");

        // 2. Check that the returned list matches the expected list
        assertEquals(2, actualTagDtos.size(), "The returned list of tags should have 2 elements");

        // 3. Check that the expected tags are equivalent to the actual ones
        assertEquals(expectedTagDtos, actualTagDtos, "The returned list of tags should match the expected list");

        // 4. Verify that findAll was called once
        verify(tagRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getTagById_shouldReturnTag_whenIdExists() {
        Tag expectedTag = new Tag();
        expectedTag.setId(1L);
        expectedTag.setName("First Tag");

        // Simulate expected tag DTO
        TagResponseDto expectedTagDto = new TagResponseDto(
                expectedTag.getId(),
                expectedTag.getName()
        );

        // Configure the mock repository to return the expected tag when findById is called
        when(tagRepositoryMock.findById(1L)).thenReturn(Optional.of(expectedTag));

        // Call the method under test
        Optional<TagResponseDto> actualTagDtoOptional = tagService.getTagById(1L);

        // 1. Check that the returned optional is present
        assertTrue(actualTagDtoOptional.isPresent(), "The returned optional should contain a tag for existing ID");
        TagResponseDto actualTagDto = actualTagDtoOptional.get();

        // 2. Check that fields were retrieved correctly
        assertEquals(expectedTagDto.getId(), actualTagDto.getId(), "ID of the returned tag should match the ID of the saved tag");
        assertEquals(expectedTagDto.getName(), actualTagDto.getName(), "Name of the returned tag should match the name of the saved tag");

        // 3. Verify that findById was called once
        verify(tagRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void getTagById_shouldReturnEmptyOptional_whenIdDoesNotExist() {
        // Configure the mock repository to return an empty optional when findById is called
        when(tagRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        // Call the method under test
        Optional<TagResponseDto> actualTagDtoOptional = tagService.getTagById(99L);

        // 1. Check that the returned optional is empty
        assertNotNull(actualTagDtoOptional, "The returned optional should not be null");
        assertTrue(actualTagDtoOptional.isEmpty(), "The returned optional should be empty");

        // 2. Verify that findById was called once
        verify(tagRepositoryMock, times(1)).findById(99L);
    }

    @Test
    public void updateTag_shouldUpdateAndReturnTag_whenIdExistsAndNameIsValid() {
        Long tagIdToUpdate = 1L;
        String oldNormalizedName = "old tag name";
        String newNameFromDetails = " New Valid Name ";
        String newNormalizedName = "new valid name";

        // Simulate the tag to update in the repository
        Tag existingTagInRepo = new Tag();
        existingTagInRepo.setId(tagIdToUpdate);
        existingTagInRepo.setName(oldNormalizedName);

        // Simulate the details coming in for the update request
        Tag tagUpdateDetails = new Tag();
        tagUpdateDetails.setName(newNameFromDetails);

        // Simulate the updated tag returned from the repository after a successful update
        Tag expectedSavedTag = new Tag();
        expectedSavedTag.setId(tagIdToUpdate);
        expectedSavedTag.setName(newNormalizedName);

        // Simulate the expected tag DTO
        TagResponseDto expectedTagDto = new TagResponseDto(
                tagIdToUpdate,
                newNormalizedName
        );

        // Configure the mock repository behavior:
        //  - findById should return the existing tag in the repository
        when(tagRepositoryMock.findById(tagIdToUpdate)).thenReturn(Optional.of(existingTagInRepo));
        //  - findByNameIgnoreCase should return an empty optional to simulate no other tag has this name
        when(tagRepositoryMock.findByNameIgnoreCase(newNormalizedName)).thenReturn(Optional.empty());
        //  - save should return the expected tag from the repository when the updated Tag is passed
        when(tagRepositoryMock.save(any(Tag.class))).thenReturn(expectedSavedTag);

        // Call the method under test
        Optional<TagResponseDto> actualUpdatedTagDtoOptional = tagService.updateTag(tagIdToUpdate, tagUpdateDetails);

        // 1. Check that the returned optional is present
        assertTrue(actualUpdatedTagDtoOptional.isPresent(), "The returned optional should contain a tag for existing ID");
        TagResponseDto actualUpdatedTagDto = actualUpdatedTagDtoOptional.get();

        // 2. Check that the returned tag matches the expected tag
        assertEquals(expectedTagDto.getId(), actualUpdatedTagDto.getId(), "ID of the returned tag should match the ID of the saved tag");
        assertEquals(expectedTagDto.getName(), actualUpdatedTagDto.getName(), "Name of the returned tag should match the name of the saved tag");

        // 3. Verify that findById and findByNameIgnoreCase were called once
        verify(tagRepositoryMock, times(1)).findById(tagIdToUpdate);
        verify(tagRepositoryMock, times(1)).findByNameIgnoreCase(newNormalizedName);

        // 4. Verify that save was called once with the correct tag
        ArgumentCaptor<Tag> tagArgumentCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepositoryMock, times(1)).save(tagArgumentCaptor.capture());

        assertEquals(newNormalizedName, tagArgumentCaptor.getValue().getName(), "The saved tag should have the expected name");
    }

    @Test
    public void updateTag_shouldReturnEmptyOptional_whenIdDoesNotExist() {
        Long nonExistentTagId = 99L; // An ID we assume doesn't exist
        Tag tagUpdateDetails = new Tag();
        tagUpdateDetails.setName("Some Name");

        // Configure the mock repository to return an empty optional when findById is called
        when(tagRepositoryMock.findById(nonExistentTagId)).thenReturn(Optional.empty());

        // Call the method under test
        Optional<TagResponseDto> actualUpdatedTagDtoOptional = tagService.updateTag(nonExistentTagId, tagUpdateDetails);

        // 1. Check that the returned optional is empty
        assertNotNull(actualUpdatedTagDtoOptional, "The returned optional should not be null");
        assertTrue(actualUpdatedTagDtoOptional.isEmpty(), "The returned optional should be empty");

        // 2. Verify that findById was called once
        verify(tagRepositoryMock, times(1)).findById(nonExistentTagId);

        // 3. Verify that findByNameIgnoreCase was NOT called
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());

        // 4. Verify that save was NOT called
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void updateTag_shouldReturnEmptyOptional_whenNewNameConflictsWithAnotherTag() {
        Long tagIdToUpdate = 1L;
        String originalNameForTag1 = "original name";
        String conflictingName = " EXiSTing NaME ";
        String conflictingNormalizedName = "existing name";

        // Simulate the tag to update in the repository
        Tag tagToUpdate = new Tag();
        tagToUpdate.setId(tagIdToUpdate);
        tagToUpdate.setName(originalNameForTag1);

        // Simulate another tag that already has the conflicting name
        Tag anotherTagWithConflictingName = new Tag();
        anotherTagWithConflictingName.setId(2L);
        anotherTagWithConflictingName.setName(conflictingName);

        // Simulate the details coming in for the update request
        Tag tagUpdateDetails = new Tag();
        tagUpdateDetails.setName(conflictingName);

        // Configure the mock repository behavior:
        //  - findById should return the existing tag in the repository
        when(tagRepositoryMock.findById(tagIdToUpdate)).thenReturn(Optional.of(tagToUpdate));
        //  - findByNameIgnoreCase should return the conflicting tag
        when(tagRepositoryMock.findByNameIgnoreCase(conflictingNormalizedName)).thenReturn(Optional.of(anotherTagWithConflictingName));

        // Call the method under test
        Optional<TagResponseDto> actualUpdatedTagDtoOptional = tagService.updateTag(tagIdToUpdate, tagUpdateDetails);

        // 1. Check that the returned optional is empty
        assertNotNull(actualUpdatedTagDtoOptional, "The returned optional should not be null");
        assertTrue(actualUpdatedTagDtoOptional.isEmpty(), "The returned optional should be empty");

        // 2. Verify that findById and findByNameIgnoreCase were called once
        verify(tagRepositoryMock, times(1)).findById(tagIdToUpdate);
        verify(tagRepositoryMock, times(1)).findByNameIgnoreCase(conflictingNormalizedName);

        // 3. Verify that save was NOT called
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void updateTag_shouldThrowIllegalArgumentException_whenNameIsNull() {
        Long tagIdToUpdate = 1L;
        Tag tagUpdateDetailsWithNullName = new Tag();
        tagUpdateDetailsWithNullName.setName(null);

        // The expected error message
        String expectedErrorMessage = "Tag name for update cannot be null or empty";

        // Call the method under test and check that it throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.updateTag(tagIdToUpdate, tagUpdateDetailsWithNullName);
        }, "Should throw an IllegalArgumentException when the tag name is null");

        // 1. Check that the exception message matches the expected message
        assertEquals(expectedErrorMessage, exception.getMessage(), "The exception message should match the expected message");

        // 2. Verify no repository methods were called
        verify(tagRepositoryMock, never()).findById(anyLong());
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void updateTag_shouldThrowIllegalArgumentException_whenNameIsEmpty() {
        Long tagIdToUpdate = 1L;
        Tag tagUpdateDetailsWithNullName = new Tag();
        tagUpdateDetailsWithNullName.setName("");

        // The expected error message
        String expectedErrorMessage = "Tag name for update cannot be null or empty";

        // Call the method under test and check that it throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.updateTag(tagIdToUpdate, tagUpdateDetailsWithNullName);
        }, "Should throw an IllegalArgumentException when the tag name is null");

        // 1. Check that the exception message matches the expected message
        assertEquals(expectedErrorMessage, exception.getMessage(), "The exception message should match the expected message");

        // 2. Verify no repository methods were called
        verify(tagRepositoryMock, never()).findById(anyLong());
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void updateTag_shouldThrowIllegalArgumentException_whenNameIsBlank() {
        Long tagIdToUpdate = 1L;
        Tag tagUpdateDetailsWithNullName = new Tag();
        tagUpdateDetailsWithNullName.setName("  ");

        // The expected error message
        String expectedErrorMessage = "Tag name for update cannot be null or empty";

        // Call the method under test and check that it throws an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.updateTag(tagIdToUpdate, tagUpdateDetailsWithNullName);
        }, "Should throw an IllegalArgumentException when the tag name is null");

        // 1. Check that the exception message matches the expected message
        assertEquals(expectedErrorMessage, exception.getMessage(), "The exception message should match the expected message");

        // 2. Verify no repository methods were called
        verify(tagRepositoryMock, never()).findById(anyLong());
        verify(tagRepositoryMock, never()).findByNameIgnoreCase(anyString());
        verify(tagRepositoryMock, never()).save(any(Tag.class));
    }

    @Test
    public void deleteTag_shouldReturnTrue_whenIdExists() {
        Long tagIdToDelete = 1L;

        // Configure the mock repository behavior:
        //  - existsById should return true to simulate the tag exists
        when(tagRepositoryMock.existsById(tagIdToDelete)).thenReturn(true);
        //  - deleteById should return void to simulate the tag was deleted successfully
        doNothing().when(tagRepositoryMock).deleteById(tagIdToDelete);

        // Call the method under test
        boolean result = tagService.deleteTag(tagIdToDelete);

        // 1. Check that the result is true
        assertTrue(result, "deleteTag should return true when the tag is deleted successfully");

        // 2. Verify that existsById and deleteById were called once
        verify(tagRepositoryMock, times(1)).existsById(tagIdToDelete);
        verify(tagRepositoryMock, times(1)).deleteById(tagIdToDelete);
    }

    @Test
    public void deleteTag_shouldReturnFalse_whenIdDoesNotExist() {
        Long nonExistentTagId = 99L;

        // Configure the mock repository behavior to return false for existsById
        when(tagRepositoryMock.existsById(nonExistentTagId)).thenReturn(false);

        // Call the method under test
        boolean result = tagService.deleteTag(nonExistentTagId);

        // 1. Check that the result is false
        assertFalse(result, "deleteTag should return false when the tag does not exist");

        // 2. Verify that existsById was called once
        verify(tagRepositoryMock, times(1)).existsById(nonExistentTagId);

        // 3. Verify that deleteById was NOT called
        verify(tagRepositoryMock, never()).deleteById(anyLong());
    }

}
