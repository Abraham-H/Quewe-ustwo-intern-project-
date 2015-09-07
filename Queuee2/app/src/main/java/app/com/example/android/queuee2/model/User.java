package app.com.example.android.queuee2.model;

/**
 * Created by bkach on 9/2/15.
 */
public class User {
    private String id;

    public User(String id){
        this.id = id;
    }
    public User() { this.id = Queue.androidID; }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
}
