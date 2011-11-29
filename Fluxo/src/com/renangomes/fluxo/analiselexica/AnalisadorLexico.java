/** 
 * Aquivo: AnalisadorLexico.java
 * Autor: Renan Gomes Barreto
 * Data de Criação: 08:22:02 09/09/2011
 * Data de Modificação: 09/09/2011
 * Codificacao: UTF-8
 */
package com.renangomes.fluxo.analiselexica;

import com.renangomes.fluxo.principal.Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.renangomes.fluxo.utils.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta Classe representa o Analisador Lexico
 * @since 09/09/2011
 * @author Renan Gomes Barreto
 */
public class AnalisadorLexico {

    public String ultimoErro = "";
    public int ultimalinhaerro = -1;
    /** Indica o nome do arquivo que contém o código fonte a ser analisado */
    private File arquivo = null;
    /** Codigo do erro apresentado na tela quando é encontrado um token
     * que nao pertence a linguagem. */
    private final int SIMBNAOPERTENCE = 100;
    /** Codigo do erro apresentado na tela quando um comentario
     * não é fechado */
    private final int COMMNAOFECHADO = 101;
    /** Fechado que nuca foi aberto */
    private final int FECHACOMENTARIOINEXISTENTE = 102;

    /** Construtor padrão privado. */
    public AnalisadorLexico() {
        ultimoErro = "";
    }

    /**
     * Contrutor que será usado no programa.
     * @param arqNome O nome do arquivo com o código fonte do programa
     * @throws FileNotFoundException Quando o arquivo não é encontrado
     * @throws IOException Quando o arquivo é um diretório ou não é possivel
     * fazer sua leitura
     */
    public AnalisadorLexico(String arqNome) throws FileNotFoundException, IOException {
        ultimoErro = "";
        arquivo = new File(arqNome);
        if (!arquivo.exists()) {
            throw new FileNotFoundException("O nome de arquivo passado como"
                    + " parametro não corresponde a um arquivo existente. Arquivo: "
                    + arqNome);
        }
        if (!arquivo.canRead() || arquivo.isDirectory()) {
            throw new IOException("O arquivo existe porém não pode ser lido pelo"
                    + " programa ou é um diretório. Verifique as permissões. Arquivo: "
                    + arqNome);
        }
    }

    /** 
     * Dada um string, essa função tenta tranforma-la em token, se o token for válido,
     * retorna uma intancia da classe linha. Se o token não existir, cria um 
     * erro de compilação usando a função erroCompilaca() e, consequentemente, 
     * finaliza o programa.
     * @param simbolo A string a ser testada
     * @param ln O numero da linha onde a string estava
     * @param nchar O numero do caractere, na linha, onde a string termina.
     * @return Uma instancia da classe Linha, se o token existir.
     *         Se não existir, chama a funcao erroCompilaca()
     */
    private Linha tratatoken(String simbolo, int ln, int nchar, int ctotal) {
        Linguagem.Token t = Linguagem.tranformaEmToken(simbolo);
        if (t == null) {
            erroCompilacao(simbolo, ln, nchar, SIMBNAOPERTENCE);
        } else {
            Linha l = new Linha(simbolo, t, ln, ctotal);
            if (Main.DEBUG) {
                System.err.println(l);
            }
            return l;
        }
        return null;
    }

    /** 
     * Apresenta um erro na tela informando seu tipo, linha e caractere onde foi
     * encontrado. Depois disso, finaliza o programa com o código do erro.
     * @param simb A  string encontrada no codigo fonte
     * @param linha A linha onde a string estava
     * @param nchar O numero do caractere, dentro da linha, onde o erro foi
     *              detectado.
     * @param tipo O tipo do erro. (Pode ser SIMBNAOPERTENCE ou COMMNAOFECHADO)
     */
    private void erroCompilacao(String simb, int linha, int nchar, int tipo) {
        ultimoErro = "";
        ultimoErro += ("ERRO DE COMPILAÇÃO (ANALISADOR LEXICO)\n");
        ultimoErro += ("LINHA: " + linha + ", CARACTERE: " + Math.max(0, nchar - 1) + ".\nDescrição: ");
        switch (tipo) {
            case SIMBNAOPERTENCE:
                ultimoErro += ("O simbolo \"" + simb + "\" não pertence a linguagem.\n");
                break;
            case COMMNAOFECHADO:
                ultimoErro += ("Era esperado o fechamento do comentário '}'. Foi encontrado o fim do arquivo.\n");
                break;
            case FECHACOMENTARIOINEXISTENTE:
                ultimoErro += ("Foi encontrado o simbolo '}' (fechamento de comentário) desnecessario.\n");
                break;
            default:
                ultimoErro += ("Um erro de compilação desconhecido ocorreu.\n");
                break;
        }
        ultimalinhaerro = linha;
        throw new RuntimeException(ultimoErro);

    }

