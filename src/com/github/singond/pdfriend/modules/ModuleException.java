package com.github.singond.pdfriend.modules;

/**
 * An exception signifying an error during module operation.
 * @author Singon
 *
 */
public final class ModuleException extends Exception {
	
	private static final long serialVersionUID = -2909204466624598226L;

	private final Module module;
	
	public ModuleException(Module module) {
		super();
		this.module = module;
	};
	
	public ModuleException(Module module, String message) {
		super(message);
		this.module = module;
	}
	
	public ModuleException(Module module, String message, Throwable cause) {
		super(message, cause);
		this.module = module;
	}
	
	public ModuleException(Module module, Throwable cause) {
		super(cause);
		this.module = module;
	}

	public Module getModule() {
		return module;
	}
}
