import oracle.jdbc.proxy.annotation.Pre;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class Server {

    static Connection conn = Database.getConnection();
    public static List<ClientThread> threads = new LinkedList<>();

    public Server() throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8100);

            while (true) {
                System.out.println("Waiting for client...");
                Socket socket = serverSocket.accept();

                ClientThread ct = new ClientThread(socket);
                threads.add(ct);
                ct.start();
            }
        } catch (IOException e) {
            System.err.println("Eroare" + e);
        } finally {
            serverSocket.close();
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String registerUser(String email, String pass) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO UTILIZATOR(ADMIN, EMAIL, PASSWORD) VALUES (?, ?, ?)");
            stmt.setString(1, "0");
            stmt.setString(2, email);
            stmt.setString(3, pass);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Success";
    }

    public static String logUser(String email, String password) {
        PreparedStatement cstmt = null;
        try {
            cstmt = conn.prepareStatement("SELECT * FROM UTILIZATOR WHERE EMAIL = ? AND PASSWORD = ?");
            cstmt.setString(1, email);
            cstmt.setString(2, password);
            ResultSet rs = cstmt.executeQuery();
            int len = 0;
            while(rs.next()) {
                len ++;
            }

            if(len == 1) {
                return "Success";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Fail";
    }

    public static String nextQuestion(int option, String ans, String loggedUser) {
        CallableStatement cstmt = null;
        int ret = -1;
        try {
            if(option == 0) {
                cstmt = conn.prepareCall("{? = call URMATOAREA_INTREBARE(?)}");
                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, loggedUser);
                cstmt.execute();

                ret = cstmt.getInt(1);
            }
            else {
                cstmt = conn.prepareCall("{? = call URMATOAREA_INTREBARE(?, ?)}");
                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, loggedUser);
                cstmt.setString(3, ans);
                cstmt.execute();
                ret = cstmt.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.valueOf(ret);
    }

    public static String computePoints(String loggedUser) {
        CallableStatement cstmt;
        try {
            cstmt = conn.prepareCall("{? = call PUNCTAJ(?)}");
            cstmt.registerOutParameter(1, Types.DOUBLE);
            cstmt.setString(2, loggedUser);
            cstmt.execute();

            return String.valueOf(cstmt.getDouble(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String selectQuestion(String qid) {
        String res = "";
        try {

            String query = "SELECT * FROM TEST WHERE ID = " + qid;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                res = res + rs.getString("Q_ID") + "@";
                res = res + rs.getString("ID_RASPUNS_1") + "@";
                res = res + rs.getString("ID_RASPUNS_2") + "@";
                res = res + rs.getString("ID_RASPUNS_3") + "@";
                res = res + rs.getString("ID_RASPUNS_4") + "@";
                res = res + rs.getString("ID_RASPUNS_5") + "@";
                res = res + rs.getString("ID_RASPUNS_6");
            }
            rs.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String showAnswer(String aid) {
        try {
            String query = "SELECT * FROM RASPUNSURI WHERE ID = '" + aid + "'";
            Statement st = null;
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                return rs.getString("TEXT_RASPUNS");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showQuestion(String qid) {
        try {
            String query = "SELECT * FROM INTREBARI WHERE ID = '" + qid + "'";
            Statement st = null;
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                return rs.getString("TEXT_INTREBARE");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String showStats(String loggedUser) {
        String sol = "";
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT ID FROM TEST WHERE FINISHED = '1' AND USER_EMAIL = ? ORDER BY ID DESC");
            stmt.setString(1, loggedUser);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                sol = sol + String.valueOf(rs.getInt("ID")) + ",";
            }
            sol = sol.substring(0, Math.max(0, sol.length() - 1));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sol;
    }

    public static String getCorrectAns(String qid) {
        String sol = "";
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT RASPUNS_CORECT FROM TEST WHERE ID = ?");
            stmt.setString(1, qid);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                sol = rs.getString("RASPUNS_CORECT");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sol;
    }


    public static String getOwnAns(String qid) {
        String sol = "";
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT RASPUNS FROM TEST WHERE ID = ?");
            stmt.setString(1, qid);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                sol = rs.getString("RASPUNS");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sol;
    }
}
