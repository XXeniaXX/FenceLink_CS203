import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './RankingPage.css';
import Navbar from '../components/Navbar';

const RankingPage = () => {
    const playersPerPage = 20;
    const [players, setPlayers] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [gender, setGender] = useState(''); // Filter by gender
    const [country, setCountry] = useState(''); // Filter by country

    useEffect(() => {
        const fetchLeaderboard = async () => {
            try {
                const response = await axios.get('/api/leaderboard/top', {
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

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    return (
        <div className="leaderboard">
            <Navbar />
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
                    <option value="USA">USA</option>
                    <option value="Canada">Canada</option>
                    <option value="UK">UK</option>
                    {/* Add more countries as needed */}
                </select>
            </div>
    
            <div className="leaderboard-container">
                <table className="leaderboard-table">
                    <thead>
                        <tr>
                        <th className="rank-header">Rank</th>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Gender</th>
                            <th>Country</th>
                            <th>Points</th>
                        </tr>
                    </thead>
                    <tbody>
                        {players.length > 0 ? players.map((player, index) => (
                            <tr key={player.id} className="leaderboard-row">
                                <td className="rank-cell">{currentPage * playersPerPage + index + 1}</td>
                                <td>{player.id}</td>
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
        </div>
    );    
};

export default RankingPage;
