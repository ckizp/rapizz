package fr.rapizz;

import fr.rapizz.view.MainFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.*;

@SpringBootApplication
@Slf4j
public class Main {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("spring.main.web-application-type", "none");

        log.info("Starting RaPizz application...");

        // Launch the Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

                context.getBean(MainFrame.class);

                log.info("RaPizz application started successfully");
            } catch (Exception e) {
                log.error("Failed to start application", e);
                System.exit(1);
            }
        });
    }
}
