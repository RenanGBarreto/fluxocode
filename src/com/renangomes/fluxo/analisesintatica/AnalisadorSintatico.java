/**
 * Fluxo: Uma Ferramenta Visual para Auxílio ao Ensino de Programação
 * Copyright (C) 2012 Renan Gomes (email {at} renangomes {dot} com), Pedro Dantas (tigreped {at} gmail {dot} com)
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

/** 
 * Aquivo: AnalisadorSintatico.java
 * Data de Criação: 08:22:02 09/09/2011
 * Codificacao: UTF-8
 */
package com.renangomes.fluxo.analisesintatica;

import com.renangomes.fluxo.fluxograma.Comando;
import com.renangomes.fluxo.fluxograma.ComandoAtivacaoDeProcedimento;
import com.renangomes.fluxo.fluxograma.ComandoIf;
import com.renangomes.fluxo.fluxograma.ComandoSimples;
import com.renangomes.fluxo.fluxograma.ComandoWhile;
import com.renangomes.fluxo.fluxograma.Fluxograma;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.renangomes.fluxo.utils.Linha;
import com.renangomes.fluxo.utils.Linguagem.Token;

/**
 * Esta Classe representa o Analisador Sintatico
 * @since 22/09/2011
 */
public class AnalisadorSintatico {

    public String ultimoErro = "";
    public int ultimalinhaerro = -1;
    private List<Linha> tabela = null;
    private ArrayList<Linha> auxiliar = null;
    private Linha simboloLido = null;
    private Stack<Linha> pilha = null;
    private int posicao = 0;
    public HashMap<String, TipoDeDados> tabelaTipos = new HashMap<String, TipoDeDados>();
    ArrayList<String> idListTMP = new ArrayList<String>();
    private boolean verbose = false;
    private boolean emProcedimentos = true;
    public HashMap<String, Fluxograma> fluxogramas = new HashMap<String, Fluxograma>();
    private boolean semanticoOff = false;

    private TipoDeDados pegaTipo(String token) {
        return tabelaTipos.get(token);
    }

    public enum TipoDeDados {

        INTEIRO, REAL, BOOLEAN
    };

    /** Construtor padrão privado. */
    private AnalisadorSintatico() {
        ultimoErro = "";
    }

    /**
     * Contrutor que será usado no programa.
     * @param t A tabela gerada pelo analisador lexico
     */
    public AnalisadorSintatico(List<Linha> t) throws Exception {
        ultimoErro = "";
        if (t == null) {
            throw new RuntimeException("A tabela passada era nula");
        }
        tabela = t;
        pilha = new Stack<Linha>();
    }

    public AnalisadorSintatico(List<Linha> t, boolean sOff) throws Exception {
        semanticoOff = sOff;
        ultimoErro = "";
        if (t == null) {
            throw new RuntimeException("A tabela passada era nula");
        }
        tabela = t;
        pilha = new Stack<Linha>();
    }

    /**
     * Retorna o proximo simbolo da tabela.
     */
    private void obtenha_simbolo() {
        try {
            simboloLido = tabela.get(posicao++);
        } catch (Exception e) {
            erroSintatico("Fim de arquivo inesperado", false);
        }
    }

    /** Gera um erro de execução personalizado */
    private void erroSintatico(String string) {
        ultimoErro = "";
        ultimoErro += ("ERRO DE COMPILAÇÃO SINTÁTICO\n");
        ultimoErro += ("LINHA: " + simboloLido.numeroDaLinha + "\n");
        ultimoErro += ("Descrição: " + string + "");

        if (simboloLido != null) {
            ultimoErro += (" porém o token '" + simboloLido.token + "' é do tipo " + simboloLido.tipo + ".\n");
        } else {
            ultimoErro += (".\n");
        }
        ultimalinhaerro = simboloLido.numeroDaLinha;
        throw new RuntimeException(ultimoErro);

    }

    /** Gera um erro de execução semantico personalizado */
    private void erroSemantico(String string) {
        if (!semanticoOff) {
            ultimoErro = "";
            ultimoErro += ("ERRO DE COMPILAÇÃO SEMANTICO\n");
            ultimoErro += ("LINHA: " + simboloLido.numeroDaLinha + "\n");
            ultimoErro += ("Descrição: " + string + "\n");
            // Fica mais realista se deixarem os erros aparecerem todos.
            ultimalinhaerro = simboloLido.numeroDaLinha;
            throw new RuntimeException(ultimoErro);
        }
    }

