#!/usr/bin/env bash

# Flight Risk Assessment System - React App Launcher
echo "🚀 Starting Flight Risk Assessment System v2.0"
echo "================================================"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 16+ to continue."
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Please install npm to continue."
    exit 1
fi

echo "✅ Node.js version: $(node --version)"
echo "✅ npm version: $(npm --version)"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$ROOT_DIR/frontend"

# Navigate to frontend directory
cd "$FRONTEND_DIR"

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ Failed to install dependencies"
        exit 1
    fi
else
    echo "✅ Dependencies already installed"
fi

# Ensure jest-worker is present for CRA compatibility on newer Node
if [ ! -f "node_modules/jest-worker/build/index.js" ]; then
    echo "🔧 Adding jest-worker for CRA compatibility..."
    npm install --no-audit --no-fund --save-dev jest-worker@^27.5.1 --timeout=30000 || true
fi

# Run postinstall fix (shim) to ensure CRA can resolve jest-worker
echo "🔧 Running postinstall fixes..."
npm run postinstall --silent || echo "⚠️ Postinstall script not found, continuing..."

# Check if port 8080 is already in use
echo ""
echo "🔧 Checking backend status..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "✅ Backend already running on port 8080"
    BACKEND_PID=""
else
    echo "🚀 Starting Spring Boot backend..."
    cd "$ROOT_DIR"
    ./mvnw -q spring-boot:run &
    BACKEND_PID=$!
    
    # Wait for backend to start
    echo "⏳ Waiting for backend to initialize..."
    for i in {1..60}; do
      if curl -sSf http://localhost:8080/actuator/health >/dev/null 2>&1 || curl -sSf http://localhost:8080 >/dev/null 2>&1; then
        echo "✅ Backend is up"
        break
      fi
      sleep 1
      if [ "$i" -eq 60 ]; then
        echo "⚠️  Backend readiness timed out after 60s, continuing..."
      fi
    done
fi

# Start the React frontend
echo ""
echo "🎨 Starting React frontend..."
cd "$FRONTEND_DIR"
# Preflight environment check and auto-fix common issues
node ./scripts/check-setup.js || {
  echo "❌ Environment check failed. Fix errors before continuing."
  exit 1
}
npm start --silent &
FRONTEND_PID=$!

echo ""
echo "🎉 Flight Risk Assessment System is starting!"
echo "📱 Frontend: http://localhost:3000"
echo "🔧 Backend API: http://localhost:8080/api"
echo ""
echo "Press Ctrl+C to stop both services"

# Function to cleanup processes
cleanup() {
    echo ""
    echo "🛑 Shutting down services..."
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        echo "🔧 Backend stopped"
    fi
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null
        echo "🎨 Frontend stopped"
    fi
    echo "✅ Services stopped"
    exit 0
}

# Set trap to cleanup on script exit
trap cleanup SIGINT SIGTERM

# Wait for processes
wait
