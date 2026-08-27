import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class FixDemoPasswords {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://localhost:5432/learninghub";
    String user = "postgres";
    String pass = "postgres";
    String correctHash = "$2a$10$GW9Yqf8hURrioCQDeh/gR.D6gjJBizVdlgh2mB/8CyjbjXudcaSZm";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      String sql = "UPDATE users SET password = ? WHERE email IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      try (PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, correctHash);
        String[] emails = {
          "admin@learninghub.edu.vn",
          "gv.nguyenvana@learninghub.edu.vn",
          "gv.tranthib@learninghub.edu.vn",
          "gv.leminhc@learninghub.edu.vn",
          "sv20240001@student.edu.vn",
          "sv20240002@student.edu.vn",
          "sv20240003@student.edu.vn",
          "sv20240004@student.edu.vn",
          "sv20240005@student.edu.vn",
          "sv20240006@student.edu.vn",
          "sv20240007@student.edu.vn",
          "sv20240008@student.edu.vn"
        };
        for (int i = 0; i < emails.length; i++) {
          ps.setString(i + 2, emails[i]);
        }
        int updated = ps.executeUpdate();
        System.out.println("updated rows=" + updated);
      }
      try (PreparedStatement ps = c.prepareStatement("SELECT email, password FROM users WHERE email IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ORDER BY email")) {
        String[] emails = {
          "admin@learninghub.edu.vn",
          "gv.nguyenvana@learninghub.edu.vn",
          "gv.tranthib@learninghub.edu.vn",
          "gv.leminhc@learninghub.edu.vn",
          "sv20240001@student.edu.vn",
          "sv20240002@student.edu.vn",
          "sv20240003@student.edu.vn",
          "sv20240004@student.edu.vn",
          "sv20240005@student.edu.vn",
          "sv20240006@student.edu.vn",
          "sv20240007@student.edu.vn",
          "sv20240008@student.edu.vn"
        };
        for (int i = 0; i < emails.length; i++) {
          ps.setString(i + 1, emails[i]);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          String email = rs.getString("email");
          String hash = rs.getString("password");
          System.out.println(email + " -> " + hash + " | matches password? " + BCrypt.checkpw("password", hash));
        }
      }
    }
  }
}
