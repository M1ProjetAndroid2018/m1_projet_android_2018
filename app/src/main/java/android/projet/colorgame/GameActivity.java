package android.projet.colorgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.projet.colorgame.utils.PreferencesUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

public class GameActivity extends Activity implements OnItemClickListener {

    //constantes du jeu
    private final static int NB_EMPTY_CELLS = 20;

    private final static int PTS_TWO_MATCHS = 40;
    private final static int PTS_THREE_MATCHS = 60;
    private final static int PTS_FOUR_MATCHS = 120;

    private final static int TEMPS_PARTIE = 70000;
    private final static int TEMPS_MOINS= 3000;


    //variables du jeu
    private int[] data;
    private int score = 0;

    //elements graphiques de l'activite
    private GridView grid;
    private TextSwitcher scoreText;
    private ProgressBar progressTime;

    private Activity context;
    private boolean isTimeEnd;
    private boolean isSavedGame;

    private boolean isCreateDone;
    private boolean homeButtonPressed;

    //variables de gestion du progressbar
    private CountDownTimer countDownTimer;
    private long millisUntilFinished;

    private long totalTimeMillis;
    private int periodTimeMillis;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        //initialisation des elements graphiques
        context = this;
        grid = findViewById(R.id.gille_jeu);
        scoreText = findViewById(R.id.score);
        progressTime= findViewById(R.id.progressTime);

        //gestion de la partie sauvegardée
        Bundle objetbunble  = this.getIntent().getExtras();
        String InfoPasse=null;
        isSavedGame=false;

        //vérifier si c'est le bouton "continuer" qui a été cliqué
        if(objetbunble!=null){
            // récupération de la partie sauvegardé
            InfoPasse= objetbunble .getString("passInfo");
            isSavedGame=true;
        }

