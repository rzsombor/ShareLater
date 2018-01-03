package hu.rzsombor.sharelater

import android.app.job.JobScheduler
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class TextShareActivity : AppCompatActivity() {

    lateinit var jobScheduler: JobScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_share)

        jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        val action = intent.action
        val type = intent.type

        if (action == Intent.ACTION_SEND && type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)

            Toast.makeText(this, "To share: $text", Toast.LENGTH_SHORT).show()
        }
    }

}