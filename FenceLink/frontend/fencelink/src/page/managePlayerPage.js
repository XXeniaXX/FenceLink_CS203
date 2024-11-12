import React, { useState, useEffect } from 'react';
import AdminNavBar from '../components/AdminNavBar'; 
import './login.css'; 
import { useNavigate } from 'react-router-dom';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    TextField,
    Button,
    Snackbar
} from '@mui/material';

const ManagePlayerPage = () => {
    const navigate = useNavigate();
    const [players, setPlayers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [showSnackbar, setShowSnackbar] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Fetch all players
        const fetchPlayers = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/players/all');
                if (response.ok) {
                    const data = await response.json();
                    setPlayers(data);
                } else {
                    console.error('Failed to fetch players.');
                }
            } catch (error) {
                console.error('Error fetching players:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchPlayers();
    }, []);

    // Handle input changes for search
    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
    };

    // Filtered players based on search term
    const filteredPlayers = players.filter((player) =>
        player.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        player.id.toString().includes(searchTerm)
    );

    // Delete player
    const handleDelete = async (player) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this player?');
        if (!confirmDelete) return;
    
        try {
            const token = localStorage.getItem('jwtToken');
            let response;
            
            // Check if the player has an associated user
            if (player.user && player.user.id) {
                // Delete both user and player
                response = await fetch(`http://localhost:8080/api/users/${player.user.id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            } else {
                // Only delete the player
                response = await fetch(`http://localhost:8080/api/players/${player.id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            }
    
            if (response.ok) {
                setPlayers(players.filter(p => p.id !== player.id));
                setSnackbarMessage('Player deleted successfully!');
                setShowSnackbar(true);
            } else {
                const errorText = await response.text();
                console.error('Error deleting player:', errorText);
                setSnackbarMessage(`Error deleting player: ${errorText}`);
                setShowSnackbar(true);
            }
        } catch (error) {
            console.error('Error deleting player:', error);
            setSnackbarMessage('Error deleting player.');
            setShowSnackbar(true);
        }
    };

    return (
        <div>
            <AdminNavBar />
            <div style={{ padding: '20px' }}>
                <h1>Manage Players</h1>
                <TextField
                    label="Search by ID or Name"
                    variant="outlined"
                    value={searchTerm}
                    onChange={handleSearchChange}
                    fullWidth
                    style={{ marginBottom: '20px' }}
                />
                {loading ? (
                    <p>Loading players...</p>
                ) : (
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>ID</TableCell>
                                    <TableCell>Name</TableCell>
                                    <TableCell>Gender</TableCell>
                                    <TableCell>Country</TableCell>
                                    <TableCell>Points</TableCell>
                                    <TableCell>Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {filteredPlayers.map((player) => (
                                    <TableRow key={player.id}>
                                        <TableCell>{player.id}</TableCell>
                                        <TableCell>{player.name}</TableCell>
                                        <TableCell>{player.gender}</TableCell>
                                        <TableCell>{player.country}</TableCell>
                                        <TableCell>{player.points}</TableCell>
                                        <TableCell>
                                            <Button
                                                variant="contained"
                                                color="secondary"
                                                onClick={() => handleDelete(player)}
                                            >
                                                Delete
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}

                {/* Snackbar for Feedback */}
                <Snackbar
                    open={showSnackbar}
                    autoHideDuration={4000}
                    onClose={() => setShowSnackbar(false)}
                    message={snackbarMessage}
                />
            </div>
        </div>
    );
};

export default ManagePlayerPage;




