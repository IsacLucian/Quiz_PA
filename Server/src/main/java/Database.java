import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection con = null;

    static
    {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "QUIZDB";
        String pass = "QUIZDB";
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("conn");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection()
    {
        return con;
    }
}
