package com.hstrobel.lsfplan.model.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class BriefingJobCreator implements JobCreator {
    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case BriefingJob.TAG:
                return new BriefingJob();
            default:
                return null;
        }
    }
}