package app.sudel.service.poll;

import app.sudel.db.tables.records.PollDateRecord;
import app.sudel.db.tables.records.PollParticipantRecord;
import app.sudel.db.tables.records.PollRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.stefan.fullcalendar.Entry;

import java.util.List;

import static app.sudel.db.tables.Poll.POLL;
import static app.sudel.db.tables.PollDate.POLL_DATE;
import static app.sudel.db.tables.PollParticipant.POLL_PARTICIPANT;

@Service
public class PollService {

    private final DSLContext ctx;

    public PollService(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Transactional
    public void createPoll(String name, String location, String description, String participants, List<Entry> calendarEntries) {
        PollRecord poll = ctx.newRecord(POLL);
        poll.setName(name);
        poll.setLocation(location);
        poll.setDescription(description);
        poll.store();

        if (participants.contains(";")) {
            for (String participant : participants.split(";")) {
                createPollParticipant(poll, participant);
            }
        } else {
            createPollParticipant(poll, participants);
        }

        for (Entry calendarEntry : calendarEntries) {
            PollDateRecord pollDate = ctx.newRecord(POLL_DATE);
            pollDate.setStartsAt(calendarEntry.getStart());
            pollDate.setEndsAt(calendarEntry.getEnd());
            pollDate.setAllDay(calendarEntry.isAllDay());
            pollDate.setPollId(poll.getId());
            pollDate.store();
        }
    }

    private void createPollParticipant(PollRecord poll, String participant) {
        PollParticipantRecord pollParticipant = ctx.newRecord(POLL_PARTICIPANT);
        pollParticipant.setEmail(participant);
        pollParticipant.setPollId(poll.getId());
        pollParticipant.store();
    }
}
