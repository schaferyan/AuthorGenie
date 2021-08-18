package com.ryanschafer.authorgenie.notifcations;

public class AuthorgenieNotification {

        private final String message;
        private final String title;

        public AuthorgenieNotification(String title, String message){
            this.title = title;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getTitle() {
            return title;
        }
}
