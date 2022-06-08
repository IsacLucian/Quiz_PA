import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainFrame extends JFrame {

    Socket socket;
    RegisterPanel registerPanel;
    private String email = "";
    PrintWriter out;
    BufferedReader in;


    public MainFrame() {
        super("Quiz");
        try {
            socket = new Socket("127.0.0.1", 8100);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        setVisible(true);
        setPreferredSize(new Dimension(700, 500));
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerPanel = new RegisterPanel(this);
        add(registerPanel, BorderLayout.CENTER);

        pack();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
