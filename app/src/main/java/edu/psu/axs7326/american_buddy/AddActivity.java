package edu.psu.axs7326.american_buddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import edu.psu.axs7326.american_buddy.db.Slang;
import edu.psu.axs7326.american_buddy.db.SlangDatabase;


public class AddActivity extends AppCompatActivity {
    private int slang_id;
    private boolean liked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setSupportActionBar(findViewById(R.id.my_toolbar));

        slang_id = getIntent().getIntExtra("slang_id", -1);

        // Note: that we do not want to lose the state if the activity is being
        // recreated
        if (savedInstanceState == null) {
            if (slang_id != -1) {
                SlangDatabase.getSlang(slang_id, slang -> {
                    ((EditText) findViewById(R.id.txtEditTitle)).setText(slang.title);
                    ((EditText) findViewById(R.id.txtEditSetup)).setText(slang.slang);
                    ((EditText) findViewById(R.id.txtEditPunchline)).setText(slang.english_meaning);
                    liked = slang.liked;
                });
            }
        }
        else {
            liked = savedInstanceState.getBoolean("liked");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add, menu);
        if (slang_id == -1) {
            menu.getItem(1).setIcon(R.drawable.ic_cancel);
            menu.getItem(1).setTitle(R.string.menu_cancel);
            setTitle("Add slang");
        }
        else {
            setTitle("Edit slang");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                updateDatabase();
                return true;
            case R.id.menu_delete:
                if (slang_id != -1) {
                    ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog();
                    confirmDialog.show(getSupportFragmentManager(), "deletionConfirmation");
                }
                else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDatabase() {
        Slang slang = new Slang(slang_id == -1?0:slang_id,
                ((EditText) findViewById(R.id.txtEditTitle)).getText().toString(),
                ((EditText) findViewById(R.id.txtEditSetup)).getText().toString(),
                ((EditText) findViewById(R.id.txtEditPunchline)).getText().toString(),
                liked);
        if (slang_id == -1) {
            SlangDatabase.insert(slang);
        } else {
            SlangDatabase.update(slang);
        }
        finish(); // Quit activity
    }

    public void deleteRecord() {
        SlangDatabase.delete(slang_id);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("liked", liked);
    }

    public static class ConfirmDeleteDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Delete the slang?")
                    .setMessage("You will not be able to undo the deletion!")
                    .setPositiveButton("Delete",
                            (dialog,id) -> {
                                ((AddActivity) getActivity()).deleteRecord();
                                getActivity().finish();
                            })
                    .setNegativeButton("Return to slang list",
                            (dialog, id) -> getActivity().finish());
            return builder.create();
        }
    }

}
