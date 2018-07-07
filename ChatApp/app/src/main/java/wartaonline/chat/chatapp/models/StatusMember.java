package wartaonline.chat.chatapp.models;

public class StatusMember {
    public String uid;
    public String status;

    public StatusMember() {
    }

    public StatusMember(String uid, String status) {
        this.uid = uid;
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
