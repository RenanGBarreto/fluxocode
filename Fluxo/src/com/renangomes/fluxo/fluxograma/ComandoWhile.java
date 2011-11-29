/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.renangomes.fluxo.fluxograma;

import java.util.ArrayList;

/**
 *
 * @author RenanGomes
 */
public class ComandoWhile extends Comando {

    private String condicao = "";
    public ArrayList<Comando> comandos = new ArrayList<Comando>();

    @Override
    public TipoDeComando getTipo() {
        return TipoDeComando.WHILE;
    }

    /**
     * @return the condicao
     */
    public String getCondicao() {
        return condicao;
    }

    /**
     * @param condicao the condicao to set
     */
    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }
}
