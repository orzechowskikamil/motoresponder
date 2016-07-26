package com.medziku.motoresponder.mocks;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.LockStateUtility;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class LockStateUtilityMock {

    public LockStateUtility mock;
    private Predicate<Boolean> lockStateChangedCallback;

    public LockStateUtilityMock() {
        this.mock = mock(LockStateUtility.class);
        this.setupMock();
    }

    public void simulateLockStateChange(boolean isLocked) {
        this.lockStateChangedCallback.apply(isLocked);
    }

    private void setupMock() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LockStateUtilityMock.this.lockStateChangedCallback = (Predicate<Boolean>) invocation.getArguments()[0];
                return null;
            }
        }).when(this.mock).listenToLockStateChanges(any(Predicate.class));
    }
}
