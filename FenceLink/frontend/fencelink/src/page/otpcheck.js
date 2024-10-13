import React, { useState, useEffect } from 'react';
import { confirmSignUp, resendSignUpCode } from 'aws-amplify/auth';
import { useNavigate } from "react-router-dom";
import OtpInput from "otp-input-react";
import './otp.css'
import './login.css'; // Import the CSS file

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
    <div className="container">
      <h1 className = "header">OTP Verification</h1>
      <form onSubmit={handleSignUpConfirmation} className = "form">  
        <div>
          <label>
            Enter the 6-digit verification code that was sent to your email
          </label>
            <OtpInput 
                className="otp-container"
                type="text" 
                value={confirmationCode} 
                onChange={(value) => setConfirmationCode(value)} 
                OTPLength={6}
                otpType="number"
                autoFocus
              />
          </div>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <button type="submit" className = "button">Verify Account</button>
      </form>
      <footer className= "footer">
            Didn't receive code?<button onClick={resendConfirmationCode} className = "underline-button">Resend</button>
      </footer>
    </div>
  );
};

export default OtpCheck;