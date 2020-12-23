package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.request.BanUserRequest;
import edu.rice.comp504.model.request.InviteUserRequest;
import edu.rice.comp504.model.request.SendMsgRequest;
import edu.rice.comp504.model.response.*;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import edu.rice.comp504.model.utilities.ResponseBuilder;
import edu.rice.comp504.model.utilities.TestUtilities;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SendMsgCmdTest {
    private User sender;
    private User receiver;
    private User admin;
    private String content;
    private ChatRoom chatRoom;
    private Integer hateNum;

    public SendMsgCmdTest(){
        Session senderSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session receiverSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session adminSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);

        admin = new User(UserManager.getNextUserID(), "sia", 20, "rice university", new String[]{"swimming", "reading"}, senderSession);
        receiver = new User(UserManager.getNextUserID(), "calvin", 20, "rice university", new String[]{"swimming", "reading"}, receiverSession);
        sender = new User(UserManager.getNextUserID(), "jack", 20, "rice university", new String[]{"swimming", "reading"}, adminSession);
        UserManager.getOnly().newUser(admin);
        UserManager.getOnly().newUser(sender);
        UserManager.getOnly().newUser(receiver);

        /*create a chat room with two users(one of them is admin)*/
        chatRoom = ChatRoomManager.getOnly().addChatRoom("0606", false, admin.getUserID());
        chatRoom.addUser(sender.getUserID());
        chatRoom.addUser(receiver.getUserID());
        chatRoom.addUser(admin.getUserID());
    }


    @Test
    public void NoRightToSendMsgTest(){
        UserManager.getOnly().banUser(sender);
        SendMsgRequest sendMsgRequest = RequestBuilder.buildSendMsgRequest(chatRoom.getChatRoomID(), sender.getUserID(), receiver.getUserID(), content);
        RequestDispatcher.handle(sender.getSession(), GsonInstance.getGson().toJson(sendMsgRequest));

        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("You have to input something.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(sender.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(errorResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void FirstSensitiveWordTest(){
        content = "hate";
        hateNum = 0;
        SendMsgRequest sendMsgRequest = RequestBuilder.buildSendMsgRequest(chatRoom.getChatRoomID(), sender.getUserID(), receiver.getUserID(), content);
        RequestDispatcher.handle(sender.getSession(), GsonInstance.getGson().toJson(sendMsgRequest));

        WarningResponse warningResponse = ResponseBuilder.buildWarningResponse("Your message contains sensitive word.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(sender.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(warningResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SecondSensitiveWordTest(){
        content = "hate";
        hateNum = 1;
        SendMsgRequest sendMsgRequest = RequestBuilder.buildSendMsgRequest(chatRoom.getChatRoomID(), sender.getUserID(), receiver.getUserID(), content);
        RequestDispatcher.handle(sender.getSession(), GsonInstance.getGson().toJson(sendMsgRequest));

        WarningResponse warningResponse = ResponseBuilder.buildWarningResponse("Your message contains sensitive word.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(sender.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(warningResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SuccessfulSendTest(){
        content = "666";
        hateNum = 1;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Message newMsg = chatRoom.newMsg("private", ts, sender.getUserID(), receiver.getUserID(), content);

        SendMsgRequest sendMsgRequest = RequestBuilder.buildSendMsgRequest(chatRoom.getChatRoomID(), sender.getUserID(), receiver.getUserID(), content);
        RequestDispatcher.handle(sender.getSession(), GsonInstance.getGson().toJson(sendMsgRequest));

        NotifyResponse notifyResponse = ResponseBuilder.buildNotifyResponse(chatRoom.getChatRoomID(), newMsg.getMsgID() + 1);

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(sender.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(notifyResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