    /** Gera um erro de execução personalizado, também */
    private void erroSintatico(String string, boolean b) {
        ultimoErro = "";
        ultimoErro += ("ERRO DE COMPILAÇÃO SINTÁTICO\n");
        ultimoErro += ("LINHA: " + simboloLido.numeroDaLinha + "\n");
        ultimoErro += ("Descrição: " + string + "");
        if (simboloLido != null && b) {
            ultimoErro += (" porém o token '" + simboloLido.token + "' é do tipo " + simboloLido.tipo + ".\n");
        } else {
            ultimoErro += (".\n");
        }
        ultimalinhaerro = simboloLido.numeroDaLinha;
        throw new RuntimeException(ultimoErro);
    }

    /** Chama o procedimento de analise sintática */
    public void analisar() {

        //fluxo = new Fluxograma();


        obtenha_simbolo();
        programa();
        if (posicao != tabela.size()) {
            erroSintatico("Dados inesperados após o delimitador '.'", false);
        }

//        for (Comando c : cmd) {
//            fluxo.addComando(c);
//        }
        //fluxo.gerar(); // nao descomente
    }

    private void programa() {
        if (simboloLido.token.equals("program")) {
            obtenha_simbolo();
            if (simboloLido.tipo == Token.IDENTIFICADOR) {
                // Adiciona a marca:
                pilha.add(new Linha("$", null, simboloLido.numeroDaLinha, -1));
                // Adiciona o identificador do programa a  pilha:

                String nomePrograma = "Programa: " + simboloLido.token;



                pilha.add(simboloLido);
                if (verbose) {
                    System.out.println("--> " + simboloLido.token);
                }
                obtenha_simbolo();
                if (simboloLido.token.equals(";")) {
                    obtenha_simbolo();
                    declaracoes_variaveis();
                    obtenha_simbolo();
                    declaracoes_de_subprogramas();
                    obtenha_simbolo();
                    emProcedimentos = false;
                    Fluxograma fprograma = new Fluxograma();
                    ArrayList<Comando> comandosPrograma = comando_composto();
                    for (Comando c : comandosPrograma) {
                        fprograma.addComando(c);
                    }
                    fluxogramas.put(nomePrograma, fprograma);
                    obtenha_simbolo();
                    if (simboloLido != null && simboloLido.token.equals(".")) {
                        // Se chegar aqui tudo ocorreu bem, logo, não faremos nada.
                    } else {
                        erroSintatico("Delimitador '.' esperado");
                    }
                } else {
                    erroSintatico("Delimitador ';' esperado");
                }
            } else {
                erroSintatico("Identificador esperado");
            }
        } else {
            erroSintatico("Palavra Reservada 'program' esperada");
        }
    }

    private void declaracoes_variaveis() {
        if (simboloLido.token.equals("var")) {
            obtenha_simbolo();
            lista_declaracoes_variaveis();
        } else {
            vazio();
        }
    }

    // RECURSIVO
    // original: LDV := LDV LDI:TIPO;  | LDI:TIPO;
    // Sem recursao a esquerda: LDV  :=LDI:TIPO;LDV'
    //                          LDV' := VAZIO | LDI:TIPO;LDV'     
    private void lista_declaracoes_variaveis() {
        auxiliar = new ArrayList<Linha>();
        lista_de_identificadores();
        obtenha_simbolo();
        if (simboloLido.token.equals(":")) {
            obtenha_simbolo();
            // Configura o tipo dos tokens e adiciona Ã  pilha:
            insereIdentificadoresNaPilha();
            tipo();
            obtenha_simbolo();
            if (simboloLido.token.equals(";")) {
                obtenha_simbolo();
                lista_declaracoes_variaveisLINHA();
            } else {
                erroSintatico("Delimitador ';' esperado");
            }
        } else {
            erroSintatico("Delimitador ':' esperado");
        }
    }

    private void lista_declaracoes_variaveisLINHA() {
        auxiliar = new ArrayList<Linha>();
        if (simboloLido.tipo == Token.IDENTIFICADOR) {
            lista_de_identificadores();
            obtenha_simbolo();
            if (simboloLido.token.equals(":")) {
                obtenha_simbolo();
                // Configura o tipo dos tokens e adiciona Ã  pilha:
                insereIdentificadoresNaPilha();
                tipo();
                obtenha_simbolo();
                if (simboloLido.token.equals(";")) {
                    obtenha_simbolo();
                    lista_declaracoes_variaveisLINHA();
                } else {
                    erroSintatico("Delimitador ';' esperado");
                }
            } else {
                erroSintatico("Delimitador ':' esperado");
            }
        } else {
            vazio();
        }
    }

