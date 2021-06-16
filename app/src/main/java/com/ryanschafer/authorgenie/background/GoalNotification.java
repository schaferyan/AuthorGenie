package com.ryanschafer.authorgenie.background;

public class GoalNotification {

        private final String message;
        private final String title;

        public GoalNotification(String title, String message){
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
