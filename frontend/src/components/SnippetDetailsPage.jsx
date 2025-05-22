import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getSnippetById } from '../services/snippetApiService.js';

/**
 * @module SnippetDetailsPage
 * @description A React component that displays the details of a specific snippet.
 * It fetches the snippet data from the backend API using the snippet ID from the URL parameters.
 */
function SnippetDetailsPage() {
    const { snippetId } = useParams(); // Get the snippet ID from the URL parameters
    const [snippet, setSnippet] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    /**
     * Fetches the snippet details when the component mounts or when the snippetId changes.
     */
    useEffect(() => {
        const fetchSnippet = async () => {
            if (!snippetId) return;

            setIsLoading(true);
            setError(null);

            try {
                const data = await getSnippetById(snippetId);
                setSnippet(data);
            } catch (error) {
                console.error(`Failed to fetch snippet with id: ${snippetId}"`, error);
                setError(error.message || `Failed to fetch snippet with id: ${snippetId}.`);
                setSnippet(null);
            } finally {
                setIsLoading(false);
            }
        };

        fetchSnippet();
    }, [snippetId]); // Re-run effect when snippetId changes

    if (isLoading) {
        return <div className="text-center p-4 text-gray-600">Loading snippet details...</div>;
    }

    if (error) {
        return (
            <div className="text-center p-4 text-red-600 bg-red-100 border border-red-400 rounded">
                <p>Error: {error}</p>
                <Link to="/" className="mt-4 inline-block px-4 py-2 bg-sky-500 text-white rounded hover:bg-sky-600">
                    Back to Snippet List
                </Link>
            </div>
        );
    }

    if (!snippet) {
        return (
            <div className="text-center p-4 text-gray-500">
                Snippet not found.
                <br />
                <Link to="/" className="mt-4 inline-block px-4 py-2 bg-sky-500 text-white rounded hover:bg-sky-600">
                    Back to Snippet List
                </Link>
            </div>
        )
    }

    return (
        <div className="p-6 bg-white shadow-xl rounded-lg border border-gray-200">

            {/* Display Title */}
            <div className="mb-4 pb-2 border-b flex justify-between items-center">
                <h2 className="text-3xl font-semibold text-sky-700">{snippet.title}</h2>
                <Link to="/" className="text-sm text-sky-600 hover:text-sky-800 hover:underline">
                    &larr; Back to List
                </Link>
            </div>

            {/* Display Content */}
            <pre className="text-gray-800 whitespace-pre-wrap bg-gray-50 p-4 rounded-md text-sm font-mono overflow-x-auto">
                {snippet.content}
            </pre>

            {/* Display Creation & Modified Dates */}
            <div className="mt-6 text-xs text-gray-500 space-y-1">
                {snippet.creationDate && (
                    <p><strong>Created:</strong> {new Date(snippet.creationDate).toLocaleString()}</p>
                )}
                {snippet.lastModifiedDate && (
                    <p><strong>Last Modified:</strong> {new Date(snippet.lastModifiedDate).toLocaleString()}</p>
                )}
            </div>

            {/* Display Tags */}
            {snippet.tags && snippet.tags.length > 0 && (
                <div className="mt-6 pt-3 border-t border-gray-200">
                    <h4 className="text-md font-medium text-gray-700 mb-2">Tags:</h4>
                    {snippet.tags.map(tag => (
                        <span
                            key={tag.id}
                            className="inline-block bg-sky-100 text-sky-800 text-xs font-semibold mr-2 mb-2 px-3 py-1 rounded-full"
                        >
                            {tag.name}
                        </span>
                    ))}
                </div>
            )}

        </div>
    )
}

export default SnippetDetailsPage;
