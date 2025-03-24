package com.petrolpark.petrolsparts.config;

import net.createmod.catnip.config.ConfigBase;

public class PPCServer extends ConfigBase {

    public final PPCStress stress = nested(1, PPCStress::new, Comments.stress);

    @Override
    public String getName() {
        return "server";
    };

    private static class Comments {
		static String stress = "Fine tune the kinetic stats of individual components";
	};
    
};
