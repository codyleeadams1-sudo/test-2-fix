package com.example.quickmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Client-side cheat menu. Nothing here uses commands. */
public class QuickMenuScreen extends Screen {
    private static final String[] TABS = {"Visuals", "World"};
    private static int tab = 0; // remembered between opens

    public QuickMenuScreen() {
        super(Component.literal("Quick Menu"));
    }

    private int pw() { return 340; }
    private int ph() { return 190; }
    private int px() { return (this.width - pw()) / 2; }
    private int py() { return (this.height - ph()) / 2; }

    private static Component toggleLabel(String name, boolean on) {
        return Component.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    @Override
    protected void init() {
        int px = px(), py = py();
        int sideW = 84;

        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            Button b = Button.builder(Component.literal(TABS[i]), x -> { tab = idx; this.rebuildWidgets(); })
                    .bounds(px + 8, py + 34 + i * 24, sideW - 12, 20).build();
            b.active = (tab != i);
            this.addRenderableWidget(b);
        }

        int cx = px + sideW + 10;
        int cw = pw() - sideW - 18;

        if (tab == 0) {
            Button players = Button.builder(toggleLabel("ESP Players", CheatState.espPlayers), b -> {
                CheatState.espPlayers = !CheatState.espPlayers;
                b.setMessage(toggleLabel("ESP Players", CheatState.espPlayers));
            }).bounds(cx, py + 34, cw, 20).build();
            this.addRenderableWidget(players);

            Button mobs = Button.builder(toggleLabel("ESP Mobs", CheatState.espMobs), b -> {
                CheatState.espMobs = !CheatState.espMobs;
                b.setMessage(toggleLabel("ESP Mobs", CheatState.espMobs));
            }).bounds(cx, py + 60, cw, 20).build();
            this.addRenderableWidget(mobs);
        } else {
            this.addRenderableWidget(Button.builder(Component.literal("Open Chunk Viewer"),
                    b -> Minecraft.getInstance().gui.setScreen(new ChunkViewerScreen()))
                    .bounds(cx, py + 34, cw, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(px + 8, py + ph() - 28, sideW - 12, 20).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractBackground(g, mouseX, mouseY, delta);
        int px = px(), py = py();
        int x2 = px + pw(), y2 = py + ph();

        g.fill(px - 1, py - 1, x2 + 1, y2 + 1, 0xFF2B3440);
        g.fill(px, py, x2, y2, 0xE60F1318);
        g.fill(px, py, px + 84 + 4, y2, 0xF0161B22);
        g.fill(px, py, x2, py + 2, 0xFF4C9AFF);

        g.text(this.font, "QUICK MENU", px + 10, py + 10, 0xFFFFFFFF, true);
        g.text(this.font, TABS[tab], px + 84 + 10, py + 10, 0xFF8FB8FF, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
