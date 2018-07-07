package wartaonline.chat.chatapp.models;



public class UserTemp {

    public String uid;
    public String name;
    public String email;
    public String image;
    public String DOB;

    public String token;


    public UserTemp() {
    }

    public UserTemp(String uid, String name, String email, String image, String DOB) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.image = image;
        this.DOB = DOB;
    }

    public UserTemp(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }
}
