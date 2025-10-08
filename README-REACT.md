# ✈️ Flight Risk Assessment System v2.0 - React Edition

A modern, responsive web application for aviation safety analytics built with React + TypeScript frontend and Spring Boot backend.

## 🚀 What's New

### Complete UI Rebuild
- **Removed JavaFX**: Eliminated all JavaFX components and FXML files
- **Modern React Frontend**: Built from scratch with React 18 + TypeScript
- **Responsive Design**: Works perfectly on desktop, tablet, and mobile
- **Professional UI**: Clean, modern interface with smooth animations

### Tech Stack Upgrade
- **Frontend**: React 18, TypeScript, Tailwind CSS, Framer Motion
- **Backend**: Spring Boot 2.7.18 with enhanced REST API
- **Styling**: Tailwind CSS with custom design system
- **Icons**: Lucide React for beautiful, consistent icons
- **Animations**: Framer Motion for smooth transitions

## 🎯 Key Features

### 🎨 Modern User Interface
- **Card-based Layout**: Clean, organized information display
- **Gradient Design**: Professional blue-purple color scheme
- **Smooth Animations**: Fade transitions and hover effects
- **Interactive Elements**: Responsive buttons and form controls
- **Loading States**: Professional loading spinners and progress indicators

### 📊 Enhanced Analytics
- **Real-time Statistics**: Live updating dashboard with key metrics
- **Risk Assessment**: Comprehensive multi-factor risk analysis
- **Prediction History**: Sortable table with detailed prediction records
- **Weather Integration**: Live OpenWeatherMap API integration
- **Error Handling**: Graceful error management with user feedback

### ⚡ Performance Optimized
- **Fast Loading**: Optimized React components and lazy loading
- **Efficient API Calls**: Smart caching and request optimization
- **Responsive Design**: Smooth performance across all device sizes
- **Memory Management**: Efficient state management and cleanup

## 🏃‍♂️ Quick Start

### Option 1: Automated Setup (Recommended)
```bash
# Navigate to project directory
cd /home/loke/Downloads/cusor/flight-risk-1

# Run the automated setup script
./run-react-app.sh
```

This script will:
- Install Node.js dependencies
- Start the Spring Boot backend
- Launch the React frontend
- Open your browser automatically

### Option 2: Manual Setup

#### Backend (Spring Boot)
```bash
# Start the Spring Boot API server
./mvnw spring-boot:run
```

