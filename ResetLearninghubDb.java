import java.sql.*;
public class ResetLearninghubDb {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String pass = "postgres";
    try (Connection c = DriverManager.getConnection(url, user, pass)) {
      try (Statement s = c.createStatement()) {
        try { s.executeUpdate("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'learninghub' AND pid <> pg_backend_pid();"); } catch (Exception ignored) {}
        try { s.executeUpdate("DROP DATABASE learninghub WITH (FORCE);"); } catch (Exception ignored) {}
        s.executeUpdate("CREATE DATABASE learninghub");
        System.out.println("DATABASE_RESET_OK");
      }
    }
  }
}
