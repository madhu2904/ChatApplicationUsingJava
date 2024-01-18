import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String clientUsername;

public ClientHandler(Socket socket)
{
    try{
        this.socket=socket;
        this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.clientUsername=bufferedReader.readLine();
        clientHandlers.add(this);
        broadcastMessage("SERVER:"+clientUsername+"has entered the chat");


    }catch(Exception e){
        closeEverything(socket,bufferedReader,bufferedWriter);
    }
}
@Override

public void run()
{
    String messageFromClient;
    while(socket.isConnected()){
        try{
            messageFromClient=bufferedReader.readLine();
            broadcastMessage(messageFromClient);

        }catch(Exception e){
            closeEverything(socket,bufferedReader,bufferedWriter);
            break;
        }
    }
}
public void broadcastMessage(String messageToSend)
{
    for(ClientHandler clientHandler:clientHandlers)
    {
        try{
            if(!clientHandler.clientUsername.equals(clientUsername))
            {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            }
        }catch(Exception e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
}
public void removeClientHandler()
{
    clientHandlers.remove(this);
    broadcastMessage("SERVER:"+clientUsername+"has left the chat!");

}
public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter)
{
    try{
        if(bufferedReader!=null)
        {
            bufferedReader.close();
        }
        if(bufferedWriter !=null)
        {
            bufferedWriter.close();
        }
        if(socket !=null)
        {
            socket.close();
        }
    }catch(Exception e)
    {
        e.printStackTrace();
    }
}
     
}
