package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.elisaconnector.client.SubjectClient;
import org.unidue.ub.libintel.elisaconnector.exceptions.MissingElisaAccountException;
import org.unidue.ub.libintel.elisaconnector.model.ElisaData;

import java.util.Collections;
import java.util.List;

/**
 * handles the retrieval of the elisa account to be used
 */
@Service
public class ElisaAccountService {

    private SubjectClient subjectClient;

    /**
     * constructor based autowiring to the subject client
     * @param subjectClient the Feign subject client bean
     */
    public ElisaAccountService(SubjectClient subjectClient) {
        this.subjectClient = subjectClient;
    }

    /**
     * retrieves all elisa data for a given subject and returns the one with the highest priority.
     * @param notationgroupname the subject code
     * @return the active elisa data
     */
    public ElisaData getActiveElisaDataForSubject(String notationgroupname) {
        List<ElisaData> allData = subjectClient.getElisaAccountForSubject(notationgroupname);
        if (allData.size() == 0)
            throw new MissingElisaAccountException("no elisa account for subject " + notationgroupname);
        Collections.sort(allData);
        return determineElisaData(allData);
    }

    private ElisaData determineElisaData(List<ElisaData> availableProfiles) {
        Collections.sort(availableProfiles);
        // TODO: check for out-of-office status, select first one, second one, depending on result
        return availableProfiles.get(0);
    }
}
