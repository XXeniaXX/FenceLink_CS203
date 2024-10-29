import React, { useState } from 'react';
import { signOut } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file

const MainPage = () => {
    const navigate = useNavigate();

    async function handleSignOut() {
    try {
        await signOut({ global: true });
        console.log('User signed out successfully');
        navigate('/login');
    } catch (error) {
        console.log('error signing out: ', error);
    }
    }
    
    return (
        <div>
            <h1>Welcome!</h1>
            <button onClick={handleSignOut} className="button">
                Sign Out
            </button>
        </div>
        
    )
}
export default MainPage;