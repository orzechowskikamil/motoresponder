package com.medziku.motoresponder.redux;

import com.google.common.collect.ImmutableList;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters

public interface State {
    GPS gps();

    Accelerometer accelerometer();

    RespondingProcesses respondingProcesses();

    Settings settings();

    Proximity proximity();

    @Value.Immutable
    interface Proximity {
        boolean proximity();
    }

    @Value.Immutable
    interface GPS {
        ImmutableList<LocationUpdate> updatesList();

        boolean isEnabled();

        @Value.Immutable
        interface LocationUpdate {
        }
    }

    @Value.Immutable
    interface Accelerometer {
        ImmutableList<AccelerometerEvent> eventsList();

        boolean isEnabled();

        @Value.Immutable
        interface AccelerometerEvent {
        }
    }

    @Value.Immutable
    interface RespondingProcesses {
        ImmutableList<RespondingProcess> list();

        int nextId();


        interface RespondingProcess {
            String phoneNumber();

            int id();
        }

        @Value.Immutable
        interface CallRespondingProcess extends RespondingProcess {
        }

        @Value.Immutable
        interface MessageRespondingProcess extends RespondingProcess {
            String message();
        }
    }


    @Value.Immutable
    interface Settings {
        boolean isEnabled();
    }

    @Value.Immutable
    interface Contacts {
    }
}


class Default {
    public static State build() {
        return ImmutableState.builder()
                .gps(ImmutableGPS.builder()
                        .locationUpdates(new ImmutableList<LocationUpdate>())
                        .isEnabled(false)
                        .build())
                .accelerometer(ImmutableAccelerometer.builder()
                        .eventsList(new ImmutableList<AccelerometerEvent>())
                        .isEnabled(false)
                        .build())
                .respondingProcesses(ImmutableRespondingProcesses.builder()
                        .list(new ImmutableList<RespondingProcess>())
                        .nextId(0)
                ).build()
                .settings(ImmutableSettings.builder()
                        .isEnabled(false)
                        .build()
                ).build();
    }
}
