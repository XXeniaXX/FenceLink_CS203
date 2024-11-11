import React, { useState, useEffect } from 'react';
import { signOut, getCurrentUser } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file
import rankings from './assets/rankings.png';
import fencingcross from './assets/fencingcross.png';
import results from './assets/results.png';
import axios from 'axios';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Typography,
    TextField,
    MenuItem,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Button
} from '@mui/material';

const AdminHomePage = () => {
    const navigate = useNavigate();

    const [userName, setUserName] = useState('');
    const [loading, setLoading] = useState(true);

    const [dropdownOpen, setDropdownOpen] = useState(false);
    const storedUserName = localStorage.getItem('userName');

    const toggleDropdown = () => {
        setDropdownOpen(!dropdownOpen);
    };

    const handleSignOut = async () => {
        try {
        await signOut({ global: true });
        console.log('User signed out successfully');

        localStorage.removeItem('jwtToken');
        sessionStorage.clear();
        navigate('/login');
        } catch (error) {
        console.log('Error signing out: ', error);
        }
    };

    useEffect(() => {
        // Function to fetch the user's name from the backend
        const storedUserId = localStorage.getItem('userId');

        if (!storedUserId) {
            alert('User ID not found. Please log in again.');
            return;
        }

        const fetchUserName = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/users/${storedUserId}`);
            if (!response.ok) {
                throw new Error('Failed to fetch user data');
            }

            const data = await response.json();
            setUserName(data.userName); 
            localStorage.setItem("userName", data.userName);
        } catch (error) {
            console.error('Error fetching user data:', error);
        } finally {
            setLoading(false); // Stop loading once data is fetched or an error occurs
        }
        };

        fetchUserName();
    }, []);

    const [tournaments, setTournaments] = useState([]);
    const [error, setError] = useState(null);
    const [filterRound, setFilterRound] = useState('All');
    const [selectedTournament, setSelectedTournament] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);

    useEffect(() => {
        const fetchTournaments = async () => {

            const storedPlayerId = localStorage.getItem('playerId');
            console.log(storedPlayerId);

            try {
                const response = await axios.get(`http://localhost:8080/api/tournaments/upcomingtournaments`);
                setTournaments(response.data);
            } catch (err) {
                setError("Failed to fetch tournaments");
                console.error(err);
            }
        };
        
        fetchTournaments();
    }, []);

    // Filter tournaments by round
    const filteredTournaments = filterRound === 'All'
        ? tournaments
        : tournaments.filter((tournament) => tournament.country === parseInt(filterRound));

    // Open dialog with tournament details
    const handleRowClick = (tournament) => {
        setSelectedTournament(tournament);
        setOpenDialog(true);
    };

    // Close dialog
    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedTournament(null);
    };
    
    return (
        <div>
            {/* Render the NavBar component */}
            
            <nav className="nav">
                <img 
                    src="/fencelink.png" 
                    alt="FenceLink Logo" 
                    style={{
                        width: '210px',
                        height: '70px',
                        borderRadius: '50%',
                        objectFit: 'contain'
                    }} 
                />
                <div className="profile-section" onClick={toggleDropdown}>
                    <li className="nav-links">Hi, Admin {storedUserName}</li>
                    <span className={`dropdown-icon ${dropdownOpen ? 'rotate' : ''}`}>▼</span>
                    {dropdownOpen && (
                    <div className="dropdown-menu">
                        <button onClick={handleSignOut} className="dropdown-item">Sign Out</button>
                    </div>
                    )}
                </div>
            </nav>
            
            <div className="mainContainer">
            {/* Buttons */}
            <div className="pageContainer">
                <button className="pageButton" onClick={() => navigate('/tournament')}>
                <img src={fencingcross} alt="fencingcross" className="pagebuttonImage" />
                <p style={{ color: '#1C1E53', fontSize: '15px', fontWeight: '600' }}>Tournament</p>
                </button>
                <button className="pageButton" onClick={() => navigate('/ranking')}>
                <img src={rankings} alt="rankings" className="pagebuttonImage" />
                <p style={{ color: '#1C1E53', fontSize: '15px', fontWeight: '600' }}>Rankings</p>
                </button>
                <button className="pageButton" onClick={() => navigate('/results')}>
                <img src={results} alt="results" className="pagebuttonImage" />
                <p style={{ color: '#1C1E53', fontSize: '15px', fontWeight: '600' }}>Results</p>
                </button>
            </div>

            {/* Divider */}
            <div className="divider"></div>

            {/* Table */}
            <div className="tableContainer">
                <h1 style={{ color: '#1C1E53', textAlign: 'center', fontWeight: '800' }}>
                Upcoming Tournaments
                </h1>
                
                <TableContainer component={Paper} sx={{ width: '1000px' }}>
                <Table>
                    <TableHead>
                    <TableRow sx={{ backgroundColor: '#E0EBFF' }}>
                        <TableCell>Start Date</TableCell>
                        <TableCell>End Date</TableCell>
                        <TableCell>Registration Date</TableCell>
                        <TableCell>Tournament</TableCell>
                        <TableCell>Description</TableCell>
                        <TableCell>Location</TableCell>
                    </TableRow>
                    </TableHead>
                    <TableBody>
                    {filteredTournaments.map((tournament) => (
                        <TableRow 
                        key={tournament.id} 
                        onClick={() => handleRowClick(tournament)} 
                        style={{ cursor: 'pointer' }}
                        sx={{
                            '&:hover': {
                            backgroundColor: '#d0d0d0',
                            },
                        }}
                        >
                        <TableCell>{tournament.startDate}</TableCell>
                        <TableCell>{tournament.endDate}</TableCell>
                        <TableCell>{tournament.registrationDate}</TableCell>
                        <TableCell>{tournament.name}</TableCell>
                        <TableCell>{tournament.description}</TableCell>
                        <TableCell>{tournament.location}</TableCell>
                        </TableRow>
                    ))}
                    </TableBody>
                </Table>
                </TableContainer>
            </div>
            </div>
        </div>
        
    )
}
export default AdminHomePage;