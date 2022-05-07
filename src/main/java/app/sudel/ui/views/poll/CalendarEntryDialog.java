package app.sudel.ui.views.poll;

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
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.stefan.fullcalendar.Delta;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;

public class CalendarEntryDialog extends Dialog {

    private final Entry entry;
    private final CustomDateTimePicker fieldStart;
    private final CustomDateTimePicker fieldEnd;
    private final Binder<Entry> binder;
    private final Consumer<Entry> onSave;
    private final Consumer<Entry> onDelete;

    private boolean initTimeWhenActivated;

    public CalendarEntryDialog(Entry entry, Consumer<Entry> onSave, Consumer<Entry> onDelete) {
        this.entry = entry;
        this.initTimeWhenActivated = entry.isAllDay();
        this.onSave = onSave;
        this.onDelete = onDelete;

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        setWidth("500px");

        fieldStart = new CustomDateTimePicker("Start");
        fieldEnd = new CustomDateTimePicker("End");
        Checkbox fieldAllDay = new Checkbox("All day event");

        boolean allDay = entry.isAllDay();
        fieldStart.setDateOnly(allDay);
        fieldEnd.setDateOnly(allDay);

        Span infoEnd = new Span("End is always exclusive, e.g. for a 1 day event you need to set for instance 4th of May as start and 5th of May as end.");
        infoEnd.getStyle().set("font-size", "0.8em");
        infoEnd.getStyle().set("color", "gray");

        VerticalLayout componentsLayout = new VerticalLayout(fieldStart, fieldEnd, infoEnd, fieldAllDay);

        componentsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        componentsLayout.setSizeFull();

        fieldAllDay.addValueChangeListener(event -> {
            fieldStart.setDateOnly(event.getValue());
            fieldEnd.setDateOnly(event.getValue());

            if (initTimeWhenActivated && !event.getValue()) {
                initTimeWhenActivated = false; // init the time once with "now"
                fieldStart.setValue(fieldStart.getValue().toLocalDate().atTime(LocalTime.now()));
                fieldEnd.setValue(fieldEnd.getValue().toLocalDate().atTime(LocalTime.now().plusHours(1)));
            }
        });

        binder = new Binder<>(Entry.class);

        binder.forField(fieldStart).asRequired().bind(Entry::getStartWithOffset, Entry::setStartWithOffset);
        binder.forField(fieldEnd).asRequired().bind(Entry::getEndWithOffset, Entry::setEndWithOffset);
        binder.forField(fieldAllDay).bind(Entry::isAllDay, Entry::setAllDay);

        binder.setBean(entry);

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

        Button buttonSave = new Button("Save");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSave.addClickListener(e -> onSave());

        Button buttonCancel = new Button("Cancel", e -> close());
        buttonCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(buttonSave, buttonCancel);
        buttons.setPadding(true);
        buttons.getStyle().set("border-top", "1px solid #ddd");

        Button buttonRemove = new Button("Remove", e -> onRemove());
        buttonRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        buttons.add(buttonRemove);

        Scroller scroller = new Scroller(componentsLayout);
        VerticalLayout outer = new VerticalLayout();
        outer.add(scroller, buttons);
        outer.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        outer.setFlexGrow(1, scroller);
        outer.setSizeFull();
        outer.setPadding(false);
        outer.setSpacing(false);

        add(outer);

        fieldStart.focus();
    }

    protected void onSave() {
        if (binder.validate().isOk()) {
            if (entry.isAllDay() && entry.getStart().toLocalDate().equals(entry.getEnd().toLocalDate())) {
                // to prevent accidentally "disappearing" days
                entry.setEnd(entry.getEnd().plusDays(1));
            }
            onSave.accept(entry);
            close();
        }
    }

    protected void onRemove() {
        onDelete.accept(entry);
        close();
    }
}
