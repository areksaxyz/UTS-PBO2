package id.ac.utb.pbo2.ui;

import id.ac.utb.pbo2.model.PenggunaSaatIni;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final PenggunaSaatIni user;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private DashboardPanel dashboardPanel;
    private MahasiswaPanel mahasiswaPanel;

    public MainFrame(PenggunaSaatIni user) {
        this.user = user;
        setTitle("SIAKAD PBO2 - " + user.getRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BACKGROUND);
        setContentPane(root);

        root.add(sidebar(), BorderLayout.WEST);
        content.setBackground(Theme.BACKGROUND);
        root.add(content, BorderLayout.CENTER);

        registerPages();
        showPage("dashboard");
        setSize(1360, 820);
        setLocationRelativeTo(null);
    }

    private JPanel sidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 14));
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
        ));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        JLabel appName = new JLabel("SIAKAD PBO2");
        appName.setFont(appName.getFont().deriveFont(Font.BOLD, 20f));
        appName.setForeground(Theme.TEXT);
        header.add(appName, BorderLayout.NORTH);
        header.add(Theme.muted(user.getNamaLengkap() + " - " + user.getRole()), BorderLayout.CENTER);
        sidebar.add(header, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        menu.setOpaque(false);
        addNav(menu, "dashboard", "Dashboard", true);
        addNav(menu, "mahasiswa", "Master Mahasiswa", user.isAdmin());
        addNav(menu, "dosen", "Master Dosen", user.isAdmin());
        addNav(menu, "matakuliah", "Master Mata Kuliah", user.isAdmin());
        addNav(menu, "krs", "Transaksi KRS", user.isOperator());
        addNav(menu, "listMahasiswa", "List Mahasiswa", user.isOperator());
        addNav(menu, "nilai", "Transaksi Nilai", user.isOperator());
        addNav(menu, "users", "Setting User", user.isAdmin());
        addNav(menu, "password", "Ganti Password", true);
        sidebar.add(menu, BorderLayout.CENTER);

        JButton logout = Theme.dangerButton("Logout");
        logout.addActionListener(event -> {
            dispose();
            LoginFrame frame = new LoginFrame();
            frame.setReady();
            frame.setVisible(true);
        });
        sidebar.add(logout, BorderLayout.SOUTH);

        return sidebar;
    }

    private void registerPages() {
        dashboardPanel = new DashboardPanel(user);
        content.add(dashboardPanel, "dashboard");
        mahasiswaPanel = new MahasiswaPanel(user);
        content.add(mahasiswaPanel, "mahasiswa");
        content.add(new DosenPanel(), "dosen");
        content.add(new MataKuliahPanel(), "matakuliah");
        content.add(new KrsPanel(user), "krs");
        content.add(new StudentListPanel(), "listMahasiswa");
        content.add(new NilaiPanel(user), "nilai");
        content.add(new UserPanel(), "users");
        content.add(new PasswordPanel(user), "password");
    }

    private void addNav(JPanel menu, String key, String label, boolean enabled) {
        JButton button = Theme.secondaryButton(label);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setEnabled(enabled);
        if (!enabled) {
            button.setToolTipText("Menu ini non aktif untuk role " + user.getRole());
        }
        button.addActionListener(event -> showPage(key));
        navButtons.put(key, button);
        menu.add(button);
    }

    private void showPage(String key) {
        JButton selected = navButtons.get(key);
        if (selected == null || !selected.isEnabled()) {
            return;
        }
        navButtons.forEach((page, button) -> {
            boolean active = page.equals(key);
            button.setBackground(active ? Theme.PRIMARY : Color.WHITE);
            button.setForeground(active ? Color.WHITE : Theme.TEXT);
        });
        cardLayout.show(content, key);
        if ("dashboard".equals(key) && dashboardPanel != null) {
            dashboardPanel.refreshData();
        }
        if ("mahasiswa".equals(key) && mahasiswaPanel != null) {
            mahasiswaPanel.refreshData();
        }
        content.revalidate();
        content.repaint();
    }
}
