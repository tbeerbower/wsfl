# Fantasy Running League Web Application - Development Roadmap

## Overview
This roadmap outlines the key stages and tasks involved in building the Fantasy Running League web application. The app will allow users to play a fantasy sport based on the results of a series of races hosted by a local running club. 

## 1. Initial Setup

### 1.1 Project Setup
- [ ] Set up version control (Git).
- [ ] Set up a project management board (e.g., Trello, Jira).
- [ ] Define core technologies: **Frontend** (React, Vue, or Angular), **Backend** (Node.js/Express or Django), **Database** (PostgreSQL or MongoDB).

### 1.2 Google Authentication
- [ ] Integrate Google authentication (OAuth 2.0).
- [ ] Set up user profile creation and management.

## 2. Database and Models

### 2.1 Database Design
- [ ] Design database schema for:
  - **Users**: Profiles, teams, and player history.
  - **Runners**: Roster of all runners, attributes (e.g., gender).
  - **Teams**: User-created teams and their members.
  - **Races**: Information on each race, including date, results, and scoring.
  - **Matchups**: Weekly team pairings.
  - **Draft**: Order, picks, and status tracking.
  - **Scores**: Weekly scores, standings, and playoff brackets.

### 2.2 Implement Models
- [ ] Set up models based on database schema.
- [ ] Create relationships between models (e.g., Users, Teams, Runners, and Races).

## 3. Draft System

### 3.1 Draft Logic
- [ ] Implement a draft system that:
  - Initializes a draft with all available runners.
  - Assigns a random order to teams.
  - Reverses the order in alternating rounds.
- [ ] Create an API to manage draft picks and selections.

### 3.2 Draft User Interface
- [ ] Design UI for the draft where users can:
  - View available runners.
  - See draft order and team picks.
  - Make selections.

## 4. Race Schedule and Weekly Matchups

### 4.1 Scheduling
- [ ] Implement functionality to create a season schedule with weekly matchups.
- [ ] Ensure avoidance of duplicate team matchups.

### 4.2 Matchup UI
- [ ] Display weekly matchups on the user dashboard.
- [ ] Include options to view team rosters and results from previous weeks.

## 5. Scoring System

### 5.1 Race Results Input
- [ ] Develop an interface for entering or uploading race results, including runner names and gender placements.

### 5.2 Scoring Logic
- [ ] Implement scoring logic that:
  - Adds gender placement to the team’s weekly score.
  - Applies a penalty score (e.g., 100) for absent runners.
- [ ] Calculate weekly team scores based on drafted runners' performances.

### 5.3 Score Display
- [ ] Display weekly scores, standings, and win/loss records.

## 6. Playoffs and Championship

### 6.1 Playoff Qualification
- [ ] Implement logic to determine top teams based on regular season standings.

### 6.2 Playoff Brackets
- [ ] Set up a playoff bracket for the last two races.
- [ ] Implement logic for scoring playoffs and determining the final winner.

### 6.3 Championship Display
- [ ] Display playoff results and winning team on a dedicated championship page.

## 7. User Interface and User Experience

### 7.1 General UI Design
- [ ] Design a user-friendly layout for the dashboard, draft, matchups, and scoring.
- [ ] Include an intuitive navigation menu.

### 7.2 Profile and Team Management
- [ ] Enable users to view and edit profiles.
- [ ] Allow users to view and manage team rosters.

## 8. Testing and QA

### 8.1 Unit Testing
- [ ] Write unit tests for core functionality (e.g., draft, scoring, and authentication).

### 8.2 Integration Testing
- [ ] Test the integration between the frontend, backend, and database.

### 8.3 User Testing
- [ ] Conduct beta testing with a small group of users.
- [ ] Collect feedback and refine features.

## 9. Deployment

### 9.1 Production Environment
- [ ] Set up a production environment (e.g., AWS, DigitalOcean, or Heroku).
- [ ] Implement CI/CD for automated deployment.

### 9.2 Launch
- [ ] Announce and launch the application for general use.
- [ ] Monitor and optimize performance and user feedback post-launch.

## 10. Future Enhancements

### 10.1 Additional Features
- [ ] Add features like leaderboards, runner stats, and advanced analytics.
- [ ] Implement a mobile-friendly UI or mobile app.

### 10.2 Customization Options
- [ ] Allow leagues to set custom scoring rules and draft formats.

---

**This roadmap provides a structured approach to development, allowing for scalability and flexibility as the project progresses.**
