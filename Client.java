import java.io.*;
import java.net.*;
import java.util.Scanner;

 class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
    
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
    
        } catch (Exception e) {
            closeEverything();
        } finally {
            scanner.close(); // Close the Scanner properly
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGroupChat;
            try {
                while (socket.isConnected()) {
                    msgFromGroupChat = bufferedReader.readLine();
                    if (msgFromGroupChat == null) {
                        break; // Server disconnected
                    }
                    System.out.println(msgFromGroupChat);
                }
            } catch (IOException e) {
                closeEverything();
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your name for group chat: ");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
