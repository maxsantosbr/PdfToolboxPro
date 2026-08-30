package com.pdftoolboxpro.ui;

import com.pdftoolboxpro.ui.panels.CompressPanel;
import com.pdftoolboxpro.ui.panels.ImagePanel;
import com.pdftoolboxpro.ui.panels.MergePanel;
import com.pdftoolboxpro.ui.panels.SplitPanel;
import com.pdftoolboxpro.ui.panels.ZipPanel;
import com.pdftoolboxpro.util.I18n;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Locale;
import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JComboBox<String> langCombo;
    private JLabel statusLabel;
    private MergePanel mergePanel;
    private SplitPanel splitPanel;
    private CompressPanel compressPanel;
    private ZipPanel zipPanel;
    private ImagePanel imagePanel;
    private JMenu helpMenu;
    private JMenuItem aboutItem, updateItem, buyItem;

    public MainFrame() {
        loadSavedLanguage();
        initComponents();
        applyI18n();
        // nao dispara o ActionListener ao posicionar
        langCombo.removeActionListener(langCombo.getActionListeners()[0]);
        String lang = I18n.getCurrentLocale().toString();
        if (lang.startsWith("pt")) {
            langCombo.setSelectedIndex(1);
        } else if (lang.startsWith("de")) {
            langCombo.setSelectedIndex(2);
        } else if (lang.startsWith("es")) {
            langCombo.setSelectedIndex(3);
        } else if (lang.startsWith("mt")) {
            langCombo.setSelectedIndex(4);
        } else {
            langCombo.setSelectedIndex(0);
        }
        langCombo.addActionListener(e -> changeLanguage());
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        try {
            setIconImage(new ImageIcon(getClass().getResource("/com/pdftoolboxpro/resources/icon2.png")).getImage());
        } catch (Exception e) {
        }

        JMenuBar bar = new JMenuBar();
        helpMenu = new JMenu(I18n.get("menu.help"));
        aboutItem = new JMenuItem(I18n.get("help.about"));
        updateItem = new JMenuItem(I18n.get("help.updates"));
        buyItem = new JMenuItem(I18n.get("help.buy"));
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this, I18n.get("about.text"), I18n.get("about.title"), JOptionPane.INFORMATION_MESSAGE));
        updateItem.addActionListener(e -> {
            UpdaterUI updater = new UpdaterUI();
            updater.setLocationRelativeTo(this);
            updater.setVisible(true);
        });
        buyItem.addActionListener(e -> JOptionPane.showMessageDialog(this, I18n.get("buy.text"), I18n.get("buy.title"), JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        helpMenu.add(updateItem);
        helpMenu.add(buyItem);
        bar.add(helpMenu);
        setJMenuBar(bar);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel("Language / Idioma:"));
        langCombo = new JComboBox<>(new String[]{"English", "Portugu\u00EAs (BR)", "Deutsch", "Espa\u00F1ol", "Malti"});
        langCombo.addActionListener(e -> changeLanguage());
        topPanel.add(langCombo);
        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        mergePanel = new MergePanel();
        splitPanel = new SplitPanel();
        compressPanel = new CompressPanel();
        zipPanel = new ZipPanel();
        imagePanel = new ImagePanel();
        tabbedPane.addTab(I18n.get("tab.merge"), mergePanel);
        tabbedPane.addTab(I18n.get("tab.split"), splitPanel);
        tabbedPane.addTab(I18n.get("tab.compress"), compressPanel);
        tabbedPane.addTab(I18n.get("tab.zip"), zipPanel);
        tabbedPane.addTab(I18n.get("tab.image"), imagePanel);
        add(tabbedPane, BorderLayout.CENTER);

        statusLabel = new JLabel(" " + I18n.get("status.ready"));
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void changeLanguage() {
        int idx = langCombo.getSelectedIndex();
        if (idx == 0) {
            I18n.setLocale(Locale.ENGLISH);
        }
        if (idx == 1) {
            I18n.setLocale(new Locale("pt", "BR"));
        }
        if (idx == 2) {
            I18n.setLocale(Locale.GERMAN);
        }
        if (idx == 3) {
            I18n.setLocale(new Locale("es", "ES"));
        }
        if (idx == 4) {
            I18n.setLocale(new Locale("mt", "MT"));
        }
        // salva permanente
        try {
            Path p = Paths.get(System.getProperty("user.home"), ".pdftoolboxpro", "lang.txt");
            Files.createDirectories(p.getParent());
            Files.writeString(p, I18n.getCurrentLocale().toString());
        } catch (Exception e) {
        }
        applyI18n();
        for (Window w : Window.getWindows()) {
            if (w instanceof UpdaterUI) {
                ((UpdaterUI) w).updateTexts();
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyI18n() {
        setTitle(I18n.get("app.title"));
        helpMenu.setText(I18n.get("menu.help"));
        aboutItem.setText(I18n.get("help.about"));
        updateItem.setText(I18n.get("help.updates"));
        buyItem.setText(I18n.get("help.buy"));
        tabbedPane.setTitleAt(0, I18n.get("tab.merge"));
        tabbedPane.setTitleAt(1, I18n.get("tab.split"));
        tabbedPane.setTitleAt(2, I18n.get("tab.compress"));
        tabbedPane.setTitleAt(3, I18n.get("tab.zip"));
        tabbedPane.setTitleAt(4, I18n.get("tab.image"));
        statusLabel.setText(" " + I18n.get("status.ready"));
        mergePanel.updateTexts();
        splitPanel.updateTexts();
        compressPanel.updateTexts();
        zipPanel.updateTexts();
        imagePanel.updateTexts();
    }

    private void loadSavedLanguage() {
        try {
            Path p = Paths.get(System.getProperty("user.home"), ".pdftoolboxpro", "lang.txt");
            if (Files.exists(p)) {
                String s = Files.readString(p).trim();
                if (s.equals("pt_BR")) {
                    I18n.setLocale(new Locale("pt", "BR"));
                } else if (s.equals("de")) {
                    I18n.setLocale(Locale.GERMAN);
                } else if (s.equals("es")) {
                    I18n.setLocale(new Locale("es", "ES"));
                } else if (s.equals("mt_MT") || s.equals("mt")) {
                    I18n.setLocale(new Locale("mt", "MT"));
                } else {
                    I18n.setLocale(Locale.ENGLISH);
                }
            }
        } catch (Exception e) {
        }
    }

}
