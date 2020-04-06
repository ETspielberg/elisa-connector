package org.unidue.ub.libintel.elisaconnector.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.unidue.ub.libintel.elisaconnector.TestRequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;


import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class RequestValidatorServiceTest {

    @Autowired
    RequestValidatorService requestValidatorService;

    private final Logger log = LoggerFactory.getLogger(RequestValidatorServiceTest.class);

    @Test
    public void testRequestData() {
        RequestData requestData = TestRequestData.requestData;
        log.info("testing request validator service for request data");
        assertEquals(requestValidatorService.validate(requestData), "valid.isbn");
        requestData.isbn = "988-1-2345-3432-1";
        assertEquals(requestValidatorService.validate(requestData), "invalid.isbn");
        requestData.isbn = "";
        assertEquals(requestValidatorService.validate(requestData), "no.isbn");
        requestData.isbn = "       ";
        assertEquals(requestValidatorService.validate(requestData), "no.isbn");
        requestData.subjectarea = "";
        assertEquals(requestValidatorService.validate(requestData), "no.subjectarea");
    }

    @Test
    public void testRequestDataUser() {
        RequestDataUser requestDataUser = TestRequestData.requestDataUser;
        log.info("testing request validator service for user request data");
        assertEquals(requestValidatorService.validate(requestDataUser), "valid.isbn");
        requestDataUser.isbn = "988-1-2345-3432-1";
        assertEquals(requestValidatorService.validate(requestDataUser), "invalid.isbn");
        requestDataUser.isbn = "";
        assertEquals(requestValidatorService.validate(requestDataUser), "no.isbn");
        requestDataUser.isbn = "       ";
        assertEquals(requestValidatorService.validate(requestDataUser), "no.isbn");
        requestDataUser.subjectarea = "";
        assertEquals(requestValidatorService.validate(requestDataUser), "no.subjectarea");
    }


    @Test
    public void testRequestDataLecturer() {
        RequestDataLecturer requestDataLecturer = TestRequestData.requestDataLecturer;
        log.info("testing request validator service for user request data");
        assertEquals(requestValidatorService.validate(requestDataLecturer), "valid.isbn");
        requestDataLecturer.isbn = "988-1-2345-3432-1";
        assertEquals(requestValidatorService.validate(requestDataLecturer), "invalid.isbn");
        requestDataLecturer.isbn = "";
        assertEquals(requestValidatorService.validate(requestDataLecturer), "no.isbn");
        requestDataLecturer.isbn = "        ";
        assertEquals(requestValidatorService.validate(requestDataLecturer), "no.isbn");
        requestDataLecturer.subjectarea = "";
        assertEquals(requestValidatorService.validate(requestDataLecturer), "no.subjectarea");
    }
}
