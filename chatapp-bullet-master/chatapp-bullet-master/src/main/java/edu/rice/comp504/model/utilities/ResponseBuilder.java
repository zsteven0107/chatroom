package edu.rice.comp504.model.utilities;

import edu.rice.comp504.model.cmd.CommandFactory;
import edu.rice.comp504.model.cmd.ICommand;
import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.Message;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.response.*;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;

public class ResponseBuilder {

    public interface BroadcastInterface {
        String getResponse(User u);
    }

    /**
     * Build response containing public chat room list.
     *
     * @return UpdateChatRoomListResponse
     */
    public static UpdateChatRoomListResponse buildUpdateChatRoomListResponse(User user) {
        ChatRoom[] publicChatRooms = ChatRoomManager.getOnly().getPublicChatRooms();
        ArrayList<UpdateChatRoomListResponse.Data.ChatRoomInfo> chatRoomInfos = new ArrayList<>();
        for (int i = 0; i < publicChatRooms.length; i++) {
            ChatRoom chatRoom = publicChatRooms[i];
            if (user.hasJoined(chatRoom)) {
                continue;
            }
            chatRoomInfos.add(UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                    .chatRoomID(chatRoom.getChatRoomID())
                    .chatRoomName(chatRoom.getChatRoomName())
                    .build());
        }
        UpdateChatRoomListResponse updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(UpdateChatRoomListResponse.Data.builder()
                        .listType("public")
                        .chatRoomList(chatRoomInfos.toArray(new UpdateChatRoomListResponse.Data.ChatRoomInfo[chatRoomInfos.size()]))
                        .build())
                .build();
        return updateChatRoomListResponse;
    }

    /**
     * Build response containing the admin's joined rooms.
     *
     * @return UpdateChatRoomListResponse
     */
    public static UpdateChatRoomListResponse buildUpdateOwnChatRoomListResponse(User user) {
        ChatRoom[] chatRooms = user.getJointChatRooms().toArray(new ChatRoom[0]);
        UpdateChatRoomListResponse.Data.ChatRoomInfo[] chatRoomInfos =
                new UpdateChatRoomListResponse.Data.ChatRoomInfo[chatRooms.length];
        for (int i = 0; i < chatRooms.length; i++) {
            ChatRoom chatRoom = chatRooms[i];
            chatRoomInfos[i] = UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                    .chatRoomID(chatRoom.getChatRoomID())
                    .chatRoomName(chatRoom.getChatRoomName())
                    .build();
        }
        UpdateChatRoomListResponse updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(UpdateChatRoomListResponse.Data.builder()
                        .listType("own")
                        .chatRoomList(chatRoomInfos).build())
                .build();
        return updateChatRoomListResponse;
    }

