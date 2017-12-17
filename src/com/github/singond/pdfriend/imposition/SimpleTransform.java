package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleData;
import com.github.singond.pdfriend.modules.ModuleDataFactory;
import com.github.singond.pdfriend.modules.ModuleException;

/**
 * The simple transformation module of PDFriend.
 * @author Singon
 *
 */
public class SimpleTransform implements Module {

	/** The name of this module */
	private static final String MODULE_NAME = "Simple transformation";
	
	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(SimpleTransform.class);
	
	/**
	 * The transform task containing all settings needed.
	 */
	private SimpleTransformTask task;
	
	public SimpleTransformTask getTask() {
		return task;
	}

	public void setTask(SimpleTransformTask task) {
		this.task = task;
	}
	
	@Override
	public ModuleData process(ModuleData data) throws ModuleException  {
		logger.info("*** PDFriend Simple Transform ***");
		
		VirtualDocument document;
		document = task.imposeAndRender(data.asSingleDocument());
		return ModuleDataFactory.of(document);
	}

	@Override
	public String name() {
		return MODULE_NAME;
	}
	
}
