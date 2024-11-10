import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom';
import Login from './page/login';
import Registration from './page/registration';
import OtpCheck from './page/otpcheck';
import Main from './page/mainpage';
import ForgotPassword from './page/forgotpassword';
import PlayerInfo from './page/playerinfo';
import ProfilePage from './page/profilepage';
import EditProfile from './page/editprofile';
import PastTournaments from './page/pasttournaments';
import UpcomingTournaments from './page/upcomingtournaments';
import RankingPage from './page/RankingPage';
import TournamentPage from './page/tournamentPage';
import './App.css';
import { useEffect, useState } from 'react';
import MatchAdmin from './page/matchAdmin';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import ProtectedRoute from './page/protectedroute';
import UnauthorizedPage from './page/unauthorisedpage';

const theme = createTheme();

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <div className="App">
          <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/login" element={<Login />} />
            <Route path="/playerinfo" element={<PlayerInfo />} />
            <Route path="/updateplayer/:id" element={<PlayerInfo />} />
            <Route path="/forgotpassword" element={<ForgotPassword />} />
            <Route path="/editprofile" element={<EditProfile />} />
            <Route path="/pasttournaments" element={<PastTournaments />} />
            <Route path="/upcomingtournaments" element={<UpcomingTournaments />} />
            <Route path="/register" element={<Registration />} />
            <Route path="/otpcheck" element={<OtpCheck />} />
            <Route path="/mainpage" element={<Main />} />
            <Route path="/tournament" element={<TournamentPage />} />
            <Route path="/ranking" element={<RankingPage />} />
            <Route path="/unauthorisedpage" element={<UnauthorizedPage />} />
            
            {/* Admin-only route */}
            <Route element={<ProtectedRoute allowedRole="admin" />}>
              <Route path="/matchAdmin" element={<MatchAdmin />} />
            </Route>

            <Route path="*" element={<Navigate to="/" />} />
          
          </Routes>
        </div>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
