import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';
import { signOut } from 'aws-amplify/auth';

const NavBar = () => {
  const [showDropdown, setShowDropdown] = useState(false); // State to manage dropdown visibility
  const navigate = useNavigate();
  const storedUserName = localStorage.getItem('userName') || "User"; // Fallback if userName is missing

  const handleSignOut = async () => {
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
        <img src="/fencelink.png" alt="Logo" className="logo" />
      </div>

      <ul className="nav-links">
        <li><Link to="/playerHomepage">HOME</Link></li>
        <li><Link to="/usertournament">TOURNAMENT</Link></li>
        <li><Link to="/results">RESULTS</Link></li>
        <li><Link to="/ranking">RANKING</Link></li>
      </ul>

      {/* Profile Section with Dropdown */}
      <div
        className="profile-section"
        onMouseEnter={() => setShowDropdown(true)} // Show dropdown on hover
        onMouseLeave={() => setShowDropdown(false)} // Hide dropdown when leaving the section
      >
        {/* Link to Profile Page */}
        <Link to="/profilepage" className="profile-link">
          <img src="./profileicon.png" alt="Profile" className="profile-pic" />
          <span className="profile-name">Hi, {storedUserName}</span>
        </Link>

        {/* Dropdown Icon */}
        <span
          className={`dropdown-icon ${showDropdown ? 'rotate' : ''}`}
          onClick={toggleDropdown} // Toggle dropdown on click
        >
          ▼
        </span>

        {/* Dropdown Menu */}
        {showDropdown && (
          <div className="dropdown-menu">
            <Link to="/editprofile" className="dropdown-item">Edit Profile</Link>
            <button onClick={handleSignOut} className="dropdown-item">Sign Out</button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default NavBar;
