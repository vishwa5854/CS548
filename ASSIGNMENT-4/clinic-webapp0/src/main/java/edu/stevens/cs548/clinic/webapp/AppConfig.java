package edu.stevens.cs548.clinic.webapp;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;

/*
 * This annotation on application-scoped bean enables JSF 2.3
 */
@FacesConfig(version = FacesConfig.Version.JSF_2_3)

@ApplicationScoped
public class AppConfig {

}
