package org.unidue.ub.libintel.elisaconnector;

import org.unidue.ub.libintel.elisaconnector.model.RequestData;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataLecturer;
import org.unidue.ub.libintel.elisaconnector.model.RequestDataUser;

public class TestRequestData {

    public static RequestData requestData = new RequestData("9780553173253",
            "A Brief History of Time",
            "Stephen Hawking",
            "1",
            "Bantam",
            "2018",
            "10",
            "67",
            "this is a test",
            "John Smith",
            "EA123456789",
            "john.smith@test.de",
            true);

    public static RequestDataUser requestDataUser = new RequestDataUser("9780553173253",
            "A Brief History of Time",
            "Stephen Hawking",
            "1",
            "Bantam",
            "2018",
            "10",
            "67",
            "test source",
            "this is a test",
            "John Smith",
            "john.smith@test.de",
            "john.smith@test.de",
            true,
            true,
            true,
            "essen",
            true);

    public static RequestDataLecturer requestDataLecturer = new RequestDataLecturer("9780553173253",
            "A Brief History of Time",
            "Stephen Hawking",
            "1",
            "Bantam",
            "2018",
            "10",
            "67",
            "test source",
            "this is a test",
            "John Smith",
            "john.smith@test.de",
            "john.smith@test.de",
            true,
            true,
            true,
            true,
            "666",
            67,
            true);

}
