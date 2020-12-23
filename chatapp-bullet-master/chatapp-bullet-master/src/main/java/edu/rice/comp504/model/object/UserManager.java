package edu.rice.comp504.model.object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.rice.comp504.model.utilities.ResponseBuilder;
import org.eclipse.jetty.websocket.api.Session;

/**
 * Manages all the existing users.
 */
public class UserManager {
    private List<User> userList;
    private List<Integer> bannedUserIDList;
    static int nextUserId = 1;

    private static UserManager ONLY;

    /**
     * Singleton private constructor.
     */
    private UserManager() {
        userList = new ArrayList<>();
        bannedUserIDList = new ArrayList<>();
    }

    /**
     * Returns the singleton object.
     */
    public static UserManager getOnly() {
        if (ONLY == null) {
            ONLY = new UserManager();
        }
        return ONLY;
    }

    /**
     * Add a user.
     */
    public void newUser(User user) {
        userList.add(user);
    }

    /**
     * Delete a User from the list.
     */
    public void deleteUser(Integer userID) {
        userList.removeIf(u -> u.getUserID().equals(userID));
    }

    /**
     * Get the User object according to the userID.
     */
    public User getUser(Integer userID) {
        Iterator iter = userList.iterator();
        while (iter.hasNext()) {
            User user = (User)iter.next();
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        System.out.println("Error: no such user with userID " + userID);
        return null;
    }

    /**
     * Ban a user from all the chat rooms.
     */
    public void banUser(User user) {
        if (isBanned(user.getUserID()) == false) {
            bannedUserIDList.add(user.getUserID());
        }
    }

    /**
     * Tell if a user is banned or not.
     */
    public Boolean isBanned(Integer id) {
        return bannedUserIDList.contains(id);
    }

    /**
     * Get user id using session.
     * @param session user session
     * @return user's id
     */
    public Integer getUserID(Session session) {
        Iterator iter = userList.iterator();
        while (iter.hasNext()) {
            User user = (User)iter.next();
            if (user.getSession().equals(session)) {
                return user.getUserID();
            }
        }
        return null;
    }

    public static Integer getNextUserID() {
        return nextUserId++;
    }

    /**
     * Send response to all users.
     * @param response response in string format
     */
    public void broadcastResponse(String response) {
        for (User user : userList) {
            try {
                user.getSession().getRemote().sendString(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate a response for each of the users and send it out.
     */
    public void broadcastResponsePerUser(ResponseBuilder.BroadcastInterface i) {
        for (User user : userList) {
            try {
                user.getSession().getRemote().sendString(i.getResponse(user));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clear all the data, for test purposes.
     */
    public void reset() {
        userList = new ArrayList<>();
        bannedUserIDList = new ArrayList<>();
        nextUserId = 1;
    }
}
