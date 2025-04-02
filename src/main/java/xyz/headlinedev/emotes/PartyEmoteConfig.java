package xyz.headlinedev.emotes;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("emoji")
public interface PartyEmoteConfig extends Config
{
	@ConfigItem(
			keyName = "smileKeybind",
			name = "Smile Emote",
			description = "Binds a key to use the smile emote.",
			position = 0
	)
	default Keybind smileKeybind()
	{
		return Keybind.NOT_SET;
	}
	@ConfigItem(
			keyName = "eyerollKeybind",
			name = "Eye Roll Emote",
			description = "Binds a key to use the eye roll emoji.",
			position = 1
	)
	default Keybind eyerollKeybind()
	{
		return Keybind.NOT_SET;
	}
	@ConfigItem(
			keyName = "cryingKeybind",
			name = "Crying Emote",
			description = "Binds a key to use the crying emote.",
			position = 2
	)
	default Keybind cryingKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "sadKeybind",
			name = "Sad Emote",
			description = "Binds a key to use the sad emote.",
			position = 3
	)
	default Keybind sadKeybind()
	{
		return Keybind.NOT_SET;
	}
}
