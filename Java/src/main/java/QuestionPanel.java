import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.*;

public class QuestionPanel extends JPanel {
    final MainFrame frame;
    JButton nextQuestion, stats;
    JLabel intrebare;
    JLabel scor;
    JCheckBox rasp1;
    JCheckBox rasp2;
    JCheckBox rasp3;
    JCheckBox rasp4;
    JCheckBox rasp5;
    JCheckBox rasp6;
    String intrebare_id;
    String rasp1_id;
    String rasp2_id;
    String rasp3_id;
    String rasp4_id;
    String rasp5_id;
    String rasp6_id;
    int first = 0;

    public QuestionPanel(MainFrame frame) {
        this.frame = frame;
        init();
    }

    private void init() {
        nextQuestion = new JButton("Start Questioner");
        stats = new JButton("Stats");
        nextQuestion.setBounds(new Rectangle(175, 50, 150, 30));
        stats.setBounds(new Rectangle(350, 50, 150, 30));
        nextQuestion.addActionListener(this::getQuestion);
        stats.addActionListener(this::getStats);
        frame.add(nextQuestion);
        frame.add(stats);
    }

    private void getStats(ActionEvent actionEvent) {
        frame.out.println("stats");
        String[] ids = new String[]{};
        try {
            ids = frame.in.readLine().split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File("fila");
        PrintWriter fout = null;
        try {
            fout = new PrintWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = 0;
        for(String id : ids) {
            try {
                index++;
                if(index == 11) break;

                frame.out.println("get_question");
                frame.out.println(id);
                String[] parts = frame.in.readLine().split("@");
                if(parts.length < 2) continue;
                intrebare_id = parts[0];
                rasp1_id = parts[1];
                rasp2_id = parts[2];
                rasp3_id = parts[3];
                rasp4_id = parts[4];
                rasp5_id = parts[5];
                rasp6_id = parts[6];

                frame.out.println("show_question");
                frame.out.println(intrebare_id);
                fout.println(index + "." + frame.in.readLine());

                frame.out.println("show_answer");
                frame.out.println(rasp1_id);
                fout.println(frame.in.readLine());
                frame.out.println("show_answer");
                frame.out.println(rasp2_id);
                fout.println(frame.in.readLine());

                frame.out.println("show_answer");
                frame.out.println(rasp3_id);
                fout.println(frame.in.readLine());

                frame.out.println("show_answer");
                frame.out.println(rasp4_id);
                fout.println(frame.in.readLine());

                frame.out.println("show_answer");
                frame.out.println(rasp5_id);
                fout.println(frame.in.readLine());

                frame.out.println("show_answer");
                frame.out.println(rasp6_id);
                fout.println(frame.in.readLine());

                fout.println();
                frame.out.println("correct_ans");
                frame.out.println(id);

                String[] c_ids = frame.in.readLine().split(",");
                fout.println("Raspunsuri corecte:");
                for(String c_id : c_ids) {
                    frame.out.println("show_answer");
                    frame.out.println(c_id);
                    fout.println(frame.in.readLine());
                }

                fout.println();
                frame.out.println("own_ans");
                frame.out.println(id);
                String[] o_ids = frame.in.readLine().split(",");
                fout.println("Raspunsurile tale:");
                for(String o_id : o_ids) {
                    frame.out.println("show_answer");
                    frame.out.println(o_id);
                    fout.println(frame.in.readLine());
                }

                fout.println();
                fout.flush();
            }
            catch (IOException e ) {
                e.printStackTrace();
            }
        }

        fout.close();
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getQuestion(ActionEvent actionEvent) {
        nextQuestion.setText("Next Question");
        frame.out.println("next_question");
        CallableStatement cstmt = null;
        try {
            int ans;

            if(first == 0) {
                frame.out.println("0");
                ans = Integer.parseInt(frame.in.readLine());
                first = 1;
                frame.getContentPane().removeAll();
                frame.add(nextQuestion);
                frame.repaint();
            }
            else
            {
                frame.out.println("1");
                String rasp = "";
                if(rasp1.isSelected()) rasp = rasp + rasp1_id + ",";
                if(rasp2.isSelected()) rasp = rasp + rasp2_id + ",";
                if(rasp3.isSelected()) rasp = rasp + rasp3_id + ",";
                if(rasp4.isSelected()) rasp = rasp + rasp4_id + ",";
                if(rasp5.isSelected()) rasp = rasp + rasp5_id + ",";
                if(rasp6.isSelected()) rasp = rasp + rasp6_id + ",";

                rasp = rasp.substring(0, Math.max(0, rasp.length() - 1));
                if(rasp.length() == 0) rasp = rasp1_id;
                frame.out.println(rasp);
                frame.getContentPane().removeAll();
                frame.add(nextQuestion);
                frame.add(stats);
                ans = Integer.parseInt(frame.in.readLine());
                if(ans == -1) {
                    first = 0;
                    scor = new JLabel();

                    frame.out.println("points");
                    double s = Double.parseDouble(frame.in.readLine());

                    nextQuestion.setText("Start Questioner");
                    scor.setText("Punctajul obtinut este: " + s);
                    scor.setBounds(new Rectangle(200, 150, 500, 25));
                    scor.setFont(new Font("Serif", Font.BOLD, 20));
                    frame.add(scor);
                    frame.repaint();
                    return;
                }
                frame.repaint();
            }

            intrebare = new JLabel();
            rasp1 = new JCheckBox();
            rasp2 = new JCheckBox();
            rasp3 = new JCheckBox();
            rasp4 = new JCheckBox();
            rasp5 = new JCheckBox();
            rasp6 = new JCheckBox();
            intrebare.setBounds(new Rectangle(20, 100, 500, 50));
            rasp1.setBounds(new Rectangle(20, 150, 500, 25));
            rasp2.setBounds(new Rectangle(20, 175, 500, 25));
            rasp3.setBounds(new Rectangle(20, 200, 500, 25));
            rasp4.setBounds(new Rectangle(20, 225, 500, 25));
            rasp5.setBounds(new Rectangle(20, 250, 500, 25));
            rasp6.setBounds(new Rectangle(20, 275, 500, 25));

            frame.out.println("get_question");
            frame.out.println(String.valueOf(ans));

            String[] parts = frame.in.readLine().split("@");
            intrebare_id = parts[0];
            rasp1_id = parts[1];
            rasp2_id = parts[2];
            rasp3_id = parts[3];
            rasp4_id = parts[4];
            rasp5_id = parts[5];
            rasp6_id = parts[6];

            frame.out.println("show_question");
            frame.out.println(intrebare_id);
            intrebare.setText(frame.in.readLine());

            frame.out.println("show_answer");
            frame.out.println(rasp1_id);
            rasp1.setText(frame.in.readLine());
            frame.out.println("show_answer");
            frame.out.println(rasp2_id);
            rasp2.setText(frame.in.readLine());
            frame.out.println("show_answer");
            frame.out.println(rasp3_id);
            rasp3.setText(frame.in.readLine());
            frame.out.println("show_answer");
            frame.out.println(rasp4_id);
            rasp4.setText(frame.in.readLine());
            frame.out.println("show_answer");
            frame.out.println(rasp5_id);
            rasp5.setText(frame.in.readLine());
            frame.out.println("show_answer");
            frame.out.println(rasp6_id);
            rasp6.setText(frame.in.readLine());

            frame.add(intrebare);
            frame.add(rasp1);
            frame.add(rasp2);
            frame.add(rasp3);
            frame.add(rasp4);
            frame.add(rasp5);
            frame.add(rasp6);
            frame.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
