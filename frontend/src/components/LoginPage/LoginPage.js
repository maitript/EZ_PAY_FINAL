import { useState } from 'react';
import './LoginPage.css'; 
import { useNavigate } from 'react-router-dom'; // Import useNavigate for redirection
import Swal from 'sweetalert2';

export default function LoginForm() {
//  localStorage.setItem('temp', "0000");
localStorage.clear()

  const navigate = useNavigate(); 
  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); 
    const payload = {
      userId,
      password,
    };

    const response = await fetch('http://localhost:8005/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      
      const responseData = await response.json();

      const customerId = responseData.customerId;
      // Store customerId in localStorage
      localStorage.setItem('customerId', customerId);
      localStorage.setItem("isProfileInfoSet",responseData.profileInfoSetStatus);

      if (responseData.profileInfoSetStatus) {

         // Extract customerId from response
    
          navigate('/profileHome'); // Redirect to ProfileHome if profile is set
        } 
        else {
        navigate('/initial-profile-update'); // Redirect to Initial Profile Update page
      }
    } 
    else {
      console.log(response)
      const errorMsg = await response.json();
      setError(errorMsg);
      Swal.fire({
        title: 'Erro!',
        text: "Error: " + errorMsg.message,
        icon: 'error',
        confirmButtonText: 'Okay',
      });
    }
  }
  return (
    <>
     <h1 className="welcome-title">Hello Dear Customer</h1>
      <div className="form-container">
       
        <form onSubmit={handleSubmit}>
          <div className="login-info">
            <div className="form-group">
              <label htmlFor="userId" className="login-info-label">User Id</label>
              <input
                type="text"
                className="login-info-control"
                id="userId"
                placeholder="your user ID"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
              />
            
              <label htmlFor="password" className="login-info-label">Password</label>
              <input
                type="password"
                className="login-info-control"
                id="password"
                placeholder="******"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>
          <div className="form-buttons">
            <button type="submit" className="form-button">Login</button>
            <button type="reset" className="form-button" onClick={() => { setUserId(''); setPassword(''); }}>Reset</button>
          </div>
          <p className="forgot-password-link" onClick={() => navigate('/password/forgot')} style={{ cursor: 'pointer', color: 'blue' }}>
        Forgot Password?
      </p>
        </form>
      </div>
    </>
  );
}
