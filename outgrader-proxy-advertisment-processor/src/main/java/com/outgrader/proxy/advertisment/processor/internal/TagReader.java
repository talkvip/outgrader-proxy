package com.outgrader.proxy.advertisment.processor.internal;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.outgrader.proxy.advertisment.processor.internal.impl.Tag.TagBuilder;
import com.outgrader.proxy.core.model.ITag;

/**
 * @author Nikolay Lagutko (nikolay.lagutko@mail.com)
 * @since 0.3.0-SNAPSHOT
 * 
 */
public class TagReader implements Iterable<ITag>, Iterator<ITag>, Closeable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TagReader.class);

	private static final int BUFFER_SIZE = 4096;

	private static final int BLOCK_SIZE = 256;

	private final Reader source;

	private ITag currentTag;

	private StringBuilder textCollector = new StringBuilder();

	private StringBuilder currentTextPiece;

	private boolean isFinished;

	private boolean needMoreData = true;

	private ITag parent;

	private final Map<String, ITag> unclosedTags = new HashMap<>();

	public TagReader(final InputStream source, final Charset charset) {
		this.source = new BufferedReader(
				new InputStreamReader(source, charset), BUFFER_SIZE);
	}

	@Override
	public Iterator<ITag> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if (currentTag == null) {
			while ((currentTag == null) && !isFinished) {
				try {
					currentTag = getNextTag();

					if ((currentTag != null) && currentTag.isAnalysable()) {
						switch (currentTag.getTagType()) {
						case OPENING:
							currentTag.setParent(parent);

							unclosedTags.put(currentTag.getName(), currentTag);

							parent = currentTag;
							break;
						case CLOSING:
							currentTag.setOpeningTag(unclosedTags
									.remove(currentTag.getName()));

							if (currentTag.getOpeningTag() != null) {
								parent = currentTag.getOpeningTag().getParent();
							}

							currentTag.setParent(parent);

							break;
						case OPEN_AND_CLOSING:
							currentTag.setParent(parent);

							break;
						}
					}
				} catch (IOException e) {
					LOGGER.error("An error occured during parsing next Tag", e);
					return false;
				}
			}
		}

		return currentTag != null;
	}

	@Override
	public ITag next() {
		if (currentTag == null) {
			hasNext();

			if (currentTag == null) {
				throw new NoSuchElementException("There is no next Tag");
			}
		}

		ITag result = currentTag;
		currentTag = null;

		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Remove operation is not supported in TagReader");
	}

	private ITag getNextTag() throws IOException {
		ITag result = null;
		TagBuilder builder = null;

		boolean tagStarted = false;

		while ((result == null) && !isFinished) {
			if (needMoreData) {
				currentTextPiece = collectTextPiece();
				needMoreData = false;
			}

			if (tagStarted) {
				int endTagIndex = currentTextPiece.indexOf(">");

				if (endTagIndex >= 0) {
					textCollector.append(currentTextPiece.subSequence(0,
							endTagIndex + 1));

					result = builder.withText(textCollector.toString()).build();
					textCollector = new StringBuilder();

					currentTextPiece = new StringBuilder(
							currentTextPiece.subSequence(endTagIndex + 1,
									currentTextPiece.length()));
				} else {
					textCollector.append(currentTextPiece);
					needMoreData = true;
				}
			} else {
				int startTagIndex = currentTextPiece.indexOf("<");

				if (startTagIndex < 0) {
					textCollector.append(currentTextPiece);
					needMoreData = true;
				} else if ((startTagIndex == 0)
						&& (textCollector.length() == 0)) {
					tagStarted = true;
					builder = TagBuilder.create();
				} else {
					textCollector.append(currentTextPiece.subSequence(0,
							startTagIndex));

					result = TagBuilder.createSpaceTag()
							.withText(textCollector.toString()).build();
					textCollector = new StringBuilder();

					currentTextPiece = new StringBuilder(
							currentTextPiece.subSequence(startTagIndex,
									currentTextPiece.length()));
				}
			}
		}

		if ((result == null) && (textCollector.length() > 0)) {
			result = TagBuilder.createSpaceTag()
					.withText(textCollector.toString()).build();
		}

		return result;
	}

	private StringBuilder collectTextPiece() throws IOException {
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
