package com.github.singond.pdfriend.book;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.book.MultiPage.PageletView;
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
	/** Stores information whether empty warning has been issued for ith layer */
	private final boolean[] queueEmptyWarningIssued;
	
	/** Logger instance */
	private static final ExtendedLogger logger = Log.logger(LayerSourceProvider.class);
	
	
	public LayerSourceProvider(List<VirtualDocument> documents) {
		this.layers = documents.size();
		queueEmptyWarningIssued = new boolean[layers];
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
		int layersInPage = page.numberOfLayers();
		if (layersInPage > layers) {
			logger.debug("layerSP_tooManyLayersInPage", layersInPage, layers);
		}
		int layerNo = 0;
		for (PageletView layer : page.getLayers()) {
			if (layerNo < layers) {
				Queue<VirtualPage> pageQueue = sourcePages.get(layerNo);
				if (!pageQueue.isEmpty()) {
					layer.setSource(pageQueue.remove());
				} else if (!queueEmptyWarningIssued[layerNo]) {
					logger.warn("layerSP_queueEmpty", layerNo, page);
					queueEmptyWarningIssued[layerNo] = true;
				}
			}
			layerNo++;
		}
		
	}

}
