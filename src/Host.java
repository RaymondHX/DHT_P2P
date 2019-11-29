import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Host {
    //当前主机端口号
    public static int port;
    public static ServerSocket serverSocket;
    //当前主机id
    public static int id;
    //下一台主机端口号
    public static int nextPort;
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("welcome! ");
        System.out.println("input the id :");
        Scanner in = new Scanner(System.in);
        id = Integer.parseInt(in.nextLine());
        try {
            //为每一台主机创建一个serversocket
            serverSocket = new ServerSocket(6000+id);
            port = 6000+id;
            //每台主机只知道下一台主机的端口号
            if(id==7)
                nextPort = 6000;
            else
                nextPort = 6000+id+1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //每台主机开启两个线程，一个客户端，一个服务器端
        new Thread(new Client(id)).start();
        new Thread(new Server(serverSocket,id)).start();
    }



}
