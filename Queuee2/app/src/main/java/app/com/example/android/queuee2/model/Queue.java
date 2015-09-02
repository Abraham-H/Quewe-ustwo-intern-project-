package app.com.example.android.queuee2.model;

import java.util.ArrayList;

/**
 * Created by bkach on 9/2/15.
 */
public class Queue {
    ArrayList<User> users = new ArrayList<User>();

    public void add(User user){
        users.add(user);
    }

    public User pop(){
        return users.remove(0);
    }
}
