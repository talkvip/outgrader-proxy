package com.outgrader.proxy.advertisment.rule;

import com.outgrader.proxy.advertisment.processor.internal.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public interface IAdvertismentRule {

	boolean matches(ITag tag);

}
