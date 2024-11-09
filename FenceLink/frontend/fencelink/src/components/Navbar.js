import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const NavBar = () => {
  return (
    <nav className="nav">
      <div className="logo-container">
        <img src="/fencelink.png" alt="Logo" className="logo" />
      </div>

      <ul className="nav-links">
        <li><Link to="/homepage">Home</Link></li>
        <li><Link to="/tournament">Tournament</Link></li>
        <li><Link to="/results">Results</Link></li>
        <li><Link to="/ranking">Ranking</Link></li>
      </ul>

      <div className="profile-section">
        <img src="/defaultpfp.png" alt="Profile" className="profile-pic" />
        <span>Profile</span>
        <span className="dropdown-icon">▼</span>
      </div>
    </nav>
  );
};

export default NavBar;
