package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Create a new chat room.
 */
public class RecallMsgCmd implements ICommand {
    private ChatRoom chatRoom;
    private User executor;
    private User sender;
    private Message message;

    /**
     * Constructor.
     */
    RecallMsgCmd(Integer chatRoomID, Integer executorID, Integer messageID) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        executor = UserManager.getOnly().getUser(executorID);
        message = ChatRoomManager.getOnly().getChatRoom(chatRoomID).getMsg(messageID);
        if (message.getSenderID() > 0) {
            sender = UserManager.getOnly().getUser(message.getSenderID());
        }
    }

    /**
     * Execute the command.
     */
    public void execute() {
        if (chatRoom.getMsg(message.getMsgID()) == null) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("No such message.")));
        } else if (sender == null) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You cannot recall system message.")));
        } else if (!executor.getUserID().equals(sender.getUserID()) && !executor.getUserID().equals(chatRoom.getAdmin().getUserID())) {
            executor.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You do not have such permission.")));
        } else {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String totalContent = executor.getUsername() + " recalled message with messageID " + message.getMsgID();
            chatRoom.removeMsg(message.getMsgID());
            Message newMsg = chatRoom.newMsg("system", ts, 0, 0, totalContent);
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildRecallMessageResponse(chatRoom.getChatRoomID(),executor.getUserID(),message)
            ));
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(),newMsg)
            ));
        }
    }
}
