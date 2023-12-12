package nz.akb.lib.pretty_print.test;

import nz.akb.lib.pretty_print.ANSI;

public class Test {

	public static void main (String... args) {
		System.out.println("Welcome to lib\u001b[36;1mP\u001b[31;1mr\u001b[32;1me\u001b[33;1mt\u001b[34;1mt\u001b[35;1my\u001b[36;1mP\u001b[31;1mr\u001b[32;1mi\u001b[33;1mn\u001b[34;1mt\u001b[0m!");

		System.out.printf("%nTEXT%n%n");
		System.out.printf("This text should be: %sbold%s %sfaint%s %sitalic%s%n", ANSI.BOLD, ANSI.RESET, ANSI.FAINT, ANSI.RESET, ANSI.ITALIC, ANSI.RESET);
		System.out.printf("This text should be: %s %s %s%n", ANSI.underline("underlined"), ANSI.overline("overlined"), ANSI.strikethrough("striked out"));
		System.out.printf("This should be x² and H₂O: x%s H%sO%n", ANSI.superscript("2"), ANSI.subscript("2"));

		System.out.printf("%nFONTS%n%n");
		for (int i = 1; i < 10; i++)
			System.out.printf ("Font %d: %s%n", i, ANSI.font("Sphinx of Black quartz judge my vows.", ANSI.Font.values()[i]));
		System.out.printf("This is the default font. and this %s%n", ANSI.font("is the Fraktur font (alias Gothic). ", ANSI.Font.FRAKTUR));

		System.out.printf("%nCOLOURS%n%n");
		System.out.printf("%nPre-defined%n");
		for (var col : ANSI.Color.values())
			System.out.printf("%sThis is %s: %snormal %sbrighter %s%sbackground %sbrightbackground %s%n",
					ANSI.DEFAULT,
					col.name(),
					ANSI.color(col, false, false),
					ANSI.color(col, false, true),
					ANSI.DEFAULT,
					ANSI.color(col, true),
					ANSI.color(col, true, true),
					ANSI.color(ANSI.Color.DEFAULT, true)
			);

		System.out.printf("%nRGB%n%n");
		var colors = new int[]{
				0xE28C00, 0xECCD00, 0xFFFFFF, 0x62AECE, 0x203856,
				0xC9CBA3, 0xFFE1A8, 0xE26D5C, 0x723D46, 0x472D30,
				0xAB5852, 0xEADAA0, 0xD69E49, 0x838469, 0x476066,
				0xE4BABE, 0xFCF1EF, 0xE8D8C9, 0xCED3D7, 0x5E606C,
				0xBADBED, 0xffae00, 0xcaffe0, 0x90a2be, 0x212133
		};

		for (var color : colors)
			System.out.printf ("[%s%s   %s%s]: (%6x) This %s(should be)%s%scolored text%s%n",ANSI.color(color, true), ANSI.color(color, false), ANSI.color(ANSI.Color.DEFAULT, true), ANSI.color(ANSI.Color.DEFAULT, false), color, ANSI.color(color, true), ANSI.color(ANSI.Color.DEFAULT, true), ANSI.color(color), ANSI.DEFAULT);

	}
}
