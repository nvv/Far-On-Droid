package com.mediafire.sdk.api.responses;

public class DeviceGetStatusResponse extends ApiResponse {
    private String async_jobs_in_progress;
    private long device_revision;

    public long getRevision() {
        return device_revision;
    }

    public boolean isAsyncJobInProgress() {
        if (async_jobs_in_progress == null || async_jobs_in_progress.isEmpty()) {
            async_jobs_in_progress = "no";
        }

        return "yes".equalsIgnoreCase(async_jobs_in_progress);
    }
}
