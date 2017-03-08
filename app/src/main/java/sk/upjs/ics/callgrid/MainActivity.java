package sk.upjs.ics.callgrid;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_ID = 1;
    public static final Bundle NO_BUNDLE = null;
    public static final Cursor NO_CURSOR = null;
    public static final int NO_FLAGS = 0;

    private GridView callLogGridView;

    private SimpleCursorAdapter callLogGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(LOADER_ID, NO_BUNDLE, this);

        String[] from = { CallLog.Calls.NUMBER };
        int[] to = { R.id.gridItemTextView };
        callLogGridViewAdapter = new SimpleCursorAdapter(this, R.layout.grid_item, NO_CURSOR, from, to, NO_FLAGS);

        callLogGridViewAdapter.setViewBinder(new CallLogViewBinder());

        callLogGridView = (GridView) findViewById(R.id.callLogGridView);
        callLogGridView.setAdapter(callLogGridViewAdapter);

        callLogGridView.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID) {
            CursorLoader loader = new CursorLoader(this);
            loader.setUri(CallLog.Calls.CONTENT_URI);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        callLogGridViewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        callLogGridViewAdapter.swapCursor(NO_CURSOR);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        String number
                = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        startCallActivity(number);
    }

    private void startCallActivity(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(WebView.SCHEME_TEL + number));
        startActivity(callIntent);
    }
}
