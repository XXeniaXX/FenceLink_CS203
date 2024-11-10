import React, { useState, useEffect } from 'react';
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

const MatchAdmin = () => {
  const [matches, setMatches] = useState([]);
  const [playerNames, setPlayerNames] = useState({});
  const [editingRow, setEditingRow] = useState(null);
  const [editValues, setEditValues] = useState({});
  const [tournamentName, setTournamentName] = useState('Loading...');
  const [currentRound, setCurrentRound] = useState(null);
  const [filteredMatches, setFilteredMatches] = useState([]);
  const [selectedRound, setSelectedRound] = useState('All');
  const [predefinedRounds, setPredefinedRounds] = useState(0); // State to store the number of predefined rounds
  const tournamentId = 1; // Assumed value for demonstration purposes
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

  const handleEditClick = (match) => {
    setEditingRow(match.matchId);
    setEditValues({
      date: match.date,
      startTime: match.startTime,
      endTime: match.endTime,
      player1points: match.player1points,
      player2points: match.player2points,
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditValues((prevValues) => ({
      ...prevValues,
      [name]: value,
    }));
  };

  const handleSaveClick = (matchId) => {
    // Check if both points are provided
    if (!editValues.player1points || !editValues.player2points) {
      alert('Both player points must be filled before saving.');
      return; // Prevent the save operation
    }

    // Prepare the updated fields for date/time
    const updatedFields = {};
    if (editValues.date) updatedFields.date = editValues.date;
    if (editValues.startTime) {
      updatedFields.startTime = `${editValues.startTime}:00`; // Ensure format HH:mm:ss
    }
    if (editValues.endTime) {
      updatedFields.endTime = `${editValues.endTime}:00`; // Ensure format HH:mm:ss
    }

    // Only make the request if there are fields to update
    if (Object.keys(updatedFields).length > 0) {
      fetch(`http://localhost:8080/api/matches/${matchId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedFields),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
          }
          return response.json(); // Assuming the response is JSON
        })
        .then((updatedMatchData) => {
          if (updatedMatchData) {
            // Update state with the new match data
            setMatches((prevMatches) =>
              prevMatches.map((match) =>
                match.matchId === matchId ? { ...match, ...updatedMatchData } : match
              )
            );
            setEditValues({});
          }
          setEditingRow(null); // Exit editing mode
        })
        .catch((error) => {
          console.error('Error updating match:', error);
        });
    }
  };

  const generateMatches = () => {
    fetch(`http://localhost:8080/api/matches/generate/${tournamentId}`, {
      method: 'POST',
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log('Match generation response:', data);
        // Optionally, re-fetch the matches or update the state with the new data
        alert('First Round match generated successfully!');
      })
      .catch((error) => {
        console.error('Error generating matches:', error);
      });
  };

  const generateSLMatches = () => {
    fetch(`http://localhost:8080/api/matches/generate-seeding?tournamentId=${tournamentId}`, {
      method: 'POST',
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        // Optionally handle any response data if needed
        alert('Seeding matches generated successfully!');
      })
      .catch((error) => {
        console.error('Error generating seeding matches:', error);
      });
  };

  const generateDEMatches = () => {
    fetch(`http://localhost:8080/api/matches/generate-de-matches/${tournamentId}`, {
      method: 'POST',
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        alert('DE matches generated successfully!');
      })
      .catch((error) => {
        console.error('Error generating DE matches:', error);
      });
  };

  const promotePlayer = () => {
    fetch(`http://localhost:8080/api/matches/promote-players/${tournamentId}`, {
      method: 'POST',
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.text(); // Assuming the response is plain text
      })
      .then((message) => {
        alert(message);
      })
      .catch((error) => {
        console.error('Error promoting players:', error);
      });
  };

  const isEditingPointsAllowed = (match) => {
    return !(match.winner && editValues.player1points && editValues.player2points);
  };
  
  const handleRoundChange = (event) => {
    const selectedValue = event.target.value;
    setSelectedRound(selectedValue);
  
    // Debugging log
    console.log("Selected Round:", selectedValue);
  
    if (selectedValue === 'All') {
      setFilteredMatches(matches);
    } else {
      const round = parseInt(selectedValue, 10);
      // Ensure round value is parsed correctly and matches have correct roundNo values
      const filtered = matches.filter(match => match.roundNo === round);
      console.log("Filtered Matches for Round:", round, filtered); // Debugging log
      setFilteredMatches(filtered);
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
          <Button variant="contained" color="primary" onClick={generateMatches}>
            Generate Matches
          </Button>
        </div>
      ) : (
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} aria-label="match table">
            <TableHead>
              <TableRow>
                <TableCell>Date</TableCell>
                <TableCell>Start Time</TableCell>
                <TableCell>End Time</TableCell>
                <TableCell>Player 1 Points</TableCell>
                <TableCell>Player 2 Points</TableCell>
                <TableCell>Matchup (Player 1 VS Player 2)</TableCell>
                <TableCell>Round No</TableCell>
                <TableCell>Winner</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredMatches.map((match) => (
                <TableRow key={match.matchId}>
                  <TableCell>
                    {editingRow === match.matchId ? (
                      <TextField
                        name="date"
                        type="date"
                        value={editValues.date || ''}
                        onChange={handleInputChange}
                      />
                    ) : (
                      match.date
                    )}
                  </TableCell>
                  <TableCell>
                    {editingRow === match.matchId ? (
                      <TextField
                        name="startTime"
                        type="time"
                        value={editValues.startTime || ''}
                        onChange={handleInputChange}
                      />
                    ) : (
                      match.startTime
                    )}
                  </TableCell>
                  <TableCell>
                    {editingRow === match.matchId ? (
                      <TextField
                        name="endTime"
                        type="time"
                        value={editValues.endTime || ''}
                        onChange={handleInputChange}
                      />
                    ) : (
                      match.endTime
                    )}
                  </TableCell>
                  <TableCell>
                    {editingRow === match.matchId && isEditingPointsAllowed(match) ? (
                      <TextField
                        name="player1points"
                        type="number"
                        value={editValues.player1points || ''}
                        onChange={handleInputChange}
                      />
                    ) : (
                      match.player1points
                    )}
                  </TableCell>
                  <TableCell>
                    {editingRow === match.matchId && isEditingPointsAllowed(match) ? (
                      <TextField
                        name="player2points"
                        type="number"
                        value={editValues.player2points || ''}
                        onChange={handleInputChange}
                      />
                    ) : (
                      match.player2points
                    )}
                  </TableCell>
                  <TableCell>
                    {`${playerNames[match.player1Id] || 'Loading...'} VS ${playerNames[match.player2Id] || 'Loading...'}`}
                  </TableCell>
                  <TableCell>{`Round ${match.roundNo}`}</TableCell>
                  <TableCell>{playerNames[match.winner] || 'Loading...'}</TableCell>
                  <TableCell>
                    {editingRow === match.matchId ? (
                      <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleSaveClick(match.matchId)}
                      >
                        Save
                      </Button>
                    ) : (
                      <Button
                        variant="outlined"
                        color="secondary"
                        onClick={() => handleEditClick(match)}
                      >
                        Edit
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      <div style={{ marginTop: '20px' }}>
        {(currentRound === 1 && <Button variant="contained" color="primary" onClick={generateSLMatches}>
          Generate Seedling Match
        </Button>
        )}
        {(currentRound === 2 &&<Button variant="contained" color="secondary" onClick={generateDEMatches} style={{ marginLeft: '10px' }}>
          Generate Match
        </Button>
        )}
        {(currentRound > 3 && currentRound !== predefinedRounds && <Button variant="contained" color="success" onClick={promotePlayer} style={{ marginLeft: '10px' }}>
          Generate Match
        </Button>
        )}
        {currentRound === predefinedRounds && (
        <p>All match results has been generated.</p>
      )}
      </div>
    </div>
  );
};

export default MatchAdmin;






