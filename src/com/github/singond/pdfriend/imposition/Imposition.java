package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.ExtendedLogger;
import com.github.singond.pdfriend.Log;
import com.github.singond.pdfriend.document.VirtualDocument;
import com.github.singond.pdfriend.geometry.LengthUnit;
import com.github.singond.pdfriend.geometry.LengthUnits;
import com.github.singond.pdfriend.modules.Module;
import com.github.singond.pdfriend.modules.ModuleData;
import com.github.singond.pdfriend.modules.ModuleDataFactory;
import com.github.singond.pdfriend.modules.ModuleException;

/**
 * The imposition module of PDFriend.
 * @author Singon
 *
 */
public class Imposition implements Module {

	/**
	 * The imposition task containing all settings needed.
	 * These include preprocessor settings and settings specific to this
	 * imposition type.
	 */
	private Imposable task;
	
	/** The name of this module */
	private static final String MODULE_NAME = "Imposition";
	
	/** The unit used in working with book object model */
	public static final LengthUnit LENGTH_UNIT = LengthUnits.POINT_POSTSCRIPT;
	
	/** Logger instance */
	private static ExtendedLogger logger = Log.logger(Imposition.class);
	
	public Imposable getTask() {
		return task;
	}

	public void setTask(Imposable task) {
		this.task = task;
	}

	@Override
	public ModuleData process(ModuleData data) throws ModuleException  {
		logger.info("*** PDFriend Impose ***");
		if (task == null) {
			throw new NullPointerException("No imposition type has been specified");
		}
		logger.verbose("Selected imposition type is: " + task.getName());
		
		VirtualDocument document;
		if (task.prefersMultipleInput()) {
			document = task.imposeAndRender(data.asMultipleDocuments());
		} else {
			document = task.imposeAndRender(data.asSingleDocument());
		}
		return ModuleDataFactory.of(document);
	}

	@Override
	public String name() {
		return MODULE_NAME;
	}
	
}
