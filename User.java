package hotel;

public class User {
    private String username;
    private String email;
    private String password;
    private String phone;

    public User(String username, String email, String password, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
}