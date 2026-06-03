package hotel.model;

public class User {
    private int idAkun;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String alamatString;
    private String namaString;
    private String jenisKelamin;

    public User(int idAkun, String username, String email, String password, String phone,
        String alamatString, String namaString, String jenisKelamin) 
    {
        this.idAkun = idAkun;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.alamatString = alamatString;
        this.namaString = namaString;
        this.jenisKelamin = jenisKelamin;
    }

    public int getIdAkun() { return idAkun; }
    public void setIdAkun(int idAkun) {this.idAkun = idAkun;}

    public String getUsername() { return username; }
    public void setUsername(String username) {this.username = username;}
    
    public String getPassword() { return password; }
    public void setPassword(String password) {this.password = password;}
    
    public String getEmail()    { return email; }
    public void setEmail(String email) {this.email = email;}
   
    public String getPhone()    { return phone; }
    public void setPhone(String phone) {this.phone = phone;}
   
    public String getAlamatString() {return alamatString;}
    public void setAlamatString(String alamatString) {this.alamatString = alamatString;}
   
    public String getNamaString() {return namaString;}
    public void setNamaString(String namaString) {this.namaString = namaString;}

    public String getJenisKelamin() {return jenisKelamin;}
    public void setJenisKelamin(String jenisKelamin) {this.jenisKelamin = jenisKelamin;}
}
