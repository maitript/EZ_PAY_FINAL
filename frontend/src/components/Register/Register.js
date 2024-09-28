import React from "react";
import { useForm } from "react-hook-form";
import "./Register.css"; // Import the CSS file
import { useNavigate } from "react-router-dom"; // Import useNavigate for redirection
import Swal from 'sweetalert2';

export default function Register() {
  localStorage.removeItem("customerId");
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm();
  // Watch the password and confirm password fields for validation
  const password = watch("password");
  const confirmPassword = watch("confirmPassword");

  const validateUserId = async (userId) => {
    // checking the spring application if the user id already in use
    const response = await fetch("http://localhost:8005/api/check_user_id", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        "userId": userId,
      }),
    });
    if (response.ok) {
      return true;
    } else {
      return false;
    }
  };
  const onSubmit = async (data) => {
    const response = await fetch("http://localhost:8005/api/register-user", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        userId: data.userid,
        password: data.password,
      }),
    });

    if (response.ok) {
      Swal.fire({
        title: 'Registration successful!',
        text: 'Proceed for login.',
        icon: 'success',
        confirmButtonText: 'Login',
      });
      //alert("Registration successful. Proceed for login");
      navigate("/login");
    } else {
      const errorMsg = await response.text();
      Swal.fire({
        title: 'Registration Failed!',
        text: 'Account not created',
        icon: 'error',
        confirmButtonText: 'Okay',
      });
      //alert("Registration failed: " + errorMsg);
    }

    console.log("Form data:", data);
  };

  return (
    <>
      <h1 className="welcome-title">Registration Page</h1>
      <div className="user-registration">
        <form onSubmit={handleSubmit(onSubmit)} className="register-form">
          <div className="form-group">
            <label htmlFor="userid">User ID</label>
            <input
              id="userid"
              type="text"
              {...register("userid", {
                required: "User ID is required",
                validate: validateUserId, // for checking if the user id is already in use or note
              })}
              className={`form-input ${errors.userid ? "error" : ""}`}
            />
            {errors.userid && (
              <p className="error-message">{errors.userid.message}</p>
            )}
            {errors?.userid?.type === "validate" && (
              <p className="error-message">
                User ID already in use, please try something else
              </p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              {...register("password", {
                required: "Password is required",
                minLength: {
                  value: 6,
                  message: "Password must be at least 6 characters",
                },
                pattern: {
                  value:
                    /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/,
                  message:
                    "Password must include letters, numbers, and special characters",
                },
              })}
              className={`form-input ${errors.password ? "error" : ""}`}
            />
            {errors.password && (
              <p className="error-message">{errors.password.message}</p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              id="confirmPassword"
              type="password"
              {...register("confirmPassword", {
                required: "Please confirm your password",
                validate: (value) =>
                  value === password || "Passwords do not match",
              })}
              className={`form-input ${errors.confirmPassword ? "error" : ""}`}
            />
            {errors.confirmPassword && (
              <p className="error-message">{errors.confirmPassword.message}</p>
            )}
          </div>

          <button type="submit" className="submit-button">
            Register
          </button>
        </form>
      </div>
    </>
  );
}
