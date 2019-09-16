package org.unidue.ub.libintel.elisaconnector.service;

import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//@Service
public class OofService {

    @Value("${microsoft.exchange.email}")
    private String email;

    @Value("${microsoft.exchange.password}")
    private String password;

    @Value("${microsoft.exchange.username}")
    private String username;


    OofService() {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(username, password);
        service.setCredentials(credentials);
        try {
            service.autodiscoverUrl(email, new RedirectionUrlCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
        public boolean autodiscoverRedirectionUrlValidationCallback(
                String redirectionUrl) {
            return redirectionUrl.toLowerCase().startsWith("https://");
        }
    }


}
