package ir.Awake;

import java.util.Objects;

public class BlockKey {
    private final int x;
    private final int y;
    private final int z;

    public BlockKey(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BlockKey blockKey = (BlockKey) obj;
        return x == blockKey.x && y == blockKey.y && z == blockKey.z;
    }
}
