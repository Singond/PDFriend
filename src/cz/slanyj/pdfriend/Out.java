package cz.slanyj.pdfriend;

public class Out {

	public static void line(String string) {
		System.out.println(string);
	}
	
	public static void line(String pattern, Object... args) {
		System.out.println(String.format(pattern, args));
	}
}
