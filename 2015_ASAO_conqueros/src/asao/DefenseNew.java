/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
package asao;

import java.util.ArrayList;
import java.util.List;
import uci.conquerors.enums.Soldiers;
import uci.conquerors.jugador.acciones.Accion;
import uci.conquerors.jugador.acciones.AccionAtacar;
import uci.conquerors.jugador.acciones.AccionSubirNivel;
import uci.conquerors.jugador.acciones.ContratarOficialEntrenador;
import uci.conquerors.status.StatusArmy;
import uci.conquerors.status.StatusBuilding;
import uci.conquerors.status.StatusGame;
import uci.conquerors.status.StatusShop;
import uci.conquerors.utils.Punto2D;


public class DefenseNew implements uci.conquerors.jugador.Jugador{
    
    public static void main(String[] args) {
        uci.conquerors.Conquerors.start();
    }
    
    @Override
    public List<Accion> Execute(StatusGame ej, StatusShop ss) {
        List<Accion> accions;
        accions = new ArrayList<>();
        
        List<StatusBuilding> edmios = new ArrayList<>();
        List<StatusBuilding> edcomp = new ArrayList<>();
        boolean band;
        boolean band1;
        
        List<StatusBuilding> edAll = ej.getAllBuidings();
        List<StatusArmy> stAll = ej.getAllArmys();
            
        for (StatusBuilding i : edAll){
            band = false;
            if(i.getPlayer() == ej.getPersonalID()){
                for(StatusArmy star : stAll){
                    if(star.getDestination() == i.getPosition() && star.getEndingTime() <= 20 && star.getPlayer() != ej.getPersonalID()){
                        band = true;
                        break;
                    }
                }
                if(!band && i.getPopulation() > 10 && i.getLevel() != 5)
                    accions.add(new AccionSubirNivel(i.getPosition()));
                edmios.add(i);
            }
            else edcomp.add(i);
        }
        
        if(ss.CoachOficial())
            accions.add(new ContratarOficialEntrenador());
        
        for(StatusBuilding i : edmios){
            band = false;
            band1 = false;
            if(i.getPopulation() <= 10 && i.getLevel() > 3 && edmios.size() < 10){
                for(StatusArmy star : stAll){
                    if(star.getDestination() == i.getPosition()){
                        for(StatusBuilding mx : edmios){
                            if(i != mx){
                                double espera = i.getPosition().DistanciaA(mx.getPosition())/((mx.getSoldiers() == Soldiers.Establo) ? 0.08 : 0.02);
                                if(mx.getPopulation() >= 10){
                                    if((int)espera + 2 == star.getEndingTime()){
                                        accions.add(new AccionAtacar(mx.getPosition(), i.getPosition()));
                                        band = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(star.getEndingTime() <= 40 || band){
                            band1 = true;
                            break;
                        }
                    }
                }
                if(band1)
                    continue;
            }

            band = false; 
            double dist = 1 << 30, def = 0, atc = 0;
            Punto2D tmp = new Punto2D(1<<30, 1<<30);
            if(i.getPopulation() >= 15)
                for(StatusBuilding j : edcomp){
                    if(i.getAtackArmy() > j.getDefense() + 5){
                        if( dist > i.getPosition().DistanciaA(j.getPosition()) ){
                            dist = i.getPosition().DistanciaA(j.getPosition()); 
                            def = j.getDefense();
                            tmp = j.getPosition();
                            atc = j.getAtack();
                            band = true;
                        }
                        else if( dist == i.getPosition().DistanciaA(j.getPosition()) && def > j.getDefense() ){
                            def = j.getDefense();
                            tmp = j.getPosition(); 
                            atc = j.getAtack();
                            band = true;
                        }
                        else if( dist == i.getPosition().DistanciaA(j.getPosition()) && def == j.getDefense() && atc > j.getAtack()){
                            def = j.getDefense();
                            tmp = j.getPosition(); 
                            atc = j.getAtack();
                            band = true;
                        }
                    }
                }
            if(band)
                accions.add(new AccionAtacar(i.getPosition(), tmp));
        }
        
        return accions;
    }

    @Override
    public String MyName() {
        return "A-SAO Defense 2";
    }
}