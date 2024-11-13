import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn, fetchAuthSession} from 'aws-amplify/auth';
import { signOut } from 'aws-amplify/auth';
import './login.css'; // Import the CSS file

const Login = () => {
  const navigate = useNavigate();

  // State variables to hold username and password
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Email:', email);
    console.log('Password:', password);
  
    await handleLogin();
  }

  const handleSignOut = async () => {
    try {
      await signOut({ global: true });
      console.log('User signed out successfully');

      localStorage.removeItem('jwtToken');
      sessionStorage.clear();
      navigate('/login');
    } catch (error) {
      console.log('Error signing out: ', error);
    }
  };

  const handleLogin = async () => {
    try {
      // Try to sign in the user
      const user = await signIn({ 
        username: email, 
        password: password 
      });
      
      console.log("Login response user:", user);
  
      const cognitoTokens = (await fetchAuthSession()).tokens;
      const rawToken = cognitoTokens?.idToken?.toString();
  
      console.log("ID Token (JWT):", rawToken);
      localStorage.setItem('jwtToken', rawToken);
  
      // Call the backend to validate the token
      const response = await validateTokenOnLogin(rawToken);
  
      if (response) {
        // Parse the JSON response
        const data = await response.json();
  
        if (data.message === 'Token is valid') {
          // Store user information in localStorage
          localStorage.setItem('userId', data.userId);
          localStorage.setItem('name', data.username);
          localStorage.setItem('userRole', data.userRole);
  
          console.log('User ID:', data.userId);
          console.log('Username:', data.username);
          console.log('role:', data.userRole);
          localStorage.setItem("playerId", data.playerId);
  
          // Navigate to the main page
          if (data.userRole === 'admin') {
            navigate('/adminhomepage');
          } else {
            navigate('/playerhomepage');
          }

        } else {
          console.error('Invalid token:', data);
          localStorage.removeItem('jwtToken');
          await handleSignOut();
          navigate("/login");
        }
      } else {
        console.error('Token validation failed.');
        localStorage.removeItem('jwtToken');
        await handleSignOut();
        navigate("/login");
      }
      
    } catch (error) {
      console.error('Login failed:', error);
      await handleSignOut();
      alert('Login failed: ' + error.message);

    }
  };
  

  async function validateTokenOnLogin(token) {
    try {
      const response = await fetch('http://47.129.36.1:8080/api/auth/validate-token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ token }),
      });
  
      console.log('Response status:', response.status);
  
      if (response.status === 401) {
        console.log('Full response:', response);
        const text = await response.text();
        console.log('Response body:', text);
        return null; // Token validation failed
      }
  
      return response; // Return the response to be processed by handleLogin
    } catch (error) {
      console.error('Token validation failed:', error);
      await handleSignOut();
      return null;
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
          {'Login'}
        </button>
        <footer classname= "footer">
          Don't have an account? <Link to="/register">Register here</Link>
        </footer>
        
      </form>
    </div>
  </div>
  );

};


export default Login;