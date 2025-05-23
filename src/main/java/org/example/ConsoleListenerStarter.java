package org.example;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ConsoleListenerStarter {

    private final OperationConsoleListener consoleListener;

    private Thread consoleListenerThread;

    public ConsoleListenerStarter(OperationConsoleListener consoleListener) {
        this.consoleListener = consoleListener;
    }

    @PreDestroy
    public void preDestroy() {
        consoleListenerThread.interrupt();
        consoleListener.endListen();
    }

    @PostConstruct
    public void postConstruct() {

        this.consoleListenerThread = new Thread(() -> {
            consoleListener.start();
            consoleListener.listenerUpdate();
        });
        consoleListenerThread.start();
    }
}
