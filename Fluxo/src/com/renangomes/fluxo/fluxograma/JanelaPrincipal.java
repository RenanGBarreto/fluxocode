/**
 * Fluxo: Uma Ferramenta Visual para Auxílio ao Ensino de Programação
 * Copyright (C) 2012 Renan Gomes (email@renangomes.com), Pedro Dantas (tigreped@gmail.com)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.renangomes.fluxo.fluxograma;

import com.mxgraph.swing.mxGraphComponent;
import com.renangomes.fluxo.analiselexica.AnalisadorLexico;
import com.renangomes.fluxo.analisesintatica.AnalisadorSintatico;
import com.renangomes.fluxo.utils.Linha;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.filechooser.FileFilter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author RenanGomes
 */
public class JanelaPrincipal extends javax.swing.JFrame {

    int zoom = 0;
    TextLineNumber tln;
    ColortPane textPane;
    private HashMap<String, Fluxograma> fluxogramas = new HashMap<String, Fluxograma>();
    static JanelaPrincipal janela;
    mxGraphComponent comp;
    Thread atualizadora;
    private File arquivo = null;
    private String textoAntigo = "";

    /** Creates new form JanelaPrincipal */
    public JanelaPrincipal() {
        initComponents();
        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/import-export-icon32.png")).getImage());

        textPane = new ColortPane();


        JScrollPane scrollPane = new JScrollPane(textPane);
        tln = new TextLineNumber(textPane);
        textPane.addKeyListener(new java.awt.event.KeyAdapter() {

            String ultimo = textPane.getText();

            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {

                //   if ( ! (evt.getKeyCode() ==  evt.VK_CONTROL )  ) {
                validarcodigo();
                //  }

            }
        });
        textPane.setFont(new java.awt.Font("Verdana", 0, 14));
        scrollPane.setRowHeaderView(tln);
        pnel.add(scrollPane);
        this.setExtendedState(JanelaPrincipal.MAXIMIZED_BOTH);
        this.validate();


        carregaModeloBasico();
        validarcodigo();
        this.setLocationRelativeTo(null);



        atualizadora = new Thread("T-Atualizadora") {

            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        lbData.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                        lbHora.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        if (arquivo != null) {
                            lbArquivo.setText(arquivo.getAbsolutePath());
                        } else {
                            lbArquivo.setText("(Nenhum)");

                        }

                        if (isArquivoAlterado()) {
                            lbArquivo.setText(lbArquivo.getText() + "*");
                        }
                    } catch (Exception e) {
                    }
                    try {
                        sleep(900);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };

        atualizadora.start();

    }

