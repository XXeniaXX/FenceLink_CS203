import React, { useState } from 'react';
import { signOut } from 'aws-amplify/auth';
import './profilepage.css';
import { useNavigate } from "react-router-dom";
import Navbar from '../components/Navbar';
import fencingplayer from './assets/fencingplayer.png';
import fencingcross from './assets/fencingcross.png';
import profileicon from './assets/profileicon.png';


const ProfilePage = () => {
    const navigate = useNavigate();
    const storedUserName = localStorage.getItem('userName');
    const storedAge = localStorage.getItem('age');
    const storedLocation = localStorage.getItem('location');
    const storedCountry = localStorage.getItem('country');
    const storedBio = localStorage.getItem('bio');

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
                <div>
                    <label htmlFor="profilePicUpload">
                    <img
                        src={profileicon}
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
                    <h2>{storedUserName}, {storedAge}</h2>
                    <p>{storedCountry}, {storedLocation}</p>
                    <p>{storedBio}</p>
                    </div>
                </div>
                <div className="tournamentsContainer">

                    <button className="tournamentButton" onClick={() => navigate('/pasttournaments')}>
                        <img
                            src={fencingcross}
                            alt="fencingcross"
                            className="buttonImage"
                        />
                        <p style={{ color: '#1C1E53' }} >Past Tournaments</p>
                    </button>
                    <button className="tournamentButton" onClick={() => navigate('/upcomingtournaments')}>
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
                <div className="buttonContainer">
                <button className="button" onClick={() => navigate('/editprofile')}>
                    Edit Profile
                </button>
                </div>
                
            </div> 
            </div>
            
        )
    
}
export default ProfilePage;