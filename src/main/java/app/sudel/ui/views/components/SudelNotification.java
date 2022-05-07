package app.sudel.ui.views.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public final class SudelNotification {

    private SudelNotification() {
    }

    public static void showError(String message) {
        showNotification(message, NotificationVariant.LUMO_ERROR);
    }

    public static void showSuccess(String message) {
        showNotification(message, NotificationVariant.LUMO_SUCCESS);
    }

    private static void showNotification(String message, NotificationVariant notificationVariant) {
        Notification notification = new Notification();
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(3000);

        Div text = new Div(new Text(notification.getTranslation(message)));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }
}
