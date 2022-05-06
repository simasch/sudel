package app.sudel.ui.views.create;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.stefan.fullcalendar.Delta;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;

public class CalendarEntryDialog extends Dialog {

    private final Entry entry;
    private final Entry tmpEntry;
    private final CustomDateTimePicker fieldStart;
    private final CustomDateTimePicker fieldEnd;
    private final Binder<Entry> binder;
    private final Consumer<Entry> onSave;
    private final Consumer<Entry> onDelete;

    private boolean initTimeWhenActivated;

    public CalendarEntryDialog(Entry entry, boolean newEntry, Consumer<Entry> onSave, Consumer<Entry> onDelete) {
        this.entry = entry;
        this.initTimeWhenActivated = entry.isAllDay();
        this.onSave = onSave;
        this.onDelete = onDelete;

        this.tmpEntry = entry.copy();

        tmpEntry.setStart(entry.getStartWithOffset());
        tmpEntry.setEnd(entry.getEndWithOffset());

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setWidth("500px");

        TextField fieldTitle = new TextField("Title");
        TextArea fieldDescription = new TextArea("Description");
        Checkbox fieldAllDay = new Checkbox("All day event");

        fieldStart = new CustomDateTimePicker("Start");
        fieldEnd = new CustomDateTimePicker("End");

        boolean allDay = this.tmpEntry.isAllDay();
        fieldStart.setDateOnly(allDay);
        fieldEnd.setDateOnly(allDay);

        Span infoEnd = new Span("End is always exclusive, e.g. for a 1 day event you need to set for instance 4th of May as start and 5th of May as end.");
        infoEnd.getStyle().set("font-size", "0.8em");
        infoEnd.getStyle().set("color", "gray");

        VerticalLayout componentsLayout = new VerticalLayout(fieldTitle, fieldDescription, fieldAllDay, fieldStart, fieldEnd, infoEnd);

        componentsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        componentsLayout.setSizeFull();
        componentsLayout.setSpacing(false);

        fieldAllDay.addValueChangeListener(event -> {
            fieldStart.setDateOnly(event.getValue());
            fieldEnd.setDateOnly(event.getValue());

            if (initTimeWhenActivated && !event.getValue()) {
                initTimeWhenActivated = false; // init the time once with "now"
                fieldStart.setValue(fieldStart.getValue().toLocalDate().atTime(LocalTime.now()));
                fieldEnd.setValue(fieldEnd.getValue().toLocalDate().atTime(LocalTime.now().plusHours(1)));
            }
        });

        // init binder
        binder = new Binder<>(Entry.class);

        // required fields
        binder.forField(fieldTitle).asRequired().bind(Entry::getTitle, Entry::setTitle);
        binder.forField(fieldStart).asRequired().bind(Entry::getStart, Entry::setStart);
        binder.forField(fieldEnd).asRequired().bind(Entry::getEnd, Entry::setEnd);

        // optional fields
        binder.bind(fieldDescription, Entry::getDescription, Entry::setDescription);
        binder.bind(fieldAllDay, Entry::isAllDay, Entry::setAllDay);

        binder.setBean(this.tmpEntry);

        fieldStart.addValueChangeListener(event -> {
            LocalDateTime oldStart = event.getOldValue();
            LocalDateTime newStart = event.getValue();
            LocalDateTime end = fieldEnd.getValue();


            if (oldStart != null && newStart != null && end != null) {
                Delta delta = Delta.fromLocalDates(oldStart, newStart);
                end = delta.applyOn(end);
                fieldEnd.setValue(end);
            }
        });

        // init buttons
        Button buttonSave = new Button("Save");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSave.addClickListener(e -> onSave());

        Button buttonCancel = new Button("Cancel", e -> close());
        buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(buttonSave, buttonCancel);
        buttons.setPadding(true);
        buttons.getStyle().set("border-top", "1px solid #ddd");

        if (!newEntry) {
            Button buttonRemove = new Button("Remove", e -> onRemove());
            buttonRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            buttons.add(buttonRemove);
        }

        Scroller scroller = new Scroller(componentsLayout);
        VerticalLayout outer = new VerticalLayout();
        outer.add(scroller, buttons);
        outer.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        outer.setFlexGrow(1, scroller);
        outer.setSizeFull();
        outer.setPadding(false);
        outer.setSpacing(false);

        add(outer);

        fieldTitle.focus();
    }

    protected void onSave() {
        if (binder.validate().isOk()) {
            // to prevent accidentally "disappearing" days
            if (this.tmpEntry.isAllDay() && this.tmpEntry.getStart().toLocalDate().equals(this.tmpEntry.getEnd().toLocalDate())) {
                this.tmpEntry.setEnd(this.tmpEntry.getEnd().plusDays(1));
            }

            // we can also create a fresh copy and leave the initial entry totally untouched
            entry.copyFrom(tmpEntry);
            entry.setStartWithOffset(tmpEntry.getStart());
            entry.setEndWithOffset(tmpEntry.getEnd());
            entry.setRecurringDaysOfWeek(); // remove the DoW
            entry.clearRecurringStart();
            entry.clearRecurringEnd();

            onSave.accept(this.entry);
            close();
        }
    }

    protected void onRemove() {
        onDelete.accept(entry);
        close();
    }
}
