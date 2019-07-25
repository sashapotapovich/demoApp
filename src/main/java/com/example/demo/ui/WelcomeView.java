package com.example.demo.ui;

import com.example.demo.service.UserDetailsServiceImpl;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UIScope
@SpringComponent
@Route(value = "welcome", layout = MenuView.class)
public class WelcomeView extends VerticalLayout implements RouterLayout {

    public static final String ID = "welcome";
    private transient UserDetailsServiceImpl currentUser;

    public WelcomeView(UserDetailsServiceImpl currentUser) {
        this.currentUser = currentUser;
    }

    @PostConstruct
    public void init() {
        VerticalLayout verticalLayout = new VerticalLayout();
        H2 h2 = new H2("Welcome " + currentUser.getUser().getFirstName() + " " + currentUser.getUser().getLastName() + "!");
        verticalLayout.add(h2);
        verticalLayout.setAlignItems(Alignment.CENTER);
        add(verticalLayout);
    }
}
