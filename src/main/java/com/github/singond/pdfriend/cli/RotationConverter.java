package com.github.singond.pdfriend.cli;

import com.github.singond.pdfriend.geometry.Angle;

// TODO: This should not be a subtype of AngleConverter.
// Use composition instead.
/**
 * Parses a string as a page rotation.
 * If there are no units given, degrees are assumed.
 *
 * @author Singon
 */
public class RotationConverter extends AngleConverter {

	private static enum Quadrants {
		UPRIGHT ("upright", new Angle(0)),
		DOWN ("down", new Angle(Math.PI)),
		LEFT ("left", new Angle(Math.PI/2)),
		RIGHT ("right", new Angle(-Math.PI/2));

		private final String valueName;
		private final Angle value;

		private Quadrants(String name, Angle value) {
			this.valueName = name;
			this.value = value;
		}
	}

	@Override
	public Angle convert(String arg) {
		for (Quadrants a : Quadrants.values()) {
			if (a.valueName.equals(arg)) {
				return a.value;
			}
		}
		return super.convert(arg);
	}
}
