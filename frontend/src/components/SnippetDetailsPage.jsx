import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getSnippetById, updateSnippet } from '../services/snippetApiService.js';

/**
 * @module SnippetDetailsPage
 * @description A React component that displays the details of a specific snippet.
 * It fetches the snippet data from the backend API using the snippet ID from the URL parameters.
 * @param {Object} props - The component props.
 * @param {Function} props.onSnippetUpdated - A callback function to be called when the snippet is updated successfully.
 */
function SnippetDetailsPage({ onSnippetUpdated }) {
    const { snippetId } = useParams(); // Get the snippet ID from the URL parameters
    const navigate = useNavigate();

    const [snippet, setSnippet] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const [isEditing, setIsEditing] = useState(false);
    const [editableTitle, setEditableTitle] = useState('');
    const [editableContent, setEditableContent] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [feedbackMessage, setFeedbackMessage] = useState('');
    const [isUpdateError, setIsUpdateError] = useState(false);

    /**
     * Fetches the snippet details when the component mounts or when the snippetId changes.
     */
    useEffect(() => {
        const fetchSnippet = async () => {
            if (!snippetId) return;

            setIsLoading(true);
            setError(null);
            setFeedbackMessage('');
            setIsUpdateError(false);

            try {
                const data = await getSnippetById(snippetId);
                setSnippet(data);
                setEditableTitle(data.title);
                setEditableContent(data.content);
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

    const handleEditToggle = () => {
        if (!isEditing && snippet) {
            setEditableTitle(snippet.title);
            setEditableContent(snippet.content);
            setFeedbackMessage('');
            setIsUpdateError(false);
        }

        setIsEditing(!isEditing);
    }

    const handleSubmitUpdate = async (event) => {
        event.preventDefault();

        if (!editableTitle.trim() || !editableContent.trim()) {
            setFeedbackMessage('Title and content cannot be empty.');
            setIsUpdateError(true);
            return;
        }

        setIsSubmitting(true);
        setFeedbackMessage('');
        setIsUpdateError(false);

        try {
            const updatedData = {
                title: editableTitle,
                content: editableContent,
            };
            const updatedSnippet = await updateSnippet(snippetId, updatedData);

            setSnippet(updatedSnippet);
            setIsEditing(false);
            setFeedbackMessage(`Snippet "${updatedSnippet.title}" updated successfully!`);
            setIsUpdateError(false);

            if (onSnippetUpdated) {
                onSnippetUpdated();
            }
        } catch (error) {
            console.error(`Failed to update snippet with id - ${snippetId}:`, error);
            setFeedbackMessage(error.message || 'Failed to update snippet.');
            setIsUpdateError(true);
        } finally {
            setIsSubmitting(false);
        }
    }

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

            {isEditing ? (
                <form onSubmit={handleSubmitUpdate}> {/* Edit Mode Form */}
                    
                    <h2 className="text-3xl font-semibold text-sky-700 mb-6 border-b pb-3">Edit Snippet</h2>

                    {/* Edit Title */}
                    <div className="mb-4">
                        <label htmlFor="editableTitle" className="block text-sm font-medium text-gray-700 mb-1">
                            Title
                        </label>
                        <input
                            type="text"
                            id="editableTitle"
                            value={editableTitle}
                            onChange={(e) => setEditableTitle(e.target.value)}
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-sky-500 focus:border-sky-500 sm:text-sm"
                            disabled={isSubmitting}
                            required
                        />
                    </div>

                    {/* Edit Content */}
                    <div className="mb-6">
                        <label htmlFor="editableContent" className="block text-sm font-medium text-gray-700 mb-1">
                            Content
                        </label>
                        <textarea
                            id="editableContent"
                            value={editableContent}
                            onChange={(e) => setEditableContent(e.target.value)}
                            rows="10"
                            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-sky-500 focus:border-sky-500 sm:text-sm font-mono"
                            disabled={isSubmitting}
                            required
                        />
                    </div>

                    {/* Feedback Message */}
                    {feedbackMessage && isEditing && (
                        <p className={`mb-4 text-sm ${isUpdateError ? 'text-red-600' : 'text-green-600'}`}>
                            {feedbackMessage}
                        </p>
                    )}

                    {/* Edit Mode Form Buttons */}
                    <div className="mt-6 flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={handleEditToggle}
                            disabled={isSubmitting}
                            className="px-5 py-2.5 border border-gray-300 text-sm font-medium rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500 cursor-pointer"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="px-5 py-2.5 bg-sky-600 text-white text-sm font-medium rounded-md shadow-sm hover:bg-sky-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500 disabled:opacity-50 cursor-pointer"
                        >
                            {isSubmitting ? 'Saving...' : 'Save Changes'}
                        </button>
                    </div>

                </form>
            ) : (
                <div> {/* View Mode */}

                    {/* Back Arrow */}
                    <div className="mb-4">
                        <Link to="/" className="text-sm text-sky-600 hover:text-sky-800 flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18" />
                            </svg>
                        </Link>
                    </div>

                    {/* Display Title */}
                    <div className="pb-2 border-b border-gray-200 flex justify-between items-start">
                        <h2 className="text-3xl font-semibold text-sky-700 break-all mr-4">{snippet.title}</h2>
                        <div>
                            {/* Edit Button */}
                            <button
                                onClick={handleEditToggle}
                                className="mr-2 px-4 py-2 text-sm font-medium text-sky-600 bg-sky-100 hover:bg-sky-200 rounded-md focus:outline-none focus:ring-2 focus:ring-sky-500 cursor-pointer"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L6.832 19.82a4.5 4.5 0 0 1-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 0 1 1.13-1.897L16.863 4.487Zm0 0L19.5 7.125" />
                                </svg>
                            </button>
                        </div>
                    </div>
        
                    {/* Display Content */}
                    <pre className="text-gray-800 whitespace-pre-wrap bg-gray-50 p-4 rounded-md text-sm font-mono overflow-x-auto mt-4">
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
            )}

            {feedbackMessage && !isEditing && (
                <p className={`mt-4 text-sm ${isUpdateError ? 'text-red-600' : 'text-green-600'}`}>
                    {feedbackMessage}
                </p>
            )}

        </div>
    )
}

export default SnippetDetailsPage;
