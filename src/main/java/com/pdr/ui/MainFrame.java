package com.pdr.ui;

import com.pdr.controller.AlerteController;
import com.pdr.controller.MachineController;
import com.pdr.controller.PieceController;
import com.pdr.controller.StockController;
import com.pdr.controller.TechnicienController;
import com.pdr.model.AlerteStock;
import com.pdr.model.Machine;
import com.pdr.model.PieceRechange;
import com.pdr.model.Technicien;

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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final PieceController pieceController = new PieceController();
    private final StockController stockController = new StockController();
    private final MachineController machineController = new MachineController();
    private final TechnicienController technicienController = new TechnicienController();
    private final AlerteController alerteController = new AlerteController();

    private final JTabbedPane tabs = new JTabbedPane();
    private final JTable pieceTable = new JTable();
    private final JTable machineTable = new JTable();
    private final JTable technicienTable = new JTable();
    private final JTable alerteTable = new JTable();

    private List<PieceRechange> pieces = new ArrayList<>();
    private List<Machine> machines = new ArrayList<>();
    private List<Technicien> techniciens = new ArrayList<>();
    private List<AlerteStock> alertes = new ArrayList<>();

    public MainFrame() {
        super("Gestion PDR - Maintenance Conditionnement");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setJMenuBar(createMenuBar());
        tabs.addTab("Pièces", createPiecePanel());
        tabs.addTab("Entrées", createStockPanel(true));
        tabs.addTab("Sorties", createStockPanel(false));
        tabs.addTab("Machines", createMachinePanel());
        tabs.addTab("Techniciens", createTechnicienPanel());
        tabs.addTab("Alertes", createAlertePanel());
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        addMenu(menuBar, "Menu Pièces", 0);
        addMenu(menuBar, "Menu Entrées", 1);
        addMenu(menuBar, "Menu Sorties", 2);
        addMenu(menuBar, "Menu Machines", 3);
        addMenu(menuBar, "Menu Techniciens", 4);
        addMenu(menuBar, "Menu Alertes", 5);
        return menuBar;
    }

    private void addMenu(JMenuBar menuBar, String title, int tabIndex) {
        JMenu menu = new JMenu(title);
        JMenuItem open = new JMenuItem("Ouvrir");
        open.addActionListener(event -> tabs.setSelectedIndex(tabIndex));
        menu.add(open);
        menuBar.add(menu);
    }

    private JPanel createPiecePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Rechercher");
        JButton refreshButton = new JButton("Afficher toutes les pièces");
        JButton importButton = new JButton("Importer Excel/CSV");
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(searchField, BorderLayout.CENTER);
        JPanel searchActions = new JPanel();
        searchActions.add(searchButton);
        searchActions.add(refreshButton);
        searchActions.add(importButton);
        top.add(searchActions, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        panel.add(new JScrollPane(pieceTable), BorderLayout.CENTER);
        JPanel actions = new JPanel();
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        panel.add(actions, BorderLayout.SOUTH);

        searchButton.addActionListener(event -> safeRun(() -> loadPieces(searchField.getText())));
        refreshButton.addActionListener(event -> safeRun(() -> loadPieces("")));
        importButton.addActionListener(event -> importPieces());
        addButton.addActionListener(event -> editPiece(null));
        editButton.addActionListener(event -> editPiece(selectedPiece()));
        deleteButton.addActionListener(event -> deleteSelectedPiece());
        return panel;
    }

    private JPanel createStockPanel(boolean entree) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JComboBox<PieceRechange> pieceCombo = new JComboBox<>();
        JComboBox<Technicien> technicienCombo = new JComboBox<>();
        JComboBox<Machine> machineCombo = new JComboBox<>();
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
        JTextArea commentArea = new JTextArea(4, 30);
        JButton submit = new JButton(entree ? "Enregistrer l'entrée" : "Enregistrer la sortie");

        JButton reload = new JButton("Actualiser les listes");
        reload.addActionListener(event -> {
            refreshAll();
            fillCombo(pieceCombo, pieces);
            fillCombo(technicienCombo, techniciens);
            fillCombo(machineCombo, machines);
        });

        fillCombo(pieceCombo, pieces);
        fillCombo(technicienCombo, techniciens);
        fillCombo(machineCombo, machines);

        panel.add(new JLabel("Pièce", SwingConstants.RIGHT));
        panel.add(pieceCombo);
        panel.add(new JLabel("Technicien", SwingConstants.RIGHT));
        panel.add(technicienCombo);
        if (!entree) {
            panel.add(new JLabel("Machine", SwingConstants.RIGHT));
            panel.add(machineCombo);
        }
        panel.add(new JLabel("Quantité", SwingConstants.RIGHT));
        panel.add(quantitySpinner);
        panel.add(new JLabel("Commentaire", SwingConstants.RIGHT));
        panel.add(new JScrollPane(commentArea));
        panel.add(reload);
        panel.add(submit);

        submit.addActionListener(event -> safeRun(() -> {
            PieceRechange piece = (PieceRechange) pieceCombo.getSelectedItem();
            if (piece == null) {
                throw new IllegalArgumentException("Choisir une pièce.");
            }
            Technicien technicien = (Technicien) technicienCombo.getSelectedItem();
            Machine machine = (Machine) machineCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            boolean alertCreated;
            if (entree) {
                alertCreated = stockController.enregistrerEntree(piece.getId(), idOf(technicien), quantity, commentArea.getText());
            } else {
                alertCreated = stockController.enregistrerSortie(piece.getId(), idOf(machine), idOf(technicien), quantity, commentArea.getText());
            }
            refreshAll();
            JOptionPane.showMessageDialog(this, "Mouvement enregistré.");
            if (alertCreated) {
                JOptionPane.showMessageDialog(null, "Attention : stock inférieur au seuil minimum");
            }
        }));
        return panel;
    }

    private JPanel createMachinePanel() {
        JPanel panel = tablePanel(machineTable);
        JButton add = new JButton("Ajouter");
        JButton edit = new JButton("Modifier");
        JButton delete = new JButton("Supprimer");
        JPanel actions = new JPanel();
        actions.add(add);
        actions.add(edit);
        actions.add(delete);
        panel.add(actions, BorderLayout.SOUTH);
        add.addActionListener(event -> editMachine(null));
        edit.addActionListener(event -> editMachine(selectedMachine()));
        delete.addActionListener(event -> deleteSelectedMachine());
        return panel;
    }

    private JPanel createTechnicienPanel() {
        JPanel panel = tablePanel(technicienTable);
        JButton add = new JButton("Ajouter");
        JButton edit = new JButton("Modifier");
        JButton delete = new JButton("Supprimer");
        JPanel actions = new JPanel();
        actions.add(add);
        actions.add(edit);
        actions.add(delete);
        panel.add(actions, BorderLayout.SOUTH);
        add.addActionListener(event -> editTechnicien(null));
        edit.addActionListener(event -> editTechnicien(selectedTechnicien()));
        delete.addActionListener(event -> deleteSelectedTechnicien());
        return panel;
    }

    private JPanel createAlertePanel() {
        JPanel panel = tablePanel(alerteTable);
        JButton refresh = new JButton("Actualiser");
        JButton resolve = new JButton("Marquer comme résolue");
        JPanel actions = new JPanel();
        actions.add(refresh);
        actions.add(resolve);
        panel.add(actions, BorderLayout.SOUTH);
        refresh.addActionListener(event -> safeRun(this::loadAlertes));
        resolve.addActionListener(event -> resolveSelectedAlerte());
        return panel;
    }

    private JPanel tablePanel(JTable table) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void importPieces() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        safeRun(() -> {
            int imported = pieceController.importFile(file.toPath());
            refreshAll();
            JOptionPane.showMessageDialog(this, imported + " pièces importées.");
        });
    }

    private void editPiece(PieceRechange existing) {
        PieceRechange piece = existing == null ? new PieceRechange() : existing;
        JTextField code = new JTextField(nullToEmpty(piece.getCodeArticle()));
        JTextField description = new JTextField(nullToEmpty(piece.getDescriptionPiece()));
        JTextField unite = new JTextField(nullToEmpty(piece.getUnite()));
        JTextField groupe = new JTextField(nullToEmpty(piece.getGroupeArticles()));
        JSpinner historique = new JSpinner(new SpinnerNumberModel(piece.getQuantiteConsommeeHistorique(), 0, 1_000_000, 1));
        JTextField sousEnsemble = new JTextField(nullToEmpty(piece.getSousEnsemble()));
        JTextField reference = new JTextField(nullToEmpty(piece.getReferenceConstructeur()));
        JComboBox<String> usure = new JComboBox<>(new String[]{"Non", "Oui"});
        usure.setSelectedItem(piece.isPieceUsure() ? "Oui" : "Non");
        JSpinner stock = new JSpinner(new SpinnerNumberModel(piece.getStockActuel(), 0, 1_000_000, 1));
        JSpinner minimum = new JSpinner(new SpinnerNumberModel(piece.getStockMinimum(), 0, 1_000_000, 1));

        JPanel form = form(new String[]{"Code article", "Description", "Unité", "Groupe", "Qté consommée", "Sous-ensemble", "Réf. constructeur", "Pièce d'usure", "Stock actuel", "Stock minimum"},
                code, description, unite, groupe, historique, sousEnsemble, reference, usure, stock, minimum);
        if (JOptionPane.showConfirmDialog(this, form, "Pièce", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            safeRun(() -> {
                piece.setCodeArticle(code.getText().trim());
                piece.setDescriptionPiece(description.getText().trim());
                piece.setUnite(unite.getText().trim());
                piece.setGroupeArticles(groupe.getText().trim());
                piece.setQuantiteConsommeeHistorique((Integer) historique.getValue());
                piece.setSousEnsemble(sousEnsemble.getText().trim());
                piece.setReferenceConstructeur(reference.getText().trim());
                piece.setPieceUsure("Oui".equals(usure.getSelectedItem()));
                piece.setStockActuel((Integer) stock.getValue());
                piece.setStockMinimum((Integer) minimum.getValue());
                pieceController.save(piece);
                refreshAll();
            });
        }
    }

    private void editMachine(Machine existing) {
        Machine machine = existing == null ? new Machine() : existing;
        JTextField code = new JTextField(nullToEmpty(machine.getCode()));
        JTextField nom = new JTextField(nullToEmpty(machine.getNom()));
        JTextField emplacement = new JTextField(nullToEmpty(machine.getEmplacement()));
        JTextField description = new JTextField(nullToEmpty(machine.getDescription()));
        JPanel form = form(new String[]{"Code", "Nom", "Emplacement", "Description"}, code, nom, emplacement, description);
        if (JOptionPane.showConfirmDialog(this, form, "Machine", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            safeRun(() -> {
                machine.setCode(code.getText().trim());
                machine.setNom(nom.getText().trim());
                machine.setEmplacement(emplacement.getText().trim());
                machine.setDescription(description.getText().trim());
                machineController.save(machine);
                refreshAll();
            });
        }
    }

    private void editTechnicien(Technicien existing) {
        Technicien technicien = existing == null ? new Technicien() : existing;
        JTextField matricule = new JTextField(nullToEmpty(technicien.getMatricule()));
        JTextField nom = new JTextField(nullToEmpty(technicien.getNom()));
        JTextField prenom = new JTextField(nullToEmpty(technicien.getPrenom()));
        JTextField specialite = new JTextField(nullToEmpty(technicien.getSpecialite()));
        JTextField telephone = new JTextField(nullToEmpty(technicien.getTelephone()));
        JPanel form = form(new String[]{"Matricule", "Nom", "Prénom", "Spécialité", "Téléphone"}, matricule, nom, prenom, specialite, telephone);
        if (JOptionPane.showConfirmDialog(this, form, "Technicien", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            safeRun(() -> {
                technicien.setMatricule(matricule.getText().trim());
                technicien.setNom(nom.getText().trim());
                technicien.setPrenom(prenom.getText().trim());
                technicien.setSpecialite(specialite.getText().trim());
                technicien.setTelephone(telephone.getText().trim());
                technicienController.save(technicien);
                refreshAll();
            });
        }
    }

    private JPanel form(String[] labels, java.awt.Component... fields) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 6, 6));
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i], SwingConstants.RIGHT));
            panel.add(fields[i]);
        }
        return panel;
    }

    private void deleteSelectedPiece() {
        PieceRechange piece = selectedPiece();
        if (piece != null && confirmDelete()) {
            safeRun(() -> {
                pieceController.delete(piece.getId());
                refreshAll();
            });
        }
    }

    private void deleteSelectedMachine() {
        Machine machine = selectedMachine();
        if (machine != null && confirmDelete()) {
            safeRun(() -> {
                machineController.delete(machine.getId());
                refreshAll();
            });
        }
    }

    private void deleteSelectedTechnicien() {
        Technicien technicien = selectedTechnicien();
        if (technicien != null && confirmDelete()) {
            safeRun(() -> {
                technicienController.delete(technicien.getId());
                refreshAll();
            });
        }
    }

    private void resolveSelectedAlerte() {
        int row = alerteTable.getSelectedRow();
        if (row >= 0) {
            AlerteStock alerte = alertes.get(row);
            safeRun(() -> {
                alerteController.resolve(alerte.getId());
                loadAlertes();
            });
        }
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void refreshAll() {
        safeRun(() -> {
            loadPieces("");
            loadMachines();
            loadTechniciens();
            loadAlertes();
        });
    }

    private void loadPieces(String query) throws Exception {
        pieces = pieceController.search(query);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code", "Description", "Unité", "Groupe", "Qté hist.", "Sous-ensemble", "Réf. constructeur", "Usure", "Stock", "Min"}, 0);
        for (PieceRechange piece : pieces) {
            model.addRow(new Object[]{piece.getId(), piece.getCodeArticle(), piece.getDescriptionPiece(), piece.getUnite(),
                    piece.getGroupeArticles(), piece.getQuantiteConsommeeHistorique(), piece.getSousEnsemble(),
                    piece.getReferenceConstructeur(), piece.isPieceUsure() ? "Oui" : "Non", piece.getStockActuel(), piece.getStockMinimum()});
        }
        pieceTable.setModel(model);
    }

    private void loadMachines() throws Exception {
        machines = machineController.findAll();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code", "Nom", "Emplacement", "Description"}, 0);
        for (Machine machine : machines) {
            model.addRow(new Object[]{machine.getId(), machine.getCode(), machine.getNom(), machine.getEmplacement(), machine.getDescription()});
        }
        machineTable.setModel(model);
    }

    private void loadTechniciens() throws Exception {
        techniciens = technicienController.findAll();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Matricule", "Nom", "Prénom", "Spécialité", "Téléphone"}, 0);
        for (Technicien technicien : techniciens) {
            model.addRow(new Object[]{technicien.getId(), technicien.getMatricule(), technicien.getNom(), technicien.getPrenom(), technicien.getSpecialite(), technicien.getTelephone()});
        }
        technicienTable.setModel(model);
    }

    private void loadAlertes() throws Exception {
        alertes = alerteController.findAll();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code article", "Message", "Date", "Résolue"}, 0);
        for (AlerteStock alerte : alertes) {
            model.addRow(new Object[]{alerte.getId(), alerte.getCodeArticle(), alerte.getMessage(), alerte.getDateAlerte(), alerte.isResolue() ? "Oui" : "Non"});
        }
        alerteTable.setModel(model);
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

    private <T> void fillCombo(JComboBox<T> combo, List<T> values) {
        combo.removeAllItems();
        for (T value : values) {
            combo.addItem(value);
        }
    }

    private Long idOf(PieceRechange piece) {
        return piece == null ? null : piece.getId();
    }

    private Long idOf(Machine machine) {
        return machine == null ? null : machine.getId();
    }

    private Long idOf(Technicien technicien) {
        return technicien == null ? null : technicien.getId();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void safeRun(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
