package dev.entropy159.entropylib.mixin;

import dev.entropy159.entropylib.util.InvisEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void updateOnAdd(MobEffectInstance effectInstance, Entity entity, CallbackInfo ci) {
        if (!level().isClientSide() && effectInstance.getEffect().value() instanceof InvisEffect invis) {
            invis.update(this, true);
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void updateOnRemove(MobEffectInstance effectInstance, CallbackInfo ci) {
        if (!level().isClientSide() && effectInstance.getEffect().value() instanceof InvisEffect invis) {
            invis.update(this, false);
        }
    }
}
