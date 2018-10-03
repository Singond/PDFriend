package com.github.singond.pdfriend.imposition;

import com.github.singond.pdfriend.book.FlipDirection;

class RenderingSettings {

	private final boolean twoSided;
	private final FlipDirection flipDirection;
	
	private RenderingSettings(FlipDirection flip, boolean twoSided) {
		this.flipDirection = flip;
		this.twoSided = twoSided;
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
	
	/**
	 * Returns whether two-sided rendering has been explicitly set.
	 * This method is just a matter of style, because flip direction
	 * with the default value has the same effect as one sided render.
	 */
	public boolean isTwoSided() {
		return twoSided;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("flip direction: ").append(flipDirection)
				.append(", two-sided: ").append(twoSided);
		return builder.toString();
	}

	static class Builder {
		private boolean twoSided = false;
		private FlipDirection flipDirection = FlipDirection.AROUND_Y;

		public void setTwoSided(boolean twoSided) {
			this.twoSided = twoSided;
		}

		public void setFlipDirection(FlipDirection flipDirection) {
			this.flipDirection = flipDirection;
		}

		RenderingSettings build() {
			return new RenderingSettings(flipDirection, twoSided);
		}
	}
}
