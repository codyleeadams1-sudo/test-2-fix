package com.example.quickmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Toggle state for the client-side features. Nothing here sends commands or packets. */
public final class CheatState {
    public static boolean espPlayers = false;
    public static boolean espMobs = false;

    private CheatState() {}

    public static boolean shouldGlow(Entity e) {
        if (!espPlayers && !espMobs) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || e == mc.player) return false;
        if (e instanceof Player) return espPlayers;
        if (e instanceof LivingEntity) return espMobs;
        return false;
    }
}
