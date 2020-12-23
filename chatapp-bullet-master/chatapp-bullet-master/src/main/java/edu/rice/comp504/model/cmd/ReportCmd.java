package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

import java.sql.Timestamp;

/**
 * Report user.
 */
public class ReportCmd implements ICommand {
    private ChatRoom chatRoom;
    private User user;
    private String reason;
    private User admin;

    /**
     * Constructor.
     */
    ReportCmd(Integer chatRoomID, Integer userID, String reason) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        user = UserManager.getOnly().getUser(userID);
        this.reason = reason;
        admin = chatRoom.getAdmin();
    }

    /**
     * Execute the command.
     */
    public void execute() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String totalContent = user.getUsername() + " reported because " + reason;
        Message newMsg = chatRoom.newMsg("private", ts, user.getUserID(), admin.getUserID(), totalContent);
        admin.sendResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg)
        ));
    }
}