# 22008 |   Chasing away the foxes (Evan intro)
sm.setSpeakerID(1013101)
sm.sendNext("Did you defeat the Treacherous Foxes?")
sm.setPlayerAsSpeaker
sm.sendNext("#bWhat happened to slaying the Foxes left behind?")
sm.setSpeakerID(1013101)
sm.sendNext("Oh that? Haha, I did chase them, sort of, But I wanted to make sure they did not catch up to you. I wouldn't want you eaten by a Treacherous Fox or anything. So I just let them be.")
sm.setPlayerAsSpeaker
sm.sendNext("#bAre you sure you weren't just hiding because you were scared of the Foxes?")
sm.setSpeakerID(1013101)
sm.sendNext("What? No way! Sheeesh, I fear nothing!")
sm.setPlayerAsSpeaker
sm.sendNext("#bWatch out! There's a Treacherous Fox right behind you!")
sm.setSpeakerID(1013101)
sm.sendNext("Eeeek! Mommy!")
sm.setPlayerAsSpeaker
sm.sendNext("....")
sm.setSpeakerID(1013101)
sm.sendNext("....")
sm.sendNext("You little brat! I'm your older brother. Don't you mess with me! Your brother has a weak heart, you know. Don't surprise me like that!")
sm.setPlayerAsSpeaker()
sm.sendNext("#b(This is why I don't want to call you Older Brother....)")
sm.setSpeakerID(1013101)
sm.sendNext("Hmmph! Anyway, I'm glad you were able to defeat the Treacherous Foxes. As a reward I'll give you something an Adventurer gave me a long time ago. Here you are.  \r\n #fUI/UIWindow2.img/QuestIcon/4/0# \r\n #v1372107# Beginner Magician's Wand \r\n #fUI/UIWindow2.img/QuestIcon/8/0# 910 exp")
if sm.canHold(1372107):
    sm.giveItem(1372107)
    sm.giveExp(910)
    sm.completeQuest(parentID)
    sm.sendNext("#bThis is a weapon that Magicians use. It's a Wand. #k You probably won't really need it, but it'll make you look important if you carry it around. Hahaha")
    sm.sendSay("Anyways, the Foxes have increased, right? How weird is that? Why are they growing day by day? We should really look into it and get to the bottom of this.")
    sm.dispose()
else:
    sm.sendNext("Please make room in your Equip Inventory.")
    sm.dispose()


