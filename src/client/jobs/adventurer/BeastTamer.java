package client.jobs.adventurer;

import client.Client;
import client.character.skills.AttackInfo;
import client.jobs.Job;
import connection.InPacket;
import constants.JobConstants;

/**
 * Created on 12/14/2017.
 */
public class BeastTamer extends Job {
    @Override
    public void handleAttack(Client c, AttackInfo attackInfo) {

    }

    @Override
    public void handleSkill(Client c, InPacket inPacket) {

    }

    @Override
    public boolean isHandlerOfJob(short id) {
        JobConstants.JobEnum job = JobConstants.JobEnum.getJobById(id);
        switch (job) {
            case BEAST_TAMER_1:
            case BEAST_TAMER_2:
            case BEAST_TAMER_3:
            case BEAST_TAMER_4:
                return true;
            default:
                return false;
        }
    }
}
