package com.github.nathan130200.qdg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    public void onClick(View v) {
        if(v.getId() == R.id.btnCadastrar){
            Intent it = new Intent(this, CadastrarActivity.class);
            startActivity(it);
        }
        else if (v.getId() == R.id.btnAtualizar){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Atualizando...")
                    .setCancelable(false)
                    .setMessage("Atualizando lista...")
                    .create();
            
            dialog.show();
    
            new LoadAllItemsTask(this, (items) -> {
                if(tvItems.getChildCount() > 2) {
                    for (int i = 2; i < tvItems.getChildCount(); i++)
                        tvItems.removeViewAt(i);
                }
                
                if(items == null)
                    return;
                
                for(Item item : items){
                    TableRow row = new TableRow(this);
                    {
                        TextView tvId = new TextView(this);
                        tvId.setText(item.getId().toString());
                        row.addView(tvId);
                        
                        TextView tvData = new TextView(this);
                        tvData.setText(item.formatData());
                        row.addView(tvData);
                        
                        TextView tvValor = new TextView(this);
                        tvValor.setText(Util.getCurrencyAsString(item.getValor()));
                        row.addView(tvValor);
                    }
                    tvItems.addView(row);
                }
                
                dialog.cancel();
                btnAtualizar.performClick();
            }).execute();
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
                        .setTitle("Um erro ocorreu!")
                        .setMessage(ex.getMessage())
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
