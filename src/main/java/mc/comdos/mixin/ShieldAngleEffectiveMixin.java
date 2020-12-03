package mc.comdos.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class ShieldAngleEffectiveMixin {
    @ModifyConstant(method = "blockedByShield", slice = @Slice(
        from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;dotProduct(Lnet/minecraft/util/math/Vec3d;)D")
    ))
    double modifyShieldEffectiveAngle(double original) {
        return -0.15;
    }
}
