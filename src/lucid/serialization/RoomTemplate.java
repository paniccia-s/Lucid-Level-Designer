package lucid.serialization;

public class RoomTemplate {

    public Dimensions dimensions;
    public Wall[] walls;
    public None[] nones;
    public EnemyNest[] enemyNests;
    public POI[] pois;
    public Treasure[] treasures;

    public static class Wall
    {
        public int index;
    }

    public static class EnemyNest
    {
        public int index;
        public int spawnRadius;
        public float spawnChance;
        public int spawnAttemptsMin, spawnAttemptsMax;
    }

    public static class POI
    {
        public int index;
        public String type;
    }

    public static class Treasure
    {
        public int index;
    }

    public static class Dimensions
    {
        public int width, height, tileSize;
    }

    public static class None
    {
        public int index;
    }

}
