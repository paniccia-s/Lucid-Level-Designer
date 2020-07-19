package lucid.GUI;

import javax.swing.*;
import java.awt.*;

public class TileGridPanel extends JPanel {

    private int mWidth, mHeight, mScale;
    private Color[] mColors;

    public void acceptTileColors(Color[] colors, int width, int height, int scale) {
        mColors = colors; mWidth = width; mHeight = height; mScale = scale;
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

        for (int y = 0; y < mHeight; y++)
        {
            for (int x = 0; x < mWidth; x++)
            {
                int index = x + y * mWidth;
                Color color = mColors[index];

                int posX = x * mScale + offX; int posY = y * mScale + offY;

                gg.setColor(color);
                gg.fillRect(posX, posY, mScale, mScale);
            }
        }
    }
}
