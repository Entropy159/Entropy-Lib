package dev.entropy159.entropylib.client.util.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class RenderTypeUtil {
    public static List<CustomRenderType> RENDER_TYPES = new ArrayList<>();

    public static void register(RegisterShadersEvent event) {
        RENDER_TYPES.forEach(type -> {
            try {
                event.registerShader(new ShaderInstance(event.getResourceProvider(), type.getID(), type.getFormat()), type::setShader);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static CustomRenderType create(ResourceLocation id, VertexFormat format, VertexFormat.Mode mode, UnaryOperator<RenderType.CompositeState.CompositeStateBuilder> state) {
        var type = new CustomRenderType(id, format, mode, state);
        RENDER_TYPES.add(type);
        return type;
    }
}
