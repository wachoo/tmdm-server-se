package com.google.gwt.i18n.client;

import com.google.gwt.i18n.client.impl.CurrencyDataImpl;
import com.google.gwt.i18n.client.CurrencyList;

public class CurrencyList_fr extends com.google.gwt.i18n.client.CurrencyList_ {
  
  private void loadSuperCurrencyMap() {
    super.loadCurrencyMap();
  }
  
  @Override
  protected native void loadCurrencyMap() /*-{
    this.@com.google.gwt.i18n.client.CurrencyList_fr::loadSuperCurrencyMap()();
    this.@com.google.gwt.i18n.client.CurrencyList_fr::overrideCurrencyMap(Lcom/google/gwt/core/client/JavaScriptObject;)({
      // peseta andorrane
      "ADP": [ "ADP", "₧A", 128],
      // dirham des Émirats arabes unis
      "AED": [ "AED", "DH", 2, "DH"],
      // afghani [AFA]
      "AFA": [ "AFA", "AFA", 130],
      // afghani
      "AFN": [ "AFN", "Af", 2],
      // lek
      "ALL": [ "ALL", "lek", 2],
      // dram arménien
      "AMD": [ "AMD", "dram", 2],
      // florin des Antilles
      "ANG": [ "ANG", "f.NA", 2],
      // kwanza angolais
      "AOA": [ "AOA", "AOA", 2],
      // kwanza angolais (1977-1990)
      "AOK": [ "AOK", "AOK", 130],
      // nouveau kwanza angolais (1990-2000)
      "AON": [ "AON", "AON", 130],
      // kwanza angolais réajusté (1995-1999)
      "AOR": [ "AOR", "AOR", 130],
      // austral argentin
      "ARA": [ "ARA", "ARA", 130],
      // peso argentin (1983-1985)
      "ARP": [ "ARP", "ARP", 130],
      // peso argentin
      "ARS": [ "ARS", "Arg$", 2, "AR$"],
      // schilling autrichien
      "ATS": [ "ATS", "ATS", 130],
      // dollar australien
      "AUD": [ "AUD", "$A", 2, "AU$"],
      // florin d’Aruba
      "AWG": [ "AWG", "AWG", 2],
      // manat azéri
      "AZM": [ "AZM", "AZM", 130],
      // manat azéri
      "AZN": [ "AZN", "AZN", 2],
      // dinar de Bosnie-Herzegovine
      "BAD": [ "BAD", "BAD", 130],
      // mark bosniaque convertible
      "BAM": [ "BAM", "KM", 2],
      // dollar de Barbade
      "BBD": [ "BBD", "$Bds", 2],
      // taka
      "BDT": [ "BDT", "Tk", 2, "Tk"],
      // franc belge (convertible)
      "BEC": [ "BEC", "BEC", 130],
      // franc belge
      "BEF": [ "BEF", "FB", 130],
      // franc belge (financier)
      "BEL": [ "BEL", "BEL", 130],
      // lev
      "BGL": [ "BGL", "lev", 130],
      // nouveau lev
      "BGN": [ "BGN", "NB", 2],
      // dinar de Bahreïn
      "BHD": [ "BHD", "BHD", 3],
      // franc du Burundi
      "BIF": [ "BIF", "FBu", 0],
      // dollar des Bermudes
      "BMD": [ "BMD", "$Bm", 2],
      // dollar de Brunei
      "BND": [ "BND", "$Bn", 2],
      // boliviano
      "BOB": [ "BOB", "Bs", 2],
      // peso bolivien
      "BOP": [ "BOP", "BOP", 130],
      // mvdol
      "BOV": [ "BOV", "BOV", 130],
      // nouveau cruzeiro (1967-1986)
      "BRB": [ "BRB", "BRB", 130],
      // cruzado
      "BRC": [ "BRC", "BRC", 130],
      // cruzeiro (1990-1993)
      "BRE": [ "BRE", "BRE", 130],
      // réal
      "BRL": [ "BRL", "R$", 2, "R$"],
      // nouveau cruzado
      "BRN": [ "BRN", "BRN", 130],
      // cruzeiro
      "BRR": [ "BRR", "BRR", 130],
      // dollar bahaméen
      "BSD": [ "BSD", "DBo", 2],
      // ngultrum
      "BTN": [ "BTN", "Nu", 130],
      // kyat [BUK]
      "BUK": [ "BUK", "BUK", 130],
      // pula
      "BWP": [ "BWP", "BWP", 2],
      // nouveau rouble biélorusse (1994-1999)
      "BYB": [ "BYB", "BYB", 130],
      // rouble biélorusse
      "BYR": [ "BYR", "Rbl", 0],
      // dollar de Belize
      "BZD": [ "BZD", "$Bz", 2],
      // dollar canadien
      "CAD": [ "CAD", "$Ca", 2, "C$"],
      // franc congolais
      "CDF": [ "CDF", "FrCD", 2],
      // euro WIR
      "CHE": [ "CHE", "CHE", 130],
      // franc suisse
      "CHF": [ "CHF", "sFr.", 2, "CHF"],
      // franc WIR
      "CHW": [ "CHW", "CHW", 130],
      // unité d’investissement chilienne
      "CLF": [ "CLF", "CLF", 128],
      // peso chilien
      "CLP": [ "CLP", "$Ch", 0, "CL$"],
      // Yuan Ren-min-bi
      "CNY": [ "CNY", "Y", 2, "RMB¥"],
      // peso colombien
      "COP": [ "COP", "PsCo", 2, "COL$"],
      // Unité de valeur réelle colombienne
      "COU": [ "COU", "COU", 130],
      // colon de Costa Rica
      "CRC": [ "CRC", "C", 2, "CR₡"],
      // dinar serbo-monténégrin
      "CSD": [ "CSD", "DS", 130],
      // couronne tchèque [CSK]
      "CSK": [ "CSK", "KCs", 130],
      // peso cubain
      "CUP": [ "CUP", "PsCu", 2, "$MN"],
      // escudo du Cap-Vert
      "CVE": [ "CVE", "EscCV", 2],
      // livre cypriote
      "CYP": [ "CYP", "£C", 130],
      // couronne tchèque
      "CZK": [ "CZK", "CrCz", 2, "Kč"],
      // mark est-allemand
      "DDM": [ "DDM", "DDM", 130],
      // deutsche mark
      "DEM": [ "DEM", "DM", 130],
      // franc de Djibouti
      "DJF": [ "DJF", "DF", 0],
      // couronne danoise
      "DKK": [ "DKK", "CrD", 2, "kr"],
      // peso dominicain
      "DOP": [ "DOP", "$RD", 2, "RD$"],
      // dinar algérien
      "DZD": [ "DZD", "DA", 2],
      // sucre
      "ECS": [ "ECS", "SEq", 130],
      // unité de valeur constante équatoriale (UVC)
      "ECV": [ "ECV", "UvcÉq", 130],
      // couronne estonienne
      "EEK": [ "EEK", "CrE", 2],
      // livre égyptienne
      "EGP": [ "EGP", "£Eg", 2, "LE"],
      // ekwele
      "EQE": [ "EQE", "EQE", 130],
      // Eritrean Nakfa
      "ERN": [ "ERN", "ERN", 2],
      // peseta espagnole (compte A)
      "ESA": [ "ESA", "ESA", 130],
      // peseta espagnole (compte convertible)
      "ESB": [ "ESB", "ESB", 130],
      // peseta espagnole
      "ESP": [ "ESP", "₧", 128],
      // birr
      "ETB": [ "ETB", "Br", 2],
      // euro
      "EUR": [ "EUR", "€", 2, "€"],
      // mark finlandais
      "FIM": [ "FIM", "FIM", 130],
      // dollar de Fidji
      "FJD": [ "FJD", "$F", 2],
      // livre des Falkland (Malvinas)
      "FKP": [ "FKP", "£Fk", 2],
      // franc français
      "FRF": [ "FRF", "F", 130],
      // livre sterling
      "GBP": [ "GBP", "£UK", 2, "GB£"],
      // Georgian Kupon Larit
      "GEK": [ "GEK", "KrGe", 130],
      // lari
      "GEL": [ "GEL", "lari", 2],
      // cédi
      "GHC": [ "GHC", "Cd", 130],
      // livre de Gibraltar
      "GIP": [ "GIP", "£Gi", 2],
      // dalasie
      "GMD": [ "GMD", "Ds", 2],
      // franc guinéen
      "GNF": [ "GNF", "GF", 0],
      // syli
      "GNS": [ "GNS", "GNS", 130],
      // ekwélé
      "GQE": [ "GQE", "GQE", 130],
      // drachme
      "GRD": [ "GRD", "Dr", 130],
      // quetzal
      "GTQ": [ "GTQ", "Q", 2],
      // escudo de Guinée portugaise
      "GWE": [ "GWE", "GWE", 130],
      // peso de Guinée-Bissau
      "GWP": [ "GWP", "PsGW", 2],
      // dollar du Guyana
      "GYD": [ "GYD", "G$", 2],
      // dollar de Hong Kong
      "HKD": [ "HKD", "$HK", 2, "HK$"],
      // lempira
      "HNL": [ "HNL", "LH", 2],
      // dinar croate
      "HRD": [ "HRD", "HRD", 130],
      // kuna
      "HRK": [ "HRK", "Ku", 2],
      // gourde
      "HTG": [ "HTG", "Gd", 2],
      // forint
      "HUF": [ "HUF", "Ft", 2],
      // rupiah
      "IDR": [ "IDR", "Rp", 2],
      // livre irlandaise
      "IEP": [ "IEP", "£IE", 130],
      // livre israélienne
      "ILP": [ "ILP", "£IL", 130],
      // shekel
      "ILS": [ "ILS", "₪", 2, "IL₪"],
      // roupie indienne
      "INR": [ "INR", "INR", 2, "Rs"],
      // dinar irakien
      "IQD": [ "IQD", "DI", 3],
      // rial iranien
      "IRR": [ "IRR", "RI", 2],
      // couronne islandaise
      "ISK": [ "ISK", "KrIs", 2, "kr"],
      // lire italienne
      "ITL": [ "ITL", "₤IT", 128],
      // dollar jamaïcain
      "JMD": [ "JMD", "$JM", 2, "JA$"],
      // dinar jordanien
      "JOD": [ "JOD", "DJ", 3],
      // yen
      "JPY": [ "JPY", "¥JP", 0, "JP¥"],
      // shilling du Kenya
      "KES": [ "KES", "ShK", 2],
      // som
      "KGS": [ "KGS", "som", 2],
      // riel
      "KHR": [ "KHR", "RpC", 2],
      // franc des Comores
      "KMF": [ "KMF", "FC", 0],
      // won nord-coréen
      "KPW": [ "KPW", "₩kp", 2],
      // won sud-coréen
      "KRW": [ "KRW", "₩kr", 0, "KR₩"],
      // dinar koweïtien
      "KWD": [ "KWD", "DK", 3],
      // dollar des îles Caïmanes
      "KYD": [ "KYD", "$KY", 2],
      // tenge du Kazakhstan
      "KZT": [ "KZT", "T", 2],
      // kip
      "LAK": [ "LAK", "KL", 2],
      // livre libanaise
      "LBP": [ "LBP", "£LB", 2],
      // roupie de Sri Lanka
      "LKR": [ "LKR", "ReSL", 2, "SLRs"],
      // dollar libérien
      "LRD": [ "LRD", "$LR", 2],
      // loti
      "LSL": [ "LSL", "M", 130],
      // maloti
      "LSM": [ "LSM", "LSM", 130],
      // litas lituanien
      "LTL": [ "LTL", "LL", 2],
      // talonas
      "LTT": [ "LTT", "LTT", 130],
      // franc luxembourgeois convertible
      "LUC": [ "LUC", "LUC", 130],
      // franc luxembourgeois
      "LUF": [ "LUF", "FL", 128],
      // franc luxembourgeois financier
      "LUL": [ "LUL", "LUL", 130],
      // lats letton
      "LVL": [ "LVL", "l$", 2],
      // rouble letton
      "LVR": [ "LVR", "LVR", 130],
      // dinar lybien
      "LYD": [ "LYD", "LD", 3],
      // dirham marocain
      "MAD": [ "MAD", "Dm", 2],
      // franc marocain
      "MAF": [ "MAF", "MAF", 130],
      // leu moldave
      "MDL": [ "MDL", "LM", 2],
      // ariary
      "MGA": [ "MGA", "Ar", 0],
      // franc malgache
      "MGF": [ "MGF", "FM", 128],
      // denar
      "MKD": [ "MKD", "MDen", 2],
      // franc malien
      "MLF": [ "MLF", "Fml", 130],
      // Myanmar Kyat
      "MMK": [ "MMK", "KMm", 2],
      // tugrik
      "MNT": [ "MNT", "Tug", 2, "MN₮"],
      // pataca
      "MOP": [ "MOP", "PMo", 2],
      // ouguija
      "MRO": [ "MRO", "UM", 2],
      // lire maltaise
      "MTL": [ "MTL", "Lm", 130],
      // livre maltaise
      "MTP": [ "MTP", "£Mt", 130],
      // roupie mauricienne
      "MUR": [ "MUR", "RpMu", 2],
      // roupie des Maldives
      "MVR": [ "MVR", "MVR", 2],
      // kwacha [MWK]
      "MWK": [ "MWK", "KMw", 2],
      // peso mexicain
      "MXN": [ "MXN", "$Mex", 2, "Mex$"],
      // peso d’argent mexicain (1861-1992)
      "MXP": [ "MXP", "MXP", 130],
      // unité de conversion mexicaine (UDI)
      "MXV": [ "MXV", "MXV", 130],
      // ringgit malais
      "MYR": [ "MYR", "RM", 2, "RM"],
      // escudo du Mozambique
      "MZE": [ "MZE", "EscMz", 130],
      // métical
      "MZM": [ "MZM", "Mt", 130],
      // metical
      "MZN": [ "MZN", "NMt", 2],
      // dollar namibien
      "NAD": [ "NAD", "N$", 130],
      // naira
      "NGN": [ "NGN", "NGN", 2],
      // cordoba
      "NIC": [ "NIC", "NIC", 130],
      // cordoba d’or
      "NIO": [ "NIO", "NIO", 2],
      // florin néerlandais
      "NLG": [ "NLG", "fl.", 130],
      // couronne norvégienne
      "NOK": [ "NOK", "KNo", 2, "NOkr"],
      // roupie du Népal
      "NPR": [ "NPR", "Nrs", 2],
      // dollar néo-zélandais
      "NZD": [ "NZD", "$NZ", 2],
      // rial omani
      "OMR": [ "OMR", "RO", 3],
      // balboa
      "PAB": [ "PAB", "B/.", 2, "B/."],
      // inti péruvien
      "PEI": [ "PEI", "I/.", 130],
      // nouveau sol péruvien
      "PEN": [ "PEN", "S./", 2, "S/."],
      // sol péruvien
      "PES": [ "PES", "PES", 130],
      // kina
      "PGK": [ "PGK", "PGK", 2],
      // peso philippin
      "PHP": [ "PHP", "Php", 2, "PHP"],
      // roupie du Pakistan
      "PKR": [ "PKR", "RpP", 2, "PKRs."],
      // zloty
      "PLN": [ "PLN", "Zl", 2],
      // zloty (1950-1995)
      "PLZ": [ "PLZ", "PLZ", 130],
      // escudo portugais
      "PTE": [ "PTE", "Esc", 130],
      // guarani
      "PYG": [ "PYG", "PYG", 0],
      // rial du Qatar
      "QAR": [ "QAR", "RQ", 2],
      // dollar rhodésien
      "RHD": [ "RHD", "$RH", 130],
      // ancien leu roumain
      "ROL": [ "ROL", "ROL", 130],
      // leu roumain
      "RON": [ "RON", "RON", 2],
      // dinar serbe
      "RSD": [ "RSD", "RSD", 2],
      // rouble
      "RUB": [ "RUB", "Rub", 2, "руб"],
      // rouble de Russie (1991-1998)
      "RUR": [ "RUR", "RuR", 130],
      // franc rwandais
      "RWF": [ "RWF", "FrRw", 0],
      // rial saoudien
      "SAR": [ "SAR", "Rls", 2, "SR"],
      // dollar des Îles Salomon
      "SBD": [ "SBD", "$SB", 2],
      // roupie des Seychelles
      "SCR": [ "SCR", "RpS", 2],
      // dinar soudanais
      "SDD": [ "SDD", "SDD", 130],
      // livre soudanaise
      "SDP": [ "SDP", "SDP", 130],
      // couronne suédoise
      "SEK": [ "SEK", "KrSw", 2, "kr"],
      // dollar de Singapour
      "SGD": [ "SGD", "$SG", 2, "S$"],
      // livre de Sainte-Hélène
      "SHP": [ "SHP", "£SH", 2],
      // tolar
      "SIT": [ "SIT", "TSi", 130],
      // couronne slovaque
      "SKK": [ "SKK", "KrSk", 2],
      // léone
      "SLL": [ "SLL", "Ln", 2],
      // shilling de Somalie
      "SOS": [ "SOS", "ShSo", 2],
      // dollar surinamais
      "SRD": [ "SRD", "$SR", 2],
      // florin du Surinam
      "SRG": [ "SRG", "SRG", 130],
      // dobra
      "STD": [ "STD", "Db", 2],
      // rouble de C.E.I.
      "SUR": [ "SUR", "SUR", 130],
      // colon salvadorien
      "SVC": [ "SVC", "SVC", 2],
      // livre syrienne
      "SYP": [ "SYP", "£SY", 2],
      // lilangeni
      "SZL": [ "SZL", "Li", 2],
      // baht
      "THB": [ "THB", "B.", 2, "THB"],
      // rouble du Tadjikistan
      "TJR": [ "TJR", "TJR", 130],
      // somoni du Tadjikistan
      "TJS": [ "TJS", "TJS", 2],
      // Turkmenistan Manat
      "TMM": [ "TMM", "TMM", 2],
      // dinar tunisien
      "TND": [ "TND", "DT", 3],
      // paʻanga
      "TOP": [ "TOP", "$To", 2],
      // escudo de Timor
      "TPE": [ "TPE", "TPE", 130],
      // livre turque
      "TRL": [ "TRL", "TL", 128],
      // nouvelle livre turque
      "TRY": [ "TRY", "YTL", 2, "YTL"],
      // dollar de la Trinité
      "TTD": [ "TTD", "TTD", 2],
      // dollar taïwanais
      "TWD": [ "TWD", "NT$", 2, "NT$"],
      // shilling de Tanzanie
      "TZS": [ "TZS", "ShT", 2],
      // hryvnia
      "UAH": [ "UAH", "hrn", 2],
      // karbovanetz
      "UAK": [ "UAK", "UAK", 130],
      // shilling ougandais (1966-1987)
      "UGS": [ "UGS", "UGS", 130],
      // shilling ougandais
      "UGX": [ "UGX", "U Sh", 2],
      // dollars américains
      "USD": [ "USD", "$US", 2, "US$"],
      // dollar des Etats-Unis (jour suivant)
      "USN": [ "USN", "USN", 130],
      // dollar des Etats-Unis (jour même)
      "USS": [ "USS", "USS", 130],
      // peso uruguayen (1975-1993)
      "UYP": [ "UYP", "UYP", 130],
      // peso uruguayen
      "UYU": [ "UYU", "Ur$", 2, "UY$"],
      // sum
      "UZS": [ "UZS", "sum", 2],
      // bolivar
      "VEB": [ "VEB", "Be", 130],
      // dong
      "VND": [ "VND", "₫", 26, "₫"],
      // vatu
      "VUV": [ "VUV", "VUV", 0],
      // tala
      "WST": [ "WST", "WST", 2],
      // franc CFA (BEAC)
      "XAF": [ "XAF", "XAF", 0],
      // argent
      "XAG": [ "XAG", "XAG", 130],
      // or
      "XAU": [ "XAU", "XAU", 130],
      // unité européenne composée
      "XBA": [ "XBA", "XBA", 130],
      // unité monétaire européenne
      "XBB": [ "XBB", "XBB", 130],
      // unité de compte européenne (XBC)
      "XBC": [ "XBC", "XBC", 130],
      // unité de compte européenne (XBD)
      "XBD": [ "XBD", "XBD", 130],
      // dollar des Caraïbes orientales
      "XCD": [ "XCD", "$EC", 2],
      // droit de tirage spécial
      "XDR": [ "XDR", "XDR", 130],
      // unité de compte européenne (ECU)
      "XEU": [ "XEU", "XEU", 130],
      // franc or
      "XFO": [ "XFO", "XFO", 130],
      // franc UIC
      "XFU": [ "XFU", "XFU", 130],
      // franc CFA (BCEAO)
      "XOF": [ "XOF", "XOF", 0],
      // palladium
      "XPD": [ "XPD", "XPD", 130],
      // franc CFP
      "XPF": [ "XPF", "FCFP", 0],
      // platine
      "XPT": [ "XPT", "XPT", 130],
      // type de fonds RINET
      "XRE": [ "XRE", "XRE", 130],
      // (devise de test)
      "XTS": [ "XTS", "XTS", 130],
      // (devise indéterminée)
      "XXX": [ "XXX", "XXX", 130],
      // dinar du Yémen
      "YDD": [ "YDD", "YDD", 130],
      // riyal du Yémen
      "YER": [ "YER", "RY", 2, "YER"],
      // nouveau dinar yougoslave
      "YUD": [ "YUD", "YUD", 130],
      // dinar yougoslave Noviy
      "YUM": [ "YUM", "YUM", 130],
      // dinar yougoslave convertible
      "YUN": [ "YUN", "YUN", 130],
      // rand sud-africain (financier)
      "ZAL": [ "ZAL", "ZAL", 130],
      // rand
      "ZAR": [ "ZAR", "R.", 2, "ZAR"],
      // kwacha
      "ZMK": [ "ZMK", "Kw", 2],
      // nouveau zaïre
      "ZRN": [ "ZRN", "ZRN", 130],
      // zaïre
      "ZRZ": [ "ZRZ", "ZRZ", 130],
      // dollar du Zimbabwe
      "ZWD": [ "ZWD", "Z$", 2],
    });
  }-*/;
  
