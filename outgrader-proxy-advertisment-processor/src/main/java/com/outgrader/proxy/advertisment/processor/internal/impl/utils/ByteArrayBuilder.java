package com.outgrader.proxy.advertisment.processor.internal.impl.utils;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.4.12-SNAPSHOT
 * 
 */
public final class ByteArrayBuilder {

	private static final int DEFAULT_BLOCK_SIZE = 4096;

	private final int blockSize;

	private byte[] array = ArrayUtils.EMPTY_BYTE_ARRAY;

	private int currentSize = 0;

	private int nextSize;

	public ByteArrayBuilder(final int blockSize) {
		this.blockSize = blockSize;
		this.nextSize = 0;
	}

	public ByteArrayBuilder() {
		this(DEFAULT_BLOCK_SIZE);
	}

	public ByteArrayBuilder append(final byte[] content) {
		int nextCurrentSize = content.length + currentSize;
		while (nextCurrentSize > nextSize) {
			nextSize += blockSize;
			array = Arrays.copyOf(array, nextSize);
		}

		System.arraycopy(content, 0, array, currentSize, content.length);
		currentSize = nextCurrentSize;

		return this;
	}

	public byte[] build() {
		array = Arrays.copyOf(array, currentSize);

		return array;
	}

}
