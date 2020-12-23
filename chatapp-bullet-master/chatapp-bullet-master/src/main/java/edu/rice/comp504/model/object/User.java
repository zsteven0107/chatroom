package edu.rice.comp504.model.object;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * User of the chat room app.
 */
public class User {
    private String username;
    private Integer userID;
    private Integer age;
    private String school;
    private String[] interests;
    private Integer numOfHate;
    private List<ChatRoom> joinedChatRooms;
    private Session session;

    /**
     * Constructor.
     */
    public User(Integer userID, String username, Integer age, String school, String[] interests, Session session) {
        this.userID = userID;
        this.username = username;
        this.age = age;
        this.school = school;
        this.interests = interests;
        this.numOfHate = 0;
        this.joinedChatRooms = new ArrayList<>();
        this.session = session;
    }

    /**
     * Tell if two users are the same.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Get the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the userID.
     */
    public Integer getUserID() {
        return userID;
    }

    /**
     * Get the age.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Get how many times the user has used "hate".
     */
    public Integer getNumOfHate() {
        return numOfHate;
    }

    /**
     * Get the chat rooms the user has joint.
     */
    public List<ChatRoom> getJointChatRooms() {
        return joinedChatRooms;
    }

    /**
     * Get the chat rooms the user has joint.
     */
    public Boolean hasJoined(ChatRoom chatRoom) {
        return joinedChatRooms.contains(chatRoom);
    }

    /**
     * Get the school of the user.
     */
    public String getSchool() {
        return school;
    }

    /**
     * Get the interest of the user.
     */
    public String[] getInterests() {
        return interests;
    }

    /**
     * The user joins a chat room.
     */
    public void addChatRoom(ChatRoom chatRoom) {
        joinedChatRooms.add(chatRoom);
    }

    /**
     * The user has sent a message that contains "hate".
     */
    public void sentHate(Integer chatRoomID) {
        numOfHate += 1;
        if (numOfHate >= 2) {
            UserManager.getOnly().banUser(this);
            ChatRoomManager.getOnly().getChatRoom(chatRoomID).banUser(this.userID);
        }
    }

    /**
     * Get the corresponding session of the message.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Send response to the user himself's session
     * @param response response in string format.
     */
    public void sendResponse(String response) {
        try {
            this.getSession().getRemote().sendString(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a room from the joined room.
     */
    public void exitChatRoom(Integer chatRoomID) {
        joinedChatRooms.removeIf(r -> r.getChatRoomID().equals(chatRoomID));
    }
}
