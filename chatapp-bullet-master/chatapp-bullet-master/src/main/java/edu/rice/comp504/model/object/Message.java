package edu.rice.comp504.model.object;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * A message record in chat rooms.
 */
public class Message {
    private Integer msgID;
    private String type;
    private Timestamp timestamp;
    private Integer senderID;   // no need to keep the object.
    private Integer receiverID;
    private String content;

    /**
     * Constructor.
     */
    public Message(Integer msgID, String type, Timestamp timestamp, Integer senderID, Integer receiverID,String content) {
        this.msgID = msgID;
        this.type = type;
        this.timestamp = timestamp;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.content = content;
    }

    /**
     * Constructor.
     */
    public Integer getMsgID() {
        return msgID;
    }


    /**
     * Get the type of this message.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the timestamp.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Get the user ID of the sender.
     */
    public Integer getSenderID() {
        return senderID;
    }

    /**
     * Get the user ID of the receiver.
     */
    public Integer getReceiverID() {
        return receiverID;
    }

    /**
     * Get the content.
     */
    public String getContent() {
        return content;
    }


    /**
     * Set the content of the message.
     */
    public void editContent(String content) {
        this.content = content;
    }

}
