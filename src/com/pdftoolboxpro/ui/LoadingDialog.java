package com.pdftoolboxpro.ui;

import java.awt.*;
import javax.swing.*;

public class LoadingDialog extends JDialog {
    private final JProgressBar bar;
    private final JLabel label;

    public LoadingDialog(Window parent, String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(15, 15));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        label = new JLabel(title, SwingConstants.CENTER);
        bar = new JProgressBar(0, 100);
        bar.setValue(0);
        bar.setStringPainted(true);
        bar.setString("0%");

        add(label, BorderLayout.CENTER);
        add(bar, BorderLayout.SOUTH);
        setSize(380, 130);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public void setProgress(int percent, String text) {
        SwingUtilities.invokeLater(() -> {
            bar.setValue(percent);
            bar.setString(percent + "%");
            if (text != null) label.setText(text);
        });
    }

    public void setProgress(int percent) {
        setProgress(percent, null);
    }
}
