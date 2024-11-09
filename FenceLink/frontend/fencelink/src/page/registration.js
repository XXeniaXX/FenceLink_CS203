import React, { useEffect,useState } from 'react';
import { signUp } from 'aws-amplify/auth';
import { Link, useNavigate } from "react-router-dom";
import './login.css';
import './otp.css'; // Import the CSS file

const Registration = () => {
  const navigate = useNavigate();
  
  // State variables to hold username and password
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordValidation, setpasswordValidation] = useState('');
  const [matchPassword, setmatchPassword] = useState('');
  const [emailValidation, setEmailValidation] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the default form submission
    console.log('Username:', username);
    console.log('Email:', email);
    console.log('Password:', password);
    console.log('Confirm Password:', confirmPassword);
    console.log('Password Validation:', passwordValidation);
    console.log('Password Match:', matchPassword);
    console.log('Email Validation:', emailValidation);

    if (passwordValidation || matchPassword) {
        // If there are validation messages, do not proceed with registration
        return;
    }

    await handleSignUp(); // Call the registration function
  };


  function validatePassword(password) {
    const minLength = 8;
    const hasSpecialCharacter = /[!@#$%^&*(),.?":{}|<>]/.test(password);
    const hasUppercase = /[A-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);

    if (password.length < minLength) {
      return "Password must be at least 8 characters.";
    } else if (!hasSpecialCharacter) {
      return "Password must contain at least one special character.";
    } else if (!hasUppercase) {
      return "Password must contain at least one uppercase character.";
    } else if (!hasNumber) {
      return "Password must contain at least one number.";
    } else {
      return "";
    }
  }

  function passwordMatch(confirmPassword) {
    if (password !== confirmPassword) {
      return "Passwords do not match.";
    } else {
      return "";
    }
  }

  function validateEmail(email) {
    const validEmail = /^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6})*$/;

    if (!validEmail.test(email)) {
      return "Please enter a valid email address.";
    } else {
      return "";
    }
  }

  async function handleSignUp() {
    try {

      if (password !== confirmPassword) {
        throw new Error('Passwords do not match');
      }
     
      await signUp({
        username: email,
        password: password,
        options: {
          userAttributes: {
            email: email
          }
        }
      });
      
        console.log(username);
        localStorage.setItem('username', username);
        localStorage.setItem('email', email);
        localStorage.setItem('password', password);
    
        navigate("/otpcheck");
      } catch (error) {
         console.log('error signing up:', error);
      }
    
  }

  
  return (
  <div>
    <nav className="nav">
      <div className="site-title">FENCELINK</div>
    </nav>
    <div className="container">
      <h1 className="header">REGISTRATION</h1>
      <form onSubmit={handleSubmit} className="form">
        <label className="label">
          Username
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="input"
            placeholder="Enter your username"
            required
          />
        </label>
        <label className="label">
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => {setEmail(e.target.value); setEmailValidation(
                validateEmail(e.target.value));}}
            className="input"
            placeholder="Enter your email"
            required
          />
        </label>
        {emailValidation && (
          <p className="error-message">{emailValidation}</p>
        )}
        <label className="label">
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => {setPassword(e.target.value); setpasswordValidation(
                validatePassword(e.target.value));}}
            className="input"
            placeholder="Enter your password"
            required
          />
        </label>
        {passwordValidation && (
          <p className="error-message">{passwordValidation}</p>
        )}
        <label className="label">
          Confirm Password
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => {setConfirmPassword(e.target.value); setmatchPassword(
                passwordMatch(e.target.value));}}
            className="input"
            placeholder="Confirm your password"
            required
          />
        </label>
        {matchPassword && (
          <p className="error-message">{matchPassword}</p>
        )}
        <button type="submit" className="button">
          Register
        </button>
        <footer className= "footer">
            Already have an account? <Link to="/login">Log in here</Link>
        </footer>
      </form>
    </div>
  </div>
  );
};

export default Registration;