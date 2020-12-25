package com.trungngo.asm2;

public class Constants {
    //Fields of FireStore 'users' collection
    static class FSUser {
        static final String userCollection = "users";
        static final String usernameField = "username";
        static final String phoneField = "phone";
        static final String birthDateField = "birthDate";
        static final String genderField = "gender";
        static final String emailField = "email";
        static final String roleField = "role";
    }

//    //Fields of FireStore 'messages' collection
//    static class FSMessage {
//        static final String messageCollection = "messages";
//        static final String senderEmailField = "senderEmail";
//        static final String receiverEmailField = "receiverEmail";
//        static final String messageField = "message";
//        static final String dateField = "date";
//    }

    //All Toast messages being used
    static class ToastMessage {
        static final String emptyInputError = "Please fill in your account authentication.";
        static final String signInSuccess = "Sign in successfully!";
        static final String signInFailure = "Invalid email/password!";
        static final String registerSuccess = "Successfully registered";
        static final String registerFailure = "Authentication failed, email must be unique and has correct form!";
        static final String retrieveUsersInfoFailure = "Error querying for all users' information!";
        static final String emptyMessageInputError = "Please type your message to send!";
    }

}