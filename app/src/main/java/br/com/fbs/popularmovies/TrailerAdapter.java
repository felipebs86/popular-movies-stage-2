package br.com.fbs.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.fbs.popularmovies.dto.MovieDto;
import br.com.fbs.popularmovies.dto.TrailerDto;

/**
 * Created by felipe on 21/09/18.
 */

class TrailerAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<TrailerDto> mTrailer;

    public TrailerAdapter(Context context, List<TrailerDto> trailers) {
        mContext = context;
        mTrailer = trailers;
    }

    @Override
    public int getCount() {
        if (mTrailer == null ){
            return 0;
        }

        return mTrailer.size();
    }

    @Override
    public TrailerDto getItem(int position) {
        if (mTrailer == null || mTrailer.size() == 0) {
            return null;
        }

        return mTrailer.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
