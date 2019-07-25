package com.example.demo.ui;

import com.example.demo.ui.editor.RegisterForm;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("sa-login-view")
@Route(value = LoginView.ID)
@Theme(value = Lumo.class, variant = "dark")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    public static final String ID = "login";

    private HorizontalLayout layout = new HorizontalLayout();
    private VerticalLayout verticalLayout = new VerticalLayout();
    private LoginForm login = new LoginForm();
    private Button register = new Button("Register");
    @Autowired
    private transient RegisterForm registerForm;
    

    public LoginView() {
        setSizeUndefined();
        login.setAction("login");
        verticalLayout.add(login, register);
        verticalLayout.setAlignItems(Alignment.CENTER);
        layout.add(verticalLayout);
        layout.setAlignItems(Alignment.CENTER);
        layout.setSizeFull();
        add(layout);
        register.addClickListener(event -> registerForm.initRegistrationForm().open());
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // inform the user about an authentication error
        // (yes, the API for resolving query parameters is annoying...)
        if (!event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true);
        }
    }
}
