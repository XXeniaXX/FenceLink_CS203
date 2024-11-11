import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';
import { signOut } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';


const NavBar = () => {

  const navigate = useNavigate();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const storedUserName = localStorage.getItem('userName');

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

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

  return (
    <nav className="nav">
      <div className="logo-container">
        <img src="/fencelink.png" alt="Logo" className="logo" />
      </div>

      <ul className="nav-links">
        <li><Link to="/mainpage">HOME</Link></li>
        <li><Link to="/tournament">TOURNAMENT</Link></li>
        <li><Link to="/results">RESULTS</Link></li>
        <li><Link to="/ranking">RANKING</Link></li>
      </ul>

      <div className="profile-section" onClick={toggleDropdown}>
        <li className="nav-links"><Link to="/profilepage">Hi, {storedUserName}</Link></li>
        <span className={`dropdown-icon ${dropdownOpen ? 'rotate' : ''}`}>▼</span>
        {dropdownOpen && (
          <div className="dropdown-menu">
            <button onClick={handleSignOut} className="dropdown-item">Sign Out</button>
          </div>
        )}
      </div>

    </nav>
  );
};

export default NavBar;
