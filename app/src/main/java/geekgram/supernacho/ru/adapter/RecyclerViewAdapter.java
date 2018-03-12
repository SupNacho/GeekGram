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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CardViewHolder> {
    private List<PhotoModel> photos;

    public RecyclerViewAdapter(List<PhotoModel> photos) {
        this.photos = photos;
    }

    @Override
    public RecyclerViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_type_one, null);
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.CardViewHolder holder, int position) {
        PhotoModel photoModel = photos.get(position);
        if (photoModel.getPhotoSrc() != null) {
            holder.imageView.setImageURI(photoModel.getPhotoSrc());
        }
        if (photoModel.isFavorite()){
            holder.favoritesButton.setImageResource(R.drawable.ic_favorites_on);
        } else {
            holder.favoritesButton.setImageResource(R.drawable.ic_favorites_off);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageButton favoritesButton;

        public CardViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_cv);
            favoritesButton = itemView.findViewById(R.id.image_button_favorites);
        }
    }
}
