package org.talend.mdm.commmon.util.core;

import junit.framework.TestCase;

public class CryptTest extends TestCase {

    @SuppressWarnings("nls")
    public void testEncrypt() throws Exception {
        String pwd = "administrator";
        assertEquals("NKNCzuAvtojMffRWt9ZLRw==,Encrypt", Crypt.encrypt(pwd));

        pwd = "admin123";
        assertEquals("w4AXOA1a34afqqnlmVLB4A==,Encrypt", Crypt.encrypt(pwd));

        pwd = "";
        assertEquals("", Crypt.encrypt(pwd));
    }

    @SuppressWarnings("nls")
    public void testDecrypt() throws Exception {
        String encryptedPwd = "w4AXOA1a34afqqnlmVLB4A==,Encrypt";
        assertEquals("admin123", Crypt.decrypt(encryptedPwd));

        encryptedPwd = "w4AXOA1a34afqqnlmVLB4A==";
        assertEquals("w4AXOA1a34afqqnlmVLB4A==", Crypt.decrypt(encryptedPwd));

        encryptedPwd = "";
        assertEquals("", Crypt.decrypt(encryptedPwd));
    }
}