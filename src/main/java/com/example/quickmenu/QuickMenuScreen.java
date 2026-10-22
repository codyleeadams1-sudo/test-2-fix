package com.example.quickmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

/** Tabbed command menu. Sends normal commands (cheats/op required). */
public class QuickMenuScreen extends Screen {
    private record Cmd(String label, String command) {}

    private static final String[] TABS = {"World", "Player", "Items", "Build", "Console"};

    private static final Cmd[][] PAGES = {
        { // World
            new Cmd("Day", "time set day"), new Cmd("Night", "time set night"),
            new Cmd("Noon", "time set noon"), new Cmd("Clear weather", "weather clear"),
            new Cmd("Rain", "weather rain"), new Cmd("Thunder", "weather thunder")
        },
        { // Player
            new Cmd("Creative", "gamemode creative"), new Cmd("Survival", "gamemode survival"),
            new Cmd("Spectator", "gamemode spectator"), new Cmd("Heal", "effect give @s instant_health 1 5"),
            new Cmd("Night vision", "effect give @s night_vision 600 0 true"), new Cmd("Speed", "effect give @s speed 600 2 true")
        },
        { // Items
            new Cmd("Diamond sword", "give @s diamond_sword"), new Cmd("Diamond pickaxe", "give @s diamond_pickaxe"),
            new Cmd("Diamond axe", "give @s diamond_axe"), new Cmd("Cooked beef x64", "give @s cooked_beef 64"),
            new Cmd("Torches x64", "give @s torch 64"), new Cmd("Elytra", "give @s elytra")
        },
        { // Build
            new Cmd("Stone platform", "fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 stone"), new Cmd("Glass box", "fill ~-3 ~ ~-3 ~3 ~4 ~3 glass hollow"),
            new Cmd("Stone tower", "fill ~-2 ~ ~-2 ~2 ~15 ~2 stone_bricks hollow"), new Cmd("Clear area", "fill ~-5 ~ ~-5 ~5 ~6 ~5 air"),
            new Cmd("Water pool", "fill ~-3 ~-1 ~-3 ~3 ~-1 ~3 water"), new Cmd("Wood floor", "fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 oak_planks")
        },
        {} // Console
    };

    private static int tab = 0; // remembered between opens
    private EditBox customField;

    public QuickMenuScreen() {
        super(Component.literal("Quick Menu"));
    }

    private void run(String command) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) connection.sendCommand(command);
    }

    private int pw() { return 340; }
    private int ph() { return 190; }
    private int px() { return (this.width - pw()) / 2; }
    private int py() { return (this.height - ph()) / 2; }

    @Override
    protected void init() {
        int px = px(), py = py();
        int sideW = 84;

        // Sidebar tabs
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            Button b = Button.builder(Component.literal(TABS[i]), x -> { tab = idx; this.rebuildWidgets(); })
                    .bounds(px + 8, py + 34 + i * 24, sideW - 12, 20).build();
            b.active = (tab != i);
            this.addRenderableWidget(b);
        }

        int cx = px + sideW + 10;
        int cw = pw() - sideW - 18;
        int bw = (cw - 6) / 2;

        if (tab < 4) {
            Cmd[] page = PAGES[tab];
            for (int i = 0; i < page.length; i++) {
                final Cmd c = page[i];
                int x = cx + (i % 2) * (bw + 6);
                int y = py + 34 + (i / 2) * 26;
                this.addRenderableWidget(Button.builder(Component.literal(c.label()), b -> run(c.command()))
                        .bounds(x, y, bw, 20).build());
            }
        } else {
            customField = new EditBox(this.font, cx, py + 38, cw, 20, Component.literal("Command"));
            customField.setMaxLength(256);
            customField.setHint(Component.literal("any command, no slash"));
            this.addRenderableWidget(customField);
            this.addRenderableWidget(Button.builder(Component.literal("Run command"), b -> {
                String c = customField.getValue().trim();
                if (c.startsWith("/")) c = c.substring(1);
                if (!c.isEmpty()) run(c);
            }).bounds(cx, py + 64, cw, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(px + 8, py + ph() - 28, sideW - 12, 20).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractBackground(g, mouseX, mouseY, delta);
        int px = px(), py = py();
        int x2 = px + pw(), y2 = py + ph();

        // Panel (drawn in the background pass so the buttons sit on top)
        g.fill(px - 1, py - 1, x2 + 1, y2 + 1, 0xFF2B3440);   // border
        g.fill(px, py, x2, y2, 0xE60F1318);                    // body
        g.fill(px, py, px + 84 + 4, y2, 0xF0161B22);           // sidebar
        g.fill(px, py, x2, py + 2, 0xFF4C9AFF);                // accent bar

        g.text(this.font, "QUICK MENU", px + 10, py + 10, 0xFFFFFFFF, true);
        g.text(this.font, TABS[tab], px + 84 + 10, py + 10, 0xFF8FB8FF, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
