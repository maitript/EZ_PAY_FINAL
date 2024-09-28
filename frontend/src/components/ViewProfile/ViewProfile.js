import React, { useEffect, useState } from 'react';
import "./ViewProfile.css";

function Profile() {
  const [profileData, setProfileData] = useState(null);

  useEffect(() => {
    const fetchProfileData = async () => {
      const customerId = localStorage.getItem('customerId');
      if (!customerId) {
        window.location.href = '/login';
        return;
      }

      const response = await fetch(`http://localhost:8005/api/view-profile`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Key': customerId // Send customerId in headers
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch profile data');
      }

      const data = await response.json();
      setProfileData(data);
      console.table(profileData)

    };

    fetchProfileData();
  }, []);

  const isValidImage = (url) => {
    return url && (url.includes('.jpg') || url.includes('.jpeg') || url.includes('.png') || url.includes('.gif'));
  };

  return (
    <div className="profile-container">
      <h1>Profile Details</h1>
      {profileData ? (
        <div className="profile-details">
          <div><strong>Name:</strong> {profileData.name}</div>
          <div><strong>UPI ID:</strong> {profileData.upiId}</div>
          <div><strong>Email:</strong> {profileData.email}</div>
          <div><strong>Mobile Number:</strong> {profileData.mobileNumber}</div>
          <div><strong>Address:</strong> {profileData.address}</div>
          <div><strong>Date of Birth:</strong> {profileData.dob.split('T')[0]}</div>
          <div><strong>Gender:</strong> {profileData.gender}</div>
          <div><strong>Bank Account Number:</strong> {profileData.bankAccountNumber}</div>
          <div><strong>IFSC Code:</strong> {profileData.ifscCode}</div>
          <div><strong>Account Type:</strong> {profileData.accountType === 1 ? 'Savings Account' : "Current Account"}</div>
          
          <div className="profile-picture-container">
            <strong>Profile Picture:</strong>
            {isValidImage(profileData.profilePictureUrl) ? (
              <img 
                src={profileData.profilePictureUrl} 
                alt="Profile" 
                className="profile-picture"
              />
            ) : (
              <div className="profile-picture-placeholder">Image not Rendered</div>
            )}
          </div>
        </div>
      ) : (
        <p>No profile data available</p>
      )}
    </div>
  );
}

export default Profile;
