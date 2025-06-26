import Thread.ClientHandler;
import Logger.ServerLog;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private static final ServerLog LOGGER = ServerLog.getInstance();
    private static final Scanner sc = new Scanner(System.in);
    public static ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        // Записываем в файл setting.txt наш Host и Port для подключения.
        try (FileWriter writer = new FileWriter("src/main/resources/settings.txt", false)) {
            writer.write("host: " + HOST);
            writer.append('\n');
            writer.write("port: " + PORT);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Создаем ServerSocket и запускаем цикл ожидания подключения клиентов.
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.log("INFO", """
                    Сервер работает.
                    Ожидает консольных команд или подключения пользователя...\
                    \s
                    Чтобы сервер завершил свою работу - введите </exit>""");

            Thread readThread = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Thread.sleep(1000);
                        if (sc.hasNextLine()) {
                            System.out.println("Сервер нашел консольные команды!");
                            Thread.sleep(1000);
                            String serverCommand = sc.nextLine();
                            if (serverCommand.equalsIgnoreCase("/exit")) {
                                System.out.println("Сервер прекращает работу...");
                                executorService.shutdown();
                                serverSocket.close();
                                LOGGER.log("INFO", "EXIT");
                                break;
                            }
                        }
                    } catch (InterruptedException | IOException e) {
                        return;
                    }
                }
            });
            readThread.start();

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключение найдено");
                executorService.execute(new ClientHandler(clientSocket, LOGGER));
                System.out.println("Подключение установлено");
            }
            System.out.println("Завершаем сеанс");
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}