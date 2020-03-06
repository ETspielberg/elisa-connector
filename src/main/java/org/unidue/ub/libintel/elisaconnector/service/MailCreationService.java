package org.unidue.ub.libintel.elisaconnector.service;

public interface MailCreationService<T> {

    String buildEavMail(String name, T t);

    String buildAlreadyContainedMal(String name, T t);

    String buildNotificationMail(String reason, T t);
}
