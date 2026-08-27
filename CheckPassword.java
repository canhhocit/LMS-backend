import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class CheckPassword {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://localhost:5432/learninghub";
    String user = "postgres";
    String pass = "postgres";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (PreparedStatement ps = c.prepareStatement("SELECT email, password, status FROM users WHERE email IN (?, ?, ?) ORDER BY email")) {
        ps.setString(1, "admin@learninghub.edu.vn");
        ps.setString(2, "gv.nguyenvana@learninghub.edu.vn");
        ps.setString(3, "sv20240001@student.edu.vn");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          String email = rs.getString("email");
          String hash = rs.getString("password");
          String status = rs.getString("status");
          System.out.println(email + " | status=" + status + " | hash=" + hash);
          System.out.println("matches password? " + BCrypt.checkpw("password", hash));
        }
      }
    }
  }
}
