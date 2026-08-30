/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pdftoolboxpro.ui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import com.pdftoolboxpro.util.I18n;

/**
 *
 * @author Maxwell
 */
public class UpdaterUI extends javax.swing.JFrame {

    // Versão atual do seu programa (ex: "1.0", "1.1", "2.0")
    public static final String VERSAO_ATUAL = "1.2.2";

    // https://raw.githubusercontent.com/SEU_USUARIO/SEU_REPO/main/version.txt
    public static final String URL_VERSAO = "https://raw.githubusercontent.com/maxsantosbr/PdfToolboxPro-releases/main/version.txt";

    // https://github.com/SEU_USUARIO/SEU_REPO/releases/latest/download/MeuPrograma.jar
    private static final String URL_DOWNLOAD_JAR = "https://github.com/maxsantosbr/PdfToolboxPro-releases/releases/latest/download/PdfToolboxPro-Setup.exe";

    // Nome do arquivo jar que será baixado
    private static final String NOME_JAR = "PdfToolboxPro-Setup.exe";

    /**
     * Creates new form UpdaterUI
     */
    public UpdaterUI() {
        initComponents();
        updateTexts();
        lblVersao.setText(VERSAO_ATUAL);
        lblStatus.setVisible(false);
    }

    public void updateTexts() {
        setTitle(I18n.get("updater.title"));
        jLabel1.setText(I18n.get("updater.title")); // VERIFICADOR
        jLabel2.setText(I18n.get("updater.version")); // Versão instalada:
        btnVerificar.setText(I18n.get("updater.check"));
        jButton2.setText(I18n.get("updater.close"));
        lblStatus.setText(I18n.get("updater.status"));
    }//updateTexts

    //Verifica se existe a versão mais nova
    private void verificarAtualizacao() {
        btnVerificar.setEnabled(false);
        lblStatus.setVisible(true);
//        lblStatus.setText("Mantenha o software aberto. Estamos verificando...");
        lblStatus.setText(I18n.get("updater.checking"));
        lblStatus.setForeground(Color.BLUE);

        // Usa SwingWorker para não travar a interface
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return lerVersaoRemota();
            }

