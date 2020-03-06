package org.unidue.ub.libintel.elisaconnector.service;

class MailBuilder<T> {

    private MailCreationService<T> mailCreationService;

    MailBuilder(MailCreationService<T> mailCreationService) {
        this.mailCreationService = mailCreationService;
    }

    String buildNotificationMail(String name, T requestData) {
        return mailCreationService.buildNotificationMail(name, requestData);
    }

    String buildAlreadyContainedMal(String name, T requestData) {
        return mailCreationService.buildAlreadyContainedMal(name, requestData);
    }

    String buildEavMail(String reason, T requestData) {
        return mailCreationService.buildEavMail(reason, requestData);
    }
}
