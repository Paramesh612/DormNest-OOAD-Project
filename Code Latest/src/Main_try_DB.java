import java.sql.Connection;

public class Main_try_DB {
    public static void main(String[] args) {
        DB_Functions db = new DB_Functions();
        // Connection conn = db.connect_to_db("TestDB", "postgres", "root");
        db.createTable(conn, "TobleIsTable");
    }
}