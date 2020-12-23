package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

/**
 * Create a new chat room.
 */
public class GetUserListCmd implements ICommand {
    ChatRoom chatRoom;
    User user;
    /**
     * Constructor.
     */
    GetUserListCmd(Integer chatRoomID, Integer userID) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        user = UserManager.getOnly().getUser(userID);
    }

    /**
     * Execute the command.
     */
    public void execute() {
        user.sendResponse(GsonInstance.getGson().toJson(
                ResponseBuilder.buildUpdateUserListResponse(chatRoom)
        ));
    }
}
