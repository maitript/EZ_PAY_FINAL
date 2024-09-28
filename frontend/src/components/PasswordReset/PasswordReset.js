import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import './PasswordReset.css';
import Swal from 'sweetalert2';

/**
 * PasswordReset component allows users to reset their password using a token 
 * provided via email. It includes form validation and communicates with the 
 * backend API to handle the password reset process.
 * Validation methods: @Aman Rauth
 * @Author: Riya Shah
 */
const PasswordReset = () => {
    const [newPassword, setNewPassword] = useState(''); // State for the new password input
    const [confirmPassword, setConfirmPassword] = useState(''); // State for confirming the new password
    const [message, setMessage] = useState(''); // State for displaying messages to the user
    const [isResetting, setIsResetting] = useState(false); // State to indicate if the password reset process is ongoing
    const [passwordError, setPasswordError] = useState(''); // State for password validation error messages
    const API_URL = process.env.REACT_APP_SPRING_API_URL; // Base API URL from environment variables
    const location = useLocation(); // Hook to access the current location (URL)
    const token = new URLSearchParams(location.search).get('token'); // Extract the token from the URL parameters
    const navigate = useNavigate(); // Hook for programmatic navigation

    // Effect to check if the token is present; if not, redirect the user
    useEffect(() => {
        if (!token) {
            const alertUser = async () => {
                Swal.fire({
                    title: 'Invalid access!',
                    text: 'Please use the link provided in your email.',
                    icon: 'warning',
                    confirmButtonText: 'Okay',
                  });
                //alert(' Please use the link provided in your email.'); // Alert the user
                navigate('/login'); // Redirect to the login page
            };
            alertUser();
        }
    }, [token, navigate]);

    /**
     * Validates the new password against a specific pattern.
     * The password must include letters, numbers, special characters,
     * and be at least 6 characters long.
     * 
     * @param {string} password - Password to validate.
     * @returns {boolean} - True if valid, false otherwise.
     */
    const validatePassword = (password) => {
        const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/; // Regex pattern for password validation
        return passwordPattern.test(password); // Test the password against the pattern
    };

    /**
     * Handles the password reset process when the form is submitted.
     * 
     * @param {Event} e - Form submit event.
     */
    const handleResetPassword = async (e) => {
        e.preventDefault(); // Prevent the default form submission

        // Reset error messages
        setPasswordError('');

        // Validate the new password
        if (!validatePassword(newPassword)) {
            setPasswordError("Password must include letters, numbers, and special characters, and be at least 6 characters long.");
            return; // Exit if validation fails
        }
        if (newPassword !== confirmPassword) {
            Swal.fire({
                title: 'Access Denied!',
                text: 'Passwords do not match. Please try again.',
                icon: 'error',
                confirmButtonText: 'Okay',
              });
            //alert("Passwords do not match. Please try again."); // Alert user if passwords do not match
            return; // Exit if passwords do not match
        }

        setIsResetting(true); // Indicate that the reset process is ongoing
        try {
            // Send POST request to reset the password using the token and new password
            const response = await fetch(`${API_URL}/api/password/reset?token=${encodeURIComponent(token)}&newPassword=${encodeURIComponent(newPassword)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            // Check if the response is not OK
            if (!response.ok) {
                throw new Error(await response.text()); // Throw error with response text
            }

            const data = await response.text(); // Get success message from response
            setMessage(data); // Set message for user feedback
            Swal.fire({
                title: 'Success!',
                text: data,
                icon: 'success',
                confirmButtonText: 'Okay',
              });
            //alert(data); // Show success message
            navigate('/login'); // Redirect to login page after successful reset
        } catch (error) {
            setMessage(error.message || 'Error occurred while resetting password.'); // Set error message if something goes wrong
        } finally {
            setIsResetting(false); // Reset the loading state
        }
    };

    // Render the component
    return (
        <div className='passwordReset'>
            <h2 id="passwordReseth2">Reset Password</h2>
            <form onSubmit={handleResetPassword} id="resetForm">
                <input
                    type="password"
                    placeholder="New Password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)} // Update state on input change
                    required
                />

                {passwordError && <p className="error-message">{passwordError}</p>} {/* Display password validation error */}
                <input
                    type="password"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)} // Update state on input change
                    required
                />
                <button type="submit" disabled={isResetting}> {/* Disable button while resetting */}
                    {isResetting ? 'Resetting...' : 'Reset Password'}
                </button>
            </form>
            {message && <p>{message}</p>} {/* Display any messages to the user */}
        </div>
    );
};

export default PasswordReset;
