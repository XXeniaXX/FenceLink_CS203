import React from 'react';
import './RankingsCard.css'; // Import the CSS file

const RankingsCard = ({ rankingsData, onClose }) => {
  // Sort the rankings data in ascending order of currentRank
  const sortedRankings = [...rankingsData].sort((a, b) => a.currentRank - b.currentRank);

  return (
    <div className="rankings-card">
      <div className="rankings-card-header">
        <h3>Current Rankings</h3>
        <button
          onClick={onClose}
          className="rankings-card-close-button"
        >
          &times;
        </button>
      </div>
      <div className="rankings-card-table-container">
        <table className="rankings-card-table">
          <thead>
            <tr>
              <th>Rank</th>
              <th>Player Name</th>
              <th>Wins</th>
              <th>Losses</th>
            </tr>
          </thead>
          <tbody>
            {sortedRankings.map((rank) => (
              <tr key={rank.playerId}>
                <td>{rank.currentRank}</td> {/* Display the sorted rank */}
                <td className={rank.eliminated ? 'eliminated' : ''}>
                  {rank.playerName ? rank.playerName : `Player ${rank.playerId}`}
                </td>
                <td>{rank.winCount}</td>
                <td>{rank.lossCount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default RankingsCard;



