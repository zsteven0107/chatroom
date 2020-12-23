package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

/**
 * Create a new chat room.
 */
public class CreateRoomCmd implements ICommand {
    private String chatRoomName;
    private Boolean isPrivate;
    private Integer adminID;
    /**
     * Constructor.
     */
    CreateRoomCmd(String chatRoomName, Integer adminID, Boolean isPrivate) {
        this.chatRoomName = chatRoomName;
        this.isPrivate = isPrivate;
        this.adminID = adminID;
    }

    /**
     * Execute the command.
     */
    public void execute() {
        ChatRoom newChatRoom = ChatRoomManager.getOnly().addChatRoom(chatRoomName, isPrivate, adminID);
        // if room is public, update all users' public room lists
        if (isPrivate.equals(false)) {
            UserManager.getOnly().broadcastResponsePerUser(u -> GsonInstance.getGson().toJson(
                    ResponseBuilder.buildUpdateChatRoomListResponse(u)
            ));
        }
        // whether the room is public or not，update admin’s own list
        User admin = newChatRoom.getAdmin();
        UpdateChatRoomListResponse updateChatRoomListResponse = ResponseBuilder.buildUpdateOwnChatRoomListResponse(admin);
        admin.sendResponse(GsonInstance.getGson().toJson(updateChatRoomListResponse));
    }
}

