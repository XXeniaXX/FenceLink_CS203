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
        <li><Link to="/homepage">HOME</Link></li>
        <li><Link to="/tournament">TOURNAMENT</Link></li>
        <li><Link to="/results">RESULTS</Link></li>
        <li><Link to="/ranking">RANKING</Link></li>
      </ul>

      <div className="profile-section">
        <img src="/defaultpfp.png" alt="Profile" className="profile-pic" />
        <Link to="/profile" className="profile-link">
            Profile
        </Link>
        <span className="dropdown-icon">▼</span>
    </div>
    </nav>
  );
};

export default NavBar;
