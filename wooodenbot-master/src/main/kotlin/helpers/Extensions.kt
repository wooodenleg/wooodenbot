package helpers

import com.ktmi.tmi.events.UserContext
import com.ktmi.tmi.messages.TextMessage
import com.ktmi.tmi.messages.isMod

val UserContext<TextMessage>.isSubscriber get() = message.badges?.containsKey("subscriber") == true
val UserContext<TextMessage>.displayName get() = message.displayName
val UserContext<TextMessage>.isMod get() = message.isMod