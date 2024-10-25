import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signIn, fetchAuthSession, confirmSignIn} from 'aws-amplify/auth';
import './login.css'; // Import the CSS file

const Login = () => {
  const navigate = useNavigate();

  // State variables to hold username and password
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [otp, setOtp] = useState('');  // OTP state
  const [showOtpInput, setShowOtpInput] = useState(false);  // OTP input visibility
  const [userForOtp, setUserForOtp] = useState(null);  // Store user for OTP

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Email:', email);
    console.log('Password:', password);
    if (showOtpInput) {
      // Handle OTP confirmation if OTP input is shown
      await handleOtpConfirm();
    } else {
      await handleLogin();
    }
  };

  const postLogin = async () => {
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

  const handleLogin = async () => {
    try {
      // Try to sign in the user
      const user = await signIn({ 
        username : email, 
        password : password });
      console.log("Login response user:", user);

      if (user.challengeName === 'EMAIL_OTP') {
        console.log("OTP challenge triggered");
        // If OTP challenge is required, show OTP input field
        setShowOtpInput(true);
        setUserForOtp(user);  // Store the user object for OTP confirmation
      } else {
        // If no OTP is required, complete the login process
        completeLogin();
      }
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed: ' + error.message);
    }
  };

  const handleOtpConfirm = async () => {
    try {
      if (!otp) {
        alert('Please enter the OTP code');
        return;
      }
  
      // Confirm the OTP entered by the user
      const result = await confirmSignIn(userForOtp, otp, 'EMAIL_OTP'); // Use the OTP provided by the user
      console.log("OTP Confirmation Result:", result);
  
      // Complete the login after successful OTP confirmation
      completeLogin();
    } catch (error) {
      console.error('OTP confirmation failed:', error);
      alert('OTP confirmation failed: ' + error.message);
    }
  };

  const completeLogin = async () => {
    try {
      // Get the JWT token from Cognito
      const session = await fetchAuthSession();
      const idToken = session.getIdToken().getJwtToken(); // ID token (JWT)
      console.log('ID Token:', idToken);
      const accessToken = session.getAccessToken().getJwtToken(); // Access token
      console.log('Access Token:', accessToken);

      // Store the JWT token in localStorage or send it to your backend
      localStorage.setItem('idToken', idToken);

      // Navigate to the main page after successful login
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
        
        {showOtpInput && (
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
        )}

        <button type="submit" className="button">
          {showOtpInput ? 'Confirm OTP' : 'Login'}
        </button>
      
        <footer classname= "footer">
          Don't have an account? <Link to="/register">Register here</Link>
        </footer>

        
      </form>
      
    </div>
  );

};

export default Login;