package android.projet.colorgame;

import android.projet.colorgame.utils.PreferencesUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {

    private ImageButton playButton;
    private ImageButton continueButton;

    private Context context;
    private String savedGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = this;
        //récupérer les éléments graphiques
        playButton = (ImageButton)findViewById(R.id.fab);
        playButton.setOnClickListener(this);

        continueButton = (ImageButton)findViewById(R.id.reprise);
        continueButton.setOnClickListener(this);


    }
    @Override
    protected void onResume() {
        super.onResume();
        //récupérer la partie enregistrée s'il y en a
        PreferencesUtils prefs= new PreferencesUtils(context);
        savedGame=prefs.getString(prefs.KEY_STATE, "");

        //griser le bouton continuer s'il n'y a pas de partie sauvegardée
        if(savedGame==null||savedGame.isEmpty()){
            continueButton.setEnabled(false);
        }
        else
        {
            continueButton.setEnabled(true);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //bouton continuer cliqué
            case R.id.reprise:
                //lancer le jeu en envoyant les parmètres de la partie sauvegardée à l'activité du jeu
                Intent intentSaved = new Intent(context, GameActivity.class);
                Bundle objetbunble = new Bundle();
                objetbunble .putString("passInfo",savedGame);
                intentSaved.putExtras(objetbunble );
                startActivity(intentSaved);
                break;
            //bouton jouer cliqué
            case R.id.fab:
                Intent intent = new Intent(context, GameActivity.class);
                startActivity(intent);
                break;

        }
    }

}
