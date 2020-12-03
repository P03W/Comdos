package mc.comdos.mixin;

import mc.comdos.duck.SoftVelocityTrackable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class SoftVelocityTracker implements SoftVelocityTrackable {
    Vec3d softVelocity = Vec3d.ZERO;
    
    @Override
    public Vec3d getSoftVelocity() {
        return softVelocity;
    }
    
    @Override
    public void setSoftVelocity(Vec3d newSoftVelocity) {
        softVelocity = newSoftVelocity;
    }
}
