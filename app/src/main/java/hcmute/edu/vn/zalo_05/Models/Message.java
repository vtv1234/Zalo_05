package hcmute.edu.vn.zalo_05.Models;

import java.util.Date;

public class Message {
    private String senderNumberPhone;
    private String typeMessage;
    private String message;
    private Date sendAt;

    public Message() {
    }

    public Message(String senderNumberPhone, String typeMessage, String message, Date sendAt) {
        this.senderNumberPhone = senderNumberPhone;
        this.typeMessage = typeMessage;
        this.message = message;
        this.sendAt = sendAt;
    }

    public String getSenderNumberPhone() {
        return senderNumberPhone;
    }

    public void setSenderNumberPhone(String senderNumberPhone) {
        this.senderNumberPhone = senderNumberPhone;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSendAt() {
        return sendAt;
    }

    public void setSendAt(Date sendAt) {
        this.sendAt = sendAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderNumberPhone='" + senderNumberPhone + '\'' +
                ", typeMessage='" + typeMessage + '\'' +
                ", message='" + message + '\'' +
                ", sendAt=" + sendAt +
                '}';
    }
}
