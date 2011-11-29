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

/**
 *
 * @author Renan Gomes Barreto
 */
public abstract class Comando {

    public enum TipoDeComando {
        SIMPLES, WHILE, IF, FOR, ATIVACAODEPROCEDIMENTO
    }

    public abstract TipoDeComando getTipo();
}
