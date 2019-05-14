package unidue.ub.elisaconnector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import unidue.ub.elisaconnector.model.ElisaData;
import unidue.ub.elisaconnector.model.Notationgroup;

import java.util.List;

@FeignClient(name="settings-backend", configuration = FeignConfiguration.class)
@Component
public interface SubjectClient {

    @RequestMapping(method= RequestMethod.GET, value="/notationgroup/{subjectId}")
    Notationgroup getNotationgroupById(@PathVariable String subjectId);

    @RequestMapping(method= RequestMethod.GET, value="/elisadata/forSubject/{subject}")
    List<ElisaData> getElisaAccountForSubject(@PathVariable String subject);
}
