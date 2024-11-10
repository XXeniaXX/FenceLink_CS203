import React from 'react';
import { Outlet, Navigate } from 'react-router-dom';

const ProtectedRoute = ({ allowedRole }) => {
    const userRole = localStorage.getItem('userRole');
    return userRole === allowedRole ? <Outlet /> : <Navigate to="/unauthorisedpage" />;
  };

export default ProtectedRoute;
