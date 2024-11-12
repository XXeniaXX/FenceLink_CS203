import React, { useState, useEffect } from 'react';
import axios from 'axios';
import AdminNavBar from '../components/AdminNavBar';
import { useNavigate } from 'react-router-dom';
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
    genderType: '',
    startDate: '',
    endDate: '',
    vacancy: '',
  });

  const [filter, setFilter] = useState({
    name: '',
    tournamentType: '',
    genderType: '',
    weaponType: '',
    ageGroup: '',
    tournamentDate: '',
  });
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('http://13.213.45.2:8080/api/tournaments')
      .then(response => setTournaments(response.data))
      .catch(error => console.error('Error fetching tournaments:', error));
  }, []);

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
      genderType: '',
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

    // Enforce minimum and maximum values for the "vacancy" field
    if (name === 'vacancy') {
      const intValue = parseInt(value, 10);
      if (intValue < 10) {
        setFormData({ ...formData, [name]: 10 });
      } else if (intValue > 50) {
        setFormData({ ...formData, [name]: 50 });
      } else {
        setFormData({ ...formData, [name]: intValue });
      }
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleSave = () => {
    if (isEditing) {
      axios.put(`http://13.213.45.2:8080/api/tournaments/${selectedTournament.id}`, formData)
        .then(() => {
          setTournaments(tournaments.map(t => t.id === selectedTournament.id ? formData : t));
          closeModal();
        })
        .catch(error => console.error('Error updating tournament:', error));
    } else {
      axios.post('http://13.213.45.2:8080/api/tournaments', formData)
        .then(response => {
          setTournaments([...tournaments, response.data]);
          closeModal();
        })
        .catch(error => console.error('Error adding tournament:', error));
    }
  };

  const handleDelete = (tournament) => {
    const confirmDelete = window.confirm("Are you sure you want to delete this tournament?");
    if (confirmDelete) {
      axios.delete(`http://13.213.45.2:8080/api/tournaments/${tournament.id}`)
        .then(() => {
          setTournaments(tournaments.filter(t => t.id !== tournament.id));
          closeModal();
        })
        .catch(error => console.error('Error deleting tournament:', error));
    }
  };

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

  // Function to handle card click and navigate to MatchAdmin
  const handleCardClick = (tournamentId) => {
    navigate(`/match-admin/${tournamentId}`); // Adjust the path as needed for your routing setup
  };

  return (
    <div className="tournament-page">
      <AdminNavBar />

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

      {/* Tournament List and Add Button */}
      <div className="tournament-list">
        {filteredTournaments.map((tournament, index) => (
          <div
            key={index}
            className="tournament-item"
            onClick={() => handleCardClick(tournament.id)} // Navigate on card click
          >
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

            <div className="button-group">
              <button onClick={(e) => { e.stopPropagation(); openModal(tournament); }} className="edit-button">Edit</button>
              <button onClick={(e) => { e.stopPropagation(); handleDelete(tournament); }} className="delete-button">Delete</button>
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
              <textarea name="description" value={formData.description} onChange={handleInputChange}></textarea>
              <label>Registration Date</label>
              <input type="date" name="registrationDate" value={formData.registrationDate} onChange={handleInputChange} />
              <label>Tournament Type</label>
              <select name="tournamentType" value={formData.tournamentType} onChange={handleInputChange}>
                <option value="">Select Type</option>
                <option value="Friendly">Friendly</option>
                <option value="Competitive">Competitive</option>
              </select>
              <label>Age Group</label>
              <select name="ageGroup" value={formData.ageGroup} onChange={handleInputChange}>
                <option value="">Select Age Group</option>
                <option value="Youth">Youth</option>
                <option value="Adult">Adult</option>
              </select>
              <label>Weapon Type</label>
              <select name="weaponType" value={formData.weaponType} onChange={handleInputChange}>
                <option value="">Select Weapon Type</option>
                <option value="Epee">Epee</option>
                <option value="Foil">Foil</option>
                <option value="Saber">Saber</option>
              </select>
              <label>Gender</label>
              <select name="genderType" value={formData.genderType} onChange={handleInputChange}>
                <option value="">Select Gender</option>
                <option value="Female">Female</option>
                <option value="Male">Male</option>
                <option value="Mixed">Mixed</option>
              </select>
              <label>Start Date</label>
              <input type="date" name="startDate" value={formData.startDate} onChange={handleInputChange} />
              <label>End Date</label>
              <input type="date" name="endDate" value={formData.endDate} onChange={handleInputChange} />
              <label>Vacancy (10-50)</label>
              <input type="number" name="vacancy" value={formData.vacancy} onChange={handleInputChange} />

              <div className="modal-buttons">
                <button type="button" onClick={handleSave} className='save-button'>{isEditing ? 'Save Changes' : 'Add Tournament'}</button>
                <button type="button" className="cancel-button" onClick={closeModal}>Cancel</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default TournamentPage;








