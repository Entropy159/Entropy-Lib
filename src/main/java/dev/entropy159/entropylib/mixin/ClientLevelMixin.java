package dev.entropy159.entropylib.mixin;

import dev.entropy159.entropylib.util.HasBarrierParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Redirect(method = "getMarkerParticleTarget", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    private boolean customBarriers(Set<Item> instance, Object o) {
        return instance.contains(o) || o instanceof BlockItem item && item.getBlock() instanceof HasBarrierParticle;
    }
}
