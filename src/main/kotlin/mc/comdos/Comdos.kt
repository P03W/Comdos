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
                PLAYERS_DODGING[player as ServerPlayerEntity] = 8
                (player as RollCooldownTrackable).rollCooldown = 25.0f
                (player as JoinInvulnerabilityTicksAccessor).setSpawnInvulnerabilityTicks(2)
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

            for (entry in PLAYERS_DODGING) {
                val player = entry.key
                val speed = (player as SoftVelocityTrackable).softVelocity.length()
                if (player.isAlive && speed > 0.05f) {
                    if (speed < 0.5f) {
                        val newVelocity = (player as SoftVelocityTrackable).softVelocity.normalize().multiply(0.5)

                        player.velocity = Vec3d(newVelocity.x, player.velocity.y, newVelocity.z)
                        (player as ServerPlayerEntity).networkHandler.sendPacket(EntityVelocityUpdateS2CPacket(player))
                    }
                }

                entry.setValue(entry.value - 1)

                if (entry.value <= 0) {
                    PLAYERS_DODGING.remove(player)
                }
            }
        })
    }

    companion object {
        val SYNC_VELOCITY_PACKET_ID = Identifier("comdos", "velocity_sync")
        val ROLL_PACKET_ID = Identifier("comdos", "roll")

        val PLAYERS_DODGING: MutableMap<ServerPlayerEntity, Int> = mutableMapOf()
    }
}