package net.swordie.ms.loaders;

import net.swordie.ms.ServerConstants;
import net.swordie.ms.client.character.items.*;
import net.swordie.ms.constants.GameConstants;
import net.swordie.ms.constants.ItemConstants;
import net.swordie.ms.enums.*;
import net.swordie.ms.util.*;
import org.apache.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.*;
import java.util.*;

import static net.swordie.ms.client.character.items.Item.Type.ITEM;
import static net.swordie.ms.enums.ScrollStat.*;

/**
 * Created on 11/17/2017.
 */
public class ItemData {
    public static Map<Integer, Equip> equips = new HashMap<>();
    public static Map<Integer, ItemInfo> items = new HashMap<>();
    public static Map<Integer, PetInfo> pets = new HashMap<>();
    public static Map<Integer, ItemOption> itemOptions = new HashMap<>();
    public static Map<Integer, Integer> skillIdByItemId = new HashMap<>();
    private static final org.apache.log4j.Logger log = LogManager.getRootLogger();


    /**
     * Creates a new Equip given an itemId.
     *
     * @param itemId         The itemId of the wanted equip.
     * @param randomizeStats whether or not to randomize the stats of the created object
     * @return A deep copy of the default values of the corresponding Equip, or null if there is no equip with itemId
     * <code>itemId</code>.
     */
    public static Equip getEquipDeepCopyFromID(int itemId, boolean randomizeStats) {
        Equip e = getEquipById(itemId);
        Equip ret = e == null ? null : e.deepCopy();
        if (ret != null) {
            ret.setQuantity(1);
            ret.setCuttable((short) -1);
            ret.setItemState((short) ItemState.AmazingHyperUpgradeChecked.getVal());
            ret.setType(Item.Type.EQUIP);
            ret.setInvType(InvType.EQUIP);
            if (randomizeStats) {
                if (ItemConstants.canEquipHaveFlame(ret)) {
                    ret.randomizeFlameStats(true);
                }
                if (ItemConstants.canEquipHavePotential(ret)) {
                    ItemGrade grade = ItemGrade.None;
                    if (Util.succeedProp(GameConstants.RANDOM_EQUIP_UNIQUE_CHANCE)) {
                        grade = ItemGrade.HiddenUnique;
                    } else if (Util.succeedProp(GameConstants.RANDOM_EQUIP_EPIC_CHANCE)) {
                        grade = ItemGrade.HiddenEpic;
                    } else if (Util.succeedProp(GameConstants.RANDOM_EQUIP_RARE_CHANCE)) {
                        grade = ItemGrade.HiddenRare;
                    }
                    if (grade != ItemGrade.None) {
                        ret.setHiddenOptionBase(grade.getVal(), ItemConstants.THIRD_LINE_CHANCE);
                    }
                }
            }
        }
        return ret;
    }

    public static Equip getEquipById(int itemId) {
        return getEquips().getOrDefault(itemId, getEquipFromFile(itemId));
    }

    private static Equip getEquipFromFile(int itemId) {
        String fieldDir = String.format("%s/equips/%d.dat", ServerConstants.DAT_DIR, itemId);
        File file = new File(fieldDir);
        if (!file.exists()) {
            return null;
        } else {
            return readEquipFromFile(file);
        }
    }

