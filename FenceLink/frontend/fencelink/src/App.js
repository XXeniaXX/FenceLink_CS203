import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom'
import Login from './page/login'
import Registration from './page/registration';
import OtpCheck from './page/otpcheck';
import Main from './page/mainpage';
import ForgotPassword from './page/forgotpassword';
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
          <Route path="/" element={<Login />} />
          {/* Existing routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/forgotpassword" element={<ForgotPassword />} />

          <Route path="/register" element={<Registration />} />
          <Route path="/otpcheck" element={<OtpCheck />} />
          <Route path="/mainpage" element={<Main />} />
          {/* Fallback route to redirect unknown paths */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </BrowserRouter>
    
  );
}

export default App