package id.ac.utb.pbo2.config;

public final class AppConfig {
    public static final String DB_HOST = env("DB_HOST", "localhost");
    public static final String DB_PORT = env("DB_PORT", "3306");
    public static final String DB_NAME = env("DB_NAME", "uts_pbo2");
    public static final String DB_USER = env("DB_USER", "root");
    public static final String DB_PASSWORD = env("DB_PASSWORD", "");

    private AppConfig() {
    }

    public static String serverUrl() {
        return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
                + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    }

    public static String databaseUrl() {
        return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
