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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
            target.setClearColor(0, 0, 0, 0);
            TARGETS.put(this, target);

            Minecraft.getInstance().getTextureManager().register(id, new RenderTargetTexture(this));
        }

        public ResourceLocation getLocation() {
            return id;
        }

        public void resize(int width, int height) {
            this.width = width;
            this.height = height;
            Optional.ofNullable(getTarget()).ifPresent(target -> target.resize(width, height, Minecraft.ON_OSX));
        }

        public RenderTarget getTarget() {
            return TARGETS.get(this);
        }

        public void render(float partialTick) {
            var target = getTarget();
            if (target == null) {
                init();
                target = getTarget();
            }
            if (target.width != width || target.height != height) {
                target.resize(width, height, Minecraft.ON_OSX);
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
            }

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, format.get());
            builder.addVertex(-1, -1, 0).setColor(1f, 1f, 1f, 1f).setUv(0, 0).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            builder.addVertex(1, -1, 0).setColor(1f, 1f, 1f, 1f).setUv(1, 0).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            builder.addVertex(1, 1, 0).setColor(1f, 1f, 1f, 1f).setUv(1, 1).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
            builder.addVertex(-1, 1, 0).setColor(1f, 1f, 1f, 1f).setUv(0, 1).setNormal(0, 0, 1).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY);
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
