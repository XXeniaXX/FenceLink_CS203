import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './login.css'; // Import the CSS file
import AdminNavBar from '../components/AdminNavBar';
import { useParams } from 'react-router-dom';
import RankingsCard from '../components/RankingsCard';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';

import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';

const MatchAdmin = () => {
  const [matches, setMatches] = useState([]);
  const [filteredMatches, setFilteredMatches] = useState([]);
  const [playerNames, setPlayerNames] = useState({});
  const [editingRow, setEditingRow] = useState(null);
  const [editValues, setEditValues] = useState({});
  const [editMode, setEditMode] = useState(null);
  const [tournamentName, setTournamentName] = useState('Loading...');
  const [currentRound, setCurrentRound] = useState(null);
  const [selectedRound, setSelectedRound] = useState('All');
  const [predefinedRounds, setPredefinedRounds] = useState(0);
  const [playerCount, setPlayerCount] = useState(0);
  const { tournamentId } = useParams();
  const [rankingsData, setRankingsData] = useState([]);
  const [showRankingsCard, setShowRankingsCard] = useState(false);
  const [isGenerateClicked, setIsGenerateClicked] = useState(false);
  const [isGenerateSLClicked, setIsGenerateSLClicked] = useState(false);
  const [isGenerateDEClicked, setIsGenerateDEClicked] = useState(false);
  const [isLoading, setIsLoading] = useState(false); 

  // Function to handle loading and perform the action

  const navigate = useNavigate();
  const handleLoadingAndAction = async (action) => {
    try {
      setIsLoading(true); // Show loading screen
      await action(); // Wait for the action to complete
    } catch (error) {
      console.error('Error during action:', error);
    } finally {
      refetchMatches();
      setIsLoading(false); // Hide loading screen once the action completes
    }
  };
  
  // Fetch player data and set initial states
  useEffect(() => {
    
    const fetchPlayerIds = async () => {
      try {
        const response = await fetch(`http://47.129.36.1:8080/api/players/${tournamentId}/get-all-players`);
        const playerIds = await response.json();
        const playerCount = playerIds.length;
        setPlayerCount(playerCount);

        let rounds = 2;
        if (playerCount >= 32) rounds += 6;
        else if (playerCount >= 16) rounds += 5;
        else if (playerCount >= 10) rounds += 4;
        setPredefinedRounds(rounds);
      } catch (error) {
        console.error('Error fetching player IDs:', error);
      }
    };
    fetchPlayerIds();
  }, [tournamentId]);

  useEffect(() => {
    if (matches.length > 0) {
      fetchPlayerNames(); // Automatically fetch names when matches state is updated
    }
  }, [matches]);

  // Synchronize filteredMatches with matches
  useEffect(() => {
    if (selectedRound === 'All') {
      setFilteredMatches(matches);
    } else {
      const round = parseInt(selectedRound, 10);
      const filtered = matches.filter(match => match.roundNo === round);
      setFilteredMatches(filtered);
    }
  }, [matches, selectedRound]);

  // Fetch tournament name
  useEffect(() => {
    const fetchTournamentName = async () => {
      try {
        const response = await fetch(`http://47.129.36.1:8080/api/tournaments/${tournamentId}`);
        const data = await response.json();
        setTournamentName(data.name || 'Unknown Tournament');
      } catch (error) {
        console.error('Error fetching tournament data:', error);
        setTournamentName('Unknown Tournament');
      }
    };
    fetchTournamentName();
  }, [tournamentId]);

  // Fetch matches and player names
  useEffect(() => {
    const fetchMatches = async () => {
      try {
        const response = await fetch(`http://47.129.36.1:8080/api/matches/tournament/${tournamentId}`);
        const data = await response.json();
        setMatches(data);
        setFilteredMatches(data);

        const maxRound = data.reduce((max, match) => Math.max(max, match.roundNo), 0);
        setCurrentRound(maxRound);

        const uniquePlayerIds = new Set(data.flatMap(match => [match.player1Id, match.player2Id]));
        const fetchPlayerNames = async () => {
          const names = {};
          await Promise.all(
            Array.from(uniquePlayerIds).map(async (id) => {
              try {
                const response = await fetch(`http://47.129.36.1:8080/api/players/${id}`);
                if (response.ok) {
                  const playerData = await response.json();
                  names[id] = playerData.name;
                } else {
                  names[id] = `Player ${id}`;
                }
              } catch (error) {
                console.error(`Error fetching player with ID ${id}:`, error);
                names[id] = `Player ${id}`;
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

  const handleEditClick = (match, mode) => {
    if (editingRow === match.matchId && editMode === mode) {
      setEditingRow(null);
      setEditMode(null);
      setEditValues({});
    } else {
      setEditingRow(match.matchId);
      setEditMode(mode);
      setEditValues({
        date: match.date,
        startTime: match.startTime,
        endTime: match.endTime,
        player1points: match.player1points,
        player2points: match.player2points,
      });
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditValues((prevValues) => ({
      ...prevValues,
      [name]: value,
    }));
  };

  const handleSaveClick = async (matchId) => {
    try {
      let response;
      let updatedMatchData;
  
      if (editMode === 'score') {
        // Validate input values
        if (!editValues.player1points || !editValues.player2points) {
          alert('Both player points must be filled before saving.');
          return;
        }
        if (isNaN(editValues.player1points) || isNaN(editValues.player2points)) {
          alert('Please enter valid numeric values for both players\' points.');
          return;
        }
  
        // Make the network request to update scores
        response = await fetch(`http://47.129.36.1:8080/api/matches/${matchId}/results?player1Points=${editValues.player1points}&player2Points=${editValues.player2points}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
        });
      } else if (editMode === 'dateTime') {
        const updatedFields = {};
        if (editValues.date) updatedFields.date = editValues.date;
        if (editValues.startTime) updatedFields.startTime = `${editValues.startTime}:00`;
        if (editValues.endTime) updatedFields.endTime = `${editValues.endTime}:00`;
  
        response = await fetch(`http://47.129.36.1:8080/api/matches/${matchId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedFields),
        });
      }
  
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      // Determine response format
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        updatedMatchData = await response.json();
        console.log('Updated match data (JSON):', updatedMatchData);
      } else {
        const message = await response.text();
        console.log('Server response (string):', message);
        alert(message);
        return; // Exit function if there's no JSON data to update state with
      }
  
      // Update the state directly
      setMatches((prevMatches) =>
        prevMatches.map((match) =>
          match.matchId === matchId ? { ...match, ...updatedMatchData } : match
        )
      );
  
      // Reset editing state
      setEditingRow(null);
      setEditMode(null);
      setEditValues({});
    } catch (error) {
      console.error('Error updating match:', error);
    }
  };
  

  const refetchMatches = async () => {
    try {
      const response = await fetch(`http://47.129.36.1:8080/api/matches/tournament/${tournamentId}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch matches. Status: ${response.status}`);
      }
      const data = await response.json();
      console.log('Re-fetched matches data:', data);
      setMatches([...data]); // Ensure new array reference
    } catch (error) {
      console.error('Error fetching match data:', error);
    }
  };

  const fetchPlayerNames = async () => {
    try {
      const uniquePlayerIds = new Set(matches.flatMap(match => [match.player1Id, match.player2Id]));
      const names = {};
  
      await Promise.all(
        Array.from(uniquePlayerIds).map(async (id) => {
          try {
            const response = await fetch(`http://47.129.36.1:8080/api/players/${id}`);
            if (response.ok) {
              const playerData = await response.json();
              names[id] = playerData.name; // Assuming `name` is a field in the response
            } else {
              console.warn(`Failed to fetch player with ID ${id}: Status ${response.status}`);
              names[id] = `Player ${id}`; // Fallback value
            }
          } catch (error) {
            console.error(`Error fetching player with ID ${id}:`, error);
            names[id] = `Player ${id}`; // Fallback value in case of error
          }
        })
      );
  
      // Update state with fetched names
      setPlayerNames(names);
    } catch (error) {
      console.error('Error fetching player names:', error);
    }
  };
  


  const generateMatches = async () => {
    if (isGenerateClicked) return;
    setIsGenerateClicked(true);
  
    try {
      const response = await fetch(`http://47.129.36.1:8080/api/matches/generate/${tournamentId}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      const data = await response.json();
      setMatches(data); // Update state with new match data
      fetchPlayerNames();
    } catch (error) {
      console.error('Error generating matches:', error);
    } finally {
      setIsGenerateClicked(false);

    }
  };
  
  
  const generateSLMatches = async () => {
    if (isGenerateSLClicked) return; // Prevent multiple clicks
    setIsGenerateSLClicked(true);
  
    try {
      const response = await fetch(`http://47.129.36.1:8080/api/matches/generate-seeding?tournamentId=${tournamentId}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      const contentType = response.headers.get('content-type');
      let data;
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
        setMatches(data); // Update state with new match data
        fetchPlayerNames();
      }

    } catch (error) {
      console.error('Error generating seeding matches:', error);
    } finally {
      setIsGenerateSLClicked(false);
    }
  };
  
  const generateDEMatches = async () => {
    if (isGenerateDEClicked) return; // Prevent multiple clicks
    setIsGenerateDEClicked(true);
  
    try {
      const response = await fetch(`http://47.129.36.1:8080/api/matches/generate-de-matches/${tournamentId}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      const contentType = response.headers.get('content-type');
      let data;
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
        setMatches(data); // Update state with new match data
        fetchPlayerNames();
      }
    } catch (error) {
      console.error('Error generating DE matches:', error);
    } finally {
      setIsGenerateDEClicked(false);
    }
  };
  
  const promoteNextMatches = async () => {
    try {
      const response = await fetch(`http://47.129.36.1:8080/api/matches/promote-players/${tournamentId}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      const contentType = response.headers.get('content-type');
      let data;
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
        setMatches(data); // Update state with new match data
        fetchPlayerNames();
      }
    } catch (error) {
      console.error('Error promoting players:', error);
    }
  };


  const fetchPlayerNamesForRankings = async (rankings) => {
    try {
      const uniquePlayerIds = new Set(rankings.map(rank => rank.playerId));
      const names = {};
  
      await Promise.all(
        Array.from(uniquePlayerIds).map(async (id) => {
          try {
            const response = await fetch(`http://47.129.36.1:8080/api/players/${id}`);
            if (response.ok) {
              const playerData = await response.json();
              names[id] = playerData.name; // Assuming `name` is a field in the response
            } else {
              names[id] = `Player ${id}`; // Fallback value
            }
          } catch (error) {
            console.error(`Error fetching player with ID ${id}:`, error);
            names[id] = `Player ${id}`; // Fallback value in case of error
          }
        })
      );
  
      return names;
    } catch (error) {
      console.error('Error fetching player names for rankings:', error);
      return {};
    }
  };
  
  const fetchCurrentRankings = async () => {
    try {
      const response = await fetch(`http://47.129.36.1:8080/api/match-rank/tournament/${tournamentId}`);
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
  
      const matchRanks = await response.json();
      const playerNamesMap = await fetchPlayerNamesForRankings(matchRanks);
  
      // Attach player names to matchRanks
      const matchRanksWithNames = matchRanks.map(rank => ({
        ...rank,
        playerName: playerNamesMap[rank.playerId] || `Player ${rank.playerId}`
      }));
  
      setRankingsData(matchRanksWithNames);
      setShowRankingsCard(true); // Show the card
    } catch (error) {
      console.error('Error fetching match rankings:', error);
      alert('An error occurred while fetching match rankings.');
    }
  };
  

  const handleRoundChange = (event) => {
    const selectedValue = event.target.value;
    setSelectedRound(selectedValue);
    if (selectedValue === 'All') {
      setFilteredMatches(matches);
    } else {
      const round = parseInt(selectedValue, 10);
      const filtered = matches.filter(match => match.roundNo === round);
      setFilteredMatches(filtered);
    }
  };

  const roundOptions = Array.from(new Set(matches.map(match => match.roundNo))).sort((a, b) => a - b);
  const allWinnersAssigned = matches.every(match => match.winner !== null);

  return (
    <div>
       {isLoading && (
        <div className="loading-screen"> {/* Style this class in CSS */}
          <Box sx={{ display: 'flex' }}>
            <CircularProgress size="3rem"/>
            <h2>Generating Matches...</h2>
          </Box>
          
        </div>
      )}
      <AdminNavBar />
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
          <Button variant="contained" color="primary" onClick={() => handleLoadingAndAction(generateMatches)}>
            Generate Matches
          </Button>
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
                <TableCell>Matchup (Player 1 VS Player 2)</TableCell>
                <TableCell>Player 1 Points</TableCell>
                <TableCell>Player 2 Points</TableCell>
                <TableCell>Winner</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
            {console.log('Rendering matches:', matches)}
              {filteredMatches.map((match) => (
                <TableRow key={match.matchId}>
                  <TableCell>{`Round ${match.roundNo}`}</TableCell>
                  <TableCell>
                    {editingRow === match.matchId && editMode === 'dateTime' ? (
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
                    {editingRow === match.matchId && editMode === 'dateTime' ? (
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
                    {editingRow === match.matchId && editMode === 'dateTime' ? (
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
                    {`${playerNames[match.player1Id] || 'Loading...'} VS ${playerNames[match.player2Id] || 'Loading...'}`}
                  </TableCell>
                  <TableCell>
                    {editingRow === match.matchId && editMode === 'score' ? (
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
                    {editingRow === match.matchId && editMode === 'score' ? (
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
                  <TableCell>{playerNames[match.winner] || 'Loading...'}</TableCell>
                  <TableCell>
                    {editingRow === match.matchId ? (
                      <>
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={() => handleSaveClick(match.matchId)}
                        >
                          Save
                        </Button>
                        <Button
                          variant="outlined"
                          onClick={() => {
                            setEditingRow(null);
                            setEditValues({});
                            setEditMode(null);
                          }}
                        >
                          Cancel
                        </Button>
                      </>
                    ) : (
                      <>
                        <Button
                          variant="outlined"
                          onClick={() => handleEditClick(match, 'score')}
                          disabled={match.winner !== null} // Disable if winner is not null
                        >
                          Edit Scores
                        </Button>
                        <Button
                          variant="outlined"
                          onClick={() => handleEditClick(match, 'dateTime')}
                        >
                          Edit Date/Time
                        </Button>
                      </>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      <div style={{ marginTop: '20px' }}>
        {/* Button to Show Current Rankings */}
              {(currentRound === 2 || currentRound === 3) && (
                <Button
                  variant="contained"
                  color="info"
                  onClick={fetchCurrentRankings}
                  style={{
                    marginTop: '80px',
                    position: 'absolute',
                    top: '10px',
                    left: '10px',
                  }}
                >
                  Current Rankings
                </Button>
              )}
              {/* Conditionally render the RankingsCard */}
              {showRankingsCard && (
                <RankingsCard
                  rankingsData={rankingsData}
                  onClose={() => setShowRankingsCard(false)}
                />
              )}
        {currentRound === 1 && (playerCount === 16 || playerCount === 32 || playerCount === 17 || playerCount === 33) ? (
          <>
            <Button variant="contained" color="secondary" onClick={() => handleLoadingAndAction(generateDEMatches)}>
              Generate DE Matches
            </Button>
            <p>Due to the exact amount of players, the seeding round (Round 2) is skipped and Direct Elimination matches (Round 3) will be generated.</p>
          </>
        ) : (
          allWinnersAssigned && (
            <>
              {currentRound === 1 && (
                <Button variant="contained" color="primary" onClick={() => handleLoadingAndAction(generateSLMatches)}>
                  Generate Seeding Match
                </Button>
              )}
              {currentRound === 2 && (
                <Button variant="contained" color="secondary" onClick={() => handleLoadingAndAction(generateDEMatches)} style={{ marginLeft: '10px' }}>
                  Generate Match
                </Button>
              )}
              {currentRound >= 3 && currentRound !== predefinedRounds && (
                <Button variant="contained" color="success" onClick={() => handleLoadingAndAction(promoteNextMatches)} style={{ marginLeft: '10px' }}>
                  Generate Match
                </Button>
              )}
               {currentRound === predefinedRounds && (
                <p>All match results have been generated.</p>
              )}

              

              
            </>
          )
        )}
      </div>
    </div>
  );
};

export default MatchAdmin;








