package com.outgrader.proxy.core.advertisment.response.internal;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class TagReader implements Iterable<ITag>, Iterator<ITag>, Closeable {

	private static final int BUFFER_SIZE = 4096;

	private static final int BLOCK_SIZE = 256;

	private final Reader source;

	private ITag currentTag;

	private final StringBuilder textCollector = new StringBuilder();

	private boolean isFinished;

	public TagReader(final InputStream source, final Charset charset) {
		this.source = new BufferedReader(new InputStreamReader(source, charset), BUFFER_SIZE);
	}

	@Override
	public Iterator<ITag> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		try {
			getNextTag();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ITag next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	private ITag getNextTag() throws IOException {
		ITag result = null;

		return result;
	}

	private StringBuilder collectTagText() throws IOException {
		CharBuffer buffer = CharBuffer.allocate(BLOCK_SIZE);

		int result = source.read(buffer);
		buffer.flip();

		isFinished = result == -1;

		return new StringBuilder(buffer);
	}

	@Override
	public void close() throws IOException {
		source.close();
	}
}
