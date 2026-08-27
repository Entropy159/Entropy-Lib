package dev.entropy159.entropylib.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue UNCAP_ENCHANT_COMMAND = BUILDER.comment("If true, removes the enchant level with /enchant").define("uncapEnchantCommand", true);
    public static final ModConfigSpec.BooleanValue SEND_EFFECTS_TO_ALL = BUILDER.comment("If true, all players will receive mob effect packets for other entities").define("sendEffectsToAll", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
