package com.manojkhannakm.gtavtrainer.net.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Manoj Khanna
 */

public abstract class ClientAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final ProgressDialog mProgressDialog;

    protected ClientAsyncTask() {
        mProgressDialog = null;
    }

    protected ClientAsyncTask(Context context, String progressTitle, String progressMessage) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(progressTitle);
        mProgressDialog.setMessage(progressMessage);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
