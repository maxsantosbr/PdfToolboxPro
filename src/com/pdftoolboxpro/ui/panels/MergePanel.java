package com.pdftoolboxpro.ui.panels;

import com.pdftoolboxpro.core.PdfMerger;
import com.pdftoolboxpro.util.I18n;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import javax.swing.*;

public class MergePanel extends JPanel {
    private JLabel t1, d1, t2, d2, t3, d3;
    private JButton btn;

    public MergePanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(30,20,20,20));
        JPanel cols = new JPanel(new GridBagLayout());
        cols.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy=0; c.fill=GridBagConstraints.BOTH; c.weighty=1; c.anchor=GridBagConstraints.CENTER;
        c.gridx=0; c.weightx=1; cols.add(createColumn("/com/pdftoolboxpro/resources/pdfs.png", "merge.step1.title", "merge.step1.desc"), c);
        c.gridx=1; c.weightx=0; c.fill=GridBagConstraints.VERTICAL; c.insets=new Insets(20,15,20,15); cols.add(new JSeparator(JSeparator.VERTICAL), c);
        c.gridx=2; c.weightx=1; c.fill=GridBagConstraints.BOTH; c.insets=new Insets(0,0,0,0); cols.add(createColumn("/com/pdftoolboxpro/resources/save.png", "merge.step2.title", "merge.step2.desc"), c);
        c.gridx=3; c.weightx=0; c.fill=GridBagConstraints.VERTICAL; c.insets=new Insets(20,15,20,15); cols.add(new JSeparator(JSeparator.VERTICAL), c);
        c.gridx=4; c.weightx=1; c.fill=GridBagConstraints.BOTH; c.insets=new Insets(0,0,0,0); cols.add(createColumn("/com/pdftoolboxpro/resources/success.png", "merge.step3.title", "merge.step3.desc"), c);
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cols);
        btn = new JButton(I18n.get("merge.button"));
        btn.setPreferredSize(new Dimension(420,40));
        btn.addActionListener(e -> onMerge());
        add(centerWrapper, BorderLayout.CENTER);
        add(btn, BorderLayout.SOUTH);
    }

    private JPanel createColumn(String iconPath, String titleKey, String descKey) {
        JPanel col = new JPanel(new BorderLayout(10,10));
        col.setOpaque(false);
        JLabel icon = new JLabel(); icon.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            Image img = new ImageIcon(getClass().getResource(iconPath)).getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            icon.setIcon(new ImageIcon(img));
        } catch(Exception e){}
        JLabel title = new JLabel(I18n.get(titleKey), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        String descText = I18n.get(descKey).replace("\\n", "<br>");
        JLabel desc = new JLabel("<html><center>" + descText + "</center></html>", SwingConstants.CENTER);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if(titleKey.contains("step1")){ t1=title; d1=desc; }
        else if(titleKey.contains("step2")){ t2=title; d2=desc; }
        else { t3=title; d3=desc; }
        JPanel text = new JPanel(new BorderLayout(5,5));
        text.setOpaque(false);
        text.add(title, BorderLayout.NORTH);
        text.add(desc, BorderLayout.CENTER);
        col.add(icon, BorderLayout.NORTH);
        col.add(text, BorderLayout.CENTER);
        return col;
    }

    public void updateTexts(){
        t1.setText(I18n.get("merge.step1.title"));
        d1.setText("<html><center>" + I18n.get("merge.step1.desc").replace("\\n", "<br>") + "</center></html>");
        t2.setText(I18n.get("merge.step2.title"));
        d2.setText("<html><center>" + I18n.get("merge.step2.desc").replace("\\n", "<br>") + "</center></html>");
        t3.setText(I18n.get("merge.step3.title"));
        d3.setText("<html><center>" + I18n.get("merge.step3.desc").replace("\\n", "<br>") + "</center></html>");
        btn.setText(I18n.get("merge.button"));
    }

    private void onMerge(){
        JFileChooser fc=new JFileChooser(); fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF","pdf"));
        if(fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        File[] files=fc.getSelectedFiles(); if(files.length<2){ JOptionPane.showMessageDialog(this,"Select at least 2 PDFs"); return; }
        JFileChooser save=new JFileChooser(); save.setSelectedFile(new File("merged.pdf"));
        if(save.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        File dest=save.getSelectedFile(); if(!dest.getName().toLowerCase().endsWith(".pdf")) dest=new File(dest.getParentFile(), dest.getName()+".pdf");
        try{
            new PdfMerger().merge(Arrays.asList(files), dest);
            String msg = dest.getAbsolutePath() + "\n\n" + I18n.get("dialog.openfolder");
            int opt = JOptionPane.showConfirmDialog(this, msg, I18n.get("merge.step3.title"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if(opt == JOptionPane.YES_OPTION){
                try{ if(Desktop.isDesktopSupported()) Desktop.getDesktop().open(dest.getParentFile()); } catch(Exception ex){}
            }
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }
}