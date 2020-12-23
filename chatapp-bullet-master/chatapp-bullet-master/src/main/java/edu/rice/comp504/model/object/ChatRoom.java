package edu.rice.comp504.model.object;

import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;
import java.util.*;

/**
 * Chat room that users have chats in.
 */
public class ChatRoom {
    private Integer chatRoomID;
    private String chatRoomName;
    private Boolean isPrivate;
    private List<User> userList;
    private List<User> bannedUserList;
    private List<Message> msgList;
    private User admin;
    private Integer nextMsgID = 1;

    /**
     * Constructor.
     *
     * @param isPrivate If the chat room is private or not
     * @param adminID   User ID of the admin
     */
    public ChatRoom(Integer chatRoomID, String chatRoomName, Boolean isPrivate, Integer adminID) {
        this.chatRoomID = chatRoomID;
        this.chatRoomName = chatRoomName;
        this.isPrivate = isPrivate;
        userList = new ArrayList<>();
        bannedUserList = new ArrayList<>();
        msgList = new ArrayList<>();
        this.admin = UserManager.getOnly().getUser(adminID);
        userList.add(admin);
        admin.addChatRoom(this);
    }

    /**
     * Tell if two chat rooms are the same.
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Get the ID of the chat room.
     */
    public Integer getChatRoomID() {
        return chatRoomID;
    }

    /**
     * Get the name of the chat room.
     */
    public String getChatRoomName() {
        return chatRoomName;
    }

    /**
     * If the chat room is private.
     */
    public Boolean getIsPrivate() {
        return isPrivate;
    }

    /**
     * Get the user list of the room.
     */
    public List<User> getUserList() {
        return userList;
    }

    /**
     * Get the message list.
     */
    public List<Message> getMsgList() {
        return msgList;
    }

    /**
     * Get the admin of the room.
     */
    public User getAdmin() {
        if (admin == null) {
            System.out.printf("Error: admin is null in chat room %s(ID %d)%n", chatRoomName, chatRoomID);
        }
        return admin;
    }

    /**
     * Add a user to the room.
     */
    public void addUser(Integer userID) {
        userList.add(UserManager.getOnly().getUser(userID));
    }

    /**
     * Delete a User from the room.
     */
    public void deleteUser(Integer userID) {
        userList.removeIf(u -> u.getUserID().equals(userID));
    }

    /**
     * Construct a new message and add into the log.
     */
    public Message newMsg(String type, Timestamp timestamp, Integer senderID, Integer receiverID, String content) {
        Message newMsg = new Message(nextMsgID, type, timestamp, senderID, receiverID, content);
        msgList.add(newMsg);
        nextMsgID += 1;
        return newMsg;
    }

    /**
     * Remove a message.
     */
    public void removeMsg(Integer msgID) {
        msgList.removeIf(m -> m.getMsgID().equals(msgID));
    }

    /**
     * Get a message with msgID.
     */
    public Message getMsg(Integer msgID) {
        Iterator iter = msgList.iterator();
        while (iter.hasNext()) {
            Message msg = (Message)iter.next();
            if (msg.getMsgID().equals(msgID)) {
                return msg;
            }
        }
        System.out.printf("Error: no such message with message ID %d in chat room %s(ID %d)%n", msgID, chatRoomName, chatRoomID);
        return null;
    }

    /**
     * Ban a user from the chat room.
     */
    public void banUser(Integer userID) {
        bannedUserList.add(UserManager.getOnly().getUser(userID));
    }

    /**
     * Tell if a user is banned or not.
     */
    public Boolean isBanned(Integer userID) {
        return bannedUserList.contains(UserManager.getOnly().getUser(userID));
    }

    /**
     * Send response to all users in the chatroom.
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
     * Send response to particular receiver in the chatroom.
     * @param receiverID receiver's userID.
     * @param response response in string format.
     */
    public void sendResponse(Integer receiverID, String response) {
        for (User user : userList) {
            if (user.getUserID() == receiverID) {
                try {
                    user.getSession().getRemote().sendString(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    /**
     * Get the number of users in the chat room.
     */
    public Integer getUserNum() {
        return userList.size();
    }

    /**
     * Set a new user as the admin.
     */
    public void setNewAdmin() {
        for (User u : userList) {
            if (u != admin) {
                admin = u;
                break;
            }
        }
    }

    /**
     * Check whether a user is already in a chat room.
     * @param userID userID
     * @return if exists, return true; otherwise, return false;
     */
    public boolean isUserExists(Integer userID) {
        for (User user : userList) {
            if (user.getUserID() == userID) {
                return true;
            }
        }
        return false;
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
     * Return this chatroom's next message id.
     * @return next available message id.
     */
    public int getNextMsgID() {
        return nextMsgID;
    }
}
