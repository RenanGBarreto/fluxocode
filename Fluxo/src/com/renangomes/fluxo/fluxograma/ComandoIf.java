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
