package com.github.singond.pdfriend.imposition;

import java.util.List;

import com.github.singond.pdfriend.book.Book;
import com.github.singond.pdfriend.document.VirtualDocument;

abstract class AbstractImposable<T extends Book> implements Imposable<T> {

//	@Override
//	public VirtualDocument imposeAndRender(VirtualDocument source) {
//		return impose(source).renderDocument();
//	}
//
//	@Override
//	public VirtualDocument imposeAndRender(List<VirtualDocument> sources) {
//		return impose(sources).renderDocument();
//	}
	
	/**
	 * Returns a new builder of {@code PageSource} objects initialized to the
	 * values inferred from common settings.
	 * @param commonSettings
	 * @param doc
	 * @return
	 */
	protected PageSource.Builder pageSourceBuilder(
			CommonSettings commonSettings, VirtualDocument doc) {
		return initPageSourceBuilder(PageSource.of(doc), commonSettings);
	}
	
	/**
	 * Initializes a builder according to the given settings.
	 * @param builder the builder to be initialized
	 * @param commonSettings settings to be used
	 * @return the same builder as in {@code builder}, initialized with {@code commonSettings}
	 */
	private PageSource.Builder initPageSourceBuilder(
			PageSource.Builder builder, CommonSettings commonSettings) {
		builder.setPageRepeated(commonSettings.getRepeatPage());
		builder.setDocRepeated(commonSettings.getRepeatDocument());
//		if (!commonSettings.isAutoPageCount())
//			builder.setPageRange(0, commonSettings.getPageCount());
		return builder;
	}
}
