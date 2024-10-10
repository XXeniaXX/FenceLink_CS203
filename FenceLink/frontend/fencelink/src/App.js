import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom'
import Home from './page/home'
import Registration from './page/registration';
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
          <Route path="/" element={<Home />} />
          {/* Existing routes */}
          <Route path="/home" element={<Home />} />

          <Route path="/register" element={<Registration />} />
          {/* Fallback route to redirect unknown paths */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App