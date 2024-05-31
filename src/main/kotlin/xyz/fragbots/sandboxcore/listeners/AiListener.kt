package xyz.fragbots.sandboxcore.listeners

import net.dongliu.requests.Requests
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import xyz.fragbots.sandboxcore.SandboxCore
import xyz.fragbots.sandboxcore.utils.Utils
import xyz.fragbots.sandboxcore.utils.player.PlayerExtensions.getHighestRank
import xyz.fragbots.sandboxcore.utils.player.PlayerExtensions.getStats
import xyz.fragbots.sandboxcore.utils.player.PlayerExtensions.sendFormattedMessage

val apiKey = "Bearer sk-proj-Ys09fRTIeDxTqDRcoKpsT3BlbkFJ8CUv0zpUMlmJzO9mrAAX"
val aiApi = "https://api.openai.com/v1/chat/completions"

data class OpenAiRequest(val model: String = "gpt-3.5-turbo", val messages: List<OpenAiMessage>)
data class OpenAiMessage(val role: String, val content: String)
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    val system_fingerprint: String?
)

data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: Any?,
    val finish_reason: String
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)
val startMessage = OpenAiMessage("system", "You are going to act as a classification model, I will give you a text and you need to respond to if a player wants an item that is the following, Hyperion ID: HYPERION, Aspect of the End ID: AOTE, Juju Shortbow ID: JUJUSHORTBOW, Giant's Sword ID: GIANTSWORD, Summoning Eye ID: SUMMONINGEYE, Yeti Sword ID: YETISWORD, you will respond with one word which will either be the item they wants ID or NONE, please make sure you only classift as the item if the player actually requests for one not if they make a statement about it")

class AiListener : Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncPlayerChatEvent){
        val player = event.player
        val question = OpenAiMessage("user", event.message)
        Bukkit.getScheduler().runTaskAsynchronously(SandboxCore.instance
        ) {
            val resp = Requests.post(aiApi).headers(mapOf("Content-Type" to "application/json", "Authorization" to apiKey)).jsonBody(OpenAiRequest(messages = listOf(startMessage,question))).send().readToJson(ChatCompletionResponse::class.java)
            val id = resp.choices[0].message.content
            if(id == "NONE") return@runTaskAsynchronously
            val item = SandboxCore.instance.itemFactory.getItem(id) ?: return@runTaskAsynchronously
            player.inventory.addItem(item.create(player.getStats()))
            player.sendFormattedMessage("&c[AI] &aYou have been given a " + Utils.rarityToColor(item.getItemData(player.getStats(), create = true).getRarity()).toString() + item.itemName)
        }
    }
}