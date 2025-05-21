// frontend/src/components/CreateSnippetForm.jsx
import React, { useState } from 'react';
import { createSnippet } from '../services/snippetApiService.js';

/**
 * @module CreateSnippetForm
 * @description A React component that provides a form for creating new snippets.
 * It handles form input state, submission, error states and feedback messages.
 * @param {Object} props - The component props.
 * @param {Function} props.onSnippetCreated - A callback function to be called when a new snippet is created successfully.
 */
function CreateSnippetForm({ onSnippetCreated }) {
    // State for form inputs
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    // State for 
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [feedbackMessage, setFeedbackMessage] = useState('');
    const [isError, setIsError] = useState(false);

    /**
     * Handles the form submission event and calls the API service.
     * 
     * @async
     * @param {React.SyntheticEvent} event - The form submission event.
     */
    const handleSubmit = async (event) => {
        // Prevent page reload and default form submission behavior
        event.preventDefault();

        // Basic client-side validation
        if (!title.trim() || !content.trim()) {
            setFeedbackMessage('Title and content cannot be empty.');
            setIsError(true);
            return;
        }

        setIsSubmitting(true);
        setFeedbackMessage('');
        setIsError(false);

        try {
            // Call the API service to create a new snippet
            const newSnippetData = { title, content };
            const createdSnippet = await createSnippet(newSnippetData);

            setFeedbackMessage(`Snippet "${createdSnippet.title}" created successfully with ID: ${createdSnippet.id}!`);
            setIsError(false);

            // Clear form fields
            setTitle('');
            setContent('');

            if (onSnippetCreated) {
                onSnippetCreated();
            }
        } catch (error) {
            console.error("Failed to create snippet:", error);
            setFeedbackMessage(error.message || 'Failed to create snippet. Please try again.');
            setIsError(true);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="p-6 mt-8 bg-white shadow-xl rounded-lg border border-gray-200">
            <h2 className="text-2xl font-semibold text-gray-800 mb-6">Create New Snippet</h2>
            <form onSubmit={handleSubmit}>

                {/* Display Title */}
                <div className="mb-4">
                    <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
                        Title
                    </label>
                    <input
                        type="text"
                        id="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)} // Update 'title' state
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-sky-500 focus:border-sky-500 sm:text-sm"
                        disabled={isSubmitting} // Disable input when submitting
                        required
                    />
                </div>

                {/* Display Content */}
                <div className="mb-6">
                    <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-1">
                        Content
                    </label>
                    <textarea
                        id="content"
                        value={content}
                        onChange={(e) => setContent(e.target.value)} // Update 'content' state
                        rows="6"
                        className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-sky-500 focus:border-sky-500 sm:text-sm"
                        disabled={isSubmitting} // Disable input when submitting
                        required
                    />
                </div>

                {/* Display Create Button */}
                <div className="flex items-center justify-end">
                    <button
                        type="submit"
                        className="px-6 py-2.5 bg-sky-600 text-white font-medium text-sm rounded-md shadow-sm hover:bg-sky-700 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2 disabled:opacity-50 cursor-pointer disabled:cursor-not-allowed"
                        disabled={isSubmitting} // Disable button when submitting
                    >
                        {isSubmitting ? 'Creating...' : 'Create Snippet'}
                    </button>
                </div>

                {/* Display Feedback Message */}
                {feedbackMessage && (
                    <p className={`mt-4 text-sm ${isError ? 'text-red-600' : 'text-green-600'}`}>
                        {feedbackMessage}
                    </p>
                )}
            </form>
        </div>
    );
}

export default CreateSnippetForm;
