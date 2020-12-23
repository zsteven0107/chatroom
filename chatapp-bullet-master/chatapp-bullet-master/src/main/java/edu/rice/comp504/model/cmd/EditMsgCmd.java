package edu.rice.comp504.model.cmd;

import com.google.gson.Gson;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Create a new chat room.
 */
public class EditMsgCmd implements ICommand {
    private ChatRoom chatRoom;
    private User executor;
    private User sender;
    private String newContent;
    private Message message;

    /**
     * Constructor.
     */
    EditMsgCmd(Integer chatRoomID, Integer executorID, Integer senderID, String newContent, Integer messageID) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        executor = UserManager.getOnly().getUser(executorID);
        if (senderID > 0) {
            sender = UserManager.getOnly().getUser(senderID);
        }
        this.newContent = newContent;
        message = ChatRoomManager.getOnly().getChatRoom(chatRoomID).getMsg(messageID);
    }

    /**
     * Execute the command.
     */
    public void execute() {
        if (chatRoom.isBanned(sender.getUserID())) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You have been banned.")));
        } else if (chatRoom.getMsg(message.getMsgID()) == null) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("No such message.")));
        } else if (sender == null) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You cannot edit system message.")));
        } else if (!executor.getUserID().equals(sender.getUserID()) && !executor.getUserID().equals(chatRoom.getAdmin().getUserID())) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You do not have such permission.")));
        } else if (newContent.contains("hate")) {
            if (executor.getNumOfHate() == 0) {
                executor.sendResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildWarningResponse("Your message contains sensitive word.")));
            } else {
                executor.sendResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildErrorResponse("You have been banned.")));
            }
            executor.sentHate(chatRoom.getChatRoomID());
        } else {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String totalContent = executor.getUsername() + " modified messageID " + message.getMsgID() + " to " + newContent;
            message.editContent(newContent);
            Message newMsg = chatRoom.newMsg("system", ts, 0, 0, totalContent);
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg)
            ));
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateUserListResponse(chatRoom)
            ));
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildEditMessageResponse(chatRoom.getChatRoomID(),executor.getUserID(),message)
            ));
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildNotifyResponse(chatRoom.getChatRoomID(), newMsg.getMsgID())
            ));
        }
    }
}
