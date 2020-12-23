package edu.rice.comp504.model.object;

import java.util.*;

/**
 * Manages all the chat rooms.
 */
public class ChatRoomManager {
    private List<ChatRoom> chatRoomList;
    private static ChatRoomManager ONLY;
    private Integer nextChatRoomID = 1;

    /**
     * Singleton private constructor.
     */
    private ChatRoomManager() {
        chatRoomList = new ArrayList<>();
    }

    /**
     * Return the singleton object.
     */
    public static ChatRoomManager getOnly() {
        if (ONLY == null) {
            ONLY = new ChatRoomManager();
        }
        return ONLY;
    }

    /**
     * Add a room into the manager.
     */
    public ChatRoom addChatRoom(String chatRoomName, Boolean isPrivate, Integer adminID) {
        ChatRoom newRoom = new ChatRoom(nextChatRoomID, chatRoomName, isPrivate, adminID);
        chatRoomList.add(newRoom);
        nextChatRoomID += 1;
        return newRoom;
    }

    /**
     * Delete a room from the manager.
     */
    public void deleteRoom(Integer roomID) {
        chatRoomList.removeIf(r -> r.getChatRoomID().equals(roomID));
    }

    /**
     * Get the chatRoom object with specific ID.
     */
    public ChatRoom getChatRoom(Integer chatRoomID) {
        Iterator iter = chatRoomList.iterator();
        while (iter.hasNext()) {
            ChatRoom chatRoom = (ChatRoom)iter.next();
            if (chatRoom.getChatRoomID().equals(chatRoomID)) {
                return chatRoom;
            }
        }
        System.out.println("Error: no such chat room with userID " + chatRoomID);
        return null;
    }

    /**
     * Get all public chatrooms.
     */
    public ChatRoom[] getPublicChatRooms() {
        List<ChatRoom> publicChatRooms = new LinkedList<>();
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.getIsPrivate() == false)  {
                publicChatRooms.add(chatRoom);
            }
        }
        return publicChatRooms.toArray(new ChatRoom[publicChatRooms.size()]);
    }

    /**
     * Clear all the data, for test purposes.
     */
    public void reset() {
        chatRoomList = new ArrayList<>();
        nextChatRoomID = 1;
    }
}
