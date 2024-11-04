import React, { useEffect, useState } from 'react';
import './HomePage.css';
import Navbar from '../components/Navbar';

const HomePage = () => {
  const [userName, setUserName] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Function to fetch the user's name from the backend
    const fetchUserName = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/user'); // Replace with your backend URL
        if (!response.ok) {
          throw new Error('Failed to fetch user data');
        }

        const data = await response.json();
        setUserName(data.name); // Assume the backend returns { "name": "Admin" }
      } catch (error) {
        console.error('Error fetching user data:', error);
      } finally {
        setLoading(false); // Stop loading once data is fetched or an error occurs
      }
    };

    fetchUserName();
  }, []);

  return (
    <div className="home-page">
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
    
  );
};

export default HomePage;