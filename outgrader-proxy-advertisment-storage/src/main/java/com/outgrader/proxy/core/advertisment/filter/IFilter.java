package com.outgrader.proxy.core.advertisment.filter;

import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.0-SNAPSHOT
 * 
 */
public interface IFilter {

	boolean matches(String uri, ITag tag);

}
