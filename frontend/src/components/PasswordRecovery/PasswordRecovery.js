import React, { useState, useEffect } from 'react';
import './PasswordRecovery.css';
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';

/**
 * PasswordRecovery component handles the user interface and logic for
 * requesting a password reset. It allows users to enter their email or mobile number
 * and sends them a reset link if the input is valid.
 * Validation methods: @Aman Rauth
 * @Author: Riya Shah
 */
const PasswordRecovery = () => {
    // State variables for managing form inputs and responses
    const [emailOrMobile, setEmailOrMobile] = useState(''); // User input for email or mobile
    const [newEmailOrMobile, setNewEmailOrMobile] = useState(''); // For changing the email or mobile number
    const [message, setMessage] = useState(''); // Message to display feedback to the user
    const [isEmailSubmitted, setIsEmailSubmitted] = useState(false); // Flag to check if email has been submitted
    const [isRequestDisabled, setIsRequestDisabled] = useState(false); // Disable request button after submission
    const [countdown, setCountdown] = useState(0); // Countdown timer for resend functionality
    const API_URL = process.env.REACT_APP_SPRING_API_URL; // API URL from environment variables

    const navigate = useNavigate(); // Hook for programmatic navigation
    const [error, setError] = useState(''); // State variable for handling validation errors

    /**
     * Validates the provided email address against a regex pattern.
     * 
     * @param {string} email - Email address to validate.
     * @returns {boolean} - True if valid, false otherwise.
     */
    const validateEmail = (email) => {
        const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i; // Email regex pattern
        return emailRegex.test(email); // Test and return result
    };

    /**
     * Handles input changes and validates the email format.
     * 
     * @param {Event} e - Input change event.
     */
    const handleChange = (e) => {
        const { value } = e.target; // Get value from input
        setEmailOrMobile(value); // Update state

        // Validate email format and update error message if invalid
        if (validateEmail(value)) {
            setError('');
        } else {
            setError('Please enter a valid email address');
        }
    };

    /**
     * Handles the password reset request when the form is submitted.
     * Sends a POST request to the backend API.
     * 
     * @param {Event} e - Form submit event.
     */
    const handleForgotPassword = async (e) => {
        e.preventDefault(); // Prevent default form submission
        try {
            const response = await fetch(`${API_URL}/api/password/forgot?emailOrMobile=${encodeURIComponent(emailOrMobile)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(await response.text()); // Throw error if response is not OK
            }

            const data = await response.text(); // Get response text
            setMessage(data); // Set feedback message
            setIsEmailSubmitted(true); // Update state to reflect that email has been submitted
            setIsRequestDisabled(true); // Disable request button after sending
        } catch (error) {
            setMessage(error.message || 'Error occurred while requesting password reset.'); // Set error message
        }
    };

    /**
     * Handles sending the reset link again after a cooldown period.
     */
    const handleSendResetLink = () => {
        if (isEmailSubmitted) {
            Swal.fire({
                title: 'Link Sent!',
                text: 'Please check your inbox! A reset link will be sent again in 10 minutes',
                icon: 'info',
                confirmButtonText: 'Okay',
              });
            //alert('Please check your inbox! A reset link will be sent again in 10 minutes');
            setCountdown(600); // Set countdown to 10 minutes (600 seconds)
        }
    };

    /**
     * Allows the user to change their email or mobile number after submitting the request.
     */
    const handleChangeEmailOrMobile = () => {
        setEmailOrMobile(newEmailOrMobile); // Update email/mobile number
        setNewEmailOrMobile(''); // Clear new input
        setIsEmailSubmitted(false); // Reset email submitted status
        setIsRequestDisabled(false); // Enable request button
        setMessage(''); // Clear message
        setCountdown(0); // Reset countdown
    };

    // Effect hook to manage countdown timer
    useEffect(() => {
        let timer; // Timer variable
        if (countdown > 0) {
            timer = setInterval(() => {
                setCountdown(prev => prev - 1); // Decrement countdown
            }, 1000);
        } else if (countdown === 0 && isEmailSubmitted) {
            // Automatically resend the reset link after countdown expires
            handleForgotPassword(new Event('submit')); // Simulate a form submission
        }
        return () => clearInterval(timer); // Cleanup the timer on component unmount
    }, [countdown, isEmailSubmitted]);

    // Render the component
    return (
        <div className='passwordRecovery'>
            <h2 id="passwordRecoveryh2">Password Recovery</h2>
            <form onSubmit={handleForgotPassword} id="recoveryForm">
                <h3 id="passwordRecoveryh3">{isEmailSubmitted ? 'Email Sent!' : 'Request Password Reset'}</h3>
                <input
                    type="text"
                    placeholder="Email or Mobile Number"
                    value={emailOrMobile}
                    onChange={handleChange}
                    required
                />
                <button type="submit" disabled={isRequestDisabled}>
                    Request Reset
                </button>
                {isEmailSubmitted && (
                    <div>
                        <p>Please check your email for a password reset link.</p>
                        <button onClick={handleSendResetLink}>Re-Send Reset Link</button>
                        {countdown > 0 && (
                            <p>Time remaining: {Math.floor(countdown / 60)}:{(countdown % 60).toString().padStart(2, '0')}</p>
                        )}
                        <button type="button" onClick={handleChangeEmailOrMobile}>
                            Change Email or Mobile Number
                        </button>
                    </div>
                )}
            </form>
            {message && <p id='message'>{message}</p>} {/* Display feedback message */}
        </div>
    );
};

export default PasswordRecovery;
