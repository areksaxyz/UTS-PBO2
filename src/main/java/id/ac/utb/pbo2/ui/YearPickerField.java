package id.ac.utb.pbo2.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.Year;

public class YearPickerField extends JPanel {
    private final JTextField yearField = Theme.textField(6);
    private int selectedYear;
    private int pageStart;
    private Runnable changeListener;

    public YearPickerField(int initialYear) {
        this.selectedYear = initialYear;
        this.pageStart = initialYear - (initialYear % 12);
        setLayout(new BorderLayout(6, 0));
        setOpaque(false);
        yearField.setEditable(false);
        yearField.setText(String.valueOf(initialYear));
        JButton button = Theme.secondaryButton("Pilih Tahun");
        button.addActionListener(event -> showPicker());
        add(yearField, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);
    }

    public int getYear() {
        return selectedYear;
    }

    public void setYear(int year) {
        this.selectedYear = year;
        this.pageStart = year - (year % 12);
        yearField.setText(String.valueOf(year));
        if (changeListener != null) {
            changeListener.run();
        }
    }

    public void setYearChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    private void showPicker() {
        Frame frame = (Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(frame, "Pilih Tahun Angkatan", true);
        dialog.setSize(360, 310);
        dialog.setLocationRelativeTo(this);

        JPanel root = Theme.surface();
        root.setLayout(new BorderLayout(0, 12));
        JLabel rangeLabel = Theme.sectionTitle("");
        JPanel grid = new JPanel(new GridLayout(3, 4, 8, 8));
        grid.setOpaque(false);

        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            grid.removeAll();
            rangeLabel.setText(pageStart + " - " + (pageStart + 11));
            for (int year = pageStart; year < pageStart + 12; year++) {
                JButton yearButton = year == selectedYear
                        ? Theme.primaryButton(String.valueOf(year))
                        : Theme.secondaryButton(String.valueOf(year));
                int chosenYear = year;
                yearButton.addActionListener(event -> {
                    setYear(chosenYear);
                    dialog.dispose();
                });
                grid.add(yearButton);
            }
            grid.revalidate();
            grid.repaint();
        };

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JButton previous = Theme.secondaryButton("<");
        previous.addActionListener(event -> {
            pageStart -= 12;
            render[0].run();
        });
        JButton next = Theme.secondaryButton(">");
        next.addActionListener(event -> {
            pageStart += 12;
            render[0].run();
        });
        top.add(previous, BorderLayout.WEST);
        top.add(rangeLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);

        JPanel quick = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quick.setOpaque(false);
        JButton thisYear = Theme.secondaryButton("Tahun Ini");
        thisYear.addActionListener(event -> {
            setYear(Year.now().getValue());
            dialog.dispose();
        });
        quick.add(thisYear);

        root.add(top, BorderLayout.NORTH);
        root.add(grid, BorderLayout.CENTER);
        root.add(quick, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        render[0].run();
        dialog.setVisible(true);
    }
}
