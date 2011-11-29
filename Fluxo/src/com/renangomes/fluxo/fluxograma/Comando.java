/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.renangomes.fluxo.fluxograma;

/**
 *
 * @author RenanGomes
 */
public abstract class Comando {

    public enum TipoDeComando {
        SIMPLES, WHILE, IF, FOR, ATIVACAODEPROCEDIMENTO
    }

    public abstract TipoDeComando getTipo();
}