    // RECURSIVO
    // original: LDI := ID | LDI,ID
    // Sem recursao a esquerda: LDI := ID LDI'
    // LDI' := VAZIO | ,ID LDI'
    private void lista_de_identificadores() {
        if (simboloLido.tipo == Token.IDENTIFICADOR) {
            auxiliar.add(simboloLido);
            idListTMP.add(simboloLido.token);
            obtenha_simbolo();
            lista_de_identificadoresLINHA();
        } else {
            erroSintatico("Identificador Esperado");
        }
    }

    private void lista_de_identificadoresLINHA() {
        if (simboloLido.token.equals(",")) {
            obtenha_simbolo();
            if (simboloLido.tipo == Token.IDENTIFICADOR) {
                auxiliar.add(simboloLido);
                idListTMP.add(simboloLido.token);
                obtenha_simbolo();
                lista_de_identificadoresLINHA();
            } else {
                erroSintatico("Identificador esperado");
            }
        } else {
            vazio();
        }
    }

    private void tipo() {
        if ((simboloLido.token.equals("integer") || simboloLido.token.equals("real") || simboloLido.token.equals("boolean"))) {

            TipoDeDados t1;
            if (simboloLido.token.equals("integer")) {
                t1 = TipoDeDados.INTEIRO;
            } else {
                if (simboloLido.token.equals("real")) {
                    t1 = TipoDeDados.REAL;
                } else {
                    t1 = TipoDeDados.BOOLEAN;
                }
            }

            for (String s : idListTMP) {
                tabelaTipos.put(s, t1);
            }
            idListTMP.clear();
        } else {
            erroSintatico("Era esperado um TIPO: 'integer', 'real' ou 'boolean'");
        }
    }

    // RECURSIVO
    // original: DSDS := DSDS DDS | VAZIO
    // Sem recursao a esquerda: DSDS := DSDS'
    //                          DSDS' := VAZIO | DDS DSDS'
    // Simplificado DSDS := VAZIO | DDS DSDS
    private void declaracoes_de_subprogramas() {
        if (simboloLido.token.equals("procedure")) {
            declaracao_de_subprograma();
            obtenha_simbolo();
            declaracoes_de_subprogramas();
        } else {
            vazio();
        }
    }

    private void declaracao_de_subprograma() {
        if (simboloLido.token.equals("procedure")) {
            obtenha_simbolo();
            if (simboloLido.tipo == Token.IDENTIFICADOR) {

                // Adiciona o identificador da procedure:
                pilha.add(simboloLido);
                if (verbose) {
                    System.out.println("--> " + simboloLido.token);
                }
                String nomeSubprograma = "Procedimento: " + simboloLido.token;
                // Adiciona a marca que identifica o novo escopo do procedure:
                pilha.add(new Linha("$", null, simboloLido.numeroDaLinha, -1));
                obtenha_simbolo();
                argumentos();
                // ponto e virgula
                obtenha_simbolo();
                if (!simboloLido.token.equalsIgnoreCase(";")) {
                    erroSintatico("Era esperado um ; depois da lista de argumentos.");
                }
                obtenha_simbolo();
                declaracoes_variaveis();

                obtenha_simbolo();
                declaracoes_de_subprogramas();
                obtenha_simbolo();
                Fluxograma fsubprograma = new Fluxograma();
                ArrayList<Comando> comandosSubprograma = comando_composto();
                for (Comando c : comandosSubprograma) {
                    fsubprograma.addComando(c);
                }
                fluxogramas.put(nomeSubprograma, fsubprograma);





                obtenha_simbolo();


            } else {
                erroSintatico("Identificador Esperado");
            }
        } else {
            erroSintatico("Palavra Reservada 'procedure' esperada");
        }
    }

    private void argumentos() {
        if (simboloLido.token.equals("(")) {
            obtenha_simbolo();
            lista_de_parametros();
            obtenha_simbolo();
            if (!simboloLido.token.equals(")")) {
                erroSintatico("Era esperado o caractere ')'");
            }
        } else {
            vazio();
        }
    }

    // RECURSIVO
    // original: LDP :=  LDI:TIPO | LDP;LDI:TIPO
    // Sem recursao a esquerda: LDP  := LDI:TIPO LDP'
    //                          LDP1 := VAZIO | ;LDI:TIPO LDP'
    private void lista_de_parametros() {
        auxiliar = new ArrayList<Linha>();
        lista_de_identificadores();
        obtenha_simbolo();
        if (simboloLido.token.equals(":")) {
            obtenha_simbolo();
            insereIdentificadoresNaPilha();
            tipo();
            obtenha_simbolo();
            lista_de_parametrosLINHA();
        } else {
            erroSintatico("Era esperado o delimitador ':'");
        }
    }

