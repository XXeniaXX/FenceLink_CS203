import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const NavBar = () => {

  const storedUserName = localStorage.getItem('userName');

  return (
    <nav className="nav">
      <div className="site-title">FENCELINK</div>

      <ul className="nav-links">
        <li><Link to="/mainpage">Home</Link></li>
        <li><Link to="/tournament">Tournament</Link></li>
        <li><Link to="/results">Results</Link></li>
        <li><Link to="/ranking">Ranking</Link></li>
      </ul>

      <div className="profile-section">
        <li className="nav-links"><Link to="/profilepage">Hi, {storedUserName}</Link></li>
      </div>
    </nav>
  );
};

export default NavBar;
