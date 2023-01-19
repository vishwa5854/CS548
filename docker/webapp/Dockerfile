FROM payara/micro:5.2022.4-jdk17
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "clinic", \
"--deploy", "/opt/payara/deployments/clinic-webapp.war" ]