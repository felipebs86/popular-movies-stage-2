package br.com.fbs.popularmovies.dto;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.fbs.popularmovies.model.FavoriteMovie;

/**
 * Created by felipe on 21/09/18.
 */

public class MovieDto implements Parcelable {
    private String mId;
    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private Double mVoteAverage;
    private String mReleaseDate;

    public MovieDto() {
    }

    protected MovieDto(Parcel in) {
        mTitle = in.readString();
        mPosterPath = in.readString();
        mSynopsis = in.readString();
        if (in.readByte() == 0) {
            mVoteAverage = null;
        } else {
            mVoteAverage = in.readDouble();
        }
        mReleaseDate = in.readString();
        mId = in.readString();
    }

    public static final Creator<MovieDto> CREATOR = new Creator<MovieDto>() {
        @Override
        public MovieDto createFromParcel(Parcel in) {
            return new MovieDto(in);
        }

        @Override
        public MovieDto[] newArray(int size) {
            return new MovieDto[size];
        }
    };

    public void setTitle(String Title) {
        mTitle = Title;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public void setSynopsis(String synopsis) { mSynopsis = synopsis; }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) { mReleaseDate = releaseDate; }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() { return mReleaseDate; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        if (mVoteAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(mVoteAverage);
        }
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public static MovieDto MovieDtoFrom(FavoriteMovie favoriteMovie) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(favoriteMovie.movieId);
        movieDto.setTitle(favoriteMovie.movieTitle);
        movieDto.setPosterPath(favoriteMovie.posterPath);
        movieDto.setSynopsis(favoriteMovie.plotSynopsis);
        movieDto.setVoteAverage(Double.valueOf(favoriteMovie.userRating));
        movieDto.setReleaseDate(favoriteMovie.releaseDate);
        return movieDto;
    }
}
