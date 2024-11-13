import React, { useState, useEffect } from 'react';
import { signOut, getCurrentUser } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file
import Navbar from '../components/Navbar';

const MainPage = () => {
    const navigate = useNavigate();

    const [userName, setUserName] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Function to fetch the user's name from the backend
        const storedUserId = localStorage.getItem('userId');

        if (!storedUserId) {
            alert('User ID not found. Please log in again.');
            return;
        }

        const fetchUserName = async () => {
        try {
            const response = await fetch(`http://47.129.36.1:8080/api/users/${storedUserId}`);
            if (!response.ok) {
                throw new Error('Failed to fetch user data');
            }

            const data = await response.json();
            setUserName(data.userName); 
            localStorage.setItem("userName", data.userName);
        } catch (error) {
            console.error('Error fetching user data:', error);
        } finally {
            setLoading(false); // Stop loading once data is fetched or an error occurs
        }
        };

        fetchUserName();
    }, []);
    
    return (
        <div>
            {/* Render the NavBar component */}
                <Navbar />

            {/* Display a loading message until the data is fetched */}
            <h1>{loading ? 'Loading...' : `Welcome, ${userName}`}</h1>
            <p>This is your homepage, where you can access different parts of the app.</p>
                
            {/* Upcoming Tournaments Section */}
            <section className="content">
            <h2>Your Upcoming Tournaments</h2>
            <p>Here you can find the latest updates and access your features.</p>
            {/* Add more content as needed */}
            </section>
        </div>
        
    )
}
export default MainPage;