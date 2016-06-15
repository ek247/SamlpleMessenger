import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Eugene Kennedy on 4/12/2016.
 * Server, runs multiple threads, one per client + itself
 * Takes in input and broadcasts it to all clients, storing usernames
 */
public class Server extends Thread{

    private ServerSocket server;
    private ArrayList<ServerThread> pool;
    public Server(int port)
    {
        pool = new ArrayList<ServerThread>();
        try
        {

            server = new ServerSocket(port);

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }



    }

    public void run()
    {
        try
        {

            //System.out.println(server.getInetAddress());
            while(true) {
                Socket sock = server.accept();
                System.out.println(sock.getInetAddress() + " connected");
                ServerThread t = new ServerThread(sock, pool);
                pool.add(t); //Possible not ThreadSafe
                t.start();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


}

class ServerThread extends Thread
{
    private Socket sock;
    private Queue<Message> messages;
    private ArrayList<ServerThread> pool;
    ServerThread(Socket inSock, ArrayList<ServerThread> others)
    {
        messages = new LinkedList<Message>();
        sock = inSock;
        pool = others;
    }

    public void run()
    {
        try
        {
            BufferedReader scan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            /*String message = "What is your username?\n";
            for(char c : message.toCharArray())
                out.write(c);
            System.out.println("Asked for username");
            out.flush();
            while(!scan.ready())
            {}
            user = scan.readLine();
            System.out.println(user);
            */
            boolean waitingForUser = true;
            String user = "";
            while(!sock.isClosed())
            {
                String line = "";
                if(scan.ready())
                {
                    line = scan.readLine();
                    if(waitingForUser) {
                        user = line;
                        waitingForUser = false;
                    }
                    else
                        synchronized (pool)
                        {
                            for(ServerThread t : pool)
                                t.pushIntoQueue(new Message(line, user));
                        }
                }

                if(!messages.isEmpty()) {
                    Message m = messages.remove();
                    String toSend = (m.getSender() + ": " + m.getMessage() + "\n");
                        for (char c : toSend.toCharArray())
                            out.write(c);
                        out.flush();

                }




            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void pushIntoQueue(Message m)
    {

        messages.offer(m);
    }




}

class Message
{
    private String message;
    private String sender;

    public Message(String m, String s)
    {
        message = m;
        sender = s;
    }

    public String getMessage()
    {
        return message;
    }

    public String getSender()
    {
        return sender;
    }
}