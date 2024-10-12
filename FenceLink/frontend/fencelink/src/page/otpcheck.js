import React, { useState } from 'react';
import { confirmSignUp } from 'aws-amplify/auth';
import { useNavigate } from "react-router-dom";
import './otp.css'; // Import the CSS file

const OtpCheck = () => {
  const [username, setUsername] = useState(''); // State to hold the username
  const [confirmationCode, setConfirmationCode] = useState(''); // State to hold OTP
  const [errorMessage, setErrorMessage] = useState(''); // State to hold errors
  const navigate = useNavigate();

  // Function to handle OTP confirmation
  const handleSignUpConfirmation = async (e) => {
    e.preventDefault();
    try {
      await confirmSignUp(username, confirmationCode); // Confirm sign-up with AWS Amplify
      alert('OTP confirmation successful!');
      navigate('/mainpage'); // Redirect user to home page upon success
    } catch (error) {
      setErrorMessage('Error confirming sign up: ' + error.message); // Display error message
      console.error('Error confirming sign up', error);
    }
  };

  return (
    <div className="otp-container">
      <h1>OTP Confirmation</h1>
      <form onSubmit={handleSignUpConfirmation}>
        <label>
          Username
          <input 
            type="text" 
            value={username} 
            onChange={(e) => setUsername(e.target.value)} 
            placeholder="Enter your username" 
            required 
          />
        </label>
        <label>
          Confirmation Code (OTP)
          <input 
            type="text" 
            value={confirmationCode} 
            onChange={(e) => setConfirmationCode(e.target.value)} 
            placeholder="Enter your confirmation code" 
            required 
          />
        </label>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <button type="submit">Confirm Sign Up</button>
      </form>
    </div>
  );
};

export default OtpCheck;