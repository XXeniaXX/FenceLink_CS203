import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Navbar from '../components/Navbar';
import { useNavigate } from 'react-router-dom';
import './tournamentPage.css';

const UserTournamentPage = () => {
  const [tournaments, setTournaments] = useState([]);
  const [filter, setFilter] = useState({
    name: '',
    tournamentType: '',
    genderType: '',
    weaponType: '',
    ageGroup: '',
    tournamentDate: '',
  });
  const [playerId, setPlayerId] = useState(null);
  const [registeredTournaments, setRegisteredTournaments] = useState([]); // New state for registered tournaments
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch the player ID (this could come from a logged-in user context or API)
    setPlayerId(1); // Example player ID (replace with actual playerId logic)

    // Fetch all tournaments
    axios.get('/api/tournaments')
      .then(response => setTournaments(response.data))
      .catch(error => console.error('Error fetching tournaments:', error));

    // Fetch the player's registered tournaments
    if (playerId) {
      axios.get(`/api/players/${playerId}/upcoming-registered-tournaments`)
        .then(response => setRegisteredTournaments(response.data))
        .catch(error => console.error('Error fetching registered tournaments:', error));
    }
  }, [playerId]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter({ ...filter, [name]: value });
  };

  const clearFilters = () => {
    setFilter({
      name: '',
      tournamentType: '',
      genderType: '',
      weaponType: '',
      ageGroup: '',
      tournamentDate: '',
    });
  };

  const filteredTournaments = tournaments.filter((tournament) => {
    const tournamentDate = new Date(tournament.startDate);
    const filterDate = new Date(filter.tournamentDate);

    return (
      (!filter.name || tournament.name.toLowerCase().includes(filter.name.toLowerCase())) &&
      (!filter.tournamentType || tournament.tournamentType === filter.tournamentType) &&
      (!filter.genderType || tournament.genderType === filter.genderType) &&
      (!filter.weaponType || tournament.weaponType === filter.weaponType) &&
      (!filter.ageGroup || tournament.ageGroup === filter.ageGroup) &&
      (!filter.tournamentDate || tournamentDate.toDateString() === filterDate.toDateString())
    );
  });

  const isPlayerRegistered = (tournamentId) => {
    // Check if the player is registered for the tournament
    return registeredTournaments.some(tournament => tournament.id === tournamentId);
  };

  // Register the player for a tournament
  const handleJoinTournament = (e, tournamentId) => {
    e.preventDefault(); // Prevent any default behavior
    e.stopPropagation(); // Stop the event from propagating

    axios.post(`/api/players/${playerId}/register/${tournamentId}`)
      .then(response => {
        // Update the vacancy count and the tournament registration
        setTournaments(prevTournaments =>
          prevTournaments.map(tournament =>
            tournament.id === tournamentId ? { ...tournament, vacancy: tournament.vacancy - 1 } : tournament
          )
        );

        // Update the registered tournaments
        setRegisteredTournaments(prev => [...prev, { id: tournamentId }]);
      })
      .catch(error => console.error('Error joining tournament:', error));
  };

  // Withdraw the player from a tournament
  const handleWithdrawTournament = (e, tournamentId) => {
    e.preventDefault(); // Prevent any default behavior
    e.stopPropagation(); // Stop the event from propagating
  
    axios.delete(`/api/players/${playerId}/withdraw/${tournamentId}`)
      .then(response => {
        // Update the vacancy count and the tournament registration
        setTournaments(prevTournaments =>
          prevTournaments.map(tournament =>
            tournament.id === tournamentId ? { ...tournament, vacancy: tournament.vacancy + 1 } : tournament
          )
        );
  
        // Update the registered tournaments
        setRegisteredTournaments(prev => prev.filter(tournament => tournament.id !== tournamentId));
      })
      .catch(error => console.error('Error withdrawing from tournament:', error));
  };
  

  // Function to handle card click and navigate to MatchAdmin
  const handleCardClick = (tournamentId) => {
    navigate(`/match-admin/${tournamentId}`); // Adjust the path as needed for your routing setup
  };

  return (
    <div className="user-tournament-page">
      <Navbar />

      {/* Filter Section */}
      <div className="filter-section">
        <input
          type="text"
          placeholder="Search by Name"
          className="search-input"
          name="name"
          value={filter.name}
          onChange={handleFilterChange}
        />
        <select
          name="tournamentType"
          value={filter.tournamentType}
          onChange={handleFilterChange}
        >
          <option value="">Select Tournament Type</option>
          <option value="Friendly">Friendly</option>
          <option value="Competitive">Competitive</option>
        </select>
        <select
          name="genderType"
          value={filter.genderType}
          onChange={handleFilterChange}
        >
          <option value="">Select Gender</option>
          <option value="Female">Female</option>
          <option value="Male">Male</option>
          <option value="Mixed">Mixed</option>
        </select>
        <select
          name="weaponType"
          value={filter.weaponType}
          onChange={handleFilterChange}
        >
          <option value="">Select Weapon Type</option>
          <option value="Epee">Epee</option>
          <option value="Foil">Foil</option>
          <option value="Saber">Saber</option>
        </select>
        <select
          name="ageGroup"
          value={filter.ageGroup}
          onChange={handleFilterChange}
        >
          <option value="">Select Age Group</option>
          <option value="Youth">Youth</option>
          <option value="Adult">Adult</option>
        </select>
        <input
          type="date"
          name="tournamentDate"
          value={filter.tournamentDate}
          onChange={handleFilterChange}
        />
        <button className="search-button">Search</button>
        <button className="clear-button" onClick={clearFilters}>Clear Filters</button>
      </div>

      {/* Tournament List */}
      <div className="tournament-list">
        {filteredTournaments.map((tournament, index) => (
          <div key={index} className="tournament-item" onClick={() => handleCardClick(tournament.id)}>
            <h3>{tournament.name}</h3>
            <p><strong>Location:</strong> {tournament.location}</p>
            <p><strong>Description:</strong> {tournament.description}</p>
            <p><strong>Registration Date:</strong> {tournament.registrationDate}</p>
            <p><strong>Tournament Type:</strong> {tournament.tournamentType}</p>
            <p><strong>Age Group:</strong> {tournament.ageGroup}</p>
            <p><strong>Weapon Type:</strong> {tournament.weaponType}</p>
            <p><strong>Gender:</strong> {tournament.genderType}</p>
            <p><strong>Start Date:</strong> {tournament.startDate}</p>
            <p><strong>End Date:</strong> {tournament.endDate}</p>
            <p><strong>Vacancy:</strong> {tournament.vacancy}</p>
            
            {/* Join/Withdraw Button */}
            {isPlayerRegistered(tournament.id) ? (
              <button className="delete-button" onClick={(e) => handleWithdrawTournament(e, tournament.id)}>Withdraw</button>
            ) : (
              tournament.vacancy > 0 && 
              <button className="edit-button" onClick={(e) => handleJoinTournament(e, tournament.id)}>Join</button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserTournamentPage;



