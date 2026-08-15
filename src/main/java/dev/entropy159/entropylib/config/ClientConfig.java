package dev.entropy159.entropylib.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue ICON_FADE_RADIUS = BUILDER.comment("The radius from the center of the screen to fade icons in", "A value of 0.5 means half the distance from the edge to the center").defineInRange("iconFadeRadius", 0.1, 0, 1);

    public static final ModConfigSpec SPEC = BUILDER.build();
}