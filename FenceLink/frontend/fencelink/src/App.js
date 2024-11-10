import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom'
import Login from './page/login'
import Registration from './page/registration';
import OtpCheck from './page/otpcheck';
import Main from './page/mainpage';
import ForgotPassword from './page/forgotpassword';
import PlayerInfo from './page/playerinfo';
import ProfilePage from './page/profilepage';
import RankingPage from './page/RankingPage';
import TournamentPage from './page/tournamentPage';
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
          <Route path="/playerinfo" element={<PlayerInfo />} />
          <Route path="/updateplayer/:id" element={<PlayerInfo />} />
          <Route path="/forgotpassword" element={<ForgotPassword />} />
          <Route path="/profilepage" element={<ProfilePage />} />

          <Route path="/register" element={<Registration />} />
          <Route path="/otpcheck" element={<OtpCheck />} />
          <Route path="/mainpage" element={<Main />} />
          <Route path="/tournament" element={<TournamentPage />} />
          {/* Fallback route to redirect unknown paths */}
          <Route path="*" element={<Navigate to="/" />} />
          <Route path="/ranking" element={<RankingPage />} />
        </Routes>
      </div>
    </BrowserRouter>
    
  );
}

export default App