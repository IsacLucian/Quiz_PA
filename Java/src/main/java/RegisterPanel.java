import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPanel extends JPanel implements ActionListener
{
    final MainFrame frame;
    JLabel l1, l2, l3, l4, l5;
    JTextField tf1, tf2;
    JButton btn1, btn2;
    JPasswordField p1, p2;
    RegisterPanel(MainFrame frame)
    {
        this.frame = frame;
        l1 = new JLabel("Registration Form:");
        l1.setFont(new Font("Serif", Font.BOLD, 20));
        l2 = new JLabel("Name:");
        l3 = new JLabel("Email-ID:");
        l4 = new JLabel("Create Passowrd:");
        l5 = new JLabel("Confirm Password:");
        tf1 = new JTextField();
        tf2 = new JTextField();
        p1 = new JPasswordField();
        p2 = new JPasswordField();
        btn1 = new JButton("Submit");
        btn2 = new JButton("Login");
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        l1.setBounds(100, 30, 400, 30);
        l2.setBounds(80, 70, 200, 30);
        l3.setBounds(80, 110, 200, 30);
        l4.setBounds(80, 150, 200, 30);
        l5.setBounds(80, 190, 200, 30);
        tf1.setBounds(300, 70, 200, 30);
        tf2.setBounds(300, 110, 200, 30);
        p1.setBounds(300, 150, 200, 30);
        p2.setBounds(300, 190, 200, 30);
        btn1.setBounds(225, 350, 100, 30);
        btn2.setBounds(350, 350, 100, 30);
        frame.add(l1);
        frame.add(l2);
        frame.add(tf1);
        frame.add(l3);
        frame.add(tf2);
        frame.add(l4);
        frame.add(p1);
        frame.add(l5);
        frame.add(p2);
        frame.add(btn1);
        frame.add(btn2);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btn1)
        {
            int x = 0;
            String s1 = tf1.getText();
            String s2 = tf2.getText();
            char[] s3 = p1.getPassword();
            char[] s4 = p2.getPassword();
            String s8 = new String(s3);
            String s9 = new String(s4);
            if (s8.equals(s9))
            {
                try
                {
                    frame.out.println("register");
                    frame.out.println(s2);
                    frame.out.println(s8);
                    String ans = frame.in.readLine();
                    x++;
                    if (x > 0)
                    {
                        JOptionPane.showMessageDialog(btn1, ans);
                    }
                }
                catch (Exception ex)
                {
                    System.out.println(ex);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(btn1, "Password Does Not Match");
            }
        }
        else
        {
            frame.getContentPane().removeAll();
            frame.add(new LoginPanel(frame), BorderLayout.CENTER);
            frame.repaint();
        }
    }
}