    /**
     * Build response containing the admin's joined rooms.
     *
     * @return UpdateChatRoomListResponse
     */
    public static UpdateUserListResponse buildUpdateUserListResponse(ChatRoom chatRoom) {
        User[] users = chatRoom.getUserList().toArray(new User[0]);
        UpdateUserListResponse.Data.UserInfo[] userInfos =
                new UpdateUserListResponse.Data.UserInfo[users.length];
        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            Integer userID = user.getUserID();
            userInfos[i] = UpdateUserListResponse.Data.UserInfo.builder()
                    .userName(user.getUsername())
                    .userID(userID)
                    .isBanned(chatRoom.isBanned(userID))
                    .isAdmin(chatRoom.getAdmin().getUserID().equals(userID))
                    .build();
        }
        UpdateUserListResponse updateUserListResponse = UpdateUserListResponse.builder()
                .response("update_user_list")
                .data(UpdateUserListResponse.Data.builder()
                        .userList(userInfos).build())
                .build();
        return updateUserListResponse;
    }

    /**
     * Build response containing the admin's joined rooms.
     *
     * @return UpdateChatRoomListResponse
     */
    public static AddMsgResponse buildAddMessageResponse(Integer chatRoomID, Message msg, String senderName, String receiverName) {
        AddMsgResponse addMsgResponse = AddMsgResponse.builder()
                .response("add_msg")
                .data(AddMsgResponse.Data.builder()
                        .chatRoomID(chatRoomID)
                        .userID(msg.getSenderID())
                        .senderName(senderName)
                        .messageID(msg.getMsgID())
                        .messageType(msg.getType())
                        .timestamp(msg.getTimestamp().toString())
                        .receiverID(msg.getReceiverID())
                        .receiverName(receiverName)
                        .content(msg.getContent())
                        .build())
                .build();
        return addMsgResponse;
    }

    /**
     * Build add message response.
     *
     * @return AddMsgResponse
     */
    public static AddMsgResponse buildAddMessageResponse(Integer chatRoomID, Message msg) {
        AddMsgResponse addMsgResponse = AddMsgResponse.builder()
                .response("add_msg")
                .data(AddMsgResponse.Data.builder()
                        .chatRoomID(chatRoomID)
                        .userID(msg.getSenderID())
                        .messageID(msg.getMsgID())
                        .messageType(msg.getType())
                        .timestamp(msg.getTimestamp().toString())
                        .receiverID(msg.getReceiverID())
                        .content(msg.getContent())
                        .build())
                .build();
        return addMsgResponse;
    }
    /**
     * Build response containing the admin's joined rooms.
     *
     * @return UpdateChatRoomListResponse
     */
    public static AskApproveResponse buildAskApproveResponse(ChatRoom chatRoom, User user) {
        AskApproveResponse askApproveResponse = AskApproveResponse.builder()
                .response("ask_approve")
                .data(AskApproveResponse.Data.builder()
                        .chatRoomID(chatRoom.getChatRoomID())
                        .chatRoomName(chatRoom.getChatRoomName())
                        .userID(user.getUserID())
                        .userName(user.getUsername())
                        .build())
                .build();
        return askApproveResponse;
    }

    /**
     * Build response containing error message.
     *
     * @return UpdateChatRoomListResponse
     */
    public static ErrorResponse buildErrorResponse(String reason) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .response("error")
                .data(ErrorResponse.Data.builder()
                        .reason(reason)
                        .build())
                .build();
        return errorResponse;
    }

    /**
     * Build response containing user's profile(containing user id).
     *
     * @return ReturnProfileResponse
     */
    public static ReturnProfileResponse buildProfileResponse(User user) {
        ReturnProfileResponse.Data data = ReturnProfileResponse.Data.builder()
                .age(user.getAge())
                .userName(user.getUsername())
                .school(user.getSchool())
                .userID(user.getUserID())
                .interests(user.getInterests())
                .build();

        ReturnProfileResponse returnProfileResponse = ReturnProfileResponse.builder()
                .response("return_profile")
                .data(data)
                .build();
        return returnProfileResponse;
    }

    /**
     * Build edit messsage response.
     *
     * @return EditMsgResponse
     */
    public static EditMsgResponse buildEditMessageResponse(Integer chatRoomID, Integer executorID, Message msg) {
        EditMsgResponse editMsgResponse = EditMsgResponse.builder()
                .response("edit_msg")
                .data(EditMsgResponse.Data.builder()
                        .chatRoomID(chatRoomID)
                        .executorID(executorID)
                        .senderID(msg.getSenderID())
                        .messageID(msg.getMsgID())
                        .messageType(msg.getType())
                        .timestamp(msg.getTimestamp().toString())
                        .content(msg.getContent())
                        .build())
                .build();
        return editMsgResponse;
    }

    /**
     * Build recall messsage response.
     *
     * @return RecallMsgResponse
     */
    public static RecallMsgResponse buildRecallMessageResponse(Integer chatRoomID, Integer executorID, Message msg) {
        RecallMsgResponse recallMsgResponse = RecallMsgResponse.builder()
                .response("recall_msg")
                .data(RecallMsgResponse.Data.builder()
                        .chatRoomID(chatRoomID)
                        .executorID(executorID)
                        .senderID(msg.getSenderID())
                        .messageID(msg.getMsgID())
                        .messageType(msg.getType())
                        .timestamp(msg.getTimestamp().toString())
                        .build())
                .build();
        return recallMsgResponse;
    }

    /**
     * Build get message response.
     *
     * @return GetMsgResponse
     */
    public static GetMsgResponse buildGetMessageResponse(Integer chatRoomID) {
        List<Message> messageList = ChatRoomManager.getOnly().getChatRoom(chatRoomID).getMsgList();
        GetMsgResponse.Data.MessageInfo[] messageInfos =
                new GetMsgResponse.Data.MessageInfo[messageList.size()];
        for (int i = 0; i < messageList.size(); i++) {
            Message msg = messageList.get(i);
            messageInfos[i] = GetMsgResponse.Data.MessageInfo.builder()
                    .chatRoomID(chatRoomID)
                    .userID(msg.getSenderID())
                    .messageID(msg.getMsgID())
                    .messageType(msg.getType())
                    .timestamp(msg.getTimestamp().toString())
                    .receiverID(msg.getReceiverID())
                    .content(msg.getContent())
                    .build();
        }
        GetMsgResponse getMsgResponse = GetMsgResponse.builder()
                .response("return_msgs")
                .data(GetMsgResponse.Data.builder()
                        .messageList(messageInfos)
                        .build())
                .build();
        return getMsgResponse;
    }

    /**
     * Build response containing warning message.
     *
     * @return WarningResponse
     */
    public static WarningResponse buildWarningResponse(String reason) {
        WarningResponse warningResponse = WarningResponse.builder()
                .response("warning")
                .data(WarningResponse.Data.builder()
                        .reason(reason)
                        .build())
                .build();
        return warningResponse;
    }

    /**
     * Build response containing the admin's joined rooms.
     *
     * @return UpdateChatRoomListResponse
     */
    public static NotifyResponse buildNotifyResponse(Integer chatRoomID, Integer messageID) {
        NotifyResponse notifyResponse = NotifyResponse.builder()
                .response("notify_receive")
                .data(NotifyResponse.Data.builder()
                        .chatRoomID(chatRoomID)
                        .messageID(messageID)
                        .build())
                .build();
        return notifyResponse;
    }
}