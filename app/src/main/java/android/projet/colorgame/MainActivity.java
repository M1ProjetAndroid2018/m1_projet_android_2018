package android.projet.colorgame;

import java.util.List;

import android.projet.colorgame.utils.PreferencesUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {

    private ImageButton playButton;
    private ImageButton continueButton;
    private ImageButton scoresButton;

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

        scoresButton = (ImageButton)findViewById(R.id.bestscore);
        scoresButton.setOnClickListener(this);


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

            //bouton meilleurs scores cliqué
            case R.id.bestscore:


                //récupérer les meilleurs scores et les noms correspondants
                PreferencesUtils prefs= new PreferencesUtils(context);
                String prefsBestScores= prefs.getString(prefs.KEY_BEST_SCORES, "");
                if(prefsBestScores!=null){
                    List <Integer> meilleursScores=prefs.jsonDeserializeBestScoresValues(prefsBestScores);
                    List <String> nomMeilleursScores=prefs.jsonDeserializeBestScoresNames(prefsBestScores);
                    String msg="";
                    if(meilleursScores.size()>0){
                        for (int i = 0; i < meilleursScores.size(); i++) {
                            String espace="";
                            int score=meilleursScores.get(i);
                            if(score<1000)
                                if(score>=100)
                                    espace="\t";

                                else
                                    espace="\t";

                            msg=msg+""+score+""+espace+"\t\t\t\t\t\t\t\t\t\t"+nomMeilleursScores.get(i)+"\n";
                        }
                    }
                    else
                        msg=String.format(getResources().getString(R.string.no_best_scores_msg));

                    //afficher les meilleurs scores dans une pop up
                    final AlertDialog alertScores = new AlertDialog.Builder(this).create();

                    alertScores.setTitle(String.format(getResources().getString(R.string.best_scores_title)));
                    alertScores.setMessage(msg);

                    alertScores.setCancelable(false);
                    alertScores.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok_button),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alertScores.dismiss();
                                }
                            });
                    alertScores.show();
                }
                break;
        }
    }

}
