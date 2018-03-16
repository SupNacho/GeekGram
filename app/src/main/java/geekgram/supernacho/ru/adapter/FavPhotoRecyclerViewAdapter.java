package geekgram.supernacho.ru.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.model.PhotoModel;

public class FavPhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    private PhotoInterface photoFragment;

    public FavPhotoRecyclerViewAdapter(List<PhotoModel> photos, List<PhotoModel> favPhotos, WeakReference<PhotoInterface> fragmentWeakReference) {
        this.photos = photos;
        this.favPhotos = favPhotos;
        if (fragmentWeakReference.get() != null) {
            this.photoFragment = fragmentWeakReference.get(); // потом заменю на DI, это у нас в следующем курсе
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_two, null);
                return new ViewCardTwo(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_one, null);
                return new ViewCardOne(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PhotoModel photoModel = favPhotos.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                if (photoModel.getPhotoSrc() != null) {
                    Picasso.get().load(photoModel.getPhotoSrc()).into(((ViewCardTwo) holder).imageView);
                } else {
                    ((ViewCardTwo) holder).imageView.setImageResource(R.drawable.ic_photo);
                }
                if (photoModel.isFavorite()) {
                    ((ViewCardTwo) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_on);
                } else {
                    ((ViewCardTwo) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_off);
                }
                ((ViewCardTwo) holder).favoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoModel.setFavorite(!photoModel.isFavorite());
                        favListManage(photoModel);
                        notifyDataSetChanged();
                    }
                });
                break;
            default:
                if (photoModel.getPhotoSrc() != null) {
                    Picasso.get()
                            .load(photoModel.getPhotoSrc())
                            .into(((ViewCardOne) holder).imageView);
                } else {
                    ((ViewCardOne) holder).imageView.setImageResource(R.drawable.ic_photo);
                }
                if (photoModel.isFavorite()) {
                    ((ViewCardOne) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_on);
                } else {
                    ((ViewCardOne) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_off);
                }
                ((ViewCardOne) holder).favoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoModel.setFavorite(!photoModel.isFavorite());
                        favListManage(photoModel);
                        notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    private void favListManage(PhotoModel photoModel) {
        if (photoModel.isFavorite()) {
            favPhotos.add(photoModel);
        } else {
            favPhotos.remove(photoModel);
        }
    }

    @Override
    public int getItemCount() {
        return favPhotos.size();
    }

    class ViewCardOne extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton favoritesButton;

        ViewCardOne(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cv);
            favoritesButton = itemView.findViewById(R.id.image_button_favorites);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    photoFragment.deletePhoto(getLayoutPosition());
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    photoFragment.viewPhoto(getLayoutPosition());
                }
            });
        }
    }

    class ViewCardTwo extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton favoritesButton;

        ViewCardTwo(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cv2);
            favoritesButton = itemView.findViewById(R.id.image_button_favorites_cv_2);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    photoFragment.deletePhoto(getLayoutPosition());
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    photoFragment.viewPhoto(getLayoutPosition());
                }
            });
        }
    }
}
