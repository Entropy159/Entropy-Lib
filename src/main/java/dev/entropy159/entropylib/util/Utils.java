package dev.entropy159.entropylib.util;

import dev.entropy159.entropylib.EntropyLib;
import dev.entropy159.entropylib.network.toClient.InstantTeleportPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {
    public static @Nullable Entity getTargetedEntity(Entity source, double range, Predicate<Entity> predicate) {
        Vec3 start = source.getEyePosition();
        Vec3 end = start.add(source.getLookAngle().scale(range));
        AABB box = source.getBoundingBox().expandTowards(source.getLookAngle().scale(range)).inflate(1);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(source, start, end, box, predicate, Double.MAX_VALUE);
        return hit == null ? null : hit.getEntity();
    }

    public static <T> ListTag listToTag(List<T> list, Function<T, Tag> valueFunction) {
        ListTag tag = new ListTag();
        list.forEach(obj -> tag.add(valueFunction.apply(obj)));
        return tag;
    }

    public static <T> ArrayList<T> tagToArrayList(ListTag tag, Function<Tag, T> converter) {
        ArrayList<T> list = new ArrayList<>();
        if (tag != null) tag.forEach(t -> list.add(converter.apply(t)));
        return list;
    }

    public static <T, P> CompoundTag mapToTag(java.util.Map<T, P> map, Function<T, String> keyFunction, Function<P, Tag> valueFunction) {
        CompoundTag tag = new CompoundTag();
        map.forEach((key, value) -> tag.put(keyFunction.apply(key), valueFunction.apply(value)));
        return tag;
    }

    public static <T, P> HashMap<T, P> tagToHashMap(CompoundTag tag, Function<String, T> keyConverter, Function<Tag, P> valueConverter) {
        HashMap<T, P> map = new HashMap<>();
        if (tag != null)
            tag.getAllKeys().forEach(key -> map.put(keyConverter.apply(key), valueConverter.apply(tag.get(key))));
        return map;
    }

    public static String toTitleCase(String input) {
        return Arrays.stream(input.toLowerCase().split("_")).filter(s -> !s.isEmpty()).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
    }

    public static void instantTeleport(Entity entity, Vec3 pos) {
        instantTeleport(entity, pos.x, pos.y, pos.z);
    }

    public static void instantTeleport(Entity entity, double x, double y, double z) {
        PacketDistributor.sendToAllPlayers(new InstantTeleportPacket(entity.getId(), new Vec3(x, y, z)));
        entity.teleportTo(x, y, z);
    }

    public static void instantTeleport(Entity entity, BlockPos pos) {
        instantTeleport(entity, pos.getBottomCenter());
    }

    public static void instantTeleport(Entity entity, GlobalPos pos) {
        MinecraftServer server = entity.getServer();
        if (server != null) {
            ServerLevel target = server.getLevel(pos.dimension());
            if (target == entity.level()) {
                instantTeleport(entity, pos.pos());
            } else if (target != null) {
                entity.teleportTo(target, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), Set.of(), entity.getYRot(), entity.getXRot());
            } else {
                EntropyLib.LOGGER.error("Tried teleporting an entity to a nonexistent level {}", pos.dimension().location());
            }
        }
    }

    public static float lerp(float a, float b, float f) {
        return (float) (a * (1.0 - f)) + (b * f);
    }

    public static int lerpColors(int one, int two, float factor) {
        Color color1 = new Color(one);
        Color color2 = new Color(two);
        int red = (int) lerp(color1.getRed(), color2.getRed(), factor);
        int green = (int) lerp(color1.getGreen(), color2.getGreen(), factor);
        int blue = (int) lerp(color1.getBlue(), color2.getBlue(), factor);
        int alpha = (int) lerp(color1.getAlpha(), color2.getAlpha(), factor);
        return new Color(red, green, blue, alpha).getRGB();
    }

    public static int multiplyAlpha(int color, float mult) {
        return colorWithAlpha(color, new Color(color, true).getAlpha() / 255f * mult);
    }

    public static int colorWithAlpha(int original, float newAlpha) {
        return colorWithAlpha(original, Math.round(newAlpha * 255));
    }

    public static int colorWithAlpha(int original, int newAlpha) {
        return (original & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static void broadcastToOps(MinecraftServer server, @Nullable ServerPlayer origin, Component message) {
        Component messageWithPrefix = (origin == null ? message.copy() : Component.translatable("chat.type.admin", origin.getDisplayName(), message)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        if (server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (player != origin && server.getPlayerList().isOp(player.getGameProfile())) {
                    player.sendSystemMessage(messageWithPrefix);
                }
            });
        }

        if (server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
            server.sendSystemMessage(messageWithPrefix);
        }
    }

    public static void playSoundForEveryone(MinecraftServer server, SoundEvent event, SoundSource source) {
        server.getPlayerList().getPlayers().forEach(player -> playSoundForPlayer(player, event, source));
    }

    public static void playSoundForPlayer(ServerPlayer player, SoundEvent event, SoundSource source) {
        player.connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvent.createFixedRangeEvent(event.getLocation(), 16)), source, player.getEyePosition().x, player.getEyePosition().y, player.getEyePosition().z, 1, 1, player.serverLevel().getRandom().nextLong()));
    }
}