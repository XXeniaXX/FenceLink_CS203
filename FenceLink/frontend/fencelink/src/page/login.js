import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './login.css'; // Import the CSS file

const Login = () => {
  // State variables to hold username and password
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the default form submission
    console.log('Username:', username);
    console.log('Password:', password);
    await handleLogin(); // Call the login function here
  };

  const handleLogin = async () => {
    const response = await fetch('http://localhost:5000/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    if (response.ok) {
      // Handle successful login (e.g., redirect user)
      alert('Login successful!'); // Display success message
      console.log('Login successful');
      // You can redirect or perform other actions here
    } else {
      // Handle error
      const errorText = await response.text();
      alert('Login failed: ' + errorText); // Display error message
      console.error('Login failed');
    }
  };

  return (
    <div className="container">
      <h1 className="header">LOGIN</h1>
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