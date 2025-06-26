package Thread;

import Logger.ClientLog;
import Logger.ServerLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ClientLog clientLog;
    private static Socket socket;
    ServerLog LOGGER;

    public ClientHandler(Socket client, ServerLog LOGGER) {
        ClientHandler.socket = client;
        this.LOGGER = LOGGER;
        clientLog = ClientLog.getInstance();
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("Запись и чтение для приема и вывода создана");

            final String name = in.readLine() + "[" + socket.getPort() + "]";
            LOGGER.log("Новый участник ", String.format("%s", name));

            while (!socket.isClosed()) {
                final String msg = in.readLine();
                System.out.println("Сообщение от " + name + ":" + msg);
                if (msg.equalsIgnoreCase("exit")) {
                    LOGGER.log("Выход из чата", String.format(": %s", name));
                    System.out.println("Сервер ожидает - " + msg + " - ОК");
                    Thread.sleep(1000);
                    break;
                }

                System.out.println("Сервер готов к записи....");
                clientLog.log(name, msg);
                System.out.println("Сервер записал сообщение");
            }
            LOGGER.log("Закрытие канала с пользователем", String.format(": [%s]- выполнено", name));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}