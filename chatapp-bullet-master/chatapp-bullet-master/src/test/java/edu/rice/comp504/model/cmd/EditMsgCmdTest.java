package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.request.*;
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

public class EditMsgCmdTest {
    private User executor;
    private ChatRoom chatRoom;
    private User admin;
    private String newContent;
    private Integer hateNum;

    public EditMsgCmdTest() {
        UserManager.getOnly().reset();
        ChatRoomManager.getOnly().reset();
        Session executorSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session adminSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        executor = new User(UserManager.getNextUserID(), "jack", 20, "rice university", new String[]{"swimming", "reading"}, executorSession);
        admin = new User(UserManager.getNextUserID(), "lucy", 20, "rice university", new String[]{"swimming", "reading"}, adminSession);
        UserManager.getOnly().newUser(executor);
        UserManager.getOnly().newUser(admin);

        chatRoom = ChatRoomManager.getOnly().addChatRoom("0606", false, admin.getUserID());
        chatRoom.addUser(executor.getUserID());
        chatRoom.addUser(admin.getUserID());

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        chatRoom.newMsg("private",ts,admin.getUserID(),executor.getUserID(),"hello");
        chatRoom.newMsg("public",ts,executor.getUserID(),0,"hello");
    }

    @Test
    public void BannedTest(){
        newContent = "hi";
        hateNum = 0;
        UserManager.getOnly().banUser(executor);
        EditMsgRequest editMsgRequest = RequestBuilder.buildEditMsgRequest(chatRoom.getChatRoomID(), executor.getUserID(), admin.getUserID(), chatRoom.getMsgList().get(0).getMsgID(),newContent);
        RequestDispatcher.handle(executor.getSession(), GsonInstance.getGson().toJson(editMsgRequest));

        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("You do not have such permission.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(executor.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(errorResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void NoRightToEditMsgTest(){
        newContent= "hi";
        hateNum = 0;
        EditMsgRequest editMsgRequest = RequestBuilder.buildEditMsgRequest(chatRoom.getChatRoomID(), executor.getUserID(), admin.getUserID(), chatRoom.getMsgList().get(0).getMsgID(),newContent);
        RequestDispatcher.handle(executor.getSession(), GsonInstance.getGson().toJson(editMsgRequest));

        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("You do not have such permission.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(executor.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(errorResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void FirstSensitiveWordTest(){
        newContent = "hate";
        hateNum = 0;
        EditMsgRequest editMsgRequest = RequestBuilder.buildEditMsgRequest(chatRoom.getChatRoomID(), executor.getUserID(), executor.getUserID(), chatRoom.getMsgList().get(1).getMsgID(),newContent);
        RequestDispatcher.handle(executor.getSession(), GsonInstance.getGson().toJson(editMsgRequest));

        WarningResponse warningResponse = ResponseBuilder.buildWarningResponse("Your message contains sensitive word.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(executor.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(warningResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SecondSensitiveWordTest(){
        newContent = "hate";
        hateNum = 1;
        EditMsgRequest editMsgRequest = RequestBuilder.buildEditMsgRequest(chatRoom.getChatRoomID(), executor.getUserID(), executor.getUserID(), chatRoom.getMsgList().get(1).getMsgID(),newContent);
        RequestDispatcher.handle(executor.getSession(), GsonInstance.getGson().toJson(editMsgRequest));

        WarningResponse warningResponse = ResponseBuilder.buildWarningResponse("Your message contains sensitive word.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(executor.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(warningResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SuccessfulEditMsgTest() {
        newContent = "hi";
        hateNum = 0;
        EditMsgRequest editMsgRequest = RequestBuilder.buildEditMsgRequest(chatRoom.getChatRoomID(), executor.getUserID(), executor.getUserID(), chatRoom.getMsgList().get(1).getMsgID(),newContent);
        RequestDispatcher.handle(executor.getSession(), GsonInstance.getGson().toJson(editMsgRequest));

        EditMsgResponse editMsgResponse = ResponseBuilder.buildEditMessageResponse(chatRoom.getChatRoomID(),executor.getUserID(),chatRoom.getMsgList().get(1));

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(executor.getSession().getRemote(), times(4)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(editMsgResponse), capturedResponses.get(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
