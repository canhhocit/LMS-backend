import org.springframework.security.crypto.bcrypt.BCrypt;
public class VerifyCorrectHash {
  public static void main(String[] args) {
    String hash = "$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm";
    System.out.println(BCrypt.checkpw("password", hash));
  }
}
