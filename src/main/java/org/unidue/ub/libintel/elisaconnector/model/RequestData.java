package org.unidue.ub.libintel.elisaconnector.model;


/**
 * POJO containing all the basic information received by the web page for purchase requests
 */
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

    /**
     * constructs a RequestData object from all of its variables
     *
     * @param isbn the isbn of the desired book
     * @param title the title of the desired book
     * @param contributor the author or editor of the desired book
     * @param edition the edition of the desired book
     * @param publisher the publisher of the desired book
     * @param year guess what
     * @param price the price of the desired book
     * @param subjectarea the subject area this book sholl be dealt with
     * @param comment a comment why this book is requested for purchase
     * @param name the name of the requestor
     * @param libraryaccountNumber the library account number of the requestor
     * @param emailAddress the email adress of the requestor
     * @param ebookDesired whether or not an ebook is requested
     */
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

