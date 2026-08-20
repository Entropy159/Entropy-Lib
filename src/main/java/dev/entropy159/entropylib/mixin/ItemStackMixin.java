package dev.entropy159.entropylib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.entropy159.entropylib.events.IgnoreAdventureModeEvent;
import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow
    public abstract Item getItem();

    @ModifyReturnValue(method = "canPlaceOnBlockInAdventureMode", at = @At("RETURN"))
    private boolean allowPlacingTeamBlocks(boolean original, @Local(argsOnly = true) BlockInWorld block) {
        IgnoreAdventureModeEvent event = NeoForge.EVENT_BUS.post(new IgnoreAdventureModeEvent(null, block.getPos(), block.getState(), (ItemStack) (Object) this, true));
        return original || event.shouldBypass();
    }

    @ModifyReturnValue(method = "canBreakBlockInAdventureMode", at = @At("RETURN"))
    private boolean allowBreakingTeamBlocks(boolean original, @Local(argsOnly = true) BlockInWorld block) {
        IgnoreAdventureModeEvent event = NeoForge.EVENT_BUS.post(new IgnoreAdventureModeEvent(null, block.getPos(), block.getState(), (ItemStack) (Object) this, false));
        return original || event.shouldBypass();
    }

    @Redirect(method = "consume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasInfiniteMaterials()Z"))
    private boolean infinite(LivingEntity instance) {
        return instance.hasInfiniteMaterials() || getOrDefault(EntropyComponents.INFINITE, false);
    }
}
