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


public class Defense implements uci.conquerors.jugador.Jugador{
    
    public static void main(String[] args) {
        uci.conquerors.Conquerors.registTactic(Defense.class);
        uci.conquerors.Conquerors.start();
    }
    
    @Override
    public List<Accion> Execute(StatusGame ej, StatusShop ss) {
        List<Accion> MyAccion;
        MyAccion = new ArrayList<>();
        
        List<StatusBuilding> edmios = new ArrayList<>();
        List<StatusBuilding> edcomp = new ArrayList<>();
        StatusBuilding edatac = null;
        boolean band;
        boolean band1;
        
        List<StatusBuilding> edAll = ej.getAllBuidings();
        List<StatusArmy> stAll = ej.getAllArmys();
            
        for (StatusBuilding edificio : edAll){
            band = false;
            if(edificio.getPlayer() == ej.getPersonalID() && edificio.getPopulation() > 10){
                MyAccion.add(new AccionSubirNivel(edificio.getPosition()));
                edmios.add(edificio);
            }
            else edcomp.add(edificio);
        }
        
        if(ss.CoachOficial())
            MyAccion.add(new ContratarOficialEntrenador());
        
        for(StatusBuilding i : edmios){
            band = false;
            if(i.getPopulation() < 10 && i.getLevel() > 3 && edmios.size() <= 5){
                for(StatusArmy star : stAll){
                    if(star.getDestination() == i.getPosition() && star.getPlayer() != ej.getPersonalID()){
                        for(StatusBuilding k : edmios){
                            double espera = i.getPosition().DistanciaA(k.getPosition())/((k.getSoldiers() == Soldiers.Establo) ? 0.08 : 0.02);
                            if(star.getEndingTime() == espera){
                                MyAccion.add(new AccionAtacar(k.getPosition(), i.getPosition()));
                                band = true;
                                break;
                            }
                        }
                        if(band)
                            break;
                    }
                }
                if(band)
                    continue;
            }
               
            if(i.getPopulation() < 10)
                continue;;
                
            band = false;
            double distmn = Double.POSITIVE_INFINITY;
            for(StatusBuilding j : edcomp){
                double dist = i.getPosition().DistanciaA(j.getPosition());
                if(distmn > dist && i.getPopulation() <= j.getPopulation()){
                    distmn = dist;
                    edatac = j;
                    band = true;
                }
            }
            if(band)
                MyAccion.add(new AccionAtacar(i.getPosition(), edatac.getPosition()));
        }
        
        return MyAccion;
    }

    @Override
    public String MyName() {
        return "A-SAO Defense";
    }
}