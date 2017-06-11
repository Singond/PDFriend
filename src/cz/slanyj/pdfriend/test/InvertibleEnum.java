package cz.slanyj.pdfriend.test;

import cz.slanyj.pdfriend.book.model.Leaf.Orientation;;

public class InvertibleEnum {

	public static void main(String[] args) {
		Orientation o = Orientation.RECTO_UP;
		System.out.println(o);
		System.out.println(o.inverse());
		System.out.println(o.inverse().inverse());
	}
}
