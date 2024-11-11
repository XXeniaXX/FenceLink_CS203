import React, { useState, useEffect } from 'react'; 
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file
import Navbar from '../components/Navbar';
import { useParams } from 'react-router-dom';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

const MatchUser = () => {
  const [matches, setMatches] = useState([]);
  const [playerNames, setPlayerNames] = useState({});
  const [tournamentName, setTournamentName] = useState('Loading...');
  const [currentRound, setCurrentRound] = useState(null);
  const [filteredMatches, setFilteredMatches] = useState([]);
  const [selectedRound, setSelectedRound] = useState('All');
  const [predefinedRounds, setPredefinedRounds] = useState(0); // State to store the number of predefined rounds
  const { tournamentId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch the list of player IDs who registered for the tournament
    const fetchPlayerIds = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/players/${tournamentId}/get-all-players`);
        const playerIds = await response.json();
        const playerCount = playerIds.length;

        // Calculate predefined rounds based on player count
        let rounds = 2; // Minimum rounds: Round 1 + Seeding Round
        if (playerCount >= 32) {
          rounds += 6;
        } else if (playerCount >= 16) {
          rounds += 5;
        } else if (playerCount >= 10) {
          rounds += 4;
        }
        setPredefinedRounds(rounds);
      } catch (error) {
        console.error('Error fetching player IDs:', error);
      }
    };
    fetchPlayerIds();
  }, [tournamentId]);

  useEffect(() => {
    // Fetch tournament name based on tournamentId
    const fetchTournamentName = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/tournaments/${tournamentId}`);
        const data = await response.json();
        setTournamentName(data.name || 'Unknown Tournament');
      } catch (error) {
        console.error('Error fetching tournament data:', error);
        setTournamentName('Unknown Tournament');
      }
    };
    fetchTournamentName();
  }, [tournamentId]);

  useEffect(() => {
    // Fetch match data for the tournament
    const fetchMatches = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/matches/tournament/${tournamentId}`);
        const data = await response.json();
        setMatches(data);
        setFilteredMatches(data);

        // Calculate the current round based on the max round number in matches
        const maxRound = data.reduce((max, match) => Math.max(max, match.roundNo), 0);
        setCurrentRound(maxRound);

        // Fetch unique player names
        const uniquePlayerIds = new Set(data.flatMap(match => [match.player1Id, match.player2Id]));
        const fetchPlayerNames = async () => {
          const names = {};
          await Promise.all(
            Array.from(uniquePlayerIds).map(async (id) => {
              try {
                const response = await fetch(`http://localhost:8080/api/players/${id}`);
                if (response.ok) {
                  const playerData = await response.json();
                  names[id] = playerData.name;
                } else {
                  names[id] = `Player ${id}`; // Fallback
                }
              } catch (error) {
                console.error(`Error fetching player with ID ${id}:`, error);
                names[id] = `Player ${id}`; // Fallback
              }
            })
          );
          setPlayerNames(names);
        };
        fetchPlayerNames();
      } catch (error) {
        console.error('Error fetching match data:', error);
      }
    };
    fetchMatches();
  }, [tournamentId]);

  const handleRoundChange = (event) => {
    const selectedValue = event.target.value;
    setSelectedRound(selectedValue);

    if (selectedValue === 'All') {
      setFilteredMatches(matches);
    } else {
      const round = parseInt(selectedValue, 10);
      setFilteredMatches(matches.filter(match => match.roundNo === round));
    }
  };

  // Generate options for the dropdown based on available rounds
  const roundOptions = Array.from(new Set(matches.map(match => match.roundNo))).sort((a, b) => a - b);

  return (
    <div>
      <Navbar />
      <h2>{tournamentName}</h2>
      <div>
        <label htmlFor="round-filter">Filter by Round:</label>
        <select id="round-filter" value={selectedRound} onChange={handleRoundChange}>
          <option value="All">All</option>
          {roundOptions.map(round => (
            <option key={round} value={round}>
              Round {round}
            </option>
          ))}
        </select>
      </div>
      {matches.length === 0 ? (
        <div>
          <p>No match has been created yet.</p>
        </div>
      ) : (
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} aria-label="match table">
            <TableHead>
              <TableRow>
                <TableCell>Round No</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>Start Time</TableCell>
                <TableCell>End Time</TableCell>
                <TableCell>Player 1 Points</TableCell>
                <TableCell>Player 2 Points</TableCell>
                <TableCell>Matchup (Player 1 VS Player 2)</TableCell>
                
                <TableCell>Winner</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredMatches.map((match) => (          
                <TableRow key={match.matchId}>
                    <TableCell>{`Round ${match.roundNo}`}</TableCell>
                  <TableCell>{match.date}</TableCell>
                  <TableCell>{match.startTime}</TableCell>
                  <TableCell>{match.endTime}</TableCell>
                  <TableCell>{match.player1points}</TableCell>
                  <TableCell>{match.player2points}</TableCell>
                  <TableCell>
                    {`${playerNames[match.player1Id] || 'Loading...'} VS ${playerNames[match.player2Id] || 'Loading...'}`}
                  </TableCell>
                  
                  <TableCell>{playerNames[match.winner] || 'Loading...'}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      <div style={{ marginTop: '20px' }}>
        {currentRound === predefinedRounds && (
          <p>Matches have ended.</p>
        )}
      </div>
    </div>
  );
};

export default MatchUser;
