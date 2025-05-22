// frontend/src/services/snippetApiService.js

const API_BASE_URL = 'http://localhost:8080/api/v1';

/**
 * Fetches all snippets from the backend API
 * 
 * @async
 * @returns {Promise<Array<Object>>} A promise that resolves to an array of Snippet DTOs
 * @throws {Error} If the fetch fails or the response is not ok.
 */
export const getAllSnippets = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/snippets`);
        if (!response.ok) {
            // Attempt to parse error response from backend
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            throw new Error(`Failed to fetch snippets: ${response.status} ${errorData.message || ''}`);
        }

        // Parse the JSON response body
        return await response.json();
    } catch (error) {
        console.error("Error in getAllSnippets:", error);
        throw error;
    }
}

/**
 * Fetches a single snippet by its ID from the backend API
 * 
 * @async
 * @param {number|string} id - The ID of the snippet to fetch.
 * @returns {Promise<Object>} A promise that resolves to the Snippet DTO.
 * @throws {Error} If the fetch fails or the response is not ok.
 */
export const getSnippetById = async (id) => {
    try {
        const response = await fetch(`${API_BASE_URL}/snippets/${id}`);
        if (!response.ok) {
            // Attempt to parse error response from backend
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            throw new Error(`Failed to fetch snippet: ${response.status} ${errorData.message || ''}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Error in getSnippetById for id - ${id}:`, error);
        throw error;
    }
}

/**
 * Creates a new snippet through the backend API
 * 
 * @async
 * @param {object} snippetData - An object containing the snippet's details.
 * @param {string} [snippetData.title] - The title of the snippet.
 * @param {string} [snippetData.content] - The content of the snippet.
 * @returns {Promise<Array<Object>>} A promise that resolves to the created Snippet DTO.
 * @throws {Error} If the fetch fails or the response is not ok.
 */
export const createSnippet = async (snippetData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/snippets`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(snippetData), // Convert the JavaScript object to a JSON string
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            throw new Error(`Failed to create snippet: ${response.status} ${errorData.message || response.statusText}`);
        }

        return await response.json(); // Parse the JSON response body
    } catch (error) {
        console.error("Error in createSnippet:", error);
        throw error;
    }
}

/**
 * Updates an existing snippet through the backend API
 * 
 * @async
 * @param {number|string} id - The ID of the snippet to update.
 * @param {object} snippetData - An object containing the updated snippet's details.
 * @param {string} [snippetData.title] - The title of the snippet.
 * @param {string} [snippetData.content] - The content of the snippet.
 * @returns {Promise<Object>} A promise that resolves to the updated Snippet DTO.
 * @throws {Error} If the fetch fails or the response is not ok.
 */
export const updateSnippet = async (id, snippetData) => {
    try {
        const response = await fetch(`${API_BASE_URL}/snippets/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(snippetData),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            throw new Error(`Failed to update snippet: ${response.status} ${errorData.message || response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Error in updateSnippet for id - ${id}:`, error);
        throw error;
    }
}
