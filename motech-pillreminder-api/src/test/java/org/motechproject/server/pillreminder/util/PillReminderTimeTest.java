package org.motechproject.server.pillreminder.util;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PillReminderTimeTest {

    private PillReminderTime pillReminderTime;

    @Before
    public void setUp() {
        pillReminderTime = new PillReminderTime();
    }
    @Test
    public void testTimesPillRemindersSent() {
        DateTime oneHourEarlier = new DateTime().minusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(oneHourEarlier.getHourOfDay(), oneHourEarlier.getMinuteOfHour()));
        assertEquals(6, pillReminderTime.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testTimesPillRemindersSentWhenDosageStartTimeLaterThanNow() {
//        TODO : test fails when run between 11:00 and 12:00 pm
        DateTime oneHourLater = new DateTime().plusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourLater = new Dosage();
        dosageStaringOneHourLater.setStartTime(new Time(oneHourLater.getHourOfDay(), oneHourLater.getMinuteOfHour()));
        assertEquals(0, pillReminderTime.timesPillRemindersSent(dosageStaringOneHourLater, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testTimesPillRemainderWillBeSent() {
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        assertEquals(12, pillReminderTime.timesPillRemainderWillBeSent(twoHourWindow, tenMinuteInterval));
    }
}
