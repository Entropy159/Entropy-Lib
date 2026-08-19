package dev.entropy159.entropylib.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.literal;

public class InfiniteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("infinite").requires(ctx -> ctx.isPlayer() && ctx.hasPermission(2)).executes(ctx -> {
            if (ctx.getSource().getPlayer() != null) {
                ItemStack stack = ctx.getSource().getPlayer().getMainHandItem();
                if (stack.has(EntropyComponents.INFINITE)) {
                    stack.remove(EntropyComponents.INFINITE);
                    ctx.getSource().sendSuccess(() -> Component.translatable("message.entropylib.removed_infinite").withStyle(ChatFormatting.YELLOW), false);
                } else {
                    stack.set(EntropyComponents.INFINITE, true);
                    ctx.getSource().sendSuccess(() -> Component.translatable("message.entropylib.added_infinite").withStyle(ChatFormatting.GREEN), false);
                }
            }
            return 1;
        }));
    }
}
