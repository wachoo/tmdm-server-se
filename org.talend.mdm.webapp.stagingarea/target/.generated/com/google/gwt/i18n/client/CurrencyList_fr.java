package com.google.gwt.i18n.client;

import com.google.gwt.i18n.client.impl.CurrencyDataImpl;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.HashMap;

public class CurrencyList_fr extends com.google.gwt.i18n.client.CurrencyList_ {
  
  @Override
  protected CurrencyData getDefaultJava() {
    return new CurrencyDataImpl("EUR", "€", 2, "€");
  }
  
  @Override
  protected native CurrencyData getDefaultNative() /*-{
    return [ "EUR", "€", 2, "€"];
  }-*/;
  
  @Override
  protected HashMap<String, CurrencyData> loadCurrencyMapJava() {
    HashMap<String, CurrencyData> result = super.loadCurrencyMapJava();
    // peseta andorrane
    result.put("ADP", new CurrencyDataImpl("ADP", "₧A", 128));
    // dirham des Émirats arabes unis
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH"));
    // afghani (1927–2002)
    result.put("AFA", new CurrencyDataImpl("AFA", "AFA", 130));
    // afghani
    result.put("AFN", new CurrencyDataImpl("AFN", "Af", 0));
    // lek albanais (1947–1961)
    result.put("ALK", new CurrencyDataImpl("ALK", "ALK", 130));
    // lek albanais
    result.put("ALL", new CurrencyDataImpl("ALL", "ALL", 0));
    // dram arménien
    result.put("AMD", new CurrencyDataImpl("AMD", "AMD", 0));
    // florin antillais
    result.put("ANG", new CurrencyDataImpl("ANG", "f.NA", 2));
    // kwanza angolais
    result.put("AOA", new CurrencyDataImpl("AOA", "Kz", 2));
    // kwanza angolais (1977–1990)
    result.put("AOK", new CurrencyDataImpl("AOK", "AOK", 130));
    // nouveau kwanza angolais (1990-2000)
    result.put("AON", new CurrencyDataImpl("AON", "AON", 130));
    // kwanza angolais réajusté (1995-1999)
    result.put("AOR", new CurrencyDataImpl("AOR", "AOR", 130));
    // austral argentin
    result.put("ARA", new CurrencyDataImpl("ARA", "₳", 130));
    // ARL
    result.put("ARL", new CurrencyDataImpl("ARL", "$L", 130));
    // ARM
    result.put("ARM", new CurrencyDataImpl("ARM", "m$n", 130));
    // peso argentin (1983–1985)
    result.put("ARP", new CurrencyDataImpl("ARP", "ARP", 130));
    // peso argentin
    result.put("ARS", new CurrencyDataImpl("ARS", "$AR", 2, "AR$"));
    // schilling autrichien
    result.put("ATS", new CurrencyDataImpl("ATS", "öS", 130));
    // dollar australien
    result.put("AUD", new CurrencyDataImpl("AUD", "$AU", 2, "AU$"));
    // florin arubais
    result.put("AWG", new CurrencyDataImpl("AWG", "f.AW", 2));
    // manat azéri (1993-2006)
    result.put("AZM", new CurrencyDataImpl("AZM", "AZM", 130));
    // manat azéri
    result.put("AZN", new CurrencyDataImpl("AZN", "man.", 2));
    // dinar bosniaque
    result.put("BAD", new CurrencyDataImpl("BAD", "BAD", 130));
    // mark convertible bosniaque
    result.put("BAM", new CurrencyDataImpl("BAM", "KM", 2));
    // dollar barbadien
    result.put("BBD", new CurrencyDataImpl("BBD", "Bds$", 2));
    // taka bangladeshi
    result.put("BDT", new CurrencyDataImpl("BDT", "Tk", 2, "Tk"));
    // franc belge (convertible)
    result.put("BEC", new CurrencyDataImpl("BEC", "BEC", 2));
    // franc belge
    result.put("BEF", new CurrencyDataImpl("BEF", "FB", 130));
    // franc belge (financier)
    result.put("BEL", new CurrencyDataImpl("BEL", "BEL", 2));
    // lev bulgare (1962–1999)
    result.put("BGL", new CurrencyDataImpl("BGL", "BGL", 130));
    // nouveau lev bulgare
    result.put("BGN", new CurrencyDataImpl("BGN", "BGN", 2));
    // dinar bahreïni
    result.put("BHD", new CurrencyDataImpl("BHD", "BD", 3));
    // franc burundais
    result.put("BIF", new CurrencyDataImpl("BIF", "FBu", 0));
    // dollar bermudien
    result.put("BMD", new CurrencyDataImpl("BMD", "$BM", 2));
    // dollar brunéien
    result.put("BND", new CurrencyDataImpl("BND", "$BN", 2));
    // boliviano
    result.put("BOB", new CurrencyDataImpl("BOB", "Bs", 2));
    // peso bolivien
    result.put("BOP", new CurrencyDataImpl("BOP", "$b.", 130));
    // mvdol bolivien
    result.put("BOV", new CurrencyDataImpl("BOV", "BOV", 2));
    // nouveau cruzeiro brésilien (1967–1986)
    result.put("BRB", new CurrencyDataImpl("BRB", "BRB", 130));
    // cruzado brésilien (1986–1989)
    result.put("BRC", new CurrencyDataImpl("BRC", "BRC", 130));
    // cruzeiro brésilien (1990–1993)
    result.put("BRE", new CurrencyDataImpl("BRE", "BRE", 130));
    // réal brésilien
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$"));
    // nouveau cruzado brésilien (1989–1990)
    result.put("BRN", new CurrencyDataImpl("BRN", "BRN", 130));
    // cruzeiro
    result.put("BRR", new CurrencyDataImpl("BRR", "BRR", 130));
    // dollar bahaméen
    result.put("BSD", new CurrencyDataImpl("BSD", "$BS", 2));
    // ngultrum bouthanais
    result.put("BTN", new CurrencyDataImpl("BTN", "Nu.", 2));
    // kyat birman
    result.put("BUK", new CurrencyDataImpl("BUK", "BUK", 130));
    // pula botswanais
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2));
    // nouveau rouble biélorusse (1994-1999)
    result.put("BYB", new CurrencyDataImpl("BYB", "BYB", 130));
    // rouble biélorusse
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 0));
    // dollar bélizéen
    result.put("BZD", new CurrencyDataImpl("BZD", "$BZ", 2));
    // dollar canadien
    result.put("CAD", new CurrencyDataImpl("CAD", "$CA", 2, "C$"));
    // franc congolais
    result.put("CDF", new CurrencyDataImpl("CDF", "FrCD", 2));
    // euro WIR
    result.put("CHE", new CurrencyDataImpl("CHE", "CHE", 2));
    // franc suisse
    result.put("CHF", new CurrencyDataImpl("CHF", "CHF", 2, "CHF"));
    // franc WIR
    result.put("CHW", new CurrencyDataImpl("CHW", "CHW", 2));
    // CLE
    result.put("CLE", new CurrencyDataImpl("CLE", "Eº", 130));
    // unité d’investissement chilienne
    result.put("CLF", new CurrencyDataImpl("CLF", "CLF", 0));
    // peso chilien
    result.put("CLP", new CurrencyDataImpl("CLP", "$CL", 0, "CL$"));
    // yuan renminbi chinois
    result.put("CNY", new CurrencyDataImpl("CNY", "Ұ", 2, "RMB¥"));
    // peso colombien
    result.put("COP", new CurrencyDataImpl("COP", "$CO", 0, "COL$"));
    // Unité de valeur réelle colombienne
    result.put("COU", new CurrencyDataImpl("COU", "COU", 2));
    // colón costaricain
    result.put("CRC", new CurrencyDataImpl("CRC", "₡", 0, "CR₡"));
    // dinar serbo-monténégrin
    result.put("CSD", new CurrencyDataImpl("CSD", "CSD", 130));
    // couronne forte tchécoslovaque
    result.put("CSK", new CurrencyDataImpl("CSK", "CSK", 130));
    // CUC
    result.put("CUC", new CurrencyDataImpl("CUC", "CUC$", 2));
    // peso cubain
    result.put("CUP", new CurrencyDataImpl("CUP", "$CU", 2, "$MN"));
    // escudo capverdien
    result.put("CVE", new CurrencyDataImpl("CVE", "$CV", 2));
    // livre chypriote
    result.put("CYP", new CurrencyDataImpl("CYP", "£CY", 130));
    // couronne tchèque
    result.put("CZK", new CurrencyDataImpl("CZK", "Kč", 2, "Kč"));
    // mark est-allemand
    result.put("DDM", new CurrencyDataImpl("DDM", "DDM", 130));
    // mark allemand
    result.put("DEM", new CurrencyDataImpl("DEM", "DM", 130));
    // franc djiboutien
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0));
    // couronne danoise
    result.put("DKK", new CurrencyDataImpl("DKK", "krD", 2, "kr"));
    // peso dominicain
    result.put("DOP", new CurrencyDataImpl("DOP", "RD$", 2, "RD$"));
    // dinar algérien
    result.put("DZD", new CurrencyDataImpl("DZD", "DA", 2));
    // sucre équatorien
    result.put("ECS", new CurrencyDataImpl("ECS", "ECS", 130));
    // unité de valeur constante équatoriale (UVC)
    result.put("ECV", new CurrencyDataImpl("ECV", "ECV", 2));
    // couronne estonienne
    result.put("EEK", new CurrencyDataImpl("EEK", "krE", 2));
    // livre égyptienne
    result.put("EGP", new CurrencyDataImpl("EGP", "£EG", 2, "LE"));
    // EQE
    result.put("EQE", new CurrencyDataImpl("EQE", "EQE", 130));
    // nafka érythréen
    result.put("ERN", new CurrencyDataImpl("ERN", "Nfk", 2));
    // peseta espagnole (compte A)
    result.put("ESA", new CurrencyDataImpl("ESA", "ESA", 2));
    // peseta espagnole (compte convertible)
    result.put("ESB", new CurrencyDataImpl("ESB", "ESB", 2));
    // peseta espagnole
    result.put("ESP", new CurrencyDataImpl("ESP", "₧", 128));
    // birr éthiopien
    result.put("ETB", new CurrencyDataImpl("ETB", "Br", 2));
    // euro
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€"));
    // mark finlandais
    result.put("FIM", new CurrencyDataImpl("FIM", "mk", 130));
    // dollar fidjien
    result.put("FJD", new CurrencyDataImpl("FJD", "$FJ", 2));
    // livre des Falkland
    result.put("FKP", new CurrencyDataImpl("FKP", "£FK", 2));
    // franc français
    result.put("FRF", new CurrencyDataImpl("FRF", "F", 130));
    // livre sterling
    result.put("GBP", new CurrencyDataImpl("GBP", "£UK", 2, "GB£"));
    // Georgian Kupon Larit
    result.put("GEK", new CurrencyDataImpl("GEK", "KlGe", 130));
    // lari géorgien
    result.put("GEL", new CurrencyDataImpl("GEL", "GEL", 2));
    // cédi ghanéen (1967–2007)
    result.put("GHC", new CurrencyDataImpl("GHC", "₵", 130));
    // cédi ghanéen
    result.put("GHS", new CurrencyDataImpl("GHS", "GH₵", 2));
    // livre de Gibraltar
    result.put("GIP", new CurrencyDataImpl("GIP", "£GI", 2));
    // dalasi gambien
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2));
    // franc guinéen
    result.put("GNF", new CurrencyDataImpl("GNF", "FG", 0));
    // syli guinéen
    result.put("GNS", new CurrencyDataImpl("GNS", "GNS", 130));
    // ekwélé équatoguinéen
    result.put("GQE", new CurrencyDataImpl("GQE", "GQE", 130));
    // drachme grecque
    result.put("GRD", new CurrencyDataImpl("GRD", "₯", 130));
    // quetzal guatémaltèque
    result.put("GTQ", new CurrencyDataImpl("GTQ", "GTQ", 2));
    // escudo de Guinée portugaise
    result.put("GWE", new CurrencyDataImpl("GWE", "EscGW", 130));
    // peso bissau-guinéen
    result.put("GWP", new CurrencyDataImpl("GWP", "GWP", 2));
    // dollar guyanien
    result.put("GYD", new CurrencyDataImpl("GYD", "$GY", 0));
    // dollar de Hong Kong
    result.put("HKD", new CurrencyDataImpl("HKD", "$HK", 2, "HK$"));
    // lempira hondurien
    result.put("HNL", new CurrencyDataImpl("HNL", "HNL", 2));
    // dinar croate
    result.put("HRD", new CurrencyDataImpl("HRD", "HRD", 130));
    // kuna croate
    result.put("HRK", new CurrencyDataImpl("HRK", "kn", 2));
    // gourde haïtienne
    result.put("HTG", new CurrencyDataImpl("HTG", "HTG", 2));
    // forint hongrois
    result.put("HUF", new CurrencyDataImpl("HUF", "Ft", 0));
    // roupie indonésienne
    result.put("IDR", new CurrencyDataImpl("IDR", "Rp", 0));
    // livre irlandaise
    result.put("IEP", new CurrencyDataImpl("IEP", "£IE", 130));
    // livre israélienne
    result.put("ILP", new CurrencyDataImpl("ILP", "£IL", 130));
    // nouveau shekel israélien
    result.put("ILS", new CurrencyDataImpl("ILS", "₪", 2, "IL₪"));
    // roupie indienne
    result.put("INR", new CurrencyDataImpl("INR", "Rs", 2, "Rs"));
    // dinar irakien
    result.put("IQD", new CurrencyDataImpl("IQD", "IQD", 0));
    // rial iranien
    result.put("IRR", new CurrencyDataImpl("IRR", "IRR", 0));
    // couronne islandaise
    result.put("ISK", new CurrencyDataImpl("ISK", "krI", 0, "kr"));
    // lire italienne
    result.put("ITL", new CurrencyDataImpl("ITL", "₤IT", 128));
    // dollar jamaïcain
    result.put("JMD", new CurrencyDataImpl("JMD", "$JM", 2, "JA$"));
    // dinar jordanien
    result.put("JOD", new CurrencyDataImpl("JOD", "DJ", 3));
    // yen japonais
    result.put("JPY", new CurrencyDataImpl("JPY", "¥JP", 0, "JP¥"));
    // shilling kényan
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2));
    // som kirghize
    result.put("KGS", new CurrencyDataImpl("KGS", "KGS", 2));
    // riel cambodgien
    result.put("KHR", new CurrencyDataImpl("KHR", "KHR", 2));
    // franc comorien
    result.put("KMF", new CurrencyDataImpl("KMF", "FC", 0));
    // won nord-coréen
    result.put("KPW", new CurrencyDataImpl("KPW", "₩KP", 0));
    // won sud-coréen
    result.put("KRW", new CurrencyDataImpl("KRW", "₩", 0, "KR₩"));
    // dinar koweïtien
    result.put("KWD", new CurrencyDataImpl("KWD", "DK", 3));
    // dollar des îles Caïmanes
    result.put("KYD", new CurrencyDataImpl("KYD", "$KY", 2));
    // tenge kazakh
    result.put("KZT", new CurrencyDataImpl("KZT", "KZT", 2));
    // kip loatien
    result.put("LAK", new CurrencyDataImpl("LAK", "₭", 0));
    // livre libanaise
    result.put("LBP", new CurrencyDataImpl("LBP", "£LB", 0));
    // roupie srilankaise
    result.put("LKR", new CurrencyDataImpl("LKR", "RsSL", 2, "SLRs"));
    // dollar libérien
    result.put("LRD", new CurrencyDataImpl("LRD", "$LR", 2));
    // loti lesothan
    result.put("LSL", new CurrencyDataImpl("LSL", "LSL", 2));
    // LSM
    result.put("LSM", new CurrencyDataImpl("LSM", "LSM", 130));
    // litas lituanien
    result.put("LTL", new CurrencyDataImpl("LTL", "Lt", 2));
    // talonas lituanien
    result.put("LTT", new CurrencyDataImpl("LTT", "LTT", 130));
    // franc convertible luxembourgeois
    result.put("LUC", new CurrencyDataImpl("LUC", "LUC", 2));
    // franc luxembourgeois
    result.put("LUF", new CurrencyDataImpl("LUF", "LUF", 128));
    // franc financier luxembourgeois
    result.put("LUL", new CurrencyDataImpl("LUL", "LUL", 2));
    // lats letton
    result.put("LVL", new CurrencyDataImpl("LVL", "Ls", 2));
    // rouble letton
    result.put("LVR", new CurrencyDataImpl("LVR", "LVR", 130));
    // dinar lybien
    result.put("LYD", new CurrencyDataImpl("LYD", "DL", 3));
    // dirham marocain
    result.put("MAD", new CurrencyDataImpl("MAD", "MAD", 2));
    // franc marocain
    result.put("MAF", new CurrencyDataImpl("MAF", "MAF", 130));
    // leu moldave
    result.put("MDL", new CurrencyDataImpl("MDL", "MDL", 2));
    // ariary malgache
    result.put("MGA", new CurrencyDataImpl("MGA", "MGA", 0));
    // franc malgache
    result.put("MGF", new CurrencyDataImpl("MGF", "MGF", 128));
    // denar macédonien
    result.put("MKD", new CurrencyDataImpl("MKD", "MKD", 2));
    // franc malien
    result.put("MLF", new CurrencyDataImpl("MLF", "MLF", 130));
    // kyat myanmarais
    result.put("MMK", new CurrencyDataImpl("MMK", "MMK", 0));
    // tugrik mongol
    result.put("MNT", new CurrencyDataImpl("MNT", "₮", 0, "MN₮"));
    // pataca macanaise
    result.put("MOP", new CurrencyDataImpl("MOP", "MOP$", 2));
    // ouguiya mauritanien
    result.put("MRO", new CurrencyDataImpl("MRO", "UM", 0));
    // lire maltaise
    result.put("MTL", new CurrencyDataImpl("MTL", "Lm", 130));
    // livre maltaise
    result.put("MTP", new CurrencyDataImpl("MTP", "£MT", 130));
    // roupie mauricienne
    result.put("MUR", new CurrencyDataImpl("MUR", "RsMU", 0));
    // rufiyaa maldivienne
    result.put("MVR", new CurrencyDataImpl("MVR", "MVR", 2));
    // kwacha malawite
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2));
    // peso mexicain
    result.put("MXN", new CurrencyDataImpl("MXN", "Mex$", 2, "Mex$"));
    // peso d’argent mexicain (1861–1992)
    result.put("MXP", new CurrencyDataImpl("MXP", "MX$", 130));
    // unité de conversion mexicaine (UDI)
    result.put("MXV", new CurrencyDataImpl("MXV", "MXV", 2));
    // ringgit malais
    result.put("MYR", new CurrencyDataImpl("MYR", "RM", 2, "RM"));
    // escudo mozambicain
    result.put("MZE", new CurrencyDataImpl("MZE", "MZE", 130));
    // métical
    result.put("MZM", new CurrencyDataImpl("MZM", "Mt", 130));
    // metical mozambicain
    result.put("MZN", new CurrencyDataImpl("MZN", "MTn", 2));
    // dollar namibien
    result.put("NAD", new CurrencyDataImpl("NAD", "$NA", 2));
    // naira nigérian
    result.put("NGN", new CurrencyDataImpl("NGN", "₦", 2));
    // córdoba nicaraguayen (1912–1988)
    result.put("NIC", new CurrencyDataImpl("NIC", "NIC", 130));
    // córdoba oro nicaraguayen
    result.put("NIO", new CurrencyDataImpl("NIO", "C$", 2));
    // florin néerlandais
    result.put("NLG", new CurrencyDataImpl("NLG", "fl", 130));
    // couronne norvégienne
    result.put("NOK", new CurrencyDataImpl("NOK", "krN", 2, "NOkr"));
    // roupie népalaise
    result.put("NPR", new CurrencyDataImpl("NPR", "RsNP", 2));
    // dollar néo-zélandais
    result.put("NZD", new CurrencyDataImpl("NZD", "$NZ", 2));
    // rial omani
    result.put("OMR", new CurrencyDataImpl("OMR", "OMR", 3));
    // balboa panaméen
    result.put("PAB", new CurrencyDataImpl("PAB", "B/.", 2, "B/."));
    // inti péruvien
    result.put("PEI", new CurrencyDataImpl("PEI", "I/.", 130));
    // nouveau sol péruvien
    result.put("PEN", new CurrencyDataImpl("PEN", "S/.", 2, "S/."));
    // sol péruvien
    result.put("PES", new CurrencyDataImpl("PES", "PES", 130));
    // kina papouan-néo-guinéen
    result.put("PGK", new CurrencyDataImpl("PGK", "PGK", 2));
    // peso philippin
    result.put("PHP", new CurrencyDataImpl("PHP", "₱", 2, "PHP"));
    // roupie pakistanaise
    result.put("PKR", new CurrencyDataImpl("PKR", "RsPK", 0, "PKRs."));
    // zloty polonais
    result.put("PLN", new CurrencyDataImpl("PLN", "zł", 2));
    // zloty polonais (1950–1995)
    result.put("PLZ", new CurrencyDataImpl("PLZ", "PLZ", 130));
    // escudo portugais
    result.put("PTE", new CurrencyDataImpl("PTE", "Esc", 130));
    // guaraní paraguayen
    result.put("PYG", new CurrencyDataImpl("PYG", "₲", 0));
    // rial qatari
    result.put("QAR", new CurrencyDataImpl("QAR", "RQ", 2));
    // dollar rhodésien
    result.put("RHD", new CurrencyDataImpl("RHD", "$RH", 130));
    // ancien leu roumain
    result.put("ROL", new CurrencyDataImpl("ROL", "ROL", 130));
    // leu roumain
    result.put("RON", new CurrencyDataImpl("RON", "RON", 2));
    // dinar serbe
    result.put("RSD", new CurrencyDataImpl("RSD", "din.", 0));
    // rouble russe
    result.put("RUB", new CurrencyDataImpl("RUB", "руб", 2, "руб"));
    // rouble russe (1991–1998)
    result.put("RUR", new CurrencyDataImpl("RUR", "RUR", 130));
    // franc rwandais
    result.put("RWF", new CurrencyDataImpl("RWF", "FR", 0));
    // rial saoudien
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR"));
    // dollar des îles Salomon
    result.put("SBD", new CurrencyDataImpl("SBD", "$SB", 2));
    // roupie des Seychelles
    result.put("SCR", new CurrencyDataImpl("SCR", "SRe", 2));
    // dinar soudanais
    result.put("SDD", new CurrencyDataImpl("SDD", "LSd", 130));
    // livre soudanaise
    result.put("SDG", new CurrencyDataImpl("SDG", "SDG", 2));
    // livre soudanaise (1956–2007)
    result.put("SDP", new CurrencyDataImpl("SDP", "SDP", 130));
    // couronne suédoise
    result.put("SEK", new CurrencyDataImpl("SEK", "krS", 2, "kr"));
    // dollar de Singapour
    result.put("SGD", new CurrencyDataImpl("SGD", "$SG", 2, "S$"));
    // livre de Sainte-Hélène
    result.put("SHP", new CurrencyDataImpl("SHP", "£SH", 2));
    // tolar slovène
    result.put("SIT", new CurrencyDataImpl("SIT", "SIT", 130));
    // couronne slovaque
    result.put("SKK", new CurrencyDataImpl("SKK", "Sk", 130));
    // leone sierra-léonais
    result.put("SLL", new CurrencyDataImpl("SLL", "Le", 0));
    // shilling somalien
    result.put("SOS", new CurrencyDataImpl("SOS", "Ssh", 0));
    // dollar surinamais
    result.put("SRD", new CurrencyDataImpl("SRD", "$SR", 2));
    // florin surinamais
    result.put("SRG", new CurrencyDataImpl("SRG", "Sf", 130));
    // dobra santoméen
    result.put("STD", new CurrencyDataImpl("STD", "Db", 0));
    // rouble soviétique
    result.put("SUR", new CurrencyDataImpl("SUR", "SUR", 130));
    // colón salvadorien
    result.put("SVC", new CurrencyDataImpl("SVC", "₡SV", 130));
    // livre syrienne
    result.put("SYP", new CurrencyDataImpl("SYP", "£SY", 0));
    // lilangeni swazi
    result.put("SZL", new CurrencyDataImpl("SZL", "SZL", 2));
    // baht thaïlandais
    result.put("THB", new CurrencyDataImpl("THB", "฿", 2, "THB"));
    // rouble tadjik
    result.put("TJR", new CurrencyDataImpl("TJR", "TJR", 130));
    // somoni tadjik
    result.put("TJS", new CurrencyDataImpl("TJS", "TJS", 2));
    // manat turkmène
    result.put("TMM", new CurrencyDataImpl("TMM", "TMM", 128));
    // dinar tunisien
    result.put("TND", new CurrencyDataImpl("TND", "DT", 3));
    // pa’anga tongan
    result.put("TOP", new CurrencyDataImpl("TOP", "T$", 2));
    // escudo timorais
    result.put("TPE", new CurrencyDataImpl("TPE", "TPE", 130));
    // livre turque (1844–2005)
    result.put("TRL", new CurrencyDataImpl("TRL", "TRL", 128));
    // nouvelle livre turque
    result.put("TRY", new CurrencyDataImpl("TRY", "TL", 2, "YTL"));
    // dollar trinidadien
    result.put("TTD", new CurrencyDataImpl("TTD", "$TT", 2));
    // nouveau dollar taïwanais
    result.put("TWD", new CurrencyDataImpl("TWD", "NT$", 2, "NT$"));
    // shilling tanzanien
    result.put("TZS", new CurrencyDataImpl("TZS", "TSh", 0));
    // hryvnia ukrainienne
    result.put("UAH", new CurrencyDataImpl("UAH", "₴", 2));
    // karbovanets ukrainien (1992–1996)
    result.put("UAK", new CurrencyDataImpl("UAK", "UAK", 130));
    // shilling ougandais (1966–1987)
    result.put("UGS", new CurrencyDataImpl("UGS", "UGS", 130));
    // shilling ougandais
    result.put("UGX", new CurrencyDataImpl("UGX", "USh", 0));
    // dollar des États-Unis
    result.put("USD", new CurrencyDataImpl("USD", "$US", 2, "US$"));
    // dollar des Etats-Unis (jour suivant)
    result.put("USN", new CurrencyDataImpl("USN", "USN", 2));
    // dollar des Etats-Unis (jour même)
    result.put("USS", new CurrencyDataImpl("USS", "USS", 2));
    // peso uruguayen (unités indexées)
    result.put("UYI", new CurrencyDataImpl("UYI", "UYI", 2));
    // peso uruguayen (1975–1993)
    result.put("UYP", new CurrencyDataImpl("UYP", "UYP", 130));
    // peso uruguayen
    result.put("UYU", new CurrencyDataImpl("UYU", "$UY", 2, "UY$"));
    // sum ouzbek
    result.put("UZS", new CurrencyDataImpl("UZS", "UZS", 0));
    // bolívar vénézuélien (1879–2008)
    result.put("VEB", new CurrencyDataImpl("VEB", "VEB", 130));
    // bolivar fuerte vénézuélien
    result.put("VEF", new CurrencyDataImpl("VEF", "Bs.F.", 2));
    // dông vietnamien
    result.put("VND", new CurrencyDataImpl("VND", "₫", 24, "₫"));
    // vatu vanuatuan
    result.put("VUV", new CurrencyDataImpl("VUV", "VT", 0));
    // tala samoan
    result.put("WST", new CurrencyDataImpl("WST", "WS$", 2));
    // franc CFA (BEAC)
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0));
    // once troy d’argent
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 2));
    // or
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 2));
    // unité européenne composée
    result.put("XBA", new CurrencyDataImpl("XBA", "XBA", 2));
    // unité monétaire européenne
    result.put("XBB", new CurrencyDataImpl("XBB", "XBB", 2));
    // unité de compte 9 européenne (UEC-9)
    result.put("XBC", new CurrencyDataImpl("XBC", "XBC", 2));
    // unité de compte 17 européenne (UEC-17)
    result.put("XBD", new CurrencyDataImpl("XBD", "XBD", 2));
    // dollar des Caraïbes orientales
    result.put("XCD", new CurrencyDataImpl("XCD", "EC$", 2));
    // droit de tirage spécial
    result.put("XDR", new CurrencyDataImpl("XDR", "XDR", 2));
    // unité de compte européenne (ECU)
    result.put("XEU", new CurrencyDataImpl("XEU", "XEU", 2));
    // franc or
    result.put("XFO", new CurrencyDataImpl("XFO", "XFO", 2));
    // franc UIC
    result.put("XFU", new CurrencyDataImpl("XFU", "XFU", 2));
    // franc CFA (BCEAO)
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0));
    // once troy de palladium
    result.put("XPD", new CurrencyDataImpl("XPD", "XPD", 2));
    // franc CFP
    result.put("XPF", new CurrencyDataImpl("XPF", "FCFP", 0));
    // platine
    result.put("XPT", new CurrencyDataImpl("XPT", "XPT", 2));
    // unité de fonds RINET
    result.put("XRE", new CurrencyDataImpl("XRE", "XRE", 2));
    // (devise de test)
    result.put("XTS", new CurrencyDataImpl("XTS", "XTS", 2));
    // (devise inconnue ou invalide)
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 2));
    // dinar du Yémen
    result.put("YDD", new CurrencyDataImpl("YDD", "YDD", 130));
    // rial yéménite
    result.put("YER", new CurrencyDataImpl("YER", "RY", 0, "YER"));
    // dinar fort yougoslave (1966–1989)
    result.put("YUD", new CurrencyDataImpl("YUD", "YUD", 130));
    // nouveau dinar yougoslave (1994–2003)
    result.put("YUM", new CurrencyDataImpl("YUM", "YUM", 130));
    // dinar convertible yougoslave (1990–1992)
    result.put("YUN", new CurrencyDataImpl("YUN", "YUN", 130));
    // rand sud-africain (financier)
    result.put("ZAL", new CurrencyDataImpl("ZAL", "ZAL", 2));
    // rand sud-africain
    result.put("ZAR", new CurrencyDataImpl("ZAR", "R", 2, "ZAR"));
    // kwacha zambien
    result.put("ZMK", new CurrencyDataImpl("ZMK", "ZK", 0));
    // nouveau zaïre zaïrien
    result.put("ZRN", new CurrencyDataImpl("ZRN", "NZ", 130));
    // zaïre zaïrois
    result.put("ZRZ", new CurrencyDataImpl("ZRZ", "ZRZ", 130));
    // dollar zimbabwéen
    result.put("ZWD", new CurrencyDataImpl("ZWD", "$Z", 128));
    return result;
  }
  
  @Override
  protected JavaScriptObject loadCurrencyMapNative() {
    return overrideMap(super.loadCurrencyMapNative(), loadMyCurrencyMapOverridesNative());
  }
  
  private native JavaScriptObject loadMyCurrencyMapOverridesNative() /*-{
    return {
      // peseta andorrane
      "ADP": [ "ADP", "₧A", 128],
      // dirham des Émirats arabes unis
      "AED": [ "AED", "DH", 2, "DH"],
      // afghani (1927–2002)
      "AFA": [ "AFA", "AFA", 130],
      // afghani
      "AFN": [ "AFN", "Af", 0],
      // lek albanais (1947–1961)
      "ALK": [ "ALK", "ALK", 130],
      // lek albanais
      "ALL": [ "ALL", "ALL", 0],
      // dram arménien
      "AMD": [ "AMD", "AMD", 0],
      // florin antillais
      "ANG": [ "ANG", "f.NA", 2],
      // kwanza angolais
      "AOA": [ "AOA", "Kz", 2],
      // kwanza angolais (1977–1990)
      "AOK": [ "AOK", "AOK", 130],
      // nouveau kwanza angolais (1990-2000)
      "AON": [ "AON", "AON", 130],
      // kwanza angolais réajusté (1995-1999)
      "AOR": [ "AOR", "AOR", 130],
      // austral argentin
      "ARA": [ "ARA", "₳", 130],
      // ARL
      "ARL": [ "ARL", "$L", 130],
      // ARM
      "ARM": [ "ARM", "m$n", 130],
      // peso argentin (1983–1985)
      "ARP": [ "ARP", "ARP", 130],
      // peso argentin
      "ARS": [ "ARS", "$AR", 2, "AR$"],
      // schilling autrichien
      "ATS": [ "ATS", "öS", 130],
      // dollar australien
      "AUD": [ "AUD", "$AU", 2, "AU$"],
      // florin arubais
      "AWG": [ "AWG", "f.AW", 2],
      // manat azéri (1993-2006)
      "AZM": [ "AZM", "AZM", 130],
      // manat azéri
      "AZN": [ "AZN", "man.", 2],
      // dinar bosniaque
      "BAD": [ "BAD", "BAD", 130],
      // mark convertible bosniaque
      "BAM": [ "BAM", "KM", 2],
      // dollar barbadien
      "BBD": [ "BBD", "Bds$", 2],
      // taka bangladeshi
      "BDT": [ "BDT", "Tk", 2, "Tk"],
      // franc belge (convertible)
      "BEC": [ "BEC", "BEC", 2],
      // franc belge
      "BEF": [ "BEF", "FB", 130],
      // franc belge (financier)
      "BEL": [ "BEL", "BEL", 2],
      // lev bulgare (1962–1999)
      "BGL": [ "BGL", "BGL", 130],
      // nouveau lev bulgare
      "BGN": [ "BGN", "BGN", 2],
      // dinar bahreïni
      "BHD": [ "BHD", "BD", 3],
      // franc burundais
      "BIF": [ "BIF", "FBu", 0],
      // dollar bermudien
      "BMD": [ "BMD", "$BM", 2],
      // dollar brunéien
      "BND": [ "BND", "$BN", 2],
      // boliviano
      "BOB": [ "BOB", "Bs", 2],
      // peso bolivien
      "BOP": [ "BOP", "$b.", 130],
      // mvdol bolivien
      "BOV": [ "BOV", "BOV", 2],
      // nouveau cruzeiro brésilien (1967–1986)
      "BRB": [ "BRB", "BRB", 130],
      // cruzado brésilien (1986–1989)
      "BRC": [ "BRC", "BRC", 130],
      // cruzeiro brésilien (1990–1993)
      "BRE": [ "BRE", "BRE", 130],
      // réal brésilien
      "BRL": [ "BRL", "R$", 2, "R$"],
      // nouveau cruzado brésilien (1989–1990)
      "BRN": [ "BRN", "BRN", 130],
      // cruzeiro
      "BRR": [ "BRR", "BRR", 130],
      // dollar bahaméen
      "BSD": [ "BSD", "$BS", 2],
      // ngultrum bouthanais
      "BTN": [ "BTN", "Nu.", 2],
      // kyat birman
      "BUK": [ "BUK", "BUK", 130],
      // pula botswanais
      "BWP": [ "BWP", "BWP", 2],
      // nouveau rouble biélorusse (1994-1999)
      "BYB": [ "BYB", "BYB", 130],
      // rouble biélorusse
      "BYR": [ "BYR", "BYR", 0],
      // dollar bélizéen
      "BZD": [ "BZD", "$BZ", 2],
      // dollar canadien
      "CAD": [ "CAD", "$CA", 2, "C$"],
      // franc congolais
      "CDF": [ "CDF", "FrCD", 2],
      // euro WIR
      "CHE": [ "CHE", "CHE", 2],
      // franc suisse
      "CHF": [ "CHF", "CHF", 2, "CHF"],
      // franc WIR
      "CHW": [ "CHW", "CHW", 2],
      // CLE
      "CLE": [ "CLE", "Eº", 130],
      // unité d’investissement chilienne
      "CLF": [ "CLF", "CLF", 0],
      // peso chilien
      "CLP": [ "CLP", "$CL", 0, "CL$"],
      // yuan renminbi chinois
      "CNY": [ "CNY", "Ұ", 2, "RMB¥"],
      // peso colombien
      "COP": [ "COP", "$CO", 0, "COL$"],
      // Unité de valeur réelle colombienne
      "COU": [ "COU", "COU", 2],
      // colón costaricain
      "CRC": [ "CRC", "₡", 0, "CR₡"],
      // dinar serbo-monténégrin
      "CSD": [ "CSD", "CSD", 130],
      // couronne forte tchécoslovaque
      "CSK": [ "CSK", "CSK", 130],
      // CUC
      "CUC": [ "CUC", "CUC$", 2],
      // peso cubain
      "CUP": [ "CUP", "$CU", 2, "$MN"],
      // escudo capverdien
      "CVE": [ "CVE", "$CV", 2],
      // livre chypriote
      "CYP": [ "CYP", "£CY", 130],
      // couronne tchèque
      "CZK": [ "CZK", "Kč", 2, "Kč"],
      // mark est-allemand
      "DDM": [ "DDM", "DDM", 130],
      // mark allemand
      "DEM": [ "DEM", "DM", 130],
      // franc djiboutien
      "DJF": [ "DJF", "Fdj", 0],
      // couronne danoise
      "DKK": [ "DKK", "krD", 2, "kr"],
      // peso dominicain
      "DOP": [ "DOP", "RD$", 2, "RD$"],
      // dinar algérien
      "DZD": [ "DZD", "DA", 2],
      // sucre équatorien
      "ECS": [ "ECS", "ECS", 130],
      // unité de valeur constante équatoriale (UVC)
      "ECV": [ "ECV", "ECV", 2],
      // couronne estonienne
      "EEK": [ "EEK", "krE", 2],
      // livre égyptienne
      "EGP": [ "EGP", "£EG", 2, "LE"],
      // EQE
      "EQE": [ "EQE", "EQE", 130],
      // nafka érythréen
      "ERN": [ "ERN", "Nfk", 2],
      // peseta espagnole (compte A)
      "ESA": [ "ESA", "ESA", 2],
      // peseta espagnole (compte convertible)
      "ESB": [ "ESB", "ESB", 2],
      // peseta espagnole
      "ESP": [ "ESP", "₧", 128],
      // birr éthiopien
      "ETB": [ "ETB", "Br", 2],
      // euro
      "EUR": [ "EUR", "€", 2, "€"],
      // mark finlandais
      "FIM": [ "FIM", "mk", 130],
      // dollar fidjien
      "FJD": [ "FJD", "$FJ", 2],
      // livre des Falkland
      "FKP": [ "FKP", "£FK", 2],
      // franc français
      "FRF": [ "FRF", "F", 130],
      // livre sterling
      "GBP": [ "GBP", "£UK", 2, "GB£"],
      // Georgian Kupon Larit
      "GEK": [ "GEK", "KlGe", 130],
      // lari géorgien
      "GEL": [ "GEL", "GEL", 2],
      // cédi ghanéen (1967–2007)
      "GHC": [ "GHC", "₵", 130],
      // cédi ghanéen
      "GHS": [ "GHS", "GH₵", 2],
      // livre de Gibraltar
      "GIP": [ "GIP", "£GI", 2],
      // dalasi gambien
      "GMD": [ "GMD", "GMD", 2],
      // franc guinéen
      "GNF": [ "GNF", "FG", 0],
      // syli guinéen
      "GNS": [ "GNS", "GNS", 130],
      // ekwélé équatoguinéen
      "GQE": [ "GQE", "GQE", 130],
      // drachme grecque
      "GRD": [ "GRD", "₯", 130],
      // quetzal guatémaltèque
      "GTQ": [ "GTQ", "GTQ", 2],
      // escudo de Guinée portugaise
      "GWE": [ "GWE", "EscGW", 130],
      // peso bissau-guinéen
      "GWP": [ "GWP", "GWP", 2],
      // dollar guyanien
      "GYD": [ "GYD", "$GY", 0],
      // dollar de Hong Kong
      "HKD": [ "HKD", "$HK", 2, "HK$"],
      // lempira hondurien
      "HNL": [ "HNL", "HNL", 2],
      // dinar croate
      "HRD": [ "HRD", "HRD", 130],
      // kuna croate
      "HRK": [ "HRK", "kn", 2],
      // gourde haïtienne
      "HTG": [ "HTG", "HTG", 2],
      // forint hongrois
      "HUF": [ "HUF", "Ft", 0],
      // roupie indonésienne
      "IDR": [ "IDR", "Rp", 0],
      // livre irlandaise
      "IEP": [ "IEP", "£IE", 130],
      // livre israélienne
      "ILP": [ "ILP", "£IL", 130],
      // nouveau shekel israélien
      "ILS": [ "ILS", "₪", 2, "IL₪"],
      // roupie indienne
      "INR": [ "INR", "Rs", 2, "Rs"],
      // dinar irakien
      "IQD": [ "IQD", "IQD", 0],
      // rial iranien
      "IRR": [ "IRR", "IRR", 0],
      // couronne islandaise
      "ISK": [ "ISK", "krI", 0, "kr"],
      // lire italienne
      "ITL": [ "ITL", "₤IT", 128],
      // dollar jamaïcain
      "JMD": [ "JMD", "$JM", 2, "JA$"],
      // dinar jordanien
      "JOD": [ "JOD", "DJ", 3],
      // yen japonais
      "JPY": [ "JPY", "¥JP", 0, "JP¥"],
      // shilling kényan
      "KES": [ "KES", "Ksh", 2],
      // som kirghize
      "KGS": [ "KGS", "KGS", 2],
      // riel cambodgien
      "KHR": [ "KHR", "KHR", 2],
      // franc comorien
      "KMF": [ "KMF", "FC", 0],
      // won nord-coréen
      "KPW": [ "KPW", "₩KP", 0],
      // won sud-coréen
      "KRW": [ "KRW", "₩", 0, "KR₩"],
      // dinar koweïtien
      "KWD": [ "KWD", "DK", 3],
      // dollar des îles Caïmanes
      "KYD": [ "KYD", "$KY", 2],
      // tenge kazakh
      "KZT": [ "KZT", "KZT", 2],
      // kip loatien
      "LAK": [ "LAK", "₭", 0],
      // livre libanaise
      "LBP": [ "LBP", "£LB", 0],
      // roupie srilankaise
      "LKR": [ "LKR", "RsSL", 2, "SLRs"],
      // dollar libérien
      "LRD": [ "LRD", "$LR", 2],
      // loti lesothan
      "LSL": [ "LSL", "LSL", 2],
      // LSM
      "LSM": [ "LSM", "LSM", 130],
      // litas lituanien
      "LTL": [ "LTL", "Lt", 2],
      // talonas lituanien
      "LTT": [ "LTT", "LTT", 130],
      // franc convertible luxembourgeois
      "LUC": [ "LUC", "LUC", 2],
      // franc luxembourgeois
      "LUF": [ "LUF", "LUF", 128],
      // franc financier luxembourgeois
      "LUL": [ "LUL", "LUL", 2],
      // lats letton
      "LVL": [ "LVL", "Ls", 2],
      // rouble letton
      "LVR": [ "LVR", "LVR", 130],
      // dinar lybien
      "LYD": [ "LYD", "DL", 3],
      // dirham marocain
      "MAD": [ "MAD", "MAD", 2],
      // franc marocain
      "MAF": [ "MAF", "MAF", 130],
      // leu moldave
      "MDL": [ "MDL", "MDL", 2],
      // ariary malgache
      "MGA": [ "MGA", "MGA", 0],
      // franc malgache
      "MGF": [ "MGF", "MGF", 128],
      // denar macédonien
      "MKD": [ "MKD", "MKD", 2],
      // franc malien
      "MLF": [ "MLF", "MLF", 130],
      // kyat myanmarais
      "MMK": [ "MMK", "MMK", 0],
      // tugrik mongol
      "MNT": [ "MNT", "₮", 0, "MN₮"],
      // pataca macanaise
      "MOP": [ "MOP", "MOP$", 2],
      // ouguiya mauritanien
      "MRO": [ "MRO", "UM", 0],
      // lire maltaise
      "MTL": [ "MTL", "Lm", 130],
      // livre maltaise
      "MTP": [ "MTP", "£MT", 130],
      // roupie mauricienne
      "MUR": [ "MUR", "RsMU", 0],
      // rufiyaa maldivienne
      "MVR": [ "MVR", "MVR", 2],
      // kwacha malawite
      "MWK": [ "MWK", "MWK", 2],
      // peso mexicain
      "MXN": [ "MXN", "Mex$", 2, "Mex$"],
      // peso d’argent mexicain (1861–1992)
      "MXP": [ "MXP", "MX$", 130],
      // unité de conversion mexicaine (UDI)
      "MXV": [ "MXV", "MXV", 2],
      // ringgit malais
      "MYR": [ "MYR", "RM", 2, "RM"],
      // escudo mozambicain
      "MZE": [ "MZE", "MZE", 130],
      // métical
      "MZM": [ "MZM", "Mt", 130],
      // metical mozambicain
      "MZN": [ "MZN", "MTn", 2],
      // dollar namibien
      "NAD": [ "NAD", "$NA", 2],
      // naira nigérian
      "NGN": [ "NGN", "₦", 2],
      // córdoba nicaraguayen (1912–1988)
      "NIC": [ "NIC", "NIC", 130],
      // córdoba oro nicaraguayen
      "NIO": [ "NIO", "C$", 2],
      // florin néerlandais
      "NLG": [ "NLG", "fl", 130],
      // couronne norvégienne
      "NOK": [ "NOK", "krN", 2, "NOkr"],
      // roupie népalaise
      "NPR": [ "NPR", "RsNP", 2],
      // dollar néo-zélandais
      "NZD": [ "NZD", "$NZ", 2],
      // rial omani
      "OMR": [ "OMR", "OMR", 3],
      // balboa panaméen
      "PAB": [ "PAB", "B/.", 2, "B/."],
      // inti péruvien
      "PEI": [ "PEI", "I/.", 130],
      // nouveau sol péruvien
      "PEN": [ "PEN", "S/.", 2, "S/."],
      // sol péruvien
      "PES": [ "PES", "PES", 130],
      // kina papouan-néo-guinéen
      "PGK": [ "PGK", "PGK", 2],
      // peso philippin
      "PHP": [ "PHP", "₱", 2, "PHP"],
      // roupie pakistanaise
      "PKR": [ "PKR", "RsPK", 0, "PKRs."],
      // zloty polonais
      "PLN": [ "PLN", "zł", 2],
      // zloty polonais (1950–1995)
      "PLZ": [ "PLZ", "PLZ", 130],
      // escudo portugais
      "PTE": [ "PTE", "Esc", 130],
      // guaraní paraguayen
      "PYG": [ "PYG", "₲", 0],
      // rial qatari
      "QAR": [ "QAR", "RQ", 2],
      // dollar rhodésien
      "RHD": [ "RHD", "$RH", 130],
      // ancien leu roumain
      "ROL": [ "ROL", "ROL", 130],
      // leu roumain
      "RON": [ "RON", "RON", 2],
      // dinar serbe
      "RSD": [ "RSD", "din.", 0],
      // rouble russe
      "RUB": [ "RUB", "руб", 2, "руб"],
      // rouble russe (1991–1998)
      "RUR": [ "RUR", "RUR", 130],
      // franc rwandais
      "RWF": [ "RWF", "FR", 0],
      // rial saoudien
      "SAR": [ "SAR", "SR", 2, "SR"],
      // dollar des îles Salomon
      "SBD": [ "SBD", "$SB", 2],
      // roupie des Seychelles
      "SCR": [ "SCR", "SRe", 2],
      // dinar soudanais
      "SDD": [ "SDD", "LSd", 130],
      // livre soudanaise
      "SDG": [ "SDG", "SDG", 2],
      // livre soudanaise (1956–2007)
      "SDP": [ "SDP", "SDP", 130],
      // couronne suédoise
      "SEK": [ "SEK", "krS", 2, "kr"],
      // dollar de Singapour
      "SGD": [ "SGD", "$SG", 2, "S$"],
      // livre de Sainte-Hélène
      "SHP": [ "SHP", "£SH", 2],
      // tolar slovène
      "SIT": [ "SIT", "SIT", 130],
      // couronne slovaque
      "SKK": [ "SKK", "Sk", 130],
      // leone sierra-léonais
      "SLL": [ "SLL", "Le", 0],
      // shilling somalien
      "SOS": [ "SOS", "Ssh", 0],
      // dollar surinamais
      "SRD": [ "SRD", "$SR", 2],
      // florin surinamais
      "SRG": [ "SRG", "Sf", 130],
      // dobra santoméen
      "STD": [ "STD", "Db", 0],
      // rouble soviétique
      "SUR": [ "SUR", "SUR", 130],
      // colón salvadorien
      "SVC": [ "SVC", "₡SV", 130],
      // livre syrienne
      "SYP": [ "SYP", "£SY", 0],
      // lilangeni swazi
      "SZL": [ "SZL", "SZL", 2],
      // baht thaïlandais
      "THB": [ "THB", "฿", 2, "THB"],
      // rouble tadjik
      "TJR": [ "TJR", "TJR", 130],
      // somoni tadjik
      "TJS": [ "TJS", "TJS", 2],
      // manat turkmène
      "TMM": [ "TMM", "TMM", 128],
      // dinar tunisien
      "TND": [ "TND", "DT", 3],
      // pa’anga tongan
      "TOP": [ "TOP", "T$", 2],
      // escudo timorais
      "TPE": [ "TPE", "TPE", 130],
      // livre turque (1844–2005)
      "TRL": [ "TRL", "TRL", 128],
      // nouvelle livre turque
      "TRY": [ "TRY", "TL", 2, "YTL"],
      // dollar trinidadien
      "TTD": [ "TTD", "$TT", 2],
      // nouveau dollar taïwanais
      "TWD": [ "TWD", "NT$", 2, "NT$"],
      // shilling tanzanien
      "TZS": [ "TZS", "TSh", 0],
      // hryvnia ukrainienne
      "UAH": [ "UAH", "₴", 2],
      // karbovanets ukrainien (1992–1996)
      "UAK": [ "UAK", "UAK", 130],
      // shilling ougandais (1966–1987)
      "UGS": [ "UGS", "UGS", 130],
      // shilling ougandais
      "UGX": [ "UGX", "USh", 0],
      // dollar des États-Unis
      "USD": [ "USD", "$US", 2, "US$"],
      // dollar des Etats-Unis (jour suivant)
      "USN": [ "USN", "USN", 2],
      // dollar des Etats-Unis (jour même)
      "USS": [ "USS", "USS", 2],
      // peso uruguayen (unités indexées)
      "UYI": [ "UYI", "UYI", 2],
      // peso uruguayen (1975–1993)
      "UYP": [ "UYP", "UYP", 130],
      // peso uruguayen
      "UYU": [ "UYU", "$UY", 2, "UY$"],
      // sum ouzbek
      "UZS": [ "UZS", "UZS", 0],
      // bolívar vénézuélien (1879–2008)
      "VEB": [ "VEB", "VEB", 130],
      // bolivar fuerte vénézuélien
      "VEF": [ "VEF", "Bs.F.", 2],
      // dông vietnamien
      "VND": [ "VND", "₫", 24, "₫"],
      // vatu vanuatuan
      "VUV": [ "VUV", "VT", 0],
      // tala samoan
      "WST": [ "WST", "WS$", 2],
      // franc CFA (BEAC)
      "XAF": [ "XAF", "FCFA", 0],
      // once troy d’argent
      "XAG": [ "XAG", "XAG", 2],
      // or
      "XAU": [ "XAU", "XAU", 2],
      // unité européenne composée
      "XBA": [ "XBA", "XBA", 2],
      // unité monétaire européenne
      "XBB": [ "XBB", "XBB", 2],
      // unité de compte 9 européenne (UEC-9)
      "XBC": [ "XBC", "XBC", 2],
      // unité de compte 17 européenne (UEC-17)
      "XBD": [ "XBD", "XBD", 2],
      // dollar des Caraïbes orientales
      "XCD": [ "XCD", "EC$", 2],
      // droit de tirage spécial
      "XDR": [ "XDR", "XDR", 2],
      // unité de compte européenne (ECU)
      "XEU": [ "XEU", "XEU", 2],
      // franc or
      "XFO": [ "XFO", "XFO", 2],
      // franc UIC
      "XFU": [ "XFU", "XFU", 2],
      // franc CFA (BCEAO)
      "XOF": [ "XOF", "CFA", 0],
      // once troy de palladium
      "XPD": [ "XPD", "XPD", 2],
      // franc CFP
      "XPF": [ "XPF", "FCFP", 0],
      // platine
      "XPT": [ "XPT", "XPT", 2],
      // unité de fonds RINET
      "XRE": [ "XRE", "XRE", 2],
      // (devise de test)
      "XTS": [ "XTS", "XTS", 2],
      // (devise inconnue ou invalide)
      "XXX": [ "XXX", "XXX", 2],
      // dinar du Yémen
      "YDD": [ "YDD", "YDD", 130],
      // rial yéménite
      "YER": [ "YER", "RY", 0, "YER"],
      // dinar fort yougoslave (1966–1989)
      "YUD": [ "YUD", "YUD", 130],
      // nouveau dinar yougoslave (1994–2003)
      "YUM": [ "YUM", "YUM", 130],
      // dinar convertible yougoslave (1990–1992)
      "YUN": [ "YUN", "YUN", 130],
      // rand sud-africain (financier)
      "ZAL": [ "ZAL", "ZAL", 2],
      // rand sud-africain
      "ZAR": [ "ZAR", "R", 2, "ZAR"],
      // kwacha zambien
      "ZMK": [ "ZMK", "ZK", 0],
      // nouveau zaïre zaïrien
      "ZRN": [ "ZRN", "NZ", 130],
      // zaïre zaïrois
      "ZRZ": [ "ZRZ", "ZRZ", 130],
      // dollar zimbabwéen
      "ZWD": [ "ZWD", "$Z", 128],
    };
  }-*/;
  
  @Override
  protected HashMap<String, String> loadNamesMapJava() {
    HashMap<String, String> result = super.loadNamesMapJava();
    result.put("ADP", "peseta andorrane");
    result.put("AED", "dirham des Émirats arabes unis");
    result.put("AFA", "afghani (1927–2002)");
    result.put("AFN", "afghani");
    result.put("ALK", "lek albanais (1947–1961)");
    result.put("ALL", "lek albanais");
    result.put("AMD", "dram arménien");
    result.put("ANG", "florin antillais");
    result.put("AOA", "kwanza angolais");
    result.put("AOK", "kwanza angolais (1977–1990)");
    result.put("AON", "nouveau kwanza angolais (1990-2000)");
    result.put("AOR", "kwanza angolais réajusté (1995-1999)");
    result.put("ARA", "austral argentin");
    result.put("ARP", "peso argentin (1983–1985)");
    result.put("ARS", "peso argentin");
    result.put("ATS", "schilling autrichien");
    result.put("AUD", "dollar australien");
    result.put("AWG", "florin arubais");
    result.put("AZM", "manat azéri (1993-2006)");
    result.put("AZN", "manat azéri");
    result.put("BAD", "dinar bosniaque");
    result.put("BAM", "mark convertible bosniaque");
    result.put("BBD", "dollar barbadien");
    result.put("BDT", "taka bangladeshi");
    result.put("BEC", "franc belge (convertible)");
    result.put("BEF", "franc belge");
    result.put("BEL", "franc belge (financier)");
    result.put("BGL", "lev bulgare (1962–1999)");
    result.put("BGN", "nouveau lev bulgare");
    result.put("BHD", "dinar bahreïni");
    result.put("BIF", "franc burundais");
    result.put("BMD", "dollar bermudien");
    result.put("BND", "dollar brunéien");
    result.put("BOB", "boliviano");
    result.put("BOP", "peso bolivien");
    result.put("BOV", "mvdol bolivien");
    result.put("BRB", "nouveau cruzeiro brésilien (1967–1986)");
    result.put("BRC", "cruzado brésilien (1986–1989)");
    result.put("BRE", "cruzeiro brésilien (1990–1993)");
    result.put("BRL", "réal brésilien");
    result.put("BRN", "nouveau cruzado brésilien (1989–1990)");
    result.put("BRR", "cruzeiro");
    result.put("BSD", "dollar bahaméen");
    result.put("BTN", "ngultrum bouthanais");
    result.put("BUK", "kyat birman");
    result.put("BWP", "pula botswanais");
    result.put("BYB", "nouveau rouble biélorusse (1994-1999)");
    result.put("BYR", "rouble biélorusse");
    result.put("BZD", "dollar bélizéen");
    result.put("CAD", "dollar canadien");
    result.put("CDF", "franc congolais");
    result.put("CHE", "euro WIR");
    result.put("CHF", "franc suisse");
    result.put("CHW", "franc WIR");
    result.put("CLF", "unité d’investissement chilienne");
    result.put("CLP", "peso chilien");
    result.put("CNY", "yuan renminbi chinois");
    result.put("COP", "peso colombien");
    result.put("COU", "Unité de valeur réelle colombienne");
    result.put("CRC", "colón costaricain");
    result.put("CSD", "dinar serbo-monténégrin");
    result.put("CSK", "couronne forte tchécoslovaque");
    result.put("CUP", "peso cubain");
    result.put("CVE", "escudo capverdien");
    result.put("CYP", "livre chypriote");
    result.put("CZK", "couronne tchèque");
    result.put("DDM", "mark est-allemand");
    result.put("DEM", "mark allemand");
    result.put("DJF", "franc djiboutien");
    result.put("DKK", "couronne danoise");
    result.put("DOP", "peso dominicain");
    result.put("DZD", "dinar algérien");
    result.put("ECS", "sucre équatorien");
    result.put("ECV", "unité de valeur constante équatoriale (UVC)");
    result.put("EEK", "couronne estonienne");
    result.put("EGP", "livre égyptienne");
    result.put("ERN", "nafka érythréen");
    result.put("ESA", "peseta espagnole (compte A)");
    result.put("ESB", "peseta espagnole (compte convertible)");
    result.put("ESP", "peseta espagnole");
    result.put("ETB", "birr éthiopien");
    result.put("EUR", "euro");
    result.put("FIM", "mark finlandais");
    result.put("FJD", "dollar fidjien");
    result.put("FKP", "livre des Falkland");
    result.put("FRF", "franc français");
    result.put("GBP", "livre sterling");
    result.put("GEK", "Georgian Kupon Larit");
    result.put("GEL", "lari géorgien");
    result.put("GHC", "cédi ghanéen (1967–2007)");
    result.put("GHS", "cédi ghanéen");
    result.put("GIP", "livre de Gibraltar");
    result.put("GMD", "dalasi gambien");
    result.put("GNF", "franc guinéen");
    result.put("GNS", "syli guinéen");
    result.put("GQE", "ekwélé équatoguinéen");
    result.put("GRD", "drachme grecque");
    result.put("GTQ", "quetzal guatémaltèque");
    result.put("GWE", "escudo de Guinée portugaise");
    result.put("GWP", "peso bissau-guinéen");
    result.put("GYD", "dollar guyanien");
    result.put("HKD", "dollar de Hong Kong");
    result.put("HNL", "lempira hondurien");
    result.put("HRD", "dinar croate");
    result.put("HRK", "kuna croate");
    result.put("HTG", "gourde haïtienne");
    result.put("HUF", "forint hongrois");
    result.put("IDR", "roupie indonésienne");
    result.put("IEP", "livre irlandaise");
    result.put("ILP", "livre israélienne");
    result.put("ILS", "nouveau shekel israélien");
    result.put("INR", "roupie indienne");
    result.put("IQD", "dinar irakien");
    result.put("IRR", "rial iranien");
    result.put("ISK", "couronne islandaise");
    result.put("ITL", "lire italienne");
    result.put("JMD", "dollar jamaïcain");
    result.put("JOD", "dinar jordanien");
    result.put("JPY", "yen japonais");
    result.put("KES", "shilling kényan");
    result.put("KGS", "som kirghize");
    result.put("KHR", "riel cambodgien");
    result.put("KMF", "franc comorien");
    result.put("KPW", "won nord-coréen");
    result.put("KRW", "won sud-coréen");
    result.put("KWD", "dinar koweïtien");
    result.put("KYD", "dollar des îles Caïmanes");
    result.put("KZT", "tenge kazakh");
    result.put("LAK", "kip loatien");
    result.put("LBP", "livre libanaise");
    result.put("LKR", "roupie srilankaise");
    result.put("LRD", "dollar libérien");
    result.put("LSL", "loti lesothan");
    result.put("LTL", "litas lituanien");
    result.put("LTT", "talonas lituanien");
    result.put("LUC", "franc convertible luxembourgeois");
    result.put("LUF", "franc luxembourgeois");
    result.put("LUL", "franc financier luxembourgeois");
    result.put("LVL", "lats letton");
    result.put("LVR", "rouble letton");
    result.put("LYD", "dinar lybien");
    result.put("MAD", "dirham marocain");
    result.put("MAF", "franc marocain");
    result.put("MDL", "leu moldave");
    result.put("MGA", "ariary malgache");
    result.put("MGF", "franc malgache");
    result.put("MKD", "denar macédonien");
    result.put("MLF", "franc malien");
    result.put("MMK", "kyat myanmarais");
    result.put("MNT", "tugrik mongol");
    result.put("MOP", "pataca macanaise");
    result.put("MRO", "ouguiya mauritanien");
    result.put("MTL", "lire maltaise");
    result.put("MTP", "livre maltaise");
    result.put("MUR", "roupie mauricienne");
    result.put("MVR", "rufiyaa maldivienne");
    result.put("MWK", "kwacha malawite");
    result.put("MXN", "peso mexicain");
    result.put("MXP", "peso d’argent mexicain (1861–1992)");
    result.put("MXV", "unité de conversion mexicaine (UDI)");
    result.put("MYR", "ringgit malais");
    result.put("MZE", "escudo mozambicain");
    result.put("MZM", "métical");
    result.put("MZN", "metical mozambicain");
    result.put("NAD", "dollar namibien");
    result.put("NGN", "naira nigérian");
    result.put("NIC", "córdoba nicaraguayen (1912–1988)");
    result.put("NIO", "córdoba oro nicaraguayen");
    result.put("NLG", "florin néerlandais");
    result.put("NOK", "couronne norvégienne");
    result.put("NPR", "roupie népalaise");
    result.put("NZD", "dollar néo-zélandais");
    result.put("OMR", "rial omani");
    result.put("PAB", "balboa panaméen");
    result.put("PEI", "inti péruvien");
    result.put("PEN", "nouveau sol péruvien");
    result.put("PES", "sol péruvien");
    result.put("PGK", "kina papouan-néo-guinéen");
    result.put("PHP", "peso philippin");
    result.put("PKR", "roupie pakistanaise");
    result.put("PLN", "zloty polonais");
    result.put("PLZ", "zloty polonais (1950–1995)");
    result.put("PTE", "escudo portugais");
    result.put("PYG", "guaraní paraguayen");
    result.put("QAR", "rial qatari");
    result.put("RHD", "dollar rhodésien");
    result.put("ROL", "ancien leu roumain");
    result.put("RON", "leu roumain");
    result.put("RSD", "dinar serbe");
    result.put("RUB", "rouble russe");
    result.put("RUR", "rouble russe (1991–1998)");
    result.put("RWF", "franc rwandais");
    result.put("SAR", "rial saoudien");
    result.put("SBD", "dollar des îles Salomon");
    result.put("SCR", "roupie des Seychelles");
    result.put("SDD", "dinar soudanais");
    result.put("SDG", "livre soudanaise");
    result.put("SDP", "livre soudanaise (1956–2007)");
    result.put("SEK", "couronne suédoise");
    result.put("SGD", "dollar de Singapour");
    result.put("SHP", "livre de Sainte-Hélène");
    result.put("SIT", "tolar slovène");
    result.put("SKK", "couronne slovaque");
    result.put("SLL", "leone sierra-léonais");
    result.put("SOS", "shilling somalien");
    result.put("SRD", "dollar surinamais");
    result.put("SRG", "florin surinamais");
    result.put("STD", "dobra santoméen");
    result.put("SUR", "rouble soviétique");
    result.put("SVC", "colón salvadorien");
    result.put("SYP", "livre syrienne");
    result.put("SZL", "lilangeni swazi");
    result.put("THB", "baht thaïlandais");
    result.put("TJR", "rouble tadjik");
    result.put("TJS", "somoni tadjik");
    result.put("TMM", "manat turkmène");
    result.put("TND", "dinar tunisien");
    result.put("TOP", "pa’anga tongan");
    result.put("TPE", "escudo timorais");
    result.put("TRL", "livre turque (1844–2005)");
    result.put("TRY", "nouvelle livre turque");
    result.put("TTD", "dollar trinidadien");
    result.put("TWD", "nouveau dollar taïwanais");
    result.put("TZS", "shilling tanzanien");
    result.put("UAH", "hryvnia ukrainienne");
    result.put("UAK", "karbovanets ukrainien (1992–1996)");
    result.put("UGS", "shilling ougandais (1966–1987)");
    result.put("UGX", "shilling ougandais");
    result.put("USD", "dollar des États-Unis");
    result.put("USN", "dollar des Etats-Unis (jour suivant)");
    result.put("USS", "dollar des Etats-Unis (jour même)");
    result.put("UYI", "peso uruguayen (unités indexées)");
    result.put("UYP", "peso uruguayen (1975–1993)");
    result.put("UYU", "peso uruguayen");
    result.put("UZS", "sum ouzbek");
    result.put("VEB", "bolívar vénézuélien (1879–2008)");
    result.put("VEF", "bolivar fuerte vénézuélien");
    result.put("VND", "dông vietnamien");
    result.put("VUV", "vatu vanuatuan");
    result.put("WST", "tala samoan");
    result.put("XAF", "franc CFA (BEAC)");
    result.put("XAG", "once troy d’argent");
    result.put("XAU", "or");
    result.put("XBA", "unité européenne composée");
    result.put("XBB", "unité monétaire européenne");
    result.put("XBC", "unité de compte 9 européenne (UEC-9)");
    result.put("XBD", "unité de compte 17 européenne (UEC-17)");
    result.put("XCD", "dollar des Caraïbes orientales");
    result.put("XDR", "droit de tirage spécial");
    result.put("XEU", "unité de compte européenne (ECU)");
    result.put("XFO", "franc or");
    result.put("XFU", "franc UIC");
    result.put("XOF", "franc CFA (BCEAO)");
    result.put("XPD", "once troy de palladium");
    result.put("XPF", "franc CFP");
    result.put("XPT", "platine");
    result.put("XRE", "unité de fonds RINET");
    result.put("XTS", "(devise de test)");
    result.put("XXX", "(devise inconnue ou invalide)");
    result.put("YDD", "dinar du Yémen");
    result.put("YER", "rial yéménite");
    result.put("YUD", "dinar fort yougoslave (1966–1989)");
    result.put("YUM", "nouveau dinar yougoslave (1994–2003)");
    result.put("YUN", "dinar convertible yougoslave (1990–1992)");
    result.put("ZAL", "rand sud-africain (financier)");
    result.put("ZAR", "rand sud-africain");
    result.put("ZMK", "kwacha zambien");
    result.put("ZRN", "nouveau zaïre zaïrien");
    result.put("ZRZ", "zaïre zaïrois");
    result.put("ZWD", "dollar zimbabwéen");
    return result;
  }
  
  @Override
  protected JavaScriptObject loadNamesMapNative() {
    return overrideMap(super.loadNamesMapNative(), loadMyNamesMapOverridesNative());
  }
  
  private native JavaScriptObject loadMyNamesMapOverridesNative() /*-{
    return {
      "ADP": "peseta andorrane",
      "AED": "dirham des Émirats arabes unis",
      "AFA": "afghani (1927–2002)",
      "AFN": "afghani",
      "ALK": "lek albanais (1947–1961)",
      "ALL": "lek albanais",
      "AMD": "dram arménien",
      "ANG": "florin antillais",
      "AOA": "kwanza angolais",
      "AOK": "kwanza angolais (1977–1990)",
      "AON": "nouveau kwanza angolais (1990-2000)",
      "AOR": "kwanza angolais réajusté (1995-1999)",
      "ARA": "austral argentin",
      "ARP": "peso argentin (1983–1985)",
      "ARS": "peso argentin",
      "ATS": "schilling autrichien",
      "AUD": "dollar australien",
      "AWG": "florin arubais",
      "AZM": "manat azéri (1993-2006)",
      "AZN": "manat azéri",
      "BAD": "dinar bosniaque",
      "BAM": "mark convertible bosniaque",
      "BBD": "dollar barbadien",
      "BDT": "taka bangladeshi",
      "BEC": "franc belge (convertible)",
      "BEF": "franc belge",
      "BEL": "franc belge (financier)",
      "BGL": "lev bulgare (1962–1999)",
      "BGN": "nouveau lev bulgare",
      "BHD": "dinar bahreïni",
      "BIF": "franc burundais",
      "BMD": "dollar bermudien",
      "BND": "dollar brunéien",
      "BOB": "boliviano",
      "BOP": "peso bolivien",
      "BOV": "mvdol bolivien",
      "BRB": "nouveau cruzeiro brésilien (1967–1986)",
      "BRC": "cruzado brésilien (1986–1989)",
      "BRE": "cruzeiro brésilien (1990–1993)",
      "BRL": "réal brésilien",
      "BRN": "nouveau cruzado brésilien (1989–1990)",
      "BRR": "cruzeiro",
      "BSD": "dollar bahaméen",
      "BTN": "ngultrum bouthanais",
      "BUK": "kyat birman",
      "BWP": "pula botswanais",
      "BYB": "nouveau rouble biélorusse (1994-1999)",
      "BYR": "rouble biélorusse",
      "BZD": "dollar bélizéen",
      "CAD": "dollar canadien",
      "CDF": "franc congolais",
      "CHE": "euro WIR",
      "CHF": "franc suisse",
      "CHW": "franc WIR",
      "CLF": "unité d’investissement chilienne",
      "CLP": "peso chilien",
      "CNY": "yuan renminbi chinois",
      "COP": "peso colombien",
      "COU": "Unité de valeur réelle colombienne",
      "CRC": "colón costaricain",
      "CSD": "dinar serbo-monténégrin",
      "CSK": "couronne forte tchécoslovaque",
      "CUP": "peso cubain",
      "CVE": "escudo capverdien",
      "CYP": "livre chypriote",
      "CZK": "couronne tchèque",
      "DDM": "mark est-allemand",
      "DEM": "mark allemand",
      "DJF": "franc djiboutien",
      "DKK": "couronne danoise",
      "DOP": "peso dominicain",
      "DZD": "dinar algérien",
      "ECS": "sucre équatorien",
      "ECV": "unité de valeur constante équatoriale (UVC)",
      "EEK": "couronne estonienne",
      "EGP": "livre égyptienne",
      "ERN": "nafka érythréen",
      "ESA": "peseta espagnole (compte A)",
      "ESB": "peseta espagnole (compte convertible)",
      "ESP": "peseta espagnole",
      "ETB": "birr éthiopien",
      "EUR": "euro",
      "FIM": "mark finlandais",
      "FJD": "dollar fidjien",
      "FKP": "livre des Falkland",
      "FRF": "franc français",
      "GBP": "livre sterling",
      "GEK": "Georgian Kupon Larit",
      "GEL": "lari géorgien",
      "GHC": "cédi ghanéen (1967–2007)",
      "GHS": "cédi ghanéen",
      "GIP": "livre de Gibraltar",
      "GMD": "dalasi gambien",
      "GNF": "franc guinéen",
      "GNS": "syli guinéen",
      "GQE": "ekwélé équatoguinéen",
      "GRD": "drachme grecque",
      "GTQ": "quetzal guatémaltèque",
      "GWE": "escudo de Guinée portugaise",
      "GWP": "peso bissau-guinéen",
      "GYD": "dollar guyanien",
      "HKD": "dollar de Hong Kong",
      "HNL": "lempira hondurien",
      "HRD": "dinar croate",
      "HRK": "kuna croate",
      "HTG": "gourde haïtienne",
      "HUF": "forint hongrois",
      "IDR": "roupie indonésienne",
      "IEP": "livre irlandaise",
      "ILP": "livre israélienne",
      "ILS": "nouveau shekel israélien",
      "INR": "roupie indienne",
      "IQD": "dinar irakien",
      "IRR": "rial iranien",
      "ISK": "couronne islandaise",
      "ITL": "lire italienne",
      "JMD": "dollar jamaïcain",
      "JOD": "dinar jordanien",
      "JPY": "yen japonais",
      "KES": "shilling kényan",
      "KGS": "som kirghize",
      "KHR": "riel cambodgien",
      "KMF": "franc comorien",
      "KPW": "won nord-coréen",
      "KRW": "won sud-coréen",
      "KWD": "dinar koweïtien",
      "KYD": "dollar des îles Caïmanes",
      "KZT": "tenge kazakh",
      "LAK": "kip loatien",
      "LBP": "livre libanaise",
      "LKR": "roupie srilankaise",
      "LRD": "dollar libérien",
      "LSL": "loti lesothan",
      "LTL": "litas lituanien",
      "LTT": "talonas lituanien",
      "LUC": "franc convertible luxembourgeois",
      "LUF": "franc luxembourgeois",
      "LUL": "franc financier luxembourgeois",
      "LVL": "lats letton",
      "LVR": "rouble letton",
      "LYD": "dinar lybien",
      "MAD": "dirham marocain",
      "MAF": "franc marocain",
      "MDL": "leu moldave",
      "MGA": "ariary malgache",
      "MGF": "franc malgache",
      "MKD": "denar macédonien",
      "MLF": "franc malien",
      "MMK": "kyat myanmarais",
      "MNT": "tugrik mongol",
      "MOP": "pataca macanaise",
      "MRO": "ouguiya mauritanien",
      "MTL": "lire maltaise",
      "MTP": "livre maltaise",
      "MUR": "roupie mauricienne",
      "MVR": "rufiyaa maldivienne",
      "MWK": "kwacha malawite",
      "MXN": "peso mexicain",
      "MXP": "peso d’argent mexicain (1861–1992)",
      "MXV": "unité de conversion mexicaine (UDI)",
      "MYR": "ringgit malais",
      "MZE": "escudo mozambicain",
      "MZM": "métical",
      "MZN": "metical mozambicain",
      "NAD": "dollar namibien",
      "NGN": "naira nigérian",
      "NIC": "córdoba nicaraguayen (1912–1988)",
      "NIO": "córdoba oro nicaraguayen",
      "NLG": "florin néerlandais",
      "NOK": "couronne norvégienne",
      "NPR": "roupie népalaise",
      "NZD": "dollar néo-zélandais",
      "OMR": "rial omani",
      "PAB": "balboa panaméen",
      "PEI": "inti péruvien",
      "PEN": "nouveau sol péruvien",
      "PES": "sol péruvien",
      "PGK": "kina papouan-néo-guinéen",
      "PHP": "peso philippin",
      "PKR": "roupie pakistanaise",
      "PLN": "zloty polonais",
      "PLZ": "zloty polonais (1950–1995)",
      "PTE": "escudo portugais",
      "PYG": "guaraní paraguayen",
      "QAR": "rial qatari",
      "RHD": "dollar rhodésien",
      "ROL": "ancien leu roumain",
      "RON": "leu roumain",
      "RSD": "dinar serbe",
      "RUB": "rouble russe",
      "RUR": "rouble russe (1991–1998)",
      "RWF": "franc rwandais",
      "SAR": "rial saoudien",
      "SBD": "dollar des îles Salomon",
      "SCR": "roupie des Seychelles",
      "SDD": "dinar soudanais",
      "SDG": "livre soudanaise",
      "SDP": "livre soudanaise (1956–2007)",
      "SEK": "couronne suédoise",
      "SGD": "dollar de Singapour",
      "SHP": "livre de Sainte-Hélène",
      "SIT": "tolar slovène",
      "SKK": "couronne slovaque",
      "SLL": "leone sierra-léonais",
      "SOS": "shilling somalien",
      "SRD": "dollar surinamais",
      "SRG": "florin surinamais",
      "STD": "dobra santoméen",
      "SUR": "rouble soviétique",
      "SVC": "colón salvadorien",
      "SYP": "livre syrienne",
      "SZL": "lilangeni swazi",
      "THB": "baht thaïlandais",
      "TJR": "rouble tadjik",
      "TJS": "somoni tadjik",
      "TMM": "manat turkmène",
      "TND": "dinar tunisien",
      "TOP": "pa’anga tongan",
      "TPE": "escudo timorais",
      "TRL": "livre turque (1844–2005)",
      "TRY": "nouvelle livre turque",
      "TTD": "dollar trinidadien",
      "TWD": "nouveau dollar taïwanais",
      "TZS": "shilling tanzanien",
      "UAH": "hryvnia ukrainienne",
      "UAK": "karbovanets ukrainien (1992–1996)",
      "UGS": "shilling ougandais (1966–1987)",
      "UGX": "shilling ougandais",
      "USD": "dollar des États-Unis",
      "USN": "dollar des Etats-Unis (jour suivant)",
      "USS": "dollar des Etats-Unis (jour même)",
      "UYI": "peso uruguayen (unités indexées)",
      "UYP": "peso uruguayen (1975–1993)",
      "UYU": "peso uruguayen",
      "UZS": "sum ouzbek",
      "VEB": "bolívar vénézuélien (1879–2008)",
      "VEF": "bolivar fuerte vénézuélien",
      "VND": "dông vietnamien",
      "VUV": "vatu vanuatuan",
      "WST": "tala samoan",
      "XAF": "franc CFA (BEAC)",
      "XAG": "once troy d’argent",
      "XAU": "or",
      "XBA": "unité européenne composée",
      "XBB": "unité monétaire européenne",
      "XBC": "unité de compte 9 européenne (UEC-9)",
      "XBD": "unité de compte 17 européenne (UEC-17)",
      "XCD": "dollar des Caraïbes orientales",
      "XDR": "droit de tirage spécial",
      "XEU": "unité de compte européenne (ECU)",
      "XFO": "franc or",
      "XFU": "franc UIC",
      "XOF": "franc CFA (BCEAO)",
      "XPD": "once troy de palladium",
      "XPF": "franc CFP",
      "XPT": "platine",
      "XRE": "unité de fonds RINET",
      "XTS": "(devise de test)",
      "XXX": "(devise inconnue ou invalide)",
      "YDD": "dinar du Yémen",
      "YER": "rial yéménite",
      "YUD": "dinar fort yougoslave (1966–1989)",
      "YUM": "nouveau dinar yougoslave (1994–2003)",
      "YUN": "dinar convertible yougoslave (1990–1992)",
      "ZAL": "rand sud-africain (financier)",
      "ZAR": "rand sud-africain",
      "ZMK": "kwacha zambien",
      "ZRN": "nouveau zaïre zaïrien",
      "ZRZ": "zaïre zaïrois",
      "ZWD": "dollar zimbabwéen",
    };
  }-*/;
}
