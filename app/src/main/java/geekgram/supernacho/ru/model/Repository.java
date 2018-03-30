package geekgram.supernacho.ru.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Repository extends Observable implements IRepository {
    private static final Repository ourInstance = new Repository();
    private List<PhotoModel> photos;
    private List<Observer> observers;

    public static Repository getInstance() {
        return ourInstance;
    }

    private Repository() {
        photos = new ArrayList<>();
        observers = new ArrayList<>();
        //Start Demo data
        boolean isFav;
        for (int i = 0; i < 3; i++) {
            isFav = (i % 3) == 0;
            photos.add(new PhotoModel(isFav, null));
        }
        //End Demo data
    }

    @Override
    public void addPhoto(boolean isFavorite, String uriString) {
        photos.add(0, new PhotoModel(isFavorite, uriString));
        Log.d("++", "Photo Added Repository");
        notifyObservers();
    }

    @Override
    public void addPhoto(int pos, PhotoModel pm) {
        photos.add(pos, pm);
        notifyObservers();
    }

    @Override
    public void remove(int pos) {
        photos.remove(pos);
        notifyObservers();
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
//        observers.add(o);
    }

    @Override
    public void favoriteIsChanged() {
        notifyObservers();
    }
}
