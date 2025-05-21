// frontend/src/components/SnippetList.jsx
import React, { useState, useEffect } from 'react';
import { getAllSnippets } from '../services/snippetApiService.js';

/**
 * @module SnippetList
 * @description A React component that fetches and displays a list of snippets.
 * It handles loading and error states during the fetch operation.
 */
function SnippetList() {
    // State to store the snippet DTOs fetched from the backend
    const [snippets, setSnippets] = useState([]);

    // State to track loading status
    const [isLoading, setIsLoading] = useState(true);

    // State to track any errors that occur during the fetch operation
    const [isError, setError] = useState(null);

    // Effect hook to fetch snippets when the component mounts
    useEffect(() => {
        const fetchSnippets = async () => {
            try {
                setIsLoading(true); // Indicate loading has started
                setError(null); // Clear any previous errors
                const data = await getAllSnippets(); // Call the API service
                setSnippets(data); // Store the fetched snippets in state
            } catch (err) {
                console.error("Error fetching snippets:", err);
                setError(err.message || "An unknown error occurred while fetching snippets.");
                setSnippets([]);
            } finally {
                setIsLoading(false); // Indicate loading has finished
            }
        };

        fetchSnippets();
    }, []);

    // Conditional rendering based on loading and error states
    if (isLoading) {
        return <div className="text-center p-4">Loading snippets...</div>;
    }

    if (isError) {
        return <div className="text-center p-4 text-red-500">Error: {isError}</div>;
    }

    return (
        <div className="p-4 bg-white shadow-md rounded-lg">
            <h2 className="text-2x1 font-semibold text-gray-800 mb-6 border-b pb-2">
                Available Snippets
            </h2>
            {snippets.length === 0 ? (
                <p className="text-gray-600">No snippets found.</p>
            ) : (
                <ul className="space-y-4">
                    {snippets.map(snippet => (
                        <li key={snippet.id} className="p-4 border border-gray-200 rounded-md hover:shadow-lg transition-shadow">
                            {/* Display Title */}
                            <h3 className="text-xl font-medium text-sky-700">{snippet.title}</h3>

                            {/* Display Content */}
                            <pre className="text-gray-700 mt-1 whitespace-pre-wrap bg-gray-100 p-3 rounded-md text-sm font-mono overflow-x-auto">
                                {snippet.content}
                            </pre>

                            {/* Display Creation & Modified Dates */}
                            <div className="mt-3 text-xs text-gray-500">
                                {snippet.creationDate && (
                                    <p>Created: {new Date(snippet.creationDate).toLocaleString()}</p>
                                )}
                                {snippet.lastModifiedDate && (
                                    <p>Last Modified: {new Date(snippet.lastModifiedDate).toLocaleString()}</p>
                                )}
                            </div>

                            {/* Display Tags */}
                            <div className="mt-4 pt-2 border-t border-gray-200">
                                <span className="text-sm font-medium text-gray-600">Tags: </span>
                                {snippet.tags.map(tag => (
                                    <span
                                        key={tag.id}
                                        className="inline-block bg-sky-100 text-sky-800 text-xs front-semibold mr-2 px-2.5 py-0.5 rounded-full"
                                    >
                                        {tag.name}
                                    </span>
                                ))}
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default SnippetList;
