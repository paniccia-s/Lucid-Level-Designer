package lucid.grid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lucid.serialization.RoomTemplate;
import lucid.serialization.SerializationFormat;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Tile grid.
 */
public class TileGrid {

    private int mWidth, mHeight;

    private Tile[] mTiles;

    private TileType mActiveTileType;

    public TileGrid(int width, int height) {
        mWidth = width;
        mHeight = height;

        mTiles = InitTiles();

        mActiveTileType = TileType.Floor;
    }

    public TileGrid(File file, SerializationFormat format) {
        deserialize(file, format);
    }

    private Tile[] InitTiles() {
        Tile[] tiles = new Tile[mWidth * mHeight];

        for (int y = 0; y < mHeight; y++) {
            for (int x = 0; x < mWidth; x++) {
                int index = x + y * mWidth;
                tiles[index] = new Tile(index, TileType.Floor);
            }
        }

        setDefaultBorderWalls(tiles);

        return tiles;
    }

    private void setDefaultBorderWalls() {
        setDefaultBorderWalls(mTiles);
    }

    private void setDefaultBorderWalls(Tile[] tiles) {
        for (int x = 0; x < mWidth; ++x) {
            tiles[x].setTileType(TileType.Wall);
            tiles[mHeight * mWidth - x - 1].setTileType(TileType.Wall);
        }

        for (int y = 0; y < mHeight; ++y) {
            tiles[y * mWidth].setTileType(TileType.Wall);
            tiles[(y + 1) * mWidth - 1].setTileType(TileType.Wall);
        }
    }


    private boolean isIndexOnBorder(int index) {
        int x = index % mWidth;
        int y = index / mWidth;

        return (x == 0 || x == mWidth - 1 || y == 0 || y == mHeight - 1);
    }


    public void clear() {
        // Revert tile types to floor
        for (Tile tile : mTiles) {
            tile.setTileType(TileType.Floor);
            setDefaultBorderWalls();
        }
    }

    // vvv getters and setters vvv

    public void setActiveTileType(TileType type) {
        mActiveTileType = type;
    }

