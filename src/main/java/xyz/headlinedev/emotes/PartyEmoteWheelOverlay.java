package xyz.headlinedev.emotes;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
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

@Slf4j
@Singleton
public class PartyEmoteWheelOverlay extends Overlay
{
    private static final int EMOTE_WHEEL_IMAGE_WIDTH = 50;
    private static final int EMOTE_WHEEL_IMAGE_HEIGHT = 50;

    private Client client;
    private Emote[] emojis;
    private Point firstMousePosition;
    private int selectedImage;
    private PartyEmoteConfig config;

    @Inject
    private PartyEmoteWheelOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(0.2f);
        this.selectedImage = -1;
        this.firstMousePosition = null;
        this.config = null;
    }

    int getSelectedEmoteIndex()
    {
        if (this.emojis == null)
            return -1;

        if (this.selectedImage < 0 || this.selectedImage >= this.emojis.length)
        {
            return -1;
        }
        return this.emojis[this.selectedImage].getId();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void reset() {
        this.firstMousePosition = null;
    }
    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (this.client == null)
            return null;

        if (this.firstMousePosition == null)
        {
            this.firstMousePosition = this.client.getMouseCanvasPosition();
        }

        Point currentMousePos = this.client.getMouseCanvasPosition();

        float wheelRadius = 50.0f;
        if (this.config != null)
        {
            wheelRadius = (float)this.config.wheelRadius();
            if (wheelRadius < 10.0f)
            {
                wheelRadius = 10.0f;
            }
            if (wheelRadius > 150.0f)
            {
                wheelRadius = 150.0f;
            }
        }


        int deadZone = (int)(wheelRadius * 1/2.0f);
        this.selectedImage = -1;
        if (currentMousePos.distanceTo(this.firstMousePosition) > deadZone)
        {
            this.selectedImage = getHoveredEmoteIndex(this.firstMousePosition, currentMousePos, this.emojis.length);
        }

        for (int i = 0; i < this.emojis.length; ++i)
        {
            BufferedImage sourceImg = this.emojis[i].getImage();
            BufferedImage img = ImageUtil.resizeCanvas(sourceImg, EMOTE_WHEEL_IMAGE_WIDTH, EMOTE_WHEEL_IMAGE_HEIGHT);

            if (this.selectedImage == i)
            {
                img = addAuraBehindImage(img, img.getWidth(), Color.YELLOW);
            }

            // Subtracting PI/2 that way emote 0 appears at the top
            double ang = ((double)i/this.emojis.length) * Math.PI * 2 - Math.PI / 2;

            int x = (int)(Math.cos(ang) * wheelRadius) - img.getWidth()/2;
            int y = (int)(Math.sin(ang) * wheelRadius) - img.getHeight()/2;

            Point emotePos = new Point(x + this.firstMousePosition.getX(), y + this.firstMousePosition.getY());

            OverlayUtil.renderImageLocation(graphics, emotePos, img);
        }

        return null;
    }

    private int getHoveredEmoteIndex(Point center, Point mouse, int numEmotes) {
        double dx = center.getX() - mouse.getX();
        double dy = center.getY() - mouse.getY();

        double angle = Math.atan2(dy, dx); // Y flip
        if (angle < 0) angle += Math.PI * 2;

        double sliceAngle = (Math.PI * 2) / numEmotes;

        // Shift to center slices on emotes AND rotate index 0 to top
        angle = (angle + sliceAngle / 2 - Math.PI / 2 + Math.PI * 2) % (Math.PI * 2);

        return (int)(angle / sliceAngle);
    }

    public static BufferedImage addAuraBehindImage(BufferedImage inputImage, int auraSize, Color auraColor) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        int newWidth = width + auraSize * 2;
        int newHeight = height + auraSize * 2;

        BufferedImage output = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = output.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        float radius = Math.max(newWidth, newHeight) / 2f;
        Point2D center = new Point2D.Float(newWidth / 2f, newHeight / 2f);

        float[] dist = {0.0f, 1.0f};
        Color[] colors = {
                new Color(auraColor.getRed(), auraColor.getGreen(), auraColor.getBlue(), 180),
                new Color(auraColor.getRed(), auraColor.getGreen(), auraColor.getBlue(), 0)
        };

        RadialGradientPaint gradient = new RadialGradientPaint(center, radius, dist, colors);
        g2d.setPaint(gradient);
        g2d.fill(new Ellipse2D.Double(0, 0, newWidth, newHeight));

        g2d.drawImage(inputImage, auraSize, auraSize, null);
        g2d.dispose();

        return output;
    }

    public void setEmotes(Emote[]  emojis) {
        this.emojis = emojis;
    }

    public void setConfig(PartyEmoteConfig config) {
        this.config = config;
    }
}