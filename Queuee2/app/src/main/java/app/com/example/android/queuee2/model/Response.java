package app.com.example.android.queuee2.model;

/**
 * Created by Abraham on 9/17/2015.
 */
public class Response{

    public static class Message {
        public Message(String message){
            this(message,false);
        }
        public Message(String message, boolean error){
            setMessage(message);
            setError(error);
        }

        private String message;
        private boolean error;

        public boolean isError() {return error;}
        public void setError(boolean error) {this.error = error;}
        public String getMessage() {return message;}
        public void setMessage(String message) {this.message = message;}
    }

    public static class QueueData{
        private int position;
        private int size;

        public int getPosition(){
            return position;
        }

        public void setPosition(int position){
            this.position = position;
        }

        public int getSize(){
            return size;
        }

        public void setSize(int size){
            this.size = size;
        }
    }

}