    private void lista_de_parametrosLINHA() {
        auxiliar = new ArrayList<Linha>();
        if (simboloLido.token.equals(";")) {
            obtenha_simbolo();
            lista_de_identificadores();
            obtenha_simbolo();
            if (simboloLido.token.equals(":")) {
                obtenha_simbolo();
                insereIdentificadoresNaPilha();
                tipo();
                obtenha_simbolo();
                lista_de_parametrosLINHA();
            } else {
                erroSintatico("Era esperado o delimitador ':'");
            }
        } else {
            vazio();
        }
    }

    private ArrayList<Comando> comando_composto() {
        ArrayList<Comando> saida = new ArrayList<Comando>();
        if (simboloLido.token.equals("begin")) {

            //Cria um novo escopo
            pilha.push(new Linha("$", Token.DELIMITADOR, -1, -1));

            obtenha_simbolo();
            saida.addAll(comandos_opcionais());
            obtenha_simbolo();
            if (!simboloLido.token.equals("end")) {
                erroSintatico("Era esperado a palavra reservada 'end'");
            }

            if (emProcedimentos) {
                // Remove tudo do escopo:
                while (!pilha.empty() && !pilha.peek().token.equalsIgnoreCase("$")) {
                    Linha l = pilha.pop();
                    if (verbose) {
                        System.out.println("<-- " + l.token);
                    }
                }
                // Remove a marca:
                if (!pilha.empty()) {
                    pilha.pop();
                }
            }

        } else {
            erroSintatico("Era esperado a palavra reservada 'begin'");
        }

        return saida;
    }

    private ArrayList<Comando> comandos_opcionais() {
        ArrayList<Comando> saida = new ArrayList<Comando>();
        if (simboloLido.token.equals("while") || simboloLido.token.equals("if")
                || simboloLido.token.equals("begin") || simboloLido.tipo == Token.IDENTIFICADOR) {
            saida.addAll(lista_de_comandos());
        } else {
            vazio();

        }
        return saida;
    }

    // RECURSIVO
    // original: LDC := C | LDC;C
    // Sem recursao a esquerda: LDC := C LDC'
    //                          LDC' := VAZIO | ;C LDC'
    private ArrayList<Comando> lista_de_comandos() {
        ArrayList<Comando> saida = new ArrayList<Comando>();
        saida.addAll(comando());
        obtenha_simbolo();
        saida.addAll(lista_de_comandosLINHA());
        return saida;
    }

    private ArrayList<Comando> lista_de_comandosLINHA() {
        ArrayList<Comando> saida = new ArrayList<Comando>();
        if (simboloLido.token.equals(";")) {
            obtenha_simbolo();
            saida.addAll(comando());
            obtenha_simbolo();
            saida.addAll(lista_de_comandosLINHA());
        } else {
            vazio();
        }
        return saida;
    }

