# Hidden Street - Ardentmill :: 910001000
# Intaglio :: Master of 飾品製作 :: 9031004

MINING_SKILL = 92010000
SMITHING_CRAFT_SKILL = 92020000
ACCESSORY_CRAFT_SKILL = 92030000
ALCHEMY_CRAFT_SKILL = 92040000
FEE = [5000, 15000, 25000, 40000, 60000, 85000, 115000, 150000, 190000, 235000]

if not sm.hasSkill(ACCESSORY_CRAFT_SKILL):
    selection = sm.sendSay(u"輝煌的光芒！結晶純度！棱鏡美！我的朋友，你也是鑽石商嗎？我們應該一起尋找飾品製作的奧秘嗎？\r\n#L0#聽聽關於#b#e飾品製作#n的說明#l\r\n#L1##r學習#e飾品製作#n技術。#k#l")
    if selection == 0:
        sm.sendNext(u"從哪裡開始？我可以告訴您一些煉金術的奧秘，但是...這可能需要整夜時間。\r\n簡而言之，飾品製作是採用生鑽石或礦物將其塑造直到其真正的美麗閃耀為止。即使是最粗糙的寶石也有能力變得優雅且具有強大的力量。")
    elif selection == 1:
        if not sm.hasSkill(MINING_SKILL):
            sm.sendSayOkay(u"您需要先向#bcole#k學習採礦才能學習飾品製作。他將教您如何獲取所有您需要製作閃閃發光的配飾所需的礦物和鑽石。")
            sm.dispose()

        if sm.hasSkill(SMITHING_CRAFT_SKILL) or sm.hasSkill(ALCHEMY_CRAFT_SKILL):
            sm.sendNext(u"如果您已經學會了採藥和煉金術便不能學飾品製作，如果你想學習飾品製作的話...您需要先放棄其中一項技術！")
            sm.dispose()

        learn = sm.sendAskYesNo(
            u"你準備好學習#b飾品製作#k了嗎？\r\n既然您這麼可愛，我會給您折扣。#b5,000 楓幣#k成為我的學生。\r\n")
        if learn:
            if sm.getMesos() < 5000:
                sm.sendNext(
                    u"您沒有#B 5000 楓幣＃K？我不能免費教你。")
                sm.dispose()

            sm.giveMesos(-5000)
            sm.giveSkill(ACCESSORY_CRAFT_SKILL, 0x1000000, 13)
            sm.playSound("profession/levelup")
            sm.sendNext(u"練習，練習，練習，當您獲得足夠的掌握時，我會教您更多。")
        else:
            sm.sendNext(
                u"什麼？為什麼不？！我期待與您分享我的知識！")
else:
    selection = sm.sendSay(u"輝煌的光芒！結晶純度！棱鏡美！我的朋友，你也是鑽石商嗎？我們應該一起尋求飾品製作奧秘吧！\r\n#L2##b提升#e飾品製作#n等級#l\r\n#L3#放棄飾品製作技術#k#l")
    if selection == 2:
        if sm.isAbleToLevelUpMakingSkill(ACCESSORY_CRAFT_SKILL):
            levelup = sm.sendAskYesNo(u"看起來您準備好升級飾品製作。我會收取 #b" + str(
                FEE[sm.getMakingSkillLevel(ACCESSORY_CRAFT_SKILL)]) + " 楓幣#k作為學費。準備好生及了嗎？")
            if levelup:
                if sm.getMesos() < FEE[sm.getMakingSkillLevel(ACCESSORY_CRAFT_SKILL)]:
                    sm.sendNext(u"您沒有足夠的楓幣。")
                    sm.dispose()
                sm.giveMesos(-FEE[sm.getMakingSkillLevel(ACCESSORY_CRAFT_SKILL)])
                sm.makingSkillLevelUp(ACCESSORY_CRAFT_SKILL)
                sm.sendNext(u"您的飾品製作現在是LV." +
                            str(sm.getMakingSkillLevel(ACCESSORY_CRAFT_SKILL)) + ".")
            else:
                sm.sendNext(
                    u"謹慎是很好的，您考慮一下之後再來找我。")
                sm.dispose()
        else:
            sm.sendNext(
                u"您還沒有達到提升技術等級的條件！")
    elif selection == 3:
        unlearn = sm.sendAskYesNo(
            u"您想放棄您的飾品製作技術？你已經厭倦了我嗎？您為提高水平和精通的所有努力都將丟失...所有這些...努力...都會消失...您真的打算這樣做嗎？")
        if unlearn:
            sm.removeSkill(ACCESSORY_CRAFT_SKILL)
            # set quest value 11496, "0"
            # start quest 3263
            # complete quest 3263
            # remove quest 3263
            sm.sendNext(
                u"它已經重置了...您真冷靜...但是如果您改變主意，我會在這裡等你。")
        else:
            sm.sendSayOkay(u"哦，謝謝，謝謝，謝謝！")
