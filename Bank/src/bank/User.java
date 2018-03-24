/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import java.sql.ResultSet;

/**
 *
 * @author wolly1477
 */
public class User {
    private String Name;
    private String LastName;
    private double Saldo;
    private boolean Geslacht;
    private String card;
    
    public void setName(String name){
        this.Name = name;
    }
    
    public String getName(){
        return this.Name;
    }
    
     public void setLastName(String lastname){
        this.LastName = lastname;
    }
    
    public String getLastName(){
        return this.LastName;
    }
    
     public void setSaldo(double saldo){
        this.Saldo = saldo;           
    }
    
    public double getSaldo(){
        return this.Saldo;
    }
    
     public void setGeslacht(boolean geslacht){
        this.Geslacht = geslacht;
    }
    
    public boolean getGeslacht(){
        return this.Geslacht;
    }
    
    public void updateSaldoDatabase(double saldo){
        try{
            String query = "UPDATE rekening SET Saldo='"+saldo+"'WHERE Pas_PasID ='"+this.card+"'";
            if(Bank.statement.executeUpdate(query)==1)
            {
                setSaldo(saldo);
            }else{
                System.out.println("jammerjho");
            }
            
        }catch(Exception e){
            System.out.println("Geen geld update: "+e);
        }
    }
    
    public User(String Name, String LastName, double Saldo, boolean Geslacht, String Card){
        this.Name = Name;
        this.LastName = LastName;
        this.Saldo = Saldo;
        this.Geslacht = Geslacht;
        this.card = Card;
    }
}
