package br.com.fbs.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import br.com.fbs.popularmovies.dto.MovieDto;

/**
 * Created by felipe on 15/11/18.
 */

@Entity
public class FavoriteMovie implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(movieId);
        parcel.writeString(movieTitle);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(plotSynopsis);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
    }

    public static final Parcelable.Creator<FavoriteMovie> CREATOR
            = new Parcelable.Creator<FavoriteMovie>() {
        public FavoriteMovie createFromParcel(Parcel in) {
            return new FavoriteMovie(in);
        }

        public FavoriteMovie[] newArray(int size) {
            return new FavoriteMovie[size];
        }
    };

    @PrimaryKey
    @NonNull
    public String movieId;
    public String movieTitle;
    public String originalTitle;
    public String posterPath;
    public String plotSynopsis;
    public String userRating;
    public String releaseDate;

    public FavoriteMovie() {
    }

    private FavoriteMovie(Parcel in) {
        this.movieId = in.readString();
        this.movieTitle = in.readString();
        this.originalTitle = in.readString();
        this.posterPath = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    public static FavoriteMovie favoriteMovieFrom(MovieDto movieDto) {
        FavoriteMovie favoriteMovie = new FavoriteMovie();
        favoriteMovie.movieId = movieDto.getId();
        favoriteMovie.movieTitle = movieDto.getTitle();
        favoriteMovie.posterPath = movieDto.getPosterPath();
        favoriteMovie.plotSynopsis = movieDto.getSynopsis();
        favoriteMovie.userRating = movieDto.getVoteAverage().toString();
        favoriteMovie.releaseDate = movieDto.getReleaseDate();
        return favoriteMovie;
    }
}
