package com.github.singond.pdfriend.book.control;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.model.LayeredPage;
import com.github.singond.pdfriend.book.model.MultiPage.Pagelet;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.document.VirtualPage;

public class LayerSourceProvider implements SourceProvider<LayeredPage>{
	
	/**
	 * Queues of pages, each of which will fill one layer.
	 * The layers are filled in the order of these queues.
	 */
	private final List<Queue<VirtualPage>> sourcePages;
	/** The cached number of layers */
	private final int layers;
	/** Logger instance */
	private static final ExtendedLogger logger = Log.logger(LayerSourceProvider.class);
	
	public LayerSourceProvider(List<VirtualDocument> documents) {
		this.layers = documents.size();
		List<Queue<VirtualPage>> srcList = new ArrayList<>(layers);
		for (VirtualDocument doc : documents) {
			srcList.add(new ArrayDeque<>(doc.getPages()));
		}
		this.sourcePages = srcList;
	}

	@Override
	public void setSourceTo(Iterable<LayeredPage> pages) {
		for (LayeredPage pg : pages) {
			setSourceTo(pg);
		}
		
	}

	@Override
	public void setSourceTo(LayeredPage page) {
		int i = 0;
		for (Pagelet layer : page.getLayers()) {
			if (i < layers) {
				Queue<VirtualPage> pageQueue = sourcePages.get(i);
				if (!pageQueue.isEmpty())
					layer.setSource(pageQueue.remove());
				else
					logger.warn("layerSP_queueEmpty", i);
			} else {
				logger.warn("layerSP_tooManyLayersInPage",
				            page.numberOfLayers(), layers, i);
			}
		}
		
	}

}
