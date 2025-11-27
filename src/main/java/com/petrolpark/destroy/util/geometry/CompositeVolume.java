package com.petrolpark.destroy.util.geometry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeVolume {
    private final ArrayList<BasicVolume> volumes = new ArrayList<>();
    private final BasicVolume boundingBox;


    public CompositeVolume(double x1, double y1, double z1, double x2, double y2, double z2) {
        BasicVolume root = new BasicVolume(x1, y1, z1, x2, y2, z2);
        volumes.add(root);
        boundingBox = root.copy();
    }


    public CompositeVolume(BasicVolume root) {
        volumes.add(root);
        boundingBox = root.copy();
    }


    public void addVolume(BasicVolume volume) {
        volumes.add(volume);
        boundingBox.encapsulate(volume);
    }


    public List<BasicVolume> getVolumes() {
        return Collections.unmodifiableList(volumes);
    }


    public BasicVolume getBoundingBox() {
        return boundingBox;
    }

    public BasicVolume getRootVolume() {
        return volumes.get(0);
    }


    public List<AABB> asAABBs() {
        return volumes.stream().map(BasicVolume::toAABB).toList();
    }

    public boolean contains(BlockPos pos) {
        return contains(pos.getCenter());
    }

    public boolean contains(Vec3 pos) {
        if (!boundingBox.contains(pos)) return false;
        return volumes.stream().anyMatch(v -> v.contains(pos));
    }
}
