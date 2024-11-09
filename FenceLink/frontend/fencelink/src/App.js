import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom'
import LandingPage from './page/LandingPage';
import Registration from './page/registration';
import RankingPage from './page/RankingPage';
import HomePage from './page/HomePage'; // Import HomePage
import './App.css'
import { useEffect, useState } from 'react'

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [email, setEmail] = useState('')

  return (
    <BrowserRouter>
      <div className="App">
        <Routes>
          {/* Default route for the base URL */}
          <Route path="/" element={<HomePage />} />
          {/* Existing routes */}
          <Route path="/LandingPage" element={<LandingPage />} />

          <Route path="/register" element={<Registration />} />
          {/* Fallback route to redirect unknown paths */}
          <Route path="*" element={<Navigate to="/" />} />
          <Route path="/ranking" element={<RankingPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App