
package mirage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.util.Pair;
import uci.acm.challenge.Player.Actions.AttackTo;
import uci.acm.challenge.Player.Actions.BuyItem;
import uci.acm.challenge.Player.Actions.GeneralAction;
import uci.acm.challenge.Player.Actions.Move;
import uci.acm.challenge.Player.Actions.MoveTo;
import uci.acm.challenge.Player.Actions.Wait;
import uci.acm.challenge.Player.Effects.PlayerEffect;
import uci.acm.challenge.Player.Entities.PlayerEntity;
import uci.acm.challenge.Player.Items.PlayerItems;
import uci.acm.challenge.Player.Items.PlayerStore;
import uci.acm.challenge.Player.Player;
import uci.acm.challenge.Player.PlayerMap;
import uci.acm.challenge.Player.PlayerProp;
import uci.acm.challenge.Player.University;
import uci.acm.challenge.World.Entities.Entity;

/**
 *
 * @author Frank
 */
public class Mirage3 extends Player 
{
    private int iterationGame = 0;
    private int iterationMax = 6000;
    private int iterationLeft = 6000;
    
    private int dimX = 0;
    private int dimY = 0;
    
    private Vector<Integer> rockX;
    private Vector<Integer> rockY;

    private Vector<Integer> treeX;
    private Vector<Integer> treeY;

    private Vector<Integer> hierbaX;
    private Vector<Integer> hierbaY;
    
    private double[][] tableroValue;
    
    private boolean weaponRifle = false;
    private boolean weaponArco = false;
    private int weaponStarfighter = 0;
    private boolean weaponLightsaber = true;
    private int midasPurchased = 0;
    private int orderToBuy = 0;
    
    private class Values {
        private double damage;
        private double defense;
        private int distance;
        private double gold;
        
        public Values(double dam, double def, int dis, double g) {
            damage = dam;
            defense = def;
            distance = dis;
            gold = g;
        }
    }
    
    private class Hero 
    {
        private PlayerEntity player;
        private boolean dead;
        private int team;
        private int x;
        private int y;
        private String name;
        private double life;
        private double lifeMax;
        private double attack;
        private double defense;
        private double gold;
        private double regenLife;
        private double regenGold;
        private List<PlayerItems> items;
        private PlayerItems weapon;
        private double weaponScope;
        private double weaponDamage;
        private double damage;
        
        public void setDead(boolean x) 
        {
            dead = x;
        }
        
        public Hero(PlayerEntity p) 
        {
            dead = false;
            updateHero(p);
        }
        
        public void updateHero(PlayerEntity p) 
        {
            player = p;
            team = p.getTeam();
            name = p.getType();
            x = p.getX();
            y = p.getY();
            life = p.getProperty(PlayerProp.Life).getValue();
            lifeMax = p.getProperty(PlayerProp.Life).getTopValue();
            attack = p.getProperty(PlayerProp.Attack).getValue();
            defense = p.getProperty(PlayerProp.Defense).getValue();
            gold = p.getProperty(PlayerProp.Gold).getValue();
            items = p.getItems();
            weaponScope = 0;
            for (PlayerItems it : items) 
                if(it.isWeapon() && it.getScope() > weaponScope) {
                    weaponScope = it.getScope();
                    weapon = it;
                }
            weaponDamage = weapon.getDamage();
            damage = weaponDamage + attack;
            regenLife = 0;
            regenGold = 0;
            List<PlayerEffect> effects = p.getEffects();
            for(PlayerEffect it : effects) {
                if(it.getParameter() == PlayerProp.Life) {
                    regenLife += it.getValue();
                }
                if(it.getParameter() == PlayerProp.Gold) {
                    regenGold += it.getValue();
                }
            }
        }
    }
    
    private Hero heroe;
    private boolean midas1;
    private boolean midas2;
    
    private double myGold;
    private double myRegenGold;
    private double enemyGold;
    private double enemyRegenGold;
    
    @Override
    public String MyName() 
    {
        return "Mirage";
    }

    @Override
    public University MyUniversity() 
    {
        return University.UO_SAM;
    }

    @Override
    public PlayerEntity firstHero(ArrayList<PlayerEntity> p) 
    {
        for (PlayerEntity it : p) 
            if (it.getType() == "Hulk") 
                return it;
        return p.get(0);
    }

    @Override
    public PlayerEntity secondHero(ArrayList<PlayerEntity> p) 
    {
        for (PlayerEntity it : p) 
            if (it.getType() == "Havok") 
                return it;
        return p.get(0);
    }

    @Override
    public PlayerEntity thirdHero(ArrayList<PlayerEntity> p) 
    {
        for (PlayerEntity it : p) 
            if (it.getType() == "Havok") 
                return it;
        return p.get(0);
    }

