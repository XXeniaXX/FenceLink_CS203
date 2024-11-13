import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import axios from 'axios';
import './dialoguebox.css';
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

const UpcomingTournaments = ({ playerId }) => {
    const [tournaments, setTournaments] = useState([]);
    const [error, setError] = useState(null);
    const [filterLocation, setFilterLocation] = useState('All');
    const [selectedTournament, setSelectedTournament] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);

    useEffect(() => {
        const fetchTournaments = async () => {

            const storedPlayerId = localStorage.getItem('playerId');
            console.log(storedPlayerId);

            try {
                const response = await axios.get(`http://47.129.36.1:8080/api/players/${storedPlayerId}/upcoming-registered-tournaments`);
                setTournaments(response.data);
            } catch (err) {
                setError("Failed to fetch tournaments");
                console.error(err);
            }
        };
        
        fetchTournaments();
    }, []);

    // Filter tournaments by round
    const filteredTournaments = filterLocation === 'All'
        ? tournaments
        : tournaments.filter((tournament) => tournament.location === filterLocation);

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
            <Navbar />
            <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
                <h1 style={{ color: '#1C1E53', textAlign: 'center', fontWeight: '800' }}>
                    Upcoming Tournaments
                </h1>
                
                {error && (
                    <Typography color="error" align="center" style={{ marginBottom: '20px' }}>
                        {error}
                    </Typography>
                )}

                <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '20px' }}>
                    <TextField
                        select
                        label="Filter by Location"
                        value={filterLocation}
                        onChange={(e) => setFilterLocation(e.target.value)}
                        variant="outlined"
                        size="small"
                        style={{ width: 200 }}
                    >
                        <MenuItem value="All">All</MenuItem>
                        {[...new Set(tournaments.map((tournament) => tournament.location))]
                            .sort()
                            .map((location) => (
                                <MenuItem key={location} value={location}>
                                    {location}
                                </MenuItem>
                            ))}
                    </TextField>
                </div>

                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: '#E0EBFF' }}>
                                <TableCell>Start Date</TableCell>
                                <TableCell>End Date</TableCell>
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
                                    <TableCell>{tournament.name}</TableCell>
                                    <TableCell>{tournament.description}</TableCell>
                                    <TableCell>{tournament.location}</TableCell>
                                </TableRow>
                                ))}
                            </TableBody>
                    </Table>
                </TableContainer>

                {/* Tournament Detail Dialog */}
                <Dialog open={openDialog} onClose={handleCloseDialog} classes={{ paper: 'dialog-container' }}>
                    <DialogTitle className="dialog-title">{selectedTournament?.name}</DialogTitle>
                    <DialogContent className="dialog-content">
                        <DialogContentText className="dialog-content-text">
                            <strong>Description:</strong> {selectedTournament?.description}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Location:</strong> {selectedTournament?.location}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Start Date:</strong> {selectedTournament?.startDate}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>End Date:</strong> {selectedTournament?.endDate}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Tournament Type:</strong> {selectedTournament?.tournamentType}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Weapon Type:</strong> {selectedTournament?.weaponType}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Age Group:</strong> {selectedTournament?.ageGroup}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Vacancy:</strong> {selectedTournament?.vacancy}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Gender:</strong> {selectedTournament?.genderType}
                        </DialogContentText>
                        <DialogContentText className="dialog-content-text">
                            <strong>Registration Date:</strong> {selectedTournament?.registrationDate}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions className="dialog-actions">
                        <Button onClick={handleCloseDialog} className="dialog-close-button">
                            Close
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        </div>
    );
};

export default UpcomingTournaments;
