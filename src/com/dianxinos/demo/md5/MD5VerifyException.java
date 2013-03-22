
package com.dianxinos.demo.md5;

public class MD5VerifyException extends Throwable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MD5VerifyException(String msg) {
        super(msg);
    }

    public MD5VerifyException() {
        super("MD5VerifyException");
    }
}
