import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.gateway.Intents
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.dsl.listeners
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import me.jakejmattson.discordkt.api.extensions.isSelf


fun main() {
    val token = "token"
    bot(token) {
        prefix { "+" }
        configure {
            allowMentionPrefix = true
            commandReaction = null
            intents = Intents.nonPrivileged
        }

    }
}

var rpsmid: Snowflake? = null
var session: Boolean = false
var orginialuser: String = "none"
fun rps() = commands("rps") {
    command("ping") {
        description = "ping command"
        execute {
            val ping: Long? = message?.timestamp?.toEpochMilliseconds()!! - System.currentTimeMillis()
            respond(ping.toString() + "ms of ping")
        }
    }
        command("rps"){
            description = "rock paper scissors"
            execute {
                orginialuser = message?.author?.id.toString()
                session = true
                val message = respond("Rock, paper, or scissors?").first()
                message.addReaction(Emojis.moyai)
                message.addReaction(Emojis.pageFacingUp)
                message.addReaction(Emojis.scissors)
                rpsmid = message.id
            }
        }
        command("source"){
            description = "Source Code"
            execute {
                respond("https://github.com/gjoedev/kotlin-bot")
            }
        }
    }
fun demoListeners() = listeners {
    on<MessageCreateEvent> {
        if (message.author!!.isBot) return@on
    }
    on<ReactionAddEvent> {
        if (user.isSelf() || messageId != rpsmid || !session || orginialuser != user.id.toString()) return@on
        var userchoice = 0
        var botChoice = Math.floor(Math.random() * 3)
        if(emoji.toString() == "Unicode(name=\uD83D\uDDFF)") userchoice = 0
        if(emoji.toString() == "Unicode(name=\uD83D\uDCC4)") userchoice = 1
        if(emoji.toString() == "Unicode(name=✂️)") userchoice = 2
        var out = logic(userchoice, botChoice.toInt())
        message.channel.createMessage(out.get(0))
        message.channel.createMessage(out.get(1))
        session = false
    }
}

fun logic(u: Int, b: Int ): List<String> {
    var win = 0
    //Tie
    if(u == b) win = 2
    //User win scenarios
    if(u == 0 && b == 2 || u== 1 && b==0 || u == 2 && b == 1) win = 0
    //Bot win scenarios
    if(b == 0 && u == 2 || b== 1 && u==0 || b==2 && b==1) win = 1
    if(win == 2){
        return listOf(intToEmoji(u) + "🤝" + intToEmoji(b), "'\"If a tie is like kissing your sister, losing is like kissing you grandmother with her teeth out.\" - George Brett'")
    }
    if(win== 0){
        return listOf(intToEmoji(u) + "🤜" + intToEmoji(b), "You win!\uD83C\uDF89")
    }
    if(win==1){
        return listOf(intToEmoji(u) + "🤛" + intToEmoji(b), "You loose :regional_indicator_l:")
    }
    return emptyList()
}

fun intToEmoji(i: Int): String {
    val list = listOf("🗿", "📄", "✂")
    return list.get(i)
}
//hey