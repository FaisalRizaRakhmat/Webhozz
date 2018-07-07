package wartaonline.chat.chatapp.models;


import java.util.HashMap;

public class ChatGroup {
    public String createByUserId;
    public String id;
    public String roomname;
    public String username;
    public String avatar;
    public HashMap<String, Boolean> members;

    public ChatGroup() {
    }

    public ChatGroup(String createByUserId, String id, String roomname, String username, String image, HashMap<String , Boolean> members) {
        this.createByUserId = createByUserId;
        this.id = id;
        this.roomname = roomname;
        this.username = username;
        this.avatar = image;
        this.members = members;
    }
}
