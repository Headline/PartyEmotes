package xyz.headlinedev.emotes;

import net.runelite.client.config.*;

@ConfigGroup("emoji")
public interface PartyEmoteConfig extends Config
{
	@ConfigSection(
			name = "Emote Display Settings",
			description = "Change settings relating to how emotes appear on screen.",
			position = 1
	)
	String displaySection = "wheel2";
	@ConfigItem(
			keyName = "displaySize",
			name = "Wheel Radius",
			description = "Controls the radius of the emote wheel",
			position = 0,
			section = displaySection
	)
	default int wheelRadius()
	{
		return 50;
	}

	enum EmoteSelection
	{
		Smile,
		EyeRoll,
		Crying,
		Sad,
		Poo,
		Thinking,
		XD,
		RockOn,
		Melt,
		Heart,
	}

	@ConfigSection(
			name = "Emote Wheel Selection",
			description = "Select which emotes appear in the wheel.",
			position = 99
	)
	String wheelSection = "wheel";

	@ConfigItem(
			keyName = "wheelKeybind",
			name = "Emote Wheel",
			description = "Binds a key to open the emote wheel.",
			position = 0,
			section = wheelSection
	)
	default Keybind wheelKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			position = 21,
			keyName = "wheelSlot0",
			name = "Wheel Emote 1",
			description = "Select emote to use for slot 0 in the wheel.",
			section = wheelSection
	)

	default EmoteSelection wheelSlot0()
	{
		return EmoteSelection.Smile;
	}

	@ConfigItem(
			position = 22,
			keyName = "wheelSlot1",
			name = "Wheel Emote 2",
			description = "Select emote to use for slot 1 in the wheel.",
			section = wheelSection
	)

	default EmoteSelection wheelSlot1()
	{
		return EmoteSelection.EyeRoll;
	}

	@ConfigItem(
			position = 23,
			keyName = "wheelSlot2",
			name = "Wheel Emote 3",
			description = "Select emote to use for slot 2 in the wheel.",
			section = wheelSection
	)

	default EmoteSelection wheelSlot2()
	{
		return EmoteSelection.Thinking;
	}

	@ConfigItem(
			position = 24,
			keyName = "wheelSlot3",
			name = "Wheel Emote 4",
			description = "Select emote to use for slot 3 in the wheel.",
			section = wheelSection
	)

	default EmoteSelection wheelSlot3()
	{
		return EmoteSelection.Heart;
	}

	@ConfigItem(
			position = 25,
			keyName = "wheelSlot4",
			name = "Wheel Emote 5",
			description = "Select emote to use for slot 4 in the wheel.",
			section = wheelSection
	)

	default EmoteSelection wheelSlot4()
	{
		return EmoteSelection.Sad;
	}

	@ConfigItem(
			position = 26,
			keyName = "wheelSlot5",
			name = "Wheel Emote 6",
			description = "Select emote to use for slot 5 in the wheel.",
			section = wheelSection
	)

	default EmoteSelection wheelSlot5()
	{
		return EmoteSelection.XD;
	}

	@ConfigSection(
			name = "Manual Keybinds",
			description = "Bind a key to a specific emote.",
			position = 70
	)
	String manualKeybinds = "manual";

	@ConfigItem(
			position = 71,
			keyName = "useEmote0",
			name = "Emote 1",
			description = "Select emote to use for slot 0 in the wheel.",
			section = manualKeybinds
	)

	default EmoteSelection useEmote0()
	{
		return EmoteSelection.Smile;
	}

	@ConfigItem(
			keyName = "bindEmote0",
			name = "Bind 1",
			description = "Binds a key to the emote slot.",
			position = 72,
			section = manualKeybinds
	)
	default Keybind bindEmote0()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			position = 73,
			keyName = "useEmote1",
			name = "Emote 2",
			description = "Select emote to use for slot 1.",
			section = manualKeybinds
	)

	default EmoteSelection useEmote1()
	{
		return EmoteSelection.EyeRoll;
	}

	@ConfigItem(
			keyName = "bindEmote1",
			name = "Bind 2",
			description = "Binds a key to the emote slot.",
			position = 74,
			section = manualKeybinds
	)
	default Keybind bindEmote1()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			position = 75,
			keyName = "useEmote2",
			name = "Emote 3",
			description = "Select emote to use for slot 2.",
			section = manualKeybinds
	)

	default EmoteSelection useEmote2()
	{
		return EmoteSelection.Sad;
	}

	@ConfigItem(
			keyName = "bindEmote2",
			name = "Bind 3",
			description = "Binds a key to the emote slot.",
			position = 76,
			section = manualKeybinds
	)
	default Keybind bindEmote2()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			position = 77,
			keyName = "useEmote3",
			name = "Emote 4",
			description = "Select emote to use for slot 3.",
			section = manualKeybinds
	)

	default EmoteSelection useEmote3()
	{
		return EmoteSelection.Crying;
	}

	@ConfigItem(
			keyName = "bindEmote3",
			name = "Bind 4",
			description = "Binds a key to the emote slot.",
			position = 78,
			section = manualKeybinds
	)
	default Keybind bindEmote3()
	{
		return Keybind.NOT_SET;
	}
}
