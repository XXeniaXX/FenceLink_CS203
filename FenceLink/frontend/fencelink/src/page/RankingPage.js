import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './RankingPage.css';
import Navbar from '../components/Navbar';
import AdminNavBar from '../components/AdminNavBar';
import PlayerProfileDialog from '../components/PlayerProfileDialog'; // Adjust the import path if necessary

const RankingPage = () => {
    const playersPerPage = 20;
    const [players, setPlayers] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [gender, setGender] = useState(''); // Filter by gender
    const [country, setCountry] = useState(''); // Filter by country
    const [countries, setCountries] = useState([]); // List of countries
    const [userRole, setUserRole] = useState('');
    const [selectedPlayer, setSelectedPlayer] = useState(null); // State to manage selected player
    const [dialogOpen, setDialogOpen] = useState(false); // State to manage dialog open status

    // Fetch leaderboard data when filters or page change
    const [totalPages, setTotalPages] = useState(0); // Total pages available

    useEffect(() => {
        const fetchLeaderboard = async () => {
            try {
                const response = await axios.get('/api/leaderboard/top', {
                    params: {
                        page: currentPage,
                        gender: gender || undefined,
                        country: country || undefined
                    }
                });
                setPlayers(response.data.players); // Players data
                setTotalPages(response.data.totalPages); // Set total pages
            } catch (error) {
                console.error("Error fetching leaderboard data:", error);
            }
        };
        fetchLeaderboard();
    }, [currentPage, gender, country]);

    // Fetch list of countries on component mount
    useEffect(() => {
        const fetchCountries = async () => {
            try {
                const response = await axios.get('/api/players/countries');
                setCountries(response.data);
            } catch (error) {
                console.error("Error fetching countries:", error);
            }
        };
        fetchCountries();
    }, []);

    useEffect(() => {
        const role = localStorage.getItem('userRole');
        setUserRole(role);
    }, []);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleRowClick = async (player) => {
        try {
            // Assuming you have an endpoint that fetches detailed data for a player by ID
            const response = await axios.get(`/api/players/${player.id}`);
            const detailedPlayer = response.data;
            setSelectedPlayer(detailedPlayer); // Set detailed data as the selected player
        } catch (error) {
            console.error('Error fetching detailed player data:', error);
            setSelectedPlayer(player); // Fallback to basic player data
        }
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
        setSelectedPlayer(null);
    };

    return (
        <div className="leaderboard">
            {userRole === 'admin' ? <AdminNavBar /> : <Navbar />}
            <h1 className='world-leaderboard'>World Leaderboard</h1>
    
            {/* Filters */}
            <div className="filters">
                <select value={gender} onChange={(e) => setGender(e.target.value)}>
                    <option value="">All Genders</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                </select>
    
                <select value={country} onChange={(e) => setCountry(e.target.value)}>
                    <option value="">All Countries</option>
                    {countries.map((c) => (
                        <option key={c} value={c}>
                            {c}
                        </option>
                    ))}
                </select>
            </div>
    
            <div className="leaderboard-container">
                <table className="leaderboard-table">
                    <thead>
                        <tr>
                            <th className="rank-header">Rank</th>
                            <th className="id-header">ID</th>
                            <th>Name</th>
                            <th>Gender</th>
                            <th>Country</th>
                            <th>Points</th>
                        </tr>
                    </thead>
                    <tbody>
                        {players.length > 0 ? players.map((player, index) => (
                            <tr key={player.id} className="leaderboard-row" onClick={() => handleRowClick(player)}>
                                <td className="rank-cell">{currentPage * playersPerPage + index + 1}</td>
                                <td className="id-cell">{player.id}</td>
                                <td>{player.name}</td>
                                <td>{player.gender}</td>
                                <td>{player.country}</td>
                                <td>{player.points}</td>
                            </tr>
                        )) : (
                            <tr>
                                <td colSpan="6" className="no-data">No data available</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
    
            {/* Pagination */}
            <div className="pagination">
                {Array.from({ length: totalPages }, (_, i) => (
                    <button
                        key={i}
                        className={`pagination-button ${i === currentPage ? 'active' : ''}`}
                        onClick={() => handlePageChange(i)}
                    >
                        {i + 1}
                    </button>
                ))}
            </div>

            {/* Player Profile Dialog */}
            {selectedPlayer && (
                <PlayerProfileDialog
                    open={dialogOpen}
                    onClose={handleCloseDialog}
                    player={selectedPlayer}
                />
            )}
        </div>
    );    
};

export default RankingPage;

