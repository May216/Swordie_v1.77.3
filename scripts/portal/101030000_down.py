# Portal to enter Ellinel Fairy Academy

FANZY = 1040002 # NPC ID
FAIRYNAPPERS = 32101 # QUEST ID
MIDSUMMER_NIGHTS_FOREST_PATH_TO_ELLINEL = 101074000 # MAP ID

def init():
    sm.setSpeakerID(FANZY)

    if sm.hasQuest(FAIRYNAPPERS) or sm.hasQuestCompleted(FAIRYNAPPERS):
        sm.sendAskYesNo("Would you like to enter #b [Theme Dungeon: Ellinel Fairy Academy]#k?")
    else:
        sm.sendSayOkay("We still have a business to take care of, remember?\r\n\r\n#b (You must talk to Fanzy and complete his quests to enter.)#k")
        sm.dispose()

def action(response, answer):

        if response == 1:
            sm.warp(MIDSUMMER_NIGHTS_FOREST_PATH_TO_ELLINEL)
        sm.dispose()