package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Create a new chat room.
 */
public class ApproveCmd implements ICommand {
    private ChatRoom chatRoom;
    private User user;
    private Boolean isApprove;
    /**
     * Constructor.
     */
    ApproveCmd(Integer chatRoomID, Integer userID, Boolean isApprove) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        user = UserManager.getOnly().getUser(userID);
        this.isApprove = isApprove;
    }

    /**
     * Execute the command.
     */
    public void execute() {
        if (isApprove) {
            chatRoom.addUser(user.getUserID());
            user.addChatRoom(chatRoom);
            // add a new message about the new user
            String content = "user " + user.getUsername() + " joins";
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Message newMsg = chatRoom.newMsg("system", new Timestamp(System.currentTimeMillis()),0, 0, content);
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone")
            ));
            // update the user list of this chat room
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateUserListResponse(chatRoom)
            ));
            // update the joined chat room list of this new member
            user.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateOwnChatRoomListResponse(user)
            ));
            // update the public room list of this new member
            user.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateChatRoomListResponse(user)
            ));
        } else {
            user.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("your request has been declined.")
            ));
        }
    }
}
