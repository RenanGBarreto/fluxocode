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
public class ComandoIf extends Comando {
    
    private String condicao = "";
    public ArrayList<Comando> comandos = new ArrayList<Comando>();
    private boolean possuiElse = false;
    public ArrayList<Comando> comandosElse = new ArrayList<Comando>();
    
    @Override
    public TipoDeComando getTipo() {
        return TipoDeComando.IF;
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

    /**
     * @return the possuiElse
     */
    public boolean isPossuiElse() {
        return possuiElse;
    }

    /**
     * @param possuiElse the possuiElse to set
     */
    public void setPossuiElse(boolean possuiElse) {
        this.possuiElse = possuiElse;
    }
}
