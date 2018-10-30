package com.github.singond.pdfriend.reorder;

import java.util.List;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;

public class Compact implements Reorderable {

	/** The internal name of this reordering task */
	private static final String NAME = "compact";

	/** Logger */
	private static ExtendedLogger logger = Log.logger(Compact.class);

	private final int sectionLength;

	private final SingleUseCompacter<VirtualDocument> compacter;

	public Compact(int sectionLength) {
		this.sectionLength = sectionLength;
		compacter = new OptimizingFixedSectionCompacter<>
				(sectionLength, d -> d.getLength());
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public VirtualDocument reorder(List<VirtualDocument> sources) {
		List<VirtualDocument> compacted = compacter.process(sources);
		for (VirtualDocument doc : compacted) {
			logger.verbose("compact_appendDoc", doc, doc.getLength());
		}
		VirtualDocument result = VirtualDocument.concatenate(compacted);
		logger.info("compact_summary", compacted.size(), sectionLength);
		logger.info("compact_numberOfSplitDocs", compacter.splitObjects());
		logger.info("compact_numberOfSuboptimDocs",
		            compacter.suboptimSplitObjects());
		return result;
	}
}
