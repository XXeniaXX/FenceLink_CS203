import React, { useState } from 'react';
import './Navbar.css';

const AdminNavBar = () => {
  const [dropdownOpen, setDropdownOpen] = useState(false);

  // Example username (replace or fetch based on your logic)
  
  const storedUserName = localStorage.getItem('userName');
  const toggleDropdown = () => {
    setDropdownOpen(prevState => !prevState);
  };

  const handleSignOut = () => {
    // Add your sign-out logic here
    console.log('Sign out clicked');
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
