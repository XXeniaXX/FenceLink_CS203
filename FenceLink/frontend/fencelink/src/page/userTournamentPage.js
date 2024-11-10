import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import './tournamentPage.css';
import axios from 'axios';

const TournamentPage = () => {
  const [tournaments, setTournaments] = useState([]);
  const [joinedTournaments, setJoinedTournaments] = useState([]); // Track tournaments the user has joined
  const [filter, setFilter] = useState({
    name: '',
    tournamentType: '',
    genderType: '',
    weaponType: '',
    ageGroup: '',
    tournamentDate: '',
  });

  const playerId = 1; // Replace with actual player ID from context or auth system

  // Fetch tournaments from the backend
  useEffect(() => {
    axios.get('http://localhost:8080/api/tournaments')
      .then(response => {
        setTournaments(response.data);
      })
      .catch(error => {
        console.error('Error fetching tournaments:', error);
      });
  }, []);

  // Handle filter change
  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter({ ...filter, [name]: value });
  };

  // Clear the filter fields
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

  // Filter tournaments based on filter state
  const filteredTournaments = tournaments.filter((tournament) => {
    const tournamentDate = new Date(tournament.startDate);
    const filterDate = new Date(filter.tournamentDate);

    return (
      (!filter.name || tournament.name.toLowerCase().includes(filter.name.toLowerCase())) &&
      (!filter.tournamentType || tournament.tournamentType === filter.tournamentType) &&
      (!filter.gender || tournament.gender === filter.gender) &&
      (!filter.weaponType || tournament.weaponType === filter.weaponType) &&
      (!filter.ageGroup || tournament.ageGroup === filter.ageGroup) &&
      (!filter.tournamentDate || tournamentDate.toDateString() === filterDate.toDateString())
    );
  });

  // Handle Join tournament
  const handleJoin = (tournamentId) => {
    axios.post(`http://localhost:8080/api/players/${playerId}/register/${tournamentId}`)
      .then(response => {
        setJoinedTournaments([...joinedTournaments, tournamentId]);
      })
      .catch(error => {
        console.error('Error joining tournament:', error);
      });
  };

  // Handle Withdraw tournament
  const handleWithdraw = (tournamentId) => {
    axios.delete(`http://localhost:8080/api/players/${playerId}/withdraw/${tournamentId}`)
      .then(response => {
        setJoinedTournaments(joinedTournaments.filter(id => id !== tournamentId));
      })
      .catch(error => {
        console.error('Error withdrawing from tournament:', error);
      });
  };

  return (
    <div className="tournament-page">
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
        <select name="tournamentType" value={filter.tournamentType} onChange={handleFilterChange}>
          <option value="">Select Tournament Type</option>
          <option value="Friendly">Friendly</option>
          <option value="Competitive">Competitive</option>
        </select>
        <select name="genderType" value={filter.genderType} onChange={handleFilterChange}>
          <option value="">Select Gender</option>
          <option value="Female">Female</option>
          <option value="Male">Male</option>
          <option value="Mixed">Mixed</option>
        </select>
        <select name="weaponType" value={filter.weaponType} onChange={handleFilterChange}>
          <option value="">Select Weapon Type</option>
          <option value="Epee">Epee</option>
          <option value="Foil">Foil</option>
          <option value="Saber">Saber</option>
        </select>
        <select name="ageGroup" value={filter.ageGroup} onChange={handleFilterChange}>
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
        {filteredTournaments.map((tournament) => (
          <div key={tournament.id} className="tournament-item">
            <h3>{tournament.name}</h3>
            <p><strong>Location:</strong> {tournament.location}</p>
            <p><strong>Description:</strong> {tournament.description}</p>
            <p><strong>Registration Date:</strong> {tournament.registrationDate}</p>
            <p><strong>Type:</strong> {tournament.tournamentType}</p>
            <p><strong>Age Group:</strong> {tournament.ageGroup}</p>
            <p><strong>Weapon Type:</strong> {tournament.weaponType}</p>
            <p><strong>Gender:</strong> {tournament.genderType}</p>
            <p><strong>Start Date:</strong> {tournament.startDate}</p>
            <p><strong>End Date:</strong> {tournament.endDate}</p>
            <p><strong>Vacancy:</strong> {tournament.vacancy}</p>

            {/* Join or Withdraw buttons */}
            <div className="button-group">
              {!joinedTournaments.includes(tournament.id) ? (
                <button onClick={() => handleJoin(tournament.id)} className="edit-button">Join</button>
              ) : (
                <button onClick={() => handleWithdraw(tournament.id)} className="delete-button">Withdraw</button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TournamentPage;

