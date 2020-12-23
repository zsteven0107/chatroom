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

public class ReportCmdTest {
    private ChatRoom chatRoom;
    private User executor;
    private User admin;
    private String reason;

    public ReportCmdTest() {
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
        chatRoom.newMsg("private",ts,admin.getUserID(),executor.getUserID(),"dumbass");

        reason = "bad language";
    }

    @Test
    public void SuccessfulReportTest(){
        ReportRequest reportRequest = RequestBuilder.buildReportRequest(chatRoom.getChatRoomID(), executor.getUserID(), reason);
        RequestDispatcher.handle(executor.getSession(), GsonInstance.getGson().toJson(reportRequest));

        AddMsgResponse addMsgResponse = ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(),chatRoom.getMsgList().get(0));

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(executor.getSession().getRemote(), times(0)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(addMsgResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
