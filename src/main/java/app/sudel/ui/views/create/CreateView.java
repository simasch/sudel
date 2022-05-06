package app.sudel.ui.views.create;

import app.sudel.ui.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendarScheduler;
import org.vaadin.stefan.fullcalendar.ResourceEntry;
import org.vaadin.stefan.fullcalendar.Timezone;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@AnonymousAllowed
@RouteAlias(value = "", layout = MainLayout.class)
@Route(layout = MainLayout.class)
public class CreateView extends VerticalLayout implements HasDynamicTitle {

    private final FullCalendarScheduler calendar;

    public CreateView() {
        setId("create-view");

        TextField name = new TextField(getTranslation("event.name"));
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);

        TextField location = new TextField(getTranslation("event.location"));

        TextArea details = new TextArea(getTranslation("event.details"));

        Button createPoll = new Button(getTranslation("create.poll"), e -> {
            if (name.getValue().isBlank()) {
                name.setInvalid(true);
                Notification.show(getTranslation("name.required"));
            } else {
                name.setInvalid(false);
            }
        });

        name.addValueChangeListener(e -> createPoll.setEnabled(!e.getValue().isBlank()));

        calendar = createCalendar();
        FormLayout toolbar = createToolbar();

        Div calenderWithToolbar = new Div(toolbar, calendar);
        calenderWithToolbar.setSizeFull();

        add(new FormLayout(name, location, details, createPoll), calenderWithToolbar);
    }

    private FullCalendarScheduler createCalendar() {
        FullCalendarScheduler calendar = new FullCalendarScheduler();
        calendar.setSizeFull();
        calendar.setNowIndicatorShown(true);
        calendar.setTimezone(Timezone.getSystem());
        calendar.setLocale(UI.getCurrent().getLocale());
        calendar.setSchedulerLicenseKey("GPL-My-Project-Is-Open-Source");

        calendar.setFirstDay(DayOfWeek.MONDAY);

        calendar.addEntryClickedListener(event -> {
            if (event.getEntry().getRenderingMode() != Entry.RenderingMode.BACKGROUND
                    && event.getEntry().getRenderingMode() != Entry.RenderingMode.INVERSE_BACKGROUND) {
                new CalendarEntryDialog(event.getEntry(), false, entry -> {
                }, entry -> {
                }).open();
            }
        });

        calendar.addTimeslotsSelectedSchedulerListener(event -> {
            ResourceEntry resourceEntry = new ResourceEntry();

            resourceEntry.setStart(event.getStart());
            if (event.isAllDay()) {
                resourceEntry.setEnd(event.getStart());
            } else {
                resourceEntry.setEnd(event.getEnd());
            }
            resourceEntry.setAllDay(event.isAllDay());

            new CalendarEntryDialog(resourceEntry, true, entry -> {
            }, entry -> {
            }).open();
        });

        return calendar;
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
        select.setValue(SudelCalendarView.DAY_GRID_MONTH);
        select.setItemLabelGenerator(sudelCalendarView -> getTranslation(sudelCalendarView.getName()));
        select.addValueChangeListener(e -> calendar.changeView(Objects.requireNonNullElse(e.getValue(), SudelCalendarView.DAY_GRID_MONTH)));

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
