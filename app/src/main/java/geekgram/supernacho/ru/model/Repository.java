package geekgram.supernacho.ru.model;

import java.util.ArrayList;
import java.util.List;

public class Repository implements IRepository {
    private static final Repository ourInstance = new Repository();
    private List<PhotoModel> photos;

    public static Repository getInstance() {
        return ourInstance;
    }

    private Repository() {
        photos = new ArrayList<>();
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
    }

    @Override
    public List<PhotoModel> getPhotos() {
        return photos;
    }
}
