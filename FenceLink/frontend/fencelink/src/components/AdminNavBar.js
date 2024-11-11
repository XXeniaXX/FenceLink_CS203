import React, { useState } from 'react';
import './Navbar.css';
import { signOut } from 'aws-amplify/auth';
import { Link, useNavigate } from 'react-router-dom';

const AdminNavBar = () => {
  const navigate = useNavigate();

  const [dropdownOpen, setDropdownOpen] = useState(false);

  // Example username (replace or fetch based on your logic)
  
  const storedUserName = localStorage.getItem('userName');
  const toggleDropdown = () => {
    setDropdownOpen(prevState => !prevState);
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
    <div>
      {/* Render the NavBar component */}
      <nav className="nav">
        <img 
          src="/fencelink.png" 
          alt="FenceLink Logo" 
          style={{
              width: '210px',
              height: '70px',
              borderRadius: '50%',
              objectFit: 'contain'
          }} 
        />

        <ul className="nav-links">
          <li><Link to="/adminhomepage">HOME</Link></li>
          <li><Link to="/tournament">TOURNAMENT</Link></li>
          <li><Link to="/results">RESULTS</Link></li>
          <li><Link to="/ranking">RANKING</Link></li>
          <li><Link to="/manage-players">MANAGE PLAYERS</Link></li>
        </ul>

        <div className="profile-section" onClick={toggleDropdown}>
          <li className="nav-links">Hi, Admin {storedUserName}</li>
          <span className={`dropdown-icon ${dropdownOpen ? 'rotate' : ''}`}>▼</span>
          {dropdownOpen && (
            <div className="dropdown-menu">
              <button onClick={handleSignOut} className="dropdown-item">Sign Out</button>
            </div>
          )}
        </div>
      </nav>
    </div>
  );
};

export default AdminNavBar;
