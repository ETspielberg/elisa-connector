package org.unidue.ub.libintel.elisaconnector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.elisaconnector.model.elisa.AuthenticationRequest;
import org.unidue.ub.libintel.elisaconnector.model.elisa.AuthenticationResponse;
import org.unidue.ub.libintel.elisaconnector.model.elisa.CreateListRequest;
import org.unidue.ub.libintel.elisaconnector.model.elisa.CreateListResponse;

/**
 * Feign client to handle the communication with elisa
 */
@FeignClient(name="elisa", url="https://elisa.hbz-nrw.de:8091/api/rest")
@Component
public interface ElisaClient {

    /**
     * tries to authenticate the session and retrieves a authentication response holding the token to be used in the
     * further requests.
     * @param authenticationRequest the authentication request object
     * @return the authentication response object
     */
    @RequestMapping(method= RequestMethod.POST, value="/authenticate")
    AuthenticationResponse getToken(@RequestBody AuthenticationRequest authenticationRequest);

    /**
     * @param createListRequest the request holding the information to add titles to a given memory list within elisa.
     * @return the response from elisa upon the request to create a list or add an item to an existing list
     */
    @RequestMapping(method= RequestMethod.POST, value="/createNotepad")
    CreateListResponse createList(@RequestBody CreateListRequest createListRequest);

}
