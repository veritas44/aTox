package ltd.evilcorp.atox

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import im.tox.tox4j.core.options.ProxyOptions
import im.tox.tox4j.core.options.SaveDataOptions
import im.tox.tox4j.core.options.ToxOptions

class ToxThread(saveDestination: String, saveOption: SaveDataOptions) : HandlerThread("Tox") {
    companion object {
        // Tox
        private const val msgIterate = 0
        const val msgSave = 1
        const val msgShutdown = 2

        // self
        const val msgSetName = 3
        const val msgSetStatus = 4
        const val msgSetState = 5
        const val msgSetTyping = 6

        // contacts
        const val msgAddContact = 7
        const val msgDeleteContact = 8
        const val msgSendMsg = 9
        const val msgAcceptContact = 10

        // groups
        const val msgGroupCreate = 11
        const val msgGroupLeave = 12
        const val msgGroupMessage = 13
        const val msgGroupTopic = 14
        const val msgGroupInvite = 15
        const val msgGroupJoin = 16
    }

    private val tox = Tox(
        ToxOptions(
            true,
            true,
            true,
            ProxyOptions.`None$`(),
            0,
            0,
            0,
            saveOption,
            true
        )
    )

    val handler: Handler by lazy {
        Handler(looper) {
            when (it.what) {
                msgIterate -> {
                    tox.iterate()
                    handler.sendEmptyMessageDelayed(msgIterate, tox.iterationInterval().toLong())
                    true
                }
                msgSave -> {
                    Log.e("ToxThread", "Save")
                    tox.save(saveDestination, false)
                    true
                }
                msgShutdown -> {
                    Log.e("ToxThread", "Shutting down tox")
                    tox.kill()
                    true
                }
                msgSetName -> {
                    Log.e("ToxThread", "SetName: ${it.obj as String}")
                    tox.setName(it.obj as String)
                    true
                }
                msgSetStatus -> {
                    Log.e("ToxThread", "Setting status")
                    true
                }
                msgSetState -> {
                    Log.e("ToxThread", "Setting state")
                    true
                }
                msgSetTyping -> {
                    Log.e("ToxThread", "Set typing")
                    true
                }
                msgAddContact -> {
                    val addContact = it.obj as MsgAddContact
                    Log.e("ToxThread", "AddContact: ${addContact.toxId} ${addContact.message}")
                    tox.addContact(addContact.toxId, addContact.message)
                    true
                }
                msgDeleteContact -> {
                    Log.e("ToxThread", "Delete contact")
                    true
                }
                msgAcceptContact -> {
                    Log.e("ToxThread", "Accept contact request")
                    true
                }
                msgSendMsg -> {
                    Log.e("ToxThread", "Sending message to friend number: ${it.arg1}")
                    tox.sendMessage(it.arg1, it.obj.toString())
                    true
                }
                msgGroupCreate -> {
                    Log.e("ToxThread", "Create group")
                    true
                }
                msgGroupLeave -> {
                    Log.e("ToxThread", "Leave group")
                    true
                }
                msgGroupMessage -> {
                    Log.e("ToxThread", "Send group message")
                    true
                }
                msgGroupTopic -> {
                    Log.e("ToxThread", "Set group topic")
                    true
                }
                msgGroupInvite -> {
                    Log.e("ToxThread", "Invite group")
                    true
                }
                msgGroupJoin -> {
                    Log.e("ToxThread", "Join group")
                    true
                }
                else -> {
                    Log.e("ToxThread", "Unknown message: ${it.what}")
                    false
                }
            }
        }
    }

    init {
        start()
    }

    override fun onLooperPrepared() {
        tox.bootstrap(
            "tox.verdict.gg",
            33445,
            "1C5293AEF2114717547B39DA8EA6F1E331E5E358B35F9B6B5F19317911C5F976".hexToByteArray()
        )
        tox.bootstrap(
            "tox.kurnevsky.net",
            33445,
            "82EF82BA33445A1F91A7DB27189ECFC0C013E06E3DA71F588ED692BED625EC23".hexToByteArray()
        )
        tox.bootstrap(
            "tox.abilinski.com",
            33445,
            "10C00EB250C3233E343E2AEBA07115A5C28920E9C8D29492F6D00B29049EDC7E".hexToByteArray()
        )

        handler.sendEmptyMessage(msgIterate)
    }
}