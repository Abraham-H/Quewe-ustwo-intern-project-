package app.com.example.android.queuee2.model;

/**
 * Created by bkach on 9/17/15.
 */
public class Response {
    public Response(String message){
        this(message, null, false);
    }
    public Response(String message, Object data){
        this(message, data, false);
    }
    public Response(String message, boolean error){
        this(message, null, error);
    }
    public Response(String message, Object data, boolean error){
        setMessage(message);
        setData(data);
        setError(error);
    }

    private String message;
    private Object data;
    private boolean error;

    public boolean isError() {return error;}
    public void setError(boolean error) {this.error = error;}
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    public Object getData() {return data;}
    public void setData(Object data) {this.data = data;}
}

