package br.com.fbs.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.fbs.popularmovies.dto.MovieDto;

public class DetailActivity extends AppCompatActivity {

    private final String INTENT_EXTRAS = "movieDetails";
    private MovieDto movieDto;
    private TextView title;
    private TextView releaseDate;
    private TextView voteAverage;
    private TextView synopsis;
    private ImageView poster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = findViewById(R.id.tv_detail_title);
        releaseDate = findViewById(R.id.tv_detail_release_date);
        voteAverage = findViewById(R.id.tv_vote_average);
        synopsis = findViewById(R.id.tv_detail_synopse);
        poster = findViewById(R.id.iv_detail_poster);

        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_EXTRAS)) {
            movieDto = intent.getParcelableExtra(INTENT_EXTRAS);

            title.setText(movieDto.getTitle());
            releaseDate.setText(movieDto.getReleaseDate());
            voteAverage.setText(String.valueOf(movieDto.getVoteAverage()));
            synopsis.setText(movieDto.getSynopsis());

            Picasso.with(this)
                    .load(movieDto.getPosterPath())
                    .resize(185, 278)
                    .into(poster);
        }
    }
}