        //initialisation de l'activité
        initialize(InfoPasse);
    }

    @Override
    protected void onPause() {

        if(homeButtonPressed){
            isCreateDone = false;
            countDownTimer.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isCreateDone && homeButtonPressed){
            //redémarrer le timer si ce n'est pas une nouvelle partie en l'initialisant avec le temps restant
            initCountDownTimer(millisUntilFinished);
            countDownTimer.start();


        }
    }

    @SuppressLint("UseSparseArrays")
    private void initialize(String savedGame) {

        isCreateDone = true;
        homeButtonPressed = true;

        //cas d'une nouvelle partie
        if(savedGame==null){
            score = 0;
            //initialisation aléatoire de la grille
            data = new int[140];
            Random random = new Random();

            //generer aleatoirement les indexes des 20 cases vides
            int[] blankCells = getBlankCells();

            for (int i = 0; i < 140; i++) {
                // si i fait partie des indexes des cases vides, alors mettre une case vide dans la grille
                if (contains(blankCells, i))
                    data[i] = 7;//7 pour les cases vides

                    // sinon generer une couleur aleatoirement
                else
                    data[i] = random.nextInt(7);
            }


        }
        //cas d'une partie sauvegardée
        else{
            //chargement de la partie sauvegardé: score, grille et le temps restant
            PreferencesUtils prefs=new PreferencesUtils(context);
            score=prefs.jsonDeserializeScoreOfState(savedGame);

            data= prefs.jsonDeserializePositionsOfState(savedGame);
            millisUntilFinished=prefs.jsonDeserializeTimeOfState(savedGame);

        }

        //définition de la taille des cases selon taille de l'écran
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);

        int statusBarHeight = (int) Math.ceil(25 * context.getResources().getDisplayMetrics().density);

        final int heighProgress=(int) Math.round(outSize.y - ((outSize.x / 10+2) * 14)- statusBarHeight) ;
        scoreText.getLayoutParams().height =heighProgress;
        progressTime.getLayoutParams().height =heighProgress;


        scoreText.setFactory(new ViewFactory() {

            public View makeView() {

                TextView myText = new TextView(context);
                myText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                myText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                myText.setTextSize(1, 15);
                myText.setTextColor(Color.parseColor("#3F51B5"));
                myText.setTypeface(null, android.graphics.Typeface.BOLD);
                return myText;
            }
        });

        //animations d'entree et de sortie  du textView du score
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);

        // mettre les animations au textSwitcher
        scoreText.setInAnimation(in);
        scoreText.setOutAnimation(out);
        scoreText.setText(String.valueOf(score));

        //gestion du gridView
        ColorAdapter adapter = new ColorAdapter(context, data, outSize.x);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(this);

        totalTimeMillis = TEMPS_PARTIE;//temps du jeu
        periodTimeMillis = 1;

        isTimeEnd = false;
        //initialiser le temps du timer
        if(millisUntilFinished>0)
            initCountDownTimer(millisUntilFinished);
        else
            initCountDownTimer(totalTimeMillis);

        countDownTimer.start();
    }

    @Override
    public void onBackPressed() {

        //arréter le timer
        countDownTimer.cancel();

        //afficher la boite de dialogue avec trois boutons sauvegarder, annuler et quitter
        final AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle(R.string.app_name);
        alert.setMessage(String.format(getResources().getString(R.string.on_back_message)));

        alert.setCancelable(false);

        // le bouton sauvegarder la partie
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.save_button),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alert.dismiss();
                        context.finish();
                        homeButtonPressed = false;
                        //sauvegarder les paramétres de la partie
                        PreferencesUtils preferences=new PreferencesUtils(context);
                        String jsonEtat= preferences.jsonSerializeState(score, millisUntilFinished, data);
                        preferences.setString(preferences.KEY_STATE, jsonEtat);
                    }
                });

        //bouton annuler, revenir é la partie
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.continue_button),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alert.dismiss();
                        initCountDownTimer(millisUntilFinished);
                        countDownTimer.start();
                    }
                });

        //bouton quitter, quitter sans sauvegarder
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.quit_button),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alert.dismiss();
                        context.finish();
                        homeButtonPressed = false;
                    }
                });
        alert.show();
    }

    private void initCountDownTimer(long totalTimeMillis){
        countDownTimer = new CountDownTimer(totalTimeMillis, periodTimeMillis) {
            //gérer la progression du timer
            @Override
            public void onTick(long _millisUntilFinished)
            {
                millisUntilFinished = _millisUntilFinished;
                progressTime.setProgress((int)_millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isTimeEnd = true;
                if(!context.isFinishing())
                    showAlert();
            }
        };
    }

    private int[] getBlankCells() {
        //generer aleatoirement 20 indexes des cases vides
        int[] blankCells = new int[NB_EMPTY_CELLS];
        Random r = new Random();
        for (int i = 0; i < NB_EMPTY_CELLS; i++) {
            int value = r.nextInt(140);
            while (contains(blankCells, value)) {
                value = r.nextInt(140);
            }
            blankCells[i] = value;

        }
        return blankCells;
    }

    private boolean contains(int[] tab, int key) {
        boolean contain = false;
        int i = 0;
        while (!contain && i < tab.length) {
            if (key == tab[i])
                contain = true;
            i++;
        }

        return contain;

    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
        //si la case touchee est vide
        if (data[position] == 7 && !isTimeEnd) {

            // recuperer les positions des cases non vides du haut, du bas , de la droite et de la gauche
            List<List<Integer>> mld=listPositionsDropables(position);
            // e partir de cette liste recuperer la liste des couleurs compatibles
            List<List<Integer>> lstPositionsWin=posWithSameColors(mld);


            //calculer le score obtenu
            switch (lstPositionsWin.size()) {
                case 2:
                    score += PTS_TWO_MATCHS;
                    break;
                case 3:
                    score += PTS_THREE_MATCHS;
                    break;
                case 4:
                    score += PTS_FOUR_MATCHS;
                    break;
            }


            if(lstPositionsWin.size()>0){
                scoreText.setText(String.valueOf(score));

            }
            else{

                countDownTimer.cancel();
                //retirer du temps s'il n'y é pas de correspendance entres les couleurs des cases
                long tempsRestant=millisUntilFinished-TEMPS_MOINS;
                if(tempsRestant<0)
                    tempsRestant=0;

                initCountDownTimer(tempsRestant);
                countDownTimer.start();
            }

            (new GridAnimationListener(this, adapter.getChildAt(position))).startAnimationWay(position);
            //enlever les cellules ayant des les memes couleurs avec une animation
            for (int i = 0; i < lstPositionsWin.size(); i++) {

                List<Integer> listColorOk=lstPositionsWin.get(i);
                for(int j=0; j< listColorOk.size();j++){

                    View viewToAinmate = adapter.getChildAt(listColorOk.get(j));
                    if (viewToAinmate != null){
                        if(j==listColorOk.size()-1){
                            data[listColorOk.get(j)] = 7;
                            (new GridAnimationListener(this, viewToAinmate)).startAnimation();

                        }
                        else
                            (new GridAnimationListener(this, viewToAinmate)).startAnimationWay(listColorOk.get(j));
                    }
                }

            }
            boolean termine=completedGame();
            //verifier si le jeu est termine
            if (termine){
                showAlert();
                countDownTimer.cancel();
            }

        }

    }

    private void showAlert(){

        final PreferencesUtils prefs= new PreferencesUtils(context);

        if(isSavedGame)
            prefs.setString(prefs.KEY_STATE, "");


    }

    private int[] positionsDropables(int position) {

        //trouver les positions des cases non vides e gauche, e droite, en haut et en bas de la case selectionnee
        int[] positionsReturn = new int[4];

        int i, j;

        j = position % 10;
        i = (position - j) / 10;

        int indexHaut = i - 1, indexBas = i + 1, indexDroit = j + 1, indexGauche = j - 1;
        int haut = -1, bas = -1, droit = -1, gauche = -1;
        boolean trouveHaut = false, trouveBas = false, trouveDroit = false, trouveGauche = false;
        // mPosition=10*i+j;

        while (!trouveHaut || !trouveBas || !trouveDroit || !trouveGauche) {

            // trouver la position non vide du haut
            if (!trouveHaut) {

                if (indexHaut >= 0) {
                    if (data[10 * indexHaut + j] != 7) {
                        haut = 10 * indexHaut + j;
                        trouveHaut = true;
                    } else
                        indexHaut--;
                } else
                    trouveHaut = true;

            }
            // trouver la position non vide du bas
            if (!trouveBas) {

                if (indexBas < 14) {
                    if (data[10 * indexBas + j] != 7) {
                        bas = 10 * indexBas + j;
                        trouveBas = true;
                    } else
                        indexBas++;

                }

                else
                    trouveBas = true;
            }

            // trouver la position non vide de la droite
            if (!trouveDroit) {
                if (indexDroit < 10) {
                    if (data[10 * i + indexDroit] != 7) {
                        droit = 10 * i + indexDroit;
                        trouveDroit = true;
                    } else
                        indexDroit++;
                } else
                    trouveDroit = true;
            }

            // trouver la position non vide de la gauche
            if (!trouveGauche) {
                if (indexGauche >= 0) {
                    if (data[10 * i + indexGauche] != 7) {
                        gauche = 10 * i + indexGauche;
                        trouveGauche = true;
                    } else
                        indexGauche--;
                } else
                    trouveGauche = true;
            }

        }
        positionsReturn[0] = haut;
        positionsReturn[1] = bas;
        positionsReturn[2] = gauche;
        positionsReturn[3] = droit;

        return positionsReturn;
    }

    private boolean completedGame() {
        boolean colorMatch = false;
        int i = 0;
        //pour chaque case vide verifier s'il y a des couleurs correspendantes
        //arreter s'il y a une correspendance
        while (i < data.length && !colorMatch) {
            if (data[i] == 7) {

                int[] tab = positionsDropables(i);

                colorMatch= oneColorMatch(tab);

            }
            i++;
        }
        return !colorMatch;
    }

    private boolean oneColorMatch(int[] positionsDrops) {

        List<Integer> lst = new ArrayList<Integer>();

        // eliminer les -1
        for (int j = 0; j < positionsDrops.length; j++) {
            if (positionsDrops[j] != -1)
                lst.add(positionsDrops[j]);
        }

        boolean oneMatch = false;
        int pos = 0;

        while (pos < lst.size() - 1 && !oneMatch) {

            int i = pos + 1;

            while (i < lst.size() && !oneMatch) {

                if (data[lst.get(pos)] == data[lst.get(i)]) {
                    oneMatch = true;
                } else
                    i++;
            }

            pos++;
        }

        return oneMatch;
    }

    private List<List<Integer>>  listPositionsDropables(int position) {

        int i, j;

        j = position % 10;
        i = (position - j) / 10;

        int indexHaut = i - 1, indexBas = i + 1, indexDroit = j + 1, indexGauche = j - 1;
        int haut = -1, bas = -1, droit = -1, gauche = -1;
        List<Integer> listeHaut=new ArrayList<Integer>();
        List<Integer> listeBas=new ArrayList<Integer>();
        List<Integer> listDroit=new ArrayList<Integer>();
        List<Integer> listGauche=new ArrayList<Integer>();
        boolean trouveHaut = false, trouveBas = false, trouveDroit = false, trouveGauche = false;
        // mPosition=10*i+j;

        while (!trouveHaut || !trouveBas || !trouveDroit || !trouveGauche) {

            // trouver la position non vide du haut
            if (!trouveHaut) {

                if (indexHaut >= 0) {

                    listeHaut.add(10 * indexHaut + j);

                    if (data[10 * indexHaut + j] != 7) {
                        haut = 10 * indexHaut + j;
                        trouveHaut = true;
                    } else
                        indexHaut--;

                } else
                    trouveHaut = true;

            }
            // trouver la position non vide du bas
            if (!trouveBas) {

                if (indexBas < 14) {

                    listeBas.add(10 * indexBas + j);
                    if (data[10 * indexBas + j] != 7) {
                        bas = 10 * indexBas + j;
                        trouveBas = true;
                    } else
                        indexBas++;

                }

                else
                    trouveBas = true;
            }

            // trouver la position non vide de la droite
            if (!trouveDroit) {
                if (indexDroit < 10) {

                    listDroit.add(10 * i + indexDroit);
                    if (data[10 * i + indexDroit] != 7) {
                        droit = 10 * i + indexDroit;
                        trouveDroit = true;
                    } else
                        indexDroit++;
                } else
                    trouveDroit = true;
            }

            // trouver la position non vide de la gauche
            if (!trouveGauche) {
                if (indexGauche >= 0) {

                    listGauche.add(10 * i + indexGauche);
                    if (data[10 * i + indexGauche] != 7) {
                        gauche = 10 * i + indexGauche;
                        trouveGauche = true;
                    } else
                        indexGauche--;
                } else
                    trouveGauche = true;
            }

        }

        List<List<Integer>> lst=new ArrayList<List<Integer>>();
        if(!listeHaut.isEmpty() && haut>-1)
            lst.add(listeHaut);
        if(!listeBas.isEmpty() && bas>-1)
            lst.add(listeBas);
        if(!listDroit.isEmpty() && droit>-1)
            lst.add(listDroit);
        if(!listGauche.isEmpty()&& gauche>-1)
            lst.add(listGauche);
        return lst;
    }

    private List<List<Integer>> posWithSameColors(List<List<Integer>> positions) {

        //trouver les positions avec des couleurs semblables
        List<List<Integer>> positionsWin = new ArrayList<List<Integer>>();

        int pos = 0;

        while (pos < positions.size() - 1) {
            boolean uneCorrespendance = false;
            for (int i = pos + 1; i < positions.size(); i++) {

                if (data[positions.get(pos).get(positions.get(pos).size()-1)] == data[positions.get(i).get(positions.get(i).size()-1)]) {
                    positionsWin.add(positions.remove(i));
                    i--;
                    uneCorrespendance = true;

                }
            }
            if (uneCorrespendance) {
                positionsWin.add(positions.remove(pos));
            } else
                pos++;
        }

        return positionsWin;
    }


}