import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class LoginPanel extends JPanel {
    final MainFrame frame;
    JLabel emailLabel;
    JLabel parolaLabel, l1;
    JTextField emailTextField;
    JPasswordField parolaTextField;
    JButton submit, register;

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        init();
    }

    private void init() {
        l1 = new JLabel("Login Form:");
        l1.setFont(new Font("Serif", Font.BOLD, 20));

        emailTextField = new JTextField();
        parolaTextField = new JPasswordField();
        emailLabel = new JLabel("Email:");
        parolaLabel = new JLabel("Password:");
        submit = new JButton("Submit");
        register = new JButton("Register");

        l1.setBounds(new Rectangle(100, 30, 400, 30));
        emailLabel.setBounds(new Rectangle(80, 110, 200, 30));
        emailTextField.setBounds(new Rectangle(300, 110, 200, 30));
        parolaLabel.setBounds(new Rectangle(80, 150, 200, 30));
        parolaTextField.setBounds(new Rectangle(300, 150, 200, 30));
        submit.setBounds(new Rectangle(225, 350, 100, 30));
        submit.addActionListener(this::login);
        register.setBounds(new Rectangle(350, 350, 100, 30));
        register.addActionListener(this::goRegister);

        frame.add(emailLabel);
        frame.add(emailTextField);
        frame.add(parolaLabel);
        frame.add(parolaTextField);
        frame.add(submit);
        frame.add(l1);
        frame.add(register);
    }

    private void login(ActionEvent actionEvent) {

        frame.out.println("login");
        frame.out.println(emailTextField.getText());
        frame.out.println(parolaTextField.getPassword());
        try {
            String ans = frame.in.readLine();
            JOptionPane.showMessageDialog(submit, ans);
            if(ans.substring(0, Math.min("Success".length(), ans.length())).equals("Success")) {
                frame.getContentPane().removeAll();
                frame.add(new QuestionPanel(frame), BorderLayout.CENTER);
                frame.repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goRegister(ActionEvent e) {
        frame.getContentPane().removeAll();
        frame.add(new RegisterPanel(frame), BorderLayout.CENTER);
        frame.repaint();
    }
}
