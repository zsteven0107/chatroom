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
public class JoinRoomCmd implements ICommand {
    private ChatRoom chatRoom;
    private User user;
    /**
     * Constructor.
     */
    JoinRoomCmd(Integer chatRoomID, Integer userID) {
        chatRoom = ChatRoomManager.getOnly().getChatRoom(chatRoomID);
        user = UserManager.getOnly().getUser(userID);
    }

    /**
     * Execute the command.
     */
    public void execute() {
        if (UserManager.getOnly().isBanned(user.getUserID())) {
            String reason = "sorry, you have been banned and cannot join chat rooms";
            user.sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildErrorResponse(reason)
            ));
        } else {
            // ask admin to approve the request
            chatRoom.getAdmin().sendResponse(GsonInstance.getGson().toJson(
                    ResponseBuilder.buildAskApproveResponse(chatRoom, user)
            ));
        }
    }
}
