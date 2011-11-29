/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.renangomes.fluxo.fluxograma;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.JFrame;

/**
 *
 * @author RenanGomes
 */
public class Fluxograma extends JFrame {

    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();
    Object ultimo = null;
    final static int LARGURA = 230;
    final static int ALTURA = 40;
    final static int DISTANCIAX = 30;
    final static int DISTANCIAY = 35;
    boolean gerado = false;
    int nlinhas = 0;
    int colunaatual = 0;
    boolean verificaMaxColuna = false;
    int maxColuna = 0;
    Stack<String> proximaLigacao = new Stack<String>();

    public Fluxograma() {
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");
        //graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FILLCOLOR, "#ffeeee");
        //graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_INDICATOR_COLOR, "#000000");
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_MOVABLE, false);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FONTSIZE, "11");
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_BENDABLE, false);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_CLONEABLE, false);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_DELETABLE, false);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_EDITABLE, false);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_RESIZABLE, false);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#FAFAFA");

        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FILLCOLOR, "#ffeeee");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, "11");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTFAMILY, "verdana");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_MOVABLE, false);
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_BENDABLE, false);
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_CLONEABLE, false);
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_DELETABLE, false);
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_EDITABLE, false);
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_RESIZABLE, false);


        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_OVERFLOW, true);

        Map<String, Object> style = new HashMap<String, Object>();
        style.putAll(graph.getStylesheet().getDefaultVertexStyle());
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RHOMBUS);
        style.put(mxConstants.STYLE_FILLCOLOR, "#eeffee");
        style.put(mxConstants.STYLE_FONTSIZE, "11");
        graph.getStylesheet().putCellStyle("condicao", style);


        Map<String, Object> styleDot = new HashMap<String, Object>();
        styleDot.putAll(graph.getStylesheet().getDefaultVertexStyle());
        styleDot.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        graph.getStylesheet().putCellStyle("conector", styleDot);



        Map<String, Object> style2 = new HashMap<String, Object>();
        style2.putAll(graph.getStylesheet().getDefaultEdgeStyle());
        style2.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL);
        graph.getStylesheet().putCellStyle("linhaquadrada", style2);

        Map<String, Object> style6 = new HashMap<String, Object>();
        style6.putAll(style2);
        style6.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_SIZE);

        style6.put(mxConstants.STYLE_INDICATOR_WIDTH, 1);

        graph.getStylesheet().putCellStyle("linhaquadradasemseta", style6);


        Map<String, Object> style3 = new HashMap<String, Object>();
        style3.putAll(graph.getStylesheet().getDefaultVertexStyle());
        style3.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_DOUBLE_ELLIPSE);
        style3.put(mxConstants.STYLE_FILLCOLOR, "#eeeeee");
        style3.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        style3.put(mxConstants.STYLE_ROUNDED, true);
        graph.getStylesheet().putCellStyle("terminador", style3);

        Map<String, Object> style5 = new HashMap<String, Object>();
        style5.putAll(graph.getStylesheet().getDefaultVertexStyle());
        style5.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style5.put(mxConstants.STYLE_ROUNDED, true);
        style5.put(mxConstants.STYLE_FILLCOLOR, "#eeeeff");
        graph.getStylesheet().putCellStyle("procedimento", style5);

        Object inicio = graph.insertVertex(parent, null, "Início", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + Math.round((LARGURA - 70) / 2), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), 70, ALTURA, "terminador");
        ultimo = inicio;

    }

    public mxGraphComponent gerar() {
        graph.getModel().beginUpdate();

        if (!gerado) {
            Object fim = graph.insertVertex(parent, null, "Fim", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + Math.round((LARGURA - 70) / 2), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), 70, ALTURA, "terminador");
            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, fim, "linhaquadrada");
            gerado = true;
        }
        graph.setAllowDanglingEdges(false);
        graph.setAutoSizeCells(true);
        graph.setConnectableEdges(false);
        graph.getModel().endUpdate();





        final mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setToolTips(true);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(new Color(250, 250, 250));
        graphComponent.setGridVisible(true);
        getContentPane().add(graphComponent);


        // Handle only mouse click events
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Object cell = graphComponent.getCellAt(e.getX(), e.getY());
                if (cell != null) {
                    JanelaPrincipal.alertarClique((String) ((mxCell) cell).getValue());
                }
            }
        });




        return graphComponent;
    }

    public void addComando(Comando c) {
        if (c.getTipo() == Comando.TipoDeComando.IF) {
            addComandoIf((ComandoIf) c);
        } else {
            if (c.getTipo() == Comando.TipoDeComando.WHILE) {
                addComandoWhile((ComandoWhile) c);
            } else {
                if (c.getTipo() == Comando.TipoDeComando.FOR) {
                    addComandoFor((ComandoFor) c);
                } else {
                    if (c.getTipo() == Comando.TipoDeComando.ATIVACAODEPROCEDIMENTO) {
                        addComandoAtivacaoDeProcedimento((ComandoAtivacaoDeProcedimento) c);
                    } else {
                        addComandoSimples((ComandoSimples) c);
                    }

                }
            }

        }
    }

    private void addComandoSimples(ComandoSimples comando) {
        graph.getModel().beginUpdate();
        try {
            Object com = graph.insertVertex(parent, null, comando.getTexto(), 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), LARGURA, ALTURA);
            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, com, "linhaquadrada");
            ultimo = com;
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private void addComandoAtivacaoDeProcedimento(ComandoAtivacaoDeProcedimento comando) {
        graph.getModel().beginUpdate();
        try {
            Object com = graph.insertVertex(parent, null, comando.getTexto(), 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), LARGURA, ALTURA, "procedimento");

            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, com, "linhaquadrada");
            ultimo = com;
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private void addComandoIf(ComandoIf cmdif) {
        graph.getModel().beginUpdate();

        try {

            Object condc = graph.insertVertex(parent, null, cmdif.getCondicao(), 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), LARGURA, ALTURA, "condicao");
            int linhadaCondicao = nlinhas;
            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, condc, "linhaquadrada");

            Object ultimoFalse = condc;
            maxColuna = colunaatual;
            String proximoLabeltmp = "";
            if (cmdif.isPossuiElse()) {
                proximaLigacao.push("Não");
                ultimo = condc;

                Object conector0 = graph.insertVertex(parent, null, "", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + (Math.round(LARGURA / 2)), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas) - 10, 0, 0, "conector");
                graph.insertEdge(parent, null, getProximaLigacao(), ultimo, conector0, "linhaquadradasemseta");
                ultimo = conector0;




                verificaMaxColuna = true;
                for (Comando c : cmdif.comandosElse) {
                    addComando(c);
                }
                verificaMaxColuna = false;

                ultimoFalse = ultimo;
                if (!proximaLigacao.isEmpty()) {
                    proximoLabeltmp = proximaLigacao.pop();
                }
            }
            int linhafinalFalse = nlinhas;



            int col = colunaatual;
            nlinhas = linhadaCondicao;
            colunaatual = maxColuna;
            ultimo = condc;
            incrementaColunaAtual();
            proximaLigacao.push("Sim");

            Object conector1 = graph.insertVertex(parent, null, "", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + (Math.round(LARGURA / 2)), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), 0, 0, "conector");
            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, conector1, "linhaquadradasemseta");
            ultimo = conector1;

            for (Comando c : cmdif.comandos) {
                if (cmdif.comandos.size() >= 1 && c == cmdif.comandos.get(cmdif.comandos.size() - 1) && cmdif.isPossuiElse()) {
                    nlinhas = Math.max(linhafinalFalse, nlinhas);
                }

                addComando(c);
            }


            if (cmdif.comandos.size() == 0) {
                nlinhas = Math.max(linhafinalFalse, nlinhas);
                Object conector5 = graph.insertVertex(parent, null, "", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + (Math.round(LARGURA / 2)), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), 0, 0, "conector");
                graph.insertEdge(parent, null, getProximaLigacao(), ultimo, conector5, "linhaquadradasemseta");
                ultimo = conector5;

            }

            colunaatual = col;
            int linhafinalTrue = nlinhas;
            if (!cmdif.isPossuiElse()) {
                proximaLigacao.push("Não");
            }
            nlinhas = Math.max(linhafinalTrue, linhafinalFalse);


            if (!proximoLabeltmp.isEmpty()) {
                proximaLigacao.push(proximoLabeltmp);
            }
            Object conector = graph.insertVertex(parent, null, "", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + (Math.round(LARGURA / 2)), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), 0, 0, "conector");
            graph.insertEdge(parent, null, getProximaLigacao(), ultimoFalse, conector, "linhaquadradasemseta");





            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, conector, "linhaquadradasemseta");
            ultimo = conector;






        } finally {
            graph.getModel().endUpdate();
        }

    }

    private void addComandoFor(ComandoFor cmdif) {
    }

    private void addComandoWhile(ComandoWhile cmdwhile) {
        graph.getModel().beginUpdate();

        try {
            incrementaColunaAtual();

            Object conector0 = graph.insertVertex(parent, null, "", 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual) + (Math.round(LARGURA / 2)), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas) - 10, 0, 0, "conector");
            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, conector0, "linhaquadradasemseta");

            ultimo = conector0;

            int lcon = nlinhas;
            int ccon = colunaatual;
            Object condc = graph.insertVertex(parent, null, cmdwhile.getCondicao(), 10 + (colunaatual * DISTANCIAX) + (LARGURA * colunaatual), 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++), LARGURA, ALTURA, "condicao");

            graph.insertEdge(parent, null, getProximaLigacao(), ultimo, condc, "linhaquadrada");

            ultimo = condc;
            incrementaColunaAtual();
            proximaLigacao.push("Sim");
            for (Comando c : cmdwhile.comandos) {
                addComando(c);
            }
            graph.insertEdge(parent, "", getProximaLigacao(), ultimo, condc, "linhaquadrada");
            colunaatual--;
            proximaLigacao.push("Não");
            ultimo = condc;


            Object conector2 = graph.insertVertex(parent, null, "", 10 + (ccon * DISTANCIAX) + (LARGURA * ccon)-50, 10 + ((lcon) * DISTANCIAY) + (ALTURA * lcon)+ (Math.round(ALTURA / 2)), 0, 0, "conector");
            graph.insertEdge(parent, null, getProximaLigacao(), conector2, ultimo, "linhaquadradasemseta");

        Object conector3 = graph.insertVertex(parent, null, "", 10 + (ccon * DISTANCIAX) + (LARGURA * ccon)-50, 10 + ((nlinhas) * DISTANCIAY) + (ALTURA * nlinhas++)+ (Math.round(ALTURA / 2)) , 0, 0, "conector");
            graph.insertEdge(parent, null, getProximaLigacao(), conector3, conector2, "linhaquadradasemseta");


            ultimo = conector3;





        } finally {
            graph.getModel().endUpdate();
        }

        colunaatual--;

    }

    public static void main(String[] args) {
        Fluxograma frame = new Fluxograma();

        frame.setState(Frame.MAXIMIZED_BOTH);


        frame.addComando(new ComandoSimples("Integer variavel;"));
        frame.addComando(new ComandoSimples("variavel := 10;"));
        frame.addComando(new ComandoSimples("variavel := variavel - 1;"));

        //IF
        ComandoIf nif = new ComandoIf();
        nif.setCondicao("i>1");
        nif.comandos.add(new ComandoSimples("c1"));
        nif.comandos.add(new ComandoSimples("c2"));

        nif.setPossuiElse(true);
        nif.comandosElse.add(new ComandoSimples("e1"));
        nif.comandosElse.add(new ComandoSimples("e2"));
        nif.comandosElse.add(new ComandoSimples("e3"));

        ComandoIf nif2 = new ComandoIf();
        nif2.setCondicao("i2>21");
        nif2.comandos.add(new ComandoSimples("c21"));
        nif2.comandos.add(new ComandoSimples("c22"));
        nif2.comandos.add(new ComandoSimples("c23"));
        nif2.setPossuiElse(true);
        nif2.comandosElse.add(new ComandoSimples("e21"));
        nif2.comandosElse.add(new ComandoSimples("e22"));


        ComandoIf nif3 = new ComandoIf();
        nif3.setCondicao("i3>31");
        nif3.comandos.add(new ComandoSimples("c31"));
        nif3.comandos.add(new ComandoSimples("c32"));
        nif3.comandos.add(new ComandoSimples("c33"));
        nif3.setPossuiElse(true);
        nif3.comandosElse.add(new ComandoSimples("e31"));
        nif3.comandosElse.add(new ComandoSimples("e32"));


        //WHILE
        ComandoWhile w = new ComandoWhile();
        w.setCondicao("i >= 1 ");
        w.comandos.add(new ComandoSimples("integer a;"));
        w.comandos.add(new ComandoSimples("integer a;"));

        //WHILE ANINHADO
        ComandoWhile w2 = new ComandoWhile();
        w2.setCondicao("contaclientes < 100 ");
        w2.comandos.add(new ComandoSimples("contaclientes := contaclientes+1;"));
        w.comandos.add(w2);
        w.comandos.add(new ComandoSimples("b := 20;"));


        nif3.comandosElse.add(w);



        nif2.comandosElse.add(nif3);

        nif.comandosElse.add(nif2);


        frame.addComando(new ComandoSimples("ultimo := comando;"));

        frame.addComando(nif);

        frame.setBackground(Color.white);
        frame.setForeground(Color.WHITE);

        frame.gerar();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(frame.MAXIMIZED_BOTH);
        frame.setVisible(true);

    }

    String getProximaLigacao() {
        if (!proximaLigacao.empty()) {
            return proximaLigacao.pop();
        }

        return "";
    }

    private void incrementaColunaAtual() {
        colunaatual++;
        maxColuna = Math.max(maxColuna, colunaatual);
    }
}