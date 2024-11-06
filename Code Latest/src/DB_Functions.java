import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DB_Functions {
    public Connection connect_to_db(String dbname , String uname, String pass){
            Connection con = null;
            try {
                Class.forName("org.postgresql.Driver");
                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,uname,pass);
                if(con!=null){
                    System.out.println("Connecction Established :)");
                }else{
                    System.out.println("Connection Failed :(");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return con;
        }

        public void createTable(Connection conn , String tableName){
            Statement statement;
            try{
                String query = "create table "+tableName+"(empid SERIAL, name varchar(200), address varchar(200),primary key(empid));";
                statement = conn.createStatement();
                statement.executeUpdate(query);
                System.out.println("Table Created..... YAYYYY");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

}
