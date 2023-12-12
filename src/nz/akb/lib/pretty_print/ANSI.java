package nz.akb.lib.pretty_print;

import java.util.HashSet;
import java.util.Set;

public record ANSI (int...codes) {

	//#region [Constants]
	/** Just an Empty ANSI Escape */
	private static final ANSI EMPTY = new ANSI ();

	/** Resets all Effects */
	public static final ANSI RESET  = new ANSI (0);
	/** Makes text bold **/
	public static final ANSI BOLD   = new ANSI (1);
	/** Makes text lighter **/
	public static final ANSI FAINT  = new ANSI (2);
	/** Makes text italic **/
	public static final ANSI ITALIC = new ANSI (3);

	// colors
	/** Resets the color **/
	public static final ANSI DEFAULT = new ANSI(39);
	/** Sets the color **/
	public static final ANSI YELLOW = new ANSI(33);
	public static final ANSI PURPLE = new ANSI(35);
	public static final ANSI BLACK = new ANSI(30);
	public static final ANSI GREEN = new ANSI(32);
	public static final ANSI WHITE = new ANSI(37);
	public static final ANSI BLUE = new ANSI(34);
	public static final ANSI CYAN = new ANSI(36);
	public static final ANSI RED = new ANSI(31);
	//#endregion

	/**
	 * Combines two ANSI Escapes.
	 * → Do not use with RGB Color Codes
	 * @param arg0 ANSI Escape 1
	 * @param arg1 ANSI Escape 2
	 * @return a Combined ANSI Escape
	 */
	public static ANSI add (ANSI arg0, ANSI arg1) {
		int[] codes = new int[arg0.codes.length + arg1.codes.length];
		System.arraycopy(arg0.codes, 0, codes,         0, arg0.codes.length);
		System.arraycopy(arg1.codes, 0, codes, arg0.codes.length, arg1.codes.length);

		return new ANSI(merge(codes));
	}

	/**
	 * Combines multiple ANSI Escapes.
	 * → Do not use with RGB Color Codes
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

	/**
	 * Turns a {@link Font} into an ANSI Escape
	 * May not work on certain Console Emulators
	 * @param font the font to be used
	 * @return an ANSI Escape containing the font
	 */
	public static ANSI font (Font font) {
		return new ANSI(10 + font.ordinal());
	}

	/**
	 * Encloses a String in a font
	 * May not work on certain emulators
	 * @param text the text to be surrounded
	 * @param font the font to surround with
	 * @return the text with ANSI Escapes added
	 */
	public static String font (String text, Font font) {
		return String.format("%s%s%s", ANSI.font(font), text, ANSI.font(Font.DEFAULT));
	}

	/**
	 * Turns an RGB Color split into separate components into an ANSI Escape.
	 * @param g the Green Component [0~255]
	 * @param b the Blue Component [0~255]
	 * @param r the Red Component [0~255]
	 * @param isBackground is this Color supposed to be Fore- ({@code false}) or Background ({@code true})
	 * @return ANSI Escape for Color
	 */
	public static ANSI color (int r, int g, int b, boolean isBackground) {
		return ANSI.color(new java.awt.Color(r, g, b), isBackground);
	}

	/**
	 * Turns an RGB Color split into separate components into an ANSI Escape.
	 * @param g the Green Component [0~255]
	 * @param b the Blue Component [0~255]
	 * @param r the Red Component [0~255]
	 * @return ANSI Escape for Color
	 */
	public static ANSI color (int r, int g, int b) {
		return ANSI.color (r, g, b, false);
	}

	/**
	 * Turns an RGB Color into an ANSI Escape.
	 * @param rgb the RGB Color 0x[000000~ffffff]
	 * @return ANSI Escape for Color
	 */
	public static ANSI color (int rgb) {
		return ANSI.color (rgb, false);
	}

	/**
	 * Turns an RGB Color into an ANSI Escape.
	 * @param rgb the RGB Color 0x[000000~ffffff]
	 * @param isBackground is this Color supposed to be Fore- ({@code false}) or Background ({@code true})
	 * @return ANSI Escape for Color
	 */
	public static ANSI color (int rgb, boolean isBackground) {
		return ANSI.color(new java.awt.Color(rgb), isBackground);
	}

