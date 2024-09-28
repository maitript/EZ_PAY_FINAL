import "./InitialProfileUpdatePage.css";
import { useNavigate } from "react-router-dom";
import React from "react";
import { useForm } from "react-hook-form";
import Swal from "sweetalert2";

export default function InitialProfileUpdatePage() {
  const {
    register,
    watch,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      customerId: localStorage.getItem("customerId"),
    },
  });
  const navigate = useNavigate();

  // validate date to make sure user is atleast 18
  const validateDate = (value) => {
    console.log(value);
    const selected = new Date(value).getFullYear();
    const now = new Date().getFullYear();
    return now - selected >= 18; // atleast 18 years of age;
  };

  // check if the profile picture url is valid
  async function checkImage(url) {
    if (url === "") {
      return true;
    }
    try {
      const res = await fetch(url);
      const buff = await res.blob();
      return buff.type.startsWith("image/");
    } catch (error) {
      console.log(error);
      return false;
    }
  }

  async function validateMobileNumber(mobileNumber) {
    const response = await fetch(
      "http://localhost:8005/api/check_if_mobile_present",
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ mobileNumber: mobileNumber }),
      },
    );
    return response.ok;
  }
  const validateAccountNumber = async (accountNumber) => {
    const response = await fetch(
      "http://localhost:8005/api/check_if_bankacc_present",
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ accountNumber: accountNumber }),
      },
    );
    return response.ok;
  };

  //console.log(watch());
  //console.log(errors);
  const validateEmail = async (email) => {
    const response = await fetch(
      "http://localhost:8005/api/check_if_email_present",
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email }),
      },
    );
    return response.ok;
  };
  const validateSubmit = async (profileData) => {
    console.log(profileData);
    const response = await fetch(
      "http://localhost:8005/api/add-profile-details",
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(profileData),
      },
      
    );

    if (response.ok) {
      localStorage.setItem("isProfileInfoSet", true);
      navigate("/profileHome"); // Redirect to profile page after successful update
    } else {
      const errorMsg = await response.text();
      Swal.fire({
        title: "Failed!",
        text: "Profile Update Failed",
        icon: "error",
        confirmButtonText: "Okay",
      }); //("Profile update failed: " + errorMsg);
      console.log(profileData);
    }
  };
  return (
    <>
      <h1 className="welcome-title">Complete Your Profile</h1>
      <div className="profile-update-container">
        <form
          onSubmit={handleSubmit((data) => {
            validateSubmit(data);
          })}
          className="profile-update-form"
        >
          <div className="form-group">
            <label htmlFor="name">Name:</label>
            <input
              type="text"
              id="name"
              {...register("name", { required: "This is required" })}
              className={`form-input ${errors?.name ? "error" : ""}`}
            />
            {errors?.name && (
              <p className="error-message">{errors.name.message}</p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="email">Email:</label>
            <input
              type="text"
              id="email"
              {...register("email", {
                required: "Email is required",
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: "Invalid Email Address",
                },
                validate: validateEmail,
              })}
              className={`form-input ${errors?.email ? "error" : ""}`}
            />
            {errors?.email && (
              <p className="error-message">{errors.email.message}</p>
            )}
            {errors?.email?.type === "validate" && (
              <p className="error-message">
                Email already registered, please use other email
              </p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="mobileNumber">Mobile Number:</label>
            <input
              type="tel"
              id="mobileNumber"
              {...register("mobileNumber", {
                required: "Enter Your Mobile Number",
                pattern: {
                  /*
                   * actual number must begin with 9,8,7,6,
                   * beginning with 0, +91, 91 is allowed
                   * spaces after this is allowed
                   * other country codes are not allowed
                   * */
                  value: /^(\+91[\-\s]?)?(\91[\-\s]?)?[0]?(91)?[6789]\d{9}$/,
                  message: "Invalid Mobile Number",
                },
                validate: validateMobileNumber,
              })}
              className={`form-input ${errors?.mobileNumber ? "error" : ""}`}
            />
            {errors?.mobileNumber?.type === "required" && (
              <p className="error-message">{errors.mobileNumber.message}</p>
            )}
            {errors?.mobileNumber?.type === "pattern" && (
              <p className="error-message">{errors.mobileNumber.message}</p>
            )}
            {errors?.mobileNumber?.type === "validate" && (
              <p className="error-message">
                Mobile number already registered, please use some other number
              </p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="address">Address:</label>
            <input
              type="text"
              id="address"
              {...register("address", { required: "Enter your address" })}
              className={`form-input ${errors?.address ? "error" : ""}`}
            />
            {errors?.address && (
              <p className="error-message">{errors.address.message}</p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="dateOfBirth">Date of Birth (MM-DD-YYYY):</label>
            <input
              type="date"
              id="dateOfBirth"
              {...register("dob", {
                required: "Enter your date of birth",
                validate: validateDate,
              })}
              min="1799-01-01"
              className={`form-input ${errors?.dob ? "error" : ""}`}
            />
            {errors?.dob?.type === "required" && (
              <p className="error-message">This field is required</p>
            )}
            {errors?.dob?.type === "validate" && (
              <p className="error-message">
                You must be 18 or older to create account
              </p>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="gender">Gender:</label>
            <select
              id="gender"
              // name="gender"
              {...register("gender", { required: "Select Your Gender" })}
              className={`form-input ${errors?.gender ? "error" : ""}`}
            >
              <option value="">Select Gender</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
            {errors?.gender?.type === "required" && (
              <p className="error-message">This field is required</p>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="profilePictureUrl">Profile Picture URL:</label>
            <input
              type="url"
              id="profilePictureUrl"
              {...register("profilePictureUrl", { validate: checkImage })}
              className={`form-input ${errors?.profilePictureUrl ? "error" : ""}`}
            />
            {errors?.profilePictureUrl?.type === "validate" && (
              <>
                <p className="error-message">
                  Invalid Image url or the image cannot be accessed
                </p>
                <p className="error-message">
                  Leave blank if no link is available
                </p>
              </>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="bankAccountNumber">Bank Account Number:</label>
            <input
              type="text"
              id="bankAccountNumber"
              {...register("bankAccountNumber", {
                required: "Enter Bank Account Number",
                pattern: {
                  // 9-18 digits are valid acc to rbi
                  value: /^\d{9,18}$/,
                  message: "Invalid Bank Account Number",
                },
                validate: validateAccountNumber,
              })}
              className={`form-input ${errors?.bankAccountNumber ? "error" : ""}`}
            />
            {errors?.bankAccountNumber && (
              <p className="error-message">
                {errors.bankAccountNumber.message}
              </p>
            )}
            {errors?.bankAccountNumber?.type === "validate" && (
              <p className="error-message">
                This bank account has already registered, please use some other
                account.
              </p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="ifscCode">IFSC Code:</label>
            <input
              type="text"
              id="ifscCode"
              /*
               *
               *IFSC Code Format:
               *->Exact length should be 11
               *->First 4 alphabets
               *->Fifth character is 0 (zero)
               *->Last six characters (usually numeric, but can be alphabetic)
               * */
              {...register("ifscCode", {
                required: "Enter IFSC Code",
                pattern: {
                  value: /^[A-Za-z]{3}[A-Z0-9a-z]{8}$/,
                  message: "Invalid IFSC Code",
                },
              })}
              className={`form-input ${errors?.ifscCode ? "error" : ""}`}
            />
            {errors?.ifscCode && (
              <p className="error-message">{errors.ifscCode.message}</p>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="accountType">Account Type:</label>
            <select
              id="accountType"
              {...register("accountType", {
                required: "Select Your Account Type",
              })}
              className={`form-input ${errors?.accountType ? "error" : ""}`}
            >
              <option value="">Select Account Type</option>
              <option value="1">Savings</option>
              <option value="2">Current</option>
            </select>
            {errors?.accountType?.type === "required" && (
              <p className="error-message">{errors.accountType.message}</p>
            )}
          </div>

          <button type="submit" className="submit-button">
            Update Profile
          </button>
        </form>
      </div>
    </>
  );
}
