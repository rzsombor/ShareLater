package hu.rzsombor.sharelater

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast

class ShareService : JobService() {
    override fun onStopJob(jobParams: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(jobParams: JobParameters?): Boolean {
        if (jobParams == null) {
            return false
        }

        val text = jobParams.extras.getString("text")

        Toast.makeText(this, "To share: $text", Toast.LENGTH_SHORT).show()

        return false
    }

}