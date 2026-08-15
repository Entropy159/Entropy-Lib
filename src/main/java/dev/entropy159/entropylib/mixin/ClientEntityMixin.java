package dev.entropy159.entropylib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.entropy159.entropylib.client.EntropyLibClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class ClientEntityMixin {
    @Shadow
    public abstract int getId();

    @ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
    private boolean moreInvis(boolean original) {
        return original || EntropyLibClient.UNLERP_ENTITIES.containsKey(getId());
    }
}
