package geekgram.supernacho.ru.model.image.android;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.model.image.GlideApp;
import geekgram.supernacho.ru.model.image.IImageLoader;

public class ImageLoaderGlider implements IImageLoader<ImageView> {
    @Override
    public void loadInto(@Nullable String url, ImageView container) {
        GlideApp.with(container.getContext())
                .load(url)
                .placeholder(R.drawable.ic_photo)
                .into(container);
    }
}
