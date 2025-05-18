package com.severentertainment.snippetmanager.service;

import com.severentertainment.snippetmanager.domain.Snippet;
import com.severentertainment.snippetmanager.repository.SnippetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SnippetServiceTest {

    @Mock
    private SnippetRepository snippetRepositoryMock;

    @InjectMocks
    private SnippetService snippetService;

    @Test
    public void getAllSnippets_shouldReturnEmptyList_whenNoSnippetsExist () {
        // Configure the mock repository to return an empty list when findAll is called
        when(snippetRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        // Call the method under test
        List<Snippet> actualSnippets = snippetService.getAllSnippets();

        // 1. Check that the returned list is empty
        assertNotNull(actualSnippets, "The returned list of snippets should not be null");
        assertTrue(actualSnippets.isEmpty(), "The returned list of snippets should be empty");

        // 2. Verify that findAll was called once
        verify(snippetRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getAllSnippets_shouldReturnListOfSnippets_whenSnippetsExist () {
        Snippet snippet1 = new Snippet(1L, "First Snippet", "Content of first snippet", Instant.now(), Instant.now());
        Snippet snippet2 = new Snippet(2L, "Second Snippet", "Content of second snippet", Instant.now(), Instant.now());
        List<Snippet> expectedSnippets = Arrays.asList(snippet1, snippet2);

        // Configure the mock repository to return a list of snippets when findAll is called
        when(snippetRepositoryMock.findAll()).thenReturn(expectedSnippets);

        // Call the method under test
        List<Snippet> actualSnippets = snippetService.getAllSnippets();

        // 1. Check that the returned list is not empty
        assertNotNull(actualSnippets, "The returned list of snippets should not be null");
        assertFalse(actualSnippets.isEmpty(), "The returned list of snippets should not be empty");

        // 2. Check that the returned list matches the expected list
        assertEquals(2, actualSnippets.size(), "The returned list of snippets should have 2 elements");
        assertEquals(expectedSnippets, actualSnippets, "The returned list of snippets should match the expected list");

        // 3. Verify that findAll was called once
        verify(snippetRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getSnippetById_shouldReturnSnippet_whenIdExists () {
        Snippet expectedSnippet = new Snippet(1L, "First Snippet", "Content of first snippet", Instant.now(), Instant.now());

        // Configure the mock repository to return the expected snippet when findById is called
        when(snippetRepositoryMock.findById(1L)).thenReturn(Optional.of(expectedSnippet));

        // Call the method under test
        Optional<Snippet> actualSnippetOptional = snippetService.getSnippetById(1L);

        // 1. Check that the returned optional is present
        assertTrue(actualSnippetOptional.isPresent(), "The returned optional should contain a snippet for existing ID");
        assertEquals(expectedSnippet, actualSnippetOptional.get(), "The found snippet should match the expected snippet");
        assertEquals("First Snippet", actualSnippetOptional.get().getTitle(), "The title of the found snippet should match the expected title");

        // 2. Verify that findById was called once
        verify(snippetRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void getSnippetById_shouldReturnEmptyOptional_whenIdDoesNotExist () {
        // Configure the mock repository to return an empty optional when findById is called
        when(snippetRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        // Call the method under test
        Optional<Snippet> actualSnippetOptional = snippetService.getSnippetById(99L);

        // 1. Check that the returned optional is empty
        assertNotNull(actualSnippetOptional, "The returned optional should not be null");
        assertTrue(actualSnippetOptional.isEmpty(), "The returned optional should be empty");

        // 2. Verify that findById was called once
        verify(snippetRepositoryMock, times(1)).findById(99L);
    }

    @Test
    public void createSnippet_shouldSaveAndReturnSnippet() {
        Snippet snippetToCreate = new Snippet();
        snippetToCreate.setTitle("New Snippet");
        snippetToCreate.setContent("Content of new snippet");

        Snippet savedSnippetFromRepo = new Snippet();
        savedSnippetFromRepo.setId(1L);
        savedSnippetFromRepo.setTitle(snippetToCreate.getTitle());
        savedSnippetFromRepo.setContent(snippetToCreate.getContent());
        savedSnippetFromRepo.setCreationData(Instant.now());
        savedSnippetFromRepo.setLastModifiedData(Instant.now());

        // Configure the mock repository to return the saved snippet when save is called
        when(snippetRepositoryMock.save(any(Snippet.class))).thenReturn(savedSnippetFromRepo);

        // Call the method under test
        Snippet actualCreatedSnippet = snippetService.createSnippet(snippetToCreate);

        // 1. Check that the returned snippet is not null
        assertNotNull(actualCreatedSnippet, "The returned snippet should not be null");

        // 2. Check that the returned snippet matches the saved snippet
        assertEquals(savedSnippetFromRepo.getId(), actualCreatedSnippet.getId(), "The ID of the returned snippet should match the ID of the saved snippet");
        assertEquals(snippetToCreate.getTitle(), actualCreatedSnippet.getTitle(), "The title of the returned snippet should match the title of the saved snippet");
        assertEquals(snippetToCreate.getContent(), actualCreatedSnippet.getContent(), "The content of the returned snippet should match the content of the saved snippet");
        assertEquals(savedSnippetFromRepo.getCreationData(), actualCreatedSnippet.getCreationData(), "The creation date of the returned snippet should match the creation date of the saved snippet");
        assertEquals(savedSnippetFromRepo.getLastModifiedData(), actualCreatedSnippet.getLastModifiedData(), "The last modified date of the returned snippet should match the last modified date of the saved snippet");

        // 3. Verify that save was called once with the correct snippet
        ArgumentCaptor<Snippet> snippetArgumentCaptor = ArgumentCaptor.forClass(Snippet.class);
        verify(snippetRepositoryMock, times(1)).save(snippetArgumentCaptor.capture());

        Snippet snippetPassedToRepository = snippetArgumentCaptor.getValue();
        assertEquals(snippetToCreate.getTitle(), snippetPassedToRepository.getTitle(), "The title of the snippet passed to the repository should match the title of the snippet to create");
        assertEquals(snippetToCreate.getContent(), snippetPassedToRepository.getContent(), "The content of the snippet passed to the repository should match the content of the snippet to create");
        assertNull(snippetPassedToRepository.getId(), "The ID of the snippet passed to the repository should be null");
        assertNull(snippetPassedToRepository.getCreationData(), "The creation date of the snippet passed to the repository should be null");
        assertNull(snippetPassedToRepository.getLastModifiedData(), "The last modified date of the snippet passed to the repository should be null");
    }

    @Test
    public void updateSnippet_shouldUpdateAndReturnSnippet_whenIdExists() {
        Long snippetId = 1L;
        Instant initialCreationDate = Instant.now().minusSeconds(3600);
        Instant initialUpdateDate = Instant.now().minusSeconds(3600);

        // Represents the state of the snippet before updating
        Snippet existingSnippet = new Snippet(snippetId, "Existing Snippet", "Content of existing snippet", initialCreationDate, initialUpdateDate);

        // Represents the new data coming in
        Snippet snippetUpdateDetails = new Snippet();
        snippetUpdateDetails.setTitle("Updated Snippet");
        snippetUpdateDetails.setContent("Content of updated snippet");

        // Simulates what the repository's save method would return after the update
        Snippet savedSnippetAfterUpdate = new Snippet();
        savedSnippetAfterUpdate.setId(snippetId);
        savedSnippetAfterUpdate.setTitle(snippetUpdateDetails.getTitle());
        savedSnippetAfterUpdate.setContent(snippetUpdateDetails.getContent());
        savedSnippetAfterUpdate.setCreationData(initialCreationDate);
        savedSnippetAfterUpdate.setLastModifiedData(Instant.now());

        // When findById is called, return the existing snippet
        when(snippetRepositoryMock.findById(snippetId)).thenReturn(Optional.of(existingSnippet));

        // When save is called, return the saved updated snippet
        when(snippetRepositoryMock.save(any(Snippet.class))).thenReturn(savedSnippetAfterUpdate);

        Optional<Snippet> actualUpdatedSnippetOptional = snippetService.updateSnippet(snippetId, snippetUpdateDetails);

        // 1. Check that the optional is present
        assertTrue(actualUpdatedSnippetOptional.isPresent(), "The returned optional should contain an updated snippet for an existing ID");
        Snippet actualUpdatedSnippet = actualUpdatedSnippetOptional.get();

        // 2. Check that the fields were updated correctly
        assertEquals(snippetId, actualUpdatedSnippet.getId(), "The ID should remain the same");
        assertEquals(snippetUpdateDetails.getTitle(), actualUpdatedSnippet.getTitle(), "The title should be updated");
        assertEquals(snippetUpdateDetails.getContent(), actualUpdatedSnippet.getContent(), "The content should be updated");
        assertEquals(initialCreationDate, actualUpdatedSnippet.getCreationData(), "The creation date should remain the same");

        assertNotNull(actualUpdatedSnippet.getLastModifiedData(), "The last modified date should be set");
        assertEquals(savedSnippetAfterUpdate.getLastModifiedData(), actualUpdatedSnippet.getLastModifiedData(), "The last modified date should match the date of the saved updated snippet");

        // 3. Verify repository interactions
        verify(snippetRepositoryMock, times(1)).findById(snippetId);

        ArgumentCaptor<Snippet> snippetArgumentCaptor = ArgumentCaptor.forClass(Snippet.class);
        verify(snippetRepositoryMock, times(1)).save(snippetArgumentCaptor.capture());

        Snippet snippetPassedToSave = snippetArgumentCaptor.getValue();
        assertEquals(snippetUpdateDetails.getTitle(), snippetPassedToSave.getTitle(), "The title of the snippet passed to the repository should match the title of the snippet to update");
        assertEquals(snippetUpdateDetails.getContent(), snippetPassedToSave.getContent(), "The content of the snippet passed to the repository should match the content of the snippet to update");

        assertSame(existingSnippet, snippetPassedToSave, "The snippet passed to the repository should be the same as the existing snippet");
    }

    @Test
    public void updateSnippet_shouldReturnEmptyOptional_whenIdDoesNotExist() {
        Long nonExistentId = 99L;

        // Represents the new data coming in
        Snippet snippetUpdateDetails = new Snippet();
        snippetUpdateDetails.setTitle("Attempted Updated Snippet");
        snippetUpdateDetails.setContent("Attempted Update Content");

        // When findById is called, return an empty optional
        when(snippetRepositoryMock.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<Snippet> actualUpdatedSnippetOptional = snippetService.updateSnippet(nonExistentId, snippetUpdateDetails);

        // 1. Check that the returned optional is empty
        assertNotNull(actualUpdatedSnippetOptional, "The returned optional should not be null");
        assertTrue(actualUpdatedSnippetOptional.isEmpty(), "The returned optional should be empty");

        // 2. Verify that findById was called
        verify(snippetRepositoryMock, times(1)).findById(nonExistentId);

        // 3. Verify that save was NOT called
        verify(snippetRepositoryMock, never()).save(any(Snippet.class));
    }

    @Test
    public void deleteSnippet_shouldReturnTrue_whenIdExists() {
        Long snippetId = 1L;

        // When existsById is called, return true
        when(snippetRepositoryMock.existsById(snippetId)).thenReturn(true);

        // When deleteById is called, execute, then do nothing
        doNothing().when(snippetRepositoryMock).deleteById(snippetId);

        boolean result = snippetService.deleteSnippet(snippetId);

        // 1. Check that the result is true, indicating deletion was successful
        assertTrue(result, "deleteSnippet should return true when the snippet exists and is deleted successfully");

        // 2. Verify that existsById was called once
        verify(snippetRepositoryMock, times(1)).existsById(snippetId);

        // 3. Verify that deleteById was called once
        verify(snippetRepositoryMock, times(1)).deleteById(snippetId);
    }

    @Test
    public void deleteSnippet_shouldReturnFalse_whenIdDoesNotExist() {
        Long nonExistentId = 99L;

        // When existsById is called, return false
        when(snippetRepositoryMock.existsById(nonExistentId)).thenReturn(false);

        boolean result = snippetService.deleteSnippet(nonExistentId);

        // 1. Check that the result is false, indicating deletion was not successful
        assertFalse(result, "deleteSnippet should return false when the snippet does not exist");

        // 2. Verify that existsById was called once
        verify(snippetRepositoryMock, times(1)).existsById(nonExistentId);

        // 3. Verify that deleteById was NOT called
        verify(snippetRepositoryMock, never()).deleteById(anyLong());
    }

}
