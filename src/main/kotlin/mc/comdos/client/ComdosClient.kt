package mc.comdos.client

import io.netty.buffer.Unpooled
import mc.comdos.Comdos
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.network.PacketByteBuf
import org.lwjgl.glfw.GLFW


@Environment(EnvType.CLIENT)
class ComdosClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            if (client.player != null && client.player!!.velocity != null) {
                val passedData = PacketByteBuf(Unpooled.buffer())
                passedData.writeDouble(client.player?.velocity!!.x)
                passedData.writeDouble(client.player?.velocity!!.y)
                passedData.writeDouble(client.player?.velocity!!.z)
                ClientSidePacketRegistry.INSTANCE.sendToServer(Comdos.SYNC_VELOCITY_PACKET_ID, passedData)

                while (roll.wasPressed()) {
                    val rollPassedData = PacketByteBuf(Unpooled.buffer())
                    ClientSidePacketRegistry.INSTANCE.sendToServer(Comdos.ROLL_PACKET_ID, rollPassedData)
                }
            }
        })
    }

    companion object {
        private val roll = KeyBindingHelper.registerKeyBinding(KeyBinding(
                "key.comdos.roll",  // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z,  // The keycode of the key
                "category.comdos.keys" // The translation key of the keybinding's category.
        ))
    }
}