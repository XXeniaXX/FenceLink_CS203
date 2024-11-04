import React, { useState, useEffect } from 'react';
import { signOut, getCurrentUser } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file

const MainPage = () => {
    const navigate = useNavigate();

    const [userName, setUserName] = useState('');


    // useEffect(() => {
    //     const fetchUserName = async () => {
    //       try {
    //         // Get the current authenticated user
    //         const user = await getCurrentUser();
    //         console.log('User:', user);
            
    //         // Extract the user's name or preferred attribute
    //         const name = user.attributes.name || user.username; // Adjust based on the attribute available
    //         setUserName(name);
    
    //       } catch (error) {
    //         console.error('Error fetching user data:', error);
    //       }
    //     };
    
    //     fetchUserName();
    //   }, []);

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
            <h1>Welcome {userName}!</h1>
            <button onClick={handleSignOut} className="button">
                Sign Out
            </button>
        </div>
        
    )
}
export default MainPage;