import React from 'react';
import { Navigate } from 'react-router-dom';
import Swal from 'sweetalert2';

const PrivateRoute = ({ children }) => {
  const isLoggedIn = localStorage.getItem('customerId') !== null; // Check if the user is logged in
  const isProfileInfoSet = localStorage.getItem('isProfileInfoSet') === 'true'; // Check profile info flag

  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }

  if (!isProfileInfoSet && window.location.pathname !== '/initial-profile-update') {
    // If the profile info is not set, allow only access to profile update
      Swal.fire({
        title: 'Warning!',
        text: 'Please complete your profile information before proceeding',
        icon: 'warning',
        confirmButtonText: 'Okay',
      });
      //alert('Please complete your profile information before proceeding.');
      return <Navigate to="/initial-profile-update" replace />;
  }

  return children;
};

export default PrivateRoute;
