package wartaonline.chat.chatapp.models;



public class User {

    public String uid;
    public String name;
    public String email;
    public String image;
    public String DOB;
    public String token;
    public String alamat;
    public String HP;


    public User() {
    }

    public User(String uid, String name, String email, String image, String DOB) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
        this.DOB = DOB;
        this.alamat = alamat;
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.alamat = alamat;
    }
}
