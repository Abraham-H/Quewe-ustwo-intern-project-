package app.com.example.android.queuee2.model;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RetrofitError;
import retrofit.mime.TypedInput;

/**
 * Created by bkach on 9/17/15.
 */
public class Response {

    private String message;
    private Object data;

    public Response(String message) {
        this(message, null);
    }

    public Response(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public static Response.Error getError(Throwable error) {
        Gson gson = new Gson();
        RetrofitError retrofitError = (RetrofitError) error;
        TypedInput body = retrofitError.getResponse().getBody();
        String bodyString = "";
        Object data = null;
        int status = 0;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
            StringBuilder out = new StringBuilder();
            String newLine = System.getProperty("line.seperator");
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                if (newLine != null) {
                    out.append(newLine);
                }
            }
            bodyString = gson.fromJson(out.toString(), Response.class).getMessage();
            data = gson.fromJson(out.toString(), Response.class).getData();
            status = retrofitError.getResponse().getStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response.Error(bodyString, status, data);
    }

    public static class Error {
        private String message;
        private int status;
        private Object data;

        public Error(String message, int status, Object data) {
            this.message = message;
            this.status = status;
            this.data = data;
        }

        public String getMessage() {
            return this.message;
        }

        public int getStatus() {
            return this.status;
        }

        public Object getData() {
            return this.data;
        }
    }
}

