package geekgram.supernacho.ru.model.image.android;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.model.IImageCache;
import geekgram.supernacho.ru.model.image.GlideApp;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.utils.NetworkStatus;

public class ImageLoaderGlider implements IImageLoader<ImageView> {
    private IImageCache cache;

    public ImageLoaderGlider(IImageCache cache) {
        this.cache = cache;
    }

    @Override
    public void loadInto(@Nullable String url, ImageView container) {
        GlideApp.with(container.getContext())
                .load(url)
                .placeholder(R.drawable.ic_photo)
                .into(container);
    }

    @Override
    public void loadIntoFromNet(@Nullable String url, ImageView container) {
        if (NetworkStatus.isOnline()) {
            GlideApp.with(container.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_photo)
                    .into(container);
        } else {
            if (cache.contains(url)){
                GlideApp.with(container.getContext())
                        .load(R.drawable.ic_photo)
                        .placeholder(R.drawable.ic_photo)
                        .into(container);
            }
        }
    }
}
