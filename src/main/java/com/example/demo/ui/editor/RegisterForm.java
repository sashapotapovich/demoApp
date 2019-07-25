package com.example.demo.ui.editor;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.annotation.SessionScope;


@Slf4j
@SessionScope
@SpringComponent
public class RegisterForm {
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private Dialog dialog;

    @Autowired
    public RegisterForm(PasswordEncoder passwordEncoder, UserRepository repository){
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    public Dialog initRegistrationForm() {
        dialog = new Dialog();
        User user = new User();
        VerticalLayout verticalLayout = new VerticalLayout();
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        TextField email = new TextField("Email");
        TextField password = new TextField("Password");
        Button save = new Button("Save");
        Button cancel = new Button("Cancel", event -> dialog.close());
        save.addClickListener(event -> {
            user.setFirstName(firstName.getValue());
            user.setLastName(lastName.getValue());
            user.setEmail(email.getValue());
            user.setPasswordHash(passwordEncoder.encode(password.getValue()));
            user.setRole("ROLE_USER");
            repository.saveAndFlush(user);
            dialog.close();
        });
        verticalLayout.add(firstName, lastName, email, password, new HorizontalLayout(save, cancel));
        dialog.add(verticalLayout);
        return dialog;
    }

}
