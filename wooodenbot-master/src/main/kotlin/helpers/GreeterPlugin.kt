package helpers

import com.ktmi.tmi.commands.sendMessage
import com.ktmi.tmi.dsl.builder.Container
import com.ktmi.tmi.dsl.plugins.TwitchPlugin
import com.ktmi.tmi.events.onTwitchMessage
import com.ktmi.tmi.messages.TextMessage
import database.Database
import database.now
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val defIgnoredUser = arrayOf("streamelements", "streamlabs", "moobot", "nightbot")
val defGreetings = arrayOf("Hello there", "Nice to see you", "Heyoooo", " GivePLZ ", "I am glad to see you here")

fun Container.Greet(
    hours: Int = 48,
    customMessages: Map<String, String> = mapOf(),
    greetings: Array<String> = defGreetings,
    ignored: Array<String> = defIgnoredUser
) = object : TwitchPlugin {
    override val name = "greet"

    init { launch {
        onTwitchMessage<TextMessage> { message ->
            if (!ignored.contains(message.username)) GlobalScope.launch {
                try {
                    val seen = Database.LastSeen.get(message.channel, message.username)

                    if (seen == null) {
                        neverSeen(message)
                        Database.LastSeen.set(message.channel, message.username)
                    } else {
                        seen(message, seen)
                        Database.LastSeen.update(message.channel, message.username)
                    }
                } catch (t: Throwable) { t.printStackTrace() }
            }
        }
    } }

    fun neverSeen(message: TextMessage) {
        sendMessage(message.channel, "I've never seen you before ${message.displayName} TakeNRG Welcome!")
    }

    fun seen(message: TextMessage, lastSeen: database.LastSeenEntry) {
        val diff = ((now - lastSeen.timestamp) / 3_600_000).toInt()

        if (diff >= hours) {
            val days: Int = diff / 24
            when {
                customMessages.containsKey(message.username) ->
                    sendMessage(message.channel, "${customMessages[message.username]} , I haven't seen you for $days days!")
                else ->
                    sendMessage(message.channel, "${greetings.random()} ${message.displayName}. I haven't seen you for $days days!")
            }
        }
    }
}