package com.petrolpark.destroy.chemistry.api.thermo;

/**
 * An Equation of State, giving thermodynamic state functions in terms of state variables.
 * @since Destroy 0.1.1
 * @author petrolpark
 */
public interface IEoS {
    
    /**
     * 
     * @param T Temperature in kelvins
     * @param Vm Molar volume in moles per cubic meter
     * @return Pressure in pascals
     * @since Destroy 0.1.1
     * @author petrolpark
     */
    public double getPressure(double T, double Vm);
};
