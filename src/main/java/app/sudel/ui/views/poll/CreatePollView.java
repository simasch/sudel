package app.sudel.ui.views.poll;

import app.sudel.domain.poll.PollEntity;
import app.sudel.domain.poll.PollService;
import app.sudel.ui.views.MainLayout;
import app.sudel.ui.views.components.SudelNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendarScheduler;
import org.vaadin.stefan.fullcalendar.ResourceEntry;
import org.vaadin.stefan.fullcalendar.Timezone;
import org.vaadin.stefan.fullcalendar.dataprovider.EagerInMemoryEntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@AnonymousAllowed
@RouteAlias(value = "", layout = MainLayout.class)
@Route(layout = MainLayout.class)
public class CreatePollView extends VerticalLayout implements HasDynamicTitle {

    private final FullCalendarScheduler calendar;
    private final VirtualList<Entry> entryList;
    private final List<Entry> entries = new ArrayList<>();

    private EagerInMemoryEntryProvider<Entry> entryProvider;

    public CreatePollView(PollService pollService) {
        setId("create-view");

        calendar = createCalendar();

        Binder<PollEntity> binder = new Binder<>(PollEntity.class);

        FormLayout toolbar = createToolbar();

        TextField name = new TextField(getTranslation("event.name"));
        binder.forField(name)
                .asRequired()
                .withValidator(new StringLengthValidator(getTranslation("name.required"), 1, 200))
                .bind(PollEntity::getName, PollEntity::setName);

        TextField location = new TextField(getTranslation("event.location"));
        binder.forField(location).bind(PollEntity::getLocation, PollEntity::setLocation);

        TextArea details = new TextArea(getTranslation("event.details"));
        binder.forField(details).bind(PollEntity::getDescription, PollEntity::setDescription);

        binder.setBean(new PollEntity());

        Button createPoll = new Button(getTranslation("create.poll"), e -> {
            PollEntity pollEntity = binder.getBean();
            pollEntity.setCalendarEntries(entryProvider.getEntries());

            if (pollEntity.getCalendarEntries().isEmpty()) {
                SudelNotification.showError("dates.required");
            }
            if (binder.validate().isOk() && !pollEntity.getCalendarEntries().isEmpty()) {
                pollService.createPoll(pollEntity);

                SudelNotification.showSuccess("poll.created");
            }
        });

        Div calenderWithToolbar = new Div(toolbar, calendar);
        calenderWithToolbar.setHeightFull();
        calenderWithToolbar.setWidth("80%");

        entryList = new VirtualList<>();
        entryList.setWidthFull();
        entryList.setRenderer(new EntryRenderer(this::removeEntry));
        entryList.setItems(entries);
        entryList.setWidth("20%");
        entryList.setHeightFull();

        HorizontalLayout calenderLayout = new HorizontalLayout(calenderWithToolbar, entryList);
        calenderLayout.setSizeFull();

        add(new FormLayout(name, location, details, createPoll), calenderLayout);
    }

    private FullCalendarScheduler createCalendar() {
        FullCalendarScheduler calendar = new FullCalendarScheduler();
        calendar.setSizeFull();
        calendar.setNowIndicatorShown(true);
        calendar.setTimezone(Timezone.getSystem());
        calendar.setLocale(UI.getCurrent().getLocale());
        calendar.setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");

        calendar.setFirstDay(DayOfWeek.MONDAY);
        calendar.changeView(SudelCalendarView.TIME_GRID_WEEK);

        entryProvider = EntryProvider.eagerInMemory();
        calendar.setEntryProvider(entryProvider);

        calendar.addEntryClickedListener(event -> {
            if (event.getEntry().getRenderingMode() != Entry.RenderingMode.BACKGROUND
                    && event.getEntry().getRenderingMode() != Entry.RenderingMode.INVERSE_BACKGROUND) {
                new CalendarEntryDialog(event.getEntry(), entry -> {
                    entryProvider.updateEntry(entry);
                    entryList.getDataProvider().refreshAll();
                }, this::removeEntry).open();
            }
        });

        calendar.addTimeslotsSelectedSchedulerListener(event -> {
            ResourceEntry entry = new ResourceEntry();
            entry.setStart(event.getStart());
            if (event.isAllDay()) {
                entry.setEnd(event.getStart());
            } else {
                entry.setEnd(event.getEnd());
            }
            entry.setAllDay(event.isAllDay());

            addEntry(entry);
        });

        calendar.addEntryResizedListener(event -> {
            event.getEntry().setEnd(event.getDelta().applyOn(event.getEntry().getEnd()));
            entryList.getDataProvider().refreshAll();
        });

        calendar.addEntryDroppedSchedulerListener(event -> {
            Entry entry = event.applyChangesOnEntry();
            entryProvider.updateEntry(entry);

            entryList.getDataProvider().refreshAll();
        });

        return calendar;
    }

    private void addEntry(Entry entry) {
        entries.add(entry);
        entryList.getDataProvider().refreshAll();
        entryProvider.addEntry(entry);
    }

    private void removeEntry(Entry entry) {
        entries.remove(entry);
        entryList.getDataProvider().refreshAll();
        entryProvider.removeEntry(entry);
    }

    private FormLayout createToolbar() {
        Button buttonPrevious = new Button(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
        Button buttonToday = new Button(VaadinIcon.HOME.create(), e -> calendar.today());
        Button buttonNext = new Button(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        buttonNext.setIconAfterText(true);

        DatePicker gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
        gotoDate.getElement().getStyle().set("visibility", "hidden");
        gotoDate.getElement().getStyle().set("position", "fixed");
        gotoDate.setWeekNumbersVisible(true);

        DatePicker.DatePickerI18n datePickerI18n = new DatePicker.DatePickerI18n();
        DateFormatSymbols symbols = new DateFormatSymbols(UI.getCurrent().getLocale());
        datePickerI18n.setMonthNames(Arrays.asList(symbols.getMonths()));
        datePickerI18n.setFirstDayOfWeek(1);
        datePickerI18n.setWeekdays(Arrays.stream(symbols.getWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        datePickerI18n.setWeekdaysShort(Arrays.stream(symbols.getShortWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        gotoDate.setI18n(datePickerI18n);

        Button buttonDatePicker = new Button("", VaadinIcon.CALENDAR.create());
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());

        Select<SudelCalendarView> select = new Select<>();
        select.setWidthFull();
        select.setItems(List.of(SudelCalendarView.values()));
        select.setValue(SudelCalendarView.TIME_GRID_WEEK);
        select.setItemLabelGenerator(sudelCalendarView -> getTranslation(sudelCalendarView.getName()));
        select.addValueChangeListener(e -> calendar.changeView(e.getValue()));

        FormLayout toolbar = new FormLayout(buttonToday, buttonPrevious, buttonNext, buttonDatePicker, select);

        toolbar.getElement().getStyle().set("margin-top", "0px");
        toolbar.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("25em", 5));

        return toolbar;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("find.date");
    }

    public enum SudelCalendarView implements CalendarView {

        TIME_GRID_WEEK("timeGridWeek", "Week"),
        DAY_GRID_MONTH("dayGridMonth", "Month");

        private final String clientSideValue;
        private final String name;

        SudelCalendarView(String clientSideValue, String name) {
            this.clientSideValue = clientSideValue;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getClientSideValue() {
            return clientSideValue;
        }
    }

}
