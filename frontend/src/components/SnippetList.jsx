// frontend/src/components/SnippetList.jsx
import React from 'react';
import { Link } from 'react-router-dom';

/**
 * @module SnippetList
 * @description A React component that fetches and displays a list of snippets.
 * It handles loading and error states during the fetch operation.
 * @param {Object} props - The component props.
 * @param {Array<Objects>} props.snippets - An array of Snippet DTOs to display.
 * @param {boolean} props.isLoading - A flag indicating if the data is currently being loaded.
 * @param {string} props.error - An error message if an error occurred during data fetching.
 * @param {Function} props.onRefresh - A callback function to refresh the list of snippets.
 */
function SnippetList({ snippets, isLoading, error, onRefresh }) {
    // Conditional rendering based on loading and error states
    if (isLoading && snippets.length === 0) {
        return <div className="text-center p-4 text-gray-500">Loading snippets...</div>;
    }

    if (error) {
        return (
            <div className="text-center p-4 text-gray-500">
                <p>Error: {error}</p>
                <button
                    onClick={onRefresh}
                    className="mt-4 px-4 py-2 bg-sky-500 text-white rounded hover:bg-sky-600"
                >
                    Try Again
                </button>
            </div>
        );
    }

    return (
        <div className="p-4 bg-white shadow-md rounded-lg">
            <div className="flex justify-between items-center mb-6 border-b pb-3">
                <h2 className="text-3xl font-semibold text-gray-800">
                    Available Snippets
                </h2>

                {/* Refresh Button */}
                <button
                    onClick={onRefresh}
                    title="Refresh Snippets"
                    disabled={isLoading}
                    className="p-2 rounded-full hover:bg-gray200 transition-colors cursor-pointer disabled:cursor-wait"
                >
                    {isLoading ? (
                        <svg className="animate-spin h-6 w-6 text-gray-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                    ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="size-6">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99"/>
                        </svg>
                    )}
                </button>
            </div>

            {isLoading && snippets.length > 0 && (
                <p className="text-center text-sky-600 py-2">Refreshing snippets...</p>
            )}

            {snippets.length === 0 && !isLoading && !error ? (
                <p className="text-gray-600 text-center">No snippets found.</p>
            ) : (
                <ul className="space-y-6">
                    {snippets.map(snippet => (
                        <li
                            key={snippet.id}
                            className="flex items-stretch overflow-hidden border border-gray-200 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 ease-in-out bg-white"
                        >
                            <div className="flex-grow p-4 sm:p-6">

                                {/* Display Title */}
                                <h3 className="text-xl sm:text-2xl font-medium text-sky-700 mb-2">{snippet.title}</h3>

                                {/* Display Content */}
                                <pre className="text-gray-700 mt-1 whitespace-pre-wrap bg-gray-100 p-3 rounded-md text-sm font-mono overflow-x-auto max-h-32">
                                    {snippet.content}
                                </pre>

                                {/* Display Creation & Modified Dates */}
                                <div className="mt-3 text-xs text-gray-500 space-y-1">
                                    {snippet.creationDate && (
                                        <p>Created: {new Date(snippet.creationDate).toLocaleString()}</p>
                                    )}
                                    {snippet.lastModifiedDate && (
                                        <p>Last Modified: {new Date(snippet.lastModifiedDate).toLocaleString()}</p>
                                    )}
                                </div>

                                {/* Display Tags */}
                                {snippet.tags && snippet.tags.length > 0 && (
                                    <div className="mt-4 pt-2 border-t border-gray-200">
                                        <span className="text-sm font-medium text-gray-600">Tags: </span>
                                        {snippet.tags.map(tag => (
                                            <span
                                            key={tag.id}
                                            className="inline-block bg-sky-100 text-sky-800 text-xs front-semibold mr-2 px-2.5 py-1 rounded-full"
                                            >
                                                {tag.name}
                                            </span>
                                        ))}
                                    </div>
                                )}
                            </div>

                            <Link
                                to={`/snippets/${snippet.id}`}
                                className="group flex-shrink-0 w-16 sm:w-20 bg-slate-50 hover:bg-slate-100 flex items-center justify-center transition-colors duration-150 ease-in-out border-l border-gray-200"
                                aria-label={`View details for ${snippet.title}`}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2.5} stroke="currentColor" className="w-6 h-6 text-slate-400 group-hover:text-sky-600 transition-colors duration-150 ease-in-out">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" />
                                </svg>
                            </Link>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default SnippetList;
