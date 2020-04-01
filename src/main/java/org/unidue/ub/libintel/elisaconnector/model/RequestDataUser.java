package org.unidue.ub.libintel.elisaconnector.model;

/**
 * POJO containing all the information received by the web page for purchase requests from a student or external user
 */
public class RequestDataUser extends RequestData {

    public  String source;
    public  boolean response;
    public  boolean essen;
    public  boolean duisburg;
    public  String requestPlace;

    /**
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
     * @param source the source leading to the purchase request
     * @param response whether the requestor whishes to be notified about the purchase descision
     * @param essen whether a book shall be ordered for the campus in Essen
     * @param duisburg whether a book shall be ordered for the campus in Duisburg
     * @param requestPlace pick up location for the requested book
     */
    public RequestDataUser(String isbn, String title, String contributor, String edition, String publisher, String year, String price, String subjectarea, String source, String comment, String name, String libraryaccountNumber, String emailAddress, boolean response, boolean essen, boolean duisburg, String requestPlace, boolean ebookDesired) {
        super(isbn, title, contributor, edition, publisher, year, price, subjectarea, comment, name, libraryaccountNumber, emailAddress, ebookDesired);
        this.source = source;
        this.response = response;
        this.essen = essen;
        this.duisburg = duisburg;
        this.requestPlace = requestPlace;
    }
}
