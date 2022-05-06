package app.sudel.ui.views.poll;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.vaadin.stefan.fullcalendar.Entry;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class EntryRenderer extends ComponentRenderer<HorizontalLayout, Entry> {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm");

    private final Consumer<Entry> onDelete;

    public EntryRenderer(Consumer<Entry> onDelete) {
        this.onDelete = onDelete;
    }

    @Override
    public HorizontalLayout createComponent(Entry entry) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        horizontalLayout.add(new Span(DATE_FORMATTER.format(entry.getStart()) + " " + TIME_FORMATTER.format(entry.getStart()) + " - " + TIME_FORMATTER.format(entry.getEnd())));

        Icon deleteIcon = VaadinIcon.TRASH.create();
        deleteIcon.addClickListener(e -> onDelete.accept(entry));
        horizontalLayout.add(deleteIcon);

        return horizontalLayout;
    }
}
