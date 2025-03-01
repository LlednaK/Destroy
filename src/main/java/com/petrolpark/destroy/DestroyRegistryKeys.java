package com.petrolpark.destroy;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterial;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryBuilder;

public class DestroyRegistryKeys {
    
    public static final ResourceKey<Registry<VatMaterial>> VAT_MATERIAL = REGISTRATE.makeRegistry("vat_material", RegistryBuilder::new); // Data
    //TODO BM4x4 generator types (probably in library)
    //TODO Item Explosive Properties
    //TODO Seismology providers
};
