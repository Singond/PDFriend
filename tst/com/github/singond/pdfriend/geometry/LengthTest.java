package com.github.singond.pdfriend.geometry;

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
}
