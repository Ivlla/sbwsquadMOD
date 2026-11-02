package tech.squadmc.squadmcor.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class HumveeTowEntity extends HumveeEntity {

    public int towShakeTicks = 0;

    public HumveeTowEntity(EntityType<? extends HumveeTowEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 102) {
            this.towShakeTicks = 4;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () ->
                    tech.squadmc.squadmcor.client.ClientAccess.handleHumveeTowClientTick(this)
            );
        }
    }
}
