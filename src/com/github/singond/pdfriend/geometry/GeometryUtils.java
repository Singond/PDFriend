package com.github.singond.pdfriend.geometry;

public class GeometryUtils {

	private GeometryUtils() {};
	
	public static Dimensions rectanglePlusMargins(Dimensions rectangle, Margins margins) {
		return new Dimensions(
				Length.sum(rectangle.width(), margins.horizontal()),
				Length.sum(rectangle.height(), margins.vertical()));
	}
	
	public static Dimensions rectangleMinusMargins(Dimensions rectangle, Margins margins) {
		return new Dimensions(
				Length.subtract(rectangle.width(), margins.horizontal()),
				Length.subtract(rectangle.height(), margins.vertical()));
	}
}
