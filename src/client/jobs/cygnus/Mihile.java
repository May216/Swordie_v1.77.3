package client.jobs.cygnus;

import client.Client;
import client.character.skills.AttackInfo;
import client.jobs.Job;
import connection.InPacket;
import constants.JobConstants;

/**
 * Created on 12/14/2017.
 */
public class Mihile extends Job {
    @Override
    public void handleAttack(Client c, AttackInfo attackInfo) {

    }

    @Override
    public void handleSkill(Client c, InPacket inPacket) {

    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return id >= JobConstants.JobEnum.NAMELESS_WARDEN.getJobId() && id <= JobConstants.JobEnum.MIHILE4.getJobId();
    }
}
