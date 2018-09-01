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
 * ATENCAO, ESSE ARQUIVO ESTÁ OBSOLETO VISTO QUE AGORA O PROGRAMA POSSUI UMA GUI
 */

package com.renangomes.fluxo.principal;

import com.renangomes.fluxo.analiselexica.AnalisadorLexico;
import com.renangomes.fluxo.analisesintatica.AnalisadorSintatico;
import com.renangomes.fluxo.utils.Linha;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    /** Se essa variavel for true, faz o analisador lexico imprimir as linhas
     * da tabela enquando as cria.
     */
    public static boolean DEBUG = false;

    /** 
     * Metodo de entrada do programa
     * @param args Os parametros da linha de comando: [-d] nomedoarquivo
     */
    public static void main(String[] args) {
        //Verifica os parametros
        if (args.length < 1) {
            System.out.println("Numero de Parametros invalidos.\nUso correto: java -jar AnalisadorLexico.jar [-d] nome_do_arquivo");
            System.exit(1);
        }
        //Trata o modo debug
        String arquivo = args[0];
        if (args.length == 2) {
            if (args[0].trim().equalsIgnoreCase("-d")) {
                DEBUG = true;
            }
            arquivo = args[1];
        }
        //Verifica se o arquivo existe e pode ser lido. Se sim, cria a tabela
        //O analisador lexico.
        AnalisadorLexico lexico = null;
        List<Linha> tabela = new ArrayList<Linha>();
        try {
            lexico = new AnalisadorLexico(arquivo);
            tabela = lexico.criarTabela();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "O arquivo não foi encontrado. Arquivo: " + arquivo, ex);
            System.out.println("O arquivo não foi encontrado. Arquivo: " + arquivo);
            System.exit(2);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "O arquivo não pôde ser lido. Arquivo: " + arquivo, ex);
            System.out.println("O arquivo não pôde ser lido. Arquivo: " + arquivo);
            System.exit(3);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Ocorreu um erro ao criar a tabela baseada no arquivo: " + arquivo, ex);
            System.out.println("Ocorreu um erro ao criar a tabela baseada no arquivo: " + arquivo);
            System.exit(4);
        }

        // Imprime a tabela final na saida padrao
        System.out.println("ANALISADOR LEXICO");
        System.out.println("A analise léxica ocorreu sem erros e foi gerada uma tabela de simbolos.");
        //imprimirTabela(tabela);
        System.out.println();
        AnalisadorSintatico sintatico = null;
        try {
            System.out.println("ANALISADOR SINTATICO / SEMANTICO");
            sintatico = new AnalisadorSintatico(tabela);
            sintatico.analisar();
            System.out.println("A analise sintática e semântica da tabela ocorreu normalmente, sem erros.");
        } catch (Exception ex) {
            if (DEBUG) {
                System.out.println("Pilha de Execução do Analisador Sintático");
                ex.printStackTrace(System.out);
            }
            System.exit(12);
        }
    }

    /**
     * Imprime a tabela linha a linha usando o metodo "toString()"
     * da classe Linha.
     * @param tabela A lista de linhas a ser impressa.
     */
    private static void imprimirTabela(List<Linha> tabela) {
        System.out.println("NLINHA     TOKEN               TIPO");
        for (Linha ln : tabela) {
            System.out.println(ln);
        }
    }
}
