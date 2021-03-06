import channels.kaito.kaito
import channels.qwearty.qwearty
import channels.santa.santa
import channels.sumkat.sumkat
import channels.wooodenleg.wooodenleg
import com.ktmi.tmi.commands.join
import com.ktmi.tmi.dsl.builder.scopes.MainScope
import com.ktmi.tmi.dsl.builder.scopes.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect
import com.ktmi.tmi.events.*
import com.ktmi.tmi.messages.UndefinedMessage
import common.commonLogic
import database.Database
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun main() {
    Database.toString() // To wake the DB object
    val token = System.getenv("OAUTH")

    tmi(token) {
        + Reconnect()

        onRoomState { println("Joined $channel") }
        onConnectionState { println(it) }

        onMessage { Database.Message.onMessage(message) }

        commonLogic()

        // Channels
        wooodenleg()
        sumkat()
        kaito()
        santa()

        onTwitchMessage<UndefinedMessage> {
            println("Message not recognized: ${it.rawMessage}")
        }

        joinOnFirstConnect()
    }
}

fun MainScope.joinOnFirstConnect() {
    var first = true

    onConnected {
        val channels = Database.Channels.get()

        if (first) {
            join("wooodenleg")
            for (channel in channels)
                join(channel)

            first = false
        }
    }
}