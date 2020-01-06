/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asao;

import com.sun.org.apache.bcel.internal.generic.SWAP;
import java.util.ArrayList;
import java.util.List;
import uci.conquerors.enums.Soldiers;
import uci.conquerors.jugador.Jugador;
import uci.conquerors.jugador.acciones.Accion;
import uci.conquerors.jugador.acciones.AccionAtacar;
import uci.conquerors.jugador.acciones.AccionSubirNivel;
import uci.conquerors.jugador.acciones.ContratarOficialConstructor;
import uci.conquerors.jugador.acciones.ContratarOficialEntrenador;
import uci.conquerors.status.StatusBuilding;
import uci.conquerors.status.StatusGame;
import uci.conquerors.status.StatusShop;
import uci.conquerors.utils.Punto2D;

/**
 *
 * @author bestard
 */
public class Bestard implements uci.conquerors.jugador.Jugador{

    public static void main(String[] args) {
        uci.conquerors.Conquerors.start();
    }
    
    @Override
    public List<Accion> Execute(StatusGame ej, StatusShop et) {
     
        List<Accion> accions = new ArrayList<>();
        
        List<StatusBuilding> edif = ej.getAllBuidings();
        
        if( et.CoachOficial() ){
            accions.add(new ContratarOficialEntrenador());
        }

       for( StatusBuilding i : edif )
            if( i.getPlayer() == ej.getPersonalID() ) {
                if( i.getLevel() < 5 && i.getPopulation() > 20 )
                    accions.add(new AccionSubirNivel(i.getPosition()));
            }   
       
       for( StatusBuilding i : edif )
           if( i.getPlayer() == ej.getPersonalID() ){
               
               if( i.getPopulation() < 10 )
                   continue;
               
               double dist = 1 << 30, def = 0, atc = 0;
               Punto2D tmp = new  Punto2D(1<<30,1<<30);
               for( StatusBuilding j : edif )
                  if( j.getPlayer() != ej.getPersonalID() && i.getAtackArmy() > j.getDefense()+5 ) 
                      if( dist > i.getPosition().DistanciaA(j.getPosition()) ){
                         dist = i.getPosition().DistanciaA(j.getPosition()); 
                         def = j.getDefense();
                         tmp = j.getPosition();
                         atc = j.getAtack();
                      }
                      else
                        if( dist == i.getPosition().DistanciaA(j.getPosition()) && def < j.getDefense() ){
                            def = j.getDefense();
                            tmp = j.getPosition(); 
                            atc = j.getAtack();
                        }
                       else
                          if( dist == i.getPosition().DistanciaA(j.getPosition()) && def == j.getDefense() && atc > j.getAtack()){
                            def = j.getDefense();
                            tmp = j.getPosition(); 
                            atc = j.getAtack();
                          } 
                      
               if( tmp.getX() != 1 << 30 && tmp.getY() != 1 << 30 )
                    accions.add(new AccionAtacar(i.getPosition(), tmp));
               
           }

        
        return accions;    
    }
    
    @Override
    public String MyName() {
        return "Bestard";
    }
    
}
