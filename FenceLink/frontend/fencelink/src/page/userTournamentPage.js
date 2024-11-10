import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import './tournamentPage.css';

const TournamentPage = () => {
  const [tournaments, setTournaments] = useState([]);
  const [joinedTournaments, setJoinedTournaments] = useState([]); // Track tournaments the user has joined

  // Filter state
  const [filter, setFilter] = useState({
    name: '',
    tournamentType: '',
    gender: '',
    weaponType: '',
    ageGroup: '',
    tournamentDate: '', // New filter for tournament date
  });

  // Load tournaments from localStorage on component mount
  useEffect(() => {
    const storedTournaments = localStorage.getItem('tournaments');
    if (storedTournaments) {
      setTournaments(JSON.parse(storedTournaments));
    }
  }, []);

  // Save tournaments to localStorage whenever the list changes
  useEffect(() => {
    localStorage.setItem('tournaments', JSON.stringify(tournaments));
  }, [tournaments]);

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
      gender: '',
      weaponType: '',
      ageGroup: '',
      tournamentDate: '',
    });
  };

  // Filter tournaments based on filter state
  const filteredTournaments = tournaments.filter((tournament) => {
    const tournamentDate = new Date(tournament.startDate); // Assuming startDate is used for filtering
    const filterDate = new Date(filter.tournamentDate);

    return (
      (!filter.name || tournament.name.toLowerCase().includes(filter.name.toLowerCase())) &&
      (!filter.tournamentType || tournament.tournamentType === filter.tournamentType) &&
      (!filter.gender || tournament.gender === filter.gender) &&
      (!filter.weaponType || tournament.weaponType === filter.weaponType) &&
      (!filter.ageGroup || tournament.ageGroup === filter.ageGroup) &&
      (!filter.tournamentDate || tournamentDate.toDateString() === filterDate.toDateString()) // Check if tournament date matches
    );
  });

  // Join tournament
  const handleJoin = (tournament) => {
    if (!joinedTournaments.includes(tournament)) {
      setJoinedTournaments([...joinedTournaments, tournament]);
    }
  };

  // Withdraw from tournament
  const handleWithdraw = (tournament) => {
    setJoinedTournaments(joinedTournaments.filter(t => t !== tournament));
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
        <select name="gender" value={filter.gender} onChange={handleFilterChange}>
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
          <option value="Junior">Junior</option>
          <option value="Youth">Youth</option>
          <option value="Adult">Adult</option>
          <option value="Senior">Senior</option>
        </select>

        {/* Tournament Date Filter */}
        <input
          type="date"
          name="tournamentDate"
          value={filter.tournamentDate}
          onChange={handleFilterChange}
        />
        
        <button className="search-button">Search</button>
        {/* Clear Filters Button */}
        <button className="clear-button" onClick={clearFilters}>Clear Filters</button>
      </div>

      {/* Tournament List */}
      <div className="tournament-list">
        {filteredTournaments.map((tournament, index) => (
          <div key={index} className="tournament-item">
            <h3>{tournament.name}</h3>
            <p><strong>Location:</strong> {tournament.location}</p>
            <p><strong>Description:</strong> {tournament.description}</p>
            <p><strong>Registration Date:</strong> {tournament.registrationDate}</p>
            <p><strong>Type:</strong> {tournament.tournamentType}</p>
            <p><strong>Age Group:</strong> {tournament.ageGroup}</p>
            <p><strong>Weapon Type:</strong> {tournament.weaponType}</p>
            <p><strong>Gender:</strong> {tournament.gender}</p>
            <p><strong>Start Date:</strong> {tournament.startDate}</p>
            <p><strong>End Date:</strong> {tournament.endDate}</p>
            <p><strong>Vacancy:</strong> {tournament.vacancy}</p>

            {/* Join or Withdraw buttons */}
            <div className="button-group">
              {!joinedTournaments.includes(tournament) ? (
                <button onClick={() => handleJoin(tournament)} className="join-button">Join</button>
              ) : (
                <button onClick={() => handleWithdraw(tournament)} className="withdraw-button">Withdraw</button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TournamentPage;