    @Override
    public String firstHeroName() 
    {
        return "Rick Sanchez";
    }

    @Override
    public String secondHeroName() 
    {
        return "Morty";
    }

    @Override
    public String thirdHeroName() 
    {
        return "Morty's Clone";
    }
    
    private class par {
        private int id;
        private int v;
        public par(int a, int b) {
            id = a;
            v = b;
        }
    }
    
    private class pos {
        private int x;
        private int y;
        private int h;
        public pos(int a, int b, int c) {
            x = a;
            y = b;
            h = c;
        }
    }
    
    private class trio {
        private int id;
        private int x;
        private int y;
        private int h;
        public trio(int a, int b, int c, int d) {
            id = a;
            x = b;
            y = c;
            h = d;
        }
    }
    
    private class move {
        private int x;
        private int y;
        private MoveTo m;
        public move(int a, int b, MoveTo c) {
            x = a;
            y = b;
            m = c;
        }
    }
    
    // matar a midas2, matar a midas1, jugar
    private int gameState = 0;
    private boolean gameClean = false;
    
    @Override
    public GeneralAction iterate(List<PlayerEntity> good, List<PlayerEntity> bad, PlayerMap mapa, PlayerStore tienda) 
    {
        if (iterationGame == 0) {
            initGame(good, bad, mapa, tienda);
        }
        else {
            updateGame(good, bad, mapa, tienda);
        }
        iterationGame ++;
        
        iterationLeft =  iterationMax - iterationGame - 1;
        
        if(gameState < 2) {
            int x = good.get(good.size() - 1).getX();
            int y = good.get(good.size() - 1).getY();
            GeneralAction action = null;
            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, x, y, 0);
            } catch (Exception e) { }
            if(action != null) {
                return action;
            }
        }
        
//        todos los heroes enemigos (Heroe Enemigo)
        Vector<Hero> enemyList = new Vector<>();
        
//        heroes enemigos que tengo en la mira (Id, Distance)
        Vector<par> heroesInRange = new Vector<>();
        Vector<par> heroesOutRange = new Vector<>();
        
//        heroes enemigos donde estoy en su rango de ataque (Id, Distance)
        Vector<par> heroesInRangeOverme = new Vector<>();
        Vector<par> heroesOutRangeOverme = new Vector<>();
        
//        ATTACK NOW (Id,(x,y))
        Vector<trio> e1 = new Vector<>();
//        ATTACK IN N ITERATIONS (Id, Iteracion)
        Vector<par> e2 = new Vector<>();
//        ENEMY CAN ATTACKME NEXT (Id)
        Vector<trio> e3 = new Vector<>();
