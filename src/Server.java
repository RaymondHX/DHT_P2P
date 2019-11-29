import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    ServerSocket serverSocket;
    String filepath;
    public Server(ServerSocket serverSocket,int id) {
        this.serverSocket = serverSocket;
        filepath = "E:\\DHT_P2P\\src\\files\\file"+id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //一直监听来自客户端套接字的请求，并通信
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                byte[] bytes = new byte[1024];
                int len = inputStream.read(bytes);
                //读取第一行，这一行里面会包含客户端相应的请求消息
                String firstLine = new String(bytes,0,len,"UTF-8");
                //根据第一行的消息进行判断处理
                if(firstLine.substring(0, 7).equals("request")){
                    int want = Integer.parseInt(firstLine.substring(7));
                        if (Host.id == want) {
                            String response = String.valueOf(Host.port);
                            outputStream.write(response.getBytes("UTF-8"));
                        } else {
                            int get = query(want);
                            String response = String.valueOf(get);
                            outputStream.write(response.getBytes("UTF-8"));
                        }
                    inputStream.close();
                    outputStream.close();
                }
                else if(firstLine.substring(0,6).equals("upload")){
                    int index = firstLine.indexOf("*");
                    receieveFile(filepath+"\\"+firstLine.substring(6,index)+".txt",socket);

                }
                else if(firstLine.substring(0,8).equals("download")){
                    String filename = firstLine.substring(8);
                    File file = new File(filepath+"\\"+filename);
                    sendFile(file,socket);
                }
                else if(firstLine.substring(0,6).equals("change")){
                    String nextport = firstLine.substring(7);
                    Host.nextPort = Integer.parseInt(nextport);
                    OutputStream outputStream1 = socket.getOutputStream();
                    outputStream1.write("change port successfully".getBytes("UTF-8"));
                    outputStream1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public synchronized int query(int port) {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), Host.nextPort);
            OutputStream outputStream = socket.getOutputStream();
            String response = "request" +port;
            outputStream.write(response.getBytes("UTF-8"));
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len, "UTF-8"));
            }
            int want = Integer.parseInt(sb.toString());
            inputStream.close();
            outputStream.close();
            socket.close();
            return want;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;

    }

    private synchronized void receieveFile(String filePath, Socket socket) {

            try {
                System.out.println("receive connection from client，，，，");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));

                byte[] buf = new byte[1027 * 9];
                int len = 0;

                while ((len = dis.read(buf)) != -1) {
                    dos.write(buf, 0, len);
                }
                dos.flush();

                System.out.println("receive file end，，，，");
                dis.close();
                dos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private synchronized void sendFile(File file,Socket socket)  {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write("fileisexist".getBytes("UTF-8"));
            for (int i = 0; i < 1013; i++) {
                dos.write("*".getBytes("UTF-8"));
            }
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = dis.read(buf)) != -1) {
                dos.write(buf, 0, len);

            }
            dos.flush();
            System.out.println("file send end，，，，");

            dis.close();
            dos.close();
        } catch (FileNotFoundException e) {
            System.out.println("file does not exist");
            try {
                socket.getOutputStream().write("filenotexist".getBytes("UTF-8"));
                socket.getOutputStream().close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
