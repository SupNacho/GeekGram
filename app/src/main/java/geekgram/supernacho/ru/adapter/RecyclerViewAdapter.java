package geekgram.supernacho.ru.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

import geekgram.supernacho.ru.R;
import geekgram.supernacho.ru.model.PhotoModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PhotoModel> photos;

    public RecyclerViewAdapter(List<PhotoModel> photos) {
        this.photos = photos;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 5;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoModel photoModel = photos.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                if (photoModel.getPhotoSrc() != null) {
                    ((ViewCardTwo) holder).imageView.setImageURI(photoModel.getPhotoSrc());
                }
                if (photoModel.isFavorite()) {
                    ((ViewCardTwo) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_on);
                } else {
                    ((ViewCardTwo) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_off);
                }
                break;
            default:
                if (photoModel.getPhotoSrc() != null) {
                    ((ViewCardOne) holder).imageView.setImageURI(photoModel.getPhotoSrc());
                }
                if (photoModel.isFavorite()) {
                    ((ViewCardOne) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_on);
                } else {
                    ((ViewCardOne) holder).favoritesButton.setImageResource(R.drawable.ic_favorites_off);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

//    static class CardViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        ImageButton favoritesButton;
//
//        CardViewHolder(View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.image_view_cv);
//            favoritesButton = itemView.findViewById(R.id.image_button_favorites);
//        }
//    }

    class ViewCardOne extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton favoritesButton;

        ViewCardOne(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cv);
            favoritesButton = itemView.findViewById(R.id.image_button_favorites);
        }
    }

    class ViewCardTwo extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton favoritesButton;

        ViewCardTwo(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cv2);
            favoritesButton = itemView.findViewById(R.id.image_button_favorites_cv_2);
        }
    }
}
