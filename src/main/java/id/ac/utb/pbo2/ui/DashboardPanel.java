package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;
import id.ac.utb.pbo2.service.LayananAkademik;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardPanel extends JPanel {
    private static final Locale LOCALE_ID = new Locale("id", "ID");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", LOCALE_ID);
    private final LayananAkademik service = new LayananAkademik();
    private final JPanel statsPanel = new JPanel(new GridLayout(2, 2, 12, 12));
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance(LOCALE_ID);

    public DashboardPanel(PenggunaSaatIni user) {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(Theme.page().getBorder());

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Dashboard Akademik"), BorderLayout.NORTH);
        header.add(Theme.muted("Ringkasan indikator utama sistem akademik."), BorderLayout.CENTER);
        top.add(header, BorderLayout.NORTH);
        top.add(contextBar(user), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        statsPanel.setOpaque(false);
        add(statsPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel contextBar(PenggunaSaatIni user) {
        JPanel bar = Theme.surface();
        bar.setLayout(new BorderLayout(10, 0));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(10, 12, 10, 12)
        ));
        String tanggal = LocalDate.now().format(DATE_FORMATTER);
        bar.add(Theme.muted("Akun aktif: " + user.getNamaLengkap() + " (" + user.getRole() + ")"),
                BorderLayout.WEST);
        bar.add(Theme.muted("Terakhir diperbarui: " + capitalizeFirst(tanggal)),
                BorderLayout.EAST);
        return bar;
    }

    private void loadStats() {
        statsPanel.removeAll();
        try {
            statsPanel.add(statCard("Mahasiswa", service.count("mahasiswa"),
                    "Total mahasiswa terdaftar", new Color(31, 122, 140)));
            statsPanel.add(statCard("Dosen", service.count("dosen"),
                    "Total dosen terdaftar", new Color(54, 99, 170)));
            statsPanel.add(statCard("Mata Kuliah", service.count("matakuliah"),
                    "Katalog mata kuliah aktif", new Color(26, 144, 104)));
            statsPanel.add(statCard("Role Aktif", 2,
                    "Peran akses aplikasi", new Color(194, 108, 23)));
            revalidate();
            repaint();
        } catch (Exception ex) {
            Theme.error(this, ex);
        }
    }

    private JPanel statCard(String label, int value, String description, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 4));
        card.add(accentBar, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel title = Theme.muted(label.toUpperCase(LOCALE_ID));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 12f));

        JLabel number = new JLabel(numberFormat.format(value));
        number.setForeground(Theme.TEXT);
        number.setFont(number.getFont().deriveFont(Font.BOLD, 42f));

        JLabel info = Theme.muted(description);
        info.setFont(info.getFont().deriveFont(Font.PLAIN, 13f));

        body.add(title, BorderLayout.NORTH);
        body.add(number, BorderLayout.CENTER);
        body.add(info, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    public void refreshData() {
        loadStats();
    }
}
