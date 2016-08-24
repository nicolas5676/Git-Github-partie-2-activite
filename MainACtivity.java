Ceci est un fichier Java.

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolasdumas.fiestabayona.adapter.ListBayonneAdapter;
import com.example.nicolasdumas.fiestabayona.database.DatabaseHelper;
import com.example.nicolasdumas.fiestabayona.model.Bayonne;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.lang.String;

/**
 * Created by nicolasdumas on 16/05/2016.
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView lvbayonne;
    private ListBayonneAdapter adapter;
    private List<Bayonne> mBayonneList;
    private DatabaseHelper mDBHelper;
    private SearchView searchViewAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        /* Afficher icones à l'ouverture et fermeture de la liste slide*/

        lvbayonne = (ListView) findViewById(R.id.listview_bayonne);
        lvbayonne.setTextFilterEnabled(true);

        searchViewAction = (SearchView) findViewById(R.id.menu_search);


        mDBHelper = new DatabaseHelper(this);




    /* Vérifie que la DB existe*/
        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
       if (false == database.exists())

        {
           mDBHelper.getReadableDatabase();
            /* copie DB*/
            if (copyDatabase(this)) {
                Toast.makeText(this, "Copie de la DB reussie", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Copie de la DB echoue", Toast.LENGTH_SHORT).show();
                return;
                }

        }

        mBayonneList = mDBHelper.getListBayonne();

        adapter = new ListBayonneAdapter(this, mBayonneList);

        lvbayonne.setAdapter(adapter);

        lvbayonne.setOnItemClickListener(new ItemList());

        lvbayonne.setTextFilterEnabled(true);

        /* Affichage de la liste sous forme alphabétique*/
        Collections.sort(mBayonneList, new Comparator<Bayonne>() {
            @Override
            public int compare(Bayonne u1, Bayonne u2) {
                return u1.getTitre().compareToIgnoreCase(u2.getTitre());
            }
        });



    }

/* Méthode qui indique l'action de cliquer sur un élément de la liste et de renvoyer un certain affichage*/

    class ItemList implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewGroup vg = (ViewGroup) view;
            TextView tvparoles = (TextView) vg.findViewById(R.id.tv_bayonne_paroles);
            tvparoles.setText(mBayonneList.get(position).getParoles());
        }
    }




/* Méthode permettant l'affichage du menu*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        adapter.setFilter(mBayonneList);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });

        return super.onCreateOptionsMenu(menu);

    }


/* Méthode appelée quand le texte de la requete est modifié par l'utilisateur*/
        @Override
        public boolean onQueryTextChange(String newText) {
            final List<Bayonne> filteredBayonneList = filter(mBayonneList, newText);
            adapter.setFilter(filteredBayonneList);
            if (TextUtils.isEmpty(newText)){

                lvbayonne.clearTextFilter();
            }else {
                lvbayonne.setFilterText(newText.toString());
            }

            return true;
        }

/* Méthode appelée quand l'utilisateur soumet sa requete et filtrage des données*/
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        private List<Bayonne> filter(List<Bayonne> bayonne, String query) {
            query = query.toLowerCase();

            final List<Bayonne> filteredBayonneList = new ArrayList<>();
            for (Bayonne titre : bayonne) {
                final String text = titre.getTitre().toLowerCase();
                if (text.contains(query)) {
                    filteredBayonneList.add(titre);
                }
            }
            return filteredBayonneList;
       }



/* Méthode réagissant quand l'utilisateur clique sur un item du menu*/
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_2:

                Intent intent = new Intent(this, BarActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Voici la liste des Bars et Penas!", Toast.LENGTH_LONG).show();

                return true;


            case R.id.menu_search:
                //open Search();
                return true;

            case R.id.menu_localisation:

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private boolean copyDatabase(Context context) {

        try {

            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);

            }
            outputStream.flush();
            outputStream.close();
            Log.v("MainActivity", "DB copie");
            return true;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

}







































