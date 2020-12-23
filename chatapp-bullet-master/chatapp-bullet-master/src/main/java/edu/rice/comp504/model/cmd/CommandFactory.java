package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.request.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import org.eclipse.jetty.websocket.api.Session;

public class CommandFactory {

    /**
     * Get a command according to the message.
     */
    public static ICommand getCommand(Session session, String message) {
        ICommand cmd = null;
        IRequest request = GsonInstance.getGson().fromJson(message, IRequest.class);
        if (request.getRequest().equals("register")) {
            RegisterRequest registerRequest = GsonInstance.getGson().fromJson(message, RegisterRequest.class);
            cmd = new RegisterCmd(
                    registerRequest.getData().getUserName(),
                    registerRequest.getData().getAge(),
                    registerRequest.getData().getSchool(),
                    registerRequest.getData().getInterests(),
                    session);
        }
        if (request.getRequest().equals("create_room")) {
            CreateRoomRequest createRoomRequest = GsonInstance.getGson().fromJson(message, CreateRoomRequest.class);
            cmd = new CreateRoomCmd(
                    createRoomRequest.getData().getChatRoomName(),
                    createRoomRequest.getData().getUserID(),
                    createRoomRequest.getData().getIsPrivate()
            );
        }
        if (request.getRequest().equals("approve")) {
            ApproveRequest approveRequest = GsonInstance.getGson().fromJson(message, ApproveRequest.class);
            cmd = new ApproveCmd(
                    approveRequest.getData().getChatRoomID(),
                    approveRequest.getData().getUserID(),
                    approveRequest.getData().getIsApprove()
            );
        }
        if (request.getRequest().equals("exit")) {
            ExitRequest exitRequest = GsonInstance.getGson().fromJson(message, ExitRequest.class);
            cmd = new ExitRoomCmd(
                    exitRequest.getData().getChatRoomID(),
                    exitRequest.getData().getUserID()
            );
        }
        if (request.getRequest().equals("join")) {
            JoinRequest joinRequest = GsonInstance.getGson().fromJson(message, JoinRequest.class);
            cmd = new JoinRoomCmd(
                    joinRequest.getData().getChatRoomID(),
                    joinRequest.getData().getUserID()
            );
        }
        if (request.getRequest().equals("close_connection")) {
            CloseConnectionRequest closeConnectionRequest = GsonInstance.getGson().fromJson(message, CloseConnectionRequest.class);
            cmd = new CloseConnectionCmd(
                    closeConnectionRequest.getData().getUserID()
            );
        }
        if (request.getRequest().equals("get_user_list")) {
            GetUserListRequest getUserListRequest = GsonInstance.getGson().fromJson(message, GetUserListRequest.class);
            cmd = new GetUserListCmd(
                    getUserListRequest.getData().getChatRoomID(),
                    getUserListRequest.getData().getUserID()
            );
        }
        if (request.getRequest().equals("ban")) {
            BanUserRequest banUserRequest = GsonInstance.getGson().fromJson(message, BanUserRequest.class);
            cmd = new BanUserCmd(
                    banUserRequest.getData().getChatRoomID(),
                    banUserRequest.getData().getUserID(),
                    banUserRequest.getData().getExecutorID()
            );
        }
        if (request.getRequest().equals("invite")) {
            InviteUserRequest inviteUserRequest = GsonInstance.getGson().fromJson(message, InviteUserRequest.class);
            cmd = new InviteUserCmd(
                    inviteUserRequest.getData().getChatRoomID(),
                    inviteUserRequest.getData().getUserID(),
                    inviteUserRequest.getData().getExecutorID()
            );
        }
        if (request.getRequest().equals("send_msg")) {
            SendMsgRequest sendMsgRequest = GsonInstance.getGson().fromJson(message, SendMsgRequest.class);
            cmd = new SendMsgCmd(
                    sendMsgRequest.getData().getChatRoomID(),
                    sendMsgRequest.getData().getSenderID(),
                    sendMsgRequest.getData().getReceiverID(),
                    sendMsgRequest.getData().getContent()
            );
        }
        if (request.getRequest().equals("edit_msg")) {
            EditMsgRequest editMsgRequest = GsonInstance.getGson().fromJson(message, EditMsgRequest.class);
            cmd = new EditMsgCmd(
                    editMsgRequest.getData().getChatRoomID(),
                    editMsgRequest.getData().getExecutorID(),
                    editMsgRequest.getData().getSenderID(),
                    editMsgRequest.getData().getNewContent(),
                    editMsgRequest.getData().getMessageID()
            );
        }
        if (request.getRequest().equals("recall_msg")) {
            RecallMsgRequest recallMsgRequest = GsonInstance.getGson().fromJson(message, RecallMsgRequest.class);
            cmd = new RecallMsgCmd(
                    recallMsgRequest.getData().getChatRoomID(),
                    recallMsgRequest.getData().getExecutorID(),
                    recallMsgRequest.getData().getMessageID()
            );
        }
        if (request.getRequest().equals("get_msgs")) {
            GetMsgRequest getMsgRequest = GsonInstance.getGson().fromJson(message, GetMsgRequest.class);
            cmd = new GetMsgCmd(
                    getMsgRequest.getData().getChatRoomID(),
                    getMsgRequest.getData().getUserID()
            );
        }
        if (request.getRequest().equals("report")) {
            ReportRequest reportRequest = GsonInstance.getGson().fromJson(message, ReportRequest.class);
            cmd = new ReportCmd(
                    reportRequest.getData().getChatRoomID(),
                    reportRequest.getData().getUserID(),
                    reportRequest.getData().getReason()
            );
        }
        if (request.getRequest().equals("heart_beat")) {
            cmd = new HeartBeatCmd();
        }
        return cmd;
    }
}
