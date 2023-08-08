package sgs.sgs.ecabsdriver.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by Lenovo on 4/19/2018.
 */
public class MyJobTimerServiceTest extends JobService{

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}