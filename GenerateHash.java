import org.springframework.security.crypto.bcrypt.BCrypt;
public class GenerateHash {
  public static void main(String[] args) {
    String hash = BCrypt.hashpw("password", BCrypt.gensalt(10));
    System.out.println(hash);
  }
}
