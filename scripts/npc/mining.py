# Hidden Street - Ardentmill :: 910001000
# Gere :: Master of Mining :: 9031002

MINING_SKILL = 92010000
FEE = [5000, 15000, 25000, 40000, 60000, 85000, 115000, 150000, 190000, 235000]

if not sm.hasSkill(MINING_SKILL):
    selection = sm.sendSay(u"現在我能為你做什麼？\r\n#L0##b聽聽關於#e採礦#n的說明#l\r\n#L1##r學習#e採礦#n技術#k#l")
    if selection == 0:
        sm.sendNext(u"如果您想採集一些礦物，那麼您只需要學習採礦技術。使用模具和礦物來製作各種有用的物品。")
    elif selection == 1:
        learn = sm.sendAskYesNo(u"您真的想學習#b採礦#k嗎？這將花費您#b 5,000 楓幣#k。\r\n")
        if learn:
            if sm.getMesos() < 5000:
                sm.sendNext(u"您沒有足夠的楓幣。我需要收取#b5000 楓幣#k。")
                sm.dispose();

            sm.giveMesos(-5000)
            sm.giveSkill(MINING_SKILL, 0x1000000, 10)
            sm.playSound("profession/levelup")
            sm.sendNext(u"好的，這些是採礦的基礎知識。為使您更加精通採礦技術，我會教您一些新技巧。")
        else:
            sm.sendNext(u"謹慎是很好的，您考慮一下之後再來找我。")
else:
    selection = sm.sendSay(u"現在你要做什麼？\r\n#L2##b升級#e採礦#n#l\r\n#L3##b交易#t4011010#.#k#l")
    if selection == 2:
        if sm.isAbleToLevelUpMakingSkill(MINING_SKILL):
            levelup = sm.sendAskYesNo(u"看起來您準備好升級採礦技術了。我會收取#b" + str(FEE[sm.getMakingSkillLevel(MINING_SKILL)]) + " 楓幣#k作為學費。準備學習了嗎？")
            if levelup:
                if sm.getMesos() < FEE[sm.getMakingSkillLevel(MINING_SKILL)]:
                    sm.sendNext(u"您沒有足夠的楓幣。")
                    sm.dispose()
                sm.giveMesos(-FEE[sm.getMakingSkillLevel(MINING_SKILL)])
                sm.makingSkillLevelUp(MINING_SKILL)
                sm.sendNext(u"您的採礦技巧現在是LV." + str(sm.getMakingSkillLevel(MINING_SKILL)))
            else:
                sm.sendNext(u"當然，您可以再多花一些時間考慮，我會一直在這。")
                sm.dispose()
        else:
            sm.sendNext(u"您還沒有準備好升級技術，當您更加精通時再回來找我。")
    elif selection == 3:
        sm.sendSayOkay(u"#b100 #t4011010#s#k 可以兌換 1 個#i2028067:##b#t2028067##k。請挖掘更多#t4011010#.")