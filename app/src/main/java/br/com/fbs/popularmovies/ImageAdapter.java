package br.com.fbs.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.fbs.popularmovies.dto.MovieDto;

/**
 * Created by felipe on 21/09/18.
 */

class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<MovieDto> mMovies;

    public ImageAdapter(Context context, List<MovieDto> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public int getCount() {
        if (mMovies == null || mMovies.size() == 0) {
            return -1;
        }

        return mMovies.size();
    }

    @Override
    public MovieDto getItem(int position) {
        if (mMovies == null || mMovies.size() == 0) {
            return null;
        }

        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mMovies.get(position).getPosterPath())
                .resize(185, 278)
                .into(imageView);

        return imageView;
    }
}
