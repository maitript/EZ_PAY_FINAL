import './App.css';
import Navbar from './components/Navbar/Navbar';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './components/Home/Home';
import Register from './components/Register/Register';
import LoginPage from './components/LoginPage/LoginPage';
import InitialProfileUpdatePage from './components/InitialProfileUpdatePage/InitialProfileUpdatePage';
import PrivateRoute from './components/PrivateRoute/PrivateRoute';
import ViewProfile from './components/ViewProfile/ViewProfile';
import PasswordRecovery from './components/PasswordRecovery/PasswordRecovery';
import PasswordReset from './components/PasswordReset/PasswordReset';
import UpdateProfile from './components/UpdateProfile/UpdateProfile';
import Dashboard from './components/Dashboard/Dashboard'; //-> Tried the approach to embed dasbaord in the profile home
import ActivateBankAccount from './components/ActivateBankAccount/ActivateBankAccount';
import UpiPayment from './components/UpiPayment/UpiPayment';
import BankTransaction from './components/BankTransaction/BankTransaction';
import BankTransactionResult from './components/BankTransaction/BankTransactionResult';
import PaymentResult from './components/UpiPayment/PaymentResult';
import CheckUserBalance from './components/UpiPayment/CheckUserBalance';
import TransactionDetail from './components/TransactionHistory/Cards/TransactionDetail';
import LoginError from './components/TransactionHistory/LoginError/LoginError';
import TransactionDashboard from './components/TransactionHistory/Dashboard/TransactionDashboard';

/*
@Author: Sagar Kumar
*/

function App() {
  return (
    <>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<LoginPage />} />

          {/* Password Recovery and Reset Routes */}
          <Route path="/password/forgot" element={<PasswordRecovery />} />
          <Route path="/password/reset" element={<PasswordReset />} />

          {/* Protected Route for Profile Home rendering Dashboard */}
          <Route path="/profileHome" element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          } />

          <Route path="/initial-profile-update" element={
            <PrivateRoute>
              <InitialProfileUpdatePage />
            </PrivateRoute>
          } />

          <Route path="/view-profile" element={
            <PrivateRoute>
              <ViewProfile />
            </PrivateRoute>
          } />

          {/* Update Profile Route */}
          <Route path="/update-profile" element={
            <PrivateRoute>
              <UpdateProfile />
            </PrivateRoute>
          } />

          {/* Additional Protected Routes */}
          <Route path="/activate-account" element={
            <PrivateRoute>
              <ActivateBankAccount />
            </PrivateRoute>
          } />
          <Route path="/upi-payment" element={
            <PrivateRoute>
              <UpiPayment />
            </PrivateRoute>
          } />
          <Route path="/payment-result" element={
            <PrivateRoute>
              <PaymentResult />
            </PrivateRoute>
          } />
          <Route path="/check-balance" element={
            <PrivateRoute>
              <CheckUserBalance />
            </PrivateRoute>
          } />
          <Route path="/bank-transaction" element={
            <PrivateRoute>
              <BankTransaction />
            </PrivateRoute>
          } />
          <Route path="/bank-transaction-result" element={
            <PrivateRoute>
              <BankTransactionResult />
            </PrivateRoute>
          } />
          <Route path="/transactionhistory" element={
            <PrivateRoute>
              <TransactionDashboard />
            </PrivateRoute>
          } />
          <Route path="/transaction/:id" element={
            <PrivateRoute>
              <TransactionDetail />
            </PrivateRoute>
          } />
          <Route path="/LoginError" element={
            <PrivateRoute>
              <LoginError />
            </PrivateRoute>
          } />
        </Routes>
      </Router>
    </>
  );
}

export default App;
