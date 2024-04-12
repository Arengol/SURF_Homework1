package ru.surf.vladimir.homework1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var surfKey : String? = null
    private var surfActionMessage : String? = null

    private val surfActionReciver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ru.shalkoff.vsu_lesson2_2024.SURF_ACTION") {
                surfActionMessage = intent.getStringExtra("message")
                Toast.makeText(context, surfActionMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val surfActionIntentFilter = IntentFilter("ru.shalkoff.vsu_lesson2_2024.SURF_ACTION")
        registerReceiver(surfActionReciver, surfActionIntentFilter)
        surfKey = getSurfKey()
    }

    override fun onDestroy() {
        unregisterReceiver(surfActionReciver)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            "surf_key",
            surfKey
        )
        outState.putString(
            "surf_action_message",
            surfActionMessage
         )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        surfKey = savedInstanceState.getString("surf_key")
        surfActionMessage = savedInstanceState.getString("surf_action_message")
        Log.d("Restore", "Ключ $surfKey, сообщение $surfActionMessage")
    }

    fun getSurfKey(): String? {
        val resolver = contentResolver
        val uri = Uri.parse("content://dev.surf.android.provider/text")
        val cursor = resolver.query(uri, null, null, null, null)
        var key: String? = null
        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex("text")
                key = cursor.getString(nameIndex)
                Toast.makeText(
                    this,
                    key,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return key
    }
}
