package mc.comdos.mixin;

import mc.comdos.duck.RollCooldownTrackable;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class RollCooldownTracker implements RollCooldownTrackable {
    float rollCooldown = 0;
    
    @Override
    public float getRollCooldown() {
        return rollCooldown;
    }
    
    @Override
    public void setRollCooldown(float newCooldown) {
        rollCooldown = newCooldown;
    }
}
