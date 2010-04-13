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
package com.amalto.webapp.core.util.license;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class InetUtil {

    /**
     * 
     * Méthode Main de l'application
     * 
     * 
     * 
     * @param args
     */

    public final static void main(String[] args)

    {

        try

        {

            System.out.println("Information Réseau Local");

            System.out.println("  Système d'exploitation : " + System.getProperty("os.name"));

            System.out.println("  IP/Localhost: " + InetAddress.getLocalHost().getHostAddress());

            System.out.println("  Adresse MAC : " + getMacAddress());

        }

        catch (Throwable t)

        {

            t.printStackTrace();

        }

    }

    /**
     * 
     * Méthode invoquant le traitement à effectuer pour récupérer l'adresse MAC
     * 
     * de l'ordinateur sur lequel on se trouve suivant le système
     * 
     * d'exploitation.
     * 
     * 
     * 
     * @return L'adresse MAC
     * 
     * 
     * 
     * @throws IOException
     */

    public final static List<String> getMacAddress() throws IOException {
        try {
            return getMacAddressJava6();
        } catch (NoSuchMethodError e) {
            List<String> toReturn = new ArrayList<String>();
            toReturn.add(getMacAddressJava5());
            return toReturn;
        }
    }

    private final static List<String> getMacAddressJava6() throws IOException {
        List<String> toReturn = new ArrayList<String>();

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        for (; networkInterfaces.hasMoreElements();) {
            final NetworkInterface nextElement = networkInterfaces.nextElement();
            byte[] mac = nextElement.getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            if (mac != null) {
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
            }

            toReturn.add(sb.toString());
        }

        return toReturn;
    }

    private final static String getMacAddressJava5() throws IOException {
        String os = System.getProperty("os.name");

        try

        {

            if (os.startsWith("Windows"))

            {

                return windowsParseMacAddress(windowsRunIpConfigCommand());

            }

            else if (os.startsWith("Linux"))

            {

                return linuxParseMacAddress(linuxRunIfConfigCommand());

            }

            else if (os.startsWith("Mac OS X"))

            {

                return osxParseMacAddress(osxRunIfConfigCommand());

            }

            else

            {

                throw new IOException("Système d'exploitation non supporté : " + os);

            }

        }

        catch (ParseException ex)

        {

            throw new IOException(ex.getMessage());

        }

    }

    // *************** Commande de récupération des informations réseau ***************//

    /**
     * 
     * Méthode récupérant les informations réseau sous Windows
     * 
     * 
     * 
     * @return toutes les informations réseau
     * 
     * 
     * 
     * @throws IOException
     */

    private final static String windowsRunIpConfigCommand() throws IOException

    {

        Process p = Runtime.getRuntime().exec("ipconfig /all");

        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuffer buffer = new StringBuffer();

        for (;;)

        {

            int c = stdoutStream.read();

            if (c == -1)

                break;

            buffer.append((char) c);

        }

        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;

    }

    /**
     * 
     * Méthode récupérant les informations réseau sous Linux
     * 
     * 
     * 
     * @return toutes les informations réseau
     * 
     * 
     * 
     * @throws IOException
     */

    private final static String linuxRunIfConfigCommand() throws IOException

    {

        Process p = Runtime.getRuntime().exec("ifconfig");

        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuffer buffer = new StringBuffer();

        for (;;)

        {

            int c = stdoutStream.read();

            if (c == -1)

                break;

            buffer.append((char) c);

        }

        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;

    }

    /**
     * 
     * Méthode récupérant les informations réseau sous OS X (Apple)
     * 
     * 
     * 
     * @return toutes les informations réseau
     * 
     * 
     * 
     * @throws IOException
     */

    private final static String osxRunIfConfigCommand() throws IOException

    {

        Process p = Runtime.getRuntime().exec("ifconfig");

        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuffer buffer = new StringBuffer();

        for (;;)

        {

            int c = stdoutStream.read();

            if (c == -1)

                break;

            buffer.append((char) c);

        }

        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;

    }

    // *************** Récupération de l'adresse MAC ***************//

    /**
     * 
     * Méthode triant les informations réseau récupérer sous Windows grâce à la
     * 
     * méthode windowsRunIpConfigCommand pour extraire seulement l'adresse MAC.
     * 
     * 
     * 
     * @return l'adresse MAC
     * 
     * 
     * 
     * @throws IOException
     */

    private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException

    {

        String localHost = null;

        try

        {

            localHost = InetAddress.getLocalHost().getHostAddress();

        }

        catch (java.net.UnknownHostException ex)

        {

            ex.printStackTrace();

            throw new ParseException(ex.getMessage(), 0);

        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");

        String lastMacAddress = null;

        while (tokenizer.hasMoreTokens())

        {

            String line = tokenizer.nextToken().trim();

            // see if line contains IP address

            if (line.endsWith(localHost) && lastMacAddress != null)

            {

                return lastMacAddress;

            }

            // see if line contains MAC address

            int macAddressPosition = line.indexOf(":");

            if (macAddressPosition <= 0)

                continue;

            String macAddressCandidate = line.substring(macAddressPosition + 1).trim();

            if (windowsIsMacAddress(macAddressCandidate))

            {

                lastMacAddress = macAddressCandidate;

                continue;

            }

        }

        ParseException ex = new ParseException("cannot read MAC address from [" + ipConfigResponse + "]", 0);

        ex.printStackTrace();

        throw ex;

    }

    /**
     * 
     * Méthode triant les informations réseau récupérer sous Linux grâce à la
     * 
     * méthode linuxRunIfConfigCommand pour extraire seulement l'adresse MAC.
     * 
     * 
     * 
     * @return l'adresse MAC
     * 
     * 
     * 
     * @throws IOException
     */

    private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException

    {

        String localHost = null;

        try

        {

            localHost = InetAddress.getLocalHost().getHostAddress();

        }

        catch (java.net.UnknownHostException ex)

        {

            ex.printStackTrace();

            throw new ParseException(ex.getMessage(), 0);

        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");

        String lastMacAddress = null;

        while (tokenizer.hasMoreTokens())

        {

            String line = tokenizer.nextToken().trim();

            boolean containsLocalHost = line.indexOf(localHost) >= 0;

            // see if line contains IP address

            if (containsLocalHost && lastMacAddress != null)

            {

                return lastMacAddress;

            }

            // see if line contains MAC address

            int macAddressPosition = line.indexOf("HWaddr");

            if (macAddressPosition <= 0)

                continue;

            String macAddressCandidate = line.substring(macAddressPosition + 6).trim();

            if (linuxIsMacAddress(macAddressCandidate))

            {

                lastMacAddress = macAddressCandidate;

                continue;

            }

        }

        ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from ["

        + ipConfigResponse + "]", 0);

        // ex.printStackTrace();

        throw ex;

    }

    /**
     * 
     * Méthode triant les informations réseau récupérer sous OS X (Apple) grâce
     * 
     * à la méthode osxRunIfConfigCommand pour extraire seulement l'adresse MAC.
     * 
     * 
     * 
     * @return l'adresse MAC
     * 
     * 
     * 
     * @throws IOException
     */

    private final static String osxParseMacAddress(String ipConfigResponse) throws ParseException

    {

        String localHost = null;

        try

        {

            localHost = InetAddress.getLocalHost().getHostAddress();

        }

        catch (java.net.UnknownHostException ex)

        {

            ex.printStackTrace();

            throw new ParseException(ex.getMessage(), 0);

        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");

        while (tokenizer.hasMoreTokens())

        {

            String line = tokenizer.nextToken().trim();

            boolean containsLocalHost = line.indexOf(localHost) >= 0;

            // see if line contains MAC address

            int macAddressPosition = line.indexOf("ether");

            if (macAddressPosition != 0)

                continue;

            String macAddressCandidate = line.substring(macAddressPosition + 6).trim();

            if (osxIsMacAddress(macAddressCandidate))

            {

                return macAddressCandidate;

            }

        }

        ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from ["

        + ipConfigResponse + "]", 0);

        ex.printStackTrace();

        throw ex;

    }

    // *************** Validation de l'adresse MAC ***************//

    /**
     * 
     * Validation de l'adresse MAC
     * 
     * 
     * 
     * @param macAddressCandidate
     * 
     * 
     * 
     * @return true si l'adresse MAC récupérer sous windows est correcte
     */

    private final static boolean windowsIsMacAddress(String macAddressCandidate)

    {

        Pattern macPattern = Pattern

        .compile("[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}");

        Matcher m = macPattern.matcher(macAddressCandidate);

        return m.matches();

    }

    /**
     * 
     * Validation de l'adresse MAC
     * 
     * 
     * 
     * @param macAddressCandidate
     * 
     * 
     * 
     * @return true si l'adresse MAC récupérer sous Linux est correcte
     */

    private final static boolean linuxIsMacAddress(String macAddressCandidate)

    {

        // TODO: use a smart regular expression

        if (macAddressCandidate.length() != 17)

            return false;

        return true;

    }

    /**
     * 
     * Validation de l'adresse MAC
     * 
     * 
     * 
     * @param macAddressCandidate
     * 
     * 
     * 
     * @return true si l'adresse MAC récupérer sous OS X (Apple) est correcte
     */

    private final static boolean osxIsMacAddress(String macAddressCandidate)

    {

        // TODO: use a smart regular expression

        if (macAddressCandidate.length() != 17)

            return false;

        return true;

    }
}
