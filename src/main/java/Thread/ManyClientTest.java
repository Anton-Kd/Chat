package Thread;

import java.io.*;
import java.net.*;


public class ManyClientTest implements Runnable {
    static Socket socket;

    public ManyClientTest() {
        try {
            socket = new Socket("127.0.0.1", 8080);

            System.out.println("Клиент подключен - запуск");
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("Клиент подключился");
            System.out.println("\nВведите свое имя, чтобы присоединиться к серверу!\n" +
                    "Затем введите сообщения для отправки пользователям или exit для выхода из канала: Test");

            String name = Thread.currentThread().getName();
            out.println(name);
            out.flush();
            Thread.sleep(1000);
            ThreadReadMessage send = new ThreadReadMessage();
            send.start();
            for (int i = 1; i <= 5; i++) {
                String msg = "Message - " + i;
                out.println(msg);
                out.flush();
                Thread.sleep(1000);
                if (in.read() > -1) {
                    msgFromServer(in);
                }
            }
            String msg = "exit";
            out.println(msg);
            out.flush();
            Thread.sleep(1000);
            if (msg.equalsIgnoreCase("exit")) {
                System.out.println("Проверка завершающих подключений клиента. Test");
                send.interrupt();
            }
            System.out.println("Закрытие канала подключения выполнено. Test");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

        }
    }

    private void msgFromServer(BufferedReader in) throws IOException {
        String msgServ = in.readLine();
    }
}