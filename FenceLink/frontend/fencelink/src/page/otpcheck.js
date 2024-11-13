import React, { useState, useEffect } from 'react';
import { confirmSignUp, resendSignUpCode } from 'aws-amplify/auth';
import { useNavigate } from "react-router-dom";
import OtpInput from "otp-input-react";
import './otp.css'
import './login.css'; 
import PlainBar from '../components/AdminNavBar';

const OtpCheck = () => {
  const navigate = useNavigate();
  
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmationCode, setConfirmationCode] = useState(''); // State to hold OTP
  const [errorMessage, setErrorMessage] = useState(''); // State to hold errors

  useEffect(() => {
    const storedUsername = localStorage.getItem('username');
    const storedEmail = localStorage.getItem('email');
    const storedPassword = localStorage.getItem('password');
    if (storedUsername) {
      setUsername(storedUsername); 
    } else {
      console.log('No Username found in local storage.'); // Debug log
    }

    if (storedEmail) {
      setEmail(storedEmail); 
    } else {
      console.log('No Email found in local storage.'); // Debug log
    }

    if (storedPassword) {
      setPassword(storedPassword); 
    } else {
      console.log('No Password found in local storage.'); // Debug log
    }
  }, []);
 

  // Function to handle OTP confirmation
  async function handleSignUpConfirmation(e) {
    e.preventDefault();

    try {
      await confirmSignUp({
        username: email,
        confirmationCode: confirmationCode
      });

      await handleRegistration();

    } catch (error) {
      console.log('error confirming sign up', error);
      if (error.name === "CodeMismatchException") {
        setErrorMessage ("The verification code you entered is incorrect. Please try again.");
      } else if (error.name === "ExpiredCodeException") {
        setErrorMessage ("Your verification code has expired. Please request a new code.");
      } else if (error.name === "LimitExceededException") {
        setErrorMessage ("Too many failed attempts. Please try again after some time.");
      }
    }
  } 

  
  const handleRegistration = async () => {
    const response = await fetch('http://47.129.36.1:8080/api/users/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({username, email, password}),
    });

    if (response.ok) {
      
      const data = await response.json();
      localStorage.setItem("playerId", data.playerId);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem('userRole', data.userRole);
       
      console.log('Registration successful');
      navigate("/playerinfo");

    } else {
      // Handle error
      const errorText = await response.text();
      alert('Registration failed: ' + errorText); 
      console.error('Registration failed');
    }
    
  };

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
  </div>
  );
};

export default OtpCheck;