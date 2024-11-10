import React, { useState, useEffect } from 'react';
import { signOut, getCurrentUser } from 'aws-amplify/auth';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file
import Navbar from '../components/Navbar';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';

const UpcomingTournaments = () => {
   

   
    
    return (
        <div>
            {/* Render the NavBar component */}
                <Navbar />
        </div>
    )
}
export default UpcomingTournaments;