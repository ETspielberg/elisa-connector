package unidue.ub.elisaconnector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import unidue.ub.elisaconnector.model.AuthenticationRequest;
import unidue.ub.elisaconnector.model.CreateListRequest;
import unidue.ub.elisaconnector.model.AuthenticationResponse;
import unidue.ub.elisaconnector.model.CreateListResponse;

@FeignClient(name="elisa", url="https://elisa.hbz-nrw.de:8091/api/rest")
@Component
public interface ElisaClient {

    @RequestMapping(method= RequestMethod.POST, value="/authenticate")
    AuthenticationResponse getToken(@RequestBody AuthenticationRequest authenticationRequest);

    @RequestMapping(method= RequestMethod.POST, value="/createNotepad")
    CreateListResponse createList(@RequestBody CreateListRequest createListRequest);

}
