/** 
 * Aquivo: Linguagem.java
 * Autor: Renan Gomes Barreto
 * Data de Criação: 08:40:28 09/09/2011
 * Data de Modificação: 09/09/2011
 * Codificacao: UTF-8
 */
package com.renangomes.fluxo.utils;

import java.util.regex.Pattern;

/**
 * Esta Classe representa a Linguagem
 * @since 09/09/2011
 * @author Renan Gomes Barreto
 */
public class Linguagem {

    /** Representa um Token da linguagem */
    public static enum Token {
        PALAVRARESERVADA, NUMEROITEIRO, NUMEROREAL, DELIMITADOR, COMANDODEATRIBUICAO,
        OPERADORRELACIONAL, OPERADORADITIVO, OPERADORMUTIPLICATIVO, IDENTIFICADOR
    };
    /** As palavras reservadas da linguagem */
    public static String[] palavrasReservadas = {"program", "var", "integer", "real",
        "boolean", "procedure", "begin", "end", "if", "then", "else", "while",
        "do", "not"};
    /** os delimitadores da linguagem **/
    public static String[] delimitadores = {";", ".", ":", "(", ")", ","};
    /** Operadores de atribuicao */
    public static String[] atribuicao = {":="};
    /** Operadores Relacionais */
    public static String[] opRelacionais = {"=", ">=", "<=", "<>", "<", ">"};
    /** operadores dea adicao */
    public static String[] opAditivos = {"+", "-", "or"};
    /** Operadores de mutiplicacao */
    public static String[] opMutiplicacao = {"*", "/", "and"};
    /** Expressao regular que detecta um numero inteiro */
    public static Pattern padraoNumeroInteiro = Pattern.compile("[0-9]+");
    /** Expressao regular que detecta um numero real */
    public static Pattern padraoNumeroReal = Pattern.compile("[0-9]+\\.[0-9]*");
    /** Expressao regular que aceita apenas identificadores */
    public static Pattern padraoIdentificador = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9_]*");

    /** 
     * Dada uma string, retorna o Token correspondente
     * OBS: Essa função não pode dá TRIM na string passada
     * @param s a String 
     * @return O token. Se a string for nula ou não for um token, retorna null.
     */
    public static Token tranformaEmToken(String s) {
        if (s==null){
            return null; // Se a string for nula, retorna null
        }
        
        //Iniciando as verificações ordenadamente!
        if (isPalavraReservada(s)) {
            return Token.PALAVRARESERVADA;
        }
        if (isNumeroInteiro(s)) {
            return Token.NUMEROITEIRO;
        }
        if (isNumeroReal(s)) {
            return Token.NUMEROREAL;
        }
        if (isAtribuicao(s)) {
            return Token.COMANDODEATRIBUICAO;
        }
        if (isOperacaoRelacional(s)) {
            return Token.OPERADORRELACIONAL;
        }
        if (isOperacaoAditiva(s)) {
            return Token.OPERADORADITIVO;
        }
        if (isOperacaoMutiplicativa(s)) {
            return Token.OPERADORMUTIPLICATIVO;
        }
        if (isIdentificador(s)) {
            return Token.IDENTIFICADOR;
        }
        if (isDelimitador(s)) {
            return Token.DELIMITADOR;
        }
        return null; // Se a string não for um token, retorna null
    }
    
    /**
     * Verifica se a string é um token qualquer.
     * @param string A string a ser verificada  
     * @return true se for um token qualquer
     */
    public static boolean isAToken(String string) {
        return (tranformaEmToken(string) != null);
    }

    /**
     * Verifica que se string passada é uma palavra chave da linguagem
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isPalavraReservada(String x) {
        for (String p : palavrasReservadas) {
            if (p.equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que se string passada é um delimitador da linguagem
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isDelimitador(String x) {
        for (String p : delimitadores) {
            if (p.equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que se string passada é uma operacao de atribuicao
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isAtribuicao(String x) {
        for (String p : atribuicao) {
            if (p.equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que a string passada é uma operacao relacional
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isOperacaoRelacional(String x) {
        for (String p : opRelacionais) {
            if (p.equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que se string passada é uma operacao aditiva
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isOperacaoAditiva(String x) {
        for (String p : opAditivos) {
            if (p.equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que se string passada é uma operacao mutiplicativa
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isOperacaoMutiplicativa(String x) {
        for (String p : opMutiplicacao) {
            if (p.equals(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica que se string passada é um numero inteiro
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isNumeroInteiro(String x) {
        return padraoNumeroInteiro.matcher(x).matches();
    }

    /**
     * Verifica que se string passada é um numero real
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isNumeroReal(String x) {
        return padraoNumeroReal.matcher(x).matches();
    }

    /**
     * Verifica que se string passada é um numero real
     * @param x A string a ser verificada
     * @return true se for, false se não for.
     */
    public static boolean isIdentificador(String x) {
        return padraoIdentificador.matcher(x).matches();
    }
}