    // NÂO DETERMINISTICO!
    private ArrayList<Comando> comando() {
        ArrayList<Comando> saida = new ArrayList<Comando>();
        if (simboloLido.token.equals("if")) {
            //gerar comandoif no fluxograma
            ComandoIf cmdif = new ComandoIf();
            int p = posicao;
            String expCondicao = "";
            try {
                Linha sl = tabela.get(p++);
                while (!sl.token.equals("then")) {
                    expCondicao += sl.token + " ";
                    sl = tabela.get(p++);
                }
                cmdif.setCondicao(expCondicao);
            } catch (Exception e) {
                cmdif.setCondicao("?");
            }
            obtenha_simbolo();
            if (!(TipoDeDados.BOOLEAN == expressao())) {
                erroSemantico("Era experado uma expressão que retornasse um boolean.");
            }
            obtenha_simbolo();
            if (simboloLido.token.equals("then")) {
                obtenha_simbolo();
                cmdif.comandos.addAll(comando());
                obtenha_simbolo();
                ArrayList<Comando> lelse = parte_else();
                if (lelse == null) {
                    cmdif.setPossuiElse(false);
                } else {
                    cmdif.setPossuiElse(true);
                    cmdif.comandosElse.addAll(lelse);
                }
            } else {
                erroSintatico("Era esperada a palavra reservada 'then'");
            }

            saida.add(cmdif);

        } else {
            if (simboloLido.token.equals("while")) {
                //gerar comandowhile no fluxograma
                ComandoWhile cmdwhile = new ComandoWhile();
                int p = posicao;
                String expCondicao = "";
                try {
                    Linha sl = tabela.get(p++);
                    while (!sl.token.equals("do")) {
                        expCondicao += sl.token + " ";
                        sl = tabela.get(p++);
                    }
                    cmdwhile.setCondicao(expCondicao);
                } catch (Exception e) {
                    cmdwhile.setCondicao("?");
                }



                obtenha_simbolo();



                if (!(TipoDeDados.BOOLEAN == expressao())) {
                    erroSemantico("Era experado uma expressão que retornasse um boolean.");
                }

                obtenha_simbolo();
                if (simboloLido.token.equals("do")) {
                    obtenha_simbolo();
                    cmdwhile.comandos.addAll(comando());
                } else {
                    erroSintatico("Era esperada a palavra reservada 'do'");
                }
                saida.add(cmdwhile);
            } else {
                if (simboloLido.token.equals("begin")) {
                    saida.addAll(comando_composto());
                } else {




                    String s = simboloLido.token + " ";
                    int p1 = posicao;
                    if (simboloLido.tipo == Token.IDENTIFICADOR) {

                        TipoDeDados t1 = pegaTipo(simboloLido.token);
                        String tok = simboloLido.token;
                        obtenha_simbolo();
                        if (simboloLido.token.equals(":=")) {
                            obtenha_simbolo();
                            TipoDeDados t2 = expressao();

                            if (!((t1 == TipoDeDados.BOOLEAN && t2 == TipoDeDados.BOOLEAN)
                                    || (t1 == TipoDeDados.INTEIRO && t2 == TipoDeDados.INTEIRO)
                                    || (t1 == TipoDeDados.REAL && t2 == TipoDeDados.REAL)
                                    || (t1 == TipoDeDados.REAL && t2 == TipoDeDados.INTEIRO))) {
                                erroSemantico("Foi atribuido a variavel " + tok + " que é " + t1 + " uma expressao com tipo resultante " + t2);
                            }



                            int p2 = posicao;
                            while (p1 < p2) {
                                s += tabela.get(p1++).token;
                            }
                            saida.add(new ComandoSimples(s));
                        } else {
                            vazio();
                            saida.add(ativacao_de_procedimento());
                        }
                    } else {
                        erroSintatico("Era esperado um comando");
                    }
                }
            }
        }
        return saida;
    }

    private ArrayList<Comando> parte_else() {
        if (simboloLido.token.equals("else")) {
            obtenha_simbolo();
            return comando();
        } else {
            vazio();
            return null;
        }
    }

    private void variavel() {
        if (simboloLido.tipo == Token.IDENTIFICADOR) {
            //ok, não faz nada.
        } else {
            erroSintatico("Era esperado um identificador");
        }
    }

    //Nao determinismo detectado
    //Original: AP := ID | ID(LDE)
    //Melhorado:  AP := ID APA
    //            APA := VAZIO | (LE)
    private ComandoAtivacaoDeProcedimento ativacao_de_procedimento() {

        ArrayList<Comando> saida = new ArrayList<Comando>();

        String s = "";
        if (simboloLido.tipo == Token.IDENTIFICADOR) {
            s += simboloLido.token + " ";

            // ADICIONADO por Renan Gomes
            // Verifica se o identificador esta no escopo 
            Object pilhaAuxiliar[] = pilha.toArray();
            boolean possui = false;
            for (int i = 0; i < pilhaAuxiliar.length; i++) {
                if (((Linha) pilhaAuxiliar[i]).token.equalsIgnoreCase(simboloLido.token)) {
                    if (verbose) {
                        System.out.println("Identificador encontrado na pilha: "
                                + simboloLido.token);
                    }
                    possui = true;
                    break;
                }
            }
            //  Erro: identificador não encontrado
            if (!possui) {
                erroSemantico("Identificador não previamente declarado: " + simboloLido.token);
            }

            // Fim da edicao de Renan GOmes
            int p1 = posicao;

            obtenha_simbolo();
            ativacao_de_procedimentoAUXILIAR();
            int p2 = posicao;


            while (p1 < p2) {
                s += tabela.get(p1++).token;
            }
        } else {
            erroSintatico("Era esperado um identificador");
        }

        return new ComandoAtivacaoDeProcedimento(s);
    }

    private void ativacao_de_procedimentoAUXILIAR() {
        if (simboloLido.token.equals("(")) {
            obtenha_simbolo();
            lista_de_expressoes();
            obtenha_simbolo();
            if (!simboloLido.token.equals(")")) {
                erroSintatico("Era esperado um caractere ')'");
            }
        } else {
            vazio();
        }
    }

