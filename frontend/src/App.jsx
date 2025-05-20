// frontend/src/App.jsx
import SnippetList from "./components/SnippetList";

function App() {
  return (
    // Main container div
    <div className="min-h-screen bg-slate-100 flex flex-col items-center justify-center p-6">
      
      {/* Header Section */}
      <header className="mb-8">
        <h1 className="text-5xl font-extrabold text-sky-600">
          Documentation Snippet Manager
        </h1>
      </header>

      {/* Main Content Area (Placeholder) */}
      <main className="w-full max-w-4xl">
        <SnippetList />
      </main>

      {/* Footer Section (Placeholder) */}
      <footer className="mt-12 text-center text-gray-500 text-sm">
        <p>&copy; {new Date().getFullYear()} Sever Entertainment</p>
      </footer>

    </div>
  )
}

export default App
