package com.github.singond.pdfriend.reorder;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleData;
import com.github.singond.pdfriend.modules.ModuleDataFactory;
import com.github.singond.pdfriend.modules.ModuleException;

/**
 * The reordering module of PDFriend.
 * @author Singon
 *
 */
public class Reordering implements Module {

	/**
	 * The imposition task containing all settings needed.
	 * These include preprocessor settings and settings specific to this
	 * imposition type.
	 */
	private Reorderable task;

	/** The name of this module */
	private static final String MODULE_NAME = "Reordering";

	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(Reordering.class);

	public Reorderable getTask() {
		return task;
	}

	public void setTask(Reorderable task) {
		this.task = task;
	}

	@Override
	public ModuleData process(ModuleData data) throws ModuleException  {
		logger.info("*** PDFriend Reorder ***");
		if (task == null) {
			throw new NullPointerException("No reordering type has been specified");
		}
		logger.verbose("Selected reordering type is: " + task.getName());

		VirtualDocument document;
		document = task.reorder(data.asMultipleDocuments());
		return ModuleDataFactory.of(document);
	}

	@Override
	public String name() {
		return MODULE_NAME;
	}

}
