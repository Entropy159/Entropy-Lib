package dev.entropy159.entropylib.mixin;

import dev.entropy159.entropylib.config.ServerConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "sendEffectToPassengers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getPassengers()Ljava/util/List;"))
    private List<? extends Entity> updateOnAdd(LivingEntity instance) {
        if (!level().isClientSide() && ServerConfig.SEND_EFFECTS_TO_ALL.get()) {
            return level().players();
        }
        return instance.getPassengers();
    }

    @Redirect(method = "onEffectRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getPassengers()Ljava/util/List;"))
    private List<? extends Entity> updateOnRemove(LivingEntity instance) {
        if (!level().isClientSide() && ServerConfig.SEND_EFFECTS_TO_ALL.get()) {
            return level().players();
        }
        return instance.getPassengers();
    }
}
