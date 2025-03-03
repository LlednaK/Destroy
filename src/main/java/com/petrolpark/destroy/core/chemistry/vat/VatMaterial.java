package com.petrolpark.destroy.core.chemistry.vat;

import com.petrolpark.recipe.ingredient.BlockIngredient;

public record VatMaterial(double maxPressure, double thermalConductivity, boolean transparent, BlockIngredient<?> blocks) {
    
};
