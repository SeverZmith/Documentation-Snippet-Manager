// frontend/src/App.jsx
import React, { useState, useEffect } from 'react';
import { Routes, Route, Outlet, Link } from 'react-router-dom';
import SnippetList from "./components/SnippetList";
import CreateSnippetForm from './components/CreateSnippetForm';
import { getAllSnippets } from './services/snippetApiService.js';
import SnippetDetailsPage from './components/SnippetDetailsPage.jsx';

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
            <header className="mb-8 text-center w-full max-w-4xl">
                <Link to="/" className="text-5xl font-extrabold text-sky-600 hover:text-sky-700 no-underline">
                    Documentation Snippet Manager
                </Link>
                <nav className="mt-4">
                    <Link to="/" className="mr-4 text-sky-500 hover:text-sky-600">Home</Link>
                    <Link to="/snippets/new" className="text-sky-500 hover:text-sky-600">Create Snippet</Link>
                </nav>
            </header>

            {/* Main Content Area */}
            <main className="w-full max-w-4xl">
                <Routes>

                    {/* Snippet List */}
                    <Route path="/" element={
                        <SnippetList
                            snippets={snippets}
                            isLoading={isLoading}
                            error={error}
                            onRefresh={fetchAllSnippets}
                        />
                    } />

                    {/* Create Snippet Form */}
                    <Route path="/snippets/new" element={
                        <CreateSnippetForm onSnippetCreated={handleSnippetCreated} />
                    } />

                    {/* Snippet Details Page */}
                    <Route path="/snippets/:snippetId" element={<SnippetDetailsPage onSnippetUpdated={fetchAllSnippets} />} />

                </Routes>
            </main>

            {/* Footer Section */}
            <footer className="mt-12 text-center text-gray-500 text-sm">
                <p>&copy; {new Date().getFullYear()} Sever Entertainment</p>
            </footer>

        </div>
    )
}

export default App
