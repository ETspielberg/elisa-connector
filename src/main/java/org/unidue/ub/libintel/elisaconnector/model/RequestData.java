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
    public  String comment;
    public  String name;
    public  String libraryaccountNumber;
    public  String emailAddress;
    public boolean ebookDesired;

    public RequestData() {}

    public RequestData(String isbn, String title, String contributor, String edition, String publisher, String year, String price, String subjectarea, String comment, String name, String libraryaccountNumber, String emailAddress, boolean ebookDesired) {
        this.isbn = isbn;
        this.title = title;
        this.contributor = contributor;
        this.edition = edition;
        this.publisher = publisher;
        this.year = year;
        this.price = price;
        this.subjectarea = subjectarea;
        this.comment = comment;
        this.name = name;
        this.libraryaccountNumber = libraryaccountNumber;
        this.emailAddress = emailAddress;
        this.ebookDesired = ebookDesired;
    }
}

