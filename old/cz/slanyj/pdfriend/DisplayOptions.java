package cz.slanyj.pdfriend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class DisplayOptions {
	
	public static Color SHEET_COLOR = Color.WHITE;
	public static Color SHEET_BLEED_COLOR = Color.LIGHT_GRAY;
	public static Color SHEET_MEDIABOX_COLOR = Color.GRAY;
	public static Color PAGE_COLOR = Color.YELLOW;
	public static Color PAGE_BLEED_COLOR = new Color(0xff, 0xff, 0x80);
	public static Color PAGE_MEDIABOX_COLOR = Color.LIGHT_GRAY;
	
	public static Color SHEET_BORDER_COLOR = Color.BLACK;
	public static Color SHEET_BLEED_BORDER_COLOR = Color.BLACK;
	public static Color SHEET_MEDIABOX_BORDER_COLOR = Color.BLACK;
	public static Color PAGE_BORDER_COLOR = Color.BLACK;
	public static Color PAGE_BLEED_BORDER_COLOR = Color.BLACK;
	public static Color PAGE_MEDIABOX_BORDER_COLOR = Color.BLACK;
	
	public static Stroke DASHED = new BasicStroke(
		1.0f,
		BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER,
		10.0f,
		new float[]{2f, 10f},
		0.0f
	);
	
	public static Stroke SHEET_BORDER_STROKE = DASHED;
	public static Stroke SHEET_BLEED_BORDER_STROKE = DASHED;
	public static Stroke SHEET_MEDIABOX_BORDER_STROKE = new BasicStroke();
	public static Stroke PAGE_BORDER_STROKE = DASHED;
	public static Stroke PAGE_BLEED_BORDER_STROKE = DASHED;
	public static Stroke PAGE_MEDIABOX_BORDER_STROKE = new BasicStroke();
	
	public static Color SHADOW_COLOR = Color.GRAY;
	public static int SHADOW_OFFSET_X = 8;
	public static int SHADOW_OFFSET_Y = 8;
}
