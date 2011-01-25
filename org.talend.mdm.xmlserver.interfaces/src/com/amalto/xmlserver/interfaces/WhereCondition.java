/*
 * Created on 9 août 2005
 */
package com.amalto.xmlserver.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

/**
 * @author Bruno Grieder
 * 
 * A condition found in an XQuery where clause
 * 
 */
public class WhereCondition implements IWhereItem, Serializable {

    public static final String FULLTEXTSEARCH = "FULLTEXTSEARCH";

    public static final String CONTAINS = "CONTAINS";

    public static String STRICTCONTAINS = "STRICTCONTAINS";

    public static String STARTSWITH = "STARTSWITH";

    public static String JOINS = "JOINS";

    public static String CONTAINS_TEXT_OF = "CONTAINS_TEXT_OF";

    public static String EMPTY_NULL = "Is Empty Or Null";

    public static final String EQUALS = "=";

    public static final String NOT_EQUALS = "!=";

    public static final String GREATER_THAN = ">";

    public static final String GREATER_THAN_OR_EQUAL = ">=";

    public static final String LOWER_THAN = "<";

    public static final String LOWER_THAN_OR_EQUAL = "<=";

    public static final String NO_OPERATOR = "NO OP";

    public static final String PRE_NONE = "&"; // default

    public static final String PRE_OR = "|";

    public static final String PRE_AND = "&";

    public static final String PRE_STRICTAND = "+";

    public static final String PRE_EXACTLY = "=";

    public static final String PRE_NOT = "!";

    String leftPath;

    String operator;

    String rightValueOrPath;

    String stringPredicate;

    boolean spellCheck;

    private boolean isRightValueXPath;

    /**
     * Constructor used by Castor to unmarshell the WhereCondition
     */
    public WhereCondition() {
    }

    /**
     * @param leftPath
     * @param operator
     * @param rightValueOrPath
     * @param stringPredicate
     */
    public WhereCondition(String leftPath, String operator, String rightValueOrPath, String stringPredicate) {
        this(leftPath, operator, rightValueOrPath, stringPredicate, true);
    }

    /**
     * @param leftPath
     * @param operator
     * @param rightValueOrPath
     * @param stringPredicate
     */
    public WhereCondition(String leftPath, String operator, String rightValueOrPath, String stringPredicate, boolean spellCheck) {
        super();
        this.leftPath = leftPath;
        this.operator = operator;
        setRightValueOrPath(rightValueOrPath);
        this.stringPredicate = stringPredicate;
        this.spellCheck = spellCheck;
    }

    public String getLeftPath() {
        return leftPath;
    }

