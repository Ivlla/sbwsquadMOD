package tech.squadmc.squadmcor.entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import com.atsuishio.superbwarfare.client.particle.CustomSmokeOption;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import tech.squadmc.squadmcor.entity.*;
import tech.squadmc.squadmcor.client.sound.*;

import java.lang.reflect.Field;
import java.util.Random;

public class ClientAccess {
    private static Field lifetimeField;
    private static Field gravityField;
    private static Field frictionField;
    private static Field quadSizeField;
    private static Field hasPhysicsField;

    static {
        try {
            lifetimeField = ObfuscationReflectionHelper.findField(Particle.class, "lifetime");
            gravityField = ObfuscationReflectionHelper.findField(Particle.class, "gravity");
            frictionField = ObfuscationReflectionHelper.findField(Particle.class, "friction");
            quadSizeField = ObfuscationReflectionHelper.findField(Particle.class, "quadSize");
            hasPhysicsField = ObfuscationReflectionHelper.findField(Particle.class, "hasPhysics");

            lifetimeField.setAccessible(true);
            gravityField.setAccessible(true);
            frictionField.setAccessible(true);
            quadSizeField.setAccessible(true);
            hasPhysicsField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawnSmoke(Level level, double x, double y, double z, double xs, double ys, double zs) {
        if (!level.isClientSide()) return;

        Minecraft mc = Minecraft.getInstance();
        Particle particle = mc.particleEngine.createParticle(new CustomSmokeOption(1.0F, 1.0F, 1.0F), x, y, z, xs, ys, zs);

        if (particle instanceof TextureSheetParticle && lifetimeField != null) {
            try {
                Random random = new Random();
                lifetimeField.set(particle, 600 + random.nextInt(400));
                gravityField.set(particle, -0.0001F);
                frictionField.set(particle, 0.99F);
                quadSizeField.set(particle, 10.0F + random.nextFloat() * 6.0F);
                hasPhysicsField.set(particle, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleBmd4ClientTick(bmd4Entity entity) {
        Bmd4SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }

                if (entity.atgmShakeTicks > 0) {
                    float intensity = (entity.atgmShakeTicks / 10.0F) * 10.65F;
                    entity.atgmShakeTicks--;
                }
            }
        }
    }

    public static void handleT64ClientTick(T64Entity entity) {
        T64SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 2.4F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }
            } else if (playerSeatIndex == 0) { // Р’РѕРґРёС‚РµР»СЊ вЂ” SmokeLauncher (Р”Р“)
                // getShootAnimationTimer(0,0) РґРѕСЃС‚РѕРІРµСЂРµРЅ С‚РѕР»СЊРєРѕ С‚СѓС‚, РЅР° РєР»РёРµРЅС‚Рµ.
                // РЎРµСЂРІРµСЂ СЃР°Рј РµРіРѕ РЅРµ РІРёРґРёС‚, РїРѕСЌС‚РѕРјСѓ РїРѕРєР° РІРѕРґРёС‚РµР»СЊ Р¶РјС‘С‚ РѕРіРѕРЅСЊ,
                // СЂР°Р· РІ С‚РёРє С€Р»С‘Рј "РїСѓР»СЊСЃ", РєРѕС‚РѕСЂС‹Р№ РїСЂРѕРґР»РµРІР°РµС‚ grace-РїРµСЂРёРѕРґ Р”Р“
                // РЅР° СЃРµСЂРІРµСЂРµ (СЃРј. SquadBaseVehicleEntity.pulseSmokeGenerator()).
                if (entity.getShootAnimationTimer(0, 0) > 0) {
                    tech.squadmc.squadmcor.network.ModNetworking.CHANNEL.sendToServer(
                            new tech.squadmc.squadmcor.network.SmokeGeneratorPulsePacket());
                }
            }
        }
    }