    public Tile getTileAt(int index) {
        return mTiles[index];
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Color[] getTileColors() {
        return Arrays.stream(mTiles).map((Tile::getColor)).toArray(Color[]::new);
    }

    // vvv user interaction vvv

    public void handleMouseClick(Point locationOnScreen, int scale, Point topLeftOfGrid) {
        // scale down x and y to calculate index
        int startX = topLeftOfGrid.x;
        int startY = topLeftOfGrid.y;

        int x = (locationOnScreen.x - startX) / scale;
        int y = (locationOnScreen.y - startY) / scale;
        int index = x + y * mWidth;

        mTiles[index].setTileType(mActiveTileType);
    }

    // vvv (de)serialization vvv

    public void serialize(File saveFile, SerializationFormat format) {
        switch (format) {
            case JSON:
                serializeJson(saveFile);
                break;
            default:
                throw new IllegalArgumentException("Invalid SerializationFormat in serialize()!");
        }
    }

    private void serializeJson(File saveFile) {
        // Must create a room template from the tile data
        RoomTemplate template = CreateRoomTemplate();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String str = gson.toJson(template);

        try {
            FileWriter writer = new FileWriter(saveFile);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private RoomTemplate CreateRoomTemplate() {
        RoomTemplate template = new RoomTemplate();

        // Create the dimensions first
        template.dimensions = getRoomTemplateDimensions();

        // Iterate the tiles to find the rest
        ArrayList<RoomTemplate.Wall> walls = new ArrayList<RoomTemplate.Wall>();
        ArrayList<RoomTemplate.EnemyNest> nests = new ArrayList<RoomTemplate.EnemyNest>();
        ArrayList<RoomTemplate.Treasure> treasures = new ArrayList<RoomTemplate.Treasure>();
        ArrayList<RoomTemplate.POI> pois = new ArrayList<RoomTemplate.POI>();

        for (Tile tile : mTiles) {
            // !TODO
            if (isIndexOnBorder(tile.getIndex())) {
                if (tile.getTileType() != TileType.Wall) {
                    // !TODO
                    throw new RuntimeException("Border tile is not wall at index " + tile.getIndex() + "!");
                }
                continue;
            }

            switch (tile.getTileType()) {
                case None:
                case Floor:
                    // Do nothing for these
                    break;
                case Wall:
                    // Add a new wall
                    walls.add(CreateRoomTemplateWall(tile));
                    break;
                case Nest:
                    // Add a new nest
                    nests.add(CreateRoomTemplateNest(tile));
                    break;
                case Treasure:
                    // Add a new treasure
                    treasures.add(CreateRoomTemplateTreasure(tile));
                    break;
                case POI:
                    // Add a new POI
                    pois.add(CreateRoomTemplatePOI(tile));
                    break;
            }
        }

        template.walls = walls.toArray(new RoomTemplate.Wall[0]);
        template.enemyNests = nests.toArray(new RoomTemplate.EnemyNest[0]);
        template.treasures = treasures.toArray(new RoomTemplate.Treasure[0]);
        template.pois = pois.toArray(new RoomTemplate.POI[0]);

        return template;
    }

    private RoomTemplate.Dimensions getRoomTemplateDimensions() {
        RoomTemplate.Dimensions dim = new RoomTemplate.Dimensions();

        dim.width = mWidth;
        dim.height = mHeight;
        dim.tileSize = 4;  // !TODO

        return dim;
    }

    private RoomTemplate.EnemyNest CreateRoomTemplateNest(Tile tile) {
        RoomTemplate.EnemyNest nest = new RoomTemplate.EnemyNest();

        nest.index = tile.getIndex();
        nest.spawnChance = 1f;
        nest.spawnAttemptsMin = 1;
        nest.spawnAttemptsMax = 1;
        nest.spawnRadius = 3;

        return nest;
    }

    private RoomTemplate.Treasure CreateRoomTemplateTreasure(Tile tile) {
        RoomTemplate.Treasure treasure = new RoomTemplate.Treasure();

        treasure.index = tile.getIndex();

        return treasure;
    }

    private RoomTemplate.POI CreateRoomTemplatePOI(Tile tile) {
        RoomTemplate.POI poi = new RoomTemplate.POI();

        poi.index = tile.getIndex();

        return poi;
    }

    private RoomTemplate.Wall CreateRoomTemplateWall(Tile tile) {
        RoomTemplate.Wall wall = new RoomTemplate.Wall();

        wall.index = tile.getIndex();

        return wall;
    }


    public void deserialize(File loadFile, SerializationFormat format) {
        switch(format) {
            case JSON:
                deserializeJson(loadFile);
                break;
            default:
                throw new IllegalArgumentException("Unimplemented SerializationFormat in deserialize()!");
        }
    }

    private void deserializeJson(File loadFile) {
        // Deserialize the contents of the file
        RoomTemplate template = null;

        try {
            Scanner scanner = new Scanner(loadFile);
            StringBuilder data = new StringBuilder(128);

            while (scanner.hasNext()) {
                data.append(scanner.nextLine());
            }

            Gson gson = new Gson();
            template = gson.fromJson(data.toString(), RoomTemplate.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        // Fill grid
        fillGridFromTemplate(template);
    }

    private void fillGridFromTemplate(RoomTemplate template) {
        // Set grid dimensions
        setRoomTemplateDimensions(template.dimensions);

        // Iterate over the rest of the elements
        for (RoomTemplate.Wall wall : template.walls) {
            checkCreatingNonBorderTile(wall.index);
            createWall(wall);
        }
        for (RoomTemplate.EnemyNest nest : template.enemyNests) {
            checkCreatingNonBorderTile(nest.index);
            createEnemyNest(nest);
        }
        for (RoomTemplate.Treasure treasure : template.treasures) {
            checkCreatingNonBorderTile(treasure.index);
            createTreasure(treasure);
        }
        for (RoomTemplate.POI poi : template.pois) {
            checkCreatingNonBorderTile(poi.index);
            createPOI(poi);
        }
    }

    private void setRoomTemplateDimensions(RoomTemplate.Dimensions dimensions) {
        mWidth = dimensions.width;
        mHeight = dimensions.height;
        // !TODO deal with size

        mTiles = new Tile[mWidth * mHeight];
        for (int i = 0; i < mWidth * mHeight; i++) {
            mTiles[i] = new Tile(i, TileType.Floor);
        }

        setDefaultBorderWalls();
    }

    private void createWall(RoomTemplate.Wall wall) {
        mTiles[wall.index].setTileType(TileType.Wall);
    }

    private void createEnemyNest(RoomTemplate.EnemyNest nest) {
        mTiles[nest.index].setTileType(TileType.Nest);
    }

    private void createTreasure(RoomTemplate.Treasure treasure) {
        mTiles[treasure.index].setTileType(TileType.Treasure);
    }

    private void createPOI(RoomTemplate.POI poi) {
        mTiles[poi.index].setTileType(TileType.POI);
    }

    private void checkCreatingNonBorderTile(int index) {
        if (isIndexOnBorder(index)) {
            // !TODO
            throw new IllegalArgumentException("Index on border");
        }
    }
}
