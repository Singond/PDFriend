package com.github.singond.pdfriend.test;

import com.github.singond.pdfriend.book.Leaf.Orientation;;

public class InvertibleEnum {

	public static void main(String[] args) {
		Orientation o = Orientation.RECTO_UP;
		System.out.println(o);
		System.out.println(o.inverse());
		System.out.println(o.inverse().inverse());
	}
}
