package main;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class SimpleQuartzJob implements Job {

    public SimpleQuartzJob(){}

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("In SimpleQuartzJob - executing its JOB at "
                + new Date() + " by " + jobExecutionContext.getTrigger().getJobKey());
    }
}
