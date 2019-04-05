package com.github.nathan130200.qdg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.nathan130200.qdg.data.Item;
import com.github.nathan130200.qdg.data.SQLiteHelper;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteHelper helper;
    public Button btnCadastrar;
    public Button btnAtualizar;
    public TableLayout tvItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        helper = new SQLiteHelper(this);
        
        tvItems = findViewById(R.id.tvItems);
        
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnAtualizar = findViewById(R.id.btnAtualizar);
    
        btnCadastrar.setOnClickListener(this);
        btnAtualizar.setOnClickListener(this);
        
        btnAtualizar.performClick();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == 1200)
            this.btnAtualizar.performClick();
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnCadastrar){
            Intent it = new Intent(this, CadastrarActivity.class);
            startActivity(it);
        }
        else if (v.getId() == R.id.btnAtualizar){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Controle de Gás")
                    .setCancelable(false)
                    .setMessage("Atualizando lista...")
                    .create();
            
            dialog.show();
    
            new LoadAllItemsTask(this, (items) -> {
                while (tvItems.getChildCount() > 2)
                    tvItems.removeViewAt(tvItems.getChildCount() - 1);
                
                int idx = 1;
                
                for(Item item : items){
                    TableRow row = new TableRow(this);
                    {
                        TextView tvId = new TextView(this);
                        tvId.setText(Integer.toString(idx++));
                        row.addView(tvId);
                        
                        TextView tvData = new TextView(this);
                        tvData.setText(item.formatData());
                        row.addView(tvData);
                        
                        TextView tvValor = new TextView(this);
                        tvValor.setText(Util.getCurrencyAsString(item.getValor()));
                        row.addView(tvValor);
                        
                        Button btnRemover = new Button(this);
                        btnRemover.setText("X");
                        btnRemover.setOnClickListener((btn) -> {
                            new RemoverTask(item.getId(), this, () -> {
                                this.btnAtualizar.performClick();
                            }).execute();
                        });
                        
                        row.addView(btnRemover);
                    }
                    tvItems.addView(row);
                }
                
                dialog.cancel();
            }).execute();
        }
    }
    
    class RemoverTask extends AsyncTask<Void, Void, Void> {
        private int id;
        private AlertDialog dialog;
        private Activity activity;
        private Runnable cb;
        
        public RemoverTask(int id, Activity activity, Runnable cb){
            this.cb = cb;
            this.activity = activity;
            this.id = id;
            
            this.dialog = new AlertDialog.Builder(this.activity)
                    .setCancelable(false)
                    .setTitle("Controle de Gás")
                    .setMessage("Removendo item...")
                    .create();
        }
    
        @Override
        protected void onPreExecute() {
            this.dialog.show();
        }
    
        @Override
        protected Void doInBackground(Void... v) {
            SQLiteHelper dbhelper = ((MainActivity) this.activity).helper;
            dbhelper.remover(this.id);
            return null;
        }
    
        @Override
        protected void onPostExecute(Void aVoid) {
            this.dialog.cancel();
            this.cb.run();
        }
    }
    
    class LoadAllItemsTask extends AsyncTask<Void, Void, List<Item>> {
        private MainActivity activity;
        private AlertDialog dialog;
        private Callback<List<Item>> cb;
        
        public LoadAllItemsTask(MainActivity activity, Callback<List<Item>> cb){
            this.activity = activity;
            this.dialog = dialog;
            this.cb = cb;
        }
        
        @Override
        protected List<Item> doInBackground(Void... empty)  {
            try {
                Thread.sleep(1500);
                return activity.helper.getAll();
            }
            catch (Exception ex){
                
                AlertDialog dlg = new AlertDialog.Builder(this.activity)
                        .setCancelable(true)
                        .setTitle("Controle de Gás")
                        .setMessage("Um erro ocorreu!\n" + ex.getMessage())
                        .create();
                
                dlg.show();
                
                return new ArrayList<>();
            }
        }
    
        @Override
        protected void onPostExecute(List<Item> items) {
            cb.run(items);
        }
    }
    
    interface Callback<T> {
        void run(T param);
    }
}
