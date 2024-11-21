import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { Calendar } from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import './playerHomepage.css';
import Navbar from '../components/Navbar';
import { Link } from 'react-router-dom';
import playerTournamentCardImage from './assets/playertournamentcard.jpg';

// Configure axios defaults
axios.defaults.baseURL = 'http://localhost:8080';

const PlayerHomePage = () => {
  const [player, setPlayer] = useState(null);
  const [upcomingTournaments, setUpcomingTournaments] = useState([]);
  const [availableTournaments, setAvailableTournaments] = useState([]);
  const [tournamentDates, setTournamentDates] = useState([]);
  const [joinedTournaments, setJoinedTournaments] = useState([]);
  const playerId = localStorage.getItem('playerId');

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
        const upcomingResponse = await axios.get(`/api/players/${playerId}/tournaments/upcoming`);
        const availableResponse = await axios.get(`/api/players/${playerId}/tournaments/available`);

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

  const normalizeDate = (date) => {
    const normalized = new Date(date);
    normalized.setHours(0, 0, 0, 0);
    return normalized;
  };

  return (
    <div className="home-page">
      <Navbar />
      <div className="content-wrapper">
        <h1 className="welcome">{`Welcome, ${player ? player.name : 'Guest'}`}</h1>
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
                <p>No upcoming registered tournaments.</p>
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
                            <Card.Text>Weapon Type: {tournament.weaponType}</Card.Text>
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
                <Link to="/profilepage" className="view-all">View All</Link>
              )}
            </Col>
          </Row>
        </div>

        {/* Available Tournaments Section */}
        <div className="section">
          <h2 className='join-tournaments'>Recommended Tournaments</h2>
          <Row>
            {availableTournaments.length === 0 ? (
              <p>No available tournaments to join.</p>
            ) : (
              availableTournaments.slice(0, 3).map(({ tournament }) => (
                tournament ? (
                  <Col md={4} key={tournament.id}>
                    <Card className="mb-3 shadow-sm tournament-card">
                      <Card.Img variant="top" src={playerTournamentCardImage} alt="Tournament Image" />
                      <Card.Body>
                        <Card.Title className="join-tournament-name">{tournament.name}</Card.Title>
                        <Card.Text><strong>Location:</strong> {tournament.location}</Card.Text>
                        <Card.Text><strong>Tournament Type:</strong> {tournament.tournamentType}</Card.Text>
                        <Card.Text><strong>Age Group:</strong> {tournament.ageGroup}</Card.Text>
                        <Card.Text><strong>Weapon Type:</strong> {tournament.weaponType}</Card.Text>
                        <Card.Text><strong>Gender:</strong> {tournament.genderType}</Card.Text>
                        <Card.Text><strong>Start Date:</strong> {new Date(tournament.startDate).toLocaleDateString()}</Card.Text>
                        <Card.Text><strong>End Date:</strong> {new Date(tournament.endDate).toLocaleDateString()}</Card.Text>
                        <Card.Text><strong>Vacancy:</strong> {tournament.vacancy}</Card.Text>
                        <Card.Text><strong>Register By:</strong> {tournament.registrationDate}</Card.Text>
                      </Card.Body>
                    </Card>
                  </Col>
                ) : null
              ))
            )}
          </Row>
          {availableTournaments.length > 0 && (
            <Link to="/usertournament" className="view-more">View All Available Tournaments</Link>
          )}
        </div>
      </div>
    </div>
  );
};

export default PlayerHomePage;
