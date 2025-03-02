package com.petrolpark.destroy.chemistry.api.thermo;

import com.petrolpark.destroy.chemistry.api.util.Constants;

/**
 * Peng-Robinson Equation of State.
 * @since Destroy 0.1.1
 * @author petrolpark
 */
public class PREoS implements IEoS {

    public final double Tc;
    public final double a;
    public final double b;
    public final double acentricity;

    /**
     * 
     * @param Tc Critical temperature in kelvins
     * @param Pc Critical pressure in pascals
     */
    public PREoS(double Tc, double pc, double acentricity) {
        this.Tc = Tc;
        a = 0.457235d * Constants.GAS_CONSTANT * Constants.GAS_CONSTANT * Tc * Tc / pc;
        b = 0.077796d * Constants.GAS_CONSTANT * Tc / pc;
        this.acentricity = acentricity;
    };

    @Override
    public double getPressure(double T, double Vm) {
        return ((Constants.GAS_CONSTANT * T) / (Vm - b)) - ((a * attraction(T)) / (Vm * (Vm + b) + b * (Vm - b)));
    };

    public final double attraction(double T) {
        return (1d + (0.37464d * 1.54226d * acentricity - 0.26992d * acentricity * acentricity) * (1d - Math.sqrt(T))) * (1d + (0.37464d * 1.54226d * acentricity - 0.26992d * acentricity * acentricity) * (1d - Math.sqrt(T)));
    };
    
};