    private static Equip readEquipFromFile(File file) {
        Equip equip = null;
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file))) {
            equip = new Equip();
            equip.setItemId(dataInputStream.readInt());
            equip.setiSlot(dataInputStream.readUTF());
            equip.setvSlot(dataInputStream.readUTF());
            equip.setrJob(dataInputStream.readShort());
            equip.setrLevel(dataInputStream.readShort());
            equip.setrStr(dataInputStream.readShort());
            equip.setrDex(dataInputStream.readShort());
            equip.setrInt(dataInputStream.readShort());
            equip.setrLuk(dataInputStream.readShort());
            equip.setrPop(dataInputStream.readShort());
            equip.setiStr(dataInputStream.readShort());
            equip.setiDex(dataInputStream.readShort());
            equip.setiInt(dataInputStream.readShort());
            equip.setiLuk(dataInputStream.readShort());
            equip.setiPDD(dataInputStream.readShort());
            equip.setiMDD(dataInputStream.readShort());
            equip.setiMaxHp(dataInputStream.readShort());
            equip.setiMaxMp(dataInputStream.readShort());
            equip.setiPad(dataInputStream.readShort());
            equip.setiMad(dataInputStream.readShort());
            equip.setiEva(dataInputStream.readShort());
            equip.setiAcc(dataInputStream.readShort());
            equip.setiCraft(dataInputStream.readShort());
            equip.setiSpeed(dataInputStream.readShort());
            equip.setiJump(dataInputStream.readShort());
            equip.setDamR(dataInputStream.readShort());
            equip.setStatR(dataInputStream.readShort());
            equip.setBdr(dataInputStream.readShort());
            equip.setImdr(dataInputStream.readShort());
            equip.setTuc(dataInputStream.readShort());
            equip.setCharmEXP(dataInputStream.readInt());
            equip.setSetItemID(dataInputStream.readInt());
            equip.setPrice(dataInputStream.readInt());
            equip.setAttackSpeed(dataInputStream.readInt());
            equip.setCash(dataInputStream.readBoolean());
            equip.setExpireOnLogout(dataInputStream.readBoolean());
            equip.setExItem(dataInputStream.readBoolean());
            equip.setNotSale(dataInputStream.readBoolean());
            equip.setOnly(dataInputStream.readBoolean());
            equip.setTradeBlock(dataInputStream.readBoolean());
            equip.setEquipTradeBlock(dataInputStream.readBoolean());
            equip.setFixedPotential(dataInputStream.readBoolean());
            equip.setNoPotential(dataInputStream.readBoolean());
            equip.setBossReward(dataInputStream.readBoolean() || Arrays.asList(ItemConstants.NON_KMS_BOSS_SETS).contains(equip.getSetItemID()) || Arrays.asList(ItemConstants.NON_KMS_BOSS_ITEMS).contains(equip.getItemId()));
            equip.setSuperiorEqp(dataInputStream.readBoolean());
            equip.setiReduceReq(dataInputStream.readShort());
            short optionLength = dataInputStream.readShort();
            List<Integer> options = new ArrayList<>(optionLength);
            for (int i = 0; i < optionLength; i++) {
                options.add(dataInputStream.readInt());
            }
            for (int i = 0; i < 7 - optionLength; i++) {
                options.add(0);
            }
            equip.setOptions(options);
            equip.setFixedGrade(dataInputStream.readInt());
            equip.setSpecialGrade(dataInputStream.readInt());
            equips.put(equip.getItemId(), equip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return equip;
    }

    @Saver(varName = "equips")
    public static void saveEquips(String dir) {
        Util.makeDirIfAbsent(dir);
        for (Equip equip : getEquips().values()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(dir + "/" + equip.getItemId() + ".dat"))) {
                dataOutputStream.writeInt(equip.getItemId());
                dataOutputStream.writeUTF(equip.getiSlot());
                dataOutputStream.writeUTF(equip.getvSlot());
                dataOutputStream.writeShort(equip.getrJob());
                dataOutputStream.writeShort(equip.getrLevel());
                dataOutputStream.writeShort(equip.getrStr());
                dataOutputStream.writeShort(equip.getrDex());
                dataOutputStream.writeShort(equip.getrInt());
                dataOutputStream.writeShort(equip.getrLuk());
                dataOutputStream.writeShort(equip.getrPop());
                dataOutputStream.writeShort(equip.getiStr());
                dataOutputStream.writeShort(equip.getiDex());
                dataOutputStream.writeShort(equip.getiInt());
                dataOutputStream.writeShort(equip.getiLuk());
                dataOutputStream.writeShort(equip.getiPDD());
                dataOutputStream.writeShort(equip.getiMDD());
                dataOutputStream.writeShort(equip.getiMaxHp());
                dataOutputStream.writeShort(equip.getiMaxMp());
                dataOutputStream.writeShort(equip.getiPad());
                dataOutputStream.writeShort(equip.getiMad());
                dataOutputStream.writeShort(equip.getiEva());
                dataOutputStream.writeShort(equip.getiAcc());
                dataOutputStream.writeShort(equip.getiCraft());
                dataOutputStream.writeShort(equip.getiSpeed());
                dataOutputStream.writeShort(equip.getiJump());
                dataOutputStream.writeShort(equip.getDamR());
                dataOutputStream.writeShort(equip.getStatR());
                dataOutputStream.writeShort(equip.getBdr());
                dataOutputStream.writeShort(equip.getImdr());
                dataOutputStream.writeShort(equip.getTuc());
                dataOutputStream.writeInt(equip.getCharmEXP());
                dataOutputStream.writeInt(equip.getSetItemID());
                dataOutputStream.writeInt(equip.getPrice());
                dataOutputStream.writeInt(equip.getAttackSpeed());
                dataOutputStream.writeBoolean(equip.isCash());
                dataOutputStream.writeBoolean(equip.isExpireOnLogout());
                dataOutputStream.writeBoolean(equip.isExItem());
                dataOutputStream.writeBoolean(equip.isNotSale());
                dataOutputStream.writeBoolean(equip.isOnly());
                dataOutputStream.writeBoolean(equip.isTradeBlock());
                dataOutputStream.writeBoolean(equip.isEquipTradeBlock());
                dataOutputStream.writeBoolean(equip.isFixedPotential());
                dataOutputStream.writeBoolean(equip.isNoPotential());
                dataOutputStream.writeBoolean(equip.isBossReward() || Arrays.asList(ItemConstants.NON_KMS_BOSS_SETS).contains(equip.getSetItemID()) || Arrays.asList(ItemConstants.NON_KMS_BOSS_ITEMS).contains(equip.getItemId()));
                dataOutputStream.writeBoolean(equip.isSuperiorEqp());
                dataOutputStream.writeShort(equip.getiReduceReq());
                dataOutputStream.writeShort(equip.getOptions().size());
                for (int i : equip.getOptions()) {
                    dataOutputStream.writeInt(i);
                }
                dataOutputStream.writeInt(equip.getFixedGrade());
                dataOutputStream.writeInt(equip.getSpecialGrade());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void loadEquipsFromWz() {
        String wzDir = ServerConstants.WZ_DIR + "/Character.wz";
        String[] subMaps = new String[]{"Accessory", "Android", "Cap", "Cape", "Coat", "Dragon", "Face", "Glove",
                "Longcoat", "Mechanic", "Pants", "PetEquip", "Ring", "Shield", "Shoes", "Totem", "Weapon", "MonsterBook"};
        for (String subMap : subMaps) {
            File subDir = new File(String.format("%s/%s", wzDir, subMap));
            File[] files = subDir.listFiles();
            for (File file : files) {
                Node node = XMLApi.getRoot(file);
                List<Node> nodes = XMLApi.getAllChildren(node);
                for (Node mainNode : nodes) {
                    Map<String, String> attributes = XMLApi.getAttributes(mainNode);
                    String mainName = attributes.get("name");
                    if (mainName != null) {
                        int itemId = Integer.parseInt(attributes.get("name").replace(".img", ""));
                        Equip equip = new Equip();
                        equip.setItemId(itemId);
                        equip.setInvType(InvType.EQUIP);
                        equip.setType(Item.Type.EQUIP);
                        equip.setDateExpire(FileTime.fromType(FileTime.Type.MAX_TIME));
                        List<Integer> options = new ArrayList<>(7);
                        for (Node n : XMLApi.getAllChildren(XMLApi.getFirstChildByNameBF(mainNode, "info"))) {
                            String name = XMLApi.getNamedAttribute(n, "name");
                            String value = XMLApi.getNamedAttribute(n, "value");
                            switch (name) {
                                case "islot":
                                    equip.setiSlot(value);
                                    break;
                                case "vslot":
                                    equip.setvSlot(value);
                                    break;
                                case "reqJob":
                                    equip.setrJob(Short.parseShort(value));
                                    break;
                                case "reqLevel":
                                    equip.setrLevel(Short.parseShort(value));
                                    break;
                                case "reqSTR":
                                    equip.setrStr(Short.parseShort(value));
                                    break;
                                case "reqDEX":
                                    equip.setrDex(Short.parseShort(value));
                                    break;
                                case "reqINT":
                                    equip.setrInt(Short.parseShort(value));
                                    break;
                                case "reqPOP":
                                    equip.setrPop(Short.parseShort(value));
                                    break;
                                case "incSTR":
                                    equip.setiStr(Short.parseShort(value));
                                    break;
                                case "incDEX":
                                    equip.setiDex(Short.parseShort(value));
                                    break;
                                case "incINT":
                                    equip.setiInt(Short.parseShort(value));
                                    break;
                                case "incLUK":
                                    equip.setiLuk(Short.parseShort(value));
                                    break;
                                case "incPDD":
                                    equip.setiPDD(Short.parseShort(value));
                                    break;
                                case "incMDD":
                                    equip.setiMDD(Short.parseShort(value));
                                    break;
                                case "incMHP":
                                    equip.setiMaxHp(Short.parseShort(value));
                                    break;
                                case "incMMP":
                                    equip.setiMaxMp(Short.parseShort(value));
                                    break;
                                case "incPAD":
                                    equip.setiPad(Short.parseShort(value));
                                    break;
                                case "incMAD":
                                    equip.setiMad(Short.parseShort(value));
                                    break;
                                case "incEVA":
                                    equip.setiEva(Short.parseShort(value));
                                    break;
                                case "incACC":
                                    equip.setiAcc(Short.parseShort(value));
                                    break;
                                case "incSpeed":
                                    equip.setiSpeed(Short.parseShort(value));
                                    break;
                                case "incJump":
                                    equip.setiJump(Short.parseShort(value));
                                    break;
                                case "damR":
                                    equip.setDamR(Short.parseShort(value));
                                    break;
                                case "statR":
                                    equip.setStatR(Short.parseShort(value));
                                    break;
                                case "imdR":
                                    equip.setImdr(Short.parseShort(value));
                                    break;
                                case "bdR":
                                    equip.setBdr(Short.parseShort(value));
                                    break;
                                case "tuc":
                                    equip.setTuc(Short.parseShort(value));
                                    break;
                                case "setItemID":
                                    equip.setSetItemID(Integer.parseInt(value));
                                    break;
                                case "price":
                                    equip.setPrice(Integer.parseInt(value));
                                    break;
                                case "attackSpeed":
                                    equip.setAttackSpeed(Integer.parseInt(value));
                                    break;
                                case "cash":
                                    equip.setCash(Integer.parseInt(value)!= 0);
                                    break;
                                case "expireOnLogout":
                                    equip.setExpireOnLogout(Integer.parseInt(value)!= 0);
                                    break;
                                case "exItem":
                                    equip.setExItem(Integer.parseInt(value)!= 0);
                                    break;
                                case "notSale":
                                    equip.setNotSale(Integer.parseInt(value)!= 0);
                                    break;
                                case "only":
                                    equip.setOnly(Integer.parseInt(value)!= 0);
                                    break;
                                case "tradeBlock":
                                    equip.setTradeBlock(Integer.parseInt(value)!= 0);
                                    break;
                                case "fixedPotential":
                                    equip.setFixedPotential(Integer.parseInt(value)!= 0);
                                    break;
                                case "noPotential":
                                    equip.setNoPotential(Integer.parseInt(value) != 0);
                                    break;
                                case "bossReward":
                                    equip.setBossReward(Integer.parseInt(value) != 0);
                                    break;
                                case "superiorEqp":
                                    equip.setSuperiorEqp(Integer.parseInt(value) != 0);
                                    break;
                                case "reduceReq":
                                    equip.setiReduceReq(Short.parseShort(value));
                                    break;
                                case "fixedGrade":
                                    equip.setFixedGrade(Integer.parseInt(value));
                                    break;
                                case "specialGrade":
                                    equip.setSpecialGrade(Integer.parseInt(value));
                                    break;
                                case "charmEXP":
                                    equip.setCharmEXP(Integer.parseInt(value));
                                    break;
                            }
                            boolean hasOptions = attributes.get("name").equalsIgnoreCase("option");
                            if (hasOptions) {
                                for (Node whichOptionNode : XMLApi.getAllChildren(n)) {
                                    attributes = XMLApi.getAttributes(whichOptionNode);
                                    int index = Integer.parseInt(attributes.get("name"));
                                    Node optionNode = XMLApi.getFirstChildByNameBF(whichOptionNode, "option");
                                    Map<String, String> optionAttr = XMLApi.getAttributes(optionNode);
                                    options.set(index, Integer.parseInt(optionAttr.get("value")));
                                }
                            }
                            for (int i = 0; i < 7 - options.size(); i++) {
                                options.add(0);
                            }
                            equip.setOptions(options);
                        }
                        equips.put(equip.getItemId(), equip);
                    }
                }
            }
        }
    }

    public static ItemInfo loadItemByFile(File file) {
        ItemInfo itemInfo = null;
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file))) {
            itemInfo = new ItemInfo();
            itemInfo.setItemId(dataInputStream.readInt());
            itemInfo.setInvType(InvType.getInvTypeByString(dataInputStream.readUTF()));
            itemInfo.setCash(dataInputStream.readBoolean());
            itemInfo.setPrice(dataInputStream.readInt());
            itemInfo.setSlotMax(dataInputStream.readInt());
            itemInfo.setTradeBlock(dataInputStream.readBoolean());
            itemInfo.setNotSale(dataInputStream.readBoolean());
            itemInfo.setPath(dataInputStream.readUTF());
            itemInfo.setNoCursed(dataInputStream.readBoolean());
            itemInfo.setBagType(dataInputStream.readInt());
            itemInfo.setCharmEXP(dataInputStream.readInt());
            itemInfo.setSenseEXP(dataInputStream.readInt());
            itemInfo.setQuest(dataInputStream.readBoolean());
            itemInfo.setReqQuestOnProgress(dataInputStream.readInt());
            itemInfo.setNotConsume(dataInputStream.readBoolean());
            itemInfo.setMonsterBook(dataInputStream.readBoolean());
            itemInfo.setMobID(dataInputStream.readInt());
            itemInfo.setNpcID(dataInputStream.readInt());
            itemInfo.setLinkedID(dataInputStream.readInt());
            itemInfo.setScript(dataInputStream.readUTF());
            itemInfo.setScriptNPC(dataInputStream.readInt());
            short size = dataInputStream.readShort();
            for (int i = 0; i < size; i++) {
                ScrollStat ss = ScrollStat.getScrollStatByString(dataInputStream.readUTF());
                int val = dataInputStream.readInt();
                itemInfo.putScrollStat(ss, val);
            }
            size = dataInputStream.readShort();
            for (int i = 0; i < size; i++) {
                SpecStat ss = SpecStat.getSpecStatByName(dataInputStream.readUTF());
                int val = dataInputStream.readInt();
                itemInfo.putSpecStat(ss, val);
            }
            size = dataInputStream.readShort();
            for (int i = 0; i < size; i++) {
                itemInfo.addQuest(dataInputStream.readInt());
            }

            size = dataInputStream.readShort();
            for (int i = 0; i < size; i++) {
                itemInfo.addSkill(dataInputStream.readInt());
            }

            size = dataInputStream.readShort();
            for (int i = 0; i < size; i++) {
                ItemRewardInfo iri = new ItemRewardInfo();
                iri.setCount(dataInputStream.readInt());
                iri.setItemID(dataInputStream.readInt());
                iri.setProb(dataInputStream.readDouble());
                iri.setPeriod(dataInputStream.readInt());
                iri.setEffect(dataInputStream.readUTF());
                itemInfo.addItemReward(iri);
            }
            itemInfo.setReqSkillLv(dataInputStream.readInt());
            itemInfo.setMasterLv(dataInputStream.readInt());

            itemInfo.setMoveTo(dataInputStream.readInt());
            itemInfo.setSkillId(dataInputStream.readInt());
            getItems().put(itemInfo.getItemId(), itemInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemInfo;

    }

    public static void saveItems(String dir) {
        Util.makeDirIfAbsent(dir);
        for (ItemInfo ii : getItems().values()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(new File(dir + "/" + ii.getItemId() + ".dat")))) {
                dataOutputStream.writeInt(ii.getItemId());
                dataOutputStream.writeUTF(ii.getInvType().toString());
                dataOutputStream.writeBoolean(ii.isCash());
                dataOutputStream.writeInt(ii.getPrice());
                dataOutputStream.writeInt(ii.getSlotMax());
                dataOutputStream.writeBoolean(ii.isTradeBlock());
                dataOutputStream.writeBoolean(ii.isNotSale());
                dataOutputStream.writeUTF(ii.getPath());
                dataOutputStream.writeBoolean(ii.isNoCursed());
                dataOutputStream.writeInt(ii.getBagType());
                dataOutputStream.writeInt(ii.getCharmEXP());
                dataOutputStream.writeInt(ii.getSenseEXP());
                dataOutputStream.writeBoolean(ii.isQuest());
                dataOutputStream.writeInt(ii.getReqQuestOnProgress());
                dataOutputStream.writeBoolean(ii.isNotConsume());
                dataOutputStream.writeBoolean(ii.isMonsterBook());
                dataOutputStream.writeInt(ii.getMobID());
                dataOutputStream.writeInt(ii.getNpcID());
                dataOutputStream.writeInt(ii.getLinkedID());
                dataOutputStream.writeUTF(ii.getScript());
                dataOutputStream.writeInt(ii.getScriptNPC());
                dataOutputStream.writeShort(ii.getScrollStats().size());
                for (Map.Entry<ScrollStat, Integer> entry : ii.getScrollStats().entrySet()) {
                    dataOutputStream.writeUTF(entry.getKey().toString());
                    dataOutputStream.writeInt(entry.getValue());
                }
                dataOutputStream.writeShort(ii.getSpecStats().size());
                for (Map.Entry<SpecStat, Integer> entry : ii.getSpecStats().entrySet()) {
                    dataOutputStream.writeUTF(entry.getKey().toString());
                    dataOutputStream.writeInt(entry.getValue());
                }
                dataOutputStream.writeShort(ii.getQuestIDs().size());
                for (int i : ii.getQuestIDs()) {
                    dataOutputStream.writeInt(i);
                }

                dataOutputStream.writeShort(ii.getSkills().size());
                for (int i : ii.getSkills()) {
                    dataOutputStream.writeInt(i);
                }
                dataOutputStream.writeShort(ii.getItemRewardInfos().size());
                for (ItemRewardInfo iri : ii.getItemRewardInfos()) {
                    dataOutputStream.writeInt(iri.getCount());
                    dataOutputStream.writeInt(iri.getItemID());
                    dataOutputStream.writeDouble(iri.getProb());
                    dataOutputStream.writeInt(iri.getPeriod());
                    dataOutputStream.writeUTF(iri.getEffect());
                }
                dataOutputStream.writeInt(ii.getReqSkillLv());
                dataOutputStream.writeInt(ii.getMasterLv());

                dataOutputStream.writeInt(ii.getMoveTo());
                dataOutputStream.writeInt(ii.getSkillId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void savePets(String dir) {
        Util.makeDirIfAbsent(dir);
        for (PetInfo pi : getPets().values()) {
            File file = new File(String.format("%s/%d.dat", dir, pi.getItemID()));
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
                dos.writeInt(pi.getItemID());
                dos.writeByte(pi.getInvType().getVal());
                dos.writeInt(pi.getLife());
                dos.writeInt(pi.getSetItemID());
                dos.writeInt(pi.getLimitedLife());
                dos.writeInt(pi.getEvolutionID());
                dos.writeInt(pi.getType());
                dos.writeInt(pi.getEvolReqItemID());
                dos.writeInt(pi.getEvolNo());
                dos.writeInt(pi.getEvol1());
                dos.writeInt(pi.getEvol2());
                dos.writeInt(pi.getEvol3());
                dos.writeInt(pi.getEvol4());
                dos.writeInt(pi.getEvol5());
                dos.writeInt(pi.getProbEvol1());
                dos.writeInt(pi.getProbEvol2());
                dos.writeInt(pi.getProbEvol3());
                dos.writeInt(pi.getProbEvol4());
                dos.writeInt(pi.getProbEvol5());
                dos.writeInt(pi.getEvolReqPetLvl());
                dos.writeBoolean(pi.isAllowOverlappedSet());
                dos.writeBoolean(pi.isNoRevive());
                dos.writeBoolean(pi.isNoScroll());
                dos.writeBoolean(pi.isCash());
                dos.writeBoolean(pi.isGiantPet());
                dos.writeBoolean(pi.isPermanent());
                dos.writeBoolean(pi.isPickupItem());
                dos.writeBoolean(pi.isInteractByUserAction());
                dos.writeBoolean(pi.isLongRange());
                dos.writeBoolean(pi.isMultiPet());
                dos.writeBoolean(pi.isAutoBuff());
                dos.writeBoolean(pi.isStarPlanetPet());
                dos.writeBoolean(pi.isEvol());
                dos.writeBoolean(pi.isAutoReact());
                dos.writeBoolean(pi.isPickupAll());
                dos.writeBoolean(pi.isSweepForDrop());
                dos.writeBoolean(pi.isConsumeMP());
                dos.writeUTF(pi.getRunScript() == null ? "" : pi.getRunScript());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static PetInfo getPetInfoByID(int id) {
        return getPets().getOrDefault(id, loadPetByID(id));
    }

    public static PetInfo loadPetByID(int id) {
        File file = new File(String.format("%s/pets/%d.dat", ServerConstants.DAT_DIR, id));
        if (file.exists()) {
            return loadPetFromFile(file);
        } else {
            return null;
        }
    }

    private static PetInfo loadPetFromFile(File file) {
        PetInfo pi = null;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            pi = new PetInfo();
            pi.setItemID(dis.readInt());
            pi.setInvType(InvType.getInvTypeByVal(dis.readByte()));
            pi.setLife(dis.readInt());
            pi.setSetItemID(dis.readInt());
            pi.setLimitedLife(dis.readInt());
            pi.setEvolutionID(dis.readInt());
            pi.setType(dis.readInt());
            pi.setEvolReqItemID(dis.readInt());
            pi.setEvolNo(dis.readInt());
            pi.setEvol1(dis.readInt());
            pi.setEvol2(dis.readInt());
            pi.setEvol3(dis.readInt());
            pi.setEvol4(dis.readInt());
            pi.setEvol5(dis.readInt());
            pi.setProbEvol1(dis.readInt());
            pi.setProbEvol2(dis.readInt());
            pi.setProbEvol3(dis.readInt());
            pi.setProbEvol4(dis.readInt());
            pi.setProbEvol5(dis.readInt());
            pi.setEvolReqPetLvl(dis.readInt());
            pi.setAllowOverlappedSet(dis.readBoolean());
            pi.setNoRevive(dis.readBoolean());
            pi.setNoScroll(dis.readBoolean());
            pi.setCash(dis.readBoolean());
            pi.setGiantPet(dis.readBoolean());
            pi.setPermanent(dis.readBoolean());
            pi.setPickupItem(dis.readBoolean());
            pi.setInteractByUserAction(dis.readBoolean());
            pi.setLongRange(dis.readBoolean());
            pi.setMultiPet(dis.readBoolean());
            pi.setAutoBuff(dis.readBoolean());
            pi.setStarPlanetPet(dis.readBoolean());
            pi.setEvol(dis.readBoolean());
            pi.setAutoReact(dis.readBoolean());
            pi.setPickupAll(dis.readBoolean());
            pi.setSweepForDrop(dis.readBoolean());
            pi.setConsumeMP(dis.readBoolean());
            pi.setRunScript(dis.readUTF());
            addPetInfo(pi);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public static void loadPetsFromWZ() {
        String wzDir = ServerConstants.WZ_DIR + "/Item.wz";
        File petDir = new File(String.format("%s/%s", wzDir, "Pet"));
        for (File file : petDir.listFiles()) {
            Document doc = XMLApi.getRoot(file);
            int id = Integer.parseInt(file.getName().replace(".img.xml", ""));
            PetInfo pi = new PetInfo();
            pi.setItemID(id);
            pi.setInvType(InvType.CONSUME);
            Node infoNode = XMLApi.getFirstChildByNameBF(doc, "info");
            for (Node node : XMLApi.getAllChildren(infoNode)) {
                String name = XMLApi.getNamedAttribute(node, "name");
                String value = XMLApi.getNamedAttribute(node, "value");
                switch (name) {
                    case "icon":
                    case "iconD":
                    case "iconRaw":
                    case "iconRawD":
                    case "hungry":
                    case "nameTag":
                    case "chatBalloon":
                    case "noHungry":
                        break;
                    case "life":
                        pi.setLife(Integer.parseInt(value));
                        break;
                    case "setItemID":
                        pi.setSetItemID(Integer.parseInt(value));
                        break;
                    case "evolutionID":
                        pi.setEvolutionID(Integer.parseInt(value));
                        break;
                    case "type":
                        pi.setType(Integer.parseInt(value));
                        break;
                    case "limitedLife":
                        pi.setLimitedLife(Integer.parseInt(value));
                        break;
                    case "evol1":
                        pi.setEvol1(Integer.parseInt(value));
                        break;
                    case "evol2":
                        pi.setEvol2(Integer.parseInt(value));
                        break;
                    case "evol3":
                        pi.setEvol3(Integer.parseInt(value));
                        break;
                    case "evol4":
                        pi.setEvol4(Integer.parseInt(value));
                        break;
                    case "evol5":
                        pi.setEvol5(Integer.parseInt(value));
                        break;
                    case "evolProb1":
                        pi.setProbEvol1(Integer.parseInt(value));
                        break;
                    case "evolProb2":
                        pi.setProbEvol2(Integer.parseInt(value));
                        break;
                    case "evolProb3":
                        pi.setProbEvol3(Integer.parseInt(value));
                        break;
                    case "evolProb4":
                        pi.setProbEvol4(Integer.parseInt(value));
                        break;
                    case "evolProb5":
                        pi.setProbEvol5(Integer.parseInt(value));
                        break;
                    case "evolReqItemID":
                        pi.setEvolReqItemID(Integer.parseInt(value));
                        break;
                    case "evolReqPetLvl":
                        pi.setEvolReqPetLvl(Integer.parseInt(value));
                        break;
                    case "evolNo":
                        pi.setEvolNo(Integer.parseInt(value));
                        break;
                    case "permanent":
                        pi.setPermanent(Integer.parseInt(value) != 0);
                        break;
                    case "pickupItem":
                        pi.setPickupItem(Integer.parseInt(value) != 0);
                        break;
                    case "interactByUserAction":
                        pi.setInteractByUserAction(Integer.parseInt(value) != 0);
                        break;
                    case "longRange":
                        pi.setLongRange(Integer.parseInt(value) != 0);
                        break;
                    case "giantPet":
                        pi.setGiantPet(Integer.parseInt(value) != 0);
                        break;
                    case "noMoveToLocker":
                        pi.setAllowOverlappedSet(Integer.parseInt(value) != 0);
                        break;
                    case "allowOverlappedSet":
                        pi.setAllowOverlappedSet(Integer.parseInt(value) != 0);
                        break;
                    case "noRevive":
                        pi.setNoRevive(Integer.parseInt(value) != 0);
                        break;
                    case "noScroll":
                        pi.setNoScroll(Integer.parseInt(value) != 0);
                        break;
                    case "autoBuff":
                        pi.setAutoBuff(Integer.parseInt(value) != 0);
                        break;
                    case "multiPet":
                        pi.setAutoBuff(Integer.parseInt(value) != 0);
                        break;
                    case "autoReact":
                        pi.setAutoReact(Integer.parseInt(value) != 0);
                        break;
                    case "pickupAll":
                        pi.setPickupAll(Integer.parseInt(value) != 0);
                        break;
                    case "sweepForDrop":
                        pi.setSweepForDrop(Integer.parseInt(value) != 0);
                        break;
                    case "consumeMP":
                        pi.setConsumeMP(Integer.parseInt(value) != 0);
                        break;
                    case "evol":
                        pi.setEvol(Integer.parseInt(value) != 0);
                        break;
                    case "starPlanetPet":
                        pi.setStarPlanetPet(Integer.parseInt(value) != 0);
                        break;
                    case "cash":
                        pi.setCash(Integer.parseInt(value) != 0);
                        pi.setInvType(InvType.CASH);
                        pi.setCash(true);
                        break;
                    case "runScript":
                        pi.setRunScript(value);
                        break;
                    default:
                        log.error(String.format("Unhandled pet node, name = %s, value = %s.", name, value));
                        break;
                }
            }
            addPetInfo(pi);
            ItemInfo ii = new ItemInfo();
            ii.setItemId(pi.getItemID());
            ii.setInvType(pi.getInvType());
            addItemInfo(ii);
        }
    }

    public static void loadItemsFromWZ() {
        String wzDir = ServerConstants.WZ_DIR + "/Item.wz";
        String[] subMaps = new String[]{"Cash", "Consume", "Etc", "Install", "Special"}; // not pet
        for (String subMap : subMaps) {
            File subDir = new File(String.format("%s/%s", wzDir, subMap));
            File[] files = subDir.listFiles();
            for (File file : files) {
                Document doc = XMLApi.getRoot(file);
                Node node = doc;
                List<Node> nodes = XMLApi.getAllChildren(node);
                for (Node mainNode : XMLApi.getAllChildren(nodes.get(0))) {
                    String nodeName = XMLApi.getNamedAttribute(mainNode, "name");
                    if (!Util.isNumber(nodeName)) {
                        log.error(String.format("%s is not a number.", nodeName));
                        continue;
                    }
                    int id = Integer.parseInt(nodeName);
                    ItemInfo item = new ItemInfo();
                    item.setItemId(id);
                    item.setInvType(InvType.getInvTypeByString(subMap));
                    Node infoNode = XMLApi.getFirstChildByNameBF(mainNode, "info");
                    if (infoNode != null) {
                        for (Node info : XMLApi.getAllChildren(infoNode)) {
                            String name = XMLApi.getNamedAttribute(info, "name");
                            String value = XMLApi.getNamedAttribute(info, "value");
                            switch (name) {
                                case "cash":
                                    item.setCash(Integer.parseInt(value) != 0);
                                    break;
                                case "price":
                                    item.setPrice(Integer.parseInt(value));
                                    break;
                                case "slotMax":
                                    item.setSlotMax(Integer.parseInt(value));
                                    break;
                                // info not currently interesting. May be interesting in the future.
                                case "icon":
                                case "iconRaw":
                                case "iconD":
                                case "iconReward":
                                case "iconShop":
                                case "recoveryHP":
                                case "recoveryMP":
                                case "sitAction":
                                case "bodyRelMove":
                                case "only":
                                case "noDrop":
                                case "timeLimited":
                                case "accountSharable":
                                case "nickTag":
                                case "nickSkill":
                                case "endLotteryDate":
                                case "noFlip":
                                case "noMoveToLocker":
                                case "soldInform":
                                case "purchaseShop":
                                case "flatRate":
                                case "limitMin":
                                case "protectTime":
                                case "maxDays":
                                case "reset":
                                case "replace":
                                case "expireOnLogout":
                                case "max":
                                case "lvOptimum":
                                case "lvRange":
                                case "limitedLv":
                                case "tradeReward":
                                case "type":
                                case "floatType":
                                case "message":
                                case "pquest":
                                case "bonusEXPRate":
                                case "notExtend":
                                    break;

                                case "skill":
                                    for (Node masteryBookSkillIdNode : XMLApi.getAllChildren(info)) {
                                        item.addSkill(Integer.parseInt((XMLApi.getNamedAttribute(masteryBookSkillIdNode, "value"))));
                                    }
                                    break;
                                case "reqSkillLevel":
                                    item.setReqSkillLv(Integer.parseInt(value));
                                    break;
                                case "masterLevel":
                                    item.setMasterLv(Integer.parseInt(value));
                                    break;

                                case "stateChangeItem":
                                case "direction":
                                case "exGrade":
                                case "exGradeWeight":
                                case "effect":
                                case "bigSize":
                                case "nickSkillTimeLimited":
                                case "StarPlanet":
                                case "useTradeBlock":
                                case "commerce":
                                case "invisibleWeapon":
                                case "sitEmotion":
                                case "sitLeft":
                                case "tamingMob":
                                case "textInfo":
                                case "lv":
                                case "tradeAvailable":
                                case "pickUpBlock":
                                case "rewardItemID":
                                case "autoPrice":
                                case "selectedSlot":
                                case "minusLevel":
                                case "addTime":
                                case "reqLevel":
                                case "waittime":
                                case "buffchair":
                                case "cooltime":
                                case "consumeitem":
                                case "distanceX":
                                case "distanceY":
                                case "maxDiff":
                                case "maxDX":
                                case "levelDX":
                                case "maxLevel":
                                case "exp":
                                case "dropBlock":
                                case "dropExpireTime":
                                case "animation_create":
                                case "animation_dropped":
                                case "noCancelMouse":
                                case "soulItemType":
                                case "Rate":
                                case "unitPrice":
                                case "delayMsg":
                                case "bridlePropZeroMsg":
                                case "create":
                                case "nomobMsg":
                                case "bridleProp":
                                case "bridlePropChg":
                                case "bridleMsgType":
                                case "mobHP":
                                case "left":
                                case "right":
                                case "top":
                                case "bottom":
                                case "useDelay":
                                case "name":
                                case "uiData":
                                case "grade":
                                case "UI":
                                case "recoveryRate":
                                case "itemMsg":
                                case "noRotateIcon":
                                case "endUseDate":
                                case "noSound":
                                case "slotMat":
                                case "isBgmOrEffect":
                                case "bgmPath":
                                case "repeat":
                                case "NoCancel":
                                case "rotateSpeed":
                                case "gender":
                                case "life":
                                case "pickupItem":
                                case "add":
                                case "consumeHP":
                                case "longRange":
                                case "dropSweep":
                                case "pickupAll":
                                case "ignorePickup":
                                case "consumeMP":
                                case "autoBuff":
                                case "smartPet":
                                case "giantPet":
                                case "shop":
                                case "recall":
                                case "autoSpeaking":
                                case "consumeCure":
                                case "meso":
                                case "maplepoint":
                                case "rate":
                                case "overlap":
                                case "lt":
                                case "rb":
                                case "path4Top":
                                case "jumplevel":
                                case "slotIndex":
                                case "addDay":
                                case "incLEV":
                                case "cashTradeBlock":
                                case "dressUpgrade":
                                case "skillEffectID":
                                case "emotion":
                                case "tradBlock":
                                case "tragetBlock":
                                case "scanTradeBlock":
                                case "mobPotion":
                                case "ignoreTendencyStatLimit":
                                case "effectByItemID":
                                case "pachinko":
                                case "iconEnter":
                                case "iconLeave":
                                case "noMoveIcon":
                                case "noShadow":
                                case "preventslip":
                                case "recover":
                                case "warmsupport":
                                case "reqCUC":
                                case "incCraft":
                                case "reqEquipLevelMin":
                                case "incPVPDamage":
                                case "successRates":
                                case "enchantCategory":
                                case "additionalSuccess":
                                case "level":
                                case "specialItem":
                                case "exNew":
                                case "cuttable":
                                case "setItemCategory":
                                case "perfectReset":
                                case "resetRUC":
                                case "incMax":
                                case "noSuperior":
                                case "noRecovery":
                                case "reqMap":
                                case "random":
                                case "limit":
                                case "cantAccountSharable":
                                case "LvUpWarning":
                                case "canAccountSharable":
                                case "canUseJob":
                                case "createPeriod":
                                case "iconLarge":
                                case "morphItem":
                                case "consumableFrom":
                                case "noExpend":
                                case "sample":
                                case "notPickUpByPet":
                                case "sharableOnce":
                                case "bonusStageItem":
                                case "sampleOffsetY":
                                case "runOnPickup":
                                case "noSale":
                                case "skillCast":
                                case "activateCardSetID":
                                case "summonSoulMobID":
                                case "cursor":
                                case "karma":
                                case "pointCost":
                                case "itemPoint":
                                case "sharedStatCostGrade":
                                case "levelVariation":
                                case "accountShareable":
                                case "extendLimit":
                                case "showMessage":
                                case "mcType":
                                case "consumeItem":
                                case "hybrid":
                                case "mobId":
                                case "lvMin":
                                case "lvMax":
                                case "picture":
                                case "ratef":
                                case "time":
                                case "reqGuildLevel":
                                case "guild":
                                case "randEffect":
                                case "accountShareTag":
                                case "removeEffect":
                                case "forcingItem":
                                case "fixFrameIdx":
                                case "buffItemID":
                                case "removeCharacterInfo":
                                case "nameInfo":
                                case "bgmInfo":
                                case "flip":
                                case "pos":
                                case "randomChair":
                                case "maxLength":
                                case "continuity":
                                case "specificDX":
                                case "groupTWInfo":
                                case "face":
                                case "removeBody":
                                case "mesoChair":
                                case "towerBottom":
                                case "towerTop":
                                case "topOffset":
                                case "craftEXP":
                                case "willEXP":
                                    break;
                                case "tradeBlock":
                                    item.setTradeBlock(Integer.parseInt(value) != 0);
                                    break;
                                case "notSale":
                                    item.setNotSale(Integer.parseInt(value) != 0);
                                    break;
                                case "path":
                                    item.setPath(value);
                                    break;
                                case "noCursed":
                                    item.setNoCursed(Integer.parseInt(value) != 0);
                                    break;
                                case "noNegative":
                                    item.putScrollStat(noNegative, Integer.parseInt(value));
                                    break;
                                case "incRandVol":
                                    item.putScrollStat(incRandVol, Integer.parseInt(value));
                                    break;
                                case "success":
                                    item.putScrollStat(success, Integer.parseInt(value));
                                    break;
                                case "incSTR":
                                    item.putScrollStat(incSTR, Integer.parseInt(value));
                                    break;
                                case "incDEX":
                                    item.putScrollStat(incDEX, Integer.parseInt(value));
                                    break;
                                case "incINT":
                                    item.putScrollStat(incINT, Integer.parseInt(value));
                                    break;
                                case "incLUK":
                                    item.putScrollStat(incLUK, Integer.parseInt(value));
                                    break;
                                case "incPAD":
                                    item.putScrollStat(incPAD, Integer.parseInt(value));
                                    break;
                                case "incMAD":
                                    item.putScrollStat(incMAD, Integer.parseInt(value));
                                    break;
                                case "incPDD":
                                    item.putScrollStat(incPDD, Integer.parseInt(value));
                                    break;
                                case "incMDD":
                                    item.putScrollStat(incMDD, Integer.parseInt(value));
                                    break;
                                case "incEVA":
                                    item.putScrollStat(incEVA, Integer.parseInt(value));
                                    break;
                                case "incACC":
                                    item.putScrollStat(incACC, Integer.parseInt(value));
                                    break;
                                case "incPERIOD":
                                    item.putScrollStat(incPERIOD, Integer.parseInt(value));
                                    break;
                                case "incMHP":
                                case "incMaxHP":
                                    item.putScrollStat(incMHP, Integer.parseInt(value));
                                    break;
                                case "incMMP":
                                case "incMaxMP":
                                    item.putScrollStat(incMMP, Integer.parseInt(value));
                                    break;
                                case "incSpeed":
                                    item.putScrollStat(incSpeed, Integer.parseInt(value));
                                    break;
                                case "incJump":
                                    item.putScrollStat(incJump, Integer.parseInt(value));
                                    break;
                                case "incReqLevel":
                                    item.putScrollStat(incReqLevel, Integer.parseInt(value));
                                    break;
                                case "randOption":
                                    item.putScrollStat(randOption, Integer.parseInt(value));
                                    break;
                                case "randstat":
                                case "randStat":
                                    item.putScrollStat(randStat, Integer.parseInt(value));
                                    break;
                                case "tuc":
                                    item.putScrollStat(tuc, Integer.parseInt(value));
                                    break;
                                case "incIUC":
                                    item.putScrollStat(incIUC, Integer.parseInt(value));
                                    break;
                                case "speed":
                                    item.putScrollStat(speed, Integer.parseInt(value));
                                    break;
                                case "forceUpgrade":
                                    item.putScrollStat(forceUpgrade, Integer.parseInt(value));
                                    break;
                                case "cursed":
                                    item.putScrollStat(cursed, Integer.parseInt(value));
                                    break;
                                case "maxSuperiorEqp":
                                    item.putScrollStat(maxSuperiorEqp, Integer.parseInt(value));
                                    break;
                                case "reqRUC":
                                    item.putScrollStat(reqRUC, Integer.parseInt(value));
                                    break;
                                case "bagType":
                                    item.setBagType(Integer.parseInt(value));
                                    break;
                                case "charmEXP":
                                case "charismaEXP":
                                    item.setCharmEXP(Integer.parseInt(value));
                                    break;
                                case "senseEXP":
                                    item.setSenseEXP(Integer.parseInt(value));
                                    break;
                                case "quest":
                                    item.setQuest(Integer.parseInt(value) != 0);
                                    break;
                                case "reqQuestOnProgress":
                                    item.setReqQuestOnProgress(Integer.parseInt(value));
                                    break;
                                case "qid":
                                case "questId":
                                    if (value.contains(".") && value.split("[.]").length > 0) {
                                        item.addQuest(Integer.parseInt(value.split("[.]")[0]));
                                    } else {
                                        item.addQuest(Integer.parseInt(value));
                                    }
                                    break;
                                case "notConsume":
                                    item.setNotConsume(Integer.parseInt(value) != 0);
                                    break;
                                case "monsterBook":
                                    item.setMonsterBook(Integer.parseInt(value) != 0);
                                    break;
                                case "mob":
                                    item.setMobID(Integer.parseInt(value));
                                    break;
                                case "npc":
                                    item.setNpcID(Integer.parseInt(value));
                                    break;
                                case "linkedID":
                                    item.setLinkedID(Integer.parseInt(value));
                                    break;
                                case "reqEquipLevelMax":
                                    item.putScrollStat(reqEquipLevelMax, Integer.parseInt(value));
                                    break;
                                case "createType":
                                    item.putScrollStat(createType, Integer.parseInt(value));
                                    break;
                                case "optionType":
                                    item.putScrollStat(optionType, Integer.parseInt(value));
                                    break;
                                case "spec":
                                    break;
                                default:
                                    log.warn(String.format("Unknown node: %s, value = %s, itemID = %s", name, value, item.getItemId()));
                            }
                        }
                    }
                    Node socket = XMLApi.getFirstChildByNameBF(mainNode, "socket");
                    if (socket != null) {
                        for (Node socketNode : XMLApi.getAllChildren(socket)) {
                            String name = XMLApi.getNamedAttribute(socketNode, "name");
                            String value = XMLApi.getNamedAttribute(socketNode, "value");
                            switch (name) {
                                case "optionType":
                                    item.putScrollStat(optionType, Integer.parseInt(value));
                                    break;
                            }
                        }
                    }
                    Node spec = XMLApi.getFirstChildByNameBF(mainNode, "spec");
                    if (spec != null) {
                        for (Node specNode : XMLApi.getAllChildren(spec)) {
                            String name = XMLApi.getNamedAttribute(specNode, "name");
                            String value = XMLApi.getNamedAttribute(specNode, "value");
                            switch (name) {
                                case "script":
                                    item.setScript(value);
                                    break;
                                case "npc":
                                    item.setScriptNPC(Integer.parseInt(value));
                                    break;
                                case "moveTo":
                                    item.setMoveTo(Integer.parseInt(value));
                                    break;
                                default:
                                    SpecStat ss = SpecStat.getSpecStatByName(name);
                                    if (ss != null && value != null) {
                                        item.putSpecStat(ss, Integer.parseInt(value));
                                    } else {
                                        log.error(String.format("Unhandled spec for id %d, name %s, value %s", id, name, value));
                                    }
                            }
                        }
                    }
                    Node reward = XMLApi.getFirstChildByNameBF(mainNode, "reward");
                    if (reward != null) {
                        for (Node rewardNode : XMLApi.getAllChildren(reward)) {
                            ItemRewardInfo iri = new ItemRewardInfo();
                            for (Node rewardInfoNode : XMLApi.getAllChildren(rewardNode)) {
                                String name = XMLApi.getNamedAttribute(rewardInfoNode, "name");
                                String value = XMLApi.getNamedAttribute(rewardInfoNode, "value");
                                if (value == null) {
                                    continue;
                                }
                                value = value.replace("\\r\\n", "").replace("[R8]", "");
                                switch (name) {
                                    case "count":
                                        iri.setCount(Integer.parseInt(value));
                                        break;
                                    case "item":
                                        iri.setItemID(Integer.parseInt(value));
                                        break;
                                    case "prob":
                                        iri.setProb(Double.parseDouble(value));
                                        break;
                                    case "period":
                                        iri.setPeriod(Integer.parseInt(value));
                                        break;
                                    case "effect":
                                    case "Effect":
                                        iri.setEffect(value);
                                        break;
                                }
                            }
                            item.addItemReward(iri);
                        }
                    }
                    item.setSkillId(getSkillIdByItemId(id));
                    getItems().put(item.getItemId(), item);
                }
            }
        }
    }

    public static void loadMountItemsFromFile() {
        File file = new File(String.format("%s/mountsFromItem.txt", ServerConstants.RESOURCES_DIR));
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineSplit = line.split(" ");
                int itemId = Integer.parseInt(lineSplit[0]);
                int skillId = Integer.parseInt(lineSplit[1]);
                skillIdByItemId.put(itemId, skillId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getSkillIdByItemId(int itemId) {
        return skillIdByItemId.getOrDefault(itemId, 0);
    }

    public static Item getDeepCopyByItemInfo(ItemInfo itemInfo) {
        if (itemInfo == null) {
            return null;
        }
        Item res = new Item();
        res.setItemId(itemInfo.getItemId());
        res.setQuantity(1);
        res.setType(ITEM);
        res.setInvType(itemInfo.getInvType());
        return res;
    }

    public static Item getItemDeepCopy(int id) {
        return getItemDeepCopy(id, false);
    }

    public static Item getItemDeepCopy(int id, boolean randomize) {
        if (ItemConstants.isEquip(id)) {
            return getEquipDeepCopyFromID(id, randomize);
        } else if (ItemConstants.isPet(id)) {
            return getPetDeepCopyFromID(id);
        }
        return getDeepCopyByItemInfo(getItemInfoByID(id));
    }

    private static PetItem getPetDeepCopyFromID(int id) {
        return getPetInfoByID(id) == null ? null : getPetInfoByID(id).createPetItem();
    }

    public static ItemInfo getItemInfoByID(int itemID) {
        ItemInfo ii = getItems().getOrDefault(itemID, null);
        if (ii == null) {
            File file = new File(String.format("%s/items/%d.dat", ServerConstants.DAT_DIR, itemID));
            if (!file.exists()) {
                return null;
            } else {
                ii = loadItemByFile(file);
            }
        }
        return ii;
    }

    public static Map<Integer, Equip> getEquips() {
        return equips;
    }

    public static void loadItemOptionsFromWZ() {
        String wzDir = ServerConstants.WZ_DIR + "/Item.wz";
        String itemOptionDir = String.format("%s/ItemOption.img.xml", wzDir);
        File file = new File(itemOptionDir);
        Document doc = XMLApi.getRoot(file);
        Node node = doc;
        List<Node> nodes = XMLApi.getAllChildren(node);
        for (Node mainNode : XMLApi.getAllChildren(nodes.get(0))) {
            ItemOption io = new ItemOption();
            String nodeName = XMLApi.getNamedAttribute(mainNode, "name");
            io.setId(Integer.parseInt(nodeName));
            Node infoNode = XMLApi.getFirstChildByNameBF(mainNode, "info");
            if (infoNode != null) {
                for (Node infoChild : XMLApi.getAllChildren(infoNode)) {
                    String name = XMLApi.getNamedAttribute(infoChild, "name");
                    String value = XMLApi.getNamedAttribute(infoChild, "value");
                    switch (name) {
                        case "optionType":
                            io.setOptionType(Integer.parseInt(value));
                            break;
                        case "weight":
                            io.setWeight(Integer.parseInt(value));
                            break;
                        case "reqLevel":
                            io.setReqLevel(Integer.parseInt(value));
                            break;
                        case "string":
                            io.setString(value);
                            break;
                    }
                }
            }
            Node levelNode = XMLApi.getFirstChildByNameBF(mainNode, "level");
            if (levelNode != null) {
                for (Node levelChild : XMLApi.getAllChildren(levelNode)) {
                    int level = Integer.parseInt(XMLApi.getNamedAttribute(levelChild, "name"));
                    for (Node levelInfoNode : XMLApi.getAllChildren(levelChild)) {
                        String name = XMLApi.getNamedAttribute(levelInfoNode, "name");
                        String stringValue = XMLApi.getNamedAttribute(levelInfoNode, "value");
                        int value = 0;
                        if (Util.isNumber(stringValue)) {
                            value = Integer.parseInt(stringValue);
                        }
                        switch (name) {
                            case "incSTR":
                                io.addStatValue(level, BaseStat.str, value);
                                break;
                            case "incDEX":
                                io.addStatValue(level, BaseStat.dex, value);
                                break;
                            case "incINT":
                                io.addStatValue(level, BaseStat.inte, value);
                                break;
                            case "incLUK":
                                io.addStatValue(level, BaseStat.luk, value);
                                break;
                            case "incMHP":
                                io.addStatValue(level, BaseStat.mhp, value);
                                break;
                            case "incMMP":
                                io.addStatValue(level, BaseStat.mmp, value);
                                break;
                            case "incACC":
                                io.addStatValue(level, BaseStat.acc, value);
                                break;
                            case "incEVA":
                                io.addStatValue(level, BaseStat.eva, value);
                                break;
                            case "incSpeed":
                                io.addStatValue(level, BaseStat.speed, value);
                                break;
                            case "incJump":
                                io.addStatValue(level, BaseStat.jump, value);
                                break;
                            case "incPAD":
                                io.addStatValue(level, BaseStat.pad, value);
                                break;
                            case "incMAD":
                                io.addStatValue(level, BaseStat.mad, value);
                                break;
                            case "incPDD":
                                io.addStatValue(level, BaseStat.pdd, value);
                                break;
                            case "incMDD":
                                io.addStatValue(level, BaseStat.mdd, value);
                                break;
                            case "incCr":
                                io.addStatValue(level, BaseStat.cr, value);
                                break;
                            case "incPADr":
                                io.addStatValue(level, BaseStat.padR, value);
                                break;
                            case "incMADr":
                                io.addStatValue(level, BaseStat.madR, value);
                                break;
                            case "incSTRr":
                                io.addStatValue(level, BaseStat.strR, value);
                                break;
                            case "incDEXr":
                                io.addStatValue(level, BaseStat.dexR, value);
                                break;
                            case "incINTr":
                                io.addStatValue(level, BaseStat.intR, value);
                                break;
                            case "incLUKr":
                                io.addStatValue(level, BaseStat.lukR, value);
                                break;
                            case "ignoreTargetDEF":
                                io.addStatValue(level, BaseStat.ied, value);
                                break;
                            case "boss":
                                io.addStatValue(level, BaseStat.bd, value);
                                break;
                            case "incDAMr":
                                io.addStatValue(level, BaseStat.fd, value);
                                break;
                            case "incAllskill":
                                io.addStatValue(level, BaseStat.incAllSkill, value);
                                break;
                            case "incMHPr":
                                io.addStatValue(level, BaseStat.mhpR, value);
                                break;
                            case "incMMPr":
                                io.addStatValue(level, BaseStat.mmpR, value);
                                break;
                            case "incACCr":
                                io.addStatValue(level, BaseStat.accR, value);
                                break;
                            case "incEVAr":
                                io.addStatValue(level, BaseStat.evaR, value);
                                break;
                            case "incPDDr":
                                io.addStatValue(level, BaseStat.pddR, value);
                                break;
                            case "incMDDr":
                                io.addStatValue(level, BaseStat.mddR, value);
                                break;
                            case "RecoveryHP":
                                io.addStatValue(level, BaseStat.hpRecovery, value);
                                break;
                            case "RecoveryMP":
                                io.addStatValue(level, BaseStat.mpRecovery, value);
                                break;
                            case "incMaxDamage":
                                io.addStatValue(level, BaseStat.damageOver, value);
                                break;
                            case "incSTRlv":
                                io.addStatValue(level, BaseStat.strLv, value / 10D);
                                break;
                            case "incDEXlv":
                                io.addStatValue(level, BaseStat.dexLv, value / 10D);
                                break;
                            case "incINTlv":
                                io.addStatValue(level, BaseStat.intLv, value / 10D);
                                break;
                            case "incLUKlv":
                                io.addStatValue(level, BaseStat.lukLv, value / 10D);
                                break;
                            case "RecoveryUP":
                                io.addStatValue(level, BaseStat.recoveryUp, value);
                                break;
                            case "incTerR":
                                io.addStatValue(level, BaseStat.ter, value);
                                break;
                            case "incAsrR":
                                io.addStatValue(level, BaseStat.asr, value);
                                break;
                            case "incEXPr":
                                io.addStatValue(level, BaseStat.expR, value);
                                break;
                            case "mpconReduce":
                                io.addStatValue(level, BaseStat.mpconReduce, value);
                                break;
                            case "reduceCooltime":
                                io.addStatValue(level, BaseStat.reduceCooltime, value);
                                break;
                            case "incMesoProp":
                                io.addStatValue(level, BaseStat.mesoR, value);
                                break;
                            case "incRewardProp":
                                io.addStatValue(level, BaseStat.dropR, value);
                                break;
                            case "incCriticaldamageMin":
                                io.addStatValue(level, BaseStat.minCd, value);
                                break;
                            case "incCriticaldamageMax":
                                io.addStatValue(level, BaseStat.maxCd, value);
                                break;
                            case "incPADlv":
                                io.addStatValue(level, BaseStat.padLv, value / 10D);
                                break;
                            case "incMADlv":
                                io.addStatValue(level, BaseStat.madLv, value / 10D);
                                break;
                            case "incMHPlv":
                                io.addStatValue(level, BaseStat.mhpLv, value / 10D);
                                break;
                            case "incMMPlv":
                                io.addStatValue(level, BaseStat.mmpLv, value / 10D);
                                break;
                            case "prop":
                                io.addMiscValue(level, ItemOption.ItemOptionType.prop, value);
                                break;
                            case "face":
                                io.addMiscValue(level, ItemOption.ItemOptionType.face, value);
                                break;
                            case "time":
                                io.addMiscValue(level, ItemOption.ItemOptionType.time, value);
                                break;
                            case "HP":
                                io.addMiscValue(level, ItemOption.ItemOptionType.hpRecoveryOnHit, value);
                                break;
                            case "MP":
                                io.addMiscValue(level, ItemOption.ItemOptionType.mpRecoveryOnHit, value);
                                break;
                            case "attackType":
                                io.addMiscValue(level, ItemOption.ItemOptionType.attackType, value);
                                break;
                            case "level":
                                io.addMiscValue(level, ItemOption.ItemOptionType.level, value);
                                break;
                            case "ignoreDAM":
                                io.addMiscValue(level, ItemOption.ItemOptionType.ignoreDam, value);
                                break;
                        }
                    }

                }
            }
            if (io.getWeight() == 0) {
                io.setWeight(1);
            }
            getItemOptions().put(io.getId(), io);
        }
    }

    public static Map<Integer, ItemOption> getItemOptions() {
        return itemOptions;
    }

    public static ItemOption getItemOptionById(int id) {
        return itemOptions.getOrDefault(id, null);
    }

    public static void saveItemOptions(String dir) {
        File file = new File(String.format("%s/itemOptions.dat", dir));
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.writeInt(getItemOptions().size());
            for (ItemOption io : getItemOptions().values()) {
                dos.writeInt(io.getId());
                dos.writeInt(io.getOptionType());
                dos.writeInt(io.getWeight());
                dos.writeInt(io.getReqLevel());
                dos.writeUTF(io.getString());
                dos.writeShort(io.getStatValuesPerLevel().size());
                for (Map.Entry<Integer, Map<BaseStat, Double>> entry1 : io.getStatValuesPerLevel().entrySet()) {
                    dos.writeInt(entry1.getKey());
                    dos.writeShort(entry1.getValue().size());
                    for (Map.Entry<BaseStat, Double> entry2 : entry1.getValue().entrySet()) {
                        dos.writeInt(entry2.getKey().ordinal());
                        dos.writeDouble(entry2.getValue());
                    }
                }
                dos.writeShort(io.getMiscValuesPerLevel().size());
                for (Map.Entry<Integer, Map<ItemOption.ItemOptionType, Integer>> entry1 : io.getMiscValuesPerLevel().entrySet()) {
                    dos.writeInt(entry1.getKey());
                    dos.writeShort(entry1.getValue().size());
                    for (Map.Entry<ItemOption.ItemOptionType, Integer> entry2 : entry1.getValue().entrySet()) {
                        dos.writeInt(entry2.getKey().ordinal());
                        dos.writeInt(entry2.getValue());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Loader(varName = "itemOptions")
    public static void loadItemOptions(File file, boolean exists) {
        if (!exists) {
            loadItemOptionsFromWZ();
            saveItemOptions(ServerConstants.DAT_DIR);
        } else {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
                int size = dis.readInt();
                for (int i = 0; i < size; i++) {
                    ItemOption io = new ItemOption();
                    io.setId(dis.readInt());
                    io.setOptionType(dis.readInt());
                    io.setWeight(dis.readInt());
                    io.setReqLevel(dis.readInt());
                    io.setString(dis.readUTF());
                    short size2 = dis.readShort();
                    for (int j = 0; j < size2; j++) {
                        int level = dis.readInt();
                        short size3 = dis.readShort();
                        for (int k = 0; k < size3; k++) {
                            io.addStatValue(level, BaseStat.values()[dis.readInt()], dis.readDouble());
                        }
                    }
                    size2 = dis.readShort();
                    for (int j = 0; j < size2; j++) {
                        int level = dis.readInt();
                        short size3 = dis.readShort();
                        for (int k = 0; k < size3; k++) {
                            io.addMiscValue(level, ItemOption.ItemOptionType.values()[dis.readInt()], dis.readInt());
                        }
                    }
                    getItemOptions().put(io.getId(), io);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused") // Reflection
    public static void generateDatFiles() {
        log.info("Started generating item data.");
        long start = System.currentTimeMillis();
        loadMountItemsFromFile();
        loadEquipsFromWz();
        loadItemsFromWZ();
        loadPetsFromWZ();
        loadItemOptionsFromWZ();
        QuestData.linkItemData();
        saveEquips(ServerConstants.DAT_DIR + "/equips");
        saveItems(ServerConstants.DAT_DIR + "/items");
        savePets(ServerConstants.DAT_DIR + "/pets");
        saveItemOptions(ServerConstants.DAT_DIR);
        log.info(String.format("Completed generating item data in %dms.", System.currentTimeMillis() - start));
    }

    public static void main(String[] args) {
        generateDatFiles();
    }

    public static Map<Integer, ItemInfo> getItems() {
        return items;
    }

    public static void addItemInfo(ItemInfo ii) {
        getItems().put(ii.getItemId(), ii);
    }

    private static Map<Integer, PetInfo> getPets() {
        return pets;
    }

    public static void addPetInfo(PetInfo pi) {
        getPets().put(pi.getItemID(), pi);
    }

    public static void clear() {
        getEquips().clear();
        getItems().clear();
        getItemOptions().clear();
    }
}
