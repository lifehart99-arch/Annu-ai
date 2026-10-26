package com.annu.ai.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object DeviceActions {

    fun openUrl(context: Context, url: String): Boolean {
        return try {
            val validUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open URL: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun searchYoutube(context: Context, query: String): Boolean {
        return try {
            val encodedQuery = Uri.encode(query)
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$encodedQuery")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (appIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(appIntent)
            } else {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
            true
        } catch (e: Exception) {
            Toast.makeText(context, "YouTube search failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun draftMessage(context: Context, text: String, recipient: String? = null): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                if (!recipient.isNullOrBlank()) {
                    putExtra("address", recipient)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Draft message via:").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to draft message: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun draftSocialPost(context: Context, postContent: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, postContent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Share post to:").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share post: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun openApp(context: Context, packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                Toast.makeText(context, "App not installed: $packageName", Toast.LENGTH_SHORT).show()
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun executeAction(context: Context, action: ActionRegistry.ProposedAction): Boolean {
        return when (action.type) {
            ActionRegistry.ActionType.OPEN_URL -> {
                val url = action.params["url"] ?: "https://google.com"
                openUrl(context, url)
            }
            ActionRegistry.ActionType.SEARCH_YOUTUBE -> {
                val query = action.params["query"] ?: action.params["search"] ?: ""
                searchYoutube(context, query)
            }
            ActionRegistry.ActionType.DRAFT_MESSAGE -> {
                val text = action.params["text"] ?: action.params["message"] ?: ""
                val recipient = action.params["recipient"]
                draftMessage(context, text, recipient)
            }
            ActionRegistry.ActionType.DRAFT_SOCIAL_POST -> {
                val content = action.params["content"] ?: action.params["text"] ?: ""
                draftSocialPost(context, content)
            }
            ActionRegistry.ActionType.OPEN_APP -> {
                val pkg = action.params["package"] ?: action.params["packageName"] ?: ""
                openApp(context, pkg)
            }
            else -> {
                Toast.makeText(context, "Action handled in app: ${action.type.actionName}", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}
