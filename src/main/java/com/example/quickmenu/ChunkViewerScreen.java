package com.example.quickmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Top-down grid showing which chunks around you are loaded on the client. */
public class ChunkViewerScreen extends Screen {
    private static final int RADIUS = 12; // chunks in each direction

    private int cell, gx, gy;

    public ChunkViewerScreen() {
        super(Component.literal("Chunk Viewer"));
    }

    @Override
    protected void init() {
        int grid = 2 * RADIUS + 1;
        cell = Math.max(4, Math.min((this.width - 40) / grid, (this.height - 80) / grid));
        gx = (this.width - cell * grid) / 2;
        gy = 30;
        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.onClose())
                .bounds(this.width / 2 - 40, this.height - 28, 80, 20).build());
    }

    private int playerChunkX() { return Minecraft.getInstance().player.getBlockX() >> 4; }
    private int playerChunkZ() { return Minecraft.getInstance().player.getBlockZ() >> 4; }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractBackground(g, mouseX, mouseY, delta);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        int grid = 2 * RADIUS + 1;
        g.fill(gx - 3, gy - 3, gx + cell * grid + 3, gy + cell * grid + 3, 0xE60F1318);
        g.fill(gx - 3, gy - 3, gx + cell * grid + 3, gy - 1, 0xFF4C9AFF);

        int pcx = playerChunkX(), pcz = playerChunkZ();
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                boolean loaded = mc.level.hasChunk(pcx + dx, pcz + dz);
                int color = loaded ? 0xFF2E7D4F : 0xFF1A1F26;
                if (dx == 0 && dz == 0) color = 0xFFFFD54A;
                int x = gx + (dx + RADIUS) * cell;
                int y = gy + (dz + RADIUS) * cell;
                g.fill(x, y, x + cell - 1, y + cell - 1, color);
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        g.text(this.font, "CHUNK VIEWER  (green = loaded, yellow = you)", gx, 12, 0xFFFFFFFF, true);

        int grid = 2 * RADIUS + 1;
        int col = (mouseX - gx) / cell;
        int row = (mouseY - gy) / cell;
        String info;
        if (mouseX >= gx && mouseY >= gy && col < grid && row < grid) {
            int cx = playerChunkX() + col - RADIUS;
            int cz = playerChunkZ() + row - RADIUS;
            info = "Chunk " + cx + ", " + cz + "   blocks " + (cx << 4) + ", " + (cz << 4)
                    + " to " + ((cx << 4) + 15) + ", " + ((cz << 4) + 15);
        } else {
            info = "Your chunk: " + playerChunkX() + ", " + playerChunkZ();
        }
        g.text(this.font, info, gx, gy + cell * grid + 8, 0xFF8FB8FF, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
