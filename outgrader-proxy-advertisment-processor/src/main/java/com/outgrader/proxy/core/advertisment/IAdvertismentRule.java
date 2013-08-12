package com.outgrader.proxy.core.advertisment;

import com.outgrader.proxy.core.advertisment.response.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRule {

	boolean matches(ITag tag);

}
