package com.gmail.chickenpowerrr.ranksync.server.language;

import com.gmail.chickenpowerrr.ranksync.api.languagehelper.LanguageHelper;
import lombok.Setter;

/**
 * This enum contains the translations that can get used by the plugin
 *
 * @author Chickenpowerrr
 * @since 1.0.0
 */
public enum Translation {

	STARTUP_TRANSLATIONS,
	STARTUP_RANKS,
	INVALID_CODE,
	RANKSYNC_COMMAND_USAGE,
	RANKSYNC_COMMAND_REQUEST_LIMIT,
	RANKSYNC_COMMAND_LINKED,
	RANKSYNC_COMMAND_ALREADY_LINKED,
	RANKSYNC_COMMAND_GET_CODE,
	COMMAND_PLAYERONLY,
	UNSYNC_COMMAND_UNLINKED,
	UNSYNC_COMMAND_NOT_LINKED,
	UNSYNC_COMMAND_INVALID_SERVICE,
	UNSYNC_COMMAND_USAGE,
	DISCORD_LINKINFO,
	INVALID_RANK;

	private static final String DEFAULT_LANGUAGE = "english";
	@Setter
	private static LanguageHelper languageHelper;
	@Setter
	private static String language;

	private final String key;

	Translation() {
		this.key = "minecraft-" + super.toString().toLowerCase().replace("_", "-");
	}

	Translation(String key) {
		this.key = key;
	}

	/**
	 * Returns a translation based on the placeholders
	 *
	 * @param replacements the placeholders (placeholder, replacement, placeholder, replacement, etc.)
	 * @return a translation based on the placeholders
	 */
	public String getTranslation(String... replacements) {
		String message = languageHelper.getMessage(language, DEFAULT_LANGUAGE, this.key);

		for (int i = 0; i < replacements.length; i += 2) {
			message = message.replaceAll("%" + replacements[i] + "%", replacements[i + 1]);
		}
		Character character = '\u00A7';
		return message.replaceAll("&", character.toString());
	}

	@Override
	public String toString() {
		return key;
	}
}
