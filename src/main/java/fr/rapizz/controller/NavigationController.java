package fr.rapizz.controller;

import fr.rapizz.view.MainFrame;

public class NavigationController {
    private final MainFrame mainFrame;

    public NavigationController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void navigateTo(String viewName) {
        if(mainFrame != null) {
            mainFrame.showView(viewName);
        }
    }
}
