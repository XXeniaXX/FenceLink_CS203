import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn } from 'aws-amplify/auth';
import './login.css'; // Import the CSS file

const Login = () => {
  const navigate = useNavigate();

  // State variables to hold username and password
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the default form submission
    console.log('Email:', email);
    console.log('Password:', password);
    await handleLogin(); // Call the login function here
  };

  const handleLogin = async () => {
    const response = await fetch('http://localhost:5000/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({email, password }),
    });

    if (response.ok) {
      // Handle successful login (e.g., redirect user)
      alert('Login successful!'); // Display success message
      console.log('Login successful');
      localStorage.setItem('username', email);
      navigate("/mainpage");
      // You can redirect or perform other actions here
    } else {
      // Handle error
      const errorText = await response.text();
      alert('Login failed: ' + errorText); // Display error message
      console.error('Login failed');
    }
  };

  //don't delete this, im trying to figure this out
  // async function handleSignIn() {
  //   try {
  //     const user = await signIn({ 
  //       username : email, 
  //       password : password });

  //       if (user.challengeName === 'EMAIL_OTP') {
  //         // Store the user object to be used in the OTP confirmation step
  //         localStorage.setItem('username', JSON.stringify(user));
  //         navigate("/otpcheck2");
  //       } else {
  //         // If no OTP challenge, go straight to main page
  //         navigate("/mainpage");
  //       }

  //   } catch (error) {
  //     console.log('error signing in', error);
  //   }
  // }

  return (
    <div className="container">
      <h1 className="header">LOGIN</h1>
      <form onSubmit={handleSubmit} className="form">
        <label className="label">
          Email
          <input
            type="text"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="input"
            placeholder="Enter your email"
            required
          />
        </label>
        <label className="label">
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="input"
            placeholder="Enter your password"
            required
          />
        </label>
        <label className = "label2">
          <Link to="/forgotpassword">Forgot Password?</Link>
        </label>
        <button type="submit" className="button">
          Login
        </button>
        <footer classname= "footer">
          Don't have an account? <Link to="/register">Register here</Link>
        </footer>
      </form>
      
    </div>
  );
};

export default Login;