package edu.rice.comp504.model.utilities;

import edu.rice.comp504.model.response.AddMsgResponse;

public class TestUtilities {

    /**
     * Verify if two messages have correct contents.
     */
    public static boolean verifyMessage(String expectedMsg, String actualMsg) {
        AddMsgResponse expectedResponse = GsonInstance.getGson().fromJson(expectedMsg, AddMsgResponse.class);
        AddMsgResponse actualResponse = GsonInstance.getGson().fromJson(actualMsg, AddMsgResponse.class);
        AddMsgResponse.Data expectedData = expectedResponse.getData();
        AddMsgResponse.Data actualData = actualResponse.getData();
        if (expectedResponse.getResponse().equals(actualResponse.getResponse())
                && expectedData.getChatRoomID() == actualData.getChatRoomID()
                && expectedData.getUserID() == actualData.getUserID()
                && expectedData.getSenderName().equals(actualData.getSenderName())
                && expectedData.getMessageID() == actualData.getMessageID()
                && expectedData.getMessageType().equals(actualData.getMessageType())
                && expectedData.getReceiverID() == actualData.getReceiverID()
                && expectedData.getReceiverName().equals(actualData.getReceiverName())
                && expectedData.getContent().equals(actualData.getContent())) {
            return true;
        }
        return false;

    }
}
