package app.sudel.ui.views.poll;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
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
        infoLayout.getElement().appendChild(ElementFactory.createStrong(
                DATE_FORMATTER.format(entry.getStart()) +
                        " " +
                        TIME_FORMATTER.format(entry.getStart()) +
                        " - " +
                        TIME_FORMATTER.format(entry.getEnd())));

        cardLayout.add(infoLayout);

        Icon deleteIcon = VaadinIcon.TRASH.create();
        deleteIcon.addClickListener(e -> onDelete.accept(entry));

        cardLayout.add(deleteIcon);
        return cardLayout;
    }
}
