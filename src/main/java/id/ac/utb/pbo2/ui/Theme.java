package id.ac.utb.pbo2.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;

public final class Theme {
    public static final Color BACKGROUND = new Color(246, 248, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(33, 37, 41);
    public static final Color MUTED = new Color(94, 103, 113);
    public static final Color BORDER = new Color(218, 225, 231);
    public static final Color PRIMARY = new Color(0, 137, 123);
    public static final Color PRIMARY_DARK = new Color(0, 96, 86);
    public static final Color ACCENT = new Color(226, 84, 64);
    public static final Color WARNING = new Color(231, 164, 52);

    private Theme() {
    }

    public static void apply() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
            // Default Swing LAF is acceptable when Nimbus is unavailable.
        }
        UIManager.put("control", BACKGROUND);
        UIManager.put("Table.alternateRowColor", new Color(250, 252, 253));
        UIManager.put("Table.showGrid", false);
    }

    public static JPanel page() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    public static JPanel surface() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return panel;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        return label;
    }

    public static JTextField textField(int columns) {
        JTextField field = new JTextField(columns);
        decorateInput(field);
        return field;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        decorateInput(field);
        return field;
    }

    public static <T> JComboBox<T> comboBox(T[] values) {
        JComboBox<T> box = new JComboBox<>(values);
        decorateInput(box);
        return box;
    }

    public static JButton primaryButton(String text) {
        JButton button = button(text, PRIMARY, Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = button(text, SURFACE, TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 13, 8, 13)
        ));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = button(text, ACCENT, Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        return button;
    }

    public static JTable table() {
        JTable table = new JTable();
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowMargin(2);
        table.setSelectionBackground(new Color(215, 244, 239));
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(238, 242, 245));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(new Color(235, 241, 244));
        table.getTableHeader().setForeground(TEXT);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 8, 0, 8));
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }

    public static JScrollPane tableScroll(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getVerticalScrollBar().setBlockIncrement(96);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);
        return scrollPane;
    }

    public static void error(Component parent, Exception ex) {
        JOptionPane.showMessageDialog(parent, ex.getMessage(), "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static void decorateInput(JComponent component) {
        component.setBackground(Color.WHITE);
        component.setForeground(TEXT);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 9, 7, 9)
        ));
    }
}
