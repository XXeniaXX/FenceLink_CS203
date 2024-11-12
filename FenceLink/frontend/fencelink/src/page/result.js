import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Navbar from '../components/Navbar';
import PlayerProfileDialog from '../components/PlayerProfileDialog'; // Assuming you have this component
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Typography,
    Collapse,
    Box,
    Tooltip,
    Button
} from '@mui/material';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import { useNavigate } from 'react-router-dom';
import AdminNavBar from '../components/AdminNavBar';

const ResultPage = () => {
    const [tournaments, setTournaments] = useState([]);
    const [error, setError] = useState(null);
    const [sortOrder, setSortOrder] = useState('asc');
    const [expandedRow, setExpandedRow] = useState(null);
    const [winners, setWinners] = useState({});
    const [openPlayerDialog, setOpenPlayerDialog] = useState(false);
    const [selectedPlayer, setSelectedPlayer] = useState(null);
    const [userRole, setUserRole] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        const fetchCompletedTournaments = async () => {
            try {
                const response = await axios.get('http://13.213.45.2:8080/api/tournaments');
                const sortedData = response.data.sort((a, b) => new Date(a.endDate) - new Date(b.endDate));
                setTournaments(sortedData);
            } catch (err) {
                setError("Failed to fetch tournament results");
                console.error(err);
            }
        };
        fetchCompletedTournaments();
    }, []);

    const handleRowClick = (id) => {
        setExpandedRow(expandedRow === id ? null : id);
        if (expandedRow !== id) {
            fetchWinners(id);
        }
    };

    const fetchWinners = async (tournamentId) => {
        try {
            const response = await axios.get(`http://13.213.45.2:8080/api/matches/${tournamentId}/winners`);
            const playerIds = Object.values(response.data);

            const playerNamesResponse = await Promise.all(
                playerIds.map(async (id) => {
                    try {
                        const playerResponse = await axios.get(`http://13.213.45.2:8080/api/players/${id}`);
                        return { id, name: playerResponse.data.name, ...playerResponse.data };
                    } catch (error) {
                        console.error(`Error fetching player with ID ${id}:`, error);
                        return { id, name: `Player ${id}` };
                    }
                })
            );

            const winnersWithNames = {
                gold: playerNamesResponse.find(player => player.id === response.data.Gold) || { name: 'N/A' },
                silver: playerNamesResponse.find(player => player.id === response.data.Silver) || { name: 'N/A' },
                bronze: playerNamesResponse.find(player => player.id === response.data.Bronze) || { name: 'N/A' },
            };

            setWinners((prevWinners) => ({
                ...prevWinners,
                [tournamentId]: winnersWithNames
            }));
        } catch (error) {
            console.error('Error fetching winners:', error);
            setError('Could not fetch winners for this tournament.');
        }
    };

    const checkStatus = (endDate) => {
        const currentDate = new Date();
        const tournamentEndDate = new Date(endDate);
        return tournamentEndDate < currentDate ? 'Completed' : 'Ongoing';
    };

    const getStatusColor = (status) => {
        return status === 'Completed' ? 'green' : 'blue';
    };

    const handleSort = () => {
        const sortedTournaments = [...tournaments].sort((a, b) => {
            const dateA = new Date(a.endDate);
            const dateB = new Date(b.endDate);
            return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
        });
        setTournaments(sortedTournaments);
        setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    };

    const handleViewMatches = (tournamentId) => {
        navigate(`/match/${tournamentId}`);
    };

    const handlePlayerClick = (player) => {
        setSelectedPlayer(player);
        setOpenPlayerDialog(true);
    };

    const handleClosePlayerDialog = () => {
        setOpenPlayerDialog(false);
        setSelectedPlayer(null);
    };

    useEffect(() => {
        const role = localStorage.getItem('userRole');
        setUserRole(role);
    }, []);


    return (
        <div>
            {userRole === 'admin' ? <AdminNavBar /> : <Navbar />}
            <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
                <h1 style={{ color: '#1C1E53', textAlign: 'center', fontWeight: '800' }}>
                    Tournament Results
                </h1>

                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: '#E0EBFF' }}>
                                <TableCell>Tournament</TableCell>
                                <TableCell>Description</TableCell>
                                <TableCell>Type</TableCell>
                                <TableCell
                                    style={{ cursor: 'pointer' }}
                                    onClick={handleSort}
                                >
                                    End Date {sortOrder === 'asc' ? '↑' : '↓'}
                                </TableCell>
                                <TableCell>Status</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {tournaments.map((tournament) => (
                                <React.Fragment key={tournament.id}>
                                    <TableRow
                                        sx={{
                                            '&:hover': {
                                                backgroundColor: '#d0d0d0',
                                            },
                                        }}
                                        onClick={() => handleRowClick(tournament.id)}
                                    >
                                        <TableCell>{tournament.name}</TableCell>
                                        <TableCell>{tournament.description}</TableCell>
                                        <TableCell>{tournament.tournamentType}</TableCell>
                                        <TableCell>{tournament.endDate}</TableCell>
                                        <TableCell style={{ color: getStatusColor(checkStatus(tournament.endDate)) }}>
                                            {checkStatus(tournament.endDate)}
                                        </TableCell>
                                    </TableRow>
                                    {expandedRow === tournament.id && (
                                        <TableRow>
                                            <TableCell colSpan={5}>
                                                <Collapse in={expandedRow === tournament.id} timeout="auto" unmountOnExit>
                                                    <Box margin={1} sx={{ backgroundColor: '#E0EBFF', padding: '10px' }}>
                                                        <Typography variant="h6" gutterBottom>
                                                            Winners:
                                                        </Typography>
                                                        {winners[tournament.id] ? (
                                                            <div style={{ display: 'flex', justifyContent: 'space-around', marginTop: '10px' }}>
                                                                {['gold', 'silver', 'bronze'].map((medal) => (
                                                                    <div key={medal} style={{ textAlign: 'center' }}>
                                                                        <Tooltip title={medal.charAt(0).toUpperCase() + medal.slice(1)} placement="top">
                                                                            <EmojiEventsIcon
                                                                                style={{
                                                                                    color: medal === 'gold' ? 'gold' : medal === 'silver' ? 'silver' : '#cd7f32',
                                                                                    fontSize: '40px'
                                                                                }}
                                                                            />
                                                                        </Tooltip>
                                                                        <Typography
                                                                            variant="h6"
                                                                            style={{ marginTop: '5px', cursor: 'pointer' }}
                                                                            onClick={() => handlePlayerClick(winners[tournament.id][medal])}
                                                                        >
                                                                            {winners[tournament.id][medal].name || 'N/A'}
                                                                        </Typography>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        ) : (
                                                            <Typography variant="body2">
                                                                No winners for this match yet...
                                                            </Typography>
                                                        )}
                                                        <div style={{ marginTop: '20px', textAlign: 'center' }}>
                                                            <Button
                                                                variant="contained"
                                                                color="primary"
                                                                onClick={() => handleViewMatches(tournament.id)}
                                                            >
                                                                View Matches
                                                            </Button>
                                                        </div>
                                                    </Box>
                                                </Collapse>
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </React.Fragment>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>

                {/* Player Profile Dialog */}
                <PlayerProfileDialog
                    open={openPlayerDialog}
                    onClose={handleClosePlayerDialog}
                    player={selectedPlayer}
                />
            </div>
        </div>
    );
};

export default ResultPage;









