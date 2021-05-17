
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextArea;

public class Server 
{
    private int port;
    private boolean running;
    
    private JTextArea messageBox;
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    
    private Runnable listenTask;
    private Thread listenThread;

    public Server(int port, JTextArea messageBox) 
    {
        this.port = port;
        this.messageBox = messageBox;
    }

    public void startListening()
    {
        listenTask = new Runnable()
        {
            @Override
            public void run()
            {
                try 
                {
                    messageBox.append("Listening...\n");
                    serverSocket = new ServerSocket(port);
                    clientSocket = serverSocket.accept();
                    messageBox.append("Client Connected " + clientSocket.getLocalAddress().getHostName() + ":" + clientSocket.getLocalPort() + "\n");
                    
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    while (true)
                    {
                        messageBox.append("Client : " + dis.readUTF() + "\n");
                    }
                }
                catch (Exception ex) 
                {
                    messageBox.append(ex.getMessage());
                    running = false;
                }
            }
        };
        
        listenThread = new Thread(listenTask);
        running = true;
        listenThread.start();
    }
    
    public void writeData(String data)
    {
        try 
        {
            DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
            dout.writeUTF(data);
            messageBox.append("You : " + data + "\n");
        }
        catch (IOException ex) 
        {
            messageBox.append(ex.getMessage());
        }
    }
    
    public void terminate()
    {
        if(!running)
        {
            messageBox.append("Server is not running!\n");
            return;
        }
        
        if(listenThread != null && listenThread.isAlive())
        {
            listenThread.interrupt();
        }
        
        try 
        {
            serverSocket.close();
        } 
        catch (IOException ex) 
        {
            messageBox.append(ex.getMessage());
        }
        
        running = false;
        messageBox.append("Stopped!\n");
    }
    
    public int getPort() 
    {
        return this.port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public boolean isRunning() 
    {
        return this.running;
    }

    public void setRunning(boolean running) 
    {
        this.running = running;
    }
    
    public JTextArea getMessageBox() 
    {
        return this.messageBox;
    }

    public void setMessageBox(JTextArea messageBox)
    {
        this.messageBox = messageBox;
    }
}
