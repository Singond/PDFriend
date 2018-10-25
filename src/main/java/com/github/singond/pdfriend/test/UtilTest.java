package com.github.singond.pdfriend.test;

import com.github.singond.pdfriend.Util;

public class UtilTest {
	public static void main(String[] args) {
//		int a = 3;
		int b = -3;
		for (int i=-10; i<10; i++) {
			System.out.print(i+"/"+b+" = ");
			System.out.println(Util.ceilingDivision(i, b));
		}
//		System.out.println(Math.ceil(-2.3));
	}
}
