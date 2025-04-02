package xyz.headlinedev.emotes;

import com.google.inject.Provides;

import javax.inject.Inject;
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

class Emote {
	private static short NUM_EMOTES = 0;
	private short id;
	private BufferedImage img;

	Emote(String path) {
		this.id = NUM_EMOTES++;
		this.img = ImageUtil.loadImageResource(PartyEmotePlugin.class, path);
	}

	static int getNumEmotes() { return Emote.NUM_EMOTES; }
	public BufferedImage getImage() {
		return img;
	}
}

@PluginDescriptor(
	name = "Emotes"
)
public class PartyEmotePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PartyEmoteConfig config;

	@Inject
	private PartyEmoteOverlay emoteOverlay;

	@Inject
	private KeyManager keyManager;

	@Inject
	private PartyService party;

	@Inject
	private WSClient wsClient;

	private Emote[] emotes;

	@Override
	protected void startUp() throws Exception
	{
		this.emotes = new Emote[]{
			new Emote("1F601.png"),
			new Emote("1F644.png"),
			new Emote("1F62D.png"),
			new Emote("1F641.png"),
		};

		overlayManager.add(emoteOverlay);
		keyManager.registerKeyListener(smileListener);
		keyManager.registerKeyListener(eyeRollListener);
		keyManager.registerKeyListener(cryingListener);
		keyManager.registerKeyListener(sadListener);
		wsClient.registerMessage(PartyEmoteUpdate.class);

		emoteOverlay.setEmotes(emotes);
		emoteOverlay.setClient(client);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(emoteOverlay);
		keyManager.unregisterKeyListener(smileListener);
		keyManager.unregisterKeyListener(eyeRollListener);
		keyManager.unregisterKeyListener(cryingListener);
		keyManager.unregisterKeyListener(sadListener);
		wsClient.unregisterMessage(PartyEmoteUpdate.class);

		overlayManager.remove(emoteOverlay);
	}

	protected void useEmote(int emoteId)
	{
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
		if (emoteId > 0 && emoteId < Emote.getNumEmotes())
		{
			emoteOverlay.addEvent(new EmoteEvent(event.getPlayerId(), event.getEmoteId()));
		}
	}

	@Provides
	PartyEmoteConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyEmoteConfig.class);
	}

	private final HotkeyListener smileListener = new HotkeyListener(() -> config.smileKeybind())
	{
		@Override
		public void hotkeyPressed() { useEmote( 0); }
	};
	private final HotkeyListener eyeRollListener = new HotkeyListener(() -> config.eyerollKeybind())
	{
		@Override
		public void hotkeyPressed() { useEmote(1); }
	};
	private final HotkeyListener cryingListener = new HotkeyListener(() -> config.cryingKeybind())
	{
		@Override
		public void hotkeyPressed() { useEmote(2); }
	};
	private final HotkeyListener sadListener = new HotkeyListener(() -> config.sadKeybind())
	{
		@Override
		public void hotkeyPressed() { useEmote(3); }
	};

}
