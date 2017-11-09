package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.imposition.Preprocessor.Settings;

/**
 * A skeletal implementation of a builder for Imposable objects.
 */
abstract class AbstractImposableBuilder<T extends Imposable>
		implements ImposableBuilder<T> {

	protected Preprocessor.Settings preprocess = Preprocessor.Settings.auto();
	protected CommonSettings common = CommonSettings.auto();
	
	@Override
	public ImposableBuilder<T> acceptPreprocessSettings(Settings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Preprocess settings cannot be null");
		this.preprocess = settings;
		return this;
	}
	
	@Override
	public ImposableBuilder<T> acceptCommonSettings(CommonSettings settings) {
		if (settings == null)
			throw new IllegalArgumentException("Settings cannot be null");
		this.common = settings;
		return this;
	}
}
