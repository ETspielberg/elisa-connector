package org.unidue.ub.libintel.elisaconnector.model;

/**
 * POJO containing all the information received by the web page for purchase requests from a lecturer
 */
public class RequestDataLecturer extends RequestData {

    public boolean directToStock;
    public boolean personalAccount;
    public boolean happAccount;
    public boolean semAppAccount;
    public int number;
    public String semAppNumber;
    public String source;

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
     * @param ebookDesired whether or not an ebook is requested     * @param isbn
     * @param source the source leading to the purchase request
     * @param personalAccount the personal library account number of the requestor
     * @param happAccount the happ account number of the requestor
     * @param semAppAccount whether the book should go into a semester account
     * @param directToStock whether the book should go directly into the stock
     * @param semAppNumber the semester apparate number the book is requested for
     * @param number the number of books requested
     */
    public RequestDataLecturer(String isbn, String title, String contributor, String edition, String publisher, String year, String price, String subjectarea, String source, String comment, String name, String libraryaccountNumber, String emailAddress, boolean personalAccount, boolean happAccount, boolean semAppAccount, boolean directToStock, String semAppNumber, int number, boolean ebookDesired) {
        super(isbn, title, contributor, edition, publisher, year, price, subjectarea, comment, name, libraryaccountNumber, emailAddress, ebookDesired);
        this.directToStock = directToStock;
        this.personalAccount = personalAccount;
        this.happAccount = happAccount;
        this.semAppAccount = semAppAccount;
        this.source = source;
        this.semAppNumber = semAppNumber;
        this.number = number;
    }
}
