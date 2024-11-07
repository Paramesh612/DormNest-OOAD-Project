import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Functions {
    public Connection connect_to_db(String dbname, String user, String pass) {
        Connection conn = null;
        try {
            String url = "jdbc:postgresql://localhost:5432/" + dbname;
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }
}
