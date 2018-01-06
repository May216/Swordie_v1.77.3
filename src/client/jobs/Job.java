package client.jobs;

import client.Client;
import client.character.skills.AttackInfo;
import client.character.skills.SkillInfo;
import connection.InPacket;
import loaders.SkillData;

/**
 * Created on 1/2/2018.
 */
public abstract class Job {
    public abstract void handleAttack(Client c, AttackInfo attackInfo);

    public abstract void handleSkill(Client c, InPacket inPacket);

    public abstract boolean isHandlerOfJob(short id);

    public SkillInfo getInfo(int skillID) {
        return SkillData.getSkillInfoById(skillID);
    }
}
