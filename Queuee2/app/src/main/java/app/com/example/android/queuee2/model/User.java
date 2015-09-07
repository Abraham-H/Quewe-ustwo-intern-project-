package app.com.example.android.queuee2.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bkach on 9/2/15.
 */
public class User implements Parcelable {
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

    public User(Parcel in){
        this.id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        public User createFromParcel(Parcel in) {
            return new User(in.readString());
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
