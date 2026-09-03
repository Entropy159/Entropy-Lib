package dev.entropy159.entropylib.mixin;

import dev.entropy159.entropylib.config.ServerConfig;
import dev.entropy159.entropylib.util.SwitchItemListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getMainHandItem();

    @Shadow
    public abstract ItemStack getOffhandItem();

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

    @Inject(method = "setItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private void switchListeners(InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        LivingEntity living = (LivingEntity) (Object) this;
        ItemStack old = switch (hand) {
            case MAIN_HAND -> getMainHandItem();
            case OFF_HAND -> getOffhandItem();
        };
        if (stack.getItem() instanceof SwitchItemListener listener) {
            listener.switchTo(living, hand, old, stack);
        }
        if (old.getItem() instanceof SwitchItemListener listener) {
            listener.switchFrom(living, hand, old, stack);
        }
    }
}
