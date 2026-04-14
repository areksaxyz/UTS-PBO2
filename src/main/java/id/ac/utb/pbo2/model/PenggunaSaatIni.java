package id.ac.utb.pbo2.model;

public class PenggunaSaatIni {
    private final int id;
    private final String username;
    private final String namaLengkap;
    private final String role;

    public PenggunaSaatIni(int id, String username, String namaLengkap, String role) {
        this.id = id;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isOperator() {
        return "OPERATOR".equalsIgnoreCase(role);
    }
}