    // RECURSIVO
    // original: LE := E | LE,E
    // Sem recursao a esquerda: LE := E LE'
    //                          LE' := VAZIO | ,E LE'
    private void lista_de_expressoes() {
        expressao();
        obtenha_simbolo();
        lista_de_expressoesLINHA();
    }

    private void lista_de_expressoesLINHA() {
        if (simboloLido.token.equals(",")) {
            obtenha_simbolo();
            expressao();
            obtenha_simbolo();
            lista_de_expressoesLINHA();
        } else {
            vazio();
        }
    }

    //Nao determinismo detectado
    //Original: E := ES | ES OPR ES
    //Melhorado:  E := ES EAUX
    //            EAUX := VAZIO | OPR ES
    private TipoDeDados expressao() {

        TipoDeDados t1 = expressao_simples();
        obtenha_simbolo();
        TipoDeDados t2 = expressaoAUXILIAR();

        if (t2 == null) {
            return t1;
        }
        if (t1 == TipoDeDados.BOOLEAN || t2 == TipoDeDados.BOOLEAN) {
            erroSemantico("Erro semantico na expressão. Tipos incompatíveis. Tentando comparar expressão boleana.");
        }

        return TipoDeDados.BOOLEAN;

    }

    private TipoDeDados expressaoAUXILIAR() {
        if (simboloLido.token.equals("=")
                || simboloLido.token.equals("<")
                || simboloLido.token.equals(">")
                || simboloLido.token.equals("<=")
                || simboloLido.token.equals(">=")
                || simboloLido.token.equals("<>")) {
            op_relacional();
            obtenha_simbolo();
            return expressao_simples();
        } else {
            vazio();
            return null;
        }
    }

    // RECURSIVO
    // original: ES := T | S T | ES OPA T
    // Sem recursao a esquerda: ES := T ES' | S T ES'
    //                          ES' := VAZIO | OPA T ES'
    private TipoDeDados expressao_simples() {
        if (simboloLido.token.equals("+") || simboloLido.token.equals("-")) {
            sinal();
            obtenha_simbolo();
            TipoDeDados t1 = termo();
            if (t1 == TipoDeDados.BOOLEAN) {
                erroSemantico("Incompatibilidade de tipo no uso do SINAl. O tipo era booleano.");
            }
            obtenha_simbolo();
            TipoDeDados t2 = expressao_simplesLINHA();
            if (t2 == TipoDeDados.BOOLEAN) {
                erroSemantico("Incompatibilidade de tipo no uso do SINAl. O tipo era booleano.");
            }

            if (t1 == TipoDeDados.REAL || t2 == TipoDeDados.REAL) {
                return TipoDeDados.REAL;
            } else {
                return TipoDeDados.INTEIRO;
            }
        } else {
            TipoDeDados t1 = termo();
            obtenha_simbolo();
            TipoDeDados t2 = expressao_simplesLINHA();

            if (t2 == null) {
                return t1;
            }
            if (t1 == TipoDeDados.BOOLEAN) {
                if (t2 == TipoDeDados.INTEIRO || t2 == TipoDeDados.REAL) {
                    erroSemantico("Tipo incorreto na expressão simples.");
                }

            }

            if (t2 == TipoDeDados.BOOLEAN) {
                if (t1 == TipoDeDados.INTEIRO || t1 == TipoDeDados.REAL) {
                    erroSemantico("Tipo incorreto na expressão simples.");
                }
            }

            if (t1 == TipoDeDados.BOOLEAN && t2 == TipoDeDados.BOOLEAN) {
                return TipoDeDados.BOOLEAN;
            }

            if (t1 == TipoDeDados.REAL || t2 == TipoDeDados.REAL) {
                return TipoDeDados.REAL;
            } else {
                return TipoDeDados.INTEIRO;
            }

        }
    }

