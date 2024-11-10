import React, { useState, useEffect } from 'react';
import './login.css'; // Import the CSS file
import { useNavigate } from "react-router-dom";

const PlayerInfo = () => {
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [gender, setGender] = useState('');
    const [location, setLocation] = useState('');
    const [country, setCountry] = useState('');
    const [fencingWeapon, setFencingWeapon] = useState('');
    const [birthdate, setBirthDate] = useState('');
    const [bio, setBio] = useState('');
    
    const handlePlayerUpdate = async () => {

        const storedPlayerId = localStorage.getItem('playerId');
        setBio(null);

        if (!storedPlayerId) {
            alert('Player ID not found. Please log in again.');
            return;
        }
        
        const response = await fetch(`http://localhost:8080/api/players/${storedPlayerId}`, {
            method: 'PUT',
            headers: {
            'Content-Type': 'application/json',
            },
            body: JSON.stringify({name, gender, location, country, fencingWeapon, birthdate, bio}),
        })

        if (response.ok) {
            
            console.log('Player update successful');
            navigate("/mainpage");

        } else {
            // Handle error
            const errorText = await response.text();
            alert('Player Update failed: ' + errorText); 
            console.error('Player Update failed');
        }
    };

    return (
    <div>
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
        </nav>
        <div className="container">
            <h1 className="header">Player Information</h1>
            <form className="form2" onSubmit={(e) => { e.preventDefault(); handlePlayerUpdate(); }}>
                <label className="label">
                    Fullname
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="input"
                        placeholder="Enter your fullname"
                        required
                    />
                </label>
            <div className="form-group">
                <label className="label">
                    Birth Date
                    <input
                        type="date"
                        value={birthdate}
                        onChange={(e) => setBirthDate(e.target.value)}
                        className="input"
                        placeholder="Enter your Birth Date"
                        required
                    />
                </label>
                <label className="label">
                Fencing Weapon
                <select
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                    className="input select-input"
                    required
                >
                    <option value="" disabled selected>Select Gender</option>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                </select>
                </label>
            </div>
            <div className="form-group">
                <label className="label">
                    Country
                    <input
                        type="text"
                        value={country}
                        onChange={(e) => setCountry(e.target.value)}
                        className="input"
                        placeholder="Enter your Country"
                        required
                    />
                </label>
                <label className="label">
                    State
                    <input
                        type="text"
                        value={location}
                        onChange={(e) => setLocation(e.target.value)}
                        className="input"
                        placeholder="Enter your state"
                        required
                    />
                </label>
            </div>
            <label className="label">
                Fencing Weapon
                <select
                    value={fencingWeapon}
                    onChange={(e) => setFencingWeapon(e.target.value)}
                    className="input select-input"
                    required
                >
                    <option value="" disabled selected>Select Weapon</option>
                    <option value="Epee">Epee</option>
                    <option value="Foil">Foil</option>
                    <option value="Saber">Saber</option>
                </select>
            </label>
            
            <button type="submit" className="button">Submit</button>
        </form>
        </div>
    </div>
    );
}

export default PlayerInfo;