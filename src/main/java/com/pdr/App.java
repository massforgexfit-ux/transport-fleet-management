package com.pdr;

import com.pdr.config.DatabaseInitializer;
import com.pdr.ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {
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
}
