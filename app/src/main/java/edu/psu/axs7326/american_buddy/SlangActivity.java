package edu.psu.axs7326.american_buddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.psu.axs7326.american_buddy.db.Slang;
import edu.psu.axs7326.american_buddy.db.SlangDatabase;
import edu.psu.axs7326.american_buddy.db.SlangViewModel;

public class SlangActivity extends AppCompatActivity  {

    private boolean filtered = false;  // Are results filtered by likes
    private SlangViewModel slangViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slang);
        if (savedInstanceState != null) {
            filtered = savedInstanceState.getBoolean("filtered");
        }

        // Set the action bar
        setSupportActionBar(findViewById(R.id.toolbar));

        RecyclerView recyclerView = findViewById(R.id.lstSlangs);
        SlangListAdapter adapter = new SlangListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        slangViewModel = new ViewModelProvider(this).get(SlangViewModel.class);
        //slangViewModel.filterSlangs(filtered);

        //slangViewModel.getAllSlangs().observe(this, new Observer<List<Slang>>() {
        //    public void onChanged(@Nullable final List<Slang> slangs) {
        //        // Update the cached copy of the words in the adapter.
        //        adapter.setSlangs(slangs);
        //    }
        //});
        // OR As a lambda expression:
        //slangViewModel.getAllSlangs().observe(this, slangs -> {adapter.setSlangs(slangs);});
        // As a function reference
        // ContainingClass::staticMethodName
        // containingObject::instanceMethodName
        slangViewModel.getAllSlangs().observe(this, adapter::setSlangs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_slang, menu);

        if (filtered) {
            menu.getItem(1).setIcon(R.drawable.ic_thumbs_up_down);
        } else {
            menu.getItem(1).setIcon(R.drawable.ic_thumb_up);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(this, AddActivity.class));
                return true;
            case R.id.menu_filter:
                filtered = !filtered;
                if (filtered) {
                    item.setIcon(R.drawable.ic_thumbs_up_down);
                } else {
                    item.setIcon(R.drawable.ic_thumb_up);
                }
                RecyclerView recyclerView = findViewById(R.id.lstSlangs);
                SlangListAdapter adapter = new SlangListAdapter(this);
                recyclerView.setAdapter(adapter);
                slangViewModel = new ViewModelProvider(this).get(SlangViewModel.class);
                slangViewModel.filterSlangs(filtered);

                slangViewModel.getAllSlangs().observe(this, adapter::setSlangs);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("filtered", filtered);
    }

    public void displaySetup(int id) {
        SlangDatabase.getSlang(id, slang -> {
            Bundle args = new Bundle();
            args.putInt("slang_id", slang.id);
            args.putString("title", slang.title);
            args.putString("slang", slang.slang);
            args.putString("english_meaning", slang.english_meaning);

            DisplaySetupDialog slangDialog = new DisplaySetupDialog();
            slangDialog.setArguments(args);
            slangDialog.show(getSupportFragmentManager(), "slangDialog");
        });
    }

    public void displayEnglish_Meaning(int id) {
        SlangDatabase.getSlang(id, slang -> {
            Bundle args = new Bundle();
            args.putInt("slang_id", slang.id);
            args.putString("title", slang.title);
            args.putString("slang", slang.slang);
            args.putString("english_meaning", slang.english_meaning);

            DisplayEnglish_MeaningDialog english_meaningDialog = new DisplayEnglish_MeaningDialog();
            english_meaningDialog.setArguments(args);
            english_meaningDialog.show(getSupportFragmentManager(), "english_meaningDialog");
        });
    }


    // Notes: This can be an outer class or a static nested class. We will make an inner class
    // since it is only used in the MainActivity _and_ we would like to simplify communication
    // with the activity
    public class SlangListAdapter extends RecyclerView.Adapter<SlangListAdapter.SlangViewHolder> {
        // If the SlangListAdapter were an outer class, the SlangViewHolder could be
        // a static class.  We want to be able to get access to the MainActivity instance,
        // so we want it to be an inner class
        class SlangViewHolder extends RecyclerView.ViewHolder {
            private final TextView titleView;
            private final ImageView likedView;
            private Slang slang;

            // Note that this view holder will be used for different items -
            // The callbacks though will use the currently stored item
            private SlangViewHolder(View itemView) {
                super(itemView);
                titleView = itemView.findViewById(R.id.txtTitle);
                likedView = itemView.findViewById(R.id.imgLiked);

                itemView.setOnLongClickListener(view -> {
                    // Note that we need a reference to the MainActivity instance
                    Intent intent = new Intent(SlangActivity.this, AddActivity.class);
                    // Note getItemId will return the database identifier
                    intent.putExtra("slang_id", slang.id);
                    // Note that we are calling a method of the MainActivity object
                    startActivity(intent);
                    return true;
                });

                itemView.setOnClickListener(view -> displaySetup(slang.id));

                likedView.setOnClickListener(view -> {
                    slang.liked = !slang.liked;
                    SlangDatabase.update(slang);
                });
            }
        }

        private final LayoutInflater layoutInflater;
        private List<Slang> slangs; // Cached copy of slangs

        SlangListAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public SlangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new SlangViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SlangViewHolder holder, int position) {
            if (slangs != null) {
                Slang current = slangs.get(position);
                holder.slang = current;
                holder.titleView.setText(current.title);
                if (current.liked) {
                    holder.likedView.setImageResource(R.drawable.ic_thumb_up);
                }
                else {
                    holder.likedView.setImageResource(R.drawable.ic_thumb_down);
                }
            } else {
                // Covers the case of data not being ready yet.
                holder.titleView.setText("...intializing...");
                holder.likedView.setImageResource(R.drawable.ic_thumb_down);
                holder.likedView.setTag("N");
            }
        }

        void setSlangs(List<Slang> slangs){
            this.slangs = slangs;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (slangs != null)
                return slangs.size();
            else return 0;
        }


    }


    public static class DisplaySetupDialog extends DialogFragment {
        int slang_id;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            slang_id = getArguments().getInt("slang_id");
            final String title = getArguments().getString("title");
            final String slang = getArguments().getString("slang");
            builder.setTitle(title)
                    .setMessage(slang)
                    .setPositiveButton("English Meaning",
                            (dialog, id) -> ((SlangActivity) getActivity()).displayEnglish_Meaning(slang_id))
                    .setNegativeButton("Cancel",
                            (dialog, id) -> {});
            return builder.create();
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("JJB", "tester");
        }
    }

    public static class DisplayEnglish_MeaningDialog extends DialogFragment {
        int slang_id;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            slang_id = getArguments().getInt("slang_id");
            String title = getArguments().getString("title");
            String english_meaning = getArguments().getString("english_meaning");

            builder.setTitle(title)
                    .setMessage(english_meaning)
                    .setPositiveButton("Liked", (dialog, id) -> {
                        SlangDatabase.getSlang(slang_id, slang -> {
                            slang.liked = true;
                            SlangDatabase.update(slang);
                        });
                    })
                    .setNeutralButton("Slang", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((SlangActivity) getActivity()).displaySetup(slang_id);
                        }
                    })
                    .setNegativeButton("Disiked", (dialog, id) -> {
                        SlangDatabase.getSlang(slang_id, slang -> {
                            slang.liked = false;
                            SlangDatabase.update(slang);
                        });
                    });

            return builder.create();
        }
    }

}