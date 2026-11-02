package tech.squadmc.squadmcor.client;

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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }

                if (entity.atgmShakeTicks > 0) {
                    float intensity = (entity.atgmShakeTicks / 10.0F) * 10.65F;
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }
            } else if (playerSeatIndex == 0) { // Водитель — SmokeLauncher (ДГ)
                // getShootAnimationTimer(0,0) достоверен только тут, на клиенте.
                // Сервер сам его не видит, поэтому пока водитель жмёт огонь,
                // раз в тик шлём "пульс", который продлевает grace-период ДГ
                // на сервере (см. SquadBaseVehicleEntity.pulseSmokeGenerator()).
                if (entity.getShootAnimationTimer(0, 0) > 0) {
                    tech.squadmc.squadmcor.network.ModNetworking.CHANNEL.sendToServer(
                            new tech.squadmc.squadmcor.network.SmokeGeneratorPulsePacket());
                }
            }
        }
    }

    public static void handleFenekClientTick(FenekEntity entity) {
        FenekSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // РќР°РІРѕРґС‡РёРє
                float shakeIntensity = 0.0F;

                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.20F;
                }

                if (shakeIntensity > 0) {
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }

                if (entity.atgmShakeTicks > 0) {
                    float intensity = (entity.atgmShakeTicks / 10.0F) * 10.65F;
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }

                if (entity.towShakeTicks > 0) {
                    float intensity = (entity.towShakeTicks / 10.0F) * 10.65F;
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }
            }
        }
    }

    public static void handlePumaClientTick(PumaEntity entity) {
        PumaSoundManager.updateSounds(entity);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);
            if (playerSeatIndex == 1) { // РќР°РІРѕРґС‡РёРє
                float shakeIntensity = 0.0F;

                // 1. РўСЂСЏСЃРєР° РѕС‚ СЃС‚СЂРµР»СЊР±С‹ РёР· 30-РјРј Р°РІС‚РѕРїСѓС€РєРё (Weapon 0)
                if (entity.getShootAnimationTimer(1, 0) > 0) {
                    shakeIntensity = 0.22F;
                }
                // 2. РўСЂСЏСЃРєР° РѕС‚ РїСѓР»РµРјС‘С‚Р° (Weapon 1)
                else if (entity.getShootAnimationTimer(1, 1) > 0) {
                    shakeIntensity = 0.05F;
                }
                // 3. Р›РµРіРєР°СЏ РїСЂРµРґРїСѓСЃРєРѕРІР°СЏ РІРёР±СЂР°С†РёСЏ РїСЂРё РЅР°Р¶Р°С‚РёРё РЅР° СЃРїСѓСЃРє РџРўРЈР  (Weapon 2)
                else if (entity.getShootAnimationTimer(1, 2) > 0) {
                    shakeIntensity = 0.12F;
                }

                if (shakeIntensity > 0) {
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }

                // 4. РњРѕС‰РЅС‹Р№ С‚РѕР»С‡РѕРє РІ РјРѕРјРµРЅС‚ С„Р°РєС‚РёС‡РµСЃРєРѕРіРѕ РІС‹Р»РµС‚Р° СЂР°РєРµС‚С‹ С‡РµСЂРµР· 1 СЃРµРєСѓРЅРґСѓ
                if (entity.missileShakeTicks > 0) {
                    float intensity = (entity.missileShakeTicks / 8.0F) * 3.5F;
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }
            }
        }
    }

    public static void handleUralGradClientTick(UralGradEntity entity) {
        UralGradSoundManager.updateSounds(entity);

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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
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
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }

                if (entity.towShakeTicks > 0) {
                    float intensity = (entity.towShakeTicks / 10.0F) * 10.65F;
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * intensity);
                    entity.towShakeTicks--;
                }
            }
        }
    }
    public static void handleZisClientTick(tech.squadmc.squadmcor.entity.empl.ZISEntity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() == entity) {
            int playerSeatIndex = entity.getSeatIndex(mc.player);

            // РЈ Р—РёРЎ-3 СЃС‚СЂРµР»РѕРє СЃРёРґРёС‚ РЅР° РјРµСЃС‚Рµ 0
            if (playerSeatIndex == 0) {
                float shakeIntensity = 0.0F;

                // РњРµСЃС‚Рѕ 0, РћСЂСѓРґРёРµ 0 (Cannon)
                if (entity.getShootAnimationTimer(0, 0) > 0) {
                    shakeIntensity = 3.22F; // РЎРёР»Р° С‚СЂСЏСЃРєРё РєР°Рє Сѓ РїСѓС€РєРё Рў-80
                }

                if (shakeIntensity > 0) {
                    mc.player.setXRot(mc.player.getXRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                    mc.player.setYRot(mc.player.getYRot() + (entity.getRandom().nextFloat() - 0.5F) * shakeIntensity);
                }
            }
        }
    }
}
