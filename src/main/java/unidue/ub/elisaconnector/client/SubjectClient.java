package unidue.ub.elisaconnector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="settings-backend", configuration = FeignConfiguration.class)
@Component
public interface SubjectClient {

    @RequestMapping(method= RequestMethod.GET, value="/notationgroup/getElisaMail/{subjectArea}")
    String getElisaAccount(@PathVariable String subjectArea);
}
