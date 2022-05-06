package app.sudel.ui.views.create;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Custom date time picker that allows hiding the time field.
 */
public class CustomDateTimePicker extends CustomField<LocalDateTime> {

    private final DatePicker datePicker = new DatePicker();
    private final TimePicker timePicker = new TimePicker();
    private boolean dateOnly;

    CustomDateTimePicker(String label) {
        setLabel(label);

        DatePicker.DatePickerI18n datePickerI18n = new DatePicker.DatePickerI18n();
        DateFormatSymbols symbols = new DateFormatSymbols(UI.getCurrent().getLocale());
        datePickerI18n.setMonthNames(Arrays.asList(symbols.getMonths()));
        datePickerI18n.setFirstDayOfWeek(1);
        datePickerI18n.setWeekdays(Arrays.stream(symbols.getWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        datePickerI18n.setWeekdaysShort(Arrays.stream(symbols.getShortWeekdays()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        datePicker.setI18n(datePickerI18n);

        timePicker.setLocale(UI.getCurrent().getLocale());

        add(datePicker, timePicker);
    }

    @Override
    protected LocalDateTime generateModelValue() {
        final LocalDate date = datePicker.getValue();
        final LocalTime time = timePicker.getValue();

        if (date != null) {
            if (dateOnly || time == null) {
                return date.atStartOfDay();
            }
            return LocalDateTime.of(date, time);
        }

        return null;
    }

    @Override
    protected void setPresentationValue(
            LocalDateTime newPresentationValue) {
        datePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalDate() : null);
        timePicker.setValue(newPresentationValue != null ? newPresentationValue.toLocalTime() : null);
    }

    public void setDateOnly(boolean dateOnly) {
        this.dateOnly = dateOnly;
        timePicker.setVisible(!dateOnly);
    }
}
