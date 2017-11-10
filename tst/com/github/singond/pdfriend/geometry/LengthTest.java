package com.github.singond.pdfriend.geometry;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class LengthTest {

	@Test
	public void simpleTest() {
		Length a = new Length(12, LengthUnits.METRE);
		Assert.assertEquals(120, a.in(LengthUnits.DECIMETRE), 0.001);
		Assert.assertEquals(1200, a.in(LengthUnits.CENTIMETRE), 0.001);
		Assert.assertEquals(12000, a.in(LengthUnits.MILLIMETRE), 0.001);
	}
	
	private void selfEquality(double value, LengthUnit unit) {
		Length length = new Length(value, unit);
		Assert.assertEquals(length.in(unit), value, 1e-12);
		System.out.println("Self-equality OK: " + length + " is equal to " + value + " " + unit.nameInPlural());
	}
	
	@Test
	public void selfEquality() {
		Random rnd = new Random();
		for (LengthUnit unit : LengthUnits.values()) {
			for (int i=0; i<1000; i++) {
				selfEquality(rnd.nextDouble(), unit);
			}
		}
	}
}
