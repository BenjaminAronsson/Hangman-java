package com.example.dev.hangman;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameActivity extends AppCompatActivity {

    private String[] wordArray;
    private EditText inputField;
    private TextView guesses;
    private TextView guessesMade;
    private ImageView hangmanView;
    private SharedPreferences sharedPreferences;
    private List<Drawable> images= new ArrayList<>();
    private final int BILD = 9;
    private Hangman hangman = new Hangman();
    private final String PATH_TO_RESOURCES = "https://benjaminaronsson.github.io/Hangman/";
    private String theme = "Standard";
    private String pic = "hang0";
    private String hangmanPicturePath;
    // This is the game object








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        //wordArray = getResources().getStringArray(R.array.wordList);
        hangman = new Hangman(getResources());


        //load game
        //hangman = hangman.loadGame();

        //test if activity is reactivated from on start
        if( savedInstanceState != null)
        {
            //Restart of activity after configuration change
        }

        //LoadPreferences();

        setContentView(R.layout.activity_game_activitty);
        inputField = findViewById(R.id.guessText);
        guesses = findViewById(R.id.hiddenWord);
        guessesMade = findViewById(R.id.guessedLetters);
        hangmanView = findViewById(R.id.hangmanView);
        hangmanView.setTag(BILD);
        sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);


        RequestCreator drawable = Picasso.get().load(hangmanPicturePath);

        //TODO add word list from web
        loadResources();
        layoutUpdate();

    }



    private void loadResources() {
        //Preload images TODO add to web
        images.add(getResources().getDrawable(R.drawable.hang0, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang1, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang2, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang3, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang4, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang5, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang6, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang7, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang8, getTheme()));
        images.add(getResources().getDrawable(R.drawable.hang9, getTheme()));



    }

    @Override
    protected void onPause() {
        super.onPause();
        //SavePreferences();
        hangman.saveGame();

    }

    private void LoadPreferences() {



        //save data
        SharedPreferences prefs = getSharedPreferences("default", MODE_PRIVATE);
        String word = prefs.getString("chosen word", "Hello");//"No name defined" is the default value.
        int guessesLeft = prefs.getInt("Guesses left", 0); //0 is the default value.
        Set<String> temp = prefs.getStringSet("guesses made",  new HashSet<String>());//TODO null

        //Set<String> userAllSet = temp;
        ArrayList<String> guessedLetter = new ArrayList<String>(temp);

            hangman.setGuessesLeft(guessesLeft);
            hangman.setWord(word);
            hangman.setGuessedLetters(guessedLetter);

    }

    private void SavePreferences() {
        // MY_PREFS_NAME - a static String variable like:
        //public static final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences.Editor editor = getSharedPreferences("default", MODE_PRIVATE).edit();
        editor.putString("Chosen word", hangman.getChoosenWord());
        editor.putInt("Guesses left", hangman.getGuessesLeft());

        //Set the values
        Set<String> guessedLetters = new HashSet<String>();
        guessedLetters.addAll(hangman.getGuessedLetters());
        editor.putStringSet("Guesses made", guessedLetters);
        editor.commit();
    }

    public void guessButtonPressed(View view) {

        if(hangman.isGameContinuing()) {

            //gets input from textfield
            String input = getInput(inputField);

            //tests if input is valid
            inputCorrect(input); //TODO check

            //guesses
            hangman.guess(input);

            //draws layout
            layoutUpdate();

            //test if player won or lost
            if (hangman.hasWon()) {
                gameWon();
            } else if (hangman.hasLost()) {
                gameLost();
                guesses.setText(hangman.getChoosenWord());
            }
        }
        inputField.setText("");
    }

    private void gameWon() {
        //TODO
        startNewGame();
    }

    private void gameLost() {
        //TODO
        startNewGame();
    }

    private void layoutUpdate() {

        /* TODO add theme
        String pictureNumber = Integer.toString(hangman.getGuessesLeft());
        hangmanPicturePath = PATH_TO_RESOURCES + theme +"/" +"hang" + pictureNumber;
*/
        //update hangman pic
        if (hangman.getGuessesLeft() < 10 && BILD > 0) {
            hangmanView.setImageDrawable(images.get(hangman.getGuessesLeft()));
            hangmanView.setTag(hangman.getGuessesLeft());
        }

        //updates text field
        guesses.setText(hangman.getHiddenWord());
        guessesMade.setText(hangman.getBadLettersUsed());
        inputField.setText("");
    }

    private String getInput(EditText inputField) {
        //takes guess from input

     String input = inputField.getText().toString();
    return input;

    }

    private boolean inputCorrect(String input) {
        //test for more then one character
        if (input.length() > 1) {
            toastWrongInput();
            return false;
        }
        //test for no input
        else if (input.length() <= 0) {
            toastNoInput();
            return false;
        }
        //test for letter all ready used
        else if (hangman.hasUsedLetter(input.toLowerCase())) {
            toastDuplicateGuess();
            return false;
        }
        //test for special character
        else if(!Character.isAlphabetic(input.charAt(0))) {
            toastNoLetter();
            return false;
        }
        else {
            return true;
        }
    }

    private void toastNoInput() {
        Toast myToast = Toast.makeText(this, "You must enter a letter!", Toast.LENGTH_LONG);
        myToast.show();
    }

    private void toastNoLetter() {
        Toast myToast = Toast.makeText(this, "You can only use letters!", Toast.LENGTH_LONG);
        myToast.show();
    }

    private void toastWrongInput() {
        Toast myToast = Toast.makeText(this, "You can only guess on one letter at a time!", Toast.LENGTH_LONG);
        myToast.show();
    }

    private void toastDuplicateGuess() {
        Toast myToast = Toast.makeText(this, "You can only guess on the same letter once!", Toast.LENGTH_LONG);
        myToast.show();
    }

    public void startNewGame() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_media_play)
                .setTitle("New game?")
                .setMessage("Do you want to start a new game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hangman.newGame();
                        layoutUpdate();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
