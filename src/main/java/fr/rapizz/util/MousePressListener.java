package fr.rapizz.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Functional interface for handling mouse click events with minimal boilerplate.
 * Extends MouseListener but provides default empty implementations for all methods
 * except mouseClicked(), allowing lambda expressions to focus only on click handling.
 * <p>
 * Example usage:
 * <pre>
 * button.addMouseListener((MousePressListener) e -> handleButtonClick());
 * </pre>
 */
@FunctionalInterface
public interface MousePressListener extends MouseListener {
    @Override
    default void mouseEntered(MouseEvent e) {}

    @Override
    default void mouseExited(MouseEvent e) {}

    @Override
    default void mousePressed(MouseEvent e) {}

    @Override
    default void mouseReleased(MouseEvent e) {}
}
