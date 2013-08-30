package com.outgrader.proxy.core.advertisment.filter;


/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public interface IFilterBuilder {

	IFilter build(String rule);

}
