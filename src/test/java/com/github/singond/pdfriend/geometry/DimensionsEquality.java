package com.github.singond.pdfriend.geometry;

import static com.github.singond.pdfriend.geometry.LengthUnits.*;
import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class DimensionsEquality {

	private static Random rnd = new Random();
	private static final LengthUnits[] units = LengthUnits.values();
	private static final int ITERATIONS = 20000;
	
	@Test
	public void simpleEquality() {
		equality(45, 60, MILLIMETRE);
	}
	
//	@Test
	public void randomizedTest() {
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.printf("%06d) ", i);
			randomized();
		}
	}
	
	@Test
	public void randomizedPropertiesTest() {
		final int limit = 3;
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.printf("%06d) ", i);
			contractProperties(limit);
		}
	}
	
	private void randomized() {
		double a = rnd.nextDouble();
		double b = rnd.nextDouble();
		double c = rnd.nextDouble();
		double d = rnd.nextDouble();
		LengthUnit unit1 = units[rnd.nextInt(units.length)];
		LengthUnit unit2 = units[rnd.nextInt(units.length)];
		equality(a, b, unit1);
		equality(c, d, unit2);
		testEquality(a, b, unit1, c, d, unit2);
	}
	
	private void contractProperties(int limit) {
		double a = rnd.nextInt(limit);
		double b = rnd.nextInt(limit);
		double c = rnd.nextInt(limit);
		double d = rnd.nextInt(limit);
		double e = rnd.nextInt(limit);
		double f = rnd.nextInt(limit);
		LengthUnit unit = units[rnd.nextInt(units.length)];
		Dimensions k = new Dimensions(a, b, unit);
		Dimensions l = new Dimensions(c, d, unit);
		Dimensions m = new Dimensions(e, f, unit);
		System.out.println("Testing " + String.join("; ", k.toString(), l.toString(), m.toString()));
		reflexivity(k);
		reflexivity(l);
		reflexivity(m);
		System.out.println("OK: reflexivity");
		symmetry(k, l);
		symmetry(l, m);
		symmetry(k, m);
		System.out.println("OK: symmetry");
		transitivity(k, l, m);
		System.out.println("OK: transitivity");
	}
	
	private void equality(double h, double w, LengthUnit unit) {
		Dimensions a = new Dimensions(h, w, unit);
		Dimensions b = new Dimensions(h, w, unit);
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertFalse(a.equals(null));
		System.out.println("OK: "+a+" equals "+b);
		int hasha = a.hashCode();
		int hashb = b.hashCode();
		assertTrue(hasha == hashb);
		System.out.println("OK: "+hasha+" equals "+hashb);
	}
	
	private void testEquality(double h1, double w1, LengthUnit unit1,
	                          double h2, double w2, LengthUnit unit2) {
		Dimensions a = new Dimensions(h1, w1, unit1);
		Dimensions b = new Dimensions(h2, w2, unit2);
		assertFalse(a.equals(null));
		assertFalse(b.equals(null));
		if (Double.isNaN(h1) || Double.isNaN(w1) || Double.isNaN(h2) || Double.isNaN(w2)) {
			assertFalse(a.equals(b));
			assertFalse(b.equals(a));
			System.out.println("OK: "+a+" is not equal to "+b);
		} else if ((h1 == h2 && w1 == w2 && unit1==unit2)
		            || (h1*unit1.inMetres() == h2*unit2.inMetres()
		                && w1*unit1.inMetres() == w2*unit2.inMetres())) {
			assertTrue(a.equals(b));
			assertTrue(b.equals(a));
			System.out.println("OK: "+a+" equals "+b);
			int hasha = a.hashCode();
			int hashb = b.hashCode();
			assertTrue(hasha == hashb);
			System.out.println("OK: "+hasha+" equals "+hashb);
		} else {
			assertFalse(a.equals(b));
			assertFalse(b.equals(a));
			System.out.println("OK: "+a+" is not equal to "+b);
		}
	}
	
	private void reflexivity(Dimensions a) {
		assertTrue(a.equals(a));
	}
	
	private void symmetry(Dimensions a, Dimensions b) {
		if (a.equals(b))
			assertTrue(b.equals(a));
		else
			assertFalse(b.equals(a));
	}
	
	private void transitivity(Dimensions a, Dimensions b, Dimensions c) {
		if (a.equals(b) && b.equals(c)) {
			assertTrue(a.equals(c));
			assertTrue(c.equals(a));
		}
	}
}
