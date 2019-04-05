package com.github.nathan130200.qdg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.nathan130200.qdg.data.Item;
import com.github.nathan130200.qdg.data.SQLiteHelper;

import java.util.Calendar;

import faranjit.currency.edittext.CurrencyEditText;

public class CadastrarActivity extends AppCompatActivity implements View.OnClickListener {
    
    DatePicker dpData;
    CurrencyEditText tbValor;
    Button btnCadastrarItem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        
        dpData = findViewById(R.id.dpData);
        tbValor = findViewById(R.id.tbValor);
        btnCadastrarItem = findViewById(R.id.btnCadastrarItem);
        btnCadastrarItem.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnCadastrarItem){
            try {
                Item item = new Item();
                item.setTimestamp(Calendar.getInstance().getTimeInMillis());
                item.setValor(tbValor.getCurrencyDouble());
    
                new InserirTask(this)
                        .execute(item);
            }
            catch (Exception ex){
                AlertDialog dlg = new AlertDialog.Builder(this)
                        .setTitle("Controle do Gás")
                        .setMessage("Um erro ocorreu!\n" + ex.getLocalizedMessage())
                        .setCancelable(true)
                        .create();
                
                return;
            }
        }
    }
    
    class InserirTask extends AsyncTask<Item, Void, Long> implements DialogInterface.OnClickListener {
        AlertDialog dialog;
        CadastrarActivity activity;
        
        public InserirTask(CadastrarActivity activity){
            this.activity = activity;
            
            this.dialog = new AlertDialog.Builder(this.activity)
                    .setTitle("Controle de Gás")
                    .setMessage("Cadastrando...")
                    .setCancelable(false)
                    .create();
            
            this.dialog.show();
        }
        
        @Override
        protected Long doInBackground(Item... items) {
            SQLiteHelper helper = new SQLiteHelper(this.activity);
            Long ret = 0L;
            
            try {
                for (Item item : items) {
                    ret += helper.cadastrar(item) ? 1 : 0;
                }
            }
            catch (Exception ex){
                AlertDialog dlg = new AlertDialog.Builder(this.activity)
                        .setTitle("Controle do Gás")
                        .setMessage("Um erro ocorreu!\n" + ex.getLocalizedMessage())
                        .setCancelable(true)
                        .create();
            }
            
            return ret;
        }
    
        @Override
        protected void onPostExecute(Long count) {
            this.dialog.cancel();
            
            if(count > 0){
                new AlertDialog.Builder(this.activity)
                        .setTitle("Controle do Gás")
                        .setMessage("Cadastrado com sucesso!")
                        .setNegativeButton("OK", this)
                        .create()
                        .show();
            }
        }
    
        @Override
        public void onClick(DialogInterface dialog, int which) {
            this.activity.finish();
        }
    }
}