	/**
	 * Turns a {@link java.awt.Color} into an ANSI Escape
	 * @param color the {@link java.awt.Color}
	 * @param isBackground is this Color supposed to be Fore- ({@code false}) or Background ({@code true})
	 * @return an ANSI Escape for this Color
	 */
	public static ANSI color (java.awt.Color color, boolean isBackground) {
		return new ANSI(isBackground? 48 : 38, 2, color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * Turns a {@link java.awt.Color} into an ANSI Escape
	 * @param color the {@link java.awt.Color}
	 * @return an ANSI Escape for this Color
	 */
	public static ANSI color (java.awt.Color color) {
		return ANSI.color (color, false);
	}

	/**
	 * Turn pre-defined colors into ANSI Escapes
	 * @param color a {@link ANSI.Color} value
	 * @return an ANSI Escape
	 */
	public static ANSI color (ANSI.Color color) {
		return ANSI.color(color, false, false);
	}

	/**
	 * Turn pre-defined colors into ANSI Escapes
	 * @param color a {@link ANSI.Color} value
	 * @param isBackground is this Color supposed to be Fore- ({@code false}) or Background ({@code true})
	 * @return an ANSI Escape
	 */
	public static ANSI color (ANSI.Color color, boolean isBackground) {
		return ANSI.color (color, isBackground, false);
	}

	/**
	 * Turn pre-defined colors into ANSI Escapes
	 * @param color a {@link ANSI.Color} value
	 * @param isBright is this Color supposed to be brighter ({@code true}) or normal intensity ({@code false})
	 * @param isBackground is this Color supposed to be Fore- ({@code false}) or Background ({@code true})
	 * @return an ANSI Escape
	 */
	public static ANSI color (ANSI.Color color, boolean isBackground, boolean isBright) {
		return new ANSI ((color == Color.DEFAULT? 9 : color.ordinal()) + (isBackground? 40 : 30) + (isBright? 50 : 0));
	}

	/**
	 * Turns a text into Superscript.
	 * (may not be supported on some Console emulators)
	 * @param text the text supposed to be turned.
	 */
	public static String superscript (String text) {
		return String.format("%s%s%s", new ANSI(73), text, new ANSI(75));
	}

	/**
	 * Turns a text into Subscript.
	 * (may not be supported on some Console emulators)
	 * @param text the text supposed to be turned.
	 */
	public static String subscript (String text) {
		return String.format("%s%s%s", new ANSI(74), text, new ANSI(75));
	}

	/**
	 * Underlines a given text.
	 * (may not be supported on some Console emulators)
	 * @param text the text supposed to be turned.
	 */
	public static String underline (String text) {
		return String.format("%s%s%s", new ANSI(4), text, new ANSI(24));
	}

	/**
	 * Overlines a given text.
	 * (may not be supported on some Console emulators)
	 * @param text the text supposed to be turned.
	 */
	public static String overline (String text) {
		return String.format("%s%s%s", new ANSI(53), text, new ANSI(55));
	}

	/**
	 * Adds a strike through a given text.
	 * (may not be supported on some Console emulators)
	 * @param text the text supposed to be turned.
	 */
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

	/**
	 * Merges a list of codes, to remove duplicates and Clashing codes
	 * @param codes the array of codes
	 * @return a merged array.
	 */
	private static int[] merge (int...codes) {
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

	/**
	 * Turns a given ANSI Escape into the correct String
	 * @return the ANSI Escape Sequence, or {@code ""} if {@see ANSI.EMPTY}.
	 */
	@Override
	public String toString () {
		if (this == EMPTY) return "";

		var codes = "\u001b[";
		for (int i=0; i<this.codes.length; i++)
			codes += this.codes[i] + (i+1<this.codes.length? ";" : "m");
		return codes;
	}


	/** list of pre-defined colors **/
	public enum Color {
		BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE, DEFAULT
	}

	/** list of standard fonts **/
	public enum Font {
		DEFAULT, ALT1, ALT2, ALT3, ALT4, ALT5, ALT6, ALT7, ALT8, ALT9, FRAKTUR
	}

}
