package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.ArrayList;
import java.util.List;

public class RespondingTasksQueue {
    protected List<RespondingTask> pendingRespondingTasks;
    private ContactsUtility contactsUtility;
    private RespondingDecision respondingDecision;
    private Settings settings;
    private SMSUtility smsUtility;
    private ResponsePreparator responsePreparator;
    private CustomLog log;
    private LockStateUtility lockStateUtility;
    private NotificationFactory notificationFactory;

    public RespondingTasksQueue(NotificationFactory notificationFactory,
                                SMSUtility smsUtility,
                                ContactsUtility contactsUtility,
                                LockStateUtility lockStateUtility,
                                Settings settings,
                                RespondingDecision respondingDecision,
                                ResponsePreparator responsePreparator,
                                CustomLog log) {
        this.pendingRespondingTasks = new ArrayList<>();
        this.smsUtility = smsUtility;
        this.notificationFactory = notificationFactory;
        this.settings = settings;
        this.respondingDecision = respondingDecision;
        this.responsePreparator = responsePreparator;
        this.log = log;
        this.lockStateUtility = lockStateUtility;
        this.contactsUtility = contactsUtility;
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
                // removal from task queue can cause concurrentModificationException, because if someone unlock phone
                // and pendingRespondingTasks will be iterated for cancelling any task, and cancelling task
                // will call this callback which will try to remove task from queue, there will be concurrent modification
                // exception. So better to leave task in queue with isFinished() == true state. it doesn't harm
                // anything, and it will be eventually removed with next call of cancelAllHandling.
                //
                // RespondingTasksQueue.this.pendingRespondingTasks.remove(task[0]);
                return true;
            }
        });

        this.pendingRespondingTasks.add(task[0]);
        task[0].execute(subject);
    }

    protected RespondingTask createRespondingTask(Predicate<Boolean> resultCallback) {
        return new RespondingTask(
                this.respondingDecision,
                this.settings,
                this.notificationFactory,
                this.smsUtility,
                this.contactsUtility,
                this.lockStateUtility,
                this.responsePreparator,
                this.log,
                resultCallback);
    }

}
