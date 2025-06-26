package Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientLog {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    protected AtomicInteger num = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Integer> frag = new ConcurrentHashMap<>();
    private static volatile ClientLog INSTANCE = null;

    public ClientLog() {
    }

    public static ClientLog getInstance() {
        if (INSTANCE == null) {
            synchronized (ClientLog.class) {
                if (INSTANCE == null)
                    INSTANCE = new ClientLog();
            }
        }
        return INSTANCE;
    }

    public String log(String userName, String msg) {
        frag.put(userName, frag.getOrDefault(userName, 0) + 1);
        String s = String.format("[%s %3d]\n%s (%d): %s\n",
                dtf.format(LocalDateTime.now()),
                num.incrementAndGet(),
                userName,
                frag.get(userName),
                msg);
        writeLog(s);
        return s;
    }

    public void writeLog(String s) {
        try {
            BufferedWriter bf = new BufferedWriter(
                    new FileWriter("src/main/resources/clientLog.log", true));
            bf.write(s);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}