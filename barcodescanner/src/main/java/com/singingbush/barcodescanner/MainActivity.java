package com.singingbush.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.sourceforge.zbar.Symbol;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String firstBarcode;

    enum ScanningState {
        START,
        FIRST_BARCODE_SCANNED,
        SECOND_BARCODE_SCANNED

    }

    private ScanningState scanningState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanningState = ScanningState.START;
        Log.v(TAG, "starting " + TAG);

        setContentView(R.layout.activity_main);
    }

//    @Override // todo
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ZBarScannerActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    handleResults(intent, "Zbar");
                }
                break;
        }
    }

    private void handleResults(final Intent result, final String provider) {
        if (scanningState == ScanningState.START || scanningState == ScanningState.SECOND_BARCODE_SCANNED) {
            reset();
            scanningState = ScanningState.FIRST_BARCODE_SCANNED;
            updateMessage();
            updatePrimaryButton();
            firstBarcode = result.getStringExtra("SCAN_RESULT");
        } else {
            scanningState = ScanningState.SECOND_BARCODE_SCANNED;
            updateMessage();
            updatePrimaryButton();
            if (firstBarcode.equals(result.getStringExtra("SCAN_RESULT"))) {
                View contentView = findViewById(R.id.barcode_equal);
                contentView.setVisibility(View.VISIBLE);
            } else {
                View contentView = findViewById(R.id.barcode_not_equal);
                contentView.setVisibility(View.VISIBLE);
            }
        }
//        Toast.makeText(this, String.format("barcode: '%s' type: %s", barcode, type), Toast.LENGTH_SHORT).show();
    }

    private void updatePrimaryButton() {
        Button contentView = (Button) findViewById(R.id.scan_button_zbar);

        switch (scanningState) {
            case START:
                contentView.setText(R.string.scan_button_start);
                break;
            case FIRST_BARCODE_SCANNED:
                contentView.setText(R.string.scan_button_continue);
                break;
            case SECOND_BARCODE_SCANNED:
                contentView.setText(R.string.scan_button_restart);
                break;
        }
    }

    private void updateMessage() {
        TextView contentView = (TextView) findViewById(R.id.fullscreen_content);

        switch (scanningState) {
            case START:
                contentView.setText(R.string.start_content);
                break;
            case FIRST_BARCODE_SCANNED:
                contentView.setText(R.string.continue_content);
                break;
            case SECOND_BARCODE_SCANNED:
                contentView.setText(R.string.restart_content);
                break;
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_button_zbar:
                scanWithZBar();
                break;
            case R.id.exit_button:
                Log.v(TAG, "exit button clicked");
                finish();
                break;
        }
    }


    private void reset() {
        firstBarcode = null;
        View contentView = findViewById(R.id.barcode_equal);
        contentView.setVisibility(View.INVISIBLE);
        contentView = findViewById(R.id.barcode_not_equal);
        contentView.setVisibility(View.INVISIBLE);
    }

    private void scanWithZBar() {
        Intent zbarIntent = new Intent(this, ZBarScannerActivity.class);
        zbarIntent.putExtra(ZBarScannerActivity.SCAN_MODES, new int[]{Symbol.QRCODE, Symbol.EAN13, Symbol.UPCE, Symbol.UPCA, Symbol.CODE128});
        startActivityForResult(zbarIntent, ZBarScannerActivity.REQUEST_CODE);
    }
}
