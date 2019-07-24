package com.example.demo.security;

import com.example.demo.ui.WelcomeView;
import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
public final class SecurityUtils {

	private SecurityUtils() {
	}


	public static String getUsername() {
		SecurityContext context = SecurityContextHolder.getContext();
		Object principal = context.getAuthentication().getPrincipal();
		if(principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}

	public static List<String> getAuthorities(){
		Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
		return userAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}
	
	public static boolean isAccessGranted(Class<?> securedClass) {
		final boolean publicView = WelcomeView.class.equals(securedClass);
		
		// Always allow access to public views
		if (publicView) {
			return true;
		}
		Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
		// All other views require authentication
		if (!isUserLoggedIn(userAuthentication)) {
			return false;
		}
		// Allow if no roles are required.
		Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
		if (secured == null) {
			return true;
		}
		List<String> allowedRoles = Arrays.asList(secured.value());
		allowedRoles.forEach(log::error);
		userAuthentication.getAuthorities().forEach(auth -> log.error("Authority - {}", auth.getAuthority()));
		return userAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(allowedRoles::contains);
	}

	public static boolean isUserLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null
				&& !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}

	private static boolean isUserLoggedIn(Authentication authentication) {
		return authentication != null
			&& !(authentication instanceof AnonymousAuthenticationToken);
	}



	public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
		final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
		return parameterValue != null
				&& Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
	}

}
