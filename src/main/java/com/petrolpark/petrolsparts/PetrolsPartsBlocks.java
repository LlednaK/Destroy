package com.petrolpark.petrolsparts;

import static com.petrolpark.petrolsparts.PetrolsParts.REGISTRATE;

import com.petrolpark.compat.create.core.tube.TubeBlockItem;
import com.petrolpark.petrolsparts.config.PPCStress;
import com.petrolpark.petrolsparts.content.coaxial_gear.CoaxialGearBlock;
import com.petrolpark.petrolsparts.content.coaxial_gear.CoaxialGearBlockItem;
import com.petrolpark.petrolsparts.content.coaxial_gear.LongShaftBlock;
import com.petrolpark.petrolsparts.content.colossal_cogwheel.ColossalCogwheelBlock;
import com.petrolpark.petrolsparts.content.colossal_cogwheel.ColossalCogwheelBlockItem;
import com.petrolpark.petrolsparts.content.corner_shaft.AbstractCornerShaftBlock;
import com.petrolpark.petrolsparts.content.corner_shaft.CornerShaftBlock;
import com.petrolpark.petrolsparts.content.corner_shaft.EncasedCornerShaftBlock;
import com.petrolpark.petrolsparts.content.differential.DifferentialBlock;
import com.petrolpark.petrolsparts.content.differential.DummyDifferentialBlock;
import com.petrolpark.petrolsparts.content.hydraulic_transmission.HydraulicTransmissionBlock;
import com.petrolpark.petrolsparts.content.planetary_gearset.PlanetaryGearsetBlock;
import com.petrolpark.petrolsparts.content.pneumatic_tube.PneumaticTubeBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class PetrolsPartsBlocks {
    
    public static final BlockEntry<CoaxialGearBlock> COAXIAL_GEAR = REGISTRATE.block("coaxial_gear", CoaxialGearBlock::small)
        .initialProperties(AllBlocks.COGWHEEL)
        .properties(p -> p
            .sound(SoundType.WOOD)
            .mapColor(MapColor.DIRT)
            .noOcclusion()
        ).transform(PPCStress.setNoImpact())
        .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
        .transform(TagGen.axeOrPickaxe())
        .item(CoaxialGearBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<CoaxialGearBlock> LARGE_COAXIAL_GEAR = REGISTRATE.block("large_coaxial_gear", CoaxialGearBlock::large)
        .initialProperties(COAXIAL_GEAR)
        .transform(PPCStress.setNoImpact())
        .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
        .transform(TagGen.axeOrPickaxe())
        .item(CoaxialGearBlockItem::new)
        .build()
        .register();

    // public static final BlockEntry<ChainedCogwheelBlock> CHAINED_COGWHEEL = REGISTRATE.block("chained_cogwheel", ChainedCogwheelBlock::small)
    //     .initialProperties(AllBlocks.COGWHEEL)
    //     .properties(p -> p
    //         .noOcclusion()
    //     ).transform(PPCStress.setNoImpact())
    //     .register();

    // public static final BlockEntry<ChainedCogwheelBlock> CHAINED_LARGE_COGWHEEL = REGISTRATE.block("chained_large_cogwheel", ChainedCogwheelBlock::large)
    //     .initialProperties(CHAINED_COGWHEEL)
    //     .properties(p -> p
    //         .noOcclusion()
    //     ).transform(PPCStress.setNoImpact())
    //     .register();

    public static final BlockEntry<ColossalCogwheelBlock> COLOSSAL_COGWHEEL = REGISTRATE.block("colossal_cogwheel", ColossalCogwheelBlock::new)
        .initialProperties(AllBlocks.LARGE_WATER_WHEEL)
        .properties(p -> p
            .noOcclusion()
        ).transform(PPCStress.setNoImpact())
        .transform(TagGen.axeOrPickaxe())
        .item(ColossalCogwheelBlockItem::new)
        .transform(ModelGen.customItemModel())
        .register();

    public static final BlockEntry<DifferentialBlock> DIFFERENTIAL = REGISTRATE.block("differential", DifferentialBlock::new)
        .initialProperties(AllBlocks.LARGE_COGWHEEL)
        .properties(p -> p
            .noOcclusion()
            .sound(SoundType.WOOD)
		    .mapColor(MapColor.DIRT)
        ).transform(PPCStress.setNoImpact())
        .transform(TagGen.axeOrPickaxe())
        .item(CogwheelBlockItem::new)
        .transform(ModelGen.customItemModel())
        .register();

    public static final BlockEntry<DummyDifferentialBlock> DUMMY_DIFFERENTIAL = REGISTRATE.block("dummy_differential", DummyDifferentialBlock::new)
        .initialProperties(DIFFERENTIAL)
        .transform(PPCStress.setNoImpact())
        .register();

    public static final BlockEntry<CornerShaftBlock> CORNER_SHAFT = REGISTRATE.block("corner_shaft", CornerShaftBlock::new)
        .initialProperties(AllBlocks.SHAFT)
        .properties(p -> p
            .mapColor(MapColor.METAL)
            .noOcclusion()
        ).transform(PPCStress.setNoImpact())
        .transform(TagGen.pickaxeOnly())
        .item()
        .transform(ModelGen.customItemModel())
        .register();

    public static final BlockEntry<EncasedCornerShaftBlock> ANDESITE_ENCASED_CORNER_SHAFT = REGISTRATE.block("andesite_encased_corner_shaft", p -> new EncasedCornerShaftBlock(p, AllBlocks.ANDESITE_CASING::get))
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .noOcclusion()
            .mapColor(MapColor.PODZOL)
        ).transform(PPCStress.setNoImpact())
        .loot((p, lb) -> p.dropOther(lb, CORNER_SHAFT))
        .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING)))
		.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.ANDESITE_CASING, (s, f) -> !AbstractCornerShaftBlock.hasShaftTowards(s, f))))
		.transform(TagGen.axeOrPickaxe())
		.register();

    public static final BlockEntry<EncasedCornerShaftBlock> BRASS_ENCASED_CORNER_SHAFT = REGISTRATE.block("brass_encased_corner_shaft", p -> new EncasedCornerShaftBlock(p, AllBlocks.BRASS_CASING::get))
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .noOcclusion()
            .mapColor(MapColor.TERRACOTTA_BROWN)
        ).transform(PPCStress.setNoImpact())
        .loot((p, lb) -> p.dropOther(lb, CORNER_SHAFT))
        .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.BRASS_CASING)))
		.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.BRASS_CASING, (s, f) -> !AbstractCornerShaftBlock.hasShaftTowards(s, f))))
		.transform(TagGen.axeOrPickaxe())
		.register();

    public static final BlockEntry<HydraulicTransmissionBlock> HYDRAULIC_TRANSMISSION = REGISTRATE.block("hydraulic_transmission", HydraulicTransmissionBlock::new)
        .initialProperties(AllBlocks.MECHANICAL_CRAFTER)
        .properties(p -> p
            .noOcclusion()
        ).transform(PPCStress.setImpact(2.0))
        .transform(TagGen.axeOrPickaxe())
        .item(TubeBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<LongShaftBlock> LONG_SHAFT = REGISTRATE.block("long_shaft", LongShaftBlock::new)
        .initialProperties(AllBlocks.SHAFT)
        .transform(PPCStress.setNoImpact())
        .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
        .register();

    public static final BlockEntry<PlanetaryGearsetBlock> PLANETARY_GEARSET = REGISTRATE.block("planetary_gearset", PlanetaryGearsetBlock::new)
        .initialProperties(AllBlocks.LARGE_COGWHEEL)
        .properties(p -> p
            .noOcclusion()
            .sound(SoundType.WOOD)
		    .mapColor(MapColor.DIRT)
        ).transform(PPCStress.setNoImpact())
        .transform(TagGen.axeOrPickaxe())
        .item(CogwheelBlockItem::new)
        .transform(ModelGen.customItemModel())
        .register();

    public static final BlockEntry<PneumaticTubeBlock> PNEUMATIC_TUBE = REGISTRATE.block("pneumatic_tube", PneumaticTubeBlock::filterable)
        .initialProperties(HYDRAULIC_TRANSMISSION)
        .properties(p -> p
            .noOcclusion()
        ).transform(PPCStress.setImpact(2.0))
        .transform(TagGen.axeOrPickaxe())
        .item(TubeBlockItem::new)
        .build()
        .register();

    public static final void register() {};

};
