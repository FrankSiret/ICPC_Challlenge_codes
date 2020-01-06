package asao;

import java.util.ArrayList;
import java.util.List;
import uci.conquerors.jugador.acciones.Accion;
import uci.conquerors.status.StatusGame;
import uci.conquerors.jugador.Jugador;
import uci.conquerors.Conquerors;
import uci.conquerors.enums.Soldiers;
import uci.conquerors.jugador.acciones.AccionAtacar;
import uci.conquerors.jugador.acciones.AccionSubirNivel;
import uci.conquerors.jugador.acciones.ContratarOficialConstructor;
import uci.conquerors.jugador.acciones.ContratarOficialEntrenador;
import uci.conquerors.status.StatusBuilding;
import uci.conquerors.status.StatusShop;
import uci.conquerors.utils.Punto2D;

/**
 *
 * @author Siret
 */
public class GameOn implements Jugador {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        Conquerors.start();
    }
    
    @Override
    public List<Accion> Execute(StatusGame ej, StatusShop et) {
        List<Accion> acciones;
        acciones = new ArrayList<>();
        List<ContratarOficialEntrenador> contrato;
        contrato = new ArrayList<>();
        Punto2D men = new Punto2D(1000, 1000);  
        List<StatusBuilding> ejB = ej.getAllBuidings();
        
        for (StatusBuilding ejB1 : ejB) {
            if(ejB1.getPlayer() == ej.getPersonalID())
                acciones.add(new AccionSubirNivel(ejB1.getPosition()));
        }
        if(et.CoachOficial()) {
            acciones.add(new ContratarOficialEntrenador());
        }
        
        for (StatusBuilding ejB1 : ejB) {
            if(ejB1.getPlayer() == ej.getPersonalID()){
                if(ejB1.getPopulation() >= 10 && ejB1.getLevel() >= 2) {
                    for (StatusBuilding ejB2 : ejB) {
                        if(ejB2.getPlayer() != ej.getPersonalID() && ejB2.getPopulation() < ejB1.getPopulation()) {
                            if (men.DistanciaA(ejB1.getPosition()) > ejB2.getPosition().DistanciaA(ejB1.getPosition()))
                                men = ejB2.getPosition();
                        }            
                    }
                    if(men == new Punto2D(1000, 1000))
                        acciones.add(new AccionAtacar(ejB1.getPosition(), men));
                    men = new Punto2D(1000, 1000);
                }
            }    
        }
        return acciones;
    }

    @Override
    public String MyName() {
        return "GameOn";
    }
}