    public void setLeftPath(String leftPath) {
        this.leftPath = leftPath;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRightValueOrPath() {
        return rightValueOrPath;
    }

    public void setRightValueOrPath(String rightValueOrPath) {
        // Quoted values (either simple or double) are considered as literal values
        // TODO To be refactored to support true literal/Xpath differenciation
        if ((rightValueOrPath.startsWith("\"") && rightValueOrPath.endsWith("\""))
                || (rightValueOrPath.startsWith("'") && rightValueOrPath.endsWith("'"))) {
            isRightValueXPath = false;
            rightValueOrPath = rightValueOrPath.substring(1, rightValueOrPath.length() - 1);
        } else
            isRightValueXPath = true;

        if (!rightValueOrPath.startsWith("^") && (null != this.operator && this.operator.equals(WhereCondition.STARTSWITH))) {
            this.rightValueOrPath = "^" + rightValueOrPath;
        } else {
            this.rightValueOrPath = rightValueOrPath;
        }
    }

    public boolean isRightValueXPath() {
        return this.isRightValueXPath;
    }

    public String getStringPredicate() {
        return stringPredicate;
    }

    public void setStringPredicate(String stringPredicate) {
        this.stringPredicate = stringPredicate;
    }

    /**
     * @return Returns the spellCheck.
     */
    public boolean isSpellCheck() {
        return spellCheck;
    }

    /**
     * @param spellCheck The spellCheck to set.
     */
    public void setSpellCheck(boolean spellCheck) {
        this.spellCheck = spellCheck;
    }

    private String xmlEncode(String uncoded) {
        String encoded = uncoded;
        encoded = encoded.replaceAll("&", "&amp;");
        encoded = encoded.replaceAll(">", "&gt;");
        encoded = encoded.replaceAll("<", "&lt;");
        return encoded;
    }

    public String serialize() {

        return "<wherecondition>" + "	<leftpath>" + xmlEncode(getLeftPath()) + "</leftpath>" + "	<operator>"
                + xmlEncode(getOperator()) + "</operator>" + "	<rightvalueorpath>" + xmlEncode(getRightValueOrPath())
                + "</rightvalueorpath>" + "	<stringpredicate>" + xmlEncode(getStringPredicate()) + "</stringpredicate>"
                + "	<spellcheck>" + (spellCheck ? "yes" : "no") + "</spellcheck>" + "</wherecondition>";
    }

    public static WhereCondition deserialize(String xml) throws XmlServerException {
        Document d = Util.parse(xml);
        return new WhereCondition(Util.getFirstTextNode(d.getDocumentElement(), "./leftpath"), Util.getFirstTextNode(d
                .getDocumentElement(), "./operator"), Util.getFirstTextNode(d.getDocumentElement(), "./rightvalueorpath"), Util
                .getFirstTextNode(d.getDocumentElement(), "./stringpredicate"), "yes".equals(Util.getFirstTextNode(d
                .getDocumentElement(), "./spellcheck")));
    }

    /**
     * Detects Intra joins
     * 
     * @param wcs
     * @return <code>true</code> if there are intr joins
     */
    public static boolean hasIntraJoin(ArrayList<WhereCondition> wcs) {
        for (Iterator<WhereCondition> iter = wcs.iterator(); iter.hasNext();) {
            WhereCondition condition = iter.next();
            if (WhereCondition.JOINS.equals(condition.getOperator())) {
                String leftConcept = getConceptFromPath(condition.getLeftPath());
                String rightConcept = getConceptFromPath(condition.getRightValueOrPath());
                if ((leftConcept != null) && (rightConcept != null) && (leftConcept.equals(rightConcept)))
                    return true;
            }
        }
        return false;
    }

    /**
     * Detects Intra joins
     * 
     * @param item The {@link IWhereItem} to inspect
     * @return <code>true</code> if there are intra joins
     */
    public static boolean hasIntraJoin(IWhereItem item) throws XmlServerException {

        try {
            if (item == null)
                return false;
            if (item instanceof WhereLogicOperator) {
                ArrayList<IWhereItem> c = ((WhereLogicOperator) item).getItems();
                for (Iterator<IWhereItem> iter = c.iterator(); iter.hasNext();) {
                    if (hasIntraJoin(iter.next()))
                        return true;
                }
                return false;
            } else if (item instanceof WhereCondition) {
                WhereCondition wc = (WhereCondition) item;
                String leftConcept = getConceptFromPath(wc.getLeftPath());
                String rightConcept = getConceptFromPath(wc.getRightValueOrPath());
                if ((leftConcept != null) && (rightConcept != null) && (leftConcept.equals(rightConcept)))
                    return true;
                return false;
            } else {
                throw new XmlServerException("Unknown element of whereCondition: " + item.getClass().getName());
            }
        } catch (Exception e) {
            throw new XmlServerException("Unable to detect intra document join: " + e.getLocalizedMessage());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "(" + getLeftPath() + " " + getOperator() + " " + getRightValueOrPath() + " " + getStringPredicate() + " "
                + isSpellCheck() + ")";
    }

    public static void main(String[] args) {

        try {
            WhereCondition wc = new WhereCondition("toto", ">", "titi", "NONE");

            System.out.println(wc.serialize());
            System.out.println(Util.getFirstTextNode(Util.parse(wc.serialize()).getDocumentElement(), "./operator"));
        } catch (Exception e) {
        }
    }

    private static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*");

    /**
     * Returns the first part - eg. the concept - from the path
     * 
     * @param path
     * @return the Concept
     */
    private static String getConceptFromPath(String path) {
        if (!path.endsWith("/"))
            path += "/";
        Matcher m = pathWithoutConditions.matcher(path);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }

}
