import org.springframework.security.crypto.bcrypt.BCrypt;
public class VerifyHash {
  public static void main(String[] args) {
    String hash = "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi4FuyQ5WsUorYpGZxk6o9wVaZV7eWa";
    System.out.println(BCrypt.checkpw("password", hash));
  }
}
