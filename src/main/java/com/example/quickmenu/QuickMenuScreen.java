package com.example.quickmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

/**
 * A simple menu that runs normal Minecraft commands.
 * Works in singleplayer with cheats on, or on a server where you are an operator.
 */
public class QuickMenuScreen extends Screen {
    private EditBox customField;

    public QuickMenuScreen() {
        super(Component.literal("Quick Menu"));
    }

    private void run(String command) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.sendCommand(command);
        }
    }

    private void addCmd(String label, String command, int col, int row) {
        int w = 110, h = 20, gap = 6;
        int totalW = 3 * w + 2 * gap;
        int x = (this.width - totalW) / 2 + col * (w + gap);
        int y = 40 + row * (h + gap);
        this.addRenderableWidget(Button.builder(Component.literal(label), b -> run(command))
                .bounds(x, y, w, h).build());
    }

    @Override
    protected void init() {
        // World
        addCmd("Day", "time set day", 0, 0);
        addCmd("Night", "time set night", 1, 0);
        addCmd("Clear weather", "weather clear", 2, 0);

        // Game mode
        addCmd("Creative", "gamemode creative", 0, 1);
        addCmd("Survival", "gamemode survival", 1, 1);
        addCmd("Spectator", "gamemode spectator", 2, 1);

        // Items
        addCmd("Diamond sword", "give @s diamond_sword", 0, 2);
        addCmd("Diamond pickaxe", "give @s diamond_pickaxe", 1, 2);
        addCmd("Cooked beef x64", "give @s cooked_beef 64", 2, 2);

        // Player
        addCmd("Heal", "effect give @s instant_health 1 5", 0, 3);
        addCmd("Night vision", "effect give @s night_vision 600 0 true", 1, 3);
        addCmd("Speed", "effect give @s speed 600 2 true", 2, 3);

        // Quick builds (relative to where you stand)
        addCmd("Stone platform", "fill ~-5 ~-1 ~-5 ~5 ~-1 ~5 stone", 0, 4);
        addCmd("Glass box", "fill ~-3 ~ ~-3 ~3 ~4 ~3 glass hollow", 1, 4);
        addCmd("Stone tower", "fill ~-2 ~ ~-2 ~2 ~15 ~2 stone_bricks hollow", 2, 4);

        // Custom command box
        int fieldW = 3 * 110 + 2 * 6 - 66;
        int fx = (this.width - (fieldW + 66)) / 2;
        int fy = 40 + 5 * 26 + 8;
        customField = new EditBox(this.font, fx, fy, fieldW, 20, Component.literal("Command"));
        customField.setMaxLength(256);
        customField.setHint(Component.literal("any command, no slash"));
        this.addRenderableWidget(customField);
        this.addRenderableWidget(Button.builder(Component.literal("Run"), b -> {
            String c = customField.getValue().trim();
            if (c.startsWith("/")) c = c.substring(1);
            if (!c.isEmpty()) run(c);
        }).bounds(fx + fieldW + 6, fy, 60, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(this.width / 2 - 50, fy + 30, 100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        String title = "Quick Menu";
        graphics.text(this.font, title, (this.width - this.font.width(title)) / 2, 15, 0xFFFFFFFF, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
