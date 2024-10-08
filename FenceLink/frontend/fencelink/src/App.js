import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Home from './page/home'
import './App.css'
import { useEffect, useState } from 'react'

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [email, setEmail] = useState('')

  return (
    <div className="App">
       {/* Render the Home component */}
       <Home />
    </div>
  )
}

export default App