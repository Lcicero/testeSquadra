package com.br.util.testesquadra.model.dominio;

/**
 * Define o contrato a ser seguido por classes de domínio
 *
 * @author leandro.ferreira
 *
 */
public interface Dominio {

	String getName();

	Integer getCod();

	String getDesc();

	String getLongDesc();

	@Override
	String toString();

}