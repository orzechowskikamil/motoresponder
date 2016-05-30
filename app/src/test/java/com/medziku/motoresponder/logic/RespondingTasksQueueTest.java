package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RespondingTasksQueueTest {

    private ExposedRespondingTasksQueue exposedRespondingTasksQueue;
    private String FAKE_PHONE_NUMBER = "777777777";

    @Before
    public void setUp() throws Exception {
        this.exposedRespondingTasksQueue = new ExposedRespondingTasksQueue();
    }


    @Test
    public void testAddingTask() {
        this.exposedRespondingTasksQueue.createAndExecuteRespondingTask(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        RespondingTask lastTask = this.exposedRespondingTasksQueue.lastMockedRespondingTask;

        assertEquals(1, this.exposedRespondingTasksQueue.getTasksList().size());

        this.exposedRespondingTasksQueue.lastResultCallback.apply(true);

        assertEquals(1, this.exposedRespondingTasksQueue.getTasksList().size());

        verify(lastTask, times(0)).cancelResponding();
        verify(lastTask, times(1)).execute(any(CallRespondingSubject.class));

    }

    @Test
    public void testOfAddingTwoTasks() {
        this.exposedRespondingTasksQueue.createAndExecuteRespondingTask(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        assertEquals(1, this.exposedRespondingTasksQueue.getTasksList().size());
        Predicate<Boolean> firstCallback = this.exposedRespondingTasksQueue.lastResultCallback;

        this.exposedRespondingTasksQueue.createAndExecuteRespondingTask(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        assertEquals(2, this.exposedRespondingTasksQueue.getTasksList().size());
        Predicate<Boolean> secondCallback = this.exposedRespondingTasksQueue.lastResultCallback;

        RespondingTask lastTask = this.exposedRespondingTasksQueue.lastMockedRespondingTask;

        firstCallback.apply(true);
        secondCallback.apply(true);

        assertEquals(2, this.exposedRespondingTasksQueue.getTasksList().size());
        verify(lastTask, times(0)).cancelResponding();
        verify(lastTask, times(1)).execute(any(CallRespondingSubject.class));
    }

    @Test
    public void testOfCancellingTasks() {
        this.exposedRespondingTasksQueue.createAndExecuteRespondingTask(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        this.exposedRespondingTasksQueue.createAndExecuteRespondingTask(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        this.exposedRespondingTasksQueue.createAndExecuteRespondingTask(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        RespondingTask firstTask = this.exposedRespondingTasksQueue.getTasksList().get(0);
        RespondingTask secondTask = this.exposedRespondingTasksQueue.getTasksList().get(1);
        RespondingTask thirdTask = this.exposedRespondingTasksQueue.getTasksList().get(2);

        this.exposedRespondingTasksQueue.cancelAllHandling();

        verify(firstTask, times(1)).cancelResponding();
        verify(secondTask, times(1)).cancelResponding();
        verify(thirdTask, times(1)).cancelResponding();
        assertTrue(this.exposedRespondingTasksQueue.pendingRespondingTasks.size() == 0);

    }


}

class ExposedRespondingTasksQueue extends RespondingTasksQueue {


    public Predicate<Boolean> lastResultCallback;
    public RespondingTask lastMockedRespondingTask;


    public ExposedRespondingTasksQueue() {
        super(null, null, null, null, null, null, null, null);
    }

    @Override
    protected RespondingTask createRespondingTask(Predicate<Boolean> resultCallback) {
        this.lastResultCallback = resultCallback;
        RespondingTask mock = mock(RespondingTask.class);
        this.lastMockedRespondingTask = mock;
        return mock;
    }

    public List<RespondingTask> getTasksList() {
        return this.pendingRespondingTasks;
    }
}