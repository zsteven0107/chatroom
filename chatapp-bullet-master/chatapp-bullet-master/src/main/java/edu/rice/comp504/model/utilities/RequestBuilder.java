package edu.rice.comp504.model.utilities;



import edu.rice.comp504.model.request.*;


public class RequestBuilder {

    /**
     * Build a request with intended fields.
     */
    public static InviteUserRequest buildInviteUserRequest(Integer chatRoomID, Integer userID, Integer executorID) {
        InviteUserRequest.Data inviteUserRequestData = InviteUserRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .executorID(executorID)
                .build();
        return InviteUserRequest.builder()
                .request("invite")
                .data(inviteUserRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static BanUserRequest buildBanUserRequest(Integer chatRoomID, Integer userID, Integer executorID) {
        BanUserRequest.Data banUserRequestData = BanUserRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .executorID(executorID)
                .build();
        return BanUserRequest.builder()
                .request("ban")
                .data(banUserRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static RegisterRequest buildRegisterRequest(String userName, int age, String school, String[] interests) {
        RegisterRequest.Data registerRequestData = RegisterRequest.Data.builder()
                .userName(userName)
                .age(age)
                .school(school)
                .interests(interests)
                .build();
        return RegisterRequest.builder()
                .request("register")
                .data(registerRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static CreateRoomRequest buildCreateRoomRequest(String chatRoomName, Integer userID, Boolean isPrivate) {
        CreateRoomRequest.Data createRoomRequestData = CreateRoomRequest.Data.builder()
                .chatRoomName(chatRoomName)
                .userID(userID)
                .isPrivate(isPrivate)
                .build();
        return CreateRoomRequest.builder()
                .request("create_room")
                .data(createRoomRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static ApproveRequest buildApproveRequest(Integer chatRoomID, Integer userID, Boolean isApprove) {
        ApproveRequest.Data approveRequestData = ApproveRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .isApprove(isApprove)
                .build();
        return ApproveRequest.builder()
                .request("approve")
                .data(approveRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static ExitRequest buildExitRequest(Integer chatRoomID, Integer userID) {
        ExitRequest.Data exitRequestData = ExitRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .build();
        return ExitRequest.builder()
                .request("exit")
                .data(exitRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static JoinRequest buildJoinRequest(Integer chatRoomID, Integer userID) {
        JoinRequest.Data joinRequestData = JoinRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .build();
        return JoinRequest.builder()
                .request("join")
                .data(joinRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static CloseConnectionRequest buildCloseConnectionRequest(Integer userID) {
        CloseConnectionRequest.Data closeConnectionRequestData = CloseConnectionRequest.Data.builder()
                .userID(userID)
                .build();
        return CloseConnectionRequest.builder()
                .request("close_connection")
                .data(closeConnectionRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static GetUserListRequest buildCGetUserListRequest(Integer chatRoomID, Integer userID) {
        GetUserListRequest.Data getUserListRequestData = GetUserListRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .build();
        return GetUserListRequest.builder()
                .request("get_user_list")
                .data(getUserListRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static SendMsgRequest buildSendMsgRequest(Integer chatRoomID, Integer senderID, Integer receiverID, String content) {
        SendMsgRequest.Data sendMsgRequestData = SendMsgRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .senderID(senderID)
                .receiverID(receiverID)
                .content(content)
                .build();
        return SendMsgRequest.builder()
                .request("send_msg")
                .data(sendMsgRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static GetMsgRequest buildGetMsgRequest(Integer chatRoomID, Integer userID) {
        GetMsgRequest.Data getMsgRequestData = GetMsgRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .build();
        return GetMsgRequest.builder()
                .request("get_msgs")
                .data(getMsgRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static RecallMsgRequest buildRecallMsgRequest(Integer chatRoomID, Integer executorID, Integer senderID, Integer messageID) {
        RecallMsgRequest.Data recallMsgRequestData = RecallMsgRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .executorID(executorID)
                .senderID(senderID)
                .messageID(messageID)
                .build();
        return RecallMsgRequest.builder()
                .request("recall_msg")
                .data(recallMsgRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static ReportRequest buildReportRequest(Integer chatRoomID, Integer userID, String reason) {
        ReportRequest.Data reportRequestData = ReportRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .userID(userID)
                .reason(reason)
                .build();
        return ReportRequest.builder()
                .request("report")
                .data(reportRequestData)
                .build();
    }

    /**
     * Build a request with intended fields.
     */
    public static EditMsgRequest buildEditMsgRequest(Integer chatRoomID, Integer executorID, Integer senderID, Integer messageID, String newContent) {
        EditMsgRequest.Data editMsgRequestData = EditMsgRequest.Data.builder()
                .chatRoomID(chatRoomID)
                .executorID(executorID)
                .senderID(senderID)
                .messageID(messageID)
                .newContent(newContent)
                .build();
        return EditMsgRequest.builder()
                .request("edit_msg")
                .data(editMsgRequestData)
                .build();
    }
}
