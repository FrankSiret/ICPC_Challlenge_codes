/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asao;

import java.util.ArrayList;
import java.util.List;
import uci.conquerors.jugador.acciones.Accion;
import uci.conquerors.jugador.acciones.AccionAtacar;
import uci.conquerors.jugador.acciones.AccionSubirNivel;
import uci.conquerors.jugador.acciones.ContratarOficialEntrenador;
import uci.conquerors.status.StatusBuilding;
import uci.conquerors.status.StatusGame;
import uci.conquerors.status.StatusShop;

/**
 *
 * @author FrankS
 */

public class Atack implements uci.conquerors.jugador.Jugador{

    public static void main(String[] args) {
        uci.conquerors.Conquerors.start();
    }
    
    @Override
    public List<Accion> Execute(StatusGame EjStatusGame, StatusShop EtStatusShop) {
     
        List<Accion> My_Accions = new ArrayList<>();
        
        List<StatusBuilding> EdBuildings = EjStatusGame.getAllBuidings();
        
        if( EtStatusShop.CoachOficial() ){
            My_Accions.add(new ContratarOficialEntrenador());
        }

       for( StatusBuilding i : EdBuildings )
            if(i.getPlayer() == EjStatusGame.getPersonalID() && i.getPopulation() > 20){
                    My_Accions.add(new AccionSubirNivel(i.getPosition()));
            }

       boolean band = false;
       for( StatusBuilding i : EdBuildings ){
           if( i.getPlayer() == EjStatusGame.getPersonalID() && i.getPopulation() >= 10){
               double dist = Double.POSITIVE_INFINITY, def = 0, atc = 0;
               StatusBuilding EdAtack = i;
               for( StatusBuilding j : EdBuildings ){
                    if( j.getPlayer() != EjStatusGame.getPersonalID() && i.getAtackArmy() > j.getDefense()+5 ){
                        if( dist > i.getPosition().DistanciaA(j.getPosition()) ){
                            dist = i.getPosition().DistanciaA(j.getPosition()); 
                            def = j.getDefense();
                            atc = j.getAtack();
                            EdAtack = j;
                            band = true;
                        } else if( dist == i.getPosition().DistanciaA(j.getPosition()) && def < j.getDefense() ){
                            def = j.getDefense();
                            atc = j.getAtack();
                            EdAtack = j;
                            band = true;
                        } else if( dist == i.getPosition().DistanciaA(j.getPosition()) && def == j.getDefense() && atc > j.getAtack()){
                            def = j.getDefense();
                            atc = j.getAtack();
                            EdAtack = j;
                            band = true;
                        }
                    }
               }
               if( band )
                    My_Accions.add(new AccionAtacar(i.getPosition(), EdAtack.getPosition()));
           }
        }
        return My_Accions;    
    }

    @Override
    public String MyName() {
        return "Atack";
    }
}