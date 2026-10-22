package com.example.quickmenu.mixin;

import com.example.quickmenu.CheatState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes selected entities use the vanilla glow outline, which draws through walls. */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true, require = 0)
    private void quickmenu$esp(CallbackInfoReturnable<Boolean> cir) {
        if (CheatState.shouldGlow((Entity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
