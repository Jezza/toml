package com.github.jezza.lang;

import static com.github.jezza.Toml.NONSTANDARD_EXTENSIONS_KEY;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author Jezza
 */
final class ExtensionsTest extends AbstractTest {

	@Test
	void examples() throws IOException {
		System.setProperty(NONSTANDARD_EXTENSIONS_KEY, "true");
		compareOutput("/exts/relative.txt", "/exts/relative.toml");
		System.setProperty(NONSTANDARD_EXTENSIONS_KEY, "false");
	}
}
