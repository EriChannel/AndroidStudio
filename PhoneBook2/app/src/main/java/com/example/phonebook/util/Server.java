package com.example.phonebook.util;

public class Server {

    public static String localhost = "192.168.1.6";
    public static String urlGetAllContact = "http://"+localhost+"/phone_book/getAllContact.php";
    public static String urlInsertContact = "http://"+localhost+"/phone_book/insertContact.php";
    public static String urlDeleteContact = "http://"+localhost+"/phone_book/deleteContact.php";
    public static String urlUpdateContact = "http://"+localhost+"/phone_book/updateContact.php";
}