  private void loadSuperNamesMap() {
    super.loadNamesMap();
  }
  
  @Override
  protected native void loadNamesMap() /*-{
    this.@com.google.gwt.i18n.client.CurrencyList_fr::loadSuperNamesMap()();
    this.@com.google.gwt.i18n.client.CurrencyList_fr::overrideNamesMap(Lcom/google/gwt/core/client/JavaScriptObject;)({
      "ADP": "peseta andorrane",
      "AED": "dirham des Émirats arabes unis",
      "AFA": "afghani [AFA]",
      "AFN": "afghani",
      "ALL": "lek",
      "AMD": "dram arménien",
      "ANG": "florin des Antilles",
      "AOA": "kwanza angolais",
      "AOK": "kwanza angolais (1977-1990)",
      "AON": "nouveau kwanza angolais (1990-2000)",
      "AOR": "kwanza angolais réajusté (1995-1999)",
      "ARA": "austral argentin",
      "ARP": "peso argentin (1983-1985)",
      "ARS": "peso argentin",
      "ATS": "schilling autrichien",
      "AUD": "dollar australien",
      "AWG": "florin d’Aruba",
      "AZM": "manat azéri",
      "AZN": "manat azéri",
      "BAD": "dinar de Bosnie-Herzegovine",
      "BAM": "mark bosniaque convertible",
      "BBD": "dollar de Barbade",
      "BDT": "taka",
      "BEC": "franc belge (convertible)",
      "BEF": "franc belge",
      "BEL": "franc belge (financier)",
      "BGL": "lev",
      "BGN": "nouveau lev",
      "BHD": "dinar de Bahreïn",
      "BIF": "franc du Burundi",
      "BMD": "dollar des Bermudes",
      "BND": "dollar de Brunei",
      "BOB": "boliviano",
      "BOP": "peso bolivien",
      "BOV": "mvdol",
      "BRB": "nouveau cruzeiro (1967-1986)",
      "BRC": "cruzado",
      "BRE": "cruzeiro (1990-1993)",
      "BRL": "réal",
      "BRN": "nouveau cruzado",
      "BRR": "cruzeiro",
      "BSD": "dollar bahaméen",
      "BTN": "ngultrum",
      "BUK": "kyat [BUK]",
      "BWP": "pula",
      "BYB": "nouveau rouble biélorusse (1994-1999)",
      "BYR": "rouble biélorusse",
      "BZD": "dollar de Belize",
      "CAD": "dollar canadien",
      "CDF": "franc congolais",
      "CHE": "euro WIR",
      "CHF": "franc suisse",
      "CHW": "franc WIR",
      "CLF": "unité d’investissement chilienne",
      "CLP": "peso chilien",
      "CNY": "Yuan Ren-min-bi",
      "COP": "peso colombien",
      "COU": "Unité de valeur réelle colombienne",
      "CRC": "colon de Costa Rica",
      "CSD": "dinar serbo-monténégrin",
      "CSK": "couronne tchèque [CSK]",
      "CUP": "peso cubain",
      "CVE": "escudo du Cap-Vert",
      "CYP": "livre cypriote",
      "CZK": "couronne tchèque",
      "DDM": "mark est-allemand",
      "DEM": "deutsche mark",
      "DJF": "franc de Djibouti",
      "DKK": "couronne danoise",
      "DOP": "peso dominicain",
      "DZD": "dinar algérien",
      "ECS": "sucre",
      "ECV": "unité de valeur constante équatoriale (UVC)",
      "EEK": "couronne estonienne",
      "EGP": "livre égyptienne",
      "EQE": "ekwele",
      "ERN": "Eritrean Nakfa",
      "ESA": "peseta espagnole (compte A)",
      "ESB": "peseta espagnole (compte convertible)",
      "ESP": "peseta espagnole",
      "ETB": "birr",
      "EUR": "euro",
      "FIM": "mark finlandais",
      "FJD": "dollar de Fidji",
      "FKP": "livre des Falkland (Malvinas)",
      "FRF": "franc français",
      "GBP": "livre sterling",
      "GEK": "Georgian Kupon Larit",
      "GEL": "lari",
      "GHC": "cédi",
      "GIP": "livre de Gibraltar",
      "GMD": "dalasie",
      "GNF": "franc guinéen",
      "GNS": "syli",
      "GQE": "ekwélé",
      "GRD": "drachme",
      "GTQ": "quetzal",
      "GWE": "escudo de Guinée portugaise",
      "GWP": "peso de Guinée-Bissau",
      "GYD": "dollar du Guyana",
      "HKD": "dollar de Hong Kong",
      "HNL": "lempira",
      "HRD": "dinar croate",
      "HRK": "kuna",
      "HTG": "gourde",
      "HUF": "forint",
      "IDR": "rupiah",
      "IEP": "livre irlandaise",
      "ILP": "livre israélienne",
      "ILS": "shekel",
      "INR": "roupie indienne",
      "IQD": "dinar irakien",
      "IRR": "rial iranien",
      "ISK": "couronne islandaise",
      "ITL": "lire italienne",
      "JMD": "dollar jamaïcain",
      "JOD": "dinar jordanien",
      "JPY": "yen",
      "KES": "shilling du Kenya",
      "KGS": "som",
      "KHR": "riel",
      "KMF": "franc des Comores",
      "KPW": "won nord-coréen",
      "KRW": "won sud-coréen",
      "KWD": "dinar koweïtien",
      "KYD": "dollar des îles Caïmanes",
      "KZT": "tenge du Kazakhstan",
      "LAK": "kip",
      "LBP": "livre libanaise",
      "LKR": "roupie de Sri Lanka",
      "LRD": "dollar libérien",
      "LSL": "loti",
      "LSM": "maloti",
      "LTL": "litas lituanien",
      "LTT": "talonas",
      "LUC": "franc luxembourgeois convertible",
      "LUF": "franc luxembourgeois",
      "LUL": "franc luxembourgeois financier",
      "LVL": "lats letton",
      "LVR": "rouble letton",
      "LYD": "dinar lybien",
      "MAD": "dirham marocain",
      "MAF": "franc marocain",
      "MDL": "leu moldave",
      "MGA": "ariary",
      "MGF": "franc malgache",
      "MKD": "denar",
      "MLF": "franc malien",
      "MMK": "Myanmar Kyat",
      "MNT": "tugrik",
      "MOP": "pataca",
      "MRO": "ouguija",
      "MTL": "lire maltaise",
      "MTP": "livre maltaise",
      "MUR": "roupie mauricienne",
      "MVR": "roupie des Maldives",
      "MWK": "kwacha [MWK]",
      "MXN": "peso mexicain",
      "MXP": "peso d’argent mexicain (1861-1992)",
      "MXV": "unité de conversion mexicaine (UDI)",
      "MYR": "ringgit malais",
      "MZE": "escudo du Mozambique",
      "MZM": "métical",
      "MZN": "metical",
      "NAD": "dollar namibien",
      "NGN": "naira",
      "NIC": "cordoba",
      "NIO": "cordoba d’or",
      "NLG": "florin néerlandais",
      "NOK": "couronne norvégienne",
      "NPR": "roupie du Népal",
      "NZD": "dollar néo-zélandais",
      "OMR": "rial omani",
      "PAB": "balboa",
      "PEI": "inti péruvien",
      "PEN": "nouveau sol péruvien",
      "PES": "sol péruvien",
      "PGK": "kina",
      "PHP": "peso philippin",
      "PKR": "roupie du Pakistan",
      "PLN": "zloty",
      "PLZ": "zloty (1950-1995)",
      "PTE": "escudo portugais",
      "PYG": "guarani",
      "QAR": "rial du Qatar",
      "RHD": "dollar rhodésien",
      "ROL": "ancien leu roumain",
      "RON": "leu roumain",
      "RSD": "dinar serbe",
      "RUB": "rouble",
      "RUR": "rouble de Russie (1991-1998)",
      "RWF": "franc rwandais",
      "SAR": "rial saoudien",
      "SBD": "dollar des Îles Salomon",
      "SCR": "roupie des Seychelles",
      "SDD": "dinar soudanais",
      "SDP": "livre soudanaise",
      "SEK": "couronne suédoise",
      "SGD": "dollar de Singapour",
      "SHP": "livre de Sainte-Hélène",
      "SIT": "tolar",
      "SKK": "couronne slovaque",
      "SLL": "léone",
      "SOS": "shilling de Somalie",
      "SRD": "dollar surinamais",
      "SRG": "florin du Surinam",
      "STD": "dobra",
      "SUR": "rouble de C.E.I.",
      "SVC": "colon salvadorien",
      "SYP": "livre syrienne",
      "SZL": "lilangeni",
      "THB": "baht",
      "TJR": "rouble du Tadjikistan",
      "TJS": "somoni du Tadjikistan",
      "TMM": "Turkmenistan Manat",
      "TND": "dinar tunisien",
      "TOP": "paʻanga",
      "TPE": "escudo de Timor",
      "TRL": "livre turque",
      "TRY": "nouvelle livre turque",
      "TTD": "dollar de la Trinité",
      "TWD": "dollar taïwanais",
      "TZS": "shilling de Tanzanie",
      "UAH": "hryvnia",
      "UAK": "karbovanetz",
      "UGS": "shilling ougandais (1966-1987)",
      "UGX": "shilling ougandais",
      "USD": "dollars américains",
      "USN": "dollar des Etats-Unis (jour suivant)",
      "USS": "dollar des Etats-Unis (jour même)",
      "UYP": "peso uruguayen (1975-1993)",
      "UYU": "peso uruguayen",
      "UZS": "sum",
      "VEB": "bolivar",
      "VND": "dong",
      "VUV": "vatu",
      "WST": "tala",
      "XAF": "franc CFA (BEAC)",
      "XAG": "argent",
      "XAU": "or",
      "XBA": "unité européenne composée",
      "XBB": "unité monétaire européenne",
      "XBC": "unité de compte européenne (XBC)",
      "XBD": "unité de compte européenne (XBD)",
      "XCD": "dollar des Caraïbes orientales",
      "XDR": "droit de tirage spécial",
      "XEU": "unité de compte européenne (ECU)",
      "XFO": "franc or",
      "XFU": "franc UIC",
      "XOF": "franc CFA (BCEAO)",
      "XPD": "palladium",
      "XPF": "franc CFP",
      "XPT": "platine",
      "XRE": "type de fonds RINET",
      "XTS": "(devise de test)",
      "XXX": "(devise indéterminée)",
      "YDD": "dinar du Yémen",
      "YER": "riyal du Yémen",
      "YUD": "nouveau dinar yougoslave",
      "YUM": "dinar yougoslave Noviy",
      "YUN": "dinar yougoslave convertible",
      "ZAL": "rand sud-africain (financier)",
      "ZAR": "rand",
      "ZMK": "kwacha",
      "ZRN": "nouveau zaïre",
      "ZRZ": "zaïre",
      "ZWD": "dollar du Zimbabwe",
    });
  }-*/;
  
  @Override
  public native CurrencyData getDefault() /*-{
    return [ "EUR", "€", 2, "€"];
  }-*/;
}
