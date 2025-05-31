package fr.rapizz.view.panels;

import fr.rapizz.view.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FooterPanel extends JPanel {
    public FooterPanel() {
        setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel authorsLabel = new JLabel("développé par Mouad MOUSTARZAK & Ibraguim TEMIRKHAEV & Léo DESSERTENNE", JLabel.CENTER);
        authorsLabel.setFont(AppTheme.FOOTER);

        add(authorsLabel);
    }
}
