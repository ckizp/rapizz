package fr.rapizz.controller;

import fr.rapizz.view.MainFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NavigationController {
    private MainFrame mainFrame;

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        log.debug("MainFrame reference set in NavigationController");
    }

    public void navigateTo(String viewName) {
        log.debug("Navigation requested to: {}", viewName);

        if (mainFrame != null) {
            mainFrame.showView(viewName);
        } else {
            log.error("MainFrame reference is null - navigation failed");
        }
    }
}
