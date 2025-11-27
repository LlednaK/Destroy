package com.petrolpark.destroy.core.pollution.pollutedareas;

import javax.annotation.Nullable;

public interface IPollutionMaker {
    @Nullable
    ContaminatedVolume getCurrentContaminatedVolume();
    void setCurrentContaminatedVolume(ContaminatedVolume contaminatedVolume);
}