//        ENEMY CAN ATTACKME IN N ITERATIONS (Id, Iteracion)
        Vector<par> e4 = new Vector<>();
        
        Vector<trio> e5 = new Vector<>();
        Vector<par> e6 = new Vector<>();
        
        double damage = heroe.damage;
        
        for(int i = 0; i < bad.size(); i++) { // para cada heroe enemigo 
            Hero enemy = new Hero(bad.get(i));
            enemyList.add(enemy);
            double enemyDamage = enemy.damage;
            int dist = distance2(heroe.x, heroe.y, enemy.x, enemy.y);
            
            if(dist <= sqr((int)heroe.weaponScope)) { // en la mira
                heroesInRange.add(new par(i, dist));
                double danno = damage - enemy.defense;
                if(danno > 0) { // puedo atacarlo
                    if(danno >= enemy.life) { // lo mato en una iteracion
                        e1.add(new trio(i, enemy.x, enemy.y, (int)enemyDamage));
                    }
                    else if(danno > enemy.regenLife) { // lo "puedo" matar en N iteraciones
                        int iteracionForKill = (int)Math.ceil((enemy.life - enemy.regenLife) / (danno - enemy.regenLife));
                        e2.add(new par(i, iteracionForKill));
                    }
                }
            }
            else {
                heroesOutRange.add(new par(i, dist));
                double danno = damage - enemy.defense;
                if(danno > 0) { // puedo atacarlo
                    if(danno >= enemy.life) { // lo mato en una iteracion
                        e5.add(new trio(i, enemy.x, enemy.y, (int)enemyDamage));
                    }
                    else if(danno > enemy.regenLife) { // lo "puedo" matar en N iteraciones
                        int iteracionForKill = (int)Math.ceil((enemy.life - enemy.regenLife) / (danno - enemy.regenLife));
                        e6.add(new par(i, iteracionForKill));
                    }
                }
            }
            
            if(dist <= sqr((int)enemy.weaponScope)) { // estoy en su mira
                heroesInRangeOverme.add(new par(i, dist));
                double enemyDanno = enemyDamage - heroe.defense;
                if(enemyDanno > 0) { // puede atacarme
                    if(enemyDanno >= heroe.life) { // me puede matar en el siguente turno
                        e3.add(new trio(i, enemy.x, enemy.y, (int)enemyDamage));
                    }
                    else if(enemyDanno > heroe.regenLife) { // me "puede" matar en N iteraciones
                        int iteracionForKillme = (int)Math.ceil((heroe.life - heroe.regenLife) / (enemyDanno - heroe.regenLife));
                        e4.add(new par(i, iteracionForKillme));
                    }
                }
            }
            else {
                heroesOutRangeOverme.add(new par(i, dist));
            }
        }
        
        e1.sort(new Comparator<trio>() {
            @Override
            public int compare(trio o1, trio o2) {
                if(o1.h < o2.h) return 1;
                if(o1.h > o2.h) return -1;
                return 0;
            }
        });
        
        e2.sort(new Comparator<par>() {
            @Override
            public int compare(par o1, par o2) {
                if(o1.v > o2.v) return 1;
                if(o1.v < o2.v) return -1;
                return 0;
            }
        });
        
        e3.sort(new Comparator<trio>() {
            @Override
            public int compare(trio o1, trio o2) {
                if(o1.h < o2.h) return 1;
                if(o1.h > o2.h) return -1;
                return 0;
            }
        });
        
        e4.sort(new Comparator<par>() {
            @Override
            public int compare(par o1, par o2) {
                if(o1.v > o2.v) return 1;
                if(o1.v < o2.v) return -1;
                return 0;
            }
        });
        
        e5.sort(new Comparator<trio>() {
            @Override
            public int compare(trio o1, trio o2) {
                if(o1.h < o2.h) return 1;
                if(o1.h > o2.h) return -1;
                return 0;
            }
        });
        
        e6.sort(new Comparator<par>() {
            @Override
            public int compare(par o1, par o2) {
                if(o1.v > o2.v) return 1;
                if(o1.v < o2.v) return -1;
                return 0;
            }
        });
        
        if(e3.size() >= 2) { // mas de dos heroe enemigos me matan
            GeneralAction action = null;
            action = buyItemForMyDefense(heroe, 100000, tienda);
            if(action != null) {
                return action;
            }
            if(e3.get(0).h < heroe.lifeMax) { // comprar posion
                action = buyItemForMyHero(heroe, tienda, "Posion");
                if(action != null) {
                    return action;
                }
            }
        }
        else if (e3.size() == 1) {
            GeneralAction action = null;
            
            for(trio it : e1) {
                if(it.id == e3.get(0).id) { // ataco al que me mata
                    try {
                        action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
                    } catch (Exception e) { }
                }
            }
            
            if(action != null) {
                return action;
            }
            
            action = buyItemForMyDefense(heroe, enemyList.get(e3.get(0).id).damage, tienda);
            if(action != null) {
                return action;
            }
            
            if(e3.get(0).h < heroe.lifeMax) { // comprar posion
                action = buyItemForMyHero(heroe, tienda, "Posion");
                if(action != null){
                    return action;
                }
            }
        }
        
        if(e4.size() > 0) {
            GeneralAction action = null;
            
            for(trio it : e1) {
                if(it.id == e4.get(0).id) { // ataco al que me mata
                    try {
                        action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
                    } catch (Exception e) { }
                }
            }
            
            if(action != null) {
                return action;
            }
            
            action = buyItemForMyDefense(heroe, enemyList.get(e4.get(0).id).damage, tienda);
            if(action != null) {
                return action;
            }
            
            if(enemyList.get(e4.get(0).id).damage < heroe.lifeMax) { // comprar posion
                action = buyItemForMyHero(heroe, tienda, "Posion");
                if(action != null){
                    return action;
                }
            }
        }
        
        if(e1.size() > 0) {
            GeneralAction action = null;
            try {
                trio it = e1.get(0);
                action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
            } catch (Exception e) { }
            if(action != null)
                return action;
        }
        
        if(e2.size() > 0) {
            GeneralAction action = null;
            try {
                par it = e2.get(0);
                Hero e = enemyList.get(it.id);
                action = attackHeroTo(heroe, bad, mapa, tienda, e.x, e.y, 1);
            } catch (Exception e) { }
            if(action != null) 
                return action;
        }
        
        double maxEnemyDamage1 = 0;
        Hero enemy1 = null;
        for(Hero it : enemyList) {
            if(it.damage > maxEnemyDamage1) {
                maxEnemyDamage1 = it.damage;
                enemy1 = it;
            }
        }
        
        if(enemy1 != null) {
            GeneralAction action = null;
            double dist = Math.sqrt(distance2(heroe.x, heroe.y, enemy1.x, enemy1.y)) - enemy1.weaponScope;
            if(dist > 5) action = buyItemForMyDefense(heroe, enemy1.damage, tienda);
            else action = buyItemForMyDefense(heroe, enemy1.damage, tienda);
            if(action != null) {
                return action;
            }
        }
        
        if(e5.size() > 0) {
            GeneralAction action = null;
            try {
                trio it = e5.get(0);
                action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 3);
            } catch (Exception e) { }
            if(action != null)
                return action;
        }
        
        if(e6.size() > 0) {
            GeneralAction action = null;
            try {
                par it = e6.get(0);
                Hero e = enemyList.get(it.id);
                action = attackHeroTo(heroe, bad, mapa, tienda, e.x, e.y, 3);
            } catch (Exception e) { }
            if(action != null) 
                return action;
        }
        
        int timeToRecoverGold = timeToRecover(heroe, myGold - 6000, myGold, "Gold", 50);
        if(timeToRecoverGold < (iterationMax - iterationGame - 10) && midasPurchased == 0) {
            GeneralAction action = null;
            action = buyItemForMyHero(heroe, tienda, "Buscador_de_Oro");
            if(action != null) {
                midasPurchased ++;
                return action;
            }
        }
        if(weaponStarfighter == 0) {
            GeneralAction action = null;
            action = buyItemForMyHero(heroe, tienda, "Starfighter");
            if(action != null) {
                weaponStarfighter ++;
                return action;
            }
        }
        if(midasPurchased != 0 && weaponStarfighter != 0) {
            GeneralAction action = null;
            action = buyItemBack(heroe, mapa, tienda);
            if(action != null) {
                return action;
            }
        }
        
