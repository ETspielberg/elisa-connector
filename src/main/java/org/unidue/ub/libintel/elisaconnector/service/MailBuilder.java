package org.unidue.ub.libintel.elisaconnector.service;

/**
 * handles the creation of the appropriate mal creation services
 * @param <T>
 */
class MailBuilder<T> {

    private MailCreationService<T> mailCreationService;

    /**
     * constructor based autowiring to mal creation service
     * @param mailCreationService the mail creation service
     */
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

    String buildEbookMail(T requestData) {
        return mailCreationService.buildEbookMail(requestData);
    }
}
