import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn, fetchAuthSession, confirmSignIn, signInWithRedirect} from 'aws-amplify/auth';
import './login.css'; // Import the CSS file
import googleLogo from './assets/googlelogo.png';

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

  const handleGoogleLogin = async () => {
    try {
      await signInWithRedirect({ provider: 'Google' });
      console.log('Redirecting to Google login...');
    } catch (error) {
      console.error('Google sign-in failed:', error);
      alert('Google sign-in failed: ' + error.message);
    }
  };

  const handleLogin = async () => {
    try {
      // Try to sign in the user
      const user = await signIn({ 
        username : email, 
        password : password });
      console.log("Login response user:", user);

      var cognitoTokens = (await fetchAuthSession()).tokens;
      let rawToken = cognitoTokens?.idToken?.toString();
      //let payload = cognitoTokens?.idToken?.payload;

      console.log("ID Token (JWT):", rawToken);
      localStorage.setItem('jwtToken', rawToken);

      validateTokenOnLogin(rawToken);
      
      
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed: ' + error.message);
    }
  };

  async function validateTokenOnLogin(token) {
    try {
      const response = await fetch('http://localhost:8080/api/auth/validate-token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // Include token in Authorization header
        },
        body: JSON.stringify({ token }),
      });

      console.log('Response status:', response.status);
    if (response.status === 401) {
      console.log('Full response:', response);
      const text = await response.text();
      console.log('Response body:', text);
    }
  
      const result = await response.text();
      if (result === 'Token is valid') {
        console.log('Token is valid');
        // Navigate to main page or update UI to show user is logged in
        navigate("/mainpage");
      } else {
        console.error('Invalid token:', result);
        // Redirect to login if invalid or expired
        localStorage.removeItem('idToken');
        navigate("/login");
      }
    } catch (error) {
      console.error('Token validation failed:', error);
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
        or
        <button onClick={handleGoogleLogin} className="google-login-button">
          <img
            src={googleLogo}
            alt="Google Logo"
            className="google-logo"
          />
          Login with Google
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