    private TipoDeDados expressao_simplesLINHA() {
        if (simboloLido.token.equals("+") || simboloLido.token.equals("-") || simboloLido.token.equals("or")) {
            op_aditivo();

            boolean apenasNumeros = (simboloLido.token.equals("+") || simboloLido.token.equals("-"));
            obtenha_simbolo();


            TipoDeDados t1 = termo();
            if (apenasNumeros && t1 == TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto na expressao_simplesLINHA. Não era esperado um BOOLEAN.");
            }
            if (!apenasNumeros && t1 != TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto na expressao_simplesLINHA. Não era esperado um INTEIRO/REAL.");
            }
            obtenha_simbolo();
            TipoDeDados t2 = expressao_simplesLINHA();
            if (t2 == null) {
                return t1;
            }

            if (apenasNumeros && t2 == TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto na expressão simples. Não era esperado um BOOLEAN.");
            }
            if (!apenasNumeros && t2 != TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto na expressão simples. Não era esperado um INTEIRO/REAL.");
            }
            if (apenasNumeros) {
                if (t1 == TipoDeDados.REAL || t2 == TipoDeDados.REAL) {
                    return TipoDeDados.REAL;
                } else {
                    return TipoDeDados.INTEIRO;
                }
            } else {
                return TipoDeDados.BOOLEAN;
            }
        } else {
            vazio();
            return null;
        }
    }

    // RECURSIVO
    // original: T := F | T OPM F
    // Sem recursao a esquerda: T := F T'
    //                          T' := VAZIO | OPM F T'
    private TipoDeDados termo() {
        TipoDeDados t1 = fator();
        obtenha_simbolo();
        TipoDeDados t2 = termoLINHA();

        if (t2 == null) {
            return t1;
        }


        if (t1 == TipoDeDados.BOOLEAN) {
            if (t2 == TipoDeDados.INTEIRO || t2 == TipoDeDados.REAL) {
                erroSemantico("Tipo incorreto no termo.");
            }

        }

        if (t2 == TipoDeDados.BOOLEAN) {
            if (t1 == TipoDeDados.INTEIRO || t1 == TipoDeDados.REAL) {
                erroSemantico("Tipo incorreto no termo.");
            }
        }

        if (t1 == TipoDeDados.BOOLEAN && t2 == TipoDeDados.BOOLEAN) {
            return TipoDeDados.BOOLEAN;
        }

        if (t1 == TipoDeDados.REAL || t2 == TipoDeDados.REAL) {
            return TipoDeDados.REAL;
        } else {
            return TipoDeDados.INTEIRO;
        }

    }

    private TipoDeDados termoLINHA() {
        if (simboloLido.token.equals("*") || simboloLido.token.equals("/") || simboloLido.token.equals("and")) {

            boolean apenasNumeros = (simboloLido.token.equals("*") || simboloLido.token.equals("/"));


            op_multiplicativo();
            obtenha_simbolo();
            TipoDeDados t1 = fator();
            if (apenasNumeros && t1 == TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto no termoLINHA. Era esperado um INTEIRO/REAL e não um BOOLEAN");
            }

            if (!apenasNumeros && t1 != TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto no termoLINHA. Era esperado um BOOLEAN e não um INTEIRO/REAL");
            }

            obtenha_simbolo();
            TipoDeDados t2 = termoLINHA();

            if (t2 == null) {
                return null;
            }
            if (apenasNumeros && t2 == TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto no termoLINHA. Não era esperado um BOOLEAN.");
            }
            if (!apenasNumeros && t2 != TipoDeDados.BOOLEAN) {
                erroSemantico("Tipo incorreto no termoLINHA. Não era esperado um INTEIRO/REAL.");
            }
            if (apenasNumeros) {
                if (t1 == TipoDeDados.REAL || t2 == TipoDeDados.REAL) {
                    return TipoDeDados.REAL;
                } else {
                    return TipoDeDados.INTEIRO;
                }
            } else {
                return TipoDeDados.BOOLEAN;
            }

        } else {
            vazio();
            return null;
        }
    }

