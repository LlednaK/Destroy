package com.petrolpark.destroy.core.chemistry.hazard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.petrolpark.destroy.DestroyDamageSources;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.DestroyMobEffects;
import com.petrolpark.destroy.DestroyTags.Items;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class ChemistryHazardHelper {
  
    /**
     * Apply the effects of exposure to a Mixture to an Entity.
     * @param level
     * @param entity
     * @param mixture
     * @param skinContact Whether the entity has contact with the Mixture on a body part other than their mouth (if they are "submerged" in it), regardless of any protective clothing
     */
    public static void damage(Level level, LivingEntity entity, FluidStack stack, boolean skinContact) {
        if (!DestroyFluids.isMixture(stack)) return;
        ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, stack.getOrCreateChildTag("Mixture"));
        if (mixture.isEmpty()) return;

        boolean burning = mixture.getConcentrationOf(DestroyMolecules.PROTON) > 0.01f || mixture.getConcentrationOf(DestroyMolecules.HYDROXIDE) > 0.01f;
        boolean smelly = false;
        boolean carcinogen = false;
        boolean lacrimator = false;
        boolean lead = false;
        LegacySpecies toxicMolecule = null;

        for (LegacySpecies molecule : mixture.getContents(true)) {
            if (molecule.hasTag(DestroyMolecules.Tags.ACUTELY_TOXIC)) toxicMolecule = molecule;
            if (molecule.hasTag(DestroyMolecules.Tags.SMELLY)) smelly = true;
            if (molecule.hasTag(DestroyMolecules.Tags.CARCINOGEN)) carcinogen = true;
            if (molecule.hasTag(DestroyMolecules.Tags.LACRIMATOR)) lacrimator = true;
            if (molecule.getMolecularFormula().containsKey(LegacyElement.LEAD)) lead = true;
            if (toxicMolecule != null && smelly && carcinogen && lacrimator && lead) break;
        };

        boolean noseProtected = Protection.NOSE.isProtected(entity);
        boolean mouthProtected = Protection.MOUTH.isProtected(entity);
        boolean eyesProtected = Protection.EYES.isProtected(entity);
        boolean sensitivePartsProtected = noseProtected && mouthProtected && eyesProtected;
        boolean wholeBodyProtected = sensitivePartsProtected && Protection.HEAD.isProtected(entity) && Protection.BODY.isProtected(entity) && Protection.LEGS.isProtected(entity) && Protection.FEET.isProtected(entity);

        // Wah wah cry like a little baby
        if (lacrimator && !eyesProtected) {
            entity.addEffect(new MobEffectInstance(DestroyMobEffects.CRYING.get(), 600, 0, false, false, true));
        };
        
        // Smelly chemicals
        if (smelly && !noseProtected && !(entity instanceof Player player && player.isCreative())) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
        };

        // Acutely toxic Molecules
        if (toxicMolecule != null && !sensitivePartsProtected && !level.isClientSide()) {
            EntityChemicalPoisonCapability.setMolecule(entity, toxicMolecule);
            if (!entity.hasEffect(DestroyMobEffects.CHEMICAL_POISON.get())) entity.addEffect(new MobEffectInstance(DestroyMobEffects.CHEMICAL_POISON.get(), 219, 0, false, false));
        };
        
        // Carcinogens
        if (carcinogen && (!noseProtected || !mouthProtected)) {
            if (entity.getRandom().nextInt(2400) == 0) entity.addEffect(DestroyMobEffects.cancerInstance());
        };

        // Lead poisoning
        if (lead && (!noseProtected || !mouthProtected)) {
            if (entity.getRandom().nextInt(2400) == 0) DestroyMobEffects.increaseEffectLevel(entity, DestroyMobEffects.LEAD_POISONING.get(), 1, -1);
        };

        // Acid/base burns
        if (skinContact) {
            if (wholeBodyProtected && (burning || smelly || carcinogen || toxicMolecule != null)) { // If there is a hazard
                for (ItemStack armor : entity.getArmorSlots()) contaminate(armor, stack);
            } else {
                if (burning) {
                    entity.hurt(DestroyDamageSources.chemicalBurn(level), 5f);
                };
            };
        };

    };
    
    public static void contaminate(ItemStack stack, FluidStack fluidStack) {
        if (Items.CONTAMINABLE.matches(stack.getItem())) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("ContaminatingFluid")) return;
            CompoundTag fluidTag = new CompoundTag();
            fluidStack.writeToNBT(fluidTag);
            tag.put("ContaminatingFluid", fluidTag);
        };
    };

    public static void decontaminate(ItemStack stack) {
        stack.getOrCreateTag().remove("ContaminatingFluid");
    };

    public static enum Protection {
        FEET(EquipmentSlot.FEET, Items.CHEMICAL_PROTECTION_FEET),
        LEGS(EquipmentSlot.LEGS, Items.CHEMICAL_PROTECTION_LEGS),
        BODY(EquipmentSlot.CHEST, Items.CHEMICAL_PROTECTION_CHEST),
        HEAD(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_HEAD),
        EYES(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_EYES),
        NOSE(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_NOSE),
        MOUTH(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_MOUTH),
        /**
         * Whether the mouth is obstructed, preventing things that require an open mouth like eating.
         */
        MOUTH_COVERED(EquipmentSlot.HEAD, Items.CHEMICAL_PROTECTION_MOUTH);

        public final Items defaultTag;

        private List<Predicate<LivingEntity>> tests = new ArrayList<>();

        public void registerTest(Predicate<LivingEntity> testForProtection) {
            tests.add(testForProtection);
        };

        public boolean isProtected(LivingEntity livingEntity) {
            return tests.stream().anyMatch(t -> t.test(livingEntity));
        };

        private Protection(EquipmentSlot defaultEquipmentSlot, Items defaultTag) {
            this.defaultTag = defaultTag;
            registerTest(le -> defaultTag.matches(le.getItemBySlot(defaultEquipmentSlot).getItem()));
        };

        static {
            NOSE.registerTest(le -> le.hasEffect(DestroyMobEffects.FRAGRANCE.get()));
        };
    };
};
