package edu.rice.comp504.model.cmd;

import edu.rice.comp504.controller.ChatAppController;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Create a new chat room.
 */
public class SendMsgCmd implements ICommand {
    private ChatRoom chatRoom;
    private User sender;
    private User receiver;
    private String content;

    /**
     * Constructor.
     */
    SendMsgCmd(Integer chatRoomID, Integer senderID, Integer receiverID, String content) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        sender = UserManager.getOnly().getUser(senderID);
        if (receiverID > 0) {
            receiver = UserManager.getOnly().getUser(receiverID);
        }
        this.content = content;
    }

    /**
     * Execute the command.
     */
    public void execute() {
        System.out.println(content);
        if (chatRoom.isBanned(sender.getUserID())) {
            sender.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You have been banned.")));
        } else if (content == null) {
            sender.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse("You have to input something.")));
        } else if (content.contains("hate")) {
            System.out.println(1);
            if (sender.getNumOfHate() == 0) {
                sender.sendResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildWarningResponse("Your message contains sensitive word.")));
            } else {
                sender.sendResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildErrorResponse("You have been banned."))
                );
                chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                        ResponseBuilder.buildUpdateUserListResponse(chatRoom)
                ));
            }
            sender.sentHate(chatRoom.getChatRoomID());
        } else if (receiver == null) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Message newMsg = chatRoom.newMsg("public", ts, sender.getUserID(), 0, content);
            chatRoom.broadcastResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, sender.getUsername(), "everyone")
            ));
            sender.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildNotifyResponse(chatRoom.getChatRoomID(), newMsg.getMsgID())
            ));

        } else {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Message newMsg = chatRoom.newMsg("private", ts, sender.getUserID(), receiver.getUserID(), content);
            receiver.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, sender.getUsername(), receiver.getUsername())));
            sender.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildNotifyResponse(chatRoom.getChatRoomID(), newMsg.getMsgID())
            ));
        }
    }
}
