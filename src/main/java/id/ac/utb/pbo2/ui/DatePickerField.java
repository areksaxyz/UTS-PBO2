package id.ac.utb.pbo2.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;

public class DatePickerField extends JPanel {
    private final JTextField dateField = Theme.textField(12);
    private Date selectedDate;
    private Runnable changeListener;

    public DatePickerField() {
        setLayout(new BorderLayout(6, 0));
        setOpaque(false);
        dateField.setEditable(false);
        JButton button = Theme.secondaryButton("Pilih Tanggal");
        button.addActionListener(event -> showPicker());
        add(dateField, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        if (date != null) {
            dateField.setText(date.toLocalDate().toString());
        } else {
            dateField.setText("");
        }
        if (changeListener != null) {
            changeListener.run();
        }
    }

    public void setDateChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    private void showPicker() {
        JDialog dialog = new JDialog((Frame) null, "Pilih Tanggal Lahir", true);
        dialog.setLayout(new BorderLayout());

        // Header with year/month selection
        JPanel header = new JPanel(new FlowLayout());
        header.setOpaque(false);

        JComboBox<Integer> yearBox = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear - 100; year <= currentYear; year++) {
            yearBox.addItem(year);
        }
        yearBox.setSelectedItem(selectedDate != null ?
            selectedDate.toLocalDate().getYear() : currentYear - 20);

        JComboBox<String> monthBox = new JComboBox<>(new String[]{
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        monthBox.setSelectedIndex(selectedDate != null ?
            selectedDate.toLocalDate().getMonthValue() - 1 : currentMonth);

        JButton prevButton = Theme.secondaryButton("←");
        JButton nextButton = Theme.secondaryButton("→");

        header.add(prevButton);
        header.add(monthBox);
        header.add(yearBox);
        header.add(nextButton);

        dialog.add(header, BorderLayout.NORTH);

        // Calendar grid
        JPanel calendarPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        calendarPanel.setOpaque(false);

        // Day headers
        String[] days = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        for (String day : days) {
            calendarPanel.add(Theme.muted(day));
        }

        JButton[][] dayButtons = new JButton[6][7];

        Runnable updateCalendar = () -> {
            calendarPanel.removeAll();
            // Re-add headers
            for (String day : days) {
                calendarPanel.add(Theme.muted(day));
            }

            int year = (Integer) yearBox.getSelectedItem();
            int month = monthBox.getSelectedIndex() + 1;
            YearMonth yearMonth = YearMonth.of(year, month);
            int firstDayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue() % 7;
            int daysInMonth = yearMonth.lengthOfMonth();

            int dayCounter = 1;
            for (int week = 0; week < 6; week++) {
                for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek) {
                        calendarPanel.add(new JLabel());
                    } else if (dayCounter <= daysInMonth) {
                        JButton dayButton = Theme.secondaryButton(String.valueOf(dayCounter));
                        int selectedDay = dayCounter;
                        dayButton.addActionListener(e -> {
                            LocalDate selectedLocalDate = LocalDate.of(year, month, selectedDay);
                            setSelectedDate(Date.valueOf(selectedLocalDate));
                            dialog.dispose();
                        });
                        calendarPanel.add(dayButton);
                        dayCounter++;
                    } else {
                        calendarPanel.add(new JLabel());
                    }
                }
            }
            dialog.revalidate();
            dialog.repaint();
        };

        yearBox.addActionListener(e -> updateCalendar.run());
        monthBox.addActionListener(e -> updateCalendar.run());

        prevButton.addActionListener(e -> {
            int currentMonthIndex = monthBox.getSelectedIndex();
            if (currentMonthIndex == 0) {
                monthBox.setSelectedIndex(11);
                yearBox.setSelectedItem((Integer) yearBox.getSelectedItem() - 1);
            } else {
                monthBox.setSelectedIndex(currentMonthIndex - 1);
            }
        });

        nextButton.addActionListener(e -> {
            int currentMonthIndex = monthBox.getSelectedIndex();
            if (currentMonthIndex == 11) {
                monthBox.setSelectedIndex(0);
                yearBox.setSelectedItem((Integer) yearBox.getSelectedItem() + 1);
            } else {
                monthBox.setSelectedIndex(currentMonthIndex + 1);
            }
        });

        updateCalendar.run();

        dialog.add(calendarPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton clearButton = Theme.secondaryButton("Hapus");
        clearButton.addActionListener(e -> {
            setSelectedDate(null);
            dialog.dispose();
        });
        JButton cancelButton = Theme.secondaryButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        footer.add(clearButton);
        footer.add(cancelButton);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