    static void alertarClique(String string) {
        if (janela != null) {

            try {
                for (int i = 0; i < janela.cbFluxos.getItemCount(); i++) {
                    if (("Procedimento: " + string).startsWith(((String) janela.cbFluxos.getItemAt(i)))) {
                        janela.cbFluxos.setSelectedIndex(i);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void alteraLAF() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex2) {
                try {
                    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            javax.swing.UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (ClassNotFoundException ex3) {
                    java.util.logging.Logger.getLogger(JanelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex3) {
                    java.util.logging.Logger.getLogger(JanelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex3) {
                    java.util.logging.Logger.getLogger(JanelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex3) {
                    java.util.logging.Logger.getLogger(JanelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        pnel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        painelFluxo = new javax.swing.JPanel();
        cbFluxos = new javax.swing.JComboBox();
        btzoomout = new javax.swing.JButton();
        btzoomin = new javax.swing.JButton();
        bExportar = new javax.swing.JButton();
        bLegenda = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taSaida = new javax.swing.JTextArea();
        statusBar = new javax.swing.JPanel();
        lbArquivo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbData = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        mArquivo = new javax.swing.JMenu();
        mNovo = new javax.swing.JMenuItem();
        mAbrir = new javax.swing.JMenuItem();
        mSalvar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mFechar = new javax.swing.JMenuItem();
        mFluxograma = new javax.swing.JMenu();
        mSalvarFluxograma = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mZommIn = new javax.swing.JMenuItem();
        mZoomOut = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mLegenda = new javax.swing.JMenuItem();
        mConfiguracoes = new javax.swing.JMenu();
        rbSemantico = new javax.swing.JRadioButtonMenuItem();
        mExemplos = new javax.swing.JMenu();
        mModeloBasico = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mexemploComandoSimples = new javax.swing.JMenuItem();
        mExemploIf = new javax.swing.JMenuItem();
        mExemploWhile = new javax.swing.JMenuItem();
        mExemploProcedimento = new javax.swing.JMenuItem();
        mAjuda = new javax.swing.JMenu();
        mSobre = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Fluxo: Uma Ferramenta Visual para Auxílio ao Ensino de Programação");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                fechando(evt);
            }
        });

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setResizeWeight(0.3);
        jSplitPane1.setAutoscrolls(true);
        jSplitPane1.setLastDividerLocation(300);
        jSplitPane1.setOneTouchExpandable(true);

        pnel.setBackground(new java.awt.Color(255, 255, 255));
        pnel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnel, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnel, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        painelFluxo.setBackground(new java.awt.Color(250, 250, 250));
        painelFluxo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(109, 109, 109)));
        painelFluxo.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                painelFluxoMouseWheelMoved(evt);
            }
        });
        painelFluxo.setLayout(new java.awt.BorderLayout());

        cbFluxos.setFont(new java.awt.Font("Verdana", 0, 16));
        cbFluxos.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFluxosItemStateChanged(evt);
            }
        });
        cbFluxos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFluxosActionPerformed(evt);
            }
        });

        btzoomout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/zoomout.png"))); // NOI18N
        btzoomout.setToolTipText("Zoom Out");
        btzoomout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btzoomoutActionPerformed(evt);
            }
        });

        btzoomin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/zoomin.png"))); // NOI18N
        btzoomin.setToolTipText("Zoom In");
        btzoomin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btzoominActionPerformed(evt);
            }
        });

        bExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/export-icon24.png"))); // NOI18N
        bExportar.setToolTipText("Exportar Gráfico");
        bExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bExportarActionPerformed(evt);
            }
        });

        bLegenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Actions-view-pim-notes-icon24.png"))); // NOI18N
        bLegenda.setToolTipText("Legenda");
        bLegenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLegendaActionPerformed(evt);
            }
        });

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(painelFluxo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(bExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bLegenda, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btzoomout, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btzoomin, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbFluxos, 0, 425, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bExportar, btzoomin, btzoomout});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbFluxos)
                    .addComponent(btzoomin, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btzoomout, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(bLegenda, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(bExportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(painelFluxo, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {bExportar, bLegenda, btzoomin, btzoomout, jSeparator4});

        jSplitPane1.setRightComponent(jPanel3);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(109, 109, 109)));

        taSaida.setBackground(new java.awt.Color(204, 204, 204));
        taSaida.setColumns(20);
        taSaida.setEditable(false);
        taSaida.setFont(new java.awt.Font("Verdana", 0, 14));
        taSaida.setLineWrap(true);
        taSaida.setRows(3);
        taSaida.setWrapStyleWord(true);
        taSaida.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jScrollPane1.setViewportView(taSaida);

        statusBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(109, 109, 109)));
        statusBar.setPreferredSize(new java.awt.Dimension(872, 25));

        lbArquivo.setText("(Nenhum)");

        jLabel2.setText("Data:");

        jLabel5.setText("Hora:");

        jLabel3.setText("Arquivo:");

        javax.swing.GroupLayout statusBarLayout = new javax.swing.GroupLayout(statusBar);
        statusBar.setLayout(statusBarLayout);
        statusBarLayout.setHorizontalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lbArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lbData, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel5)
                .addGap(0, 0, 0)
                .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        statusBarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel5});

        statusBarLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbData, lbHora});

        statusBarLayout.setVerticalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                .addComponent(lbArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lbData, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel3))
        );

        statusBarLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel5, lbArquivo, lbData, lbHora});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        mArquivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/attachment16.png"))); // NOI18N
        mArquivo.setText("Arquivo");

        mNovo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Files-New-File-icon16.png"))); // NOI18N
        mNovo.setText("Novo Código Fonte");
        mNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mNovoActionPerformed(evt);
            }
        });
        mArquivo.add(mNovo);

        mAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Open-icon16.png"))); // NOI18N
        mAbrir.setText("Abrir Código Fonte");
        mAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mAbrirActionPerformed(evt);
            }
        });
        mArquivo.add(mAbrir);

        mSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/document-save-icon16.png"))); // NOI18N
        mSalvar.setText("Salvar Código Fonte");
        mSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSalvarActionPerformed(evt);
            }
        });
        mArquivo.add(mSalvar);
        mArquivo.add(jSeparator3);

        mFechar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mFechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Delete-icon16.png"))); // NOI18N
        mFechar.setText("Fechar");
        mFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFecharActionPerformed(evt);
            }
        });
        mArquivo.add(mFechar);

        menu.add(mArquivo);

        mFluxograma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/import-export-icon16.png"))); // NOI18N
        mFluxograma.setText("Fluxograma");

        mSalvarFluxograma.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mSalvarFluxograma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/export-icon16.png"))); // NOI18N
        mSalvarFluxograma.setText("Exportar Fluxograma");
        mSalvarFluxograma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSalvarFluxogramaActionPerformed(evt);
            }
        });
        mFluxograma.add(mSalvarFluxograma);
        mFluxograma.add(jSeparator2);

        mZommIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_MASK));
        mZommIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/zoomin16.png"))); // NOI18N
        mZommIn.setText("Zoom In");
        mZommIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mZommInActionPerformed(evt);
            }
        });
        mFluxograma.add(mZommIn);

        mZoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_MASK));
        mZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/zoomout16.png"))); // NOI18N
        mZoomOut.setText("Zoom Out");
        mZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mZoomOutActionPerformed(evt);
            }
        });
        mFluxograma.add(mZoomOut);
        mFluxograma.add(jSeparator1);

        mLegenda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mLegenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Actions-view-pim-notes-icon16.png"))); // NOI18N
        mLegenda.setText("Legenda...");
        mLegenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mLegendaActionPerformed(evt);
            }
        });
        mFluxograma.add(mLegenda);

        menu.add(mFluxograma);

        mConfiguracoes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Categories-applications-system-icon16.png"))); // NOI18N
        mConfiguracoes.setText("Configurações");

        rbSemantico.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        rbSemantico.setSelected(true);
        rbSemantico.setText("Verificar erros semânticos");
        rbSemantico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSemanticoActionPerformed(evt);
            }
        });
        mConfiguracoes.add(rbSemantico);

        menu.add(mConfiguracoes);

        mExemplos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Document-Flow-Chart-icon16.png"))); // NOI18N
        mExemplos.setText("Exemplos");

        mModeloBasico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Document-Flow-Chart-icon16.png"))); // NOI18N
        mModeloBasico.setText("Modelo Básico");
        mModeloBasico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mModeloBasicoActionPerformed(evt);
            }
        });
        mExemplos.add(mModeloBasico);
        mExemplos.add(jSeparator5);

        mexemploComandoSimples.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Document-Flow-Chart-icon16.png"))); // NOI18N
        mexemploComandoSimples.setText("Exemplo: Comandos Simples");
        mexemploComandoSimples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mexemploComandoSimplesActionPerformed(evt);
            }
        });
        mExemplos.add(mexemploComandoSimples);

        mExemploIf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Document-Flow-Chart-icon16.png"))); // NOI18N
        mExemploIf.setText("Exemplo: IF");
        mExemploIf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExemploIfActionPerformed(evt);
            }
        });
        mExemplos.add(mExemploIf);

        mExemploWhile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Document-Flow-Chart-icon16.png"))); // NOI18N
        mExemploWhile.setText("Exemplo: While");
        mExemploWhile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExemploWhileActionPerformed(evt);
            }
        });
        mExemplos.add(mExemploWhile);

        mExemploProcedimento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Document-Flow-Chart-icon16.png"))); // NOI18N
        mExemploProcedimento.setText("Exemplo: Procedimento");
        mExemploProcedimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExemploProcedimentoActionPerformed(evt);
            }
        });
        mExemplos.add(mExemploProcedimento);

        menu.add(mExemplos);

        mAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/Freelance-icon16.png"))); // NOI18N
        mAjuda.setText("Ajuda");
        mAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mAjudaActionPerformed(evt);
            }
        });

        mSobre.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mSobre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/renangomes/fluxo/fluxograma/imgs/id_card16.png"))); // NOI18N
        mSobre.setText("Sobre");
        mSobre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSobreActionPerformed(evt);
            }
        });
        mAjuda.add(mSobre);

        menu.add(mAjuda);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbFluxosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFluxosActionPerformed
    }//GEN-LAST:event_cbFluxosActionPerformed

    private void cbFluxosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFluxosItemStateChanged
        if (fluxogramas != null) {
            painelFluxo.removeAll();
            painelFluxo.invalidate();
            validate();
            repaint();


            comp = fluxogramas.get((String) cbFluxos.getSelectedItem()).gerar();
            comp.setBorder(null);
            atualizaZoom();
            painelFluxo.add(comp);
        }
        validate();
    }//GEN-LAST:event_cbFluxosItemStateChanged

    private void btzoomoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btzoomoutActionPerformed
        if (comp != null) {
            comp.zoomOut();
            zoom--;
        }
    }//GEN-LAST:event_btzoomoutActionPerformed

    private void btzoominActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btzoominActionPerformed
        if (comp != null) {
            comp.zoomIn();
            zoom++;
        }
    }//GEN-LAST:event_btzoominActionPerformed

    private void painelFluxoMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_painelFluxoMouseWheelMoved
    }//GEN-LAST:event_painelFluxoMouseWheelMoved

    private void mZommInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mZommInActionPerformed
        if (comp != null) {

            comp.zoomIn();
            zoom++;
        }
    }//GEN-LAST:event_mZommInActionPerformed

    private void mZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mZoomOutActionPerformed
        if (comp != null) {

            comp.zoomOut();
            zoom--;
        }
    }//GEN-LAST:event_mZoomOutActionPerformed

    private void mSalvarFluxogramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSalvarFluxogramaActionPerformed
        exportarGrafico();
    }//GEN-LAST:event_mSalvarFluxogramaActionPerformed

    private void mLegendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mLegendaActionPerformed
        abrirLegenda();
    }//GEN-LAST:event_mLegendaActionPerformed

    private void rbSemanticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSemanticoActionPerformed
        validarcodigo();
    }//GEN-LAST:event_rbSemanticoActionPerformed

    private void mFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFecharActionPerformed
        fecharPrograma();
    }//GEN-LAST:event_mFecharActionPerformed

    private void fechando(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_fechando
        fecharPrograma();
    }//GEN-LAST:event_fechando

    private void mSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_mSalvarActionPerformed

    private void mModeloBasicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mModeloBasicoActionPerformed
        carregaModeloBasico();
    }//GEN-LAST:event_mModeloBasicoActionPerformed

    private void mExemploIfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExemploIfActionPerformed
        carregaModeloIF();
    }//GEN-LAST:event_mExemploIfActionPerformed

    private void mexemploComandoSimplesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mexemploComandoSimplesActionPerformed
        carregaModeloComandoSimples();
    }//GEN-LAST:event_mexemploComandoSimplesActionPerformed

    private void mExemploWhileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExemploWhileActionPerformed
        carregaModeloWhile();
    }//GEN-LAST:event_mExemploWhileActionPerformed

    private void mExemploProcedimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExemploProcedimentoActionPerformed
        carregaModeloProcedimento();

    }//GEN-LAST:event_mExemploProcedimentoActionPerformed

    private void mAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAbrirActionPerformed
        abrir();
    }//GEN-LAST:event_mAbrirActionPerformed

    private void mNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mNovoActionPerformed
        novo(false);
    }//GEN-LAST:event_mNovoActionPerformed

    private void mAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAjudaActionPerformed
    }//GEN-LAST:event_mAjudaActionPerformed

    private void mSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSobreActionPerformed
        abrirSobre();
    }//GEN-LAST:event_mSobreActionPerformed

    private void bLegendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLegendaActionPerformed
        abrirLegenda();
    }//GEN-LAST:event_bLegendaActionPerformed

    private void bExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bExportarActionPerformed
        exportarGrafico();
    }//GEN-LAST:event_bExportarActionPerformed

    public void validarcodigo() {
        boolean erro = false;
        int linhaDoerro = -1;




        List<Linha> tabela;

        AnalisadorSintatico as = null;
        AnalisadorLexico al = null;
        try {
            al = new AnalisadorLexico();
            tabela = al.criarTabela(textPane.getText() + " ");
            highlight(textPane, tabela);
            try {
                if (tabela != null && tabela.size() < 1) {
                    return;
                }
                as = new AnalisadorSintatico(tabela, !rbSemantico.isSelected());
                as.analisar();
            } catch (RuntimeException e) {
                //e.printStackTrace();
                taSaida.setText(as.ultimoErro);
                taSaida.setBackground(new Color(255, 100, 100));
                erro = true;
                linhaDoerro = as.ultimalinhaerro;
            } catch (Exception ne) {
                ne.printStackTrace();
                taSaida.setText("Erro desconhecido: " + ne.getMessage());
                taSaida.setBackground(Color.GRAY);
                erro = true;
                linhaDoerro = as.ultimalinhaerro;
            }
        } catch (RuntimeException e) {
            //e.printStackTrace();
            taSaida.setText(al.ultimoErro);
            taSaida.setBackground(new Color(255, 100, 100));
            erro = true;
            linhaDoerro = al.ultimalinhaerro;
        } catch (Exception ne) {
            ne.printStackTrace();
            taSaida.setText("Erro desconhecido: " + ne.getMessage());
            taSaida.setBackground(Color.GRAY);
            erro = true;
            linhaDoerro = al.ultimalinhaerro;
        }
        painelFluxo.removeAll();
        painelFluxo.invalidate();
        validate();
        repaint();
        String selecionado = null;
        if (cbFluxos.getSelectedIndex() > 0 && !((String) cbFluxos.getSelectedItem()).trim().isEmpty()) {
            selecionado = (String) cbFluxos.getSelectedItem();
        }


        atualizaCB(null);


        try {

            fluxogramas.clear();
            fluxogramas.putAll(as.fluxogramas);

            if (fluxogramas != null && !erro) {
                atualizaCB(fluxogramas.keySet().toArray(new String[0]));
                if (selecionado != null) {
                    cbFluxos.setSelectedItem(selecionado);
                }

                comp = fluxogramas.get((String) cbFluxos.getSelectedItem()).gerar();


                comp.setBorder(null);
                atualizaZoom();
                painelFluxo.add(comp);
            }


        } catch (Exception e) {
            //e.printStackTrace();
        }

        if (!erro) {

            if (!rbSemantico.isSelected()) {
                taSaida.setText("Código compilado com sucesso!\n(Verificação de erros semânticos desativada)");
            } else {
                taSaida.setText("Código compilado com sucesso!");
            }
            taSaida.setBackground(new Color(100, 255, 100));
            removeHighlights(textPane);
        } else {
            destacarLinha(linhaDoerro);
        }

        validate();
        repaint();

    }

    private void atualizaCB(String[] fl) {
        if (fl == null) {
            cbFluxos.setModel(new DefaultComboBoxModel());
            cbFluxos.setEnabled(false);
            return;
        }
        //Arrays.sort(fl);
        cbFluxos.setModel(new DefaultComboBoxModel(fl));
        cbFluxos.setEnabled(true);
        cbFluxos.setSelectedIndex(fl.length - 1);
    }

    public static void main(String args[]) {
        alteraLAF();
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JanelaPrincipal j = new JanelaPrincipal();
                j.setVisible(true);
                JanelaPrincipal.janela = j;

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bExportar;
    private javax.swing.JButton bLegenda;
    private javax.swing.JButton btzoomin;
    private javax.swing.JButton btzoomout;
    private javax.swing.JComboBox cbFluxos;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbArquivo;
    private javax.swing.JLabel lbData;
    private javax.swing.JLabel lbHora;
    private javax.swing.JMenuItem mAbrir;
    private javax.swing.JMenu mAjuda;
    private javax.swing.JMenu mArquivo;
    private javax.swing.JMenu mConfiguracoes;
    private javax.swing.JMenuItem mExemploIf;
    private javax.swing.JMenuItem mExemploProcedimento;
    private javax.swing.JMenuItem mExemploWhile;
    private javax.swing.JMenu mExemplos;
    private javax.swing.JMenuItem mFechar;
    private javax.swing.JMenu mFluxograma;
    private javax.swing.JMenuItem mLegenda;
    private javax.swing.JMenuItem mModeloBasico;
    private javax.swing.JMenuItem mNovo;
    private javax.swing.JMenuItem mSalvar;
    private javax.swing.JMenuItem mSalvarFluxograma;
    private javax.swing.JMenuItem mSobre;
    private javax.swing.JMenuItem mZommIn;
    private javax.swing.JMenuItem mZoomOut;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem mexemploComandoSimples;
    private javax.swing.JPanel painelFluxo;
    private javax.swing.JPanel pnel;
    private javax.swing.JRadioButtonMenuItem rbSemantico;
    private javax.swing.JPanel statusBar;
    private javax.swing.JTextArea taSaida;
    // End of variables declaration//GEN-END:variables

// Creates highlights around all occurrences of pattern in textComp
    public void highlight(ColortPane textComp, List<Linha> l) {

        StyledDocument document = textComp.getStyledDocument();



        SimpleAttributeSet variavel = new SimpleAttributeSet();
        StyleConstants.setForeground(variavel, new Color(0, 88, 38)); //Verde escuro
        StyleConstants.setBold(variavel, true);
        StyleConstants.setItalic(variavel, false);


        SimpleAttributeSet estiloNumero = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloNumero, new Color(74, 0, 115));//violeta escuro
        StyleConstants.setBold(estiloNumero, true);
        StyleConstants.setItalic(estiloNumero, false);

        SimpleAttributeSet palavrachave = new SimpleAttributeSet();
        StyleConstants.setForeground(palavrachave, new Color(0, 0, 250)); //Azul
        StyleConstants.setBold(palavrachave, true);
        StyleConstants.setItalic(palavrachave, false);

        SimpleAttributeSet estiloOperador = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloOperador, new Color(200, 0, 0)); //vermelho  
        StyleConstants.setBold(estiloOperador, true);
        StyleConstants.setItalic(estiloOperador, true);

        SimpleAttributeSet estiloNada = new SimpleAttributeSet();
        StyleConstants.setForeground(estiloNada, new Color(150, 150, 150));
        StyleConstants.setBold(estiloNada, false);
        StyleConstants.setItalic(estiloNada, true);

        SimpleAttributeSet delimitador = new SimpleAttributeSet();
        StyleConstants.setForeground(delimitador, new Color(0, 0, 0));
        StyleConstants.setBold(delimitador, false);
        StyleConstants.setItalic(delimitador, true);


        document.setCharacterAttributes(0, document.getLength(), estiloNada, true);

        for (Linha ln : l) {
            if (ln.tipo == ln.tipo.PALAVRARESERVADA) {
                document.setCharacterAttributes(ln.caractereInicial - ln.token.length(), ln.token.length(), palavrachave, true);
            } else {
                if (ln.tipo == ln.tipo.IDENTIFICADOR) {
                    document.setCharacterAttributes(ln.caractereInicial - ln.token.length(), ln.token.length(), variavel, true);
                } else {
                    if (ln.tipo == ln.tipo.NUMEROREAL || ln.tipo == ln.tipo.NUMEROITEIRO) {
                        document.setCharacterAttributes(ln.caractereInicial - ln.token.length(), ln.token.length(), estiloNumero, true);
                    } else {
                        if (ln.tipo == ln.tipo.OPERADORADITIVO || ln.tipo == ln.tipo.OPERADORMUTIPLICATIVO || ln.tipo == ln.tipo.OPERADORRELACIONAL || ln.tipo == ln.tipo.COMANDODEATRIBUICAO) {
                            document.setCharacterAttributes(ln.caractereInicial - ln.token.length(), ln.token.length(), estiloOperador, true);
                        } else {
                            document.setCharacterAttributes(ln.caractereInicial - ln.token.length(), ln.token.length(), delimitador, true);

                        }
                    }
                }
            }
        }

    }

