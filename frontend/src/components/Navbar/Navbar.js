import React from 'react';
import Swal from 'sweetalert2';
import { useNavigate, Link } from 'react-router-dom';
import './Navbar.css'; // Import the CSS
import ezpayLogo from "../../assets/images/EZPAY-LOGO.png";
import userHead from "../../assets/images/userIcon.png";


function Navbar() {
  const navigate = useNavigate();
  const customerId = localStorage.getItem('customerId');

  const handleLogout = () => {
    localStorage.clear();
    Swal.fire({
      title: 'Success!',
      text: 'You have successfully logged out.',
      icon: 'success',
      confirmButtonText: 'Okay',
    });
    //alert("You have successfully logged out.");
    navigate('/login'); // Redirect to login page
  };

  return (
    <nav className="navbar navbar-expand-lg">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">
          <img src={ezpayLogo} alt="Logo" className="brand-logo-image" />
          EZPay
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarSupportedContent">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link className="nav-link active" aria-current="page" to="/">Home</Link>
            </li>
            {customerId && (
  <>
    <li className="nav-item">
      <Link className="nav-link active" to="/view-profile">View Profile</Link>
    </li>
    <li className="nav-item">
      <Link className="nav-link active" to="/update-profile">Update Profile</Link>
    </li>
  </>
)}
          </ul>

          <div className="dropdown">
            <button
              className="btn dropdown-toggle"
              type="button"
              id="dropdownMenuButton"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <img src={userHead} className='user-logo'/>
              Profile
            </button>

            <ul className="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownMenuButton">
              {customerId && (
                <>
                  <li>
                    <Link className="dropdown-item" to="/view-profile">View Profile</Link>
          
                  </li>
                  <div className="dropdown-divider"></div> {/* Optional divider */}
                  <li>
                    <a className="dropdown-item" href="/update-profile">Update Profile</a>
                  </li>
                  <div className="dropdown-divider"></div> {/* Optional divider */}
                  <li>
                    <button className="dropdown-item" onClick={handleLogout}>
                      Logout
                    </button>
                  </li>
                </>
              )}
            </ul>
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
