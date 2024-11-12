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
    useEffect(() => {
        const fetchLeaderboard = async () => {
            try {
                const response = await axios.get('http://13.213.45.2:8080/api/leaderboard/top', {
                    params: {
                        page: currentPage,
                        gender: gender || undefined, // Only send if gender is selected
                        country: country || undefined // Only send if country is selected
                    }
                });
                setPlayers(response.data.content || response.data);
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
                const response = await axios.get('http://13.213.45.2:8080/api/players/countries');
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

    const handleRowClick = (player) => {
        setSelectedPlayer(player);
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
        setSelectedPlayer(null);
    };

    return (
        <div className="leaderboard">
            {userRole === 'admin' ? <AdminNavBar /> : <Navbar />}
            <h1>World Leaderboard</h1>
    
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
            {players.length > playersPerPage && (
                <div className="pagination">
                    {[...Array(5)].map((_, i) => (
                        <button
                            key={i}
                            className={`pagination-button ${i === currentPage ? 'active' : ''}`}
                            onClick={() => handlePageChange(i)}
                        >
                            {i + 1}
                        </button>
                    ))}
                </div>
            )}

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

