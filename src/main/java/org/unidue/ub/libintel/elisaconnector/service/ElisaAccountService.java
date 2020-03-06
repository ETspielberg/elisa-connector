package org.unidue.ub.libintel.elisaconnector.service;

import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.elisaconnector.client.SubjectClient;
import org.unidue.ub.libintel.elisaconnector.exceptions.MissingElisaAccountException;
import org.unidue.ub.libintel.elisaconnector.model.ElisaData;

import java.util.Collections;
import java.util.List;

@Service
public class ElisaAccountService {

    private SubjectClient subjectClient;

    public ElisaAccountService(SubjectClient subjectClient) {
        this.subjectClient = subjectClient;
    }

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
