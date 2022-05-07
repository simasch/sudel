package app.sudel.ui.views.poll;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
        HorizontalLayout cardLayout = new HorizontalLayout();
        cardLayout.setMargin(true);

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        infoLayout.add(createDateTimeInfo(entry));

        cardLayout.add(infoLayout);

        Icon deleteIcon = VaadinIcon.TRASH.create();
        deleteIcon.addClickListener(e -> onDelete.accept(entry));

        cardLayout.add(deleteIcon);
        return cardLayout;
    }

    private Div createDateTimeInfo(Entry entry) {
        Div div = new Div();
        if (entry.isAllDay()) {
            if (entry.getStart().equals(entry.getEnd())) {
                div.add(new Span(DATE_FORMATTER.format(entry.getStartWithOffset())));
            } else {
                div.add(new Span(DATE_FORMATTER.format(entry.getStartWithOffset()) +
                        "  - " +
                        DATE_FORMATTER.format(entry.getEndWithOffset())));
            }
        } else {
            if (entry.getStart().toLocalDate().equals(entry.getEnd().toLocalDate())) {
                div.add(new Span(DATE_FORMATTER.format(entry.getStartWithOffset()) +
                        " " +
                        TIME_FORMATTER.format(entry.getStartWithOffset()) +
                        " - " +
                        TIME_FORMATTER.format(entry.getEndWithOffset())));
            } else {
                div.add(new Span(DATE_FORMATTER.format(entry.getStartWithOffset()) +
                        " " +
                        TIME_FORMATTER.format(entry.getStartWithOffset()) +
                        " -"));
                div.add(new Paragraph());
                div.add(new Span(DATE_FORMATTER.format(entry.getEndWithOffset()) +
                        " " +
                        TIME_FORMATTER.format(entry.getEndWithOffset())));
            }
        }
        return div;
    }
}
