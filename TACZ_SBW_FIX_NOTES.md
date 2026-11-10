# SBW + TACZ compatibility (Forge 1.20.1)

This branch is a first implementation and requires compilation / in-game validation.

## Changes
- `TaczWheelDamage`: server-side TACZ kinetic bullet trajectory vs SBW wheel OBBs. Each projectile can damage a given vehicle once, at 12 HP per crossing. SBW's `WheelLoss` handles actual wheel detachment.
- `VehicleLauncherDamageMixin`: reduces computed explosive damage to tracked vehicles to 25%, making track destruction less likely from a single M79 explosion. This also reduces calculated hull damage, not just track damage.
- `MtsFxServer`: downgrades mappings for medium-to-epic explosion particle presets; gameplay explosion radius remains unchanged.

## Validation needed
- Build with Java 17, the exact SBW 0.8.9.2 and AAS dependencies from `libs/`.
- Test TACZ guns against stationary and moving wheeled vehicles; TACZ's internal bullet motion may not match the Forge-level tick trace in every case.
- Test M79 direct hits and splash versus tracked vehicles, including the custom explosion damage source tags.
- Check whether visual effects need further downsizing of individual `assets/squadmc/mts_fx/*.json` particle entries.

This branch does NOT claim to be a verified fully working binary. No jar is supplied.
