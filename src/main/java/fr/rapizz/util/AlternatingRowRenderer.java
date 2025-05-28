package fr.rapizz.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AlternatingRowRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }

        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return c;
    }
}