            @Override
            protected void done() {
                try {
                    String versaoRemota = get();
                    compararVersoes(versaoRemota);
                } catch (InterruptedException | ExecutionException ex) {
                    lblStatus.setText("Erro ao verificar: " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                    btnVerificar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//verificarAtualizacao

    //Ler arquivo version.txt
    private String lerVersaoRemota() throws Exception {
        URL url = new URL(URL_VERSAO);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String linha = reader.readLine();
            if (linha == null || linha.trim().isEmpty()) {
//                throw new IOException("version.txt está vazio ou inacessível.");
                throw new IOException(I18n.get("updater.error.version"));
            }
            return linha.trim();
        }
    }//lerVersaoRemota

    //Compara versão local com versão remota
    private void compararVersoes(String versaoRemota) {
        if (versaoRemota == null || versaoRemota.isEmpty()) {
            lblStatus.setVisible(true);
//            lblStatus.setText("Não foi possível obter a versão remota.");
            lblStatus.setText(I18n.get("updater.error.remote"));
            lblStatus.setForeground(Color.RED);
            btnVerificar.setEnabled(true);
            return;
        }
        lblStatus.setVisible(true);
//        lblStatus.setText("Versão disponível no servidor: " + versaoRemota);
        lblStatus.setText(I18n.get("updater.version.available") + versaoRemota);

        // Compara como números (ex: 1.0 < 1.1 < 2.0)
        if (versaoMaiorQue(versaoRemota, VERSAO_ATUAL)) {
            lblStatus.setVisible(true);
            // Há atualização disponível!
            lblStatus.setForeground(new Color(0, 128, 0)); // verde
//            int resposta = JOptionPane.showConfirmDialog(this,
//                    "Nova versão disponível: " + versaoRemota + "\n"
//                    + "Deseja baixar e instalar agora?",
//                    "ATUALIZAÇÃO DISPONÍVEL",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.QUESTION_MESSAGE);

            int resposta = JOptionPane.showConfirmDialog(this, I18n.get("updater.new.version") + versaoRemota + "\n" + I18n.get("updater.want.download"), I18n.get("updater.title.available"), JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (resposta == JOptionPane.YES_OPTION) {
                baixarAtualizacao();
            } else {
                btnVerificar.setEnabled(true);
            }
        } else {
            lblStatus.setVisible(true);
//            lblStatus.setText("Você já tem a versão mais recente! (" + VERSAO_ATUAL + ")");
            lblStatus.setText(I18n.get("updater.have.latest") + VERSAO_ATUAL + ")");
            lblStatus.setForeground(new Color(0, 128, 0));
            btnVerificar.setEnabled(true);
        }
    }//compararVersoes

    //Verifica se a versão A é maior que a versão B
    private boolean versaoMaiorQue(String versaoA, String versaoB) {
        String[] partsA = versaoA.split("\\.");
        String[] partsB = versaoB.split("\\.");
        int maxLen = Math.max(partsA.length, partsB.length);

        for (int i = 0; i < maxLen; i++) {
            int a = (i < partsA.length) ? Integer.parseInt(partsA[i]) : 0;
            int b = (i < partsB.length) ? Integer.parseInt(partsB[i]) : 0;
            if (a > b) {
                return true;
            }
            if (a < b) {
                return false;
            }
        }
        return false;
    }//versaoMaiorQue

    //Baixa o instalador novo em uma pasta temporária
    private void baixarAtualizacao() {
        lblStatus.setVisible(true);
//        lblStatus.setText("Baixando atualização...");
lblStatus.setText(I18n.get("updater.downloading"));
        barraProgresso.setValue(0);
        barraProgresso.setString("0%");
        btnVerificar.setEnabled(false);

        SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                URL url = new URL(URL_DOWNLOAD_JAR);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int tamanhoTotal = conn.getContentLength();

                File arquivoDestino = new File(System.getProperty("java.io.tmpdir"), NOME_JAR);

                try (InputStream in = conn.getInputStream();
                        FileOutputStream out = new FileOutputStream(arquivoDestino)) {

                    byte[] buffer = new byte[4096];
                    int bytesLidos;
                    long totalBaixado = 0;

                    while ((bytesLidos = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesLidos);
                        totalBaixado += bytesLidos;

                        if (tamanhoTotal > 0) {
                            int progresso = (int) ((totalBaixado * 100) / tamanhoTotal);
                            publish(progresso);
                        }
                    }
                }
                return true;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int ultimo = chunks.get(chunks.size() - 1);
                barraProgresso.setValue(ultimo);
                barraProgresso.setString(ultimo + "%");
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        instalarAtualizacao();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    lblStatus.setVisible(true);
                    lblStatus.setText("Erro no download: " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                    btnVerificar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }//baixarAtualizacao

    //Abre o instalado baixado e fecha o programa atual
    private void instalarAtualizacao() {
        try {
            File instalador = new File(System.getProperty("java.io.tmpdir"), NOME_JAR);

            JOptionPane.showMessageDialog(this,
                    "ATUALIZAÇÃO BAIXADA COM SUCESSO!\n"
                    + "O INSTALADOR SERÁ ABERTO AGORA.\n"
                    + "SIGA AS INSTRUÇÕES NA TELA PARA CONCLUIR A ATUALIZAÇÃO.",
                    "PRONTO",
                    JOptionPane.INFORMATION_MESSAGE);

            //A linha de código abaixo evita travar durante a atualização.
            new ProcessBuilder(instalador.getAbsolutePath()).start();

            System.exit(0);

        } catch (HeadlessException | IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "ERRO AO ABRIR O INSTALADOR: " + ex.getMessage()
                    + "\nO ARQUIVO FOI BAIXADO EM: " + System.getProperty("java.io.tmpdir") + NOME_JAR,
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            lblStatus.setVisible(true);
            lblStatus.setForeground(Color.RED);
            btnVerificar.setEnabled(true);
        }
    }//instalarAtualizacao

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelUpdateUI = new javax.swing.JPanel();
        barraProgresso = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblVersao = new javax.swing.JLabel();
        btnVerificar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.OverlayLayout(getContentPane()));

        panelUpdateUI.setBackground(new java.awt.Color(255, 255, 255));
        panelUpdateUI.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        barraProgresso.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        barraProgresso.setForeground(new java.awt.Color(0, 51, 255));
        barraProgresso.setStringPainted(true);
        panelUpdateUI.add(barraProgresso, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 380, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("VERIFICADOR DE ATUALIZAÇÕES");
        panelUpdateUI.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 21, 380, -1));

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Status");
        panelUpdateUI.add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 380, 15));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Versão instalada:");
        panelUpdateUI.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 55, 220, -1));

        lblVersao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblVersao.setText("número da versão");
        panelUpdateUI.add(lblVersao, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 55, 90, -1));

        btnVerificar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnVerificar.setText("Verificar");
        btnVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarActionPerformed(evt);
            }
        });
        panelUpdateUI.add(btnVerificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 156, 215, -1));

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton2.setText("Fechar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        panelUpdateUI.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(243, 156, 147, -1));

        getContentPane().add(panelUpdateUI);

        setBounds(0, 0, 414, 249);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerificarActionPerformed
        verificarAtualizacao();
    }//GEN-LAST:event_btnVerificarActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UpdaterUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdaterUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraProgresso;
    private javax.swing.JButton btnVerificar;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblVersao;
    private javax.swing.JPanel panelUpdateUI;
    // End of variables declaration//GEN-END:variables
}
