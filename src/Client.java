import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Created by Eugene Kennedy on 4/12/2016.
 * Client for chat server, connects, sends username, then sends/reads text to/from server
 */
public class Client extends Thread{

    private JFrame frame;
    private JButton send;
    private JTextArea inMessages;
    private JTextField toSend;
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;

    public Client(int port)
    {
        frame = new JFrame();

        try {
            InetAddress inet = InetAddress.getLocalHost(); //Can change this if server + client are not on the same server.
            clientSocket = new Socket(inet, port);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }


        init();


    }

    public void run()
    {
        while(!clientSocket.isClosed())
        {
            String toDo = "";

            String read = "";
            try {
                while (in.ready()) {
                    read = in.readLine();
                    toDo = read;
                    inMessages.append(toDo + "\n\r");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            }
        }


    private void init()
    {

        frame.setSize(800,800);
        frame.setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        panel1.setVisible(true);
        panel1.setLayout(new FlowLayout());
        inMessages = new JTextArea("");
        inMessages.setVisible(true);
        inMessages.setEditable(false);
        JScrollPane scroll = new JScrollPane (inMessages,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(scroll, BorderLayout.CENTER);

        toSend = new JTextField("Please enter username here, then press send.");
        toSend.setVisible(true);
        toSend.setColumns(60);

        panel1.add(toSend);

        send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()==send) {
                        out.print(toSend.getText()+"\n");
                        toSend.setText("");
                        toSend.setColumns(60);
                        out.flush();

                }
            }
        });
        panel1.add(send);
        frame.add(panel1, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

}
