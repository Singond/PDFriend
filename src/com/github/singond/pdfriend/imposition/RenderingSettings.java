package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.book.FlipDirection;

class RenderingSettings {

	private final FlipDirection flipDirection;
	
	private RenderingSettings(FlipDirection flip) {
		this.flipDirection = flip;
	}
	
	/** Returns the default rendering settings */
	static RenderingSettings auto() {
		return new Builder().build();
	}
	
	/**
	 * Returns the relationship between the front and back side of the
	 * output document.
	 */
	public FlipDirection getFlipDirection() {
		return flipDirection;
	}

	static class Builder {
		private FlipDirection flipDirection = FlipDirection.AROUND_Y;
		
		public void setFlipDirection(FlipDirection flipDirection) {
			this.flipDirection = flipDirection;
		}

		RenderingSettings build() {
			return new RenderingSettings(flipDirection);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("flip direction: ").append(flipDirection);
		return builder.toString();
	}
}
