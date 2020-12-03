package mc.comdos.duck;

import net.minecraft.util.math.Vec3d;

public interface SoftVelocityTrackable {
    Vec3d getSoftVelocity();
    
    void setSoftVelocity(Vec3d newSoftVelocity);
}
