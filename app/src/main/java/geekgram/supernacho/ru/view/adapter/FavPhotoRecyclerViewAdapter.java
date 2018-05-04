package geekgram.supernacho.ru.view.adapter;

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
import geekgram.supernacho.ru.model.api.ApiConst;
import geekgram.supernacho.ru.model.image.IImageLoader;
import geekgram.supernacho.ru.presenters.IFragmentPresenter;

public class FavPhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PhotoModel> favPhotos;
    private IFragmentPresenter presenter;
    @Inject
    IImageLoader<ImageView> imageLoader;

    public FavPhotoRecyclerViewAdapter(IFragmentPresenter presenter) {
        this.presenter = presenter;
        this.favPhotos = presenter.getFavPhotos();
    }

    @Override
    public int getItemViewType(int position) {
        if (favPhotos.get(position).getPhotoSrc().startsWith(ApiConst.NET_URI_STARTS)) {
            return AViewConstsnts.VIEW_INSTAGRAM;
        }
        return AViewConstsnts.VIEW_DB;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AViewConstsnts.VIEW_INSTAGRAM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_two, parent, false);
                return new ViewCardTwo(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_one, parent, false);
                return new ViewCardOne(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PhotoModel photoModel = favPhotos.get(position);
        switch (holder.getItemViewType()) {
            case AViewConstsnts.VIEW_INSTAGRAM:
                if (photoModel.getPhotoSrc() != null) {
                    imageLoader.loadInto(photoModel.getPhotoSrc(), ((ViewCardTwo) holder).imageView);
                } else {
                    ((ViewCardTwo) holder).imageView.setImageResource(R.drawable.ic_photo);
                }
                break;
            default:
                if (photoModel.getPhotoSrc() != null) {
                    imageLoader.loadInto(photoModel.getPhotoSrc(), ((ViewCardOne) holder).imageView);
                } else {
                    ((ViewCardOne) holder).imageView.setImageResource(R.drawable.ic_photo);
                }
                if (photoModel.isFavorite()) {
                    ((ViewCardOne) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_on);
                } else {
                    ((ViewCardOne) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_off);
                }
                ((ViewCardOne) holder).favoritesButton.setOnClickListener(view -> {
                    presenter.setFavorite(photoModel);
                    notifyDataSetChanged();
                });
                break;
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
            itemView.setOnLongClickListener(view -> {
                presenter.deleteDialog(getLayoutPosition());
                return false;
            });
            itemView.setOnClickListener(view -> presenter.viewPhoto(getLayoutPosition()));
        }
    }

    class ViewCardTwo extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewCardTwo(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cv2);
            itemView.setOnLongClickListener(view -> {
                presenter.deleteDialog(getLayoutPosition());
                return false;
            });
            itemView.setOnClickListener(view -> presenter.viewPhoto(getLayoutPosition()));
        }
    }
}
