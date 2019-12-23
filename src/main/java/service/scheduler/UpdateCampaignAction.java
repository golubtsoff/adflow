package service.scheduler;

import service.CampaignService;

public class UpdateCampaignAction implements Runnable {

    @Override
    public void run() {
        CampaignService.updateCampaignActionPauseToRun();
    }
}
