import React from 'react';
import Navbar from '../components/Navbar';

const UnauthorizedPage = () => (
  <div>
    <Navbar />
    <h2 style={{ textAlign: 'center', marginTop: '20px' }}>Access Denied</h2>
    <p>You do not have permission to access this page.</p>
  </div>
);

export default UnauthorizedPage;
