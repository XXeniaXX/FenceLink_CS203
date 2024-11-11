import React, { useState } from 'react';
import './Navbar.css';

const PlainBar = () => {

  return (
    <nav className="nav">
      <div className="logo-container">
        <img src="/fencelink.png" alt="Logo" className="logo" />
      </div>
    </nav>
  );
};

export default PlainBar;
