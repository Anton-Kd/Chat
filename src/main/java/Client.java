import Logger.ClientLog;
import Thread.ThreadReadMessage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static String userName = "Новый участник";
    private static final ClientLog clientLog = new ClientLog();

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        if (!userName.trim().isEmpty())
            Client.userName = userName;
    }

    public static void main(String[] args) {
        String serverHost = null;
        int serverPort = 0;
        // Читаем Host и Port из файла setting.txt
        try (BufferedReader bf = new BufferedReader(
                new FileReader("src/main/resources/settings.txt"))) {
            String setting;
            while ((setting = bf.readLine()) != null) {
                if (setting.contains("host")) {
                    String[] s = setting.split(" ");
                    serverHost = s[1];
                } else if (setting.contains("port")) {
                    String[] s = setting.split(" ");
                    serverPort = Integer.parseInt(s[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Socket socketClient = new Socket(serverHost, serverPort);
             PrintWriter printWriter = new PrintWriter(socketClient.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()))) {

            System.out.println("Введите имя, чтобы присоединиться к серверу.\n" +
                    "Затем введите сообщения для отправки пользователям или </exit>, чтобы выйти из канала: CLIENT");

            setUserName(SCANNER.nextLine());
            printWriter.println(getUserName());
            clientLog.writeLog(getUserName());
            Thread.sleep(1000);
            ThreadReadMessage send = new ThreadReadMessage();
            send.start();

            while (true) {
                String msg = SCANNER.nextLine();
                printWriter.println(msg);
                if (msg.equalsIgnoreCase("/exit")) {
                    Thread.sleep(100);
                    if (in.read() > -1) {
                        msgFromServer(in);
                    }
                    break;
                }
                if (in.read() > -1) {
                    msgFromServer(in);
                }
            }
            send.interrupt();
            System.out.println("Закрытие канала подключения клиента.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void msgFromServer(BufferedReader in) throws IOException {
        String msgServ = in.readLine();
    }

}