    public List<Linha> criarTabela() throws Exception {
        // Ler o arquivo com o código fonte completamente
        FileInputStream stream = new FileInputStream(arquivo);
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);
        String ln = null;
        String programa = "";
        while ((ln = reader.readLine()) != null) {
            programa += ln + '\n';
        }
        reader.close();
        streamReader.close();
        stream.close();
        return criarTabela(programa);
    }

    /**
     * Ler o codigo do programa do arquivo completamente e depois faz uma busca
     * rdenada or tokens.
     * Algoritmo Basico : Usa um buffer (iniciamente vazio) e vai adicionando
     * carateres um a um. Quando, ao adicionar um caractere, a string no buffer
     * deixar de ser um tipo qualquer de token. Retira o caractere adicionado e
     * chama funcao trata token que irá adicionar o token corretamente na tebala OU,
     * se o token nao existir, gerar um erro SIMBNAOPERTENCE.
     * Essa função tambem trata comentários (gerando erro se ele não forem fechados
     * corretamente), espaços, tabulações e quebras de linhas.
     * @return Uma lista de linhas (uma tabela)
     * @throws Exception Se qualquer coisa errada acontecer...
     */
    public List<Linha> criarTabela(String programa) throws Exception {
        //Cria uma "tabela" vazia na memória
        ArrayList<Linha> tabela = new ArrayList<Linha>();



        String TokenTemporario = ""; //usado como buffer
        int contLinhas = 1; //Conta a linha atual
        int contChar = 1; // Conta o caractere atual dentro da linha
        boolean comentario = false; //Usado no tratamento do comentários

        programa = programa.replaceAll("\\r", "");
        
        for (int i = 0; i < programa.length(); contChar++, i++) {

            //Tratamento de comentários
            if (programa.charAt(i) == '}' && !comentario) {
                erroCompilacao("{", contLinhas, contChar, FECHACOMENTARIOINEXISTENTE);
            }

            if (programa.charAt(i) == '{') {
                if (!TokenTemporario.isEmpty()) {
                    tabela.add(tratatoken(TokenTemporario, contLinhas, contChar, i));
                    TokenTemporario = "";
                }
                comentario = true;
                continue;
            }
            // Se achar um } e estiver com um comentário aberto
            if (comentario && programa.charAt(i) == '}') {
                comentario = false;
                continue;
            }
            // Se chegar ao fim do arquivo e o comentário não estiver fechado
            if (comentario == true) {
                if (i + 1 == programa.length()) {
                    erroCompilacao("", contLinhas, contChar, COMMNAOFECHADO);
                }
                //Trata quebras de linha dentro de comentários 
                if (programa.charAt(i) == '\n') {
                    contLinhas++;
                    contChar = 0;
                }
                continue;
            }
            //Tratamento de quebras de linha
            if (programa.charAt(i) == '\n') {
                // Se tiver uma quebra de linha logo aposum token.
                if (!TokenTemporario.isEmpty()) {
                    tabela.add(tratatoken(TokenTemporario, contLinhas, contChar, i));
                    TokenTemporario = "";
                }
                contLinhas++;
                contChar = 0;
                continue;
            }
            //Tratamento de tokens
            //Se o buffer NÃO FOR vazio E com o novo caractere ele deixe de ser um token:
            //O token antigo* obrigatoriamente tem que ser um token da linguagem.
            //Se não for, lascou (erro de compilação).
            if (!TokenTemporario.isEmpty() && !Linguagem.isAToken(TokenTemporario + programa.charAt(i))
                    && trataIdentificador(TokenTemporario, programa.charAt(i))) {
                tabela.add(tratatoken(TokenTemporario, contLinhas, contChar, i));
                TokenTemporario = ""; // Zera o buffer
            }
            // Ignora espaços, tabulações, etc. E, adiciona o char atual no buffer.
            if (!(programa.charAt(i) == ' ' || programa.charAt(i) == '\r' || programa.charAt(i) == '\t')) {
                TokenTemporario += programa.charAt(i);
            }
        } // Fim do for
        return tabela;
    }

    /**
     * Corrige o bug quando uma variável é definida usando numero inteiro em seu incio
     * @param TokenTemporario o otoken já lido
     * @param charAt O proximo char
     * @return true se a string for numeros seguidos de um identificador
     */
    private boolean trataIdentificador(String TokenTemporario, char charAt) {
        if (Linguagem.isNumeroInteiro(TokenTemporario) || Linguagem.isNumeroReal(TokenTemporario)) {
            return !(Linguagem.isIdentificador(Character.toString(charAt)));
        }
        return true;
    }
}