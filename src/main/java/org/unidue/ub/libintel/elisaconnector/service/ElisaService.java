package org.unidue.ub.libintel.elisaconnector.service;

import feign.codec.DecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.elisaconnector.client.ElisaClient;
import org.unidue.ub.libintel.elisaconnector.exceptions.AlreadyContainedException;
import org.unidue.ub.libintel.elisaconnector.exceptions.ElisaAuthenticationException;
import org.unidue.ub.libintel.elisaconnector.exceptions.InvalidIsbnException;
import org.unidue.ub.libintel.elisaconnector.model.elisa.AuthenticationRequest;
import org.unidue.ub.libintel.elisaconnector.model.elisa.AuthenticationResponse;
import org.unidue.ub.libintel.elisaconnector.model.elisa.CreateListRequest;
import org.unidue.ub.libintel.elisaconnector.model.elisa.CreateListResponse;

/**
 * handles the submission of books to elisa memory lists
 */
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

    /**
     * constructor based autowiring to the elisa client
     * @param elisaClient the Feign elisa client
     */
    public ElisaService(ElisaClient elisaClient) {
        this.elisaClient = elisaClient;
    }

    /**
     * sends a given list creation request to elisa
     * @param createListRequest the list creation request holding the information about memory list name, elisa account and the individual title data
     * @return true, if the submission was successful (return status is 0)
     * @throws AlreadyContainedException thrown, if the title is already on the list (indicated by return status, but doesn't work, need to be fixed in elisa)
     * @throws ElisaAuthenticationException thrown, if the authentication with elisa fails
     * @throws InvalidIsbnException thrown, if the elisa returns "invalid isbn"
     */
    public boolean sendToElisa(CreateListRequest createListRequest) throws AlreadyContainedException, ElisaAuthenticationException, InvalidIsbnException {
        //prepare the authentication request
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(callerID, secret);
        // perform authentication
        AuthenticationResponse authenticationResponse = elisaClient.getToken(authenticationRequest);
        // if successful try to create list
        if (authenticationResponse.getErrorcode() == 0) {
            createListRequest.setToken(authenticationResponse.getToken());
            try {
                // submit list creation request to elisa
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

            // if authentication fails, throw authentication exception
        } else {
            log.error("elisa authentication failed. Reason: " + authenticationResponse.getErrorMessage());
            throw new ElisaAuthenticationException("could not authenticate with elisa");
        }
    }
}
