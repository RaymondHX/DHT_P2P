import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Host {
    public static int port;
    public static ServerSocket serverSocket;
    public static int id;
    public static int nextPort;
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("welcome! ");
        System.out.println("input the id :");
        Scanner in = new Scanner(System.in);
        id = Integer.parseInt(in.nextLine());
        try {
            serverSocket = new ServerSocket(6000+id);
            port = 6000+id;
            if(id==7)
                nextPort = 6000;
            else
                nextPort = 6000+id+1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Client(id)).start();
        new Thread(new Server(serverSocket,id)).start();
    }



}
