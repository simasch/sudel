package app.sudel.domain.poll;

import app.sudel.db.tables.records.PollDateRecord;
import app.sudel.db.tables.records.PollRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.stefan.fullcalendar.Entry;

import static app.sudel.db.tables.Poll.POLL;
import static app.sudel.db.tables.PollDate.POLL_DATE;

@Service
public class PollService {

    private final DSLContext ctx;

    public PollService(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Transactional
    public void createPoll(PollEntity pollEntity) {
        PollRecord pollRecord = ctx.newRecord(POLL);
        pollRecord.setName(pollEntity.getName());
        pollRecord.setLocation(pollEntity.getLocation());
        pollRecord.setDescription(pollEntity.getDescription());
        pollRecord.store();

        for (Entry calendarEntry : pollEntity.getCalendarEntries()) {
            PollDateRecord pollDateRecord = ctx.newRecord(POLL_DATE);
            pollDateRecord.setStartsAt(calendarEntry.getStart());
            pollDateRecord.setEndsAt(calendarEntry.getEnd());
            pollDateRecord.setAllDay(calendarEntry.isAllDay());
            pollDateRecord.setPollId(pollRecord.getId());
            pollDateRecord.store();
        }
    }

}