    private TipoDeDados fator() {
        if (simboloLido.token.equals("true") || simboloLido.token.equals("false") || simboloLido.tipo == Token.NUMEROITEIRO || simboloLido.tipo == Token.NUMEROREAL) {

            if (simboloLido.token.equals("true") || simboloLido.token.equals("false")) {
                return TipoDeDados.BOOLEAN;
            }
            if (simboloLido.tipo == Token.NUMEROITEIRO) {
                return TipoDeDados.INTEIRO;
            }
            return TipoDeDados.REAL;

        } else {
            if (simboloLido.token.equals("not")) {
                obtenha_simbolo();
                TipoDeDados t1 = fator();
                if (t1 != TipoDeDados.BOOLEAN) {
                    erroSemantico("Tipo incorreto no fator. Não era esperado um BOOLEAN junto com o operador 'not'.");
                }
                return TipoDeDados.BOOLEAN;
            } else {
                if (simboloLido.token.equals("(")) {
                    obtenha_simbolo();
                    TipoDeDados t1 = expressao();
                    obtenha_simbolo();
                    if (!simboloLido.token.equals(")")) {
                        erroSintatico("Era esperado o caractere ')'");
                    }
                    return t1;
                } else {
                    if (simboloLido.tipo == Token.IDENTIFICADOR) {
                        // Verifica se o identificador esta no escopo
                        Object pilhaAuxiliar[] = pilha.toArray();
                        boolean possui = false;
                        for (int i = 0; i < pilhaAuxiliar.length; i++) {
                            if (((Linha) pilhaAuxiliar[i]).token.equalsIgnoreCase(simboloLido.token)) {
                                if (verbose) {
                                    System.out.println("Identificador encontrado na pilha: "
                                            + simboloLido.token);
                                }
                                possui = true;
                                break;
                            }
                        }
                        //  Erro: identificador não encontrado
                        if (!possui) {
                            erroSemantico("Identificador não previamente declarado: " + simboloLido.token);
                        }

                        if (simboloLido.tipo == Token.IDENTIFICADOR) {
                            String tok = simboloLido.token;
                            obtenha_simbolo();

                            if (fatorAUXILIAR()) {
                                return null;
                            } else {
                                return pegaTipo(tok);
                            }
                        } else {
                            erroSintatico("Era esperado um FATOR");
                            return null;
                        }
                    } else {
                        erroSintatico("Era esperado um IDENTIFICADOR");
                        return null;
                    }
                }
            }



        }
    }

    //Retorna true false se foi vazio
    private boolean fatorAUXILIAR() {
        if (simboloLido.token.equals("(")) {
            obtenha_simbolo();
            lista_de_expressoes();
            obtenha_simbolo();
            if (!simboloLido.token.equals(")")) {
                erroSintatico("Era esperado o caractere ')'");
            }
            return true;
        } else {
            vazio();
            return false;
        }
    }

    private void sinal() {
        if (simboloLido.token.equals("+") || simboloLido.token.equals("-")) {
            //Tratar SINAL
        } else {
            erroSintatico("Era esperado um sinal '+' ou '-'");
        }
    }

    private void op_relacional() {
        if (simboloLido.token.equals("=")
                || simboloLido.token.equals("<")
                || simboloLido.token.equals(">")
                || simboloLido.token.equals("<=")
                || simboloLido.token.equals(">=")
                || simboloLido.token.equals("<>")) {
            //Tratar OP_RELACIONAL
        } else {
            erroSintatico("Era esperado um operador relacional '=', '<','>','<=','>=' ou '<>'");
        }
    }

    private void op_aditivo() {
        if (simboloLido.token.equals("+") || simboloLido.token.equals("-")
                || simboloLido.token.equals("or")) {
            //Tratar OPERADOR_ADITIVO
        } else {
            erroSintatico("Era esperado um operador aditivo '+', '-' ou 'or'");
        }
    }

    private void op_multiplicativo() {
        if (simboloLido.token.equals("*") || simboloLido.token.equals("/")
                || simboloLido.token.equals("and")) {
            //Tratar OPERADOR_MULTIPLICATIVO
        } else {
            erroSintatico("Era esperado um operador aditivo '*', '/' ou 'and'");
        }
    }

    //Volta uma posição
    private void vazio() {
        posicao = posicao - 2;
        obtenha_simbolo();
    }

    private void insereIdentificadoresNaPilha() {
        for (Linha l : auxiliar) {
            l.tipo = simboloLido.tipo;

            // Pega o ultimo indice do token/linha
            int index = -1;
            // Varre a pilha de cima pra baixo, pois lastIndexOf nao esta
            // rolando:
            for (int i = pilha.size() - 1; i > 0; i--) {
                if (l.token.equalsIgnoreCase(pilha.get(i).token)) {
                    index = i;
                    break;
                }
            }

            // token = -1: identificador nao existe na pilha e pode ser inserido
            if (index == -1) {
                pilha.push(l);
                if (verbose) {
                    System.out.println("--> " + l.token);
                }
            } // identificador existe na pilha:
            else {
                // pega o indice da ultima marca:
                int mark = 0;
                for (int i = pilha.size() - 1; i > 0; i--) {
                    if (pilha.get(i).token.equalsIgnoreCase("$")) {
                        mark = i;
                        break;
                    }
                }

                // Token esta em outro escopo, pode inserir:
                if (index < mark) {
                    pilha.push(l);
                    if (verbose) {
                        System.out.println("--> " + l.token);
                    }
                } // Erro: "Identificador já existe no escopo."
                else {
                    erroSemantico("Identificador já existe no escopo: " + l.token);
                }
            }
        }
    }
}