import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './UpdateProfile.css';

const UpdateProfile = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    mobileNumber: '',
    address: '',
    profilePictureUrl: '',
  });
  const [newValues, setNewValues] = useState({
    newName: '',
    newEmail: '',
    newMobileNumber: '',
    newAddress: '',
    newprofilePictureUrl: '',
  });
  const [changedFields, setChangedFields] = useState({});
  const [message, setMessage] = useState('');
  const [password, setPassword] = useState('');
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordError, setPasswordError] = useState('');
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const customerId = localStorage.getItem('customerId');
        const response = await fetch(`http://localhost:8005/customers/by-id/${customerId}`, {
          method: 'GET',
        });
        if (!response.ok) throw new Error('Failed to fetch data');
        const data = await response.json();

        setFormData({
          name: data.name || '',
          email: data.email || '',
          mobileNumber: data.mobileNumber || '',
          address: data.address || '',
          profilePictureUrl: data.profilePictureUrl || '',
        });
      } catch (error) {
        console.error('Error fetching customer data:', error);
        setMessage('Error fetching data');
      }
    };

    fetchData();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewValues({
      ...newValues,
      [name]: value,
    });
    setChangedFields({
      ...changedFields,
      [name]: true, // Mark this field as changed
    });
  };

  const validate = () => {
    const errors = {};

    // Name validation
    if (!newValues.newName && !formData.name) {
      errors.newName = 'Name is required';
     } else if (newValues.newName && !/^[A-Za-z\s]+$/.test(newValues.newName)) {
       errors.newName = 'Name must contain only letters';
    }

    // Email validation
    if (!newValues.newEmail && !formData.email) {
      errors.newEmail = 'Email is required';
    } else if (newValues.newEmail && !/^\S+@\S+\.\S+$/.test(newValues.newEmail)) {
      errors.newEmail = 'Invalid email format';
    }

    // Mobile number validation
    if (!newValues.newMobileNumber && !formData.mobileNumber) {
      errors.newMobileNumber = 'Mobile number is required';
    } else if (newValues.newMobileNumber && !/^(\+91[\-\s]?)?(\91[\-\s]?)?[0]?(91)?[6789]\d{9}$/.test(newValues.newMobileNumber)) {
      errors.newMobileNumber = 'Invalid mobile number';
    }

    // Address validation
    if (!newValues.newAddress && !formData.address) {
      errors.newAddress = 'Address is required';
    }

    setErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (validate()) {
      setShowPasswordModal(true);
    }
  };

  const handleConfirmUpdate = async () => {
    try {
      const customerId = localStorage.getItem('customerId');
  
      // Verify password
      const verifyResponse = await fetch('http://localhost:8005/customers/verify-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ customerId, password }),
      });
  
      // Handle incorrect password (401 Unauthorized)
      if (verifyResponse.status === 401) {
        const errorText = await verifyResponse.text(); // Get the text response
        setPasswordError(errorText); // Set the password error message
        return; // Stop further execution if password verification fails
      }
  
      // Prepare updated data - Only changed fields
      const updatedData = {
        customerId,
        name: newValues.newName || formData.name,
        email: newValues.newEmail || formData.email,
        mobileNumber: newValues.newMobileNumber || formData.mobileNumber,
        address: newValues.newAddress || formData.address,
        profilePictureUrl: newValues.newprofilePictureUrl || formData.profilePictureUrl,
      };
  
      // Update customer details
      const updateResponse = await fetch(`http://localhost:8005/customers/update?customerId=${customerId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedData),
      });
  
      let responseData;
      const contentType = updateResponse.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        responseData = await updateResponse.json(); // Parse as JSON
      } else {
        responseData = await updateResponse.text(); // Parse as plain text
      }
  
      if (updateResponse.status === 409) { // Handle conflict status
        if (responseData.includes("Email already exists")) {
          setErrors((prevErrors) => ({
            ...prevErrors,
            newEmail: responseData,
          }));
        } else if (responseData.includes("Mobile number already exists")) {
          setErrors((prevErrors) => ({
            ...prevErrors,
            newMobileNumber: responseData,
          }));
        } else {
          setMessage(responseData);
        }
        return;
      }
  
      if (!updateResponse.ok) {
        setMessage(responseData);
        return;
      }
  
      setMessage('Profile updated successfully!');
      setShowPasswordModal(false); // Close modal after update
  
    } catch (error) {
      console.error('Error during update:', error);
      setMessage('Error updating profile');
    }
  };
  
  
  
  const getInputStyle = (fieldName) => {
    return changedFields[fieldName] ? { backgroundColor: '#f0e5e5' } : {}; // Darker shade on change
  };

  return (
    <div className="container">
      <h2>Update Profile</h2>
      {message && <div className="message">{message}</div>}
      <form onSubmit={handleSubmit}>
        <div className="input-container">
          <label>Name:</label>
          <input
            type="text"
            name="newName"
            value={newValues.newName}
            onChange={handleChange}
            placeholder={formData.name}
            style={getInputStyle('newName')}
            className={errors.newName ? 'error-input' : ''}
          />
          {errors.newName && <p className="error-message">{errors.newName}</p>}
        </div>
        <div className="input-container">
          <label>Email:</label>
          <input
            type="email"
            name="newEmail"
            value={newValues.newEmail}
            onChange={handleChange}
            placeholder={formData.email}
            style={getInputStyle('newEmail')}
            className={errors.newEmail ? 'error-input' : ''}
          />
          {errors.newEmail && <p className="error-message">{errors.newEmail}</p>}
        </div>
        <div className="input-container">
          <label>Mobile Number:</label>
          <input
            type="text"
            name="newMobileNumber"
            value={newValues.newMobileNumber}
            onChange={handleChange}
            placeholder={formData.mobileNumber}
            style={getInputStyle('newMobileNumber')}
            className={errors.newMobileNumber ? 'error-input' : ''}
          />
          {errors.newMobileNumber && <p className="error-message">{errors.newMobileNumber}</p>}
        </div>
        <div className="input-container">
          <label>Address:</label>
          <input
            type="text"
            name="newAddress"
            value={newValues.newAddress}
            onChange={handleChange}
            placeholder={formData.address}
            style={getInputStyle('newAddress')}
            className={errors.newAddress ? 'error-input' : ''}
          />
          {errors.newAddress && <p className="error-message">{errors.newAddress}</p>}
        </div>
        <div className="input-container">
          <label>Profile Picture URL:</label>
          <input
            type="text"
            name="newprofilePictureUrl"
            value={newValues.newprofilePictureUrl} // Start empty
            onChange={handleChange}
            placeholder={formData.profilePictureUrl} // Show current profile picture URL as a placeholder
            style={getInputStyle('newprofilePictureUrl')}
          />
        </div>
        <button className="button" type="submit">Update Profile</button>
        <button type="button" className="button" onClick={() => navigate('/view-profile')}>Cancel</button>
      </form>

      {showPasswordModal && (
        <div className="password-modal">
          <div className="modal-content">
            <h3 align="center">Confirm Update</h3>
            <p>Please enter your password to confirm the update:</p>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
            />
            {passwordError && <p className="error-message">{passwordError}</p>}
            <button className="modal-button confirm-btn" onClick={handleConfirmUpdate}>Confirm</button>
            <button className="modal-button cancel-btn" onClick={() => setShowPasswordModal(false)}>Cancel</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default UpdateProfile;
