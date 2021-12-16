package com.sf.oauth;

public class Test {
    public static void main(String[] args) {
        String url = "https://yourInstance.salesforce.com/005x000...";
        System.out.println(url);
        System.out.println(url.lastIndexOf("/"));
        System.out.println(url.substring(0, url.lastIndexOf("/")));
    }
}
