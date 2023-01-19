FROM payara/micro:5.2022.4-jdk17
COPY --chown=payara:payara clinic-rest.war ${DEPLOY_DIR}
CMD [ "--contextroot", "api", \
"--deploy", "/opt/payara/deployments/clinic-rest.war" ]