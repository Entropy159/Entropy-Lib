package dev.entropy159.entropylib.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

public enum ScreenAnchorPoint {
    TOP_LEFT {
        @Override
        public void renderText(GuiGraphics graphics, Component text, int line, float alpha) {
            Minecraft client = Minecraft.getInstance();
            int padding = RenderingUtils.getTextPadding();
            graphics.drawString(client.font, text, padding, RenderingUtils.textLine(line), new Color(1, 1, 1, alpha).getRGB());
        }
    },
    TOP_CENTER {
        @Override
        public void renderText(GuiGraphics graphics, Component text, int line, float alpha) {
            Minecraft client = Minecraft.getInstance();
            graphics.drawCenteredString(client.font, text, client.getWindow().getGuiScaledWidth() / 2, RenderingUtils.textLine(line), new Color(1, 1, 1, alpha).getRGB());
        }
    },
    TOP_RIGHT {
        @Override
        public void renderText(GuiGraphics graphics, Component text, int line, float alpha) {
            Minecraft client = Minecraft.getInstance();
            graphics.drawString(client.font, text, RenderingUtils.alignRight(text), RenderingUtils.textLine(line), new Color(1, 1, 1, alpha).getRGB());
        }
    },
    BOTTOM_LEFT {
        @Override
        public void renderText(GuiGraphics graphics, Component text, int line, float alpha) {
            Minecraft client = Minecraft.getInstance();
            int padding = RenderingUtils.getTextPadding();
            graphics.drawString(client.font, text, padding, RenderingUtils.alignBottom(line), new Color(1, 1, 1, alpha).getRGB());
        }
    },
    BOTTOM_RIGHT {
        @Override
        public void renderText(GuiGraphics graphics, Component text, int line, float alpha) {
            Minecraft client = Minecraft.getInstance();
            graphics.drawString(client.font, text, RenderingUtils.alignRight(text), RenderingUtils.alignBottom(line), new Color(1, 1, 1, alpha).getRGB());
        }
    };

    public abstract void renderText(GuiGraphics graphics, Component text, int line, float alpha);
}
