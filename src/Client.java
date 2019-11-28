import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
    String filepath;

    Client(int id){
        filepath = "E:\\DHT_P2P\\src\\files\\"+"file"+id;
    }
    @Override
    public void run() {
        boolean flag = true;
        while(flag) {
            System.out.println("please input your choice");
            System.out.println("1. upload");
            System.out.println("2. download");
            Scanner in = new Scanner(System.in);
            String filename;
            int hash;
            int choice;
            int port;
            choice = Integer.parseInt(in.nextLine());
            switch (choice) {
                case 1:
                    System.out.println("please input filename");
                     filename= in.nextLine();
                     hash = filename.hashCode() % 8;
                     port = getPort(Host.nextPort, hash);
                    System.out.println(port);
                    try {
                        Socket socket = new Socket(InetAddress.getLocalHost(), port);
                        File file = new File(filepath + "\\" + filename + ".txt");
                        System.out.println(file.getAbsolutePath());
                        uploadFile(socket, file, filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("plaese input the filename you want");
                    filename = in.nextLine();
                    hash = filename.hashCode()%8;
                    port = getPort(Host.nextPort,hash);
                    try {
                        Socket socket = new Socket(InetAddress.getLocalHost(),port);
                        filepath = filepath+"\\";
                        downloadFile(socket, filepath,filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


            }
        }
    }


    public synchronized int getPort(int nextPort,int hash){
        System.out.println("host"+Host.id);
        System.out.println("nextport"+nextPort);
        System.out.println("want"+hash);
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(),nextPort);
            OutputStream outputStream = socket.getOutputStream();
            String request = "request"+hash;
            outputStream.write(request.getBytes("UTF-8"));
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
            while ((len = inputStream.read(bytes)) != -1) {
                // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len, "UTF-8"));
            }
            int find = Integer.parseInt(sb.toString());
            inputStream.close();
            outputStream.close();
            socket.close();
            return find;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public synchronized void uploadFile(Socket socket, File file,String filename){

            try {
                System.out.println("文件大小：" + file.length() + "kb");
                StringBuilder sb = new StringBuilder("upload");
                sb.append(filename);
                for (int i = 0; i <1024-sb.toString().length() ; i++) {
                    sb.append("*");
                }
                String fill = sb.toString();
                socket.getOutputStream().write(fill.getBytes("UTF-8"));
                DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                byte[] buf = new byte[1024 * 9];
                int len = 0;
                while ((len = dis.read(buf)) != -1) {
                    dos.write(buf, 0, len);

                }
                dos.flush();
                System.out.println("文件上传结束，，，，");

                dis.close();
                dos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


    public synchronized void downloadFile(Socket socket,String filepath,String filename){

        try {
            String request = "download"+filename+".txt";
            socket.getOutputStream().write(request.getBytes("UTF-8"));
            System.out.println("接收到服务器端连接，，，，");
            filepath = filepath+"\\"+filename+".txt";
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(filepath));

            byte[] buf = new byte[1027 * 9];
            int len = 0;

            while ((len = dis.read(buf)) != -1) {
                dos.write(buf, 0, len);
            }
            dos.flush();

            System.out.println("文件下载结束，，，，");
            dis.close();
            dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
