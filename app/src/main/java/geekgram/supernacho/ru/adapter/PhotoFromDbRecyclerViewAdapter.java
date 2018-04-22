package geekgram.supernacho.ru.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

import javax.inject.Inject;

import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.model.PhotoModel;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.presenters.IFragmentPresenter;

public class PhotoFromDbRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PhotoModel> photos;
    private List<PhotoModel> favPhotos;
    private IFragmentPresenter presenter;
    @Inject
    IImageLoader<ImageView> imageLoader;

    public PhotoFromDbRecyclerViewAdapter(IFragmentPresenter presenter) {
        this.presenter = presenter;
        this.photos = presenter.getPhotos();
        this.favPhotos = presenter.getFavPhotos();
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_two, parent, false);
                return new ViewCardTwo(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_one, parent, false);
                return new ViewCardOne(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PhotoModel photoModel = photos.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                if (photoModel.getPhotoSrc() != null) {
                    imageLoader.loadInto(photoModel.getPhotoSrc(), ((ViewCardTwo) holder).imageView);

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
                        presenter.setFavorite(photoModel);
                        notifyDataSetChanged();
                    }
                });
                break;
            default:
                if (photoModel.getPhotoSrc() != null) {
                    imageLoader.loadInto(photoModel.getPhotoSrc(), ((ViewCardOne) holder).imageView);
                }  else {
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
                        presenter.setFavorite(photoModel);
                        notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
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
                    presenter.deleteDialog(getLayoutPosition());
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.viewPhoto(getLayoutPosition());
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
                    presenter.deleteDialog(getLayoutPosition());
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.viewPhoto(getLayoutPosition());
                }
            });
        }
    }
}