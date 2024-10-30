import React, { useState } from 'react';
import { resetPassword, confirmResetPassword } from 'aws-amplify/auth';
import './login.css'; // Import the CSS file

const ForgotPassword = () => {

    const [email, setEmail] = useState('');
    const [confirmationCode, setConfirmationCode] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmNewPassword, setConfirmNewPassword] = useState('');
    const [newPasswordValidation, setNewPasswordValidation] = useState('');
    const [matchPassword, setmatchPassword] = useState('');
    const [confirmationCodeError, setConfirmationCodeError] = useState('');
    
    const [step, setStep] = useState(1);
    
    async function handleResetPassword() {
      
        try {
          const output = await resetPassword({ username: email });
          handleResetPasswordNextSteps(output);
        } catch (error) {
          console.log(error);
          console.error('Error requesting password reset:', error);
        }
      }
      
      function handleResetPasswordNextSteps(output) {
        const { nextStep } = output;
        switch (nextStep.resetPasswordStep) {
          case 'CONFIRM_RESET_PASSWORD_WITH_CODE':
            const codeDeliveryDetails = nextStep.codeDeliveryDetails;
            console.log(
              `Confirmation code was sent to ${codeDeliveryDetails.deliveryMedium}`
            );
            // Collect the confirmation code from the user and pass to confirmResetPassword.
            setStep(2);

            break;
          case 'DONE':
            console.log('Successfully reset password.');
            setStep(3);
            break;
        }
      }

      async function handleConfirmResetPassword() {
        try {
          await confirmResetPassword({ 
            username: email, 
            confirmationCode: confirmationCode,
            newPassword: newPassword
        });
        console.log('Password reset successful');

        const response = await fetch('http://localhost:8080/users/updatepassword', {
            method: 'PUT',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, newPassword}),
          });
      
          if (response.ok) {
             
            setStep(3);
      
          } else {
            const errorText = await response.text();
            alert('Failed: ' + errorText); 
            console.error('Failed');
          }

        } catch (error) {
          console.log(error);
          setConfirmationCodeError('Incorrect confirmation code. Please try again.');
        }
      }

      function validatePassword(newPassword) {
        const minLength = 8;
        const hasSpecialCharacter = /[!@#$%^&*(),.?":{}|<>]/.test(newPassword);
        const hasUppercase = /[A-Z]/.test(newPassword);
        const hasNumber = /[0-9]/.test(newPassword);
    
        if (newPassword.length < minLength) {
          return "Password must be at least 8 characters.";
        } else if (!hasSpecialCharacter) {
          return "Password must contain at least one special character.";
        } else if (!hasUppercase) {
          return "Password must contain at least one uppercase character.";
        } else if (!hasNumber) {
          return "Password must contain at least one number.";
        } else {
          return "";
        }
      }
    
      function passwordMatch(confirmNewPassword) {
        if (newPassword !== confirmNewPassword) {
          return "Passwords do not match.";
        } else {
          return "";
        }
      }
    
    
    return (
    <div className="container">
      <h1 className="header">Forgot Password</h1>
      {step === 1 && (
        <div className="form">
            <label>
                Enter your email to reset your password
            </label>
            <input
                type="text"
                placeholder="Enter your email"
                className="input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <button onClick={handleResetPassword} className="button">Submit</button>
        </div>
      )}
      {step === 2 && (
        <div className="form">
            <label>
                Enter the confirmation code sent to your email
            </label>
          <input
            type="text"
            placeholder="Enter confirmation code"
            className="input"
            value={confirmationCode}
            onChange={(e) => setConfirmationCode(e.target.value)}
          />
          {confirmationCodeError && (
              <p className="error-message">{confirmationCodeError}</p>
          )}
            <label>
                Enter your new password below
            </label>
          <input
            type="password"
            placeholder="Enter new password"
            className="input"
            value={newPassword}
            onChange={(e) => {setNewPassword(e.target.value); setNewPasswordValidation(
              validatePassword(e.target.value));}}
          />
          {newPasswordValidation && (
          <p className="error-message">{newPasswordValidation}</p>
          )}
          <label>
                Confirm your new password
            </label>
          <input
            type="password"
            placeholder="Confirm new password"
            className="input"
            value={confirmNewPassword}
            onChange={(e) => {setConfirmNewPassword(e.target.value); setmatchPassword(
              passwordMatch(e.target.value));}}
          />
          {matchPassword && (
          <p className="error-message">{matchPassword}</p>
          )}
          <button onClick={handleConfirmResetPassword} className="button">Confirm New Password</button>
        </div>
      )}
      {step === 3 && <p>Password reset successful! You can now log in with your new password.</p>}
    </div>
        
    )
}
export default ForgotPassword;