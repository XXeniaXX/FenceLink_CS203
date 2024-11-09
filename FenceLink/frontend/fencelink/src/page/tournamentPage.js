import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import './tournamentPage.css';

const TournamentPage = () => {
  const [showModal, setShowModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedTournament, setSelectedTournament] = useState(null);
  const [tournaments, setTournaments] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    location: '',
    description: '',
    registrationDate: '',
    tournamentType: '',
    ageGroup: '',
    weaponType: '',
    gender: '',
    startDate: '',
    endDate: '',
    vacancy: '',
  });

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

  const openModal = (tournament = null) => {
    setIsEditing(!!tournament);
    setFormData(tournament || {
      name: '',
      location: '',
      description: '',
      registrationDate: '',
      tournamentType: '',
      ageGroup: '',
      weaponType: '',
      gender: '',
      startDate: '',
      endDate: '',
      vacancy: '',
    });
    setSelectedTournament(tournament);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setIsEditing(false);
    setSelectedTournament(null);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilter({ ...filter, [name]: value });
  };

  const handleSave = () => {
    if (isEditing) {
      setTournaments(tournaments.map(t => t === selectedTournament ? formData : t));
    } else {
      setTournaments([...tournaments, formData]);
    }
    closeModal();
  };

  const handleDelete = (tournament) => {
    setTournaments(tournaments.filter(t => t !== tournament));
    closeModal();
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

      {/* Tournament List and Add Button */}
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

            {/* Edit and Delete buttons */}
            <div className="button-group">
              <button onClick={() => openModal(tournament)} className="edit-button">Edit</button>
              <button onClick={() => handleDelete(tournament)} className="delete-button">Delete</button>
            </div>
          </div>
        ))}
        <div className="add-tournament" onClick={() => openModal()}>
          <span>+</span>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>{isEditing ? 'Edit Tournament' : 'Add Tournament'}</h2>
            <form>
              <label>Tournament Name</label>
              <input type="text" name="name" value={formData.name} onChange={handleInputChange} />

              <label>Location</label>
              <input type="text" name="location" value={formData.location} onChange={handleInputChange} />

              <label>Description</label>
              <input type="text" name="description" value={formData.description} onChange={handleInputChange} />

              <label>Registration Date</label>
              <input type="date" name="registrationDate" value={formData.registrationDate} onChange={handleInputChange} />

              <label>Tournament Type</label>
              <select name="tournamentType" value={formData.tournamentType} onChange={handleInputChange}>
                <option value="">Select Tournament Type</option>
                <option value="Friendly">Friendly</option>
                <option value="Competitive">Competitive</option>
              </select>

              <label>Age Group</label>
              <select name="ageGroup" value={formData.ageGroup} onChange={handleInputChange}>
                <option value="">Select Age Group</option>
                <option value="Junior">Junior</option>
                <option value="Youth">Youth</option>
                <option value="Adult">Adult</option>
                <option value="Senior">Senior</option>
              </select>

              <label>Weapon Type</label>
              <select name="weaponType" value={formData.weaponType} onChange={handleInputChange}>
                <option value="">Select Weapon Type</option>
                <option value="Epee">Epee</option>
                <option value="Foil">Foil</option>
                <option value="Saber">Saber</option>
              </select>

              <label>Gender</label>
              <select name="gender" value={formData.gender} onChange={handleInputChange}>
                <option value="">Select Gender</option>
                <option value="Female">Female</option>
                <option value="Male">Male</option>
                <option value="Mixed">Mixed</option>
              </select>

              <label>Tournament Start Date</label>
              <input type="date" name="startDate" value={formData.startDate} onChange={handleInputChange} />

              <label>Tournament End Date</label>
              <input type="date" name="endDate" value={formData.endDate} onChange={handleInputChange} />

              <label>Vacancy</label>
              <input type="number" name="vacancy" value={formData.vacancy} onChange={handleInputChange} />

              <div className="modal-buttons">
                <button type="button" onClick={handleSave} className="save-button">Save</button>
                <button type="button" onClick={closeModal} className="close-button">Close</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default TournamentPage;



