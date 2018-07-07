package wartaonline.chat.chatapp.models;


import java.util.HashMap;
import java.util.Map;

public class Message {
    public String uid;
    public String name;
    public String avatar;
    public String message;
    public String tanggal;
    //public Map serverdate = new HashMap();
    public int tipe;
    public String originalname;

    public Message() {
    }

    public Message(String uid, String name, String avatar, String message, String tanggal) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.message = message;
        this.tanggal = tanggal;
        //this.serverdate.put("time", serverdate);
        //this.serverdate = serverdate;
    }

    public Message(String uid, String name, String avatar, String message, String tanggal, int tipe) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.message = message;
        this.tanggal = tanggal;
        //this.serverdate.put("time", serverdate);
        this.tipe = tipe;
    }

    public String getTanggal() {
        return this.tanggal;
    }
}


