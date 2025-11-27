package com.petrolpark.destroy.core.pollution.pollutedareas;

import com.petrolpark.destroy.DestroyMobEffects;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.core.chemistry.hazard.EntityChemicalPoisonCapability;
import com.petrolpark.destroy.util.geometry.BasicVolume;
import com.petrolpark.destroy.util.geometry.CompositeVolume;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class ContaminatedVolume {
    private final Future<CompositeVolume> futurePropagationData;

    private CompositeVolume area;
    private HashMap<LegacySpecies, Double> composition;

    public ContaminatedVolume(Level level, BlockPos startingPos, HashMap<LegacySpecies, Double> composition, int maxDepth, int maxSize) {
        PropagatorThread propagatorThread = new PropagatorThread(level, startingPos, maxDepth, maxSize);
        this.composition = composition;

        ExecutorService e = Executors.newSingleThreadExecutor();
        this.futurePropagationData = e.submit(propagatorThread);
    }

    public ContaminatedVolume(Level level, BlockPos startingPos, int maxDepth, int maxSize) {
        this(level, startingPos, null, maxDepth, maxSize);
    }

    public Status getStatus() throws ExecutionException, InterruptedException {
        if (futurePropagationData.isDone()) {
            CompositeVolume data = futurePropagationData.get();
            if (data == null) {
                return Status.INFINITE;
            }

            area = data;

            return Status.DONE;
        }
        return Status.WORKING;
    }

    public double getSize() {
        return 50;
    }

    public List<BasicVolume> getAreas() {
        return area.getVolumes();
    }

    public CompositeVolume getVolume() {
        return this.area;
    }

    public boolean contains(Vec3 pos) {
        return this.area.contains(pos);
    }

    public void applyContaminationEffects(LivingEntity entity) {
        if (composition == null) return;

        if (composition.containsKey(DestroyMolecules.CHLORINE)) {
            EntityChemicalPoisonCapability.setMolecule(entity, DestroyMolecules.CHLORINE);
            if (!entity.hasEffect(DestroyMobEffects.CHEMICAL_POISON.get())) entity.addEffect(new MobEffectInstance(DestroyMobEffects.CHEMICAL_POISON.get(), 219, 0, false, false));
        }
    }

    private static class PropagatorThread implements Callable<CompositeVolume> {
        BlockPos startingPos;

        private final Level level;
        private final int maxDepth;
        private final int maxSize;

        private final CompositeVolume compositeVolume;

        public PropagatorThread(Level level, BlockPos startPos, int maxDepth, int maxSize) {
            super();
            this.maxDepth = maxDepth;
            this.maxSize = maxSize;

            this.level = level;
            this.startingPos = startPos;

            this.compositeVolume = new CompositeVolume(new BasicVolume(startingPos.getX(), startingPos.getY(), startingPos.getZ(),
                    startingPos.getX() + 1, startingPos.getY() + 1, startingPos.getZ() + 1));
        }

        @Override
        public CompositeVolume call() {
            ArrayDeque<BasicVolume> active = new ArrayDeque<>();
            active.add(compositeVolume.getRootVolume());

            int depth = 0;
            while (!active.isEmpty() && depth < maxDepth) {
                BasicVolume current = active.poll();

                for (Direction dir : Direction.values()) {
                    int size = 0;
                    while (current.tryExpand(dir, this::posValid) && size < maxSize) {
                        size++;
                    }

                    if (size >= maxSize) return null;
                }

                for (Direction dir : Direction.values()) {
                    active.addAll(current.generateSubVolumes(dir, this::posValid));
                }

                compositeVolume.addVolume(current);
                depth++;
            }

            if (depth > maxDepth) return null;

            return compositeVolume;
        }

        public boolean posValid(BlockPos pos) {
            if (compositeVolume.contains(pos)) return false;

            BlockState state;
            synchronized (level) {
                state = level.getBlockState(pos);
            }
            return stateValid(state);
        }

        private boolean stateValid(BlockState state) {
            return state.isAir() || state.getBlock() == Blocks.GLASS;
        }
    }

    public enum Status {
        DONE,
        WORKING,
        INFINITE
    }
}
