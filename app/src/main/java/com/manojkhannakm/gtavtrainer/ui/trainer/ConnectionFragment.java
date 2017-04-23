package com.manojkhannakm.gtavtrainer.ui.trainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.manojkhannakm.gtavtrainer.R;
import com.manojkhannakm.gtavtrainer.net.client.Client;
import com.manojkhannakm.gtavtrainer.net.client.ClientAsyncTask;
import com.manojkhannakm.gtavtrainer.ui.MainActivity;

/**
 * @author Manoj Khanna
 */

public class ConnectionFragment extends Fragment {

    private static final String IP_ADDRESS_PREFERENCE = "ip_address";
    private static final String PORT_PREFERENCE = "port";
    private static final String PASSWORD_PREFERENCE = "password";

    private ConnectAsyncTask mConnectAsyncTask;

    public static ConnectionFragment newInstance() {
        return new ConnectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_connection, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(ConnectionFragment.class.getName(), Context.MODE_PRIVATE);

        final TextInputEditText ipAddressEditText = (TextInputEditText) view.findViewById(R.id.ip_address_edit_text_connection);
        ipAddressEditText.setText(preferences.getString(IP_ADDRESS_PREFERENCE,
                getString(R.string.ip_address_text_edit_text_connection)));

        final TextInputEditText portEditText = (TextInputEditText) view.findViewById(R.id.port_edit_text_connection);
        portEditText.setText(preferences.getString(PORT_PREFERENCE,
                getString(R.string.port_text_edit_text_connection)));

        final TextInputEditText passwordEditText = (TextInputEditText) view.findViewById(R.id.password_edit_text_connection);
        passwordEditText.setText(preferences.getString(PASSWORD_PREFERENCE, ""));

        Button connectButton = (Button) view.findViewById(R.id.connect_button_connection);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String ipAddress = ipAddressEditText.getText().toString(),
                        port = portEditText.getText().toString(),
                        password = passwordEditText.getText().toString();
                if (port.isEmpty()) {
                    port = "0";
                }

                mConnectAsyncTask = (ConnectAsyncTask) new ConnectAsyncTask().execute(ipAddress, port, password);
            }

        });

        if (savedInstanceState == null) {
            ipAddressEditText.setSelection(ipAddressEditText.getText().length());
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mConnectAsyncTask != null) {
            mConnectAsyncTask.cancel(true);
        }
    }

    private class ConnectAsyncTask extends ClientAsyncTask<String, Void, Boolean> {

        private Client mClient;

        public ConnectAsyncTask() {
            super(getContext(), getString(R.string.connection_progress_connection), getString(R.string.connecting_progress_connection));
        }

        @Override
        protected Boolean doInBackground(String... params) {
            mClient = new Client(params[0], params[1], params[2]);
            return mClient.connect();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                MainActivity activity = (MainActivity) getActivity();

                SharedPreferences preferences = activity.getSharedPreferences(ConnectionFragment.class.getName(), Context.MODE_PRIVATE);
                preferences.edit()
                        .putString(IP_ADDRESS_PREFERENCE, mClient.getIpAddress())
                        .putString(PORT_PREFERENCE, mClient.getPort())
                        .putString(PASSWORD_PREFERENCE, mClient.getPassword())
                        .apply();

                activity.setClient(mClient);

                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out,
                                R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                        .replace(R.id.container_layout_main, TrainerFragment.newInstance())
                        .addToBackStack(null)
                        .commit();

                //noinspection ConstantConditions
                Snackbar.make(getView(), R.string.connected_progress_connection, Snackbar.LENGTH_LONG).show();
            } else {
                //noinspection ConstantConditions
                Snackbar.make(getView(), R.string.could_not_connect_progress_connection, Snackbar.LENGTH_LONG).show();
            }
        }

    }

}
