package com.gmail.chickenpowerrr.ranksync.api.languagehelper;

import java.io.IOException;
import java.io.InputStream;

class LanguageResource extends LanguageContainer {

	LanguageResource(String language, InputStream inputStream) throws IOException {
		super(language, inputStream);
	}
}