package app.sudel.domain.poll;

import org.vaadin.stefan.fullcalendar.Entry;

import java.util.List;

public class PollEntity {

    private String name;
    private String location;
    private String description;
    private List<Entry> calendarEntries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Entry> getCalendarEntries() {
        return calendarEntries;
    }

    public void setCalendarEntries(List<Entry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }
}
