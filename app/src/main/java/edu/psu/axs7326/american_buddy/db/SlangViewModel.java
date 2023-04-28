package edu.psu.axs7326.american_buddy.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SlangViewModel extends AndroidViewModel {
    private LiveData<List<Slang>> slangs;

    public SlangViewModel (Application application) {
        super(application);
        slangs = SlangDatabase.getDatabase(getApplication()).slangDAO().getAll();
    }

    public void filterSlangs(boolean onlyLiked) {
        if (onlyLiked)
            slangs = SlangDatabase.getDatabase(getApplication()).slangDAO().getLiked(true);
        else
            slangs = SlangDatabase.getDatabase(getApplication()).slangDAO().getAll();
    }

    public LiveData<List<Slang>> getAllSlangs() {
        return slangs;
    }
}