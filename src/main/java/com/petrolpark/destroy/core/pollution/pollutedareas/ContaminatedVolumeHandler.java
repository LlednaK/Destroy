package com.petrolpark.destroy.core.pollution.pollutedareas;

import com.petrolpark.client.outline.Outliner;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.core.pollution.PollutionHelper;
import com.petrolpark.destroy.util.geometry.BasicVolume;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Quintet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContaminatedVolumeHandler {
    static HashMap<BlockPos, ContaminatedVolume> constructingPollutionAreas = new HashMap<>();
    static HashMap<BlockPos, Quintet<Level, BlockPos, Float, Integer, CompoundTag>> dataMap = new HashMap<>();

    static HashMap<BlockPos, ContaminatedVolume> pollutedAreas = new HashMap<>();

    public static void polluteWithMixture(Level level, BlockPos pos, float multiplier, int amount, CompoundTag fluidTag) {
        ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, fluidTag);
        List<LegacySpecies> species = mixture.getContents(true);
        HashMap<LegacySpecies, Double> composition = new HashMap<>();
        for (LegacySpecies molecule : species) {
            double concentration = mixture.getConcentrationOf(molecule) * amount / 1000;
            composition.put(molecule, concentration);
        }
        constructingPollutionAreas.put(pos, new ContaminatedVolume(level, pos, composition, 50, 50));
        dataMap.put(pos, new Quintet<>(level, pos, multiplier, amount, fluidTag));
    }

    public static void newPollutionArea(Level level, BlockPos emissionPos) {
        constructingPollutionAreas.put(emissionPos, new ContaminatedVolume(level, emissionPos, 50, 50));
    }

    public static void checkStatuses() {
        for ( Map.Entry<BlockPos, ContaminatedVolume> entry : constructingPollutionAreas.entrySet()) {
            BlockPos areaPos = entry.getKey();
            ContaminatedVolume pollutedArea = entry.getValue();

            try {
                ContaminatedVolume.Status areaStatus = pollutedArea.getStatus();
                if (areaStatus != ContaminatedVolume.Status.WORKING) {
                    Quintet<Level, BlockPos, Float, Integer, CompoundTag> data = dataMap.get(areaPos);
                    if (areaStatus == ContaminatedVolume.Status.INFINITE) {
                        Destroy.LOGGER.info("Infinite area found :/");
                        PollutionHelper.polluteMixture(data.getA(), data.getB(), data.getC(), data.getD(), data.getE(), false);
                    } else {
                        Destroy.LOGGER.info("Yay! \n Created area of {} cubes", pollutedArea.getSize());
                        pollutedAreas.put(areaPos, pollutedArea);
                        for ( BasicVolume area : pollutedArea.getAreas()) {
                            Outliner.getInstance().showAABB(area, area.toAABB(), Integer.MAX_VALUE).colored(Color.RED);
                        }
                    }
                    constructingPollutionAreas.remove(areaPos);
                    dataMap.remove(areaPos, data);
                }
            } catch ( Exception e ) {
                Destroy.LOGGER.error("Polluted area construction failed :/");
                constructingPollutionAreas.remove(areaPos);
                dataMap.remove(areaPos);
                e.printStackTrace();
            }
        }
    }

    public static Optional<ContaminatedVolume> getVolumeForPos(Vec3 pos) {
        for (ContaminatedVolume volume : pollutedAreas.values()) {
            if (volume.contains(pos)) return Optional.of(volume);
        }

        return Optional.empty();
    }

    public static void clear() {
        constructingPollutionAreas.clear();
        dataMap.clear();

        pollutedAreas.clear();

        Outliner.getInstance().getOutlines().clear();
    }
}
