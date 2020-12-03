package mc.comdos.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class ShieldDamageReductionMixin {
    @Inject(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;limbDistance:F"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    void changeShieldDidBlockAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, float f, boolean bl, float g) {
        if (bl) {
            ((LivingEntity)(Object)this).world.sendEntityStatus((LivingEntity)(Object)this, (byte)29);
            ((LivingEntity)(Object)this).setHealth(((LivingEntity)(Object)this).getHealth() - amount);
            ((LivingEntity)(Object)this).damage(DamageSource.GENERIC, g * 0.15f);
            ((LivingEntity)(Object)this).setVelocity(((LivingEntity)(Object)this).getVelocity().multiply(0.2f));
            cir.setReturnValue(true);
        }
    }
}
