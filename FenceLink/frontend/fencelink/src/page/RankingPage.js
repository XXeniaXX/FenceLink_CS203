import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './RankingPage.css';
import Navbar from '../components/Navbar';

const RankingPage = () => {
    const playersPerPage = 20;
    const [players, setPlayers] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);

    useEffect(() => {
        const fetchLeaderboard = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/leaderboard/top', {
                    params: { page: currentPage }
                });
                setPlayers(response.data.content || response.data); // Log players after setting
                console.log(response.data.content || response.data); // Debugging line
            } catch (error) {
                console.error("Error fetching leaderboard data:", error);
            }
        };
        fetchLeaderboard();
    }, [currentPage]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    return (
        <div className="leaderboard">
            <Navbar />
            <h1>Leaderboard</h1>
            <div className="leaderboard-container">
                {players.length > 0 ? players.map((player, index) => (
                    <div key={player.id} className="leaderboard-row">
                        <span className="rank">{currentPage * playersPerPage + index + 1}</span>
                        <span className="id">ID: {player.id}</span>
                        <span className="name">Name: {player.name}</span>
                        <span className="gender">Gender: {player.gender}</span>
                        <span className="country">Country: {player.country}</span>
                        <span className="points">Points: {player.points}</span>
                    </div>
                )) : (
                    <div className="no-data">No data available</div>
                )}
            </div>


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
        </div>
    );
};

export default RankingPage;
