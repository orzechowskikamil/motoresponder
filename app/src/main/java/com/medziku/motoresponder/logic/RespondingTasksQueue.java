package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.pseudotesting.integration.RespondingDecisionIntegrationTest;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.ArrayList;
import java.util.List;

public class RespondingTasksQueue {
    protected List<RespondingTask> pendingRespondingTasks;
    private RespondingDecision respondingDecision;
    private Settings settings;
    private NotificationUtility notificationUtility;
    private SMSUtility smsUtility;
    private ResponsePreparator responsePreparator;
    private DecisionLog log;

    public RespondingTasksQueue(NotificationUtility notificationUtility,
                                SMSUtility smsUtility,
                                Settings settings,
                                RespondingDecision respondingDecision,
                                ResponsePreparator responsePreparator,
                                DecisionLog log) {
        this.pendingRespondingTasks = new ArrayList<>();
        this.notificationUtility = notificationUtility;
        this.smsUtility = smsUtility;
        this.settings = settings;
        this.respondingDecision = respondingDecision;
        this.responsePreparator = responsePreparator;
        this.log = log;
    }


    protected RespondingTask createRespondingTask(Predicate<Boolean> resultCallback) {
        return new RespondingTask(
                this.respondingDecision,
                this.settings,
                this.notificationUtility,
                this.smsUtility,
                this.responsePreparator,
                this.log,
                resultCallback);
    }


    /**
     * This method cancels all currently pending responding tasks, and clean after themselves.
     */
    public void cancelAllHandling() {
        for (RespondingTask task : this.pendingRespondingTasks) {
            task.cancelResponding();
        }
        this.pendingRespondingTasks.clear();
    }


    public void createAndExecuteRespondingTask(RespondingSubject subject) {
        final RespondingTask[] task = new RespondingTask[1];

        task[0] = this.createRespondingTask(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RespondingTasksQueue.this.pendingRespondingTasks.remove(task[0]);
                return true;
            }
        });

        this.pendingRespondingTasks.add(task[0]);
        task[0].execute(subject);
    }

}
