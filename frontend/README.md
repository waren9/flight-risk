# Flight Risk Assessment System - Frontend

Modern React + TypeScript frontend for the Flight Risk Assessment System.

## Features

- **Modern UI**: Built with React 18, TypeScript, and Tailwind CSS
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Real-time Updates**: Live data updates and smooth animations
- **Interactive Components**: Modern cards, statistics, and data visualization
- **Error Handling**: Graceful error handling with user-friendly messages
- **Performance Optimized**: Efficient API calls and state management

## Tech Stack

- **React 18** - Modern React with hooks and concurrent features
- **TypeScript** - Type-safe development
- **Tailwind CSS** - Utility-first CSS framework
- **Framer Motion** - Smooth animations and transitions
- **Lucide React** - Beautiful icons
- **Axios** - HTTP client for API calls

## Getting Started

### Prerequisites

- Node.js 16+ and npm
- Backend Spring Boot application running on port 8080

### Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The application will open at `http://localhost:3000`

### Build for Production

```bash
# Create production build
npm run build

# The build files will be in the 'build' directory
```

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Header.tsx
│   │   ├── StatisticsCard.tsx
│   │   ├── RiskAssessment.tsx
│   │   ├── PredictionHistory.tsx
│   │   └── LoadingSpinner.tsx
│   ├── services/
│   │   └── api.ts
│   ├── types/
│   │   └── index.ts
│   ├── App.tsx
│   ├── index.tsx
│   └── index.css
├── package.json
├── tailwind.config.js
└── postcss.config.js
```

## API Integration

The frontend communicates with the Spring Boot backend via REST API:

- `POST /api/predict/{airport}` - Risk prediction
- `GET /api/predictions` - Get prediction history
- `GET /api/airports` - Get available airports
- `DELETE /api/predictions` - Clear all predictions
- `GET /api/statistics` - Get risk statistics

## Environment Variables

Create a `.env` file in the frontend directory:

```
REACT_APP_API_URL=http://localhost:8080/api
```

## Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App (not recommended)
