import React, { useState, useEffect } from 'react';
import './login.css';
import { useNavigate } from "react-router-dom";
import Navbar from '../components/Navbar';
import backarrow from './assets/arrow.png'

const EditProfile = () => {
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [gender, setGender] = useState('');
    const [location, setLocation] = useState('');
    const [country, setCountry] = useState('');
    const [fencingWeapon, setFencingWeapon] = useState('');
    const [birthdate, setBirthDate] = useState('');
    const [bio, setBio] = useState('');
    const [user_id, setUserId] = useState('');
    const [formError, setFormError] = useState(false);

    // Fetch player data on component mount
    useEffect(() => {
        const storedPlayerId = localStorage.getItem('playerId');

        if (!storedPlayerId) {
            alert('Player ID not found. Please log in again.');
            return;
        }

        const fetchPlayerData = async () => {
            const response = await fetch(`http://47.129.36.1:8080/api/players/${storedPlayerId}`);
            if (response.ok) {
                const data = await response.json();
                setName(data.name || '');
                setGender(data.gender || '');
                setLocation(data.location || '');
                setCountry(data.country || '');
                setFencingWeapon(data.fencingWeapon || '');
                setBirthDate(data.birthdate || '');
                setBio(data.bio || '');
                setUserId(data.user?.id || '');
            } else {
                alert('Failed to fetch player data.');
            }
        };

        fetchPlayerData();
    }, []);

    const handleUpdate = async () => {
        if (!name || !gender || !location || !country || !fencingWeapon || !birthdate) {
            setFormError(true);
            return;
        }

        const storedPlayerId = localStorage.getItem('playerId');
        const storedUserId = localStorage.getItem('userId');

        const updatePayload = {};
        if (name) updatePayload.name = name;
        if (gender) updatePayload.gender = gender;
        if (location) updatePayload.location = location;
        if (country) updatePayload.country = country;
        if (fencingWeapon) updatePayload.fencingWeapon = fencingWeapon;
        if (birthdate) updatePayload.birthdate = birthdate;
        if (bio) updatePayload.bio = bio;
        if (user_id) updatePayload.user = { id: storedUserId };

        const response = await fetch(`http://47.129.36.1:8080/api/players/${storedPlayerId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatePayload),
        });

        if (response.ok) {
            const playerData = await response.json();

            // Update localStorage with new values
            localStorage.setItem("name", playerData.name);
            localStorage.setItem("location", playerData.location);
            localStorage.setItem("country", playerData.country);
            localStorage.setItem("fencingWeapon", playerData.fencingWeapon);
            localStorage.setItem("gender", playerData.gender);
            localStorage.setItem('birthdate', birthdate);
            localStorage.setItem('age', playerData.age);
            localStorage.setItem('bio', bio);

            console.log('Profile updated successfully');
            navigate("/profilepage");
        } else {
            const errorText = await response.text();
            alert('Profile update failed: ' + errorText);
        }
    };

    const handleBioChange = (e) => {
        const text = e.target.value;
        if (text.length <= 50) {
            setBio(text); // Only update if character count is 50 or less
            setFormError(false);
        }
    };

    return (
        <div>
        <Navbar />
        <div className="container">

            <div className="back-button-container">
                <back-button onClick={() => navigate('/profilepage')} className="back-button">
                    <img src={backarrow} className="backbuttonimg" />
                </back-button>
            </div>  
    
            <h1 className="header">Edit Profile</h1>
            <form className="form" onSubmit={(e) => { e.preventDefault(); handleUpdate(); }}>
                <div className="form-group">
                    <label className="label">
                        Fullname
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => { setName(e.target.value); setFormError(false); }}
                            className="input"
                            placeholder="Enter your fullname"
                        />
                    </label>
                    <label className="label">
                        Birth Date
                        <input
                            type="date"
                            value={birthdate}
                            onChange={(e) => { setBirthDate(e.target.value); setFormError(false); }}
                            className="input"
                        />
                    </label>
                </div>
                <div className="form-group">
                    <label className="label">
                        Gender
                        <select
                            value={gender}
                            onChange={(e) => { setGender(e.target.value); setFormError(false); }}
                            className="input select-input"
                        >
                            <option value="" disabled>Select Gender</option>
                            <option value="Male">Male</option>
                            <option value="Female">Female</option>
                        </select>
                    </label>
                    <label className="label">
                        Country
                        <input
                            type="text"
                            value={country}
                            onChange={(e) => { setCountry(e.target.value); setFormError(false); }}
                            className="input"
                            placeholder="Enter your Country"
                        />
                    </label>
                    <label className="label">
                        State
                        <input
                            type="text"
                            value={location}
                            onChange={(e) => { setLocation(e.target.value); setFormError(false); }}
                            className="input"
                            placeholder="Enter your state"
                        />
                    </label>
                </div>
                <label className="label">
                    Fencing Weapon
                    <select
                        value={fencingWeapon}
                        onChange={(e) => { setFencingWeapon(e.target.value); setFormError(false); }}
                        className="input select-input"
                    >
                        <option value="" disabled>Select Weapon</option>
                        <option value="Epee">Epee</option>
                        <option value="Foil">Foil</option>
                        <option value="Saber">Saber</option>
                    </select>
                </label>
                <label className="label">
                    Bio
                    <input
                        value={bio}
                        onChange={(e) => handleBioChange(e)}
                        className="input"
                        placeholder="Tell us about yourself (max 50 characters)"
                    />
                </label>
                <p style={{ fontSize: '0.9rem', color: '#555', marginTop: '1px', marginBottom: '5px' }}>
                    {bio.length} / 50 characters
                </p>
                {formError && <p className="error-message">All fields should be filled up.</p>}
                <button type="submit" className="button">
                    Update Profile
                </button>
            </form>
        </div>
    </div>
    
    );
};

export default EditProfile;
