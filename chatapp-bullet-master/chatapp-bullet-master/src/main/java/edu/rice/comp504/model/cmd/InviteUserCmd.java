package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Invite the user to join room.
 */
public class InviteUserCmd implements ICommand {
    private Integer chatRoomID;
    private Integer userID;
    private Integer executorID;

    /**
     * Constructor.
     */
    InviteUserCmd(Integer chatRoomID, Integer userID, Integer executorID) {
        this.chatRoomID = chatRoomID;
        this.userID = userID;
        this.executorID = executorID;
    }

    /**
     * Execute the command.
     */
    public void execute() {
        ChatRoom chatRoom = ChatRoomManager.getOnly().getChatRoom(this.chatRoomID);
        User user = UserManager.getOnly().getUser(this.userID);
        User admin = chatRoom.getAdmin();
        User executor = UserManager.getOnly().getUser(this.executorID);

        /*Check whether the executor has the right to ban*/
        if (admin.getUserID() != this.executorID) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You are not allowed to invite other users.")));
            return;
        }

        /*Check whether the user exists*/
        if (user == null) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("User doesn't exists.")));
            return;
        }

        /*Check whether the user is already in the chatroom*/
        if (chatRoom.isUserExists(this.userID)) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("User is already in this chat room.")));
            return;
        }

        /*Invite successfully*/

        chatRoom.addUser(this.userID);
        user.addChatRoom(chatRoom);

        /*Broadcast join message to all users in chat room*/
        String content = "user " + user.getUsername() + " joins";
        Message newMsg = chatRoom.newMsg("system", new Timestamp(System.currentTimeMillis()),0, 0, content);
        chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone")
        ));

        /*Update user list for all users in chat room*/
        chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildUpdateUserListResponse(chatRoom)));

        /*Update joined user's own chat room list*/
        user.sendResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildUpdateOwnChatRoomListResponse(user)));
    }
}
