package com.petrolpark.destroy.util.geometry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BasicVolume {
    protected double x1;
    protected double y1;
    protected double z1;

    protected double x2;
    protected double y2;
    protected double z2;

    public BasicVolume(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;

        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public static @NotNull BasicVolume of(@NotNull AABB pBox) {
        return new BasicVolume(pBox.minX, pBox.minY, pBox.minZ, pBox.maxX, pBox.maxY, pBox.maxZ);
    }

    public boolean tryExpand(Direction direction, Predicate<BlockPos> condition) {
        if (expansionPossible(direction, condition)) {
            this.expand(direction.getNormal());
            return true;
        }
        return false;
    }

    public boolean expansionPossible(@NotNull Direction direction, Predicate<BlockPos> condition) {
        double volumeSize = this.getSize(direction.getAxis());

        BasicVolume explorer = this.copy()
                .expand(direction.getNormal())
                .contract(direction.getOpposite().getNormal().multiply(( int ) Math.floor(volumeSize)));

        int minX = Mth.floor(explorer.x1 + 0.001);
        int maxX = Mth.floor(explorer.x2 - 0.001);
        int minY = Mth.floor(explorer.y1 + 0.001);
        int maxY = Mth.floor(explorer.y2 - 0.001);
        int minZ = Mth.floor(explorer.z1 + 0.001);
        int maxZ = Mth.floor(explorer.z2 - 0.001);

        return BlockPos.betweenClosedStream(
                new BlockPos(minX, minY, minZ),
                new BlockPos(maxX, maxY, maxZ)
        ).allMatch(condition);
    }

    public BasicVolume contract(@NotNull Vec3i vector) {
        return this.contract(vector.getX(), vector.getY(), vector.getZ());
    }

    public BasicVolume contract(double pX, double pY, double pZ) {
        double d0 = this.x1;
        double d1 = this.y1;
        double d2 = this.z1;
        double d3 = this.x2;
        double d4 = this.y2;
        double d5 = this.z2;

        if (pX < 0.0D) {
            d0 -= pX;
        } else if (pX > 0.0D) {
            d3 -= pX;
        }

        if (pY < 0.0D) {
            d1 -= pY;
        } else if (pY > 0.0D) {
            d4 -= pY;
        }

        if (pZ < 0.0D) {
            d2 -= pZ;
        } else if (pZ > 0.0D) {
            d5 -= pZ;
        }

        return this.set(d0, d1, d2, d3, d4, d5);
    }

    public BasicVolume expand(@NotNull Vec3i vector) {
        return this.expand(vector.getX(), vector.getY(), vector.getZ());
    }

    public BasicVolume expand(double pX, double pY, double pZ) {
        double d0 = this.x1;
        double d1 = this.y1;
        double d2 = this.z1;
        double d3 = this.x2;
        double d4 = this.y2;
        double d5 = this.z2;

        if (pX < 0.0D) {
            d0 += pX;
        } else if (pX > 0.0D) {
            d3 += pX;
        }

        if (pY < 0.0D) {
            d1 += pY;
        } else if (pY > 0.0D) {
            d4 += pY;
        }

        if (pZ < 0.0D) {
            d2 += pZ;
        } else if (pZ > 0.0D) {
            d5 += pZ;
        }

        return this.set(d0, d1, d2, d3, d4, d5);
    }

    public BasicVolume set(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.x1 = minX;
        this.y1 = minY;
        this.z1 = minZ;

        this.x2 = maxX;
        this.y2 = maxY;
        this.z2 = maxZ;

        return this;
    }

    public BasicVolume encapsulate(@NotNull BasicVolume volume) {
        this.x1 = Math.min(x1, volume.x1);
        this.y1 = Math.min(y1, volume.y1);
        this.z1 = Math.min(z1, volume.z1);

        this.x2 = Math.max(x2, volume.x2);
        this.y2 = Math.max(y2, volume.y2);
        this.z2 = Math.max(z2, volume.z2);

        return this;
    }

    public double getSize(Direction.@NotNull Axis axis) {
        return switch ( axis ) {
            case X -> this.getXSize();
            case Y -> this.getYSize();
            case Z -> this.getZSize();
        };
    }

    private double getZSize() {
        return this.z2 - this.z1;
    }

    private double getYSize() {
        return this.y2 - this.y1;
    }

    public double getXSize() {
        return this.x2 - this.x1;
    }

    public BasicVolume move(double pX, double pY, double pZ) {
        return this.set(this.x1 + pX, this.y1 + pY, this.z1 + pZ, this.x2 + pX, this.y2 + pY, this.z2 + pZ);
    }

    public BasicVolume move(Vec3i vector) {
        return this.move(vector.getX(), vector.getY(), vector.getZ());
    }

    public ArrayList<BasicVolume> generateSubVolumes(@NotNull Direction direction, Predicate<BlockPos> validatePos) {
        ArrayList<BasicVolume> subVolumes = new ArrayList<>();
        double volumeSize = this.getSize(direction.getAxis());

        BasicVolume explorer = this.copy().move(direction.getNormal());
        if (volumeSize > 0.2) {
            explorer.contract(direction.getOpposite().getNormal().multiply(( int ) Math.floor(volumeSize)));
        }

        int minX = Mth.floor(explorer.x1);
        int maxX = Mth.ceil(explorer.x2);
        int minY = Mth.floor(explorer.y1);
        int maxY = Mth.ceil(explorer.y2);
        int minZ = Mth.floor(explorer.z1);
        int maxZ = Mth.ceil(explorer.z2);

        boolean[][] grid;
        int width, height;

        switch (direction) {
            case NORTH -> {
                width = maxX - minX;
                height = maxY - minY;
                grid = new boolean[width][height];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos pos = new BlockPos(minX + x, minY + y, minZ);
                        grid[x][y] = validatePos.test(pos);
                    }
                }

                List<BasicVolume> volumes = extractVolumesFromGrid(grid, minX, minY, minZ, direction);
                List<BasicVolume> offsetedVolumes = new ArrayList<>();
                for (BasicVolume subVolume : volumes) {
                    offsetedVolumes.add(subVolume.move(direction.getOpposite().getNormal()));
                }
                subVolumes.addAll(offsetedVolumes);
            }
            case SOUTH -> {
                width = maxX - minX;
                height = maxY - minY;
                grid = new boolean[width][height];
                int z = maxZ - 1;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos pos = new BlockPos(minX + x, minY + y, z);
                        grid[x][y] = validatePos.test(pos);
                    }
                }
                subVolumes.addAll(extractVolumesFromGrid(grid, minX, minY, z, direction));
            }

            case WEST -> {
                width = maxZ - minZ;
                height = maxY - minY;
                grid = new boolean[width][height];
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos pos = new BlockPos(minX, minY + y, minZ + z);
                        grid[z][y] = validatePos.test(pos);
                    }
                }
                List<BasicVolume> volumes = extractVolumesFromGrid(grid, minX, minY, minZ, direction);
                List<BasicVolume> offsetedVolumes = new ArrayList<>();
                for (BasicVolume subVolume : volumes) {
                    offsetedVolumes.add(subVolume.move(direction.getOpposite().getNormal()));
                }
                subVolumes.addAll(offsetedVolumes);
            }
            case EAST -> {
                width = maxZ - minZ;
                height = maxY - minY;
                grid = new boolean[width][height];
                int x = maxX - 1;
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos pos = new BlockPos(x, minY + y, minZ + z);
                        grid[z][y] = validatePos.test(pos);
                    }
                }
                subVolumes.addAll(extractVolumesFromGrid(grid, x, minY, minZ, direction));
            }

            case DOWN -> {
                width = maxX - minX;
                height = maxZ - minZ;
                grid = new boolean[width][height];
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < height; z++) {
                        BlockPos pos = new BlockPos(minX + x, minY, minZ + z);
                        grid[x][z] = validatePos.test(pos);
                    }
                }
                List<BasicVolume> volumes = extractVolumesFromGrid(grid, minX, minY, minZ, direction);
                List<BasicVolume> offsetedVolumes = new ArrayList<>();
                for (BasicVolume subVolume : volumes) {
                    offsetedVolumes.add(subVolume.move(direction.getOpposite().getNormal()));
                }
                subVolumes.addAll(offsetedVolumes);
            }
            case UP -> {
                width = maxX - minX;
                height = maxZ - minZ;
                grid = new boolean[width][height];
                int y = maxY - 1;
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < height; z++) {
                        BlockPos pos = new BlockPos(minX + x, y, minZ + z);
                        grid[x][z] = validatePos.test(pos);
                    }
                }
                subVolumes.addAll(extractVolumesFromGrid(grid, minX, y, minZ, direction));
            }
        }

        return subVolumes;
    }

    private List<BasicVolume> extractVolumesFromGrid(boolean[][] grid, int baseX, int baseY, int baseZ, Direction dir) {
        List<BasicVolume> boxes = new ArrayList<>();
        int width = grid.length;
        int height = grid[0].length;

        int stepX = dir.getStepX();
        int stepY = dir.getStepY();
        int stepZ = dir.getStepZ();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!grid[i][j]) continue;

                int w = 1;
                while (i + w < width && grid[i + w][j]) w++;

                int h = 1;
                outer: while (j + h < height) {
                    for (int k = 0; k < w; k++) {
                        if (!grid[i + k][j + h]) break outer;
                    }
                    h++;
                }

                for (int x = 0; x < w; x++)
                    for (int y = 0; y < h; y++)
                        grid[i + x][j + y] = false;

                BasicVolume sub;
                switch (dir) {
                    case NORTH, SOUTH -> {
                        int zBlockMin = Math.min(baseZ, baseZ + stepZ);
                        double zMax = zBlockMin + 1.0;
                        double xMin = baseX + i;
                        double xMax = baseX + i + w;
                        double yMin = baseY + j;
                        double yMax = baseY + j + h;
                        sub = new BasicVolume(xMin, yMin, zBlockMin, xMax, yMax, zMax);
                    }
                    case WEST, EAST -> {
                        int xBlockMin = Math.min(baseX, baseX + stepX);
                        double xMax = xBlockMin + 1.0;
                        double zMin = baseZ + i;
                        double zMax = baseZ + i + w;
                        double yMin = baseY + j;
                        double yMax = baseY + j + h;
                        sub = new BasicVolume(xBlockMin, yMin, zMin, xMax, yMax, zMax);
                    }
                    default -> {
                        int yBlockMin = Math.min(baseY, baseY + stepY);
                        double yMax = yBlockMin + 1.0;
                        double xMin = baseX + i;
                        double xMax = baseX + i + w;
                        double zMin = baseZ + j;
                        double zMax = baseZ + j + h;
                        sub = new BasicVolume(xMin, yBlockMin, zMin, xMax, yMax, zMax);
                    }
                }
                boxes.add(sub);
            }
        }
        return boxes;
    }

    public AABB toAABB() {
        return new AABB(x1, y1, z1, x2, y2, z2);
    }

    public boolean contains(Vec3 pos) {
        return pos.x >= x1 && pos.x <= x2 && pos.y >= y1 && pos.y <= y2 && pos.z >= z1 && pos.z <= z2;
    }

    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof BasicVolume volume)) {
            return false;
        } else {
            if (Double.compare(volume.x1, this.x1) != 0) {
                return false;
            } else if (Double.compare(volume.y1, this.y1) != 0) {
                return false;
            } else if (Double.compare(volume.z1, this.z1) != 0) {
                return false;
            } else if (Double.compare(volume.x2, this.x2) != 0) {
                return false;
            } else if (Double.compare(volume.y2, this.y2) != 0) {
                return false;
            } else {
                return Double.compare(volume.z2, this.z2) == 0;
            }
        }
    }

    public BasicVolume copy() {
        return new BasicVolume(
                this.x1, this.y1, this.z1,
                this.x2, this.y2, this.z2
        );
    }

    public void showOutline(int ttl) {
        //Outliner.getInstance().showAABB(this, this.toAABB(), ttl);
    }
}
