package dev.entropy159.entropylib.client.util.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class CustomRenderType {
    private ShaderInstance shader;
    private final RenderType renderType;
    private final ResourceLocation id;
    private final VertexFormat format;

    public CustomRenderType(ResourceLocation id, VertexFormat format, VertexFormat.Mode mode, UnaryOperator<RenderType.CompositeState.CompositeStateBuilder> state) {
        this.id = id;
        this.format = format;
        RenderType.CompositeState typeState = state.apply(RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(this::getShader))).createCompositeState(false);
        renderType = RenderType.create(id.getPath(), format, mode, 256, typeState);
    }

    public ShaderInstance getShader() {
        return shader;
    }

    public RenderType get() {
        return renderType;
    }

    public ResourceLocation getID() {
        return id;
    }

    public VertexFormat getFormat() {
        return format;
    }

    protected void setShader(ShaderInstance shader) {
        this.shader = shader;
    }
}