// Removes only our private highlights
    public void removeHighlights(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (int i = 0; i < hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }

    private void destacarLinha(int linhaDoerro) {

        String t = textPane.getText();
        int contalinha = 1;
        int incio = 0;
        int fim = 0;
        boolean iniciou = false;
        for (int i = 0; i < t.length(); i++) {
            if (contalinha == linhaDoerro && !iniciou) {
                incio = i;
                iniciou = true;
            }
            if (iniciou && contalinha != linhaDoerro) {
                fim = i;
                break;
            }

            if (t.charAt(i) == '\n') {
                contalinha++;
            }
        }

        if (fim == 0) {
            fim = Math.max(0, t.length());
        }

        highlight(textPane, incio - contalinha + 1, fim - contalinha, new MyHighlightPainter(new Color(255, 200, 200)));
    }

    public void highlight(JTextComponent textComp, int start, int end, Highlighter.HighlightPainter z) {
        // First remove all old highlights
        removeHighlights(textComp);

        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());

            hilite.addHighlight(start, end + 1, z);


        } catch (BadLocationException e) {
        }
    }

    /** Abre um arquivo externamente (usado com o manual) */
    public static void abrirExternamente(File document) throws Exception {
        Desktop dt = Desktop.getDesktop();
        dt.open(document);
    }

    private void atualizaZoom() {
        int c = zoom;
        comp.zoomTo(1, true);
        while (c > 0) {
            comp.zoomIn();
            c--;
        }
        while (c < 0) {
            comp.zoomOut();
            c++;
        }

    }

    public void exportarGrafico() {

        File ffff = new File("fluxograma.png");
        JFileChooser fc = new JFileChooser(ffff);
        FileFilter ff = new FileFilter() {

            @Override
            public boolean accept(File f) {
                String caminho = f.getAbsolutePath().toLowerCase();
                if (f.isDirectory() || caminho.endsWith(".png")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Imagem PNG";
            }
        };
        fc.setFileFilter(ff);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();

            if (selFile.exists()) {
                if (JOptionPane.showConfirmDialog(this, "O arquivo já existe. Deseja sobrescreve-lo?", "Sobresrever?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    exportarGrafico();
                    return;
                }
            }
            if (!selFile.getName().toLowerCase().contains(".png")) {
                selFile = new File(selFile.getAbsolutePath() + ".png");
            }

            int divider = jSplitPane1.getDividerLocation();
            jSplitPane1.setDividerLocation(0);
            validate();


            BufferedImage img = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics gx = img.getGraphics();
            comp.paint(gx);
            gx.drawString("Fluxograma gerado pelo programa Fluxo em " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), 10, img.getHeight() - 20);
            gx.dispose();
            jSplitPane1.setDividerLocation(divider);
            validate();

            try {
                // Salva PNG
                ImageIO.write(img, "png", selFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "O arquivo não foi salvo. Verifique as permissoes de gravação.", "Erro!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, "Não foi possivel aravar no arquivo.", ex);
            }
        }

    }

    private void salvar() {
        File ffff;

        if (arquivo != null) {
            ffff = arquivo;
        } else {
            ffff = new File("programa.spas");
        }

        JFileChooser fc = new JFileChooser(ffff);
        fc.setSelectedFile(ffff);
        FileFilter ff = new FileFilter() {

            @Override
            public boolean accept(File f) {
                String caminho = f.getAbsolutePath().toLowerCase();
                if (f.isDirectory() || caminho.endsWith(".spas")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Código Fonte SPAS";
            }
        };
        fc.setFileFilter(ff);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();

            if (selFile.exists()) {
                if (JOptionPane.showConfirmDialog(this, "O arquivo já existe. Deseja sobrescreve-lo?", "Sobresrever?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    exportarGrafico();
                    return;
                }
            }
            if (!selFile.getName().toLowerCase().contains(".spas")) {
                selFile = new File(selFile.getAbsolutePath() + ".spas");
            }

            try {

                salvarArquivoTexto(selFile, textPane.getText());
                textoAntigo = textPane.getText();
                arquivo = selFile;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "O arquivo não foi salvo. Verifique as permissoes de gravação.", "Erro!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, "Não foi possivel aravar no arquivo.", ex);
            }
        }
    }

    private void fecharPrograma() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja fechar assim mesmo?", "Desejar Fechar?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    private boolean isArquivoAlterado() {
        if (!textPane.getText().equals(textoAntigo)) {
            return true;
        }
        return false;
    }

    private void salvarArquivoTexto(File ffff, String text) throws IOException {
        BufferedWriter saida = new BufferedWriter(new FileWriter(ffff));
        saida.write(text);
        saida.flush();
        saida.close();
    }

    private void carregaModeloBasico() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar o modelo?", "Desejar abrir o modelo?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        textPane.setText("{ Modelo basico de programa }\r\n"
                + "program nomedoprograma;\r\n"
                + "var x: integer; { Exemplo de variavel }\r\n"
                + "begin\r\n"
                + "    {Coloque seus comandos aqui}\r\n"
                + "end.\r\n");
        textoAntigo = textPane.getText();
        validarcodigo();

    }

    private void carregaModeloIF() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar o modelo?", "Desejar abrir o modelo?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        textPane.setText("{ Exemplo: Usando o comando IF }\r\n"
                + "program usandoif;\r\n"
                + "var x,y: integer; \r\n"
                + "begin\r\n"
                + "    x:= 10;\r\n"
                + "    y:= 20;\r\n"
                + "    if x < 20 then\r\n"
                + "    begin\r\n"
                + "        y:= x;\r\n"
                + "        x:= x+(2*y)\r\n"
                + "    end\r\n"
                + "    else \r\n"
                + "        x:= y\r\n"
                + "end.\r\n");
        textoAntigo = textPane.getText();
        validarcodigo();

    }

    private void carregaModeloComandoSimples() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar o modelo?", "Desejar abrir o modelo?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        textPane.setText("{ Exemplo: Executando comandos simples }\r\n"
                + "program executandocomandos;\r\n"
                + "var x,y: integer; \r\n"
                + "begin\r\n"
                + "    y:= x;\r\n"
                + "    x:= x+(2*y)\r\n"
                + "end.\r\n");
        textoAntigo = textPane.getText();
        validarcodigo();
    }

    private void carregaModeloWhile() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar o modelo?", "Desejar abrir o modelo?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        textPane.setText("{ Exemplo: Usando o laço While }\r\n"
                + "program usandowhile;\r\n"
                + "var x,y: integer; \r\n"
                + "begin\r\n"
                + "    y:= 50;\r\n"
                + "    x:= 10;\r\n"
                + "    while x < y do\r\n"
                + "    begin\r\n"
                + "        x:= x+1\r\n"
                + "    end\r\n"
                + "end.\r\n");
        textoAntigo = textPane.getText();
        validarcodigo();
    }

    private void carregaModeloProcedimento() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar o modelo?", "Desejar abrir o modelo?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }
        textPane.setText("{ Exemplo: Usando Procedimentos }\r\n"
                + "program usandoprocedimentos;\r\n"
                + "var x,y: integer; \r\n"
                + "procedure somar; \r\n"
                + "begin \r\n"
                + "    x:= x+y\r\n"
                + "end; \r\n"
                + "begin\r\n"
                + "    y:= 10;\r\n"
                + "    x:= 50;\r\n"
                + "    somar\r\n"
                + "end.\r\n");
        textoAntigo = textPane.getText();
        validarcodigo();
    }

    private void abrir() {
        if (isArquivoAlterado()) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar um novo arquivo?", "Desejar descartar as alterações?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }
        }



        JFileChooser fc = new JFileChooser("");
        FileFilter ff = new FileFilter() {

            @Override
            public boolean accept(File f) {
                String caminho = f.getAbsolutePath().toLowerCase();
                if (f.isDirectory() || caminho.endsWith(".spas")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Código Fonte SPAS";
            }
        };
        fc.setFileFilter(ff);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();

            if (!selFile.exists()) {
                if (JOptionPane.showConfirmDialog(this, "O arquivo não existe! Deseja criar um novo?", "Criar arquivo?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    novo(true);
                    return;
                } else {
                    return;
                }
            }
            if (!selFile.getName().toLowerCase().contains(".spas")) {
                selFile = new File(selFile.getAbsolutePath() + ".spas");
            }

            try {
                String conteudo = lerArquivoTexto(selFile);
                textPane.setText(conteudo);
                textoAntigo = conteudo;
                arquivo = selFile;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "O arquivo não foi salvo. Verifique as permissoes de gravação.", "Erro!", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, "Não foi possivel aravar no arquivo.", ex);
            }
        }


        validarcodigo();
        repaint();
        validate();



    }

    private String lerArquivoTexto(File selFile) throws IOException {
        FileInputStream stream = new FileInputStream(selFile);
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);
        String line = null;
        String conteudo = "";
        while ((line = reader.readLine()) != null) {
            conteudo += line + "\r\n";
        }
        reader.close();
        return conteudo;
    }

    private void novo(boolean forcar) {
        if (isArquivoAlterado() || forcar) {
            if (JOptionPane.showConfirmDialog(this, "Você não salvou as modificações. Tem certeza que deseja carregar um novo arquivo?", "Desejar descartar as alterações?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {

                return;
            }
        }
        arquivo = null;
        textPane.setText("");
        textoAntigo = "";

        painelFluxo.removeAll();

        validarcodigo();
        repaint();
        validate();
    }

    private void abrirSobre() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JanelaSobre j2 = new JanelaSobre();
                j2.setLocationRelativeTo(null);
                j2.setVisible(true);
            }
        });
    }

    private void abrirLegenda() {
        try {
            abrirExternamente(new File("legenda.rtf"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o arquivo.", "Erro ao abrir o arquivo", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(JanelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



// A private subclass of the default highlight painter
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

    // A private subclass of the default highlight painter
    class MyHighlightPainterR extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainterR(Color color) {
            super(color);
        }
    }
}
