import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const NavBar = () => {
  return (
    <nav className="nav">
      <div className="site-title">FENCELINK</div>

      <ul className="nav-links">
        <li><Link to="/homepage">Home</Link></li>
        <li><Link to="/tournament">Tournament</Link></li>
        <li><Link to="/results">Results</Link></li>
        <li><Link to="/ranking">Ranking</Link></li>
      </ul>

      <div className="profile-section">
        <img src="/path/to/profile-pic.jpg" alt="Profile" className="profile-pic" />
        <li className="nav-links"><Link to="/profilepage">Hi, name</Link></li>
      </div>
    </nav>
  );
};

export default NavBar;
