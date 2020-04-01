package org.unidue.ub.libintel.elisaconnector.model;

public class RequestDataUser extends RequestData {

    public  String source;
    public  boolean response;
    public  boolean essen;
    public  boolean duisburg;
    public  String requestPlace;

    public RequestDataUser(String isbn, String title, String contributor, String edition, String publisher, String year, String price, String subjectarea, String source, String comment, String name, String libraryaccountNumber, String emailAddress, boolean response, boolean essen, boolean duisburg, String requestPlace, boolean ebookDesired) {
        super(isbn, title, contributor, edition, publisher, year, price, subjectarea, comment, name, libraryaccountNumber, emailAddress, ebookDesired);
        this.source = source;
        this.response = response;
        this.essen = essen;
        this.duisburg = duisburg;
        this.requestPlace = requestPlace;
    }
}
