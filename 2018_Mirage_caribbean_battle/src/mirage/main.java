/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirage;

import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author Frank
 */
public class main {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception 
    {            
        uci.acm.challenge.Player.Players.registTactic(Mirage.class);
        uci.acm.challenge.Player.Players.registTactic(Mirage2.class);
        uci.acm.challenge.Logic.Constants.ITERATION_PER_SECOND = 5;
        uci.acm.challenge.Run.start();
    }    
}