//        DESTROY ATTACK ROCK
        Vector<trio> r1 = new Vector<>();
//        CAN ATTACK ROCK
        Vector<trio> r2 = new Vector<>();
        
        for (int rX = Math.max(0, heroe.x - (int)heroe.weaponScope - 1); rX < Math.min(heroe.x + (int)heroe.weaponScope + 1, dimX); rX++) {
            for (int rY = Math.max(0, heroe.y - (int)heroe.weaponScope - 1); rY < Math.min(heroe.y + (int)heroe.weaponScope + 1, dimY); rY++) {
                if(!mapa.existItem(rX, rY)) continue;
                if(mapa.getGameCell(rX, rY).getEntity().getType() != "Rock") continue;
                if(heroe.x == rX && heroe.y == rY) continue;

                PlayerEntity ent = mapa.getGameCell(rX, rY).getEntity();
                double rockLife = ent.getProperty(PlayerProp.Life).getValue();
                double rockDefense = ent.getProperty(PlayerProp.Defense).getValue();
                double rockGold = ent.getProperty(PlayerProp.Gold).getValue();

                int dist = distance2(rX, rY, heroe.x, heroe.y);
                if(dist <= sqr((int)heroe.weaponScope)) { // tengo la roca en la mira
                    double danno = damage - rockDefense;
                    if(danno > 0) { // puedo atacar
                        if(danno >= rockLife) { // la destruyo en una iteracion
                            r1.add(new trio(0, rX, rY, dist));
                        }
                        else {
                            double maxEnemyDamage2 = 0;
                            for(Hero it : enemyList) {
                                int d = distance2(it.x, it.y, rX, rY);
                                double enemyDamage = it.damage;
                                if(d <= sqr((int)it.weaponScope)) {
                                    maxEnemyDamage2 = Math.max(maxEnemyDamage2, enemyDamage);
                                }
                            }
                            double maxEnemyDanno = maxEnemyDamage2 - rockDefense;
                            if(rockLife - danno - maxEnemyDanno > 0) // el enemigo no puede destruir la roca si ataco
                                r2.add(new trio(0, rX, rY, dist));
                        }
                    }
                }
            }
        }
        
        r1.sort(new Comparator<trio>() {
            @Override
            public int compare(trio o1, trio o2) {
                if(o1.h > o2.h) return 1;
                if(o1.h < o2.h) return -1;
                return 0;
            }
        });
        
        r2.sort(new Comparator<trio>() {
            @Override
            public int compare(trio o1, trio o2) {
                if(o1.h > o2.h) return 1;
                if(o1.h < o2.h) return -1;
                return 0;
            }
        });
        
        GeneralAction action = null;
        
        boolean treeOnRock = iterationGame < 60;
        
        if(treeOnRock) {
            pos posTree1 = getIdEntityTree(heroe, bad, mapa);
            if(posTree1 != null) {
                try {
                    action = attackHeroTo(heroe, bad, mapa, tienda, posTree1.x, posTree1.y, 3);
                } catch (Exception e) { }
            }

            if(action != null)
                return action;  
        }
        
        pos posRock = getIdEntityRock(heroe, bad, mapa);
        
        if(posRock != null) {
            for(trio it : r1) {
                if(it.x == posRock.x && it.y == posRock.y) {
                    try {
                        action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
                    } catch (Exception e) { }
                }
            }
        }
        
        if(action != null) {
            return action;
        }
        
        if(r1.size() > 0) {
            trio it = r1.get(0);
            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
            } catch (Exception e) { }
        }
       
        if(action != null) {
            return action;
        }
        
        if(posRock != null) {
            for(trio it : r2) {
                if(it.x == posRock.x && it.y == posRock.y) {
                    try {
                        action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
                    } catch (Exception e) { }
                }
            }
        }
        
        if(action != null) {
            return action;
        }
        
        if(r2.size() > 0) {
            trio it = r2.get(0);
            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, it.x, it.y, 1);
            } catch (Exception e) { }
        }
       
        if(action != null) {
            return action;
        }
        
        if(posRock != null) {
            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, posRock.x, posRock.y, 3);
            } catch (Exception e) { }
        }
        
        if(action != null) {
            return action;
        }      
        
        pos posTree1 = getIdEntityTree(heroe, bad, mapa);
        if(posTree1 != null) {
            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, posTree1.x, posTree1.y, 3);
            } catch (Exception e) { }
        }

        if(action != null)
            return action;
        
        pos posHierba = getIdEntityHierba(heroe, bad, mapa);
        if(posHierba != null) {
            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, posHierba.x, posHierba.y, 4);
            } catch (Exception e) { }
        }
        
        if(action != null)
            return action;
        
        Random ran = new Random();
        int times = 10;
        
        while(action == null && times > 0) {    
            int xr = ran.nextInt(dimX);
            int yr = ran.nextInt(dimY);
            
            xr = Math.max(0, Math.min(xr, dimX-1));
            yr = Math.max(0, Math.min(yr, dimY-1));

            try {
                action = attackHeroTo(heroe, bad, mapa, tienda, xr, yr, 0);
            } catch (Exception e) { }
        }
        
        if(action != null) 
            return action;
        
        return new Wait(good.get(0));
    }

    private int WinForGold() {
        double eG = enemyGold;
        int iL = iterationLeft - 10;
        double eRG = enemyRegenGold;
        double mG = myGold;
        double mRG = myRegenGold;
        
        double enemyGoldEnd = eG + iL * eRG;
        int t = 0;
        int c = midasPurchased - 1;
        while(t < iL) {
            if(mG + (iL - t) * mRG > enemyGoldEnd) {
                return c;
            }
            if(mG >= 6000) {
                mG -= 6000;
                mRG += 50;
                c++;
            }
            int tmp = (int)((6000 - mG) / mRG);
            t += tmp;
            if(t >= iL) {
                return -1; 
            }
            mG += tmp * mRG;
        }
        
        return -1;
    }
    
    private void busqueda(PlayerEntity heroe, PlayerMap mapa) 
    {
        rockX = new Vector<>();
        rockY = new Vector<>();
        treeX = new Vector<>();
        treeY = new Vector<>();
        hierbaX = new Vector<>();
        hierbaY = new Vector<>();
        tableroValue = new double[100][100];

        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                try {
                    double life = 0;
                    List<PlayerEffect> list = mapa.getGameCell(i, j).getEffects();
                    for(PlayerEffect it : list) {
                        if(it.getParameter() == PlayerProp.Life)
                            life += it.getValue();
                    }
                    tableroValue[i][j] = life;
                    
                    if(!mapa.existItem(i, j)) continue;
                    
                    String type = mapa.getGameCell(i, j).getEntity().getType();
                    
                    if (type == "Rock") {
                        rockX.add(i);
                        rockY.add(j);
                    }
                    else if (type == "Tree") {
                        treeX.add(i);
                        treeY.add(j);
                    }
                    else if (type == "Hierba") {
                        hierbaX.add(i);
                        hierbaY.add(j);
                    }
                }
                catch (Exception e) { }
            }
        }
    }
    
    private GeneralAction buyItemForMyDefense(Hero heroe, double damage, PlayerStore tienda) {
        double wantBuyDefense = damage - heroe.defense;
        if(wantBuyDefense <= 0) return null;
        ArrayList<PlayerItems> heroCanBuy = tienda.canBuy(heroe.player);
        double maxi = 0;
        double maxw = 0;
        PlayerItems itemi = null;
        PlayerItems itemw = null;
        for(PlayerItems it : heroCanBuy) {
            ArrayList<PlayerEffect> listEffects = it.getEffects();
            for(PlayerEffect jt : listEffects) {
                if(jt.getParameter() != PlayerProp.Defense) continue;
                
                //if(jt.getValue() < wantBuyDefense) continue;
                
                if(it.isWeapon() && maxw < jt.getValue() / it.getPrice()) {
                    maxw = jt.getValue() / it.getPrice();
                    itemw = it;
                }
                else if(maxi < jt.getValue() / it.getPrice()) {
                    if(it.getType() != "Helmet" || (it.getType() == "Helmet" && heroe.life > 60)) {
                        maxi = jt.getValue() / it.getPrice();
                        itemi = it;
                    }
                }
            }
        }
        if(itemi != null) {
            return new BuyItem(heroe.player, itemi);
        }
        if(itemw != null) {
            if(itemw.getType() == "Starfighter" && weaponStarfighter < 2) {
                weaponStarfighter ++;
                return new BuyItem(heroe.player, itemw);
            }
            if(itemw.getType() == "Rifle" && !weaponRifle) {
                weaponRifle = true;
                return new BuyItem(heroe.player, itemw);
            }
            if(itemw.getType() == "Arco" && !weaponArco) {
                weaponArco = true;
                return new BuyItem(heroe.player, itemw);
            }
        }
        return null;
    }
    
    private GeneralAction buyItemForMyHero(Hero heroe, PlayerStore tienda, String type) {
        ArrayList<PlayerItems> listCanBuy = tienda.canBuy(heroe.player);
        for(PlayerItems it : listCanBuy) {
            if(it.getType() == type) 
                return new BuyItem(heroe.player, it);
        }
        return null;
    }
    
    private int timeToRecover(Hero heroe, double from, double to, String type, int inc) {
        double regen = inc;
        if(type == "Gold") regen += heroe.regenGold;
        else if(type == "Life") regen += heroe.regenLife;
        return (int)Math.ceil((to - from) / regen);
    }
    
    private boolean iBuyToWin(double from, int inc, int iterations) {
        double regen = myRegenGold + inc;
        return (from + regen * iterations) > (enemyGold + enemyRegenGold * iterations);
    }

    private void initGame(List<PlayerEntity> good, List<PlayerEntity> bad, PlayerMap mapa, PlayerStore tienda) 
    {
        try {
            iterationMax = uci.acm.challenge.Logic.Constants.ITERATIONS;
        } catch (Exception e) { }
        
        myGold = enemyGold = 0;
        for (PlayerEntity it : good) {
            dimX = Math.max(dimX, it.getX() + 1);
            dimY = Math.max(dimY, it.getY() + 1);
            myGold += it.getProperty(PlayerProp.Gold).getValue();
        }
        for (PlayerEntity it : bad) {
            dimX = Math.max(dimX, it.getX() + 1);
            dimY = Math.max(dimY, it.getY() + 1);
            enemyGold += it.getProperty(PlayerProp.Gold).getValue();
        }
        busqueda(good.get(0), mapa);
        heroe = new Hero(good.get(0));
        midas1 = true;
        midas2 = true;
    }

    private void updateGame(List<PlayerEntity> good, List<PlayerEntity> bad, PlayerMap mapa, PlayerStore tienda) {
        heroe.updateHero(good.get(0));
        if (good.size() == 2) {
            midas2 = false;
        }
        if (good.size() == 1) {
            midas1 = false;
            gameState += 2;
        }
        if (weaponStarfighter > 0) 
            weaponArco = weaponRifle = true;
        else if (weaponRifle)
            weaponArco = true;
        
        myGold = enemyGold = 0;
        myRegenGold = enemyRegenGold = 0;
        for (PlayerEntity it : good) {
            myGold += it.getProperty(PlayerProp.Gold).getValue();
            List<PlayerItems> items = it.getItems();
            for(PlayerItems jt : items) {
                ArrayList<PlayerEffect> effects = jt.getEffects();
                for(PlayerEffect kt : effects) {
                    if(kt.getParameter() == PlayerProp.Gold)
                        myRegenGold += kt.getValue();
                }
            }
        }
        for (PlayerEntity it : bad) {
            enemyGold += it.getProperty(PlayerProp.Gold).getValue();
            List<PlayerItems> items = it.getItems();
            for(PlayerItems jt : items) {
                ArrayList<PlayerEffect> effects = jt.getEffects();
                for(PlayerEffect kt : effects) {
                    if(kt.getParameter() == PlayerProp.Gold)
                        myRegenGold += kt.getValue();
                }
            }
        }
        
        if(gameClean) 
            return;
        
        boolean b = false;
        for(int i=0; i<dimX; i++) {
            for(int j=0; j<dimY; j++) {
                try {
                    if(mapa.existItem(i, j)) 
                        if(!mapa.getGameCell(i, j).getEntity().isHero()) {
                            b = true;
                            break;
                        }
                }
                catch (Exception e) { }
            }
            if(b)
                break;
        }
        if(!b) gameClean = true;
    }
    
    private GeneralAction buyItemBack(Hero hero, PlayerMap mapa, PlayerStore tienda) {
        ArrayList<PlayerItems> items = tienda.canBuy(hero.player);
        int maxGold = 10000;
        if(orderToBuy > 10) 
            maxGold *= orderToBuy / 2;
           
        int cantMidas = WinForGold();
        //System.out.println(cantMidas);
        if(cantMidas == -1 && gameClean) {
            gameState = 3;
            for (PlayerItems it : items) {
                if (it.getType() == "Shield") 
                    return new BuyItem(hero.player, it);
            }
            return null;
        }
        else if(gameClean && cantMidas > midasPurchased) {
            gameState = 4;
            for (PlayerItems it : items) {
                if(it.getType() == "Buscador_de_Oro" && midasPurchased < cantMidas)
                    if(myGold > maxGold && timeToRecover(heroe, myGold - it.getPrice(), myGold, "Gold", 0) < iterationLeft - 100) {
                        midasPurchased ++;
                        return new BuyItem(hero.player, it);
                    }
            }
            return null;
        }
        
        for (PlayerItems it : items) {
            if(myGold > maxGold && timeToRecover(heroe, myGold - it.getPrice(), myGold, "Gold", 0) < iterationLeft - 100 && 
                    iBuyToWin(myGold - it.getPrice(), 0, iterationLeft - 100)) {
                switch (it.getType()) {
                    case "Helicopter_Medical":
                        if(orderToBuy == 0 || orderToBuy == 4 || orderToBuy == 8 || orderToBuy == 11 ||
                                orderToBuy == 27 || orderToBuy == 29) {
                            orderToBuy++;
                            return new BuyItem(hero.player, it);
                        }
                        break;
                    case "Shield":
                        if(!(orderToBuy == 0 || orderToBuy == 4 || orderToBuy == 8 || orderToBuy == 11 || 
                                orderToBuy == 3 || orderToBuy == 7 || orderToBuy == 14) && orderToBuy < 25) {
                            orderToBuy++;
                            return new BuyItem(hero.player, it);
                        }
                        break;
                    case "Buscador_de_Oro":
                        if(orderToBuy == 3 || orderToBuy == 7 || orderToBuy == 14 || orderToBuy == 25) {
                            orderToBuy++;
                            midasPurchased ++;
                            return new BuyItem(hero.player, it);
                        }
                        break;
                    case "Helmet":
                        if(orderToBuy == 26 || orderToBuy == 28 || (orderToBuy >= 30 && orderToBuy <= 50)) {
                            orderToBuy++;
                            return new BuyItem(hero.player, it);
                        }
                        break;
                }
            }
        }
        return null;
    }
    
    //order: 0-Moverse y No Atack Hero, 1-Moverse, 2-Atacar Rock, 3-Atacar Tree
    private GeneralAction attackHeroTo(Hero hero, List<PlayerEntity> bad, PlayerMap mapa, PlayerStore tienda, int x, int y, int order) throws Exception {
        int dist = distance2(hero.x, hero.y, x, y);
        if(dist == 0) return null;
        
        if(mapa.existItem(x, y) && dist <= sqr((int)hero.weaponScope)) {
            if(order == 0 && mapa.getGameCell(x, y).getEntity().isHero()) {
                if(hero.team == mapa.getGameCell(x, y).getEntity().getTeam())
                    return new AttackTo(hero.player, hero.weapon, x, y);
                else return null;
            }
            return new AttackTo(hero.player, hero.weapon, x, y);
        }
        
        move t = nextPosition(hero, x, y);
        
        if((hero.x == t.x && hero.y == t.y) || t.m == null) return null;
        
        boolean existItem = mapa.existItem(t.x, t.y);
        
        if(order >= 2) {
            pos p = null;
            p = getIdEntityRock(hero, bad, mapa);    
            if(p != null && p.h != 0)
                return new AttackTo(hero.player, hero.weapon, p.x, p.y);
        }
        if(order >= 3) {
            pos p = null;
            p = getIdEntityTree(hero, bad, mapa);
            if(p != null && p.h != 0)
                return new AttackTo(hero.player, hero.weapon, p.x, p.y);
        }
        if(order >= 4) {
            pos p = null;
            p = getIdEntityHierba(hero, bad, mapa);
            if(p != null && p.h != 0)
                return new AttackTo(hero.player, hero.weapon, p.x, p.y);
        }
        
        if(existItem) {
            if(mapa.getGameCell(t.x, t.y).getEntity().isHero()) 
                return null;
            return new AttackTo(hero.player, hero.weapon, t.x, t.y);
        }
        else return new Move(hero.player, t.m);
    }
    
    private pos getIdEntityRock(Hero hero, List<PlayerEntity> bad, PlayerMap mapa) {
        pos res = null;
        pos tmp = null;
        pos tmp2 = null;
        int min = Integer.MAX_VALUE;
        int min2 = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        
        for (int k = 0; k < rockX.size(); k++) {
            int i = rockX.get(k);
            int j = rockY.get(k);
            
            if(!mapa.existItem(i, j)) continue;
            if(mapa.getGameCell(i, j).getEntity().getType() != "Rock") continue;
            if(hero.x == i && hero.y == j) continue;

            int dist = distance2(hero.x, hero.y, i, j);
            if (dist <= sqr((int)hero.weaponScope)) {
                for(PlayerEntity it : bad) {
                    Hero enemy = new Hero(it);
                    int dist2 = distance2(enemy.x, enemy.y, i, j);
                    if(dist2 < min) {
                        min = dist2;
                        res = new pos(i, j, 2);
                    }
                }
                if(dist > max) {
                    max = dist;
                    tmp = new pos(i, j, 1);
                }
            }
            if(dist < min2) {
                min2 = dist;
                tmp2 = new pos(i, j, 0);
            }
        }
        if(res != null)
            return res;
        if(tmp != null)
            return tmp;
        return tmp2;
    }
    
    private pos getIdEntityTree(Hero hero, List<PlayerEntity> bad, PlayerMap mapa) {
        pos res = null;
        pos tmp = null;
        pos tmp2 = null;
        int min = Integer.MAX_VALUE;
        int min2 = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        
        for (int k = 0; k < treeX.size(); k++) {
            int i = treeX.get(k);
            int j = treeY.get(k);
            
            if(!mapa.existItem(i, j)) continue;
            if(mapa.getGameCell(i, j).getEntity().getType() != "Tree") continue;
            if(hero.x == i && hero.y == j) continue;

            int dist = distance2(hero.x, hero.y, i, j);
            if (dist <= sqr((int)hero.weaponScope)) {
                for(PlayerEntity it : bad) {
                    Hero enemy = new Hero(it);
                    int dist2 = distance2(enemy.x, enemy.y, i, j);
                    if(dist2 < min) {
                        min = dist2;
                        res = new pos(i, j, 2);
                    }
                }
                if(dist > max) {
                    max = dist;
                    tmp = new pos(i, j, 1);
                }
            }
            if(dist < min2) {
                min2 = dist;
                tmp2 = new pos(i, j, 0);
            }
        }
        if(res != null)
            return res;
        if(tmp != null)
            return tmp;
        return tmp2;
    }
    
    private pos getIdEntityHierba(Hero hero, List<PlayerEntity> bad, PlayerMap mapa) {
        pos res = null;
        pos tmp = null;
        pos tmp2 = null;
        int min = Integer.MAX_VALUE;
        int min2 = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        
        for (int k = 0; k < hierbaX.size(); k++) {
            int i = hierbaX.get(k);
            int j = hierbaY.get(k);
            
            if(!mapa.existItem(i, j)) continue;
            if(mapa.getGameCell(i, j).getEntity().getType() != "Hierba") continue;
            if(hero.x == i && hero.y == j) continue;

            int dist = distance2(hero.x, hero.y, i, j);
            if (dist <= sqr((int)hero.weaponScope)) {
                for(PlayerEntity it : bad) {
                    Hero enemy = new Hero(it);
                    int dist2 = distance2(enemy.x, enemy.y, i, j);
                    if(dist2 < min) {
                        min = dist2;
                        res = new pos(i, j, 2);
                    }
                }
                if(dist > max) {
                    max = dist;
                    tmp = new pos(i, j, 1);
                }
            }
            if(dist < min2) {
                min2 = dist;
                tmp2 = new pos(i, j, 0);
            }
        }
        if(res != null)
            return res;
        if(tmp != null)
            return tmp;
        return tmp2;
    }
    
    private move nextPosition(Hero hero, int x, int y) {
        int i = hero.x;
        int j = hero.y;
        int k = (int)(hero.weaponScope / Math.sqrt(2));
        
        if(x > i && y > j) 
            return new move(i+1, j+1, MoveTo.DownRigth);
        if(x > i && y < j)
            return new move(i+1, j-1, MoveTo.UpRigth);
        if(x < i && y > j)
            return new move(i-1, j+1, MoveTo.DownLeft);
        if(x < i && y < j)
            return new move(i-1, j-1, MoveTo.UpLeft);
        if(x > i)
            return new move(i+1, j, MoveTo.Rigth);
        if(x < i)
            return new move(i-1, j, MoveTo.Left);
        if(y > j)
            return new move(i, j+1, MoveTo.Down);
        if(y < j)
            return new move(i, j-1, MoveTo.Up);
        return new move(i, j, null);
    }

    private int distance2(int x, int y, int i, int j) {
        return sqr(x - i) + sqr(y - j);
    }
    
    private int sqr(int x) {
        return x * x;
    }
    
}

/*
Items:
Shield
Arco
Helmet
Posion
Buscador_de_Oro
Cape
Rifle
Lightsaber
Helicopter_Medical
Starfighter
*/