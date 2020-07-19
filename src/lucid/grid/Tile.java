package lucid.grid;

import java.awt.*;

public class Tile {

    private int mIndex;

    private TileType mTileType;

    public Tile(int index, TileType tileType) {
        mIndex = index;
        mTileType = tileType;
    }

    /**
     * Returns the previous type.
     */
    public TileType setTileType(TileType type) {
        TileType old = mTileType;
        mTileType = type;
        return old;
    }

    public Color getColor() {
        return mTileType.getColor();
    }

    public TileType getTileType() {
        return mTileType;
    }

    public int getIndex() {
        return mIndex;
    }
}
