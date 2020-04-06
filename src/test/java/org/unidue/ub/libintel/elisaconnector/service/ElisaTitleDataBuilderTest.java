package org.unidue.ub.libintel.elisaconnector.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.unidue.ub.libintel.elisaconnector.TestRequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;
import org.unidue.ub.libintel.elisaconnector.model.elisa.TitleData;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ElisaTitleDataBuilderTest {

    @Test
    public void testRequestData() {
        TitleData titleData = ElisaTitleDataBuilder.fromRequestData(TestRequestData.requestData);
        assertEquals(titleData.getIsbn(), TestRequestData.requestData.isbn);
    }

    @Test
    public void testRequestDataUser() {
        RequestDataUser requestData = TestRequestData.requestDataUser;
        TitleData titleData = ElisaTitleDataBuilder.fromRequestData(requestData);
        assertEquals(titleData.getIsbn(), requestData.isbn);
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("E??"));
        requestData.essen = false;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("E??"));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("D??"));
        requestData.duisburg = false;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("D??"));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("VM " + requestData.libraryaccountNumber));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("in " + requestData.requestPlace));
        requestData.requestPlace = "";
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("in " + requestData.requestPlace));
        requestData.libraryaccountNumber = "";
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("VM " + requestData.libraryaccountNumber));
        requestData.libraryaccountNumber = null;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("VM " + requestData.libraryaccountNumber));
    }

    @Test
    public void testRequestDataLecturer() {
        RequestDataLecturer requestData = TestRequestData.requestDataLecturer;
        assertEquals(ElisaTitleDataBuilder.fromRequestData(requestData).getIsbn(), TestRequestData.requestData.isbn);
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("persönlichen Ausweis"));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("Handapparat"));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("Semesterapparat"));
        requestData.personalAccount = false;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("persönlichen Ausweis"));
        requestData.happAccount = false;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("Handapparat"));
        requestData.semAppAccount = false;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("Semesterapparat"));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("Bitte " + requestData.number + " Exemplare bestellen."));
        requestData.number = 1;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotizIntern().contains("Bitte " + requestData.number + " Exemplare bestellen."));
        assert(ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("VM " + requestData.libraryaccountNumber));
        requestData.libraryaccountNumber = "";
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("VM " + requestData.libraryaccountNumber));
        requestData.libraryaccountNumber = null;
        assert(!ElisaTitleDataBuilder.fromRequestData(requestData).getNotiz().contains("VM " + requestData.libraryaccountNumber));
    }
}
