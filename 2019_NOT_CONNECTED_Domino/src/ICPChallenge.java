/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Frank
 */

public class ICPChallenge {
    
public static void main(String[] args) {
    uci.challenge.player.Players.registTactic(NOT_CONNECTED.class);
    uci.challenge.player.Players.registTactic(NOT_CONNECTED_TEST.class);
    uci.challenge.graphic.Run.start();
}

}
