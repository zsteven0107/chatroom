package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a new chat room.
 */
public class CloseConnectionCmd implements ICommand {
    private User user;
    /**
     * Constructor.
     */
    CloseConnectionCmd(Integer userID) {
        user = UserManager.getOnly().getUser(userID);
    }

    /**
     * Execute the command.
     */
    public void execute() {
        List<ChatRoom> joinedChatRooms = user.getJointChatRooms();
        UserManager.getOnly().deleteUser(user.getUserID());
        for (ChatRoom chatRoom : joinedChatRooms) {
            chatRoom.deleteUser(user.getUserID());
            if (chatRoom.getUserNum() > 0) {
                if (user == chatRoom.getAdmin()) {
                    // assign another user as the admin
                    chatRoom.setNewAdmin();
                    // send message about the new admin
                    String content = "user " + user.getUsername() + " is the new admin now";
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    Message newMsg = chatRoom.newMsg("system", new Timestamp(System.currentTimeMillis()),0, 0, content);
                    chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                            ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone")
                    ));
                }
                // broadcast system message about the exiting user
                String content = "user " + user.getUsername() + " exits";
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                Message newMsg = chatRoom.newMsg("system", new Timestamp(System.currentTimeMillis()),0, 0, content);
                chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone")
                ));
                // update the user list of the chat room
                chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildUpdateUserListResponse(chatRoom)
                ));

            } else {
                // destroy the room
                ChatRoomManager.getOnly().deleteRoom(chatRoom.getChatRoomID());
                // update the public room list
                UserManager.getOnly().broadcastResponsePerUser(u -> GsonInstance.getGson().toJson(
                        ResponseBuilder.buildUpdateChatRoomListResponse(u)
                ));
            }
        }
    }
}
