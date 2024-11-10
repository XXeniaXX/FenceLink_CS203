import React, { useState, useEffect } from 'react';
import { signOut, getCurrentUser } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file
import './homepage.css';
import Navbar from '../components/Navbar';

const PastTournaments = () => {
   
    return (
        <div>
            {/* Render the NavBar component */}
                <Navbar />
        </div>
    )
}
export default PastTournaments;