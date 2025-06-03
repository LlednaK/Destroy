package com.petrolpark.destroy.compat.computercraft.apis;

import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.simibubi.create.compat.computercraft.implementation.CreateLuaTable;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import org.jetbrains.annotations.Nullable;

public class ChemistryApi implements ILuaAPI {

    @LuaFunction
    public final CreateLuaTable getMoleculeInfos(String id) {
        LegacySpecies molecule = LegacySpecies.getMolecule(id);
        CreateLuaTable infos = new CreateLuaTable();

        infos.putDouble("mass", molecule.getMass());
        infos.putDouble("boilingPoint", molecule.getBoilingPoint());
        infos.putDouble("density", molecule.getDensity());
        infos.putDouble("charge", molecule.getCharge());
        infos.putString("FROWNS", molecule.getFROWNSCode());

        return infos;
    }

    @Override
    public String[] getNames() {
       return new String[0];
    }

    @Override
    public @Nullable String getModuleName() {
        return "destroy.chemistry";
    }
}
