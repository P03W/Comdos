package mc.comdos

import mc.comdos.duck.RollCooldownTrackable
import mc.comdos.duck.SoftVelocityTrackable
import mc.comdos.mixin.JoinInvulnerabilityTicksAccessor
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d


class Comdos : ModInitializer {
    override fun onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(ROLL_PACKET_ID) { packetContext: PacketContext, _: PacketByteBuf? ->
            val player = packetContext.player
            packetContext.taskQueue.execute {
                if (player.isAlive && (player as RollCooldownTrackable).rollCooldown <= 0 && (player as SoftVelocityTrackable).softVelocity.length() > 0.137f) {
                    var newVelocity = (player as SoftVelocityTrackable).softVelocity.multiply(7.0)

                    if (!player.isOnGround) {
                        newVelocity = newVelocity.multiply(0.2)
                    }

                    player.velocity = Vec3d(newVelocity.x, player.velocity.y + 0.1, newVelocity.z)
                    (player as ServerPlayerEntity).networkHandler.sendPacket(EntityVelocityUpdateS2CPacket(player))
                    (player as RollCooldownTrackable).rollCooldown = 25.0f

                    (player as JoinInvulnerabilityTicksAccessor).setSpawnInvulnerabilityTicks(8)
                }
            }
        }

        ServerSidePacketRegistry.INSTANCE.register(SYNC_VELOCITY_PACKET_ID) { packetContext: PacketContext, attachedData: PacketByteBuf ->
            val player = packetContext.player
            val x = attachedData.readDouble()
            val y = attachedData.readDouble()
            val z = attachedData.readDouble()
            packetContext.taskQueue.execute {
                if (player.isAlive) {
                    (player as SoftVelocityTrackable).softVelocity = Vec3d(x, y, z)
                }
            }
        }

        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { minecraftServer ->
            minecraftServer.playerManager.playerList.forEach {
                if ((it as RollCooldownTrackable).rollCooldown > 0) {
                    (it as RollCooldownTrackable).rollCooldown -= 1
                }
            }
        })
    }

    companion object {
        val SYNC_VELOCITY_PACKET_ID = Identifier("comdos", "velocity_sync")
        val ROLL_PACKET_ID = Identifier("comdos", "roll")
    }
}