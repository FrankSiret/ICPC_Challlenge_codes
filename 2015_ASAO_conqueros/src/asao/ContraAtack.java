/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asao;

import java.util.ArrayList;
import java.util.List;
import uci.conquerors.jugador.acciones.Accion;
import uci.conquerors.jugador.acciones.AccionAtacar;
import uci.conquerors.jugador.acciones.AccionSubirNivel;
import uci.conquerors.jugador.acciones.ContratarOficialEntrenador;
import uci.conquerors.status.StatusArmy;
import uci.conquerors.status.StatusBuilding;
import uci.conquerors.status.StatusGame;
import uci.conquerors.status.StatusShop;

/**
 *
 * @author FrankS
 */

public class ContraAtack implements uci.conquerors.jugador.Jugador{
    List<StatusBuilding> edmios = new ArrayList<>();
    List<StatusBuilding> edcomp = new ArrayList<>();
    StatusBuilding edificioatac;
    StatusBuilding edifb;
    StatusBuilding desde;
    
    public static void main(String[] args) {
        uci.conquerors.Conquerors.start();
    }

    @Override
    public List<Accion> Execute(StatusGame ej, StatusShop ss) {
        List<Accion> accions;
        
        accions = new ArrayList<>();
        
        if(ss.CoachOficial())
            accions.add(new ContratarOficialEntrenador());
        
        List<StatusBuilding> edAll = ej.getAllBuidings();
        List<StatusArmy> estado = ej.getAllArmys();
        
        edmios.clear();
        edcomp.clear();
              
        for (StatusBuilding edificio : edAll){
            if(edificio.getPlayer() == ej.getPersonalID()){
                edmios.add(edificio);
            }
            else edcomp.add(edificio);
        }
        
        if(edmios.size() == edAll.size())
            return accions;
        
        for(StatusBuilding edificio : edmios){
            accions.add(new AccionSubirNivel(edificio.getPosition()));
        }
        
        double distmn = Double.POSITIVE_INFINITY;
        double dist;
        
        for(StatusBuilding edifmios : edmios){
            boolean si = false;
            if(edifmios.getPopulation() > 15){
                for(StatusBuilding edificio1 : edcomp){
                    if(edificio1.getPopulation() < edifmios.getPopulation() && edificio1.getDefense() < edifmios.getAtack()){
                            dist = edifmios.getPosition().DistanciaA(edificio1.getPosition());
                            if(distmn > dist){
                                distmn = dist;
                                edificioatac = edificio1;
                                desde = edifmios;
                                si = true;
                            }
                        }
                        else if(edificio1.getPlayer() == -1 && edificio1.getPopulation() < edifmios.getPopulation() && edificio1.getDefense() < edifmios.getAtack()){
                            dist = edifmios.getPosition().DistanciaA(edificio1.getPosition());
                            if(distmn > dist){
                                distmn = dist;
                                edificioatac = edificio1;
                                desde = edifmios;
                                si = true;
                            }
                        }
                }
            }
            if(si)
                accions.add(new AccionAtacar(desde.getPosition(), edificioatac.getPosition()));
        }
        return accions;
    }

    @Override
    public String MyName() {
        return "A-SAO ContraAtack";
    }
}
