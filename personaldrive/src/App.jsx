import { BrowserRouter, Routes , Route } from 'react-router-dom'
import Register from './components/Register'
import Login from './components/Login'
import Home from './components/Home'

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Register" element={<Register />} />
        <Route path="/Login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
