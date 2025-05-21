// frontend/src/App.jsx
import React, { useState, useEffect } from 'react';
import SnippetList from "./components/SnippetList";
import CreateSnippetForm from './components/CreateSnippetForm';
import { getAllSnippets } from './services/snippetApiService.js';

function App() {
    const [snippets, setSnippets] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchAllSnippets = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const data = await getAllSnippets();
            setSnippets(data);
        } catch (err) {
            setError(err.message || "Failed to fetch snippets.");
            setSnippets([]);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchAllSnippets();
    }, []);

    const handleSnippetCreated = () => {
        fetchAllSnippets();
    }

    return (
        <div className="min-h-screen bg-slate-100 flex flex-col items-center p-6">
        
            {/* Header Section */}
            <header className="mb-8 text-center">
                <h1 className="text-5xl font-extrabold text-sky-600">
                    Documentation Snippet Manager
                </h1>
            </header>

            {/* Main Content Area */}
            <main className="w-full max-w-4xl">

                {/* Create Snippet Form */}
                <CreateSnippetForm onSnippetCreated={handleSnippetCreated} />

                {/* Snippet List */}
                <div className="mt-12">
                    <SnippetList
                        snippets={snippets}
                        isLoading={isLoading}
                        error={error}
                        onRefresh={fetchAllSnippets}
                    />
                </div>
            </main>

            {/* Footer Section */}
            <footer className="mt-12 text-center text-gray-500 text-sm">
                <p>&copy; {new Date().getFullYear()} Sever Entertainment</p>
            </footer>

        </div>
    )
}

export default App
