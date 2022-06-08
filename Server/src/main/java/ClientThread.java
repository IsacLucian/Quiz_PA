import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket = null;
    private String loggedUser = "";
    private boolean exitVar = false;
    public ClientThread (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            while (!exitVar) {
                String request = in.readLine();
                StringBuilder answer = new StringBuilder("Server received the request ... ");
                if(request.substring(0, Math.min("register".length(), request.length())).equals("register")) {
                    if(loggedUser.length() > 0) {
                        answer = new StringBuilder("Already logged in");
                    } else {
                        String response = Server.registerUser(in.readLine(), in.readLine());
                        answer = new StringBuilder(response);
                    }
                }
                else if(request.substring(0, Math.min("login".length(), request.length())).equals("login")) {
                    if(loggedUser.length() > 0) {
                        answer = new StringBuilder("Already logged in");
                    } else {
                        String email, password;
                        email = in.readLine();
                        password = in.readLine();
                        answer = new StringBuilder(Server.logUser(email, password));

                        if(answer.substring(0, Math.min("Success".length(), answer.length())).equals("Success"))
                            loggedUser = email;
                    }
                }
                else if(request.substring(0, Math.min("next_question".length(), request.length())).equals("next_question")) {
                    int option = Integer.parseInt(in.readLine());
                    String ans = "";
                    if(option != 0) ans = in.readLine();
                    answer = new StringBuilder(Server.nextQuestion(option, ans, loggedUser));

                } else if(request.substring(0, Math.min("points".length(), request.length())).equals("points")) {
                        answer = new StringBuilder(Server.computePoints(loggedUser));
                } else if(request.substring(0, Math.min("get_question".length(), request.length())).equals("get_question")) {
                        String qid = in.readLine();
                        answer = new StringBuilder(Server.selectQuestion(qid));
                } else if(request.substring(0, Math.min("show_question".length(), request.length())).equals("show_question")) {
                        String qid = in.readLine();
                        answer = new StringBuilder(Server.showQuestion(qid));
                } else if(request.substring(0, Math.min("show_answer".length(), request.length())).equals("show_answer")) {
                        String aid = in.readLine();
                        answer = new StringBuilder(Server.showAnswer(aid));
                } else if(request.substring(0, Math.min("stats".length(), request.length())).equals("stats")) {
                        answer = new StringBuilder(Server.showStats(loggedUser));
                } else if(request.substring(0, Math.min("correct_ans".length(), request.length())).equals("correct_ans")) {
                        String qid = in.readLine();
                        answer = new StringBuilder(Server.getCorrectAns(qid));
                } else if(request.substring(0, Math.min("own_ans".length(), request.length())).equals("own_ans")) {
                        String qid = in.readLine();
                        answer = new StringBuilder(Server.getOwnAns(qid));
                }

                out.println(answer);
                out.flush();
            }

        } catch (IOException e) {
            System.err.println("Eroare comunicare" + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public void exit() {
        exitVar = true;
    }
}