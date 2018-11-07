package android.projet.colorgame;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Window;
import android.widget.GridView;

public class GameActivity extends Activity {

    //constantes du jeu
    private final static int NB_EMPTY_CELLS = 20;

    //variables du jeu
    private int[] data;

    //elements graphiques de l'activite
    private GridView grid;
    private Activity context;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        //initialisation des elements graphiques
        context = this;
        grid = findViewById(R.id.gille_jeu);

        //initialisation de l'activite
        initialize();
    }

    @SuppressLint("UseSparseArrays")
    private void initialize() {

            //initialisation aleatoire de la grille
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


        //definition de la taille des cases selon taille de l'ecran
        Point outSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outSize);

        //gestion du gridView
        ColorAdapter adapter = new ColorAdapter(context, data, outSize.x);
        grid.setAdapter(adapter);


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

}