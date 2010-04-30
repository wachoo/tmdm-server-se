// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util.license;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class Hsifwolb {

    public static byte[] decrypt(byte[] encrypted, byte[] key) throws GeneralSecurityException {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        String iv = "00000000";
        IvParameterSpec ivs = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivs);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] encrypt(byte[] messageBytes, byte[] key) throws GeneralSecurityException {
        Provider[] pvs = Security.getProviders();
        SecretKeySpec skeySpec = new SecretKeySpec(key, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        String iv = "00000000";
        IvParameterSpec ivs = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivs);
        byte[] encrypted = cipher.doFinal(messageBytes);
        return encrypted;
    }
}
