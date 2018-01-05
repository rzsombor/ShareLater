package hu.rzsombor.sharelater

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast


class TextShareActivity : AppCompatActivity() {

    private lateinit var jobScheduler: JobScheduler

    private lateinit var needsNetworkSwitch: SwitchCompat
    private lateinit var delaySpinner: AppCompatSpinner
    private lateinit var sharedText: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_share)

        jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        needsNetworkSwitch = findViewById(R.id.NeedsNetworkSwitch)

        sharedText = findViewById(R.id.SharedText)
        if (savedInstanceState == null) {
            sharedText.setText(extractSharedText(intent))
        }

        delaySpinner = findViewById(R.id.DelaySpinner)
        val delaySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.delay_option_labels,
                android.R.layout.simple_spinner_item)
        delaySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        delaySpinner.adapter = delaySpinnerAdapter

        findViewById<View>(R.id.ApplyButton).setOnClickListener({
            processIntent(sharedText.text.toString(), needsNetworkSwitch.isChecked, delayOption2Millis(delaySpinner.selectedItemPosition))
        })
    }

    private fun extractSharedText(intent: Intent): String {
        val action = intent.action
        val type = intent.type

        return if (action == Intent.ACTION_SEND && type == "text/plain" &&
                intent.hasExtra(Intent.EXTRA_TEXT)) {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            ""
        }
    }

    private fun processIntent(sharedText: String, needsNetwork: Boolean, minTimeMillis: Long?) {
        val extras = PersistableBundle()
        extras.putString("text", sharedText)

        val jobInfoBuilder = JobInfo.Builder(1, ComponentName(this, ShareService::class.java))
                .setPersisted(true)
                .setExtras(extras)

        if (needsNetwork) {
            jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        } else {
            jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
        }

        if (minTimeMillis != null) {
            jobInfoBuilder.setMinimumLatency(minTimeMillis)
        }

        jobScheduler.schedule(jobInfoBuilder.build())

        Toast.makeText(this, "Scheduled!", Toast.LENGTH_SHORT).show()

        finish()
    }

    private fun delayOption2Millis(option: Int): Long? {
        val delayValueArray = resources.getIntArray(R.array.delay_option_values)

        if (option >= delayValueArray.size) {
            throw IndexOutOfBoundsException("Delay option indexed out delay values array!")
        }

        val millis = delayValueArray[option].toLong()

        return if (millis < 0) null else millis
    }

}