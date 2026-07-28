import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdrApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new DatabaseInitializer().initialize();
                new MainFrame().setVisible(true);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,
                        "Connexion MySQL impossible : " + exception.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    static final class DatabaseConfig {
        private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/pdr_maintenance?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        private static final String DEFAULT_USER = "root";
        private static final String DEFAULT_PASSWORD = "";

        static Connection getConnection() throws SQLException {
            String url = value("PDR_DB_URL", DEFAULT_URL);
            String user = value("PDR_DB_USER", DEFAULT_USER);
            String password = value("PDR_DB_PASSWORD", DEFAULT_PASSWORD);
            return DriverManager.getConnection(url, user, password);
        }

        private static String value(String key, String fallback) {
            String systemValue = System.getProperty(key);
            if (systemValue != null && !systemValue.isBlank()) {
                return systemValue;
            }
            String envValue = System.getenv(key);
            return envValue == null || envValue.isBlank() ? fallback : envValue;
        }
    }

    static final class DatabaseInitializer {
        void initialize() throws SQLException {
            try (Connection connection = DatabaseConfig.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS PieceRechange (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            code_article VARCHAR(80) NOT NULL UNIQUE,
                            description_piece TEXT NOT NULL,
                            unite VARCHAR(50) NOT NULL,
                            groupe_articles VARCHAR(50) NOT NULL,
                            quantite_consommee_historique INT NOT NULL DEFAULT 0,
                            sous_ensemble VARCHAR(255),
                            reference_constructeur VARCHAR(255),
                            piece_usure BOOLEAN NOT NULL DEFAULT FALSE,
                            stock_actuel INT NOT NULL DEFAULT 0,
                            stock_minimum INT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS Machine (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            code VARCHAR(80) NOT NULL UNIQUE,
                            nom VARCHAR(150) NOT NULL,
                            emplacement VARCHAR(150),
                            description TEXT
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS Technicien (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            matricule VARCHAR(80) NOT NULL UNIQUE,
                            nom VARCHAR(100) NOT NULL,
                            prenom VARCHAR(100),
                            specialite VARCHAR(150),
                            telephone VARCHAR(50)
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS EntreeStock (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            piece_id BIGINT NOT NULL,
                            technicien_id BIGINT,
                            quantite INT NOT NULL,
                            date_entree TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            commentaire TEXT,
                            CONSTRAINT fk_entree_piece FOREIGN KEY (piece_id) REFERENCES PieceRechange(id) ON DELETE CASCADE,
                            CONSTRAINT fk_entree_technicien FOREIGN KEY (technicien_id) REFERENCES Technicien(id) ON DELETE SET NULL,
                            CONSTRAINT chk_entree_quantite CHECK (quantite > 0)
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS SortieStock (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            piece_id BIGINT NOT NULL,
                            machine_id BIGINT,
                            technicien_id BIGINT,
                            quantite INT NOT NULL,
                            date_sortie TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            commentaire TEXT,
                            CONSTRAINT fk_sortie_piece FOREIGN KEY (piece_id) REFERENCES PieceRechange(id) ON DELETE CASCADE,
                            CONSTRAINT fk_sortie_machine FOREIGN KEY (machine_id) REFERENCES Machine(id) ON DELETE SET NULL,
                            CONSTRAINT fk_sortie_technicien FOREIGN KEY (technicien_id) REFERENCES Technicien(id) ON DELETE SET NULL,
                            CONSTRAINT chk_sortie_quantite CHECK (quantite > 0)
                        )
                        """);
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS AlerteStock (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            piece_id BIGINT NOT NULL,
                            message VARCHAR(255) NOT NULL,
                            date_alerte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            resolue BOOLEAN NOT NULL DEFAULT FALSE,
                            CONSTRAINT fk_alerte_piece FOREIGN KEY (piece_id) REFERENCES PieceRechange(id) ON DELETE CASCADE
                        )
                        """);
            }
        }
    }

    static class PieceRechange {
        long id;
        String codeArticle;
        String descriptionPiece;
        String unite;
        String groupeArticles;
        int quantiteConsommeeHistorique;
        String sousEnsemble;
        String referenceConstructeur;
        boolean pieceUsure;
        int stockActuel;
        int stockMinimum;

        @Override
        public String toString() {
            return codeArticle + " - " + descriptionPiece;
        }
    }

    static class Machine {
        long id;
        String code;
        String nom;
        String emplacement;
        String description;

        @Override
        public String toString() {
            return code + " - " + nom;
        }
    }

    static class Technicien {
        long id;
        String matricule;
        String nom;
        String prenom;
        String specialite;
        String telephone;

        @Override
        public String toString() {
            return matricule + " - " + nom + " " + nullToEmpty(prenom);
        }
    }

    static class AlerteStock {
        long id;
        long pieceId;
        String codeArticle;
        String message;
        LocalDateTime dateAlerte;
        boolean resolue;
    }

    static final class PieceDao {
        void save(PieceRechange piece) throws SQLException {
            String sql = """
                    INSERT INTO PieceRechange
                    (code_article, description_piece, unite, groupe_articles, quantite_consommee_historique,
                     sous_ensemble, reference_constructeur, piece_usure, stock_actuel, stock_minimum)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        description_piece = VALUES(description_piece),
                        unite = VALUES(unite),
                        groupe_articles = VALUES(groupe_articles),
                        quantite_consommee_historique = VALUES(quantite_consommee_historique),
                        sous_ensemble = VALUES(sous_ensemble),
                        reference_constructeur = VALUES(reference_constructeur),
                        piece_usure = VALUES(piece_usure)
                    """;
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bind(statement, piece);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        piece.id = keys.getLong(1);
                    }
                }
            }
        }

        void update(PieceRechange piece) throws SQLException {
            String sql = """
                    UPDATE PieceRechange
                    SET code_article = ?, description_piece = ?, unite = ?, groupe_articles = ?,
                        quantite_consommee_historique = ?, sous_ensemble = ?, reference_constructeur = ?,
                        piece_usure = ?, stock_actuel = ?, stock_minimum = ?
                    WHERE id = ?
                    """;
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                bind(statement, piece);
                statement.setLong(11, piece.id);
                statement.executeUpdate();
            }
        }

        void delete(long id) throws SQLException {
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM PieceRechange WHERE id = ?")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }

        List<PieceRechange> search(String query) throws SQLException {
            String sql = """
                    SELECT * FROM PieceRechange
                    WHERE ? = '' OR code_article LIKE ? OR description_piece LIKE ? OR groupe_articles LIKE ? OR sous_ensemble LIKE ?
                    ORDER BY code_article
                    """;
            String q = query == null ? "" : query.trim();
            String pattern = "%" + q + "%";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, q);
                for (int i = 2; i <= 5; i++) {
                    statement.setString(i, pattern);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<PieceRechange> pieces = new ArrayList<>();
                    while (resultSet.next()) {
                        pieces.add(mapPiece(resultSet));
                    }
                    return pieces;
                }
            }
        }

        private void bind(PreparedStatement statement, PieceRechange piece) throws SQLException {
            statement.setString(1, piece.codeArticle);
            statement.setString(2, piece.descriptionPiece);
            statement.setString(3, piece.unite);
            statement.setString(4, piece.groupeArticles);
            statement.setInt(5, piece.quantiteConsommeeHistorique);
            statement.setString(6, blankToNull(piece.sousEnsemble));
            statement.setString(7, blankToNull(piece.referenceConstructeur));
            statement.setBoolean(8, piece.pieceUsure);
            statement.setInt(9, piece.stockActuel);
            statement.setInt(10, piece.stockMinimum);
        }
    }

    static final class MachineDao {
        void save(Machine machine) throws SQLException {
            String sql = machine.id == 0
                    ? "INSERT INTO Machine (code, nom, emplacement, description) VALUES (?, ?, ?, ?)"
                    : "UPDATE Machine SET code = ?, nom = ?, emplacement = ?, description = ? WHERE id = ?";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, machine.code);
                statement.setString(2, machine.nom);
                statement.setString(3, machine.emplacement);
                statement.setString(4, machine.description);
                if (machine.id != 0) {
                    statement.setLong(5, machine.id);
                }
                statement.executeUpdate();
            }
        }

        void delete(long id) throws SQLException {
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM Machine WHERE id = ?")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }

        List<Machine> findAll() throws SQLException {
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM Machine ORDER BY code");
                 ResultSet resultSet = statement.executeQuery()) {
                List<Machine> machines = new ArrayList<>();
                while (resultSet.next()) {
                    Machine machine = new Machine();
                    machine.id = resultSet.getLong("id");
                    machine.code = resultSet.getString("code");
                    machine.nom = resultSet.getString("nom");
                    machine.emplacement = resultSet.getString("emplacement");
                    machine.description = resultSet.getString("description");
                    machines.add(machine);
                }
                return machines;
            }
        }
    }

    static final class TechnicienDao {
        void save(Technicien technicien) throws SQLException {
            String sql = technicien.id == 0
                    ? "INSERT INTO Technicien (matricule, nom, prenom, specialite, telephone) VALUES (?, ?, ?, ?, ?)"
                    : "UPDATE Technicien SET matricule = ?, nom = ?, prenom = ?, specialite = ?, telephone = ? WHERE id = ?";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, technicien.matricule);
                statement.setString(2, technicien.nom);
                statement.setString(3, technicien.prenom);
                statement.setString(4, technicien.specialite);
                statement.setString(5, technicien.telephone);
                if (technicien.id != 0) {
                    statement.setLong(6, technicien.id);
                }
                statement.executeUpdate();
            }
        }

        void delete(long id) throws SQLException {
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM Technicien WHERE id = ?")) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }

        List<Technicien> findAll() throws SQLException {
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM Technicien ORDER BY nom, prenom");
                 ResultSet resultSet = statement.executeQuery()) {
                List<Technicien> techniciens = new ArrayList<>();
                while (resultSet.next()) {
                    Technicien technicien = new Technicien();
                    technicien.id = resultSet.getLong("id");
                    technicien.matricule = resultSet.getString("matricule");
                    technicien.nom = resultSet.getString("nom");
                    technicien.prenom = resultSet.getString("prenom");
                    technicien.specialite = resultSet.getString("specialite");
                    technicien.telephone = resultSet.getString("telephone");
                    techniciens.add(technicien);
                }
                return techniciens;
            }
        }
    }

    static final class StockDao {
        boolean entree(long pieceId, Long technicienId, int quantite, String commentaire) throws SQLException {
            return movement(pieceId, null, technicienId, quantite, commentaire, true);
        }

        boolean sortie(long pieceId, Long machineId, Long technicienId, int quantite, String commentaire) throws SQLException {
            return movement(pieceId, machineId, technicienId, quantite, commentaire, false);
        }

        private boolean movement(long pieceId, Long machineId, Long technicienId, int quantite, String commentaire, boolean entree) throws SQLException {
            if (quantite <= 0) {
                throw new IllegalArgumentException("La quantité doit être positive.");
            }
            try (Connection connection = DatabaseConfig.getConnection()) {
                connection.setAutoCommit(false);
                try {
                    int stock = stockForUpdate(connection, pieceId);
                    if (!entree && quantite > stock) {
                        throw new IllegalStateException("Sortie impossible : stock disponible insuffisant.");
                    }
                    if (entree) {
                        insertEntree(connection, pieceId, technicienId, quantite, commentaire);
                    } else {
                        insertSortie(connection, pieceId, machineId, technicienId, quantite, commentaire);
                    }
                    updateStock(connection, pieceId, entree ? quantite : -quantite);
                    boolean alert = createAlertIfNeeded(connection, pieceId);
                    connection.commit();
                    return alert;
                } catch (SQLException | RuntimeException exception) {
                    connection.rollback();
                    throw exception;
                }
            }
        }

        private int stockForUpdate(Connection connection, long pieceId) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement("SELECT stock_actuel FROM PieceRechange WHERE id = ? FOR UPDATE")) {
                statement.setLong(1, pieceId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                    throw new IllegalArgumentException("Pièce introuvable.");
                }
            }
        }

        private void insertEntree(Connection connection, long pieceId, Long technicienId, int quantite, String commentaire) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO EntreeStock (piece_id, technicien_id, quantite, commentaire) VALUES (?, ?, ?, ?)")) {
                statement.setLong(1, pieceId);
                setNullableLong(statement, 2, technicienId);
                statement.setInt(3, quantite);
                statement.setString(4, commentaire);
                statement.executeUpdate();
            }
        }

        private void insertSortie(Connection connection, long pieceId, Long machineId, Long technicienId, int quantite, String commentaire) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO SortieStock (piece_id, machine_id, technicien_id, quantite, commentaire) VALUES (?, ?, ?, ?, ?)")) {
                statement.setLong(1, pieceId);
                setNullableLong(statement, 2, machineId);
                setNullableLong(statement, 3, technicienId);
                statement.setInt(4, quantite);
                statement.setString(5, commentaire);
                statement.executeUpdate();
            }
        }

        private void updateStock(Connection connection, long pieceId, int delta) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE PieceRechange SET stock_actuel = stock_actuel + ? WHERE id = ?")) {
                statement.setInt(1, delta);
                statement.setLong(2, pieceId);
                statement.executeUpdate();
            }
        }

        private boolean createAlertIfNeeded(Connection connection, long pieceId) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement("SELECT code_article, stock_actuel, stock_minimum FROM PieceRechange WHERE id = ?")) {
                statement.setLong(1, pieceId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next() || resultSet.getInt("stock_actuel") > resultSet.getInt("stock_minimum")) {
                        return false;
                    }
                    try (PreparedStatement insert = connection.prepareStatement("INSERT INTO AlerteStock (piece_id, message) VALUES (?, ?)")) {
                        insert.setLong(1, pieceId);
                        insert.setString(2, "Stock minimum atteint pour " + resultSet.getString("code_article"));
                        insert.executeUpdate();
                    }
                    return true;
                }
            }
        }
    }

    static final class AlerteDao {
        List<AlerteStock> findAll() throws SQLException {
            String sql = """
                    SELECT a.id, a.piece_id, p.code_article, a.message, a.date_alerte, a.resolue
                    FROM AlerteStock a JOIN PieceRechange p ON p.id = a.piece_id
                    ORDER BY a.date_alerte DESC
                    """;
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                List<AlerteStock> alertes = new ArrayList<>();
                while (resultSet.next()) {
                    AlerteStock alerte = new AlerteStock();
                    alerte.id = resultSet.getLong("id");
                    alerte.pieceId = resultSet.getLong("piece_id");
                    alerte.codeArticle = resultSet.getString("code_article");
                    alerte.message = resultSet.getString("message");
                    Timestamp timestamp = resultSet.getTimestamp("date_alerte");
                    alerte.dateAlerte = timestamp == null ? null : timestamp.toLocalDateTime();
                    alerte.resolue = resultSet.getBoolean("resolue");
                    alertes.add(alerte);
                }
                return alertes;
            }
        }
    }

    static final class ImportService {
        private final PieceDao dao = new PieceDao();

        int importFile(Path path) throws Exception {
            List<PieceRechange> pieces = path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")
                    ? readCsv(path)
                    : readExcel(path);
            for (PieceRechange piece : pieces) {
                dao.save(piece);
            }
            return pieces.size();
        }

        private List<PieceRechange> readExcel(Path path) throws Exception {
            try (InputStream inputStream = Files.newInputStream(path);
                 Workbook workbook = WorkbookFactory.create(inputStream)) {
                DataFormatter formatter = new DataFormatter(Locale.FRANCE);
                for (Sheet sheet : workbook) {
                    for (Row headerRow : sheet) {
                        Map<String, Integer> header = excelHeader(headerRow, formatter);
                        if (!header.containsKey("code article")) {
                            continue;
                        }
                        List<PieceRechange> pieces = new ArrayList<>();
                        for (int i = headerRow.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                            Row row = sheet.getRow(i);
                            if (row != null) {
                                PieceRechange piece = pieceFromExcel(row, header, formatter);
                                if (piece != null) {
                                    pieces.add(piece);
                                }
                            }
                        }
                        return pieces;
                    }
                }
                return List.of();
            }
        }

        private Map<String, Integer> excelHeader(Row row, DataFormatter formatter) {
            Map<String, Integer> header = new HashMap<>();
            for (Cell cell : row) {
                header.put(normalize(formatter.formatCellValue(cell)), cell.getColumnIndex());
            }
            return header;
        }

        private PieceRechange pieceFromExcel(Row row, Map<String, Integer> header, DataFormatter formatter) {
            String code = excelValue(row, header, formatter, "code article");
            if (code.isBlank()) {
                return null;
            }
            PieceRechange piece = new PieceRechange();
            piece.codeArticle = code;
            piece.descriptionPiece = excelValue(row, header, formatter, "description de la piece et outil", "description de la piece");
            piece.unite = excelValue(row, header, formatter, "unite");
            piece.groupeArticles = excelValue(row, header, formatter, "groupe d articles");
            piece.quantiteConsommeeHistorique = parseInt(excelValue(row, header, formatter, "qte consommee historique", "quantite consommee historique"));
            piece.sousEnsemble = excelValue(row, header, formatter, "sous ensemble ref plan", "sous ensemble");
            piece.referenceConstructeur = excelValue(row, header, formatter, "ref constructeur", "reference constructeur");
            piece.pieceUsure = "oui".equals(normalize(excelValue(row, header, formatter, "piece d usure")));
            return piece;
        }

        private String excelValue(Row row, Map<String, Integer> header, DataFormatter formatter, String... keys) {
            for (String key : keys) {
                Integer index = header.get(key);
                if (index != null) {
                    Cell cell = row.getCell(index);
                    return cell == null ? "" : formatter.formatCellValue(cell).trim();
                }
            }
            return "";
        }

        private List<PieceRechange> readCsv(Path path) throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))) {
                String line;
                Map<String, Integer> header = null;
                List<PieceRechange> pieces = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    List<String> columns = parseCsvLine(line);
                    if (header == null) {
                        Map<String, Integer> candidate = csvHeader(columns);
                        if (candidate.containsKey("code article")) {
                            header = candidate;
                        }
                        continue;
                    }
                    PieceRechange piece = pieceFromCsv(columns, header);
                    if (piece != null) {
                        pieces.add(piece);
                    }
                }
                return pieces;
            }
        }

        private Map<String, Integer> csvHeader(List<String> columns) {
            Map<String, Integer> header = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                header.put(normalize(columns.get(i)), i);
            }
            return header;
        }

        private PieceRechange pieceFromCsv(List<String> columns, Map<String, Integer> header) {
            String code = csvValue(columns, header, "code article");
            if (code.isBlank()) {
                return null;
            }
            PieceRechange piece = new PieceRechange();
            piece.codeArticle = code;
            piece.descriptionPiece = csvValue(columns, header, "description de la piece et outil", "description de la piece");
            piece.unite = csvValue(columns, header, "unite");
            piece.groupeArticles = csvValue(columns, header, "groupe d articles");
            piece.quantiteConsommeeHistorique = parseInt(csvValue(columns, header, "qte consommee historique", "quantite consommee historique"));
            piece.sousEnsemble = csvValue(columns, header, "sous ensemble ref plan", "sous ensemble");
            piece.referenceConstructeur = csvValue(columns, header, "ref constructeur", "reference constructeur");
            piece.pieceUsure = "oui".equals(normalize(csvValue(columns, header, "piece d usure")));
            return piece;
        }

        private String csvValue(List<String> columns, Map<String, Integer> header, String... keys) {
            for (String key : keys) {
                Integer index = header.get(key);
                if (index != null && index < columns.size()) {
                    return columns.get(index).trim();
                }
            }
            return "";
        }
    }

    static final class MainFrame extends JFrame {
        private final PieceDao pieceDao = new PieceDao();
        private final MachineDao machineDao = new MachineDao();
        private final TechnicienDao technicienDao = new TechnicienDao();
        private final StockDao stockDao = new StockDao();
        private final AlerteDao alerteDao = new AlerteDao();
        private final ImportService importService = new ImportService();
        private final JTabbedPane tabs = new JTabbedPane();
        private final JTable pieceTable = new JTable();
        private final JTable machineTable = new JTable();
        private final JTable technicienTable = new JTable();
        private final JTable alerteTable = new JTable();
        private List<PieceRechange> pieces = new ArrayList<>();
        private List<Machine> machines = new ArrayList<>();
        private List<Technicien> techniciens = new ArrayList<>();
        private List<AlerteStock> alertes = new ArrayList<>();

        MainFrame() {
            super("Gestion PDR - fichier unique");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1200, 720);
            setLocationRelativeTo(null);
            setJMenuBar(menuBar());
            tabs.addTab("Pièces", piecePanel());
            tabs.addTab("Entrées", stockPanel(true));
            tabs.addTab("Sorties", stockPanel(false));
            tabs.addTab("Machines", machinePanel());
            tabs.addTab("Techniciens", technicienPanel());
            tabs.addTab("Alertes", alertePanel());
            add(tabs, BorderLayout.CENTER);
            refreshAll();
        }

        private JMenuBar menuBar() {
            JMenuBar bar = new JMenuBar();
            String[] labels = {"Menu Pièces", "Menu Entrées", "Menu Sorties", "Menu Machines", "Menu Techniciens", "Menu Alertes"};
            for (int i = 0; i < labels.length; i++) {
                int index = i;
                JMenu menu = new JMenu(labels[i]);
                JMenuItem item = new JMenuItem("Ouvrir");
                item.addActionListener(event -> tabs.setSelectedIndex(index));
                menu.add(item);
                bar.add(menu);
            }
            return bar;
        }

        private JPanel piecePanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            JTextField search = new JTextField();
            JButton searchButton = new JButton("Rechercher");
            JButton allButton = new JButton("Afficher toutes les pièces");
            JButton importButton = new JButton("Importer Excel/CSV");
            JPanel top = new JPanel(new BorderLayout(5, 5));
            JPanel topButtons = new JPanel();
            topButtons.add(searchButton);
            topButtons.add(allButton);
            topButtons.add(importButton);
            top.add(search, BorderLayout.CENTER);
            top.add(topButtons, BorderLayout.EAST);
            panel.add(top, BorderLayout.NORTH);
            panel.add(new JScrollPane(pieceTable), BorderLayout.CENTER);
            JButton add = new JButton("Ajouter");
            JButton edit = new JButton("Modifier");
            JButton delete = new JButton("Supprimer");
            JPanel buttons = new JPanel();
            buttons.add(add);
            buttons.add(edit);
            buttons.add(delete);
            panel.add(buttons, BorderLayout.SOUTH);
            searchButton.addActionListener(event -> safe(() -> loadPieces(search.getText())));
            allButton.addActionListener(event -> safe(() -> loadPieces("")));
            importButton.addActionListener(event -> importFile());
            add.addActionListener(event -> pieceDialog(null));
            edit.addActionListener(event -> pieceDialog(selectedPiece()));
            delete.addActionListener(event -> deletePiece());
            return panel;
        }

        private JPanel stockPanel(boolean entree) {
            JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
            JComboBox<PieceRechange> pieceCombo = new JComboBox<>();
            JComboBox<Technicien> techCombo = new JComboBox<>();
            JComboBox<Machine> machineCombo = new JComboBox<>();
            JSpinner quantite = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
            JTextArea commentaire = new JTextArea(4, 30);
            JButton reload = new JButton("Actualiser");
            JButton submit = new JButton(entree ? "Enregistrer entrée" : "Enregistrer sortie");
            reload.addActionListener(event -> {
                refreshAll();
                fill(pieceCombo, pieces);
                fill(techCombo, techniciens);
                fill(machineCombo, machines);
            });
            panel.add(new JLabel("Pièce", SwingConstants.RIGHT));
            panel.add(pieceCombo);
            panel.add(new JLabel("Technicien", SwingConstants.RIGHT));
            panel.add(techCombo);
            if (!entree) {
                panel.add(new JLabel("Machine", SwingConstants.RIGHT));
                panel.add(machineCombo);
            }
            panel.add(new JLabel("Quantité", SwingConstants.RIGHT));
            panel.add(quantite);
            panel.add(new JLabel("Commentaire", SwingConstants.RIGHT));
            panel.add(new JScrollPane(commentaire));
            panel.add(reload);
            panel.add(submit);
            submit.addActionListener(event -> safe(() -> {
                PieceRechange piece = (PieceRechange) pieceCombo.getSelectedItem();
                if (piece == null) {
                    throw new IllegalArgumentException("Choisir une pièce.");
                }
                Technicien tech = (Technicien) techCombo.getSelectedItem();
                Machine machine = (Machine) machineCombo.getSelectedItem();
                boolean alert = entree
                        ? stockDao.entree(piece.id, id(tech), (Integer) quantite.getValue(), commentaire.getText())
                        : stockDao.sortie(piece.id, id(machine), id(tech), (Integer) quantite.getValue(), commentaire.getText());
                refreshAll();
                JOptionPane.showMessageDialog(this, "Mouvement enregistré.");
                if (alert) {
                    JOptionPane.showMessageDialog(null, "Attention : stock inférieur au seuil minimum");
                }
            }));
            return panel;
        }

        private JPanel machinePanel() {
            return crudPanel(machineTable, "Machine", () -> machineDialog(null), () -> machineDialog(selectedMachine()), this::deleteMachine);
        }

        private JPanel technicienPanel() {
            return crudPanel(technicienTable, "Technicien", () -> technicienDialog(null), () -> technicienDialog(selectedTechnicien()), this::deleteTechnicien);
        }

        private JPanel alertePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(alerteTable), BorderLayout.CENTER);
            JButton refresh = new JButton("Actualiser");
            refresh.addActionListener(event -> safe(this::loadAlertes));
            panel.add(refresh, BorderLayout.SOUTH);
            return panel;
        }

        private JPanel crudPanel(JTable table, String label, Runnable add, Runnable edit, Runnable delete) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            JButton addButton = new JButton("Ajouter " + label);
            JButton editButton = new JButton("Modifier");
            JButton deleteButton = new JButton("Supprimer");
            JPanel buttons = new JPanel();
            buttons.add(addButton);
            buttons.add(editButton);
            buttons.add(deleteButton);
            panel.add(buttons, BorderLayout.SOUTH);
            addButton.addActionListener(event -> add.run());
            editButton.addActionListener(event -> edit.run());
            deleteButton.addActionListener(event -> delete.run());
            return panel;
        }

        private void importFile() {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                safe(() -> {
                    int count = importService.importFile(chooser.getSelectedFile().toPath());
                    refreshAll();
                    JOptionPane.showMessageDialog(this, count + " pièces importées.");
                });
            }
        }

        private void pieceDialog(PieceRechange existing) {
            PieceRechange piece = existing == null ? new PieceRechange() : existing;
            JTextField code = new JTextField(nullToEmpty(piece.codeArticle));
            JTextField description = new JTextField(nullToEmpty(piece.descriptionPiece));
            JTextField unite = new JTextField(nullToEmpty(piece.unite));
            JTextField groupe = new JTextField(nullToEmpty(piece.groupeArticles));
            JSpinner historique = new JSpinner(new SpinnerNumberModel(piece.quantiteConsommeeHistorique, 0, 1_000_000, 1));
            JTextField sous = new JTextField(nullToEmpty(piece.sousEnsemble));
            JTextField ref = new JTextField(nullToEmpty(piece.referenceConstructeur));
            JComboBox<String> usure = new JComboBox<>(new String[]{"Non", "Oui"});
            usure.setSelectedItem(piece.pieceUsure ? "Oui" : "Non");
            JSpinner stock = new JSpinner(new SpinnerNumberModel(piece.stockActuel, 0, 1_000_000, 1));
            JSpinner min = new JSpinner(new SpinnerNumberModel(piece.stockMinimum, 0, 1_000_000, 1));
            JPanel form = form(new String[]{"Code", "Description", "Unité", "Groupe", "Qté consommée", "Sous-ensemble", "Réf. constructeur", "Pièce d'usure", "Stock", "Stock minimum"},
                    code, description, unite, groupe, historique, sous, ref, usure, stock, min);
            if (JOptionPane.showConfirmDialog(this, form, "Pièce", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                safe(() -> {
                    piece.codeArticle = code.getText().trim();
                    piece.descriptionPiece = description.getText().trim();
                    piece.unite = unite.getText().trim();
                    piece.groupeArticles = groupe.getText().trim();
                    piece.quantiteConsommeeHistorique = (Integer) historique.getValue();
                    piece.sousEnsemble = sous.getText().trim();
                    piece.referenceConstructeur = ref.getText().trim();
                    piece.pieceUsure = "Oui".equals(usure.getSelectedItem());
                    piece.stockActuel = (Integer) stock.getValue();
                    piece.stockMinimum = (Integer) min.getValue();
                    if (piece.id == 0) {
                        pieceDao.save(piece);
                    } else {
                        pieceDao.update(piece);
                    }
                    refreshAll();
                });
            }
        }

        private void machineDialog(Machine existing) {
            Machine machine = existing == null ? new Machine() : existing;
            JTextField code = new JTextField(nullToEmpty(machine.code));
            JTextField nom = new JTextField(nullToEmpty(machine.nom));
            JTextField emplacement = new JTextField(nullToEmpty(machine.emplacement));
            JTextField description = new JTextField(nullToEmpty(machine.description));
            if (JOptionPane.showConfirmDialog(this, form(new String[]{"Code", "Nom", "Emplacement", "Description"}, code, nom, emplacement, description), "Machine", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                safe(() -> {
                    machine.code = code.getText().trim();
                    machine.nom = nom.getText().trim();
                    machine.emplacement = emplacement.getText().trim();
                    machine.description = description.getText().trim();
                    machineDao.save(machine);
                    refreshAll();
                });
            }
        }

        private void technicienDialog(Technicien existing) {
            Technicien technicien = existing == null ? new Technicien() : existing;
            JTextField matricule = new JTextField(nullToEmpty(technicien.matricule));
            JTextField nom = new JTextField(nullToEmpty(technicien.nom));
            JTextField prenom = new JTextField(nullToEmpty(technicien.prenom));
            JTextField specialite = new JTextField(nullToEmpty(technicien.specialite));
            JTextField telephone = new JTextField(nullToEmpty(technicien.telephone));
            if (JOptionPane.showConfirmDialog(this, form(new String[]{"Matricule", "Nom", "Prénom", "Spécialité", "Téléphone"}, matricule, nom, prenom, specialite, telephone), "Technicien", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                safe(() -> {
                    technicien.matricule = matricule.getText().trim();
                    technicien.nom = nom.getText().trim();
                    technicien.prenom = prenom.getText().trim();
                    technicien.specialite = specialite.getText().trim();
                    technicien.telephone = telephone.getText().trim();
                    technicienDao.save(technicien);
                    refreshAll();
                });
            }
        }

        private JPanel form(String[] labels, Component... fields) {
            JPanel panel = new JPanel(new GridLayout(labels.length, 2, 6, 6));
            for (int i = 0; i < labels.length; i++) {
                panel.add(new JLabel(labels[i], SwingConstants.RIGHT));
                panel.add(fields[i]);
            }
            return panel;
        }

        private void refreshAll() {
            safe(() -> {
                loadPieces("");
                loadMachines();
                loadTechniciens();
                loadAlertes();
            });
        }

        private void loadPieces(String q) throws SQLException {
            pieces = pieceDao.search(q);
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code", "Description", "Unité", "Groupe", "Qté hist.", "Sous-ensemble", "Réf.", "Usure", "Stock", "Min"}, 0);
            for (PieceRechange p : pieces) {
                model.addRow(new Object[]{p.id, p.codeArticle, p.descriptionPiece, p.unite, p.groupeArticles, p.quantiteConsommeeHistorique, p.sousEnsemble, p.referenceConstructeur, p.pieceUsure ? "Oui" : "Non", p.stockActuel, p.stockMinimum});
            }
            pieceTable.setModel(model);
        }

        private void loadMachines() throws SQLException {
            machines = machineDao.findAll();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code", "Nom", "Emplacement", "Description"}, 0);
            for (Machine m : machines) {
                model.addRow(new Object[]{m.id, m.code, m.nom, m.emplacement, m.description});
            }
            machineTable.setModel(model);
        }

        private void loadTechniciens() throws SQLException {
            techniciens = technicienDao.findAll();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Matricule", "Nom", "Prénom", "Spécialité", "Téléphone"}, 0);
            for (Technicien t : techniciens) {
                model.addRow(new Object[]{t.id, t.matricule, t.nom, t.prenom, t.specialite, t.telephone});
            }
            technicienTable.setModel(model);
        }

        private void loadAlertes() throws SQLException {
            alertes = alerteDao.findAll();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code article", "Message", "Date", "Résolue"}, 0);
            for (AlerteStock a : alertes) {
                model.addRow(new Object[]{a.id, a.codeArticle, a.message, a.dateAlerte, a.resolue ? "Oui" : "Non"});
            }
            alerteTable.setModel(model);
        }

        private void deletePiece() {
            PieceRechange piece = selectedPiece();
            if (piece != null && confirm()) {
                safe(() -> {
                    pieceDao.delete(piece.id);
                    refreshAll();
                });
            }
        }

        private void deleteMachine() {
            Machine machine = selectedMachine();
            if (machine != null && confirm()) {
                safe(() -> {
                    machineDao.delete(machine.id);
                    refreshAll();
                });
            }
        }

        private void deleteTechnicien() {
            Technicien technicien = selectedTechnicien();
            if (technicien != null && confirm()) {
                safe(() -> {
                    technicienDao.delete(technicien.id);
                    refreshAll();
                });
            }
        }

        private boolean confirm() {
            return JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }

        private PieceRechange selectedPiece() {
            int row = pieceTable.getSelectedRow();
            return row < 0 ? null : pieces.get(row);
        }

        private Machine selectedMachine() {
            int row = machineTable.getSelectedRow();
            return row < 0 ? null : machines.get(row);
        }

        private Technicien selectedTechnicien() {
            int row = technicienTable.getSelectedRow();
            return row < 0 ? null : techniciens.get(row);
        }

        private <T> void fill(JComboBox<T> combo, List<T> items) {
            combo.removeAllItems();
            for (T item : items) {
                combo.addItem(item);
            }
        }

        private void safe(Task task) {
            try {
                task.run();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @FunctionalInterface
    interface Task {
        void run() throws Exception;
    }

    static PieceRechange mapPiece(ResultSet resultSet) throws SQLException {
        PieceRechange piece = new PieceRechange();
        piece.id = resultSet.getLong("id");
        piece.codeArticle = resultSet.getString("code_article");
        piece.descriptionPiece = resultSet.getString("description_piece");
        piece.unite = resultSet.getString("unite");
        piece.groupeArticles = resultSet.getString("groupe_articles");
        piece.quantiteConsommeeHistorique = resultSet.getInt("quantite_consommee_historique");
        piece.sousEnsemble = resultSet.getString("sous_ensemble");
        piece.referenceConstructeur = resultSet.getString("reference_constructeur");
        piece.pieceUsure = resultSet.getBoolean("piece_usure");
        piece.stockActuel = resultSet.getInt("stock_actuel");
        piece.stockMinimum = resultSet.getInt("stock_minimum");
        return piece;
    }

    static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ';' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }

    static String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT)
                .replace('’', ' ')
                .replace('\'', ' ')
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    static int parseInt(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value.replace(',', '.').split("\\.")[0].trim());
    }

    static void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    static Long id(Machine machine) {
        return machine == null ? null : machine.id;
    }

    static Long id(Technicien technicien) {
        return technicien == null ? null : technicien.id;
    }

    static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
