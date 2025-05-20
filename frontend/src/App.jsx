// frontend/src/App.jsx

function App() {
  return (
    // Main container div, styled with Tailwind CSS
    <div className="min-h-screen bg-slate-100 flex flex-col items-center justify-center p-6">
      
      {/* Header Section */}
      <header className="mb-8">
        <h1 className="text-5xl font-extrabold text-sky-600">
          Documentation Snippet Manager
        </h1>
      </header>

      {/* Main Content Area (Placeholder) */}
      <main className="w-full max-w-4xl p-8 bg-white shadow-xl rounded-lg">
        <p className="text-xl text-gray-700">
          Welcome!
        </p>
        <p className="mt-4 text-gray-600">
          Snippets and tags go here.
        </p>
        {/* Example of a styled button */}
        <button className="mt-6 px-5 py-2.5 bg-sky-500 text-white font-medium rounded-md shadow-sm hover:bg-sky-600 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-offset-2">
          Placeholder Button
        </button>
      </main>

      {/* Footer Section (Placeholder) */}
      <footer className="mt-12 text-center text-gray-500 text-sm">
        <p>&copy; {new Date().getFullYear()} Sever Entertainment</p>
      </footer>

    </div>
  )
}

export default App
