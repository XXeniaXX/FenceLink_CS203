import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn, fetchAuthSession, confirmSignIn} from 'aws-amplify/auth';
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

      navigate("/mainpage");
      
      
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed: ' + error.message);
    }
  };

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
        
        {/* {showOtpInput && (
          <label className="label">
            OTP
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              className="input"
              placeholder="Enter the OTP sent to your email"
              required
            />
          </label>
        )} */}

        <button type="submit" className="button">
          {'Login'}
        </button>
      
        <footer classname= "footer">
          Don't have an account? <Link to="/register">Register here</Link>
        </footer>

        
      </form>
      
    </div>
  );

};


export default Login;