package Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerLog {
    private static volatile ServerLog INSTANCE = null;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final ConcurrentHashMap<String, Integer> frag = new ConcurrentHashMap<>();
    protected AtomicInteger num = new AtomicInteger(0);

    private ServerLog() {
    }

    public static ServerLog getInstance() {
        if (INSTANCE == null) {
            synchronized (ServerLog.class) {
                if (INSTANCE == null)
                    INSTANCE = new ServerLog();
            }
        }
        return INSTANCE;
    }

    public String log(String level, String msg) {
        frag.put(level, frag.getOrDefault(level, 0) + 1);
        String s = String.format("[%s %3d]\n%s (%d) : %s\n",
                dtf.format(LocalDateTime.now()),
                num.incrementAndGet(),
                level.toUpperCase(),
                frag.get(level),
                msg);
        System.out.println(s);
        writeLog(s);
        return s;
    }

    public void writeLog(String s) {
        try {
            BufferedWriter bf = new BufferedWriter(
                    new FileWriter("src/main/resources/serverLog.log", true));
            bf.write(s);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}