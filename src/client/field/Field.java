package client.field;

import client.character.Char;
import client.life.AffectedArea;
import client.life.Life;
import client.life.Mob;
import client.life.MobTemporaryStat;
import connection.OutPacket;
import packet.CField;
import util.Position;

import java.awt.Rectangle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 12/14/2017.
 */
public class Field {
    private Rectangle rect;
    private double mobRate;
    private int id;
    private int returnMap, forcedReturn, createMobInterval, timeOut, timeLimit, lvLimit, lvForceMove;
    private int consumeItemCoolTime, link;
    private long uniqueId;
    private boolean town, swim, fly, reactorShuffle, expeditionOnly, partyOnly, needSkillForFly;
    private Set<Portal> portals;
    private Set<Foothold> footholds;
    private List<Life> lifes;
    private List<Char> chars;
    private Map<Life, Char> lifeToControllers;
    private String onFirstUserEnter = "", onUserEnter = "";
    private int fixedMobCapacity;

    public Field(int fieldID, long uniqueId) {
        this.id = fieldID;
        this.uniqueId = uniqueId;
        this.rect = new Rectangle(800,600);
        this.portals = new HashSet<>();
        this.footholds = new HashSet<>();
        this.lifes = new ArrayList<>();
        this.chars = new ArrayList<>();
        this.lifeToControllers = new HashMap<>();
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public int getWidth() {
        return (int) getRect().getWidth();
    }

    public int getHeight() {
        return (int) getRect().getHeight();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Set<Portal> getPortals() {
        return portals;
    }

    public void setPortals(Set<Portal> portals) {
        this.portals = portals;
    }

    public void addPortal(Portal portal) {
        getPortals().add(portal);
    }

    public int getReturnMap() {
        return returnMap;
    }

    public void setReturnMap(int returnMap) {
        this.returnMap = returnMap;
    }

    public int getForcedReturn() {
        return forcedReturn;
    }

    public void setForcedReturn(int forcedReturn) {
        this.forcedReturn = forcedReturn;
    }

    public double getMobRate() {
        return mobRate;
    }

    public void setMobRate(double mobRate) {
        this.mobRate = mobRate;
    }

    public int getCreateMobInterval() {
        return createMobInterval;
    }

    public void setCreateMobInterval(int createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getLvLimit() {
        return lvLimit;
    }

    public void setLvLimit(int lvLimit) {
        this.lvLimit = lvLimit;
    }

    public int getLvForceMove() {
        return lvForceMove;
    }

    public void setLvForceMove(int lvForceMove) {
        this.lvForceMove = lvForceMove;
    }

    public int getConsumeItemCoolTime() {
        return consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int consumeItemCoolTime) {
        this.consumeItemCoolTime = consumeItemCoolTime;
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public boolean isTown() {
        return town;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean isSwim() {
        return swim;
    }

    public void setSwim(boolean swim) {
        this.swim = swim;
    }

    public boolean isFly() {
        return fly;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public boolean isReactorShuffle() {
        return reactorShuffle;
    }

    public void setReactorShuffle(boolean reactorShuffle) {
        this.reactorShuffle = reactorShuffle;
    }

    public boolean isExpeditionOnly() {
        return expeditionOnly;
    }

    public void setExpeditionOnly(boolean expeditionONly) {
        this.expeditionOnly = expeditionONly;
    }

    public boolean isPartyOnly() {
        return partyOnly;
    }

    public void setPartyOnly(boolean partyOnly) {
        this.partyOnly = partyOnly;
    }

    public boolean isNeedSkillForFly() {
        return needSkillForFly;
    }

    public void setNeedSkillForFly(boolean needSkillForFly) {
        this.needSkillForFly = needSkillForFly;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    public void setOnFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public void setOnUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public Portal getPortalByName(String name) {
        return getPortals().stream().filter(portal -> portal.getName().equals(name)).findAny().orElse(null);
    }

    public Foothold findFootHoldBelow(Position pos) {
        pos.setY(pos.getY() - 10);
        Set<Foothold> footholds = getFootholds().stream().filter(fh -> fh.getX1() <= pos.getX() && fh.getX2() >= pos.getX()).collect(Collectors.toSet());
        Foothold res = null;
        int lastY = Integer.MAX_VALUE;
        for(Foothold fh : footholds) {
            if(res == null) {
                res = fh;
            } else {
                // interpolate between the two foothold ends for the y value below pos.x
                int x1 = fh.getX1();
                int x2 = fh.getX2() - x1;
                int x = pos.getX() - x1;
                double perc = (double) x / (double) x2;
//                System.out.println("Percentage: " + perc);
                int y = (int) (fh.getY1() + (perc * (fh.getY1() - fh.getY2())));
                if(y < lastY && y >= pos.getY()) {
                    res = fh;
                    lastY = y;
                }
            }
        }
        return res;
    }

    public Set<Foothold> getFootholds() {
        return footholds;
    }

    public void setFootholds(Set<Foothold> footholds) {
        this.footholds = footholds;
    }

    public void addFoothold(Foothold foothold) {
        getFootholds().add(foothold);
    }

    public void setFixedMobCapacity(int fixedMobCapacity) {
        this.fixedMobCapacity = fixedMobCapacity;
    }

    public int getFixedMobCapacity() {
        return fixedMobCapacity;
    }

    public List<Life> getLifes() {
        return lifes;
    }

    public void setLifes(List<Life> lifes) {
        this.lifes = lifes;
    }

    public void addLife(Life life) {
        if(!getLifes().contains(life)) {
            getLifes().add(life);
            life.setField(this);
            if(life.getObjectId() < 0) {
                life.setObjectId(1000000 + getLifes().indexOf(life));
            }
        }
    }

    public void spawnLife(Life life, Char onlyChar) {
        addLife(life);
        if (getChars().size() > 0) {
            if(life.getObjectId() < 0) {
                life.setObjectId(1000000 + getLifes().indexOf(life));
            }
            Char controller = null;
            if(getLifeToControllers().containsKey(life)) {
                controller = getLifeToControllers().get(life);
            }
            if(controller == null) {
                controller = getChars().get(0);
                addLifeController(life, controller);
            }
            Mob mob = null;
            if(life instanceof Mob) {
                mob = (Mob) life;
                mob.setTemporaryStat(new MobTemporaryStat(mob));
            }
            if (mob != null) {
                Position pos = mob.getPosition();
                Foothold fh = getFootholdById(mob.getFh());
                if(fh == null) {
                    fh = findFootHoldBelow(pos);
                }
                mob.setHomeFoothold(fh.deepCopy());
                mob.setCurFoodhold(fh.deepCopy());
                if(onlyChar == null) {
                    for (Char chr : getChars()) {
                        chr.getClient().write(CField.mobEnterField(mob, false));
                        chr.getClient().write(CField.mobChangeController(mob, false, controller == chr));
                    }
                } else {
                    onlyChar.getClient().write(CField.mobEnterField(mob, false));
                    onlyChar.getClient().write(CField.mobChangeController(mob, false, controller == onlyChar));
                }
            }
        }
    }

    private void removeLife(Life life) {
        if(getLifes().contains(life)) {
            getLifes().remove(life);
        }
    }

    private Foothold getFootholdById(int fh) {
        return getFootholds().stream().filter(f -> f.getId() == fh).findFirst().orElse(null);
    }

    public List<Char> getChars() {
        return chars;
    }

    public void addChar(Char chr) {
        if(!getChars().contains(chr)) {
            getChars().add(chr);
        }
    }

    public void removeChar(Char chr) {
        if(getChars().contains(chr)) {
            getChars().remove(chr);
        }
        for(Map.Entry<Life, Char> entry : getLifeToControllers().entrySet()) {
            if(entry.getValue() == chr) { // yes, ==
                addLifeController(entry.getKey(), null);
            }
        }
    }

    public Map<Life, Char> getLifeToControllers() {
        return lifeToControllers;
    }

    public void setLifeToControllers(Map<Life, Char> lifeToControllers) {
        this.lifeToControllers = lifeToControllers;
    }

    public void addLifeController(Life life, Char chr) {
        getLifeToControllers().put(life, chr);
    }

    public void changeLifeController(Life life) {

    }

    public Life getLifeByObjectID(int mobId) {
        return getLifes().stream().filter(mob -> mob.getObjectId() == mobId).findFirst().orElse(null);
    }

    public void spawnLifesForChar(Char chr) {
        for(Life life : getLifes()) {
            spawnLife(life, chr);
        }
    }

    @Override
    public String toString() {
        return "" + getId();
    }

    public void respawn(Mob mob) {
        mob.setHp(mob.getMaxHp());
        mob.setMp(mob.getMaxMp());
        spawnLife(mob, null);
    }

    public void broadcastPacket(OutPacket outPacket) {
        for(Char c : getChars()) {
            c.getClient().write(outPacket);
        }
    }

    public void spawnAffectedArea(AffectedArea aa) {
        addLife(aa);
        broadcastPacket(CField.affectedAreaCreated(aa));
    }
}
