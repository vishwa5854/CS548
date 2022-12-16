FROM payara/micro:5.2022.4-jdk17
COPY --chown=payara:payara clinic-domain.war ${DEPLOY_DIR}
COPY --chown=payara:payara config.properties ${PAYARA_DIR}
CMD [ "--contextroot", "api", \
    "--systemProperties", "/opt/payara/config.properties", \
    "--deploy", "/opt/payara/deployments/clinic-domain.war" ]