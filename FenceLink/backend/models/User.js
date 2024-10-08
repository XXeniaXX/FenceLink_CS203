const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  roles: { type: String, enum: ['user', 'admin'], default: 'user' } // only user or admin
});

const User = mongoose.model('User', userSchema);

module.exports = User;