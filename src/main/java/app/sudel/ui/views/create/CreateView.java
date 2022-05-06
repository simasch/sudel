package app.sudel.ui.views.create;

import app.sudel.ui.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@RouteAlias(value = "", layout = MainLayout.class)
@Route(layout = MainLayout.class)
public class CreateView extends HorizontalLayout implements HasDynamicTitle {

    public CreateView() {

    }

    @Override
    public String getPageTitle() {
        return getTranslation("find.date");
    }
}

