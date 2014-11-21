package com.steganomobile.common.sender.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.steganomobile.common.Const;
import com.steganomobile.common.R;
import com.steganomobile.common.sender.model.CcInfo;
import com.steganomobile.common.sender.model.CcMethod;
import com.steganomobile.common.sender.model.CcSenderItem;
import com.steganomobile.common.sender.model.CcStatus;
import com.steganomobile.common.sender.model.CcSync;
import com.steganomobile.common.sender.model.CcType;

public class SenderActivity extends Activity {

    private static final String TAG = SenderActivity.class.getSimpleName();
    private boolean isScreenReceiverRegistered = false;
    private boolean sendAfterScreenGoesOff = false;
    private ScreenReceiver screenReceiver = new ScreenReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && sendAfterScreenGoesOff) {
                    sendStegano();
                }
            }
        }
    };
    private StringBuilder data = new StringBuilder();
    private StateReceiver stateReceiver = new StateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView myTextView = (TextView) findViewById(R.id.textViewState);
            CcInfo ccInfo = intent.getParcelableExtra(Const.EXTRA_CC_INFO);
            if (ccInfo.getStatus() == CcStatus.FINISH) {
                myTextView.setText(getResources().getString(R.string.state_idle));

            } else if (ccInfo.getStatus() == CcStatus.START) {
                int nameId = ccInfo.getName().getValue();
                if (nameId > 0 && nameId <= CcMethod.NAMES.length)
                    myTextView.setText(
                            getResources().getString(R.string.state_sending) + " - " + CcMethod.NAMES[nameId]
                    );
                data.delete(0, data.capacity());

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        prepareScreenListener();
        prepareSmsListener();
        prepareStateListener();
        prepareButtonPhoneState(createPhoneStateDialog());
        prepareButtonMessage(createMessageDialog());
        prepareButtonTestIterations(createTestIterationsDialog());
        prepareButtonChooseCc(createChooseCcDialog());
        prepareButtonChooseSync(createChooseSyncDialog());
        prepareButtonChooseScenario(createChooseScenarioDialog());
        prepareButtonTimeInterval(createTimeIntervalDialog());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), Const.REQUEST_SETTINGS);
        } else if (i == R.id.action_cancel) {
            cancelApplication();
        } else if (i == R.id.action_split) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelApplication() {
        finish();
    }

    private void prepareStateListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_INFO);
        registerReceiver(stateReceiver, filter);
    }

    private Dialog createTimeIntervalDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_time_interval);
        dialog.setTitle(getResources().getString(R.string.set_interval));
        updateTimeIntervalButton();
        return dialog;
    }

    private void updateTimeIntervalButton() {
        Button button = (Button) findViewById(R.id.buttonTimeInterval);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String text = preferences.getString(Const.PREF_KEY_INTERVAL, Const.DEFAULT_INTERVAL);
        button.setText(getResources().getText(R.string.select_interval) + " " + text + " [ms]");
    }

    private Dialog createChooseScenarioDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_scenario);
        dialog.setTitle(getResources().getString(R.string.set_scenario));
        return dialog;
    }

    private Dialog createChooseCcDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_cc);
        dialog.setTitle(getResources().getString(R.string.set_cc));
        updateChooseCcButton();
        return dialog;
    }

    private Dialog createChooseSyncDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_sync);
        dialog.setTitle(getResources().getString(R.string.set_sync));
        updateChooseSyncButton();
        return dialog;
    }

    private void updateChooseCcButton() {
        Button button = (Button) findViewById(R.id.buttonChooseCc);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String text = preferences.getString(Const.PREF_KEY_METHODS, "0");
        button.setText(getResources().getText(R.string.select_cc) + " " + CcMethod.NAMES[Integer.parseInt(text)]);
    }

    private void updateChooseSyncButton() {
        Button button = (Button) findViewById(R.id.buttonChooseSync);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String text = preferences.getString(Const.PREF_KEY_SYNC, Const.DEFAULT_SYNC);
        button.setText(getResources().getText(R.string.select_sync) + " " + CcSync.NAMES[Integer.parseInt(text)]);
    }

    private Dialog createTestIterationsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_test_iterations);
        dialog.setTitle(getResources().getString(R.string.set_iterations));
        updateTestIterationsButton();
        return dialog;
    }

    private void updateTestIterationsButton() {
        Button button = (Button) findViewById(R.id.buttonTestIterations);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String text = preferences.getString(Const.PREF_KEY_ITERATIONS, Const.DEFAULT_ITERATIONS);
        button.setText(getResources().getText(R.string.select_iterations) + " " + text);
    }

    private void prepareButtonTimeInterval(final Dialog dialog) {

        final InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.buttonTimeInterval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                dialog.show();
            }
        });

        dialog.findViewById(R.id.buttonSetTimeInterval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = (EditText) dialog.findViewById(R.id.editTextTimeInterval);
                String text = editText.getText().toString();

                if (text.length() > 0) {
                    Button myButton = (Button) findViewById(R.id.buttonTimeInterval);
                    myButton.setText(getResources().getText(R.string.select_interval) + " " + text + " [ms]");
                    SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                    myEditor.putString(Const.PREF_KEY_INTERVAL, text);
                    myEditor.apply();
                    editText.getText().clear();
                }
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dialog.dismiss();
            }
        });

    }

    private void prepareButtonChooseCc(final Dialog dialog) {

        final RadioGroup myRadioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupChooseCc);
        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int option = getCcName(checkedId).getValue();
                if (option != -1) {
                    Button myButton = (Button) findViewById(R.id.buttonChooseCc);
                    myButton.setText(getResources().getText(R.string.select_cc) + " " + CcMethod.NAMES[option]);
                    SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                    myEditor.putString(Const.PREF_KEY_METHODS, String.valueOf(option));
                    myEditor.apply();
                    dialog.dismiss();
                }
            }

            private CcMethod getCcName(int checkedRadioButtonId) {
                if (checkedRadioButtonId == R.id.radioButtonVolumeMusic) {
                    return CcMethod.VOLUME_MUSIC;
                } else if (checkedRadioButtonId == R.id.radioButtonVolumeRing) {
                    return CcMethod.VOLUME_RING;
                } else if (checkedRadioButtonId == R.id.radioButtonVolumeNotification) {
                    return CcMethod.VOLUME_NOTIFICATION;
                } else if (checkedRadioButtonId == R.id.radioButtonVolumeSystem) {
                    return CcMethod.VOLUME_SYSTEM;
                } else if (checkedRadioButtonId == R.id.radioButtonVolumeAlarm) {
                    return CcMethod.VOLUME_ALARM;
                } else if (checkedRadioButtonId == R.id.radioButtonVolumeVoiceCall) {
                    return CcMethod.VOLUME_VOICE_CALL;
                } else if (checkedRadioButtonId == R.id.radioButtonVolumeDtmf) {
                    return CcMethod.VOLUME_DTMF;
                } else if (checkedRadioButtonId == R.id.radioButtonFileExistence) {
                    return CcMethod.FILE_EXISTENCE;
                } else if (checkedRadioButtonId == R.id.radioButtonFileSize) {
                    return CcMethod.FILE_SIZE;
                } else if (checkedRadioButtonId == R.id.radioButtonFileLock) {
                    return CcMethod.FILE_LOCK;
                } else if (checkedRadioButtonId == R.id.radioButtonTypeOfIntentObserver) {
                    return CcMethod.CONTENT_OF_URI;
                } else if (checkedRadioButtonId == R.id.radioButtonTypeOfIntentReceiver) {
                    return CcMethod.TYPE_OF_INTENT;
                } else if (checkedRadioButtonId == R.id.radioButtonUsageTrend) {
                    return CcMethod.USAGE_TREND;
                } else if (checkedRadioButtonId == R.id.radioButtonSystemLoad) {
                    return CcMethod.SYSTEM_LOAD;
                } else if (checkedRadioButtonId == R.id.radioButtonUnixSocket) {
                    return CcMethod.UNIX_SOCKET_DISCOVERY;
                } else if (checkedRadioButtonId == R.id.radioButtonMemoryLoad) {
                    return CcMethod.MEMORY_LOAD;
                }
                return CcMethod.NO_VALUE;
            }
        });

        findViewById(R.id.buttonChooseCc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                myRadioGroup.clearCheck();
                dialog.show();
            }
        });
    }

    private void prepareButtonChooseSync(final Dialog dialog) {

        final RadioGroup myRadioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupChooseSync);
        myRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int option = getCcName(checkedId).getValue();
                if (option != -1) {
                    Button myButton = (Button) findViewById(R.id.buttonChooseSync);
                    myButton.setText(getResources().getText(R.string.select_sync) + " " + CcSync.NAMES[option]);
                    SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor myEditor = mySharedPreferences.edit();
                    myEditor.putString(Const.PREF_KEY_SYNC, String.valueOf(option));
                    myEditor.apply();
                    dialog.dismiss();
                }
            }

            private CcSync getCcName(int checkedRadioButtonId) {
                if (checkedRadioButtonId == R.id.radioButtonHandler) {
                    return CcSync.HANDLER;
                } else if (checkedRadioButtonId == R.id.radioButtonBroadcastReceiver) {
                    return CcSync.BROADCAST_RECEIVER;
                } else if (checkedRadioButtonId == R.id.radioButtonContentObserver) {
                    return CcSync.CONTENT_OBSERVER;
                } else {
                    return CcSync.NO_VALUE;
                }
            }
        });

        findViewById(R.id.buttonChooseSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                myRadioGroup.clearCheck();
                dialog.show();
            }
        });
    }

    private void prepareButtonChooseScenario(final Dialog dialog) {

        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupChooseScenario);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int scenario = getScenarioId(checkedId);
                if (scenario != Const.SCENARIO_NOT_FOUND) {
                    sendScenario(scenario);
                    dialog.dismiss();
                    group.clearCheck();
                }
            }

            private void sendScenario(int scenario) {
                Intent intent = new Intent(Const.ACTION_START_SCENARIO);
                intent.putExtra(Const.EXTRA_SCENARIO, scenario);
                sendBroadcast(intent);
            }

            private int getScenarioId(int checkedRadioButtonId) {
                if (checkedRadioButtonId == R.id.radioButtonScenario1) {
                    return Const.SCENARIO_1;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario2) {
                    return Const.SCENARIO_2;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario3) {
                    return Const.SCENARIO_3;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario4) {
                    return Const.SCENARIO_4;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario5) {
                    return Const.SCENARIO_5;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario6) {
                    return Const.SCENARIO_6;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario7) {
                    return Const.SCENARIO_7;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario8) {
                    return Const.SCENARIO_8;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario9) {
                    return Const.SCENARIO_9;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario10) {
                    return Const.SCENARIO_10;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario11) {
                    return Const.SCENARIO_11;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario12) {
                    return Const.SCENARIO_12;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario13) {
                    return Const.SCENARIO_13;
                } else if (checkedRadioButtonId == R.id.radioButtonScenario14) {
                    return Const.SCENARIO_14;
                }
                return Const.SCENARIO_NOT_FOUND;
            }
        });

        findViewById(R.id.buttonScenario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.show();
            }
        });
    }

    private void prepareButtonTestIterations(final Dialog dialog) {

        final InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.buttonTestIterations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                dialog.show();
            }
        });

        dialog.findViewById(R.id.buttonSetTestIterations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = (EditText) dialog.findViewById(R.id.editTextTestIterations);
                String myText = editText.getText().toString();

                if (myText.length() > 0) {
                    Button button = (Button) findViewById(R.id.buttonTestIterations);
                    button.setText(getResources().getText(R.string.select_iterations) + " " + myText);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Const.PREF_KEY_ITERATIONS, myText);
                    editor.apply();
                    editText.getText().clear();
                }
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
    }

    private void prepareButtonMessage(final Dialog messageDialog) {
        final InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.buttonMessage).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageDialog.show();
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        messageDialog.findViewById(R.id.buttonSendDialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) messageDialog.findViewById(R.id.editTextMessage);
                if (editText.getText() != null) {
                    setType(CcType.MESSAGE);
                    data.append(editText.getText());
                    if (!sendAfterScreenGoesOff) {
                        sendStegano();
                    } else {
                        String text = getResources().getString(R.string.will_be_sent_after_screen_goes_off);
                        Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                    }
                    if (editText.length() > 0) {
                        TextKeyListener.clear(editText.getText());
                    }
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    messageDialog.dismiss();
                }
            }
        });
    }

    private void prepareButtonPhoneState(final Dialog phoneStateDialog) {
        final RadioGroup group = (RadioGroup) phoneStateDialog.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            private int prevChecked = 0;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1 || checkedId == prevChecked) {
                    prevChecked = 0;
                    return;
                }
                setType(getTypeId(checkedId));
                if (!sendAfterScreenGoesOff) {
                    sendStegano();
                } else {
                    String text = getResources().getString(R.string.will_be_sent_after_screen_goes_off);
                    Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                }
                prevChecked = checkedId;
                phoneStateDialog.dismiss();
            }

            private CcType getTypeId(int checkedRadioButtonId) {
                if (checkedRadioButtonId == R.id.radioButtonImei) {
                    return CcType.IMEI;
                } else if (checkedRadioButtonId == R.id.radioButtonCellLocation) {
                    return CcType.CELL_LOCATION;
                } else if (checkedRadioButtonId == R.id.radioButtonOperatorName) {
                    return CcType.OPERATOR_NAME;
                } else if (checkedRadioButtonId == R.id.radioButtonLocation) {
                    return CcType.LOCATION;
                } else if (checkedRadioButtonId == R.id.radioButtonContactsList) {
                    return CcType.CONTACTS;
                } else if (checkedRadioButtonId == R.id.radioButtonSms) {
                    return CcType.SMS;
                }
                return CcType.NO_VALUE;
            }
        });

        findViewById(R.id.buttonPhoneState).setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                group.clearCheck();
                phoneStateDialog.show();
            }
        });
    }

    private void addSettingsInfoToIntent(Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int interval = Integer.parseInt(preferences.getString(Const.PREF_KEY_INTERVAL, Const.DEFAULT_INTERVAL));
        int iterations = Integer.parseInt(preferences.getString(Const.PREF_KEY_ITERATIONS, Const.DEFAULT_ITERATIONS));
        int ccNameId = Integer.parseInt(preferences.getString(Const.PREF_KEY_METHODS, Const.DEFAULT_METHODS));
        int typeId = Integer.parseInt(preferences.getString(Const.PREF_KEY_TYPE, Const.DEFAULT_TYPE));
        int syncId = Integer.parseInt(preferences.getString(Const.PREF_KEY_SYNC, Const.DEFAULT_SYNC));
        CcStatus status = CcStatus.START;
        CcType type = CcType.getFromInt(typeId);
        CcSync sync = CcSync.getFromInt(syncId);
        CcMethod name = CcMethod.getFromInt(ccNameId);
        CcInfo info = new CcInfo(status, name, iterations, type, interval, sync);
        CcSenderItem item = new CcSenderItem(data.toString(), info);

        intent.putExtra(Const.EXTRA_ITEM_SENDER_CC, item);
        data.setLength(0);
    }

    private void sendStegano() {
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_START_SENDER_CC);
        addSettingsInfoToIntent(intent);
        sendBroadcast(intent);
    }

    private Dialog createPhoneStateDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_phone_state_sender);
        dialog.setTitle(getResources().getString(R.string.send_phone_state));
        return dialog;
    }

    private Dialog createMessageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_message_sender);
        dialog.setTitle(getResources().getString(R.string.send_message));
        return dialog;
    }

    private void prepareSmsListener() {
        final Intent intent = new Intent();
        Switch aSwitch = (Switch) findViewById(R.id.switchSms);
        if (aSwitch.isChecked()) {
            intent.setAction(Const.ACTION_START_SMS_LISTENING);
        } else {
            intent.setAction(Const.ACTION_STOP_SMS_LISTENING);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    intent.setAction(Const.ACTION_START_SMS_LISTENING);
                } else {
                    intent.setAction(Const.ACTION_STOP_SMS_LISTENING);
                }
                getBaseContext().sendBroadcast(intent);
            }
        });
        getBaseContext().sendBroadcast(intent);
    }

    private void prepareScreenListener() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        Switch aSwitch = (Switch) findViewById(R.id.switchScreen);
        if (aSwitch.isChecked()) {
            registerReceiver(screenReceiver, filter);
            sendAfterScreenGoesOff = aSwitch.isChecked();
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerReceiver(screenReceiver, filter);
                    isScreenReceiverRegistered = true;
                } else {
                    unregisterReceiver(screenReceiver);
                    isScreenReceiverRegistered = false;
                }
                sendAfterScreenGoesOff = isChecked;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
        if (isScreenReceiverRegistered) unregisterReceiver(screenReceiver);
//        getActivity().unregisterReceiver(smsReceiver);
    }

    private void setType(CcType type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor myEditor = preferences.edit();
        myEditor.putString(Const.PREF_KEY_TYPE, String.valueOf(type.getValue()));
        myEditor.apply();
    }

    public abstract class ScreenReceiver extends BroadcastReceiver {
    }

    public abstract class StateReceiver extends BroadcastReceiver {
    }
}