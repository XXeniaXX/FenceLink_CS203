import React, { useState } from 'react';
import { signOut } from 'aws-amplify/auth';
import './login.css'; // Import the CSS file
import './homepage.css';
import { useNavigate } from "react-router-dom";
import Navbar from '../components/Navbar';
import fencingplayer from './assets/fencingplayer.png';
import fencingcross from './assets/fencingcross.png';


const ProfilePage = () => {
    const navigate = useNavigate();

    const [profileImage, setProfileImage] = useState(null);

    const handleImageChange = (e) => {
        if (e.target.files && e.target.files[0]) {
        setProfileImage(URL.createObjectURL(e.target.files[0]));
        }
    };

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
                {/* Render the NavBar component */}
                    <Navbar />
            <div className="container2">
                <div className="profileContainer">
                    <label htmlFor="profilePicUpload">
                    <img
                        src={profileImage || "https://via.placeholder.com/150"}
                        alt="Profile"
                        className="profileImage"
                    />
                    <input
                        type="file"
                        id="profilePicUpload"
                        accept="image/*"
                        onChange={handleImageChange}
                        style={{ display: 'none' }}
                    />
                    </label>
                    <div>
                    <h2>Jerry Anderson, 28</h2>
                    <p>New York, USA</p>
                    <p>Bio</p>
                    </div>
                    <button className="button">Edit Profile</button>
                </div>
                <div className="tournamentsContainer">

                    <button className="tournamentButton">
                        <img
                            src={fencingcross}
                            alt="fencingcross"
                            className="buttonImage"
                        />
                        <p style={{ color: '#1C1E53' }}>Past Tournaments</p>
                    </button>
                    <button className="tournamentButton">
                        <img
                            src={fencingplayer}
                            alt="fencingplayer"
                            className="buttonImage"
                        />
                        <p style={{ color: '#1C1E53' }}>Upcoming Tournaments</p>
                    </button>
                </div>
                <button onClick={handleSignOut} className="button">
                        Sign Out
                </button>
                
            </div> 
            </div>
            
        )
    
}
export default ProfilePage;