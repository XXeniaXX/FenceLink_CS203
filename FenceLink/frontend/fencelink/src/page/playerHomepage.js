import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { Calendar } from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import './playerHomepage.css';
import Navbar from '../components/Navbar';
import { Link } from 'react-router-dom';

// Configure axios defaults
axios.defaults.baseURL = 'http://localhost:8080';

const PlayerHomePage = () => {
  const [player, setPlayer] = useState(null); // Local state for player data
  const [upcomingTournaments, setUpcomingTournaments] = useState([]);
  const [availableTournaments, setAvailableTournaments] = useState([]);
  const [tournamentDates, setTournamentDates] = useState([]);
  const [joinedTournaments, setJoinedTournaments] = useState([]); // Track joined tournaments
  const playerId = 203; // Use dynamic player ID if available

  // Fetch player data
  useEffect(() => {
    const fetchPlayer = async () => {
      try {
        const response = await axios.get(`/api/players/${playerId}`);
        setPlayer(response.data);
      } catch (error) {
        console.error("Error fetching player data:", error);
      }
    };

    fetchPlayer();
  }, [playerId]);

  // Fetch tournaments data
  useEffect(() => {
    const fetchTournaments = async () => {
      try {
        const upcomingResponse = await axios.get(`/api/players/${playerId}/upcoming-registered-tournaments`);
        const availableResponse = await axios.get(`/api/players/${playerId}/upcoming-tournaments`);

        setUpcomingTournaments(upcomingResponse.data);
        setAvailableTournaments(availableResponse.data);

        setTournamentDates(
          upcomingResponse.data.map((t) => ({
            startDate: new Date(t.startDate),
            endDate: new Date(t.endDate),
          }))
        );
      } catch (error) {
        console.error("Error fetching tournament data:", error);
      }
    };

    fetchTournaments();
  }, [playerId]);

  // Handle Join tournament
  const handleJoin = (tournamentId) => {
    axios.post(`/api/players/${playerId}/register/${tournamentId}`)
      .then(response => {
        setJoinedTournaments([...joinedTournaments, tournamentId]);
      })
      .catch(error => {
        console.error('Error joining tournament:', error);
      });
  };

// // DUMMY DATA
//   const [player, setPlayer] = useState({ name: 'John Doe' }); // Dummy player data
//   const [upcomingTournaments, setUpcomingTournaments] = useState([]);
//   const [availableTournaments, setAvailableTournaments] = useState([]);
//   const [tournamentDates, setTournamentDates] = useState([]);
//   const [joinedTournaments, setJoinedTournaments] = useState([]); // Track joined tournaments

//   useEffect(() => {
//     // Dummy data for upcoming tournaments
//     const dummyUpcomingTournaments = [
//       {
//         tournament: {
//           id: 1,
//           name: 'City Fencing Championship',
//           location: 'New York',
//           startDate: '2024-12-01',
//           endDate: '2024-12-03',
//         },
//       },
//       {
//         tournament: {
//           id: 2,
//           name: 'Winter Invitational',
//           location: 'Los Angeles',
//           startDate: '2024-12-10',
//           endDate: '2024-12-12',
//         },
//       },
//       {
//         tournament: {
//           id: 3,
//           name: 'National Open',
//           location: 'Chicago',
//           startDate: '2024-12-20',
//           endDate: '2024-12-22',
//         },
//       },
//     ];

//     // Dummy data for available tournaments
//     const dummyAvailableTournaments = [
//       {
//         tournament: {
//           id: 4,
//           name: 'Spring Fencing Festival',
//           location: 'Houston',
//           startDate: '2025-03-15',
//           endDate: '2025-03-17',
//         },
//       },
//       {
//         tournament: {
//           id: 5,
//           name: 'Junior Nationals',
//           location: 'San Francisco',
//           startDate: '2025-04-05',
//           endDate: '2025-04-07',
//         },
//       },
//       {
//         tournament: {
//           id: 6,
//           name: 'Regional Challenge',
//           location: 'Miami',
//           startDate: '2025-04-20',
//           endDate: '2025-04-22',
//         },
//       },
//     ];

//     setUpcomingTournaments(dummyUpcomingTournaments);
//     setAvailableTournaments(dummyAvailableTournaments);
//     setTournamentDates(
//       dummyUpcomingTournaments.map((t) => ({
//         startDate: new Date(t.tournament.startDate),
//         endDate: new Date(t.tournament.endDate),
//       }))
//     );
//   }, []);

  // // Handle Join tournament (dummy implementation)
  // const handleJoin = (tournamentId) => {
  //   setJoinedTournaments([...joinedTournaments, tournamentId]);
  //   console.log(`Joined tournament with ID: ${tournamentId}`);
  // };

// Helper function to normalize dates to midnight for comparison
const normalizeDate = (date) => {
  const normalized = new Date(date);
  normalized.setHours(0, 0, 0, 0);
  return normalized;
};

  return (
    <div className="home-page">
      <Navbar />
      <div className="content-wrapper">
        <h1 className="text-center">{`Welcome, ${player ? player.name : 'Guest'}`}</h1>
        <p className="text-center text-muted">Happy fencing!</p>

        {/* Upcoming Tournaments Section */}
        <h2 className='player-upcoming'>Your Upcoming Tournaments</h2>
        <div className="section">
          <Row>
            <Col md={4} className="calendar-column mb-3">
              <Calendar
                tileClassName={({ date, view }) => {
                  if (view === 'month') {
                    const normalizedDate = normalizeDate(date);
                    return tournamentDates.some(({ startDate, endDate }) => {
                      const normalizedStart = normalizeDate(startDate);
                      const normalizedEnd = normalizeDate(endDate);
                      return normalizedDate >= normalizedStart && normalizedDate <= normalizedEnd;
                    })
                      ? 'highlight'
                      : null;
                  }
                  return null;
                }}
              />
            </Col>
            <Col md={8}>
              {upcomingTournaments.length === 0 ? (
                <p>No upcoming tournaments registered.</p>
              ) : (
                upcomingTournaments.slice(0, 3).map((tournament) => (
                  tournament ? (
                    <Card key={tournament.id} className="mb-3 shadow-sm tournament-card">
                      <Card.Body>
                        <Row>
                          <Col xs={3} className="tournament-date">
                            <div className='date-top'>
                              <span className="date-day">{new Date(tournament.startDate).getDate()}</span>
                              <span className="date-month">{new Date(tournament.startDate).toLocaleString('default', { month: 'short' })}</span>
                            </div>
                            <span className="date-year">{new Date(tournament.startDate).getFullYear()}</span>
                          </Col>
                          <Col xs={9} className="tournament-details">
                            <Card.Title className='upcoming-tournament-name'>{tournament.name}</Card.Title>
                            <Card.Text>{tournament.location}</Card.Text>
                            <Card.Text className="tournament-duration">
                              {new Date(tournament.startDate).toLocaleDateString()} - {new Date(tournament.endDate).toLocaleDateString()}
                            </Card.Text>
                          </Col>
                        </Row>
                      </Card.Body>
                    </Card>
                  ) : null
                ))
              )}
              {upcomingTournaments.length > 3 && (
                <Link to="/profilepage" className="view-more">View All</Link>
              )}
            </Col>
          </Row>
        </div>

        {/* Available Tournaments Section */}
        <div className="section">
          <h2 className='join-tournaments'>Join Tournaments</h2>
          <Row>
            {availableTournaments.length === 0 ? (
              <p>No available tournaments to join.</p>
            ) : (
              availableTournaments.slice(0, 3).map(({ tournament }) => (
                tournament ? (
                  <Col md={4} key={tournament.id}>
                    <Card className="mb-3 shadow-sm tournament-card">
                      <Card.Body>
                        <Card.Title className="join-tournament-name">{tournament.name}</Card.Title>
                        <Card.Text>{tournament.location}</Card.Text>
                        <Card.Text>{new Date(tournament.startDate).toLocaleDateString()} - {new Date(tournament.endDate).toLocaleDateString()}</Card.Text>
                        <Button variant="primary" onClick={() => handleJoin(tournament.id)}>Join</Button>
                      </Card.Body>
                    </Card>
                  </Col>
                ) : null
              ))
            )}
          </Row>
          {availableTournaments.length > 0 && (
            <Link to="/usertournament" className="view-more">View More</Link>
          )}
        </div>
      </div>
    </div>
  );
};

export default PlayerHomePage;
