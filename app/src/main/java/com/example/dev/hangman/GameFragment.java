package com.example.dev.hangman;


import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


public class GameFragment extends Fragment{

    public GameFragment() {
        // Required empty public constructor
    }


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
    private final String theme = "standard";
    private String pic = "hang0.gif";
    private String hangmanPicturePath;
    // This is the game object


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //wordArray = getResources().getStringArray(R.array.wordList);
        hangman = new Hangman(getResources());


        //load game
        hangman = hangman.loadGame(getResources());

        //test if activity is reactivated from on start
        if( savedInstanceState != null)
        {
            //Restart of activity after configuration change
        }

        //loadPreferences();


        inputField = Objects.requireNonNull(getView()).findViewById(R.id.guessText);
        guesses = getView().findViewById(R.id.hiddenWord);
        guessesMade = getView().findViewById(R.id.guessedLetters);
        hangmanView = getView().findViewById(R.id.hangmanView);
        hangmanView.setTag(BILD);
        //sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);


        RequestCreator drawable = Picasso.get().load(hangmanPicturePath);

        //TODO add word list from web
        //loadResources();
        layoutUpdate();

        Objects.requireNonNull(getActivity()).findViewById(R.id.guessButton).setOnClickListener(
                //points to method buttonClicked
                view -> guessButtonPressed());

    }


/*
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
*/
    @Override
    public void onPause() {
        super.onPause();
        //SavePreferences();
        hangman.saveGame();

    }

    private void loadPreferences() {



        //save data
        SharedPreferences prefs = getActivity().getSharedPreferences("default", MODE_PRIVATE);
        String word = prefs.getString("chosen word", "Hello");//"No name defined" is the default value.
        int guessesLeft = prefs.getInt("Guesses left", 0); //0 is the default value.
        Set<String> temp = prefs.getStringSet("guesses made", new HashSet<>());//TODO null

        //Set<String> userAllSet = temp;
        ArrayList<String> guessedLetter = new ArrayList<>(temp);

        hangman.setGuessesLeft(guessesLeft);
        hangman.setWord(word);
        hangman.setGuessedLetters(guessedLetter);

    }

    private void savePreferences() {
        // MY_PREFS_NAME - a static String variable like:
        //public static final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("default", MODE_PRIVATE).edit();
        editor.putString("chosen word", hangman.getChoosenWord());
        editor.putInt("guesses left", hangman.getGuessesLeft());
        editor.putBoolean("is win", hangman.isWin());
        editor.commit();

    }


    private void guessButtonPressed() {

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
            if (hangman.isWin() || hangman.isLose()) {
                gameOver();
            }
        }
        inputField.setText("");
    }



    private void gameOver() {
        //TODO change fragment to end
        savePreferences();

        // Create new fragment and transaction
        Fragment gameOverFragment = StartActivity.gameOverFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.mainFrame, gameOverFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        //startNewGame();
    }

    private void layoutUpdate() {

        //TODO add theme
        String pictureNumber = Integer.toString(hangman.getGuessesLeft());
        hangmanPicturePath = PATH_TO_RESOURCES + theme +"/" +"hang" + pictureNumber;

        /*update hangman pic
        if (hangman.getGuessesLeft() < 10 && BILD > 0) {
            hangmanView.setImageDrawable(images.get(hangman.getGuessesLeft()));
            hangmanView.setTag(hangman.getGuessesLeft());
        }*/

        //updates text field
        guesses.setText(hangman.getHiddenWord());
        guessesMade.setText(hangman.getBadLettersUsed());
        inputField.setText("");

    }

    private String getInput(EditText inputField) {
        //takes guess from input

        return inputField.getText().toString();

    }

    private void inputCorrect(String input) {
        //test for more then one character
        if (input.length() > 1) {
            toastWrongInput();
        }
        //test for no input
        else if (input.length() <= 0) {
            toastNoInput();
        }
        //test for letter all ready used
        else if (hangman.hasUsedLetter(input.toLowerCase())) {
            toastDuplicateGuess();
        }
        //test for special character
        else if(!Character.isAlphabetic(input.charAt(0))) {
            toastNoLetter();
        }
    }

    private void toastNoInput() {
        Toast myToast = Toast.makeText(getActivity(), R.string.toast1, Toast.LENGTH_LONG);
        myToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 600);
        myToast.show();
    }

    private void toastNoLetter() {
        Toast myToast = Toast.makeText(getActivity(), R.string.toast2, Toast.LENGTH_LONG);
        myToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 600);
        myToast.show();
    }

    private void toastWrongInput() {
        Toast myToast = Toast.makeText(getActivity(),  R.string.toast3, Toast.LENGTH_LONG);
        myToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 600);
        myToast.show();
    }

    private void toastDuplicateGuess() {
        Toast myToast = Toast.makeText(getActivity(),  R.string.toast4, Toast.LENGTH_LONG);
        myToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 600);
        myToast.show();
    }

    public void startNewGame() {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_media_play)
                .setTitle(R.string.new_game)
                .setMessage(R.string.new_game2)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    hangman.newGame();
                    layoutUpdate();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

}
