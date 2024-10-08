// Import necessary packages
const express = require('express');
const mongoose = require('mongoose');
const dotenv = require('dotenv');
const cors = require('cors');

// Load environment variables from .env file
dotenv.config();

const app = express();
const PORT = process.env.PORT || 5000;

// Use cors middleware
app.use(cors()); // Enable CORS
app.use(express.json()); // Parse incoming JSON requests

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URI)
  .then(() => {
    console.log('MongoDB connected successfully!');
  })
  .catch(err => {
    console.error('MongoDB connection error:', err);
  });

  const User = require('./models/User'); // Import the User model
  const bcrypt = require('bcrypt'); // Import bcrypt for password hashing

  // route to handle user login
app.post('/login', async (req, res) => {
    try {
      const { username, password } = req.body;
      console.log('Attempting to log in with username:', username); // Log the username
      
      // Find the user by username
    const user = await User.findOne({ username });
    if (!user) {
      return res.status(400).send('User not found');
    }

    // Compare the provided password with the stored hashed password
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).send('Invalid password');
    }

    // Login successful
    res.status(200).send('Login successful');
  } catch (error) {
    res.status(500).send('Server error: ' + error.message);
  }
  });


  app.post('/register', async (req, res) => {
    try {
      const { username, password, email, roles } = req.body;
  
      // Hash the password
      const salt = await bcrypt.genSalt(10);
      const hashedPassword = await bcrypt.hash(password, salt);
  
      const newUser = new User({ username, password: hashedPassword, email, roles });
      await newUser.save();
      res.status(201).send('User registered successfully');
    } catch (error) {
      res.status(400).send('Error registering user: ' + error.message);
    }
  });

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});