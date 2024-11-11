import React, { useState } from 'react';
import './login.css';
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
    const [formError, setFormError] = useState(false); // Error state for missing fields

    // Function to check if all fields are complete
    const formIsComplete = () => {
        return name && gender && location && country && fencingWeapon && birthdate;
    };

    const handlePlayerUpdate = async () => {
        // Show error and prevent submission if any field is missing
        if (!formIsComplete()) {
            setFormError(true); // Show error message
            return; // Prevent form submission
        }

        const storedPlayerId = localStorage.getItem('playerId');
        const storedUserId = localStorage.getItem('userId');
        console.log(storedUserId);

        if (!storedPlayerId) {
            alert('Player ID not found. Please log in again.');
            return;
        }

        const response = await fetch(`http://localhost:8080/api/players/${storedPlayerId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ name, gender, location, country, fencingWeapon, birthdate, bio, user: { id: storedUserId } }),
        });

        if (response.ok) {
            const playerData = await response.json();

            localStorage.setItem("age", playerData.age);
            localStorage.setItem("location", playerData.location);
            localStorage.setItem("country", playerData.country);
            localStorage.setItem("age", playerData.age);
            localStorage.setItem("fencingWeapon", playerData.fencingWeapon);
            localStorage.setItem("location", playerData.location);
            localStorage.setItem("gender", playerData.gender);
            localStorage.setItem('birthdate', birthdate);

            console.log('Player update successful');
            navigate("/mainpage");
        } else {
            const errorText = await response.text();
            alert('Player Update failed: ' + errorText); 
            console.error('Player Update failed');
        }
    };

    return (
    <div>
        <nav className="nav">
            <div className="site-title">FENCELINK</div>
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
                        onChange={(e) => {
                            setName(e.target.value);
                            setFormError(false); // Reset error on change
                        }}
                        className="input"
                        placeholder="Enter your fullname"
                    />
                </label>
                <div className="form-group">
                    <label className="label">
                        Birth Date
                        <input
                            type="date"
                            value={birthdate}
                            onChange={(e) => {
                                setBirthDate(e.target.value);
                                setFormError(false);
                            }}
                            className="input"
                            placeholder="Enter your Birth Date"
                        />
                    </label>
                    <label className="label">
                        Gender
                        <select
                            value={gender}
                            onChange={(e) => {
                                setGender(e.target.value);
                                setFormError(false);
                            }}
                            className="input select-input"
                        >
                            <option value="" disabled>Select Gender</option>
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
                            onChange={(e) => {
                                setCountry(e.target.value);
                                setFormError(false);
                            }}
                            className="input"
                            placeholder="Enter your Country"
                        />
                    </label>
                    <label className="label">
                        State
                        <input
                            type="text"
                            value={location}
                            onChange={(e) => {
                                setLocation(e.target.value);
                                setFormError(false);
                            }}
                            className="input"
                            placeholder="Enter your state"
                        />
                    </label>
                </div>
                <label className="label">
                    Fencing Weapon
                    <select
                        value={fencingWeapon}
                        onChange={(e) => {
                            setFencingWeapon(e.target.value);
                            setFormError(false);
                        }}
                        className="input select-input"
                    >
                        <option value="" disabled>Select Weapon</option>
                        <option value="Epee">Epee</option>
                        <option value="Foil">Foil</option>
                        <option value="Saber">Saber</option>
                    </select>
                </label>

                {/* Display form error message if any field is missing */}
                {formError && <p className="error-message">All fields should be filled up.</p>}
                
                <button type="submit" className="button">
                    Submit
                </button>
                
            </form>
        </div>
    </div>
    );
};

export default PlayerInfo;
