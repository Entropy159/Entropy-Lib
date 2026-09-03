package dev.entropy159.entropylib.client.util.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FBOManager {
    private static final List<FBO> FBOS = new ArrayList<>();
    private static final HashMap<FBO, RenderTarget> TARGETS = new HashMap<>();
    private static boolean initialized = false;

    public static FBO create(CustomRenderType type, int width, int height) {
        return create(type.getID(), () -> type, width, height);
    }

    public static FBO create(ResourceLocation id, Supplier<CustomRenderType> type, int width, int height) {
        return create(id, () -> type.get().getShader(), () -> type.get().getFormat(), width, height);
    }

    public static FBO create(ResourceLocation id, @Nullable Supplier<ShaderInstance> shader, Supplier<VertexFormat> format, int width, int height) {
        var fbo = new FBO(id, shader, format, width, height);
        FBOS.add(fbo);
        if (initialized) {
            fbo.init();
        }
        return fbo;
    }

    public static void init() {
        FBOS.forEach(FBO::init);
        initialized = true;
    }

    public static void renderAll(float partialTick) {
        TARGETS.keySet().forEach(fbo -> fbo.render(partialTick));
    }

    public static class FBO {
        private final ResourceLocation id;
        private int width;
        private int height;
        private final @Nullable Supplier<ShaderInstance> shader;
        private final Supplier<VertexFormat> format;
        private Color color = Color.WHITE;
        private Color clearColor = new Color(0x00000000);
        private boolean setClearColor = false;
        private Runnable beforeRender = () -> {
        };
        private Consumer<ShaderInstance> uniforms = shader -> {
        };
        private Runnable afterRender = () -> {
        };

        public FBO(ResourceLocation id, @Nullable Supplier<ShaderInstance> shader, Supplier<VertexFormat> format, int width, int height) {
            this.id = id;
            this.shader = shader;
            this.format = format;
            this.width = width;
            this.height = height;
        }

        public void init() {
            Optional.ofNullable(getTarget()).ifPresent(RenderTarget::destroyBuffers);
            var target = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            setClearColor(target);
            TARGETS.put(this, target);

            Minecraft.getInstance().getTextureManager().register(id, new RenderTargetTexture(this));
        }

        public void render(float partialTick) {
            beforeRender.run();
            var target = getTarget();
            if (target == null) {
                init();
                target = getTarget();
            }
            if (target.width != width || target.height != height) {
                target.resize(width, height, Minecraft.ON_OSX);
            }
            if (setClearColor) {
                setClearColor(target);
                setClearColor = false;
            }

            RenderSystem.viewport(0, 0, target.width, target.height);
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);

            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.identity();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(new Matrix4f().identity(), VertexSorting.ORTHOGRAPHIC_Z);

            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();

            if (shader != null) {
                RenderSystem.setShader(shader);
                if (Minecraft.getInstance().level != null) {
                    float timeInSeconds = (Minecraft.getInstance().level.getGameTime() + partialTick) / 20.0f;

                    var uniform = shader.get().getUniform("GameTime");
                    if (uniform != null) {
                        uniform.set(timeInSeconds);
                    }
                }
                uniforms.accept(shader.get());
            }

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, format.get());
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            builder.addVertex(-1, -1, 0).setColor(red, green, blue, alpha).setUv(0, 0).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            builder.addVertex(1, -1, 0).setColor(red, green, blue, alpha).setUv(1, 0).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            builder.addVertex(1, 1, 0).setColor(red, green, blue, alpha).setUv(1, 1).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            builder.addVertex(-1, 1, 0).setColor(red, green, blue, alpha).setUv(0, 1).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            MeshData meshData = builder.buildOrThrow();
            BufferUploader.drawWithShader(meshData);

            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();

            RenderSystem.restoreProjectionMatrix();
            modelViewStack.popMatrix();
            RenderSystem.applyModelViewMatrix();

            target.unbindWrite();
            var mainTarget = Minecraft.getInstance().getMainRenderTarget();
            mainTarget.bindWrite(true);
            RenderSystem.viewport(0, 0, mainTarget.width, mainTarget.height);
            afterRender.run();
        }

        public RenderTarget getTarget() {
            return TARGETS.get(this);
        }

        public ResourceLocation getLocation() {
            return id;
        }

        public void resize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(int color) {
            setColor(new Color(color));
        }

        public void setColor(int red, int green, int blue, int alpha) {
            setColor(new Color(red, green, blue, alpha));
        }

        public void setColor(float red, float green, float blue, float alpha) {
            setColor(new Color(red, green, blue, alpha));
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public Color getClearColor() {
            return clearColor;
        }

        public void setClearColor(int color) {
            setClearColor(new Color(color));
        }

        public void setClearColor(int red, int green, int blue, int alpha) {
            setClearColor(new Color(red, green, blue, alpha));
        }

        public void setClearColor(float red, float green, float blue, float alpha) {
            setClearColor(new Color(red, green, blue, alpha));
        }

        public void setClearColor(Color color) {
            setClearColor = true;
            clearColor = color;
        }

        private void setClearColor(RenderTarget target) {
            float red = clearColor.getRed() / 255f;
            float green = clearColor.getGreen() / 255f;
            float blue = clearColor.getBlue() / 255f;
            float alpha = clearColor.getAlpha() / 255f;
            target.setClearColor(red, green, blue, alpha);
        }

        public void setBeforeRender(Runnable beforeRender) {
            this.beforeRender = beforeRender;
        }

        public void setUniforms(Consumer<ShaderInstance> uniforms) {
            this.uniforms = uniforms;
        }

        public void setAfterRender(Runnable afterRender) {
            this.afterRender = afterRender;
        }
    }

    public static class RenderTargetTexture extends AbstractTexture {
        private final FBO fbo;

        public RenderTargetTexture(FBO fbo) {
            this.fbo = fbo;
        }

        @Override
        public void load(@NotNull ResourceManager manager) {
        }

        @Override
        public int getId() {
            var target = TARGETS.get(fbo);
            return target == null ? 0 : target.getColorTextureId();
        }
    }
}
