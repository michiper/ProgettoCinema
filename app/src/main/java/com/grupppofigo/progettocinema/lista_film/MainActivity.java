package com.grupppofigo.progettocinema.lista_film;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.grupppofigo.progettocinema.R;
import com.grupppofigo.progettocinema.SessionExpired;
import com.grupppofigo.progettocinema.entities.Film;
import com.grupppofigo.progettocinema.film_details.DescrizioneActivity;
import com.grupppofigo.progettocinema.helpers.ExtrasDefinition;
import com.grupppofigo.progettocinema.helpers.SessionValidator;
import com.grupppofigo.progettocinema.helpers.SnackBar;
import com.grupppofigo.progettocinema.queries.FilmQueries;
import com.grupppofigo.progettocinema.queries.SessioneQueries;

import java.lang.*;
import java.util.ArrayList;

import static com.grupppofigo.progettocinema.helpers.ExtrasDefinition.EXTRA_DEFAULT_VALUE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // id sessione
        final long idSessione = getIntent().getLongExtra(ExtrasDefinition.ID_TOKEN, EXTRA_DEFAULT_VALUE);
        if(idSessione == EXTRA_DEFAULT_VALUE) {
            SessionValidator.finishSession(this, idSessione);
        }

        // start della sessione
        final String startSession = getIntent().getStringExtra(ExtrasDefinition.START_SESSION);
        if(startSession == null) {
            SessionValidator.finishSession(this, idSessione);
        }
        else if(SessionValidator.isExpired(startSession)){
            // se è scaduta la registro e chiudo tutto
            SessioneQueries.endSession(idSessione);
            SessionValidator.finishSession(this, idSessione);
        }

        // id dell'utente passata dall'activity prima
        final int idUtente = getIntent().getIntExtra(ExtrasDefinition.ID_UTENTE, EXTRA_DEFAULT_VALUE);
        if(idUtente == EXTRA_DEFAULT_VALUE) {
            // errore idUtente non passato passo al login
            SessionValidator.finishSession(this, idSessione);
        }

        //gestisco list view e adapter
        ListView lv_film = findViewById(R.id.lv_film);

        final ArrayList<Film> films = FilmQueries.getAllFilms();
        AdapterFilm adapterFilm = new AdapterFilm(MainActivity.this, R.layout.film_list_item, films);
        lv_film.setAdapter(adapterFilm);

        Log.d("Films", films.toString());

        //per andare nell'altra activity cliccando sulla card
        lv_film.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int idFilm = films.get(position).getId();
                Log.d("Id film", idFilm + "");

                // se c'è un ID okey
                if (idFilm != EXTRA_DEFAULT_VALUE) {
                    Intent intent = new Intent(MainActivity.this, DescrizioneActivity.class);
                    intent.putExtra(ExtrasDefinition.ID_TOKEN, idSessione);
                    intent.putExtra(ExtrasDefinition.ID_UTENTE, idUtente);
                    intent.putExtra(ExtrasDefinition.START_SESSION, startSession);
                    intent.putExtra(ExtrasDefinition.ID_FILM, idFilm);
                    startActivity(intent);
                }
                else {
                    SnackBar.with(getApplicationContext())
                            .show(findViewById(R.id.mainContainer),
                                    R.string.error_film_click,
                                    Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

}
