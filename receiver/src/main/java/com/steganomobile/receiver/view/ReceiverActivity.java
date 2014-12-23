package com.steganomobile.receiver.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.Methods;
import com.steganomobile.common.receiver.model.cc.CcBaseItem;
import com.steganomobile.common.receiver.model.cc.CcContent;
import com.steganomobile.common.receiver.model.cc.CcReceiverItem;
import com.steganomobile.common.receiver.model.nsd.NsdBaseItem;
import com.steganomobile.common.receiver.model.nsd.NsdContent;
import com.steganomobile.common.receiver.model.nsd.NsdItem;
import com.steganomobile.receiver.R;
import com.steganomobile.receiver.db.ReceiverDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceiverActivity extends Activity {

    private static final String TAG = ReceiverActivity.class.getSimpleName();
    private ResultReceiver resultReceiver = new ResultReceiver();
    private ArrayList<Long> ccItemIds = new ArrayList<Long>();
    private ArrayList<CcBaseItem> ccHiddenItems = new ArrayList<CcBaseItem>();
    private ArrayList<CcBaseItem> ccVisibleItems = new ArrayList<CcBaseItem>();
    private ArrayList<Long> nsdItemIds = new ArrayList<Long>();
    private ArrayList<NsdBaseItem> nsdHiddenItems = new ArrayList<NsdBaseItem>();
    private ArrayList<NsdBaseItem> nsdVisibleItems = new ArrayList<NsdBaseItem>();
    private ArrayAdapter<CcBaseItem> ccAdapter;
    private ArrayAdapter<NsdBaseItem> nsdAdapter;
    private ReceiverDatabase database;
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean isNsdServer = false;
    private boolean isNsdClient = false;

    private void clearCcDatabase() {
        database.deleteCcItems();
        ccVisibleItems.clear();
        ccHiddenItems.clear();
        ccItemIds.clear();
        ccAdapter.notifyDataSetChanged();
    }

    private void clearNsdDatabase() {
        database.deleteNsdItems();
        nsdVisibleItems.clear();
        nsdHiddenItems.clear();
        nsdItemIds.clear();
        nsdAdapter.notifyDataSetChanged();
    }

    private void updateTimeIntervalButton() {
        Button myButton = (Button) findViewById(R.id.buttonTimeInterval);
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String myText = mySharedPreferences.getString(Const.PREF_KEY_INTERVAL, Const.DEFAULT_INTERVAL);
        myButton.setText(getResources().getText(R.string.select_interval) + " " + myText + " [ms]");
    }

    public void runService() {
        if (!Methods.isServiceRunning(this, Const.PACKAGE_STEGANO_RECEIVER)) {
            startService(new Intent(getBaseContext(), CcReceiverService.class));
        }
    }

    private void prepareResultReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_FINISH_RECEIVER_CC);
        intentFilter.addAction(Const.ACTION_FINISH_RECEIVER_NSD);
        registerReceiver(resultReceiver, intentFilter);
    }

    private void prepareListView() {
        ListView ccListView = (ListView) findViewById(R.id.ccListView);
        ccAdapter = new ArrayAdapter<CcBaseItem>(this, android.R.layout.simple_list_item_1, ccVisibleItems);
        ccListView.setAdapter(ccAdapter);
        ccListView.setTextFilterEnabled(true);
        ccListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CcBaseItem cc = ccVisibleItems.get(position);
                ccVisibleItems.set(position, ccHiddenItems.get(position));
                ccHiddenItems.set(position, cc);
                ccAdapter.notifyDataSetChanged();
            }
        });
        ccListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                database.deleteCcItem(ccItemIds.get(position));
                ccItemIds.remove(position);
                ccVisibleItems.remove(position);
                ccHiddenItems.remove(position);
                ccAdapter.notifyDataSetChanged();
                Toast.makeText(
                        getBaseContext(),
                        getResources().getString(R.string.item_deleted_from_database),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
        ListView nsdListView = (ListView) findViewById(R.id.nsdListView);
        nsdAdapter = new ArrayAdapter<NsdBaseItem>(this, android.R.layout.simple_list_item_1, nsdVisibleItems);
        nsdListView.setAdapter(nsdAdapter);
        nsdListView.setTextFilterEnabled(true);
        nsdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NsdBaseItem nsd = nsdVisibleItems.get(position);
                nsdVisibleItems.set(position, nsdHiddenItems.get(position));
                nsdHiddenItems.set(position, nsd);
                nsdAdapter.notifyDataSetChanged();
            }
        });
        nsdListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                database.deleteNsdItem(nsdItemIds.get(position));
                nsdItemIds.remove(position);
                nsdVisibleItems.remove(position);
                nsdHiddenItems.remove(position);
                nsdAdapter.notifyDataSetChanged();
                Toast.makeText(
                        getBaseContext(),
                        getResources().getString(R.string.item_deleted_from_database),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }

    private void updateArrayListsFromDatabase() {
        ccVisibleItems.clear();
        ccHiddenItems.clear();
        nsdVisibleItems.clear();
        nsdHiddenItems.clear();
        List<CcReceiverItem> ccReceiverItems = database.getCcItems();
        List<NsdItem> nsdItems = database.getNsdItems();
        for (CcReceiverItem ccReceiverItem : ccReceiverItems) {
            ccVisibleItems.add(ccReceiverItem.getCcBaseItem());
            ccHiddenItems.add(ccReceiverItem);
            ccItemIds.add(ccReceiverItem.getId());
            CcContent.addItem(ccReceiverItem);
        }
        for (NsdItem nsdItem : nsdItems) {
            nsdVisibleItems.add(nsdItem.getNsdBaseItem());
            nsdHiddenItems.add(nsdItem);
            nsdItemIds.add(nsdItem.getId());
            NsdContent.addItem(nsdItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new ReceiverDatabase(this);
        updateArrayListsFromDatabase();
        prepareResultReceiver();
        runService();
        prepareListView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Const.REQUEST_SETTINGS) {
                stopService(new Intent(getBaseContext(), CcReceiverService.class));
                runService();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                cancelApplication();
                break;
            case R.id.action_split:
                splitServiceAndActivity();
                break;
            case R.id.action_clear_nsd_database:
                clearNsdDatabase();
                break;
            case R.id.action_clear_cc_database:
                clearCcDatabase();
                break;
            case R.id.action_nsd_server:
                isNsdServer = !isNsdServer;
                item.setChecked(isNsdServer);
                updateNsd();
                break;
            case R.id.action_nsd_client:
                isNsdClient = !isNsdClient;
                item.setChecked(isNsdClient);
                updateNsd();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNsd() {
        stopNsdPresenceService();
        NsdSocketService.startActionStart(this, isNsdClient, isNsdServer, dateFormat.format(new Date()));
    }

    private void stopNsdPresenceService() {
        stopService(new Intent(this, NsdPresenceService.class));
    }

    private void stopCcService() {
        stopService(new Intent(getBaseContext(), CcReceiverService.class));
    }

    private void splitServiceAndActivity() {
        Toast.makeText(getBaseContext(),
                "Switching into \"Only in Background listening\" mode",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private void cancelApplication() {
        stopNsdPresenceService();
        stopCcService();
        Toast.makeText(getBaseContext(),
                getString(R.string.closing_stegano_receiver),
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resultReceiver != null) {
            unregisterReceiver(resultReceiver);
        }
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Const.ACTION_FINISH_RECEIVER_CC.equals(intent.getAction())) {
                createCcItem(intent);
            } else if (Const.ACTION_FINISH_RECEIVER_NSD.equals(intent.getAction())) {
                createNsdItem(intent);
            }
        }

        private void createNsdItem(Intent intent) {
            NsdItem item = intent.getParcelableExtra(Const.EXTRA_NSD_ITEM);

            nsdVisibleItems.add(item.getNsdBaseItem());
            nsdHiddenItems.add(item);
            nsdItemIds.add(item.getId());
            NsdContent.addItem(item);
            nsdAdapter.notifyDataSetChanged();
        }

        private void createCcItem(Intent intent) {
            CcReceiverItem item = intent.getParcelableExtra(Const.EXTRA_ITEM_RECEIVER_CC);

            ccVisibleItems.add(item.getCcBaseItem());
            ccHiddenItems.add(item);
            ccItemIds.add(item.getId());
            ccAdapter.notifyDataSetChanged();
            CcContent.addItem(item);
        }
    }
}
