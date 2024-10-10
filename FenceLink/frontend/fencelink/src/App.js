import { BrowserRouter, Route, Routes } from 'react-router-dom'
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
          <Route path="/home" element={<Home />} />

          <Route path="/register" element={<Registration />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App