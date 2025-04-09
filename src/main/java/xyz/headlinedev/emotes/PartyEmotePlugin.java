package xyz.headlinedev.emotes;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Emote {
	private static short NUM_EMOTES = 0;
	private int id;
	private BufferedImage img;
	private String name;

	Emote(String path, String name) {
		this.id = NUM_EMOTES++;
		this.name = name;
		this.img = ImageUtil.loadImageResource(PartyEmotePlugin.class, path);
	}

	static int getNumEmotes() { return Emote.NUM_EMOTES; }
	public BufferedImage getImage() {
		return img;
	}

	public int getId() {
		return this.id;
	}
}

@PluginDescriptor(
	name = "Party Emotes"
)
public class PartyEmotePlugin extends Plugin
{
	public static final int EMOTE_COOLDOWN_TICKS = 2;
	public static final int NUM_WHEEL_SLOTS = 6;
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PartyEmoteConfig config;

	@Inject
	private PartyEmoteOverlay emoteOverlay;
	@Inject
	private PartyEmoteWheelOverlay wheelOverlay;

	@Inject
	private KeyManager keyManager;

	@Inject
	private PartyService party;

	@Inject
	private WSClient wsClient;

	private Emote[] emotes;

	private int lastSendTick;
	private ArrayList<HotkeyListener> listeners;

	@Override
	protected void startUp() throws Exception
	{
		this.listeners = new ArrayList<>();

		// order here matters! See Emote.id
		this.emotes = new Emote[]{
			new Emote("1F601.png", "Smile"),
			new Emote("1F644.png", "Eye roll"),
			new Emote("1F62D.png", "Crying"),
			new Emote("1F641.png", "Sad"),
			new Emote("1F4A9.png", "Poo"),
			new Emote("1F9D0.png", "Thinking"),
			new Emote("1F606.png", "XD"),
			new Emote("1F918.png", "Rock On"),
			new Emote("1FAE0.png", "Melt"),
			new Emote("2764.png", "Heart"),
		};

		overlayManager.add(emoteOverlay);

		createListeners();

		for (HotkeyListener listener : this.listeners) {
			keyManager.registerKeyListener(listener);
		}

		wsClient.registerMessage(PartyEmoteUpdate.class);

		emoteOverlay.setEmotes(emotes);
		emoteOverlay.setClient(client);
		emoteOverlay.setConfig(config);

		wheelOverlay.setEmotes(emotes);
		wheelOverlay.setClient(client);
		wheelOverlay.setConfig(config);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(emoteOverlay);
		for (HotkeyListener listener : this.listeners) {
			keyManager.unregisterKeyListener(listener);
		}

		wsClient.unregisterMessage(PartyEmoteUpdate.class);

		overlayManager.remove(emoteOverlay);
	}

	protected void useEmote(int emoteId)
	{
		final int currentTick = client.getTickCount();
		// prevent too much emote spam
		if (lastSendTick + EMOTE_COOLDOWN_TICKS > currentTick)
		{
			return;
		}

		lastSendTick = currentTick;

		final int localPlayerId = client.getLocalPlayer().getId();
		emoteOverlay.addEvent(new EmoteEvent(localPlayerId, emoteId));
		if (party.isInParty())
		{
			final PartyEmoteUpdate specialCounterUpdate = new PartyEmoteUpdate(localPlayerId, emoteId);
			party.send(specialCounterUpdate);
		}
	}

	@Subscribe
	public void onPartyEmoteUpdate(PartyEmoteUpdate event)
	{
		int emoteId = event.getEmoteId();

		// make sure the emote id we're receiving is within our loaded range
		// (someone is using a newer version?)
		if (emoteId >= 0 && emoteId < Emote.getNumEmotes())
		{
			emoteOverlay.addEvent(new EmoteEvent(event.getPlayerId(), event.getEmoteId()));
		}
	}

	@Provides
	PartyEmoteConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyEmoteConfig.class);
	}

	private void createListeners() {
		// Emote Wheel
		this.listeners.add(new HotkeyListener(() -> config.wheelKeybind())
		{
			@Override
			public void hotkeyPressed() {
				Emote[] emotes = new Emote[NUM_WHEEL_SLOTS];
				emotes[0] = PartyEmotePlugin.this.emotes[config.wheelSlot0().ordinal()];
				emotes[1] = PartyEmotePlugin.this.emotes[config.wheelSlot1().ordinal()];
				emotes[2] = PartyEmotePlugin.this.emotes[config.wheelSlot2().ordinal()];
				emotes[3] = PartyEmotePlugin.this.emotes[config.wheelSlot3().ordinal()];
				emotes[4] = PartyEmotePlugin.this.emotes[config.wheelSlot4().ordinal()];
				emotes[5] = PartyEmotePlugin.this.emotes[config.wheelSlot5().ordinal()];
				wheelOverlay.setEmotes(emotes);

				overlayManager.add(wheelOverlay);
			}

			@Override
			public void hotkeyReleased() {
				int selectedIndex = wheelOverlay.getSelectedEmoteIndex();
				useEmote(selectedIndex);
				wheelOverlay.reset();
				overlayManager.remove(wheelOverlay);
			}
		});

		// Manual Keybinds
		this.listeners.add(new HotkeyListener(() -> config.bindEmote0())
		{
			@Override
			public void hotkeyPressed() { useEmote(config.useEmote0().ordinal()); }
		});
		this.listeners.add(new HotkeyListener(() -> config.bindEmote1())
		{
			@Override
			public void hotkeyPressed() { useEmote(config.useEmote1().ordinal()); }
		});
		this.listeners.add(new HotkeyListener(() -> config.bindEmote2())
		{
			@Override
			public void hotkeyPressed() { useEmote(config.useEmote2().ordinal()); }
		});
		this.listeners.add(new HotkeyListener(() -> config.bindEmote3())
		{
			@Override
			public void hotkeyPressed() { useEmote(config.useEmote3().ordinal()); }
		});
	}

}
