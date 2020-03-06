package org.unidue.ub.libintel.elisaconnector.service;

import feign.codec.DecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.elisaconnector.client.ElisaClient;
import org.unidue.ub.libintel.elisaconnector.exceptions.AlreadyContainedException;
import org.unidue.ub.libintel.elisaconnector.exceptions.ElisaAuthenticationException;
import org.unidue.ub.libintel.elisaconnector.exceptions.ElisaException;
import org.unidue.ub.libintel.elisaconnector.exceptions.InvalidIsbnException;
import org.unidue.ub.libintel.elisaconnector.model.AuthenticationRequest;
import org.unidue.ub.libintel.elisaconnector.model.AuthenticationResponse;
import org.unidue.ub.libintel.elisaconnector.model.CreateListRequest;
import org.unidue.ub.libintel.elisaconnector.model.CreateListResponse;

@Service
public class ElisaService {

    // the caller id assigned by the hbz
    @Value("${libintel.elisa.callerid}")
    private String callerID;

    // the secret provided by the hbz
    @Value("${libintel.elisa.secret}")
    private String secret;

    private final ElisaClient elisaClient;

    private static final Logger log = LoggerFactory.getLogger(ElisaService.class);

    public ElisaService(ElisaClient elisaClient) {
        this.elisaClient = elisaClient;
    }

    public boolean sendToElisa(CreateListRequest createListRequest) throws AlreadyContainedException, ElisaAuthenticationException, ElisaException {
        //prepare the authentication request
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(callerID, secret);
        // perform authentication
        AuthenticationResponse authenticationResponse = elisaClient.getToken(authenticationRequest);
        // if successful try to create list
        if (authenticationResponse.getErrorcode() == 0) {
            createListRequest.setToken(authenticationResponse.getToken());
            try {
                CreateListResponse createListResponse = elisaClient.createList(createListRequest);
                log.debug("response from elisa: " + createListResponse.getErrorcode() + "(" + createListResponse.getErrorMessage() + ")");
                if (createListResponse.getErrorcode() == 0) {
                    return true;
                } else if (createListResponse.getErrorcode() == 4) {
                    throw new AlreadyContainedException("title already on list");
                } else {
                    return false;
                }
            } catch (DecodeException de) {
                throw new InvalidIsbnException("ISBN with errors");
            }

            // if authentication fails, send email to standard address
        } else {
            log.error("elisa authentication failed. Reason: " + authenticationResponse.getErrorMessage());
            throw new ElisaAuthenticationException("could not authenticate with elisa");
        }
    }
}
