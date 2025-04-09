package xyz.headlinedev.emotes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

class EmoteEvent {
    public static final short EMOTE_DISPLAY_TIME_MS = 3000;
    public static final short FADE_OUT_TIME_MS = 750;
    public int sourcePlayerId;
    public int emojiId;
    public Instant startTime;

    EmoteEvent(int sourceId, int emoteId)
    {
        this.sourcePlayerId = sourceId;
        this.emojiId = emoteId;
        this.startTime = Instant.now();
    }

    public long GetMillisLeft() {
        long millis = Duration.between(startTime, Instant.now()).toMillis();
        long left = EMOTE_DISPLAY_TIME_MS - millis;
        return left < 0 ? 0 : left;
    }

    public boolean hasExpired() {
        return GetMillisLeft() == 0;
    }
}

@Slf4j
@Singleton
public class PartyEmoteOverlay extends Overlay
{
    private Client client;
    private Emote[] emojis;
    private final HashMap<Integer, EmoteEvent> activeEmotes;
    private Instant lastUpdate;
    private PartyEmoteConfig config;

    @Inject
    private PartyEmoteOverlay()
    {
        activeEmotes = new HashMap<>();
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(0.2f);
        lastUpdate = Instant.now();
        this.config = null;
    }

    public void addEvent(EmoteEvent event)
    {
        activeEmotes.put(event.sourcePlayerId, event);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (this.client == null)
            return null;

        if (activeEmotes.size() == 0)
            return null;

        for (Player player : client.getPlayers())
        {
            if (player == null)
                continue;

            EmoteEvent event = activeEmotes.get(player.getId());
            if (event == null)
                continue;

            if (event.hasExpired()) {
                activeEmotes.remove(player.getId());
                continue;
            }

            renderPlayerOverlay(graphics, player, event);
        }
        return null;
    }

    private void renderPlayerOverlay(Graphics2D graphics, Player actor, EmoteEvent event)
    {
        final LocalPoint actorPosition = actor.getLocalLocation();
        final int offset = actor.getLogicalHeight() + 75;
        BufferedImage image = this.emojis[event.emojiId].getImage();

        // Fade emote out
        if (event.GetMillisLeft() < EmoteEvent.FADE_OUT_TIME_MS)
        {
            BufferedImage alphaImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            float alpha = event.GetMillisLeft() / (float) EmoteEvent.FADE_OUT_TIME_MS;

            Graphics2D g2d = alphaImage.createGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            image = alphaImage;
        }

        BufferedImage resizedImage = ImageUtil.resizeCanvas(image, 50, 50);
        final Point imageLoc = Perspective.getCanvasImageLocation(client, actorPosition, resizedImage, offset);
        if (imageLoc == null)
            return;

        OverlayUtil.renderImageLocation(graphics, imageLoc, resizedImage);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setEmotes(Emote[]  emojis) {
        this.emojis = emojis;
    }

    public void setConfig(PartyEmoteConfig config) {
        this.config = config;
    }
}