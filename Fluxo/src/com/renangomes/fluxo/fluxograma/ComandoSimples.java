/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.renangomes.fluxo.fluxograma;

/**
 *
 * @author RenanGomes
 */
public class ComandoSimples extends Comando {

    private String texto = "";

    private ComandoSimples() {
    }

    public ComandoSimples(String t) {
        texto = t;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return getTexto();
    }

    @Override
    public TipoDeComando getTipo() {
        return TipoDeComando.SIMPLES;
    }
}
