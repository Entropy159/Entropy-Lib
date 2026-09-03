package dev.entropy159.entropylib.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface SwitchItemListener {
    default void onSwitch(LivingEntity entity, InteractionHand hand, ItemStack old, ItemStack current) {

    }

    default void switchTo(LivingEntity entity, InteractionHand hand, ItemStack old, ItemStack current) {
        onSwitch(entity, hand, old, current);
    }

    default void switchFrom(LivingEntity entity, InteractionHand hand, ItemStack old, ItemStack current) {
        onSwitch(entity, hand, old, current);
    }
}
