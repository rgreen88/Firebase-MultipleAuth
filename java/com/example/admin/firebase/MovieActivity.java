package com.example.admin.firebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity {

    private static final String TAG = "MovieActivity";
    private EditText movieNameET;
    private EditText movieDirectorET;
    private EditText movieProducerET;
    FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        user = FirebaseAuth.getInstance().getCurrentUser();

        movieNameET = (EditText) findViewById(R.id.etMovieName);
        movieDirectorET = (EditText) findViewById(R.id.etMovieDirector);
        movieProducerET = (EditText) findViewById(R.id.etMovieProducer);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference( "movies" );
    }

    public void addMovie(View view) {
        String name, director, producer;

        name = movieNameET.getText().toString();
        director = movieDirectorET.getText().toString();
        producer = movieProducerET.getText().toString();
        Movie m = new Movie( name, director, producer );

        myRef.child( user.getUid() )
                .push()
                .setValue( m )
        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MovieActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MovieActivity.this, "Not Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMovies(View view) {
        final List<Movie> movies = new ArrayList<>();

        myRef.child( user.getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean hasMovies = dataSnapshot.hasChildren();
                Log.d(TAG, "onDataChange: " + hasMovies);

                if( hasMovies ) {
                    Log.d(TAG, "onDataChange: Movie Count" + dataSnapshot.getChildrenCount());
                    movies.clear(); //will print list twice if data is changed in the browser. This prevents that.
                    for( DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                        Movie movie = snapshot.getValue( Movie.class );
                        movies.add( movie );
                    }
                }

                Log.d(TAG, "getMovies: " + movies.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}
