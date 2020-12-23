package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;


import java.sql.Timestamp;

public class GetMsgCmd implements ICommand {
    private ChatRoom chatRoom;
    private User user;

    /**
     * Constructor.
     */
    GetMsgCmd(Integer chatRoomID, Integer userID) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        user = UserManager.getOnly().getUser(userID);
    }

    /**
     * Execute the command.
     */
    public void execute() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String totalContent = "Message History";
        chatRoom.newMsg("system", ts, 0, user.getUserID(), totalContent);
        user.sendResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildGetMessageResponse(chatRoom.getChatRoomID())
        ));

    }
}
