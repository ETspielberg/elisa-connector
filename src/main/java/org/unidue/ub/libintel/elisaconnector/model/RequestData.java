package org.unidue.ub.libintel.elisaconnector.model;

public class RequestData {

    public  String isbn;
    public  String title;
    public  String contributor;
    public  String edition;
    public  String publisher;
    public  String year;
    public  String price;
    public  String subjectarea;
    public  String source;
    public  String comment;
    public  String name;
    public  String libraryaccountNumber;
    public  String emailAddress;
    public  boolean response;
    public  boolean essen;
    public  boolean duisburg;
    public  String requestPlace;

    public RequestData(String isbn, String title, String contributor, String edition, String publisher, String year, String price, String subjectarea, String source, String comment, String name, String libraryaccountNumber, String emailAddress, boolean response, boolean essen, boolean duisburg, String requestPlace) {
        this.isbn = isbn;
        this.title = title;
        this.contributor = contributor;
        this.edition = edition;
        this.publisher = publisher;
        this.year = year;
        this.price = price;
        this.subjectarea = subjectarea;
        this.source = source;
        this.comment = comment;
        this.name = name;
        this.libraryaccountNumber = libraryaccountNumber;
        this.emailAddress = emailAddress;
        this.response = response;
        this.essen = essen;
        this.duisburg = duisburg;
        this.requestPlace = requestPlace;
    }
}
