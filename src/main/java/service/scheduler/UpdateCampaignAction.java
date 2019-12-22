package service.scheduler;

public class UpdateCampaignAction implements Runnable {

    @Override
    public void run() {
        // Do your hourly job here.
        System.out.println("Job trigged by scheduler");
    }
}
