package dev.entropy159.entropylib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.entropy159.entropylib.util.InvisEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
    private boolean moreInvis(boolean original) {
        if ((Object) this instanceof LivingEntity entity) {
            for (var effect : entity.getActiveEffects()) {
                if (effect.getEffect().value() instanceof InvisEffect) {
                    return true;
                }
            }
        }
        return original;
    }
}