#### Frontend (React)
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm start
```

## 🌐 Access Points

- **Web Application**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Health Check**: http://localhost:8080/api/health

## 📱 User Interface

### Dashboard Overview
- **Statistics Cards**: Total predictions, risk level breakdown
- **Risk Assessment Panel**: Airport selection and prediction interface
- **Prediction History**: Comprehensive table with all past assessments
- **Real-time Updates**: Live data refresh and automatic updates

### Key Components

#### 1. Header
- Professional gradient design with system branding
- Real-time status indicators
- Responsive navigation elements

#### 2. Statistics Dashboard
- **Total Predictions**: Overall system usage metrics
- **Risk Breakdown**: High/Medium/Low risk distribution
- **Animated Counters**: Smooth number transitions
- **Progress Bars**: Visual risk level indicators

#### 3. Risk Assessment
- **Airport Selection**: Dropdown with available airports
- **Predict Button**: Animated prediction trigger
- **Results Display**: Comprehensive risk breakdown
- **Weather Information**: Live weather conditions
- **Confidence Scoring**: Assessment reliability metrics

#### 4. Prediction History
- **Sortable Table**: All past predictions with timestamps
- **Risk Color Coding**: Visual risk level indicators
- **Delete Actions**: Individual prediction removal
- **Bulk Operations**: Clear all history functionality
- **Responsive Design**: Mobile-friendly table layout

## 🔧 API Endpoints

### Prediction Management
- `POST /api/predict/{airport}` - Generate risk prediction
- `GET /api/predictions` - Retrieve prediction history
- `DELETE /api/predictions` - Clear all predictions
- `DELETE /api/predictions/{id}` - Delete specific prediction

### Data Retrieval
- `GET /api/airports` - Get available airports
- `GET /api/statistics` - Get system statistics
- `GET /api/weather/{airport}` - Get weather data
- `GET /api/health` - System health check

## 🎨 Design System

### Color Palette
- **Primary**: Blue gradient (#667eea to #764ba2)
- **Success**: Green (#48bb78)
- **Warning**: Orange (#ed8936)
- **Danger**: Red (#f56565)
- **Neutral**: Gray scale for text and backgrounds

### Typography
- **Font Family**: Inter (Google Fonts)
- **Weights**: 300, 400, 500, 600, 700
- **Responsive Sizing**: Scales appropriately across devices

### Components
- **Cards**: Rounded corners, subtle shadows, hover effects
- **Buttons**: Gradient backgrounds, smooth transitions
- **Forms**: Clean inputs with focus states
- **Tables**: Striped rows, sortable headers, responsive design

## 🔄 Data Flow

1. **User Interaction**: Select airport and click predict
2. **API Request**: Frontend sends POST request to backend
3. **Risk Calculation**: Backend processes multiple risk factors
4. **Database Storage**: Prediction saved to H2 database
5. **Response**: Comprehensive risk data returned to frontend
6. **UI Update**: Statistics and history updated automatically

## 🛠️ Development

### Project Structure
```
flight-risk-1/
├── frontend/                 # React application
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── services/        # API integration
│   │   ├── types/          # TypeScript definitions
│   │   └── App.tsx         # Main application
│   ├── public/             # Static assets
│   └── package.json        # Dependencies
├── src/main/java/          # Spring Boot backend
├── run-react-app.sh        # Automated launcher
└── README-REACT.md         # This file
```

### Adding New Features

#### Frontend Components
1. Create new component in `frontend/src/components/`
2. Add TypeScript interfaces in `frontend/src/types/`
3. Integrate with API services in `frontend/src/services/`
4. Update main App.tsx as needed

#### Backend Endpoints
1. Add new controller methods in `src/main/java/.../controller/`
2. Create service methods in `src/main/java/.../service/`
3. Update entity models if needed
4. Add CORS configuration for new endpoints

## 🚀 Deployment

### Development
- Frontend: `npm start` (http://localhost:3000)
- Backend: `./mvnw spring-boot:run` (http://localhost:8080)

### Production Build
```bash
# Build React app
cd frontend
npm run build

# Copy build files to Spring Boot static resources
cp -r build/* ../src/main/resources/static/

# Build and run Spring Boot with embedded frontend
cd ..
./mvnw clean package
java -jar target/flightrisk-0.0.1-SNAPSHOT.jar
```

## 🎯 Benefits of React Migration

### User Experience
- **Faster Loading**: Modern React optimizations
- **Better Responsiveness**: Mobile-first design approach
- **Smoother Interactions**: Framer Motion animations
- **Improved Accessibility**: Better keyboard and screen reader support

### Developer Experience
- **Modern Tooling**: React DevTools, TypeScript support
- **Component Reusability**: Modular component architecture
- **Easy Maintenance**: Clear separation of concerns
- **Scalable Architecture**: Easy to add new features

### Technical Advantages
- **Cross-Platform**: Works on any device with a web browser
- **No Installation**: Instant access via web browser
- **Easy Deployment**: Single JAR file with embedded frontend
- **Better Performance**: Optimized bundle size and loading

## 🔮 Future Enhancements

- **Real-time Notifications**: WebSocket integration for live updates
- **Advanced Charts**: Interactive data visualization with Chart.js
- **User Authentication**: Login system with role-based access
- **Export Features**: PDF reports and CSV data export
- **Mobile App**: React Native mobile application
- **Dark Mode**: Theme switching capability

---

**Flight Risk Assessment System v2.0** - Modern aviation safety analytics with React + Spring Boot architecture.
