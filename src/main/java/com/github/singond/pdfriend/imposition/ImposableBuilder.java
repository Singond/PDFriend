package com.github.singond.pdfriend.imposition;

/**
 * A mutable builder for {@code Imposable} objects.
 *
 * @author Singon
 *
 * @param <T> The {@code Imposable} implementation produced by this builder
 */
public interface ImposableBuilder<T extends Imposable<?>> {

	/**
	 * Sets the parameters to be used when pre-processing the pages.
	 * @param settings
	 */
	public ImposableBuilder<T> acceptPreprocessSettings(Preprocessor.Settings settings);
	
	/**
	 * Sets the common imposition settings to be used.
	 * @param settings
	 */
	public ImposableBuilder<T> acceptCommonSettings(CommonSettings settings);
	
	/**
	 * Sets the parameters to be used when rendering the document model
	 * into a virtual document.
	 * @param settings
	 */
	public ImposableBuilder<T> acceptRenderingSettings(RenderingSettings settings);
	
	/**
	 * Builds the {@code Imposable} object.
	 * @return an imposable object
	 */
	public T build();
	
	/**
	 * Builds the imposition task object.
	 * @return an {@code ImpositionTask} object
	 */
	public ImpositionTask buildTask();
}
