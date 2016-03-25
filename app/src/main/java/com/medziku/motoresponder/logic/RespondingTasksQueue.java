package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import com.medziku.motoresponder.utils.SettingsUtility;

import java.util.ArrayList;
import java.util.List;

public class RespondingTasksQueue {
    protected List<RespondingTask> pendingRespondingTasks;
    private RespondingDecision respondingDecision;
    private SettingsUtility settingsUtility;
    private NotificationUtility notificationUtility;
    private SMSUtility smsUtility;
    private ResponsePreparator responsePreparator;

    public RespondingTasksQueue(NotificationUtility notificationUtility,
                                SMSUtility smsUtility,
                                SettingsUtility settingsUtility,
                                RespondingDecision respondingDecision,
                                ResponsePreparator responsePreparator) {
        this.pendingRespondingTasks = new ArrayList<>();
        this.notificationUtility = notificationUtility;
        this.smsUtility = smsUtility;
        this.settingsUtility = settingsUtility;
        this.respondingDecision = respondingDecision;
        this.responsePreparator = responsePreparator;
    }


    protected RespondingTask createRespondingTask(Predicate<Boolean> resultCallback) {
        return new RespondingTask(
                this.respondingDecision,
                this.settingsUtility,
                this.notificationUtility,
                this.smsUtility,
                this.responsePreparator,
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
