package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Create a new chat room.
 */
public class ExitRoomCmd implements ICommand {
    private ChatRoom chatRoom;
    private User user;
    /**
     * Constructor.
     */
    ExitRoomCmd(Integer chatRoomID, Integer userID) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        user = UserManager.getOnly().getUser(userID);
    }

    /**
     * Execute the command.
     */
    public void execute() {
        chatRoom.deleteUser(user.getUserID());
        user.exitChatRoom(chatRoom.getChatRoomID());
        // update own room list
        user.sendResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildUpdateOwnChatRoomListResponse(user)
        ));
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
                    ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "")
            ));
            // update the user list of the chat room
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateUserListResponse(chatRoom)
            ));
            // update the public room list of this new member
            user.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateChatRoomListResponse(user)
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
