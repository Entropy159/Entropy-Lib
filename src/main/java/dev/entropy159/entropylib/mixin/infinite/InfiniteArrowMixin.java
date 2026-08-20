package dev.entropy159.entropylib.mixin.infinite;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArrowItem.class)
public class InfiniteArrowMixin {
    @ModifyReturnValue(method = "isInfinite", at = @At("RETURN"))
    private boolean infinite(boolean original, @Local(argsOnly = true, index = 1) ItemStack ammo) {
        return original || ammo.getOrDefault(EntropyComponents.INFINITE, false);
    }
}
