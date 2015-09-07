package app.com.example.android.queuee2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bkach on 9/2/15.
 */
public class User {
    private String id;

    public User(String id){
        this.id = id;
    }
    public User() { this.id = Queue.androidID; }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }

}
