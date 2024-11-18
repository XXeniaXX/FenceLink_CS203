import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';
import { signOut } from 'aws-amplify/auth';


const AdminNavBar = () => {
  const [showDropdown, setShowDropdown] = useState(false); // For profile dropdown visibility
  const navigate = useNavigate();
  const storedUserName = localStorage.getItem('name')

  const handleSignOut = async() => {
    try {
      await signOut({ global: true });
      console.log('User signed out successfully');

      localStorage.removeItem('jwtToken');
      sessionStorage.clear();
      navigate('/login');
    } catch (error) {
      console.log('Error signing out: ', error);
    }
  };


  // Toggle dropdown visibility on click
  const toggleDropdown = () => {
    setShowDropdown(prev => !prev);
  };

  return (
    <nav className="nav">
      <div className="logo-container">
        <img src="/fencelink.png" alt="Logo" className="logo" style={{
          width: '210px',
          height: '70px',
          borderRadius: '50%',
          objectFit: 'contain'
        }} />
      </div>

      {/* Navigation Links */}
      <ul className="nav-links">
        <li><Link to="/adminhomepage">HOME</Link></li>
        <li><Link to="/tournament">TOURNAMENT</Link></li>
        <li><Link to="/results">RESULTS</Link></li>
        <li><Link to="/ranking">RANKING</Link></li>
        <li><Link to="/manage-players">MANAGE PLAYERS</Link></li>
      </ul>

      {/* Profile Section with Dropdown */}
      <div
        className="profile-section"
        onMouseEnter={() => setShowDropdown(true)} // Show dropdown on hover
        onMouseLeave={() => setShowDropdown(false)} // Hide dropdown when leaving the section
      >
        <img src="./profileicon.png" alt="Profile" className="profile-pic" />
        {/* Dropdown Icon */}
        <li className="nav-links">Hi, Admin {storedUserName}</li>
        <span
          className={`dropdown-icon ${showDropdown ? 'rotate' : ''}`}
          onClick={toggleDropdown} // Toggle dropdown on click
        >
          ▼
        </span>

        {/* Dropdown Menu */}
        {showDropdown && (
          <div className={`dropdown-menu ${showDropdown ? 'show' : ''}`}>
            <button onClick={handleSignOut} className="dropdown-item">Sign Out</button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default AdminNavBar;
