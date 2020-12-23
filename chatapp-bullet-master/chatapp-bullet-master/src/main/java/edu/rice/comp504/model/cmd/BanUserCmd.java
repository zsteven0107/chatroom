package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Ban a user from a chat room.
 */
public class BanUserCmd implements ICommand {
    private Integer chatRoomID;
    private Integer userID;
    private Integer executorID;

    /**
     * Constructor.
     */
    BanUserCmd(Integer chatRoomID, Integer userID, Integer executorID) {
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
                    ResponseBuilder.buildErrorResponse("You are not allowed to ban other users.")));
            return;
        }

        /*Ban successfully*/

        chatRoom.banUser(user.getUserID());

        /*Broadcast ban message to all users in chat room*/
        String content = "user " + user.getUsername() + " is banned";
        Message newMsg = chatRoom.newMsg("system", new Timestamp(System.currentTimeMillis()),0, 0, content);
        chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone")
        ));

        /*Update user list for all users in chat room*/
        chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildUpdateUserListResponse(chatRoom)));

        /*Remind user he/she has been banned*/
        user.sendResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildErrorResponse("You have been banned.")));
    }
}
