package fr.rapizz.view.theme;

import javax.swing.*;
import java.awt.*;

public class AppTheme {
    /* *************************************************************************
     *                                                                         *
     * Fonts                                                                   *
     *                                                                         *
     **************************************************************************/
    private static final String PRIMARY_FONT = "Arial";

    public static final Font NAV_ITEM = new Font(PRIMARY_FONT, Font.BOLD, 14);
    public static final Font MENU_ITEM = new Font(PRIMARY_FONT, Font.PLAIN, 14);

    public static final Font TITLE = new Font(PRIMARY_FONT, Font.BOLD, 24);

    public static final Font FOOTER = new Font(PRIMARY_FONT, Font.ITALIC, 12);

    /* *************************************************************************
     *                                                                         *
     * Colors                                                                  *
     *                                                                         *
     **************************************************************************/
    public static final Color PRIMARY = new Color(220, 53, 69);
    public static final Color PRIMARY_DARK = new Color(200, 35, 51);
    public static final Color PRIMARY_LIGHT = new Color(239, 169, 175);

    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);

    public static final Color BACKGROUND = new Color(248, 248, 248);

    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    public static final Color INFO_COLOR = new Color(33, 150, 243);
    public static final Color ERROR_COLOR = new Color(211, 47, 47);
    public static final Color NEUTRAL_COLOR = new Color(117, 117, 117);

    /* *************************************************************************
     *                                                                         *
     * UI Utils                                                                *
     *                                                                         *
     **************************************************************************/

    /**
     * @param field Field to style
     * @param placeholder Help text
     */
    public static void styleTextField(JTextField field, String placeholder) {
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    /**
     * @param button Button to style
     * @param text Button text
     * @param mainColor Button main color
     */
    public static void styleButton(JButton button, String text, Color mainColor) {
        button.setText(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(mainColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darken(mainColor, 0.1f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(mainColor);
            }
        });
    }

    /**
     * Darkens a color by the specified factor
     *
     * @param color Color to modify
     * @param factor Darkening factor
     * @return New darkened color
     */
    private static Color darken(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * (1 - factor)), 0),
                Math.max((int)(color.getGreen() * (1 - factor)), 0),
                Math.max((int)(color.getBlue() * (1 - factor)), 0),
                color.getAlpha()
        );
    }
}
