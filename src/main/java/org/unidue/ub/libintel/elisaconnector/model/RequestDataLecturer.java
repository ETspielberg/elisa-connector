package org.unidue.ub.libintel.elisaconnector.model;

public class RequestDataLecturer extends RequestData {

    public boolean directToStock;
    public boolean personalAccount;
    public boolean happAccount;
    public boolean semAppAccount;
    public String semAppNumber;
    public String source;

    public RequestDataLecturer(String isbn, String title, String contributor, String edition, String publisher, String year, String price, String subjectarea, String source, String comment, String name, String libraryaccountNumber, String emailAddress, boolean personalAccount, boolean happAccount, boolean semAppAccount, boolean directToStock, String semAppNumber) {
        super(isbn, title, contributor, edition, publisher, year, price, subjectarea, comment, name, libraryaccountNumber, emailAddress);
        this.directToStock = directToStock;
        this.personalAccount = personalAccount;
        this.happAccount = happAccount;
        this.semAppAccount = semAppAccount;
        this.source = source;
        this.semAppNumber = semAppNumber;
    }
}
