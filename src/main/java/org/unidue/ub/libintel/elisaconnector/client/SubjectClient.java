package org.unidue.ub.libintel.elisaconnector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.elisaconnector.model.ElisaData;
import org.unidue.ub.libintel.elisaconnector.model.Notationgroup;

import java.util.List;

/**
 * Feign client to retrieve the elisa account data and the notation group data from the settings backend
 */
@FeignClient(name="settings-backend", configuration = FeignConfiguration.class)
@Component
public interface SubjectClient {

    /**
     * @param subjectId the id of the subjkect area as indicated in the purchase request
     * @return the notationgroup object, holding the subject code and the speaking name
     */
    @RequestMapping(method= RequestMethod.GET, value="/notationgroup/{subjectId}")
    Notationgroup getNotationgroupById(@PathVariable String subjectId);

    /**
     * @param subject the subject code
     * @return the corresponding elisa data for a given subject that is the responsible elisa accounts
     */
    @RequestMapping(method= RequestMethod.GET, value="/elisadata/forSubject/{subject}")
    List<ElisaData> getElisaAccountForSubject(@PathVariable String subject);
}
