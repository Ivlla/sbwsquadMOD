// Based on Frontline Combat Pack by Commissar_Mado (see FCP_LICENSE.txt). Modified for SquadMC.
package tech.squadmc.squadmcor.heli.entity.vehicle.engine;

/** Vehicles carrying the synced engine state used by {@link EngineControl}. */
public interface EngineControlled {

    byte fcp$getEngineState();

    void fcp$setEngineState(byte state);
}
