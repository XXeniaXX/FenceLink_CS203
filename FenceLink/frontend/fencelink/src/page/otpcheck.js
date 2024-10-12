import React, { useState, useEffect } from 'react';
import { confirmSignUp, resendSignUpCode } from 'aws-amplify/auth';
import { useNavigate } from "react-router-dom";
import './otp.css'; // Import the CSS file

const OtpCheck = () => {
  const navigate = useNavigate();
  
  const [username, setUsername] = useState('');
  const [confirmationCode, setConfirmationCode] = useState(''); // State to hold OTP
  const [errorMessage, setErrorMessage] = useState(''); // State to hold errors

  useEffect(() => {
    const storedUsername = localStorage.getItem('username');
    if (storedUsername) {
      setUsername(storedUsername); 
    } else {
      console.log('No username found in local storage.'); // Debug log
    }
  }, []);
 

  // Function to handle OTP confirmation
  async function handleSignUpConfirmation(e) {
    e.preventDefault();

    try {
      await confirmSignUp({
        username: username,
        confirmationCode: confirmationCode
      });
      navigate("/mainpage");
    } catch (error) {
      console.log('error confirming sign up', error);
      setErrorMessage('Failed to confirm sign-up. Please check the confirmation code.');
    }
  } 

  async function resendConfirmationCode() {
    try {
      console.log(username);
      await resendSignUpCode({username: username});
      console.log('Confirmation code resent successfully');
    } catch (error) {
      console.log('Error resending confirmation code: ', error);
    }
  }

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
      <button onClick={resendConfirmationCode}>Resend Confirmation Code</button>
    </div>
  );
};

export default OtpCheck;