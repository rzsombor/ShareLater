package hu.rzsombor.sharelater

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

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

            val extras = PersistableBundle()
            extras.putString("text", text)

            val jobInfo = JobInfo.Builder(1, ComponentName(this, ShareService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setExtras(extras)
                    .build()

            jobScheduler.schedule(jobInfo)
        }
    }

}