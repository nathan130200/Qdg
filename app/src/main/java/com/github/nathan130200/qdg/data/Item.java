package com.github.nathan130200.qdg.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Item {
    private Integer id;
    private Long timestamp;
    private Double valor;
    
    public Item(){
    
    }
    
    public Item(Date date, Double valor){
        setData(date);
        setValor(valor);
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setData(Date data){
        this.timestamp = data.getTime();
    }
    
    public void setValor(Double valor){
        this.valor = valor;
    }
    
    public Double getValor() {
        return valor;
    }
    
    public Long getData(){
        return this.timestamp;
    }
    
    public String formatData(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
    
        return new SimpleDateFormat("dd/MM/yyy")
                .format(cal.getTime());
    }
}
