import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        DB_Functions db = new DB_Functions();
        Connection conn = db.connect_to_db("TestDB","postgres","inba1234$");
        db.createTable(conn, "TobleIsTable");
    }
}