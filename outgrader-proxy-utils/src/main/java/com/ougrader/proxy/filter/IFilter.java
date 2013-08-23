package com.ougrader.proxy.filter;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public interface IFilter<T extends Object> {

	boolean matches(T exression);

	void addCondition(T condition);
}