    /**
     * Дымогенератор: шлём пульс на сервер ТОЛЬКО если локальный игрок — водитель
     * (seat 0) именно этой машины и у неё идёт таймер оружия 0/0.
     */
    public static void sendSmokePulse(SquadBaseVehicleEntity v) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == v
                && v.getSeatIndex(mc.player) == 0
                && v.getShootAnimationTimer(0, 0) > 0) {
            tech.squadmc.squadmcor.network.ModNetworking.CHANNEL.sendToServer(
                    new tech.squadmc.squadmcor.network.SmokeGeneratorPulsePacket());
        }
    }

    public static void handleFenekClientTick(FenekEntity entity) {
        FenekSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Р СњР В°Р Р†Р С•Р Т‘РЎвЂЎР С‘Р С”
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.20F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }



    public static void handleBmp1ClientTick(Bmp1Entity entity) {
        Bmp1SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 1.35F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.05F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleBmp2ClientTick(bmp2Entity entity) {
        Bmp2SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }

                if (entity.atgmShakeTicks > 0) {
                    float intensity = (entity.atgmShakeTicks / 10.0F) * 10.65F;
                    entity.atgmShakeTicks--;
                }
            }
        }
    }

    public static void handleBrdm2ClientTick(brdmEntity entity) {
        Brdm2SoundManager.updateSounds(entity);
    }

    public static void handleBradleyClientTick(bradleyEntity entity) {
        BradleySoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.20F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.05F;
                }

                if (shakeIntensity > 0) {
                }

                if (entity.towShakeTicks > 0) {
                    float intensity = (entity.towShakeTicks / 10.0F) * 10.65F;
                    entity.towShakeTicks--;
                }
            }
        }
    }

    public static void handleBtr82ClientTick(btr82Entity entity) {
        entity.prevWheelRotation = entity.wheelRotation;
        entity.prevSteeringAngle = entity.steeringAngle;
        entity.steeringAngle = entity.getEntityData().get(btr82Entity.STEERING_ANGLE);

        Vec3 motion = entity.getDeltaMovement();
        Vec3 look = entity.getLookAngle();
        double dot = motion.x * look.x + motion.z * look.z;
        float speed = (float) motion.horizontalDistance();
        float direction = dot < 0 ? -1.0F : 1.0F;

        entity.wheelRotation += speed * direction * 15.0F;

        Btr82SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleLav25ClientTick(lav25Entity entity) {
        Lav25SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleM1a2ClientTick(m1a2Entity entity) {
        m1a2SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 2.8F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.05F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleSprutClientTick(sprutEntity entity) {
        sprutSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 4.2F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.08F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleStrykerM2ClientTick(StrykerM2Entity entity) {
        StrykerM2SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.8F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleStrykerMortarClientTick(StrykerMortalEntity entity) {
        StrykerMortarSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 3.8F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleT72AVClientTick(T72AVEntity entity) {
        T72AVSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 2.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handlePumaClientTick(PumaEntity entity) {
        PumaSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Р СњР В°Р Р†Р С•Р Т‘РЎвЂЎР С‘Р С”
                float shakeIntensity = 0.0F;

                // 1. Р СћРЎР‚РЎРЏРЎРѓР С”Р В° Р С•РЎвЂљ РЎРѓРЎвЂљРЎР‚Р ВµР В»РЎРЉР В±РЎвЂ№ Р С‘Р В· 30-Р СР С Р В°Р Р†РЎвЂљР С•Р С—РЎС“РЎв‚¬Р С”Р С‘ (Weapon 0)
                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.22F;
                }
                // 2. Р СћРЎР‚РЎРЏРЎРѓР С”Р В° Р С•РЎвЂљ Р С—РЎС“Р В»Р ВµР СРЎвЂРЎвЂљР В° (Weapon 1)
                else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.05F;
                }
                // 3. Р вЂєР ВµР С–Р С”Р В°РЎРЏ Р С—РЎР‚Р ВµР Т‘Р С—РЎС“РЎРѓР С”Р С•Р Р†Р В°РЎРЏ Р Р†Р С‘Р В±РЎР‚Р В°РЎвЂ Р С‘РЎРЏ Р С—РЎР‚Р С‘ Р Р…Р В°Р В¶Р В°РЎвЂљР С‘Р С‘ Р Р…Р В° РЎРѓР С—РЎС“РЎРѓР С” Р СџР СћР Р€Р В  (Weapon 2)
                else if (entity.getShootAnimationTimer(1, 2) > 0) {
                    shakeIntensity = 0.12F;
                }

                if (shakeIntensity > 0) {
                }

                // 4. Р СљР С•РЎвЂ°Р Р…РЎвЂ№Р в„– РЎвЂљР С•Р В»РЎвЂЎР С•Р С” Р Р† Р СР С•Р СР ВµР Р…РЎвЂљ РЎвЂћР В°Р С”РЎвЂљР С‘РЎвЂЎР ВµРЎРѓР С”Р С•Р С–Р С• Р Р†РЎвЂ№Р В»Р ВµРЎвЂљР В° РЎР‚Р В°Р С”Р ВµРЎвЂљРЎвЂ№ РЎвЂЎР ВµРЎР‚Р ВµР В· 1 РЎРѓР ВµР С”РЎС“Р Р…Р Т‘РЎС“
                if (entity.missileShakeTicks > 0) {
                    float intensity = (entity.missileShakeTicks / 8.0F) * 3.5F;
                    entity.missileShakeTicks--;
                }
            }
        }
    }

    public static void handleLeopardClientTick(leopardEntity entity) {
        leopardSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 2.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleT80ClientTick(t80Entity entity) {
        t80SoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 3.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleUralGradClientTick(UralGradEntity entity) {
        UralGradSoundManager.updateSounds(entity);

        if (entity.getShootAnimationTimer(1, 0) > 0) {
            float turretYaw = entity.getYRot() - entity.getTurretYRot();
            float turretPitch = entity.getTurretXRot();

            float f = -turretPitch * ((float)Math.PI / 180F);
            float f1 = (float)Math.sin(f);
            float f2 = (float)Math.cos(f);
            float f3 = -turretYaw * ((float)Math.PI / 180F);

            double lx = Math.sin(f3) * f2;
            double ly = f1;
            double lz = Math.cos(f3) * f2;

            Vec3 lookVec = new Vec3(lx, ly, lz);

            Vec3 turretCenterLocal = new Vec3(0.011, 2.5, -2.57);
            float bodyYawRad = -entity.getYRot() * ((float)Math.PI / 180F);
            double tx = turretCenterLocal.x * Math.cos(bodyYawRad) - turretCenterLocal.z * Math.sin(bodyYawRad);
            double tz = turretCenterLocal.x * Math.sin(bodyYawRad) + turretCenterLocal.z * Math.cos(bodyYawRad);
            Vec3 turretPivotWorld = entity.position().add(tx, turretCenterLocal.y, tz);

            double distanceBack = 1.95;
            Vec3 smokeSpawnPos = turretPivotWorld.subtract(lookVec.scale(distanceBack));

            Vec3 jetVelocity = lookVec.scale(-1.3);
            for (int i = 0; i < 3; i++) {
                double rx = (entity.getRandom().nextDouble() - 0.5) * 0.3;
                double ry = (entity.getRandom().nextDouble() - 0.5) * 0.3;
                double rz = (entity.getRandom().nextDouble() - 0.5) * 0.3;

                double vx = jetVelocity.x + (entity.getRandom().nextDouble() - 0.5) * 0.25;
                double vy = jetVelocity.y + (entity.getRandom().nextDouble() - 0.5) * 0.15;
                double vz = jetVelocity.z + (entity.getRandom().nextDouble() - 0.5) * 0.25;

                entity.level().addParticle(net.minecraft.core.particles.ParticleTypes.FIREWORK,
                        smokeSpawnPos.x + rx, smokeSpawnPos.y + ry, smokeSpawnPos.z + rz,
                        vx, vy, vz);
                entity.level().addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                        smokeSpawnPos.x + rx, smokeSpawnPos.y + ry, smokeSpawnPos.z + rz,
                        vx * 0.8, vy * 0.8, vz * 0.8);
            }

            double groundY = entity.getY() + 0.15;
            Vec3 groundSmokePos = new Vec3(smokeSpawnPos.x, groundY, smokeSpawnPos.z);

            for (int i = 0; i < 6; i++) {
                double rx = (entity.getRandom().nextDouble() - 0.5) * 1.5;
                double rz = (entity.getRandom().nextDouble() - 0.5) * 1.5;

                double vx = (rx * 0.03) + (lookVec.x * -0.04);
                double vy = 0.001 + entity.getRandom().nextDouble() * 0.003;
                double vz = (rz * 0.03) + (lookVec.z * -0.04);

                entity.level().addParticle(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        groundSmokePos.x + rx, groundSmokePos.y, groundSmokePos.z + rz,
                        vx, vy, vz);

                entity.level().addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,
                        groundSmokePos.x + rx, groundSmokePos.y, groundSmokePos.z + rz,
                        vx * 0.6, vy * 0.4, vz * 0.6);
            }
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            boolean isValidSeat = (playerSeatIndex == 0 || playerSeatIndex == 1);

            if (isValidSeat) {
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = (playerSeatIndex == 1) ? 0.4F : 0.15F;
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }

    public static void handleHumveeTowClientTick(HumveeTowEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // Gunner
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 3.22F;
                } else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.07F;
                }

                if (shakeIntensity > 0) {
                }

                if (entity.towShakeTicks > 0) {
                    float intensity = (entity.towShakeTicks / 10.0F) * 10.65F;
                    entity.towShakeTicks--;
                }
            }
        }
    }
    public static void handleZisClientTick(tech.squadmc.squadmcor.entity.empl.ZISEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);

            // Р Р€ Р вЂ”Р С‘Р РЋ-3 РЎРѓРЎвЂљРЎР‚Р ВµР В»Р С•Р С” РЎРѓР С‘Р Т‘Р С‘РЎвЂљ Р Р…Р В° Р СР ВµРЎРѓРЎвЂљР Вµ 0
            if (playerSeatIndex == 0) {
                float shakeIntensity = 0.0F;

                // Р СљР ВµРЎРѓРЎвЂљР С• 0, Р С›РЎР‚РЎС“Р Т‘Р С‘Р Вµ 0 (Cannon)
                if (entity.getShootAnimationTimer(0, 0) > 0) {
                    shakeIntensity = 3.22F; // Р РЋР С‘Р В»Р В° РЎвЂљРЎР‚РЎРЏРЎРѓР С”Р С‘ Р С”Р В°Р С” РЎС“ Р С—РЎС“РЎв‚¬Р С”Р С‘ Р Сћ-80
                }

                if (shakeIntensity > 0) {
                }
            }
        }
    }
}

