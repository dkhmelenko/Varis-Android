package com.khmelenko.lab.travisclient.task;

import android.os.AsyncTask;

/**
 * Defines async task for executing tasks
 *
 * @author Dmytro Khmelenko
 */
final class LoaderAsyncTask<T> extends AsyncTask<Void, Void, TaskResult<T>> {

    private final Task<T> mTask;

    private LoaderAsyncTask(Task<T> task, TaskHelper helper) {
        mTask = task;
        mTask.setHelper(helper);
    }

    /**
     * Executes new task
     *
     * @param task   Task for execution
     * @param helper Task helper
     */
    public static <T> void executeTask(Task<T> task, TaskHelper helper) {
        LoaderAsyncTask<T> executor = new LoaderAsyncTask<T>(task, helper);
        executor.execute();
    }

    @Override
    protected TaskResult<T> doInBackground(Void... voids) {
        TaskResult<T> result;
        try {
            T response = mTask.execute();
            result = new TaskResult<T>(response, null);
        } catch (TaskException e) {
            result = new TaskResult<T>(null, e.getTaskError());
        }
        return result;
    }

    @Override
    protected void onPostExecute(TaskResult<T> taskResult) {
        if (taskResult.isSuccess()) {
            mTask.onSuccess(taskResult.getResult());
        } else {
            mTask.onFail(taskResult.getTaskError());
        }
    }
}
