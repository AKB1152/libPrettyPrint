package nz.akb.lib.pretty_print;

import java.util.HashSet;
import java.util.Set;

public record ANSI (int...codes) {

	private static final ANSI EMPTY = new ANSI ();

	public static final ANSI RESET  = new ANSI (0);
	public static final ANSI BOLD   = new ANSI (1);
	public static final ANSI FAINT  = new ANSI (2);
	public static final ANSI ITALIC = new ANSI (3);

	// colors
	public static final ANSI DEFAULT = new ANSI(39);
	public static final ANSI YELLOW = new ANSI(33);
	public static final ANSI PURPLE = new ANSI(35);
	public static final ANSI BLACK = new ANSI(30);
	public static final ANSI GREEN = new ANSI(32);
	public static final ANSI WHITE = new ANSI(37);
	public static final ANSI BLUE = new ANSI(34);
	public static final ANSI CYAN = new ANSI(36);
	public static final ANSI RED = new ANSI(31);

	public static ANSI add (ANSI arg0, ANSI arg1) {
		int[] codes = new int[arg0.codes.length + arg1.codes.length];
		System.arraycopy(arg0.codes, 0, codes,         0, arg0.codes.length);
		System.arraycopy(arg1.codes, 0, codes, arg0.codes.length, arg1.codes.length);

		return new ANSI(codes);
	}

	/**
	 * DO NOT ADD RGB Color Codes
	 * @param ansi list of ANSI Escapes
	 * @return an ANSI Escape with all codes in it
	 */
	public static ANSI add (ANSI...ansi) {
		if (ansi.length == 0) return ANSI.EMPTY;
		if (ansi.length == 1) return ansi[0];

		for (int i = 0; i < ansi.length; i++)
			if (ansi[i].codes[0] == 38 || ansi[i].codes[0] == 48)
				ansi[i] = ANSI.EMPTY;


		int len = 0;
		for (int i = 0; i < ansi.length; i++)
			len += ansi[0].codes.length;

		int q = 0;
		int[] codes = new int[len];
		for (ANSI value : ansi)
			for (int j = 0; j < value.codes.length; j++, q++)
				codes[q] = value.codes[j];

		return new ANSI(merge(codes));
	}

	public static ANSI font (Font font) {
		return new ANSI(10 + font.ordinal());
	}

	public static String font (String text, Font font) {
		return String.format("%s%s%s", ANSI.font(font), text, ANSI.font(Font.DEFAULT));
	}

	public static ANSI color (int r, int g, int b, boolean isBackground) {
		return ANSI.color(new java.awt.Color(r, g, b), isBackground);
	}

	public static ANSI color (int r, int g, int b) {
		return ANSI.color (r, g, b, false);
	}

	public static ANSI color (int rgb, boolean isBackground) {
		return ANSI.color(new java.awt.Color(rgb), isBackground);
	}

	public static ANSI color (int rgb) {
		return ANSI.color (rgb, false);
	}

	public static ANSI color (java.awt.Color color, boolean isBackground) {
		return new ANSI(isBackground? 48 : 38, 2, color.getRed(), color.getGreen(), color.getBlue());
	}

	public static ANSI color (java.awt.Color color) {
		return ANSI.color (color, false);
	}

	public static ANSI color (ANSI.Color color) {
		return ANSI.color(color, false, false);
	}

	public static ANSI color (ANSI.Color color, boolean isBackground) {
		return ANSI.color (color, isBackground, false);
	}

	public static ANSI color (ANSI.Color color, boolean isBackground, boolean isBright) {
		return new ANSI ((color == Color.DEFAULT? 9 : color.ordinal()) + (isBackground? 40 : 30) + (isBright? 50 : 0));
	}

	public static String superscript (String text) {
		return String.format("%s%s%s", new ANSI(73), text, new ANSI(75));
	}

	public static String subscript (String text) {
		return String.format("%s%s%s", new ANSI(74), text, new ANSI(75));
	}

	public static String underline (String text) {
		return String.format("%s%s%s", new ANSI(4), text, new ANSI(24));
	}

	public static String overline (String text) {
		return String.format("%s%s%s", new ANSI(53), text, new ANSI(55));
	}

	public static String strikethrough (String text) {
		return String.format("%s%s%s", new ANSI(9), text, new ANSI(29));
	}

	/**
	 * @param level in [0~2]; controls how fast the Cursor should blink
	 * <b>0</b>: Blink Off
	 * <b>1</b>: Blink Slow (&lt; 3 ×/s)
	 * <b>2</b>: Blink Fast (&gt; 3 ×/s)
	 *              → Any input above 2
	 */
	public static ANSI cursorBlink (int level) {
		return level < 0? EMPTY : new ANSI(new int[]{25, 5, 6}[level%3]);
	}

	public static int[] merge (int...codes) {
		var hs = new HashSet<Integer>();
		for (int i : codes)
			hs.add(i);

		// remove codes that don't work together
		if (hs.contains(1) && hs.contains(2)) // BOLD & FAINT annihilate each other
			hs.removeAll(Set.of(1, 2));
		if (hs.contains(6)||hs.contains(5)) { // Cursor Blink: slow & fast turn to off
			hs.removeAll(Set.of(5, 6));
			hs.add(25);
		}

		var l = new int[hs.size()];
		int i = 0;
		for (int s : hs) {
			l [i] = s;
			i++;
		}

		return l;
	}

	@Override
	public String toString () {
		var codes = "\u001b[";
		for (int i=0; i<this.codes.length; i++)
			codes += this.codes[i] + (i+1<this.codes.length? ";" : "m");
		return codes;
	}

	public enum Color {
		BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE, DEFAULT
	}

	public enum Font {
		DEFAULT, ALT1, ALT2, ALT3, ALT4, ALT5, ALT6, ALT7, ALT8, ALT9, FRAKTUR
	}

}
