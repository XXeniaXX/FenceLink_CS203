import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogContent,
    Typography,
    IconButton,
    Box,
    Avatar,
    Divider
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import FemaleIcon from '@mui/icons-material/Female';
import MaleIcon from '@mui/icons-material/Male';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import axios from 'axios';

const PlayerProfileDialog = ({ open, onClose, player }) => {
    const [rank, setRank] = useState('Loading...');

    useEffect(() => {
        const calculateRank = async () => {
            if (!player) return;

            try {
                const response = await axios.get('http://localhost:8080/api/players/all');
                const allPlayers = response.data;
                allPlayers.sort((a, b) => b.points - a.points);
                const playerRank = allPlayers.findIndex(p => p.id === player.id) + 1;
                setRank(playerRank);
            } catch (error) {
                console.error('Error fetching players for rank calculation:', error);
                setRank('N/A');
            }
        };
        calculateRank();
    }, [player]);

    if (!player) return null;

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth PaperProps={{ style: { borderRadius: 15 } }}>
            <Box display="flex" justifyContent="flex-end" padding="8px">
                <IconButton onClick={onClose}>
                    <CloseIcon />
                </IconButton>
            </Box>
            <DialogContent>
                <Box display="flex" flexDirection="column" alignItems="center" padding="16px">
                    <Avatar
                        src="/defaultpfp.png"
                        alt={player.name}
                        sx={{ width: 150, height: 150, marginBottom: '16px' }} // Increased size of the avatar
                    />
                    <Typography variant="h4" gutterBottom>{player.name || 'N/A'}</Typography>
                    <Typography variant="body1" sx={{ fontStyle: 'italic', marginBottom: '16px' }}>
                        "{player.bio || 'No bio available'}"
                    </Typography>
                    <Divider sx={{ width: '80%', marginBottom: '16px' }} />
                    <Box display="flex" alignItems="center" marginBottom="12px">
                        <LocationOnIcon sx={{ marginRight: '8px', fontSize: '30px' }} />
                        <Typography variant="body1">{player.country || 'N/A'}</Typography>
                    </Box>
                    <Box display="flex" alignItems="center" marginBottom="12px">
                        {player.gender === 'Female' ? (
                            <FemaleIcon sx={{ marginRight: '8px', fontSize: '30px', color: 'pink' }} />
                        ) : (
                            <MaleIcon sx={{ marginRight: '8px', fontSize: '30px', color: 'blue' }} />
                        )}
                        <Typography variant="body1">{player.gender || 'N/A'}</Typography>
                    </Box>
                    <Typography variant="body1" marginBottom="12px">Fencing Weapon: {player.fencingWeapon || 'N/A'}</Typography>
                    <Typography variant="body1" marginBottom="12px">World Rank: {rank}</Typography>
                    <Typography variant="body1">Elo Points: {player.points || 0}</Typography>
                </Box>
            </DialogContent>
        </Dialog>
    );
};

export default PlayerProfileDialog;






