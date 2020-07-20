package lucid.GUI;

import javax.swing.*;
import java.awt.*;

public class TileGridPanel extends JPanel {

    private int mWidth, mHeight, mScale;
    private Color[] mColors;

    private boolean mShowIndices;

    public void acceptRenderInfo(Color[] colors, int width, int height, int scale, boolean showIndices) {
        mColors = colors; mWidth = width; mHeight = height; mScale = scale; mShowIndices = showIndices;
        repaint();
    }

    public Point getTopLeftOfTileGrid() {
        return new Point(
                (getWidth() - (mWidth * mScale)) / 2,
                (getHeight() - (mHeight * mScale)) / 2
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gg = (Graphics2D)g;

        // Offsets to center in screen
        int offX = (getWidth() - (mWidth * mScale)) / 2;
        int offY = (getHeight() - (mHeight * mScale)) / 2;

        // Get font info before looping
        FontMetrics font = gg.getFontMetrics();

        for (int y = 0; y < mHeight; y++)
        {
            for (int x = 0; x < mWidth; x++)
            {
                int index = x + y * mWidth;
                Color color = mColors[index];

                int posX = x * mScale + offX; int posY = y * mScale + offY;

                gg.setColor(color);
                gg.fillRect(posX, posY, mScale, mScale);

                if (!mShowIndices) continue;

                int posYStr = posY + font.getHeight();

                double Y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000f;
                Color textColor = Y >= 128 ? Color.black : Color.white;

                gg.setColor(textColor);
                gg.drawString(String.valueOf(index), posX, posYStr);
            }
        }
    }
}
