package com.outgrader.proxy.advertisment.processor.internal.impl.utils;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.12-SNAPSHOT
 * 
 */
public final class ByteUtils {

	private ByteUtils() {

	}

	public static byte[] append(final byte[] original, final byte[] toAppend) {
		if (ArrayUtils.isEmpty(toAppend)) {
			return original;
		}

		byte[] result = Arrays.copyOf(original, original.length + toAppend.length);

		System.arraycopy(toAppend, 0, result, original.length, toAppend.length);

		return result;
	}

}
