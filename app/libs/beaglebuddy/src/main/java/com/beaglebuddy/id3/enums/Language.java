package com.beaglebuddy.id3.enums;

import com.beaglebuddy.id3.enums.v23.Encoding;                    // imported to obtain an ISO 8859 character set encoding



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language codes and names.
 * I have found contradictory listings for the ISO 639-2 language codes.  For example, the <a href="http://www.intratext.com/info/iso639-2.htm">ISO-639-2</a> language codes
 * listed at this website differs from the previous one.
 */
public enum Language
{                                                                                           /** Afar                                                                             */
   AAR("Afar"                                                                            ), /** Abkhazian                                                                        */
   ABK("Abkhazian"                                                                       ), /** Achinese                                                                         */
   ACE("Achinese"                                                                        ), /** Acoli                                                                            */
   ACH("Acoli"                                                                           ), /** Adangme                                                                          */
   ADA("Adangme"                                                                         ), /** Adyghe; Adygei                                                                   */
   ADY("Adyghe; Adygei"                                                                  ), /** Afro-Asiatic languages                                                           */
   AFA("Afro-Asiatic languages"                                                          ), /** Afrihili                                                                         */
   AFH("Afrihili"                                                                        ), /** Afrikaans                                                                        */
   AFR("Afrikaans"                                                                       ), /** Ainu                                                                             */
   AIN("Ainu"                                                                            ), /** Akan                                                                             */
   AKA("Akan"                                                                            ), /** Akkadian                                                                         */
   AKK("Akkadian"                                                                        ), /** Albanian                                                                         */
   ALB("Albanian"                                                                        ), /** Aleut                                                                            */
   ALE("Aleut"                                                                           ), /** Algonquian languages                                                             */
   ALG("Algonquian languages"                                                            ), /** Southern Altai                                                                   */
   ALT("Southern Altai"                                                                  ), /** Amharic                                                                          */
   AMH("Amharic"                                                                         ), /** English, Old (ca.450-1100)                                                       */
   ANG("English, Old (ca.450-1100)"                                                      ), /** Angika                                                                           */
   ANP("Angika"                                                                          ), /** Apache languages                                                                 */
   APA("Apache languages"                                                                ), /** Arabic                                                                           */
   ARA("Arabic"                                                                          ), /** Official Aramaic (700-300 BCE Imperial Aramaic (700-300 BCE)                     */
   ARC("Official Aramaic (700-300 BCE Imperial Aramaic (700-300 BCE)"                    ), /** Aragonese                                                                        */
   ARG("Aragonese"                                                                       ), /** Armenian                                                                         */
   ARM("Armenian"                                                                        ), /** Mapudungun; Mapuche                                                              */
   ARN("Mapudungun; Mapuche"                                                             ), /** Arapaho                                                                          */
   ARP("Arapaho"                                                                         ), /** Artificial languages                                                             */
   ART("Artificial languages"                                                            ), /** Arawak                                                                           */
   ARW("Arawak"                                                                          ), /** Assamese                                                                         */
   ASM("Assamese"                                                                        ), /** Asturian; Bable; Leonese; Asturleonese                                           */
   AST("Asturian; Bable; Leonese; Asturleonese"                                          ), /** Athapascan languages                                                             */
   ATH("Athapascan languages"                                                            ), /** Australian languages                                                             */
   AUS("Australian languages"                                                            ), /** Avaric                                                                           */
   AVA("Avaric"                                                                          ), /** Avestan                                                                          */
   AVE("Avestan"                                                                         ), /** Awadhi                                                                           */
   AWA("Awadhi"                                                                          ), /** Aymara                                                                           */
   AYM("Aymara"                                                                          ), /** Azerbaijani                                                                      */
   AZE("Azerbaijani"                                                                     ), /** Banda languages                                                                  */
   BAD("Banda languages"                                                                 ), /** Bamileke languages                                                               */
   BAI("Bamileke languages"                                                              ), /** Bashkir                                                                          */
   BAK("Bashkir"                                                                         ), /** Baluchi                                                                          */
   BAL("Baluchi"                                                                         ), /** Bambara                                                                          */
   BAM("Bambara"                                                                         ), /** Balinese                                                                         */
   BAN("Balinese"                                                                        ), /** Basque                                                                           */
   BAQ("Basque"                                                                          ), /** Basa                                                                             */
   BAS("Basa"                                                                            ), /** Baltic languages                                                                 */
   BAT("Baltic languages"                                                                ), /** Beja; Bedawiyet                                                                  */
   BEJ("Beja; Bedawiyet"                                                                 ), /** Belarusian                                                                       */
   BEL("Belarusian"                                                                      ), /** Bemba                                                                            */
   BEM("Bemba"                                                                           ), /** Bengali                                                                          */
   BEN("Bengali"                                                                         ), /** Berber languages                                                                 */
   BER("Berber languages"                                                                ), /** Bhojpuri                                                                         */
   BHO("Bhojpuri"                                                                        ), /** Bihari languages                                                                 */
   BIH("Bihari languages"                                                                ), /** Bikol                                                                            */
   BIK("Bikol"                                                                           ), /** Bini; Edo                                                                        */
   BIN("Bini; Edo"                                                                       ), /** Bislama                                                                          */
   BIS("Bislama"                                                                         ), /** Siksika                                                                          */
   BLA("Siksika"                                                                         ), /** Bantu languages                                                                  */
   BNT("Bantu languages"                                                                 ), /** Tibetan                                                                          */
   BOD("Tibetan"                                                                         ), /** Bosnian                                                                          */
   BOS("Bosnian"                                                                         ), /** Braj                                                                             */
   BRA("Braj"                                                                            ), /** Breton                                                                           */
   BRE("Breton"                                                                          ), /** Batak languages                                                                  */
   BTK("Batak languages"                                                                 ), /** Buriat                                                                           */
   BUA("Buriat"                                                                          ), /** Buginese                                                                         */
   BUG("Buginese"                                                                        ), /** Bulgarian                                                                        */
   BUL("Bulgarian"                                                                       ), /** Burmese                                                                          */
   BUR("Burmese"                                                                         ), /** Blin; Bilin                                                                      */
   BYN("Blin; Bilin"                                                                     ), /** Caddo                                                                            */
   CAD("Caddo"                                                                           ), /** Central American Indian languages                                                */
   CAI("Central American Indian languages"                                               ), /** Galibi Carib                                                                     */
   CAR("Galibi Carib"                                                                    ), /** Catalan; Valencian                                                               */
   CAT("Catalan; Valencian"                                                              ), /** Caucasian languages                                                              */
   CAU("Caucasian languages"                                                             ), /** Cebuano                                                                          */
   CEB("Cebuano"                                                                         ), /** Celtic languages                                                                 */
   CEL("Celtic languages"                                                                ), /** Czech                                                                            */
   CES("Czech"                                                                           ), /** Chamorro                                                                         */
   CHA("Chamorro"                                                                        ), /** Chibcha                                                                          */
   CHB("Chibcha"                                                                         ), /** Chechen                                                                          */
   CHE("Chechen"                                                                         ), /** Chagatai                                                                         */
   CHG("Chagatai"                                                                        ), /** Chinese                                                                          */
   CHI("Chinese"                                                                         ), /** Chuukese                                                                         */
   CHK("Chuukese"                                                                        ), /** Mari                                                                             */
   CHM("Mari"                                                                            ), /** Chinook jargon                                                                   */
   CHN("Chinook jargon"                                                                  ), /** Choctaw                                                                          */
   CHO("Choctaw"                                                                         ), /** Chipewyan; Dene Suline                                                           */
   CHP("Chipewyan; Dene Suline"                                                          ), /** Cherokee                                                                         */
   CHR("Cherokee"                                                                        ), /** Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic */
   CHU("Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic"), /** Chuvash                                                                          */
   CHV("Chuvash"                                                                         ), /** Cheyenne                                                                         */
   CHY("Cheyenne"                                                                        ), /** Chamic languages                                                                 */
   CMC("Chamic languages"                                                                ), /** Coptic                                                                           */
   COP("Coptic"                                                                          ), /** Cornish                                                                          */
   COR("Cornish"                                                                         ), /** Corsican                                                                         */
   COS("Corsican"                                                                        ), /** Creoles and pidgins, English based                                               */
   CPE("Creoles and pidgins, English based"                                              ), /** Creoles and pidgins, French-based                                                */
   CPF("Creoles and pidgins, French-based"                                               ), /** Creoles and pidgins, Portuguese-based                                            */
   CPP("Creoles and pidgins, Portuguese-based"                                           ), /** Cree                                                                             */
   CRE("Cree"                                                                            ), /** Crimean Tatar; Crimean Turkish                                                   */
   CRH("Crimean Tatar; Crimean Turkish"                                                  ), /** Creoles and pidgins                                                              */
   CRP("Creoles and pidgins"                                                             ), /** Kashubian                                                                        */
   CSB("Kashubian"                                                                       ), /** Cushitic languages                                                               */
   CUS("Cushitic languages"                                                              ), /** Welsh                                                                            */
   CYM("Welsh"                                                                           ), /** Czech                                                                            */
   CZE("Czech"                                                                           ), /** Dakota                                                                           */
   DAK("Dakota"                                                                          ), /** Danish                                                                           */
   DAN("Danish"                                                                          ), /** Dargwa                                                                           */
   DAR("Dargwa"                                                                          ), /** Land Dayak languages                                                             */
   DAY("Land Dayak languages"                                                            ), /** Delaware                                                                         */
   DEL("Delaware"                                                                        ), /** Slave (Athapascan)                                                               */
   DEN("Slave (Athapascan)"                                                              ), /** German                                                                           */
   DEU("German"                                                                          ), /** Dogrib                                                                           */
   DGR("Dogrib"                                                                          ), /** Dinka                                                                            */
   DIN("Dinka"                                                                           ), /** Divehi; Dhivehi; Maldivian                                                       */
   DIV("Divehi; Dhivehi; Maldivian"                                                      ), /** Dogri                                                                            */
   DOI("Dogri"                                                                           ), /** Dravidian languages                                                              */
   DRA("Dravidian languages"                                                             ), /** Lower Sorbian                                                                    */
   DSB("Lower Sorbian"                                                                   ), /** Duala                                                                            */
   DUA("Duala"                                                                           ), /** Dutch, Middle (ca.1050-1350)                                                     */
   DUM("Dutch, Middle (ca.1050-1350)"                                                    ), /** Dutch; Flemish                                                                   */
   DUT("Dutch; Flemish"                                                                  ), /** Dyula                                                                            */
   DYU("Dyula"                                                                           ), /** Dzongkha                                                                         */
   DZO("Dzongkha"                                                                        ), /** Efik                                                                             */
   EFI("Efik"                                                                            ), /** Egyptian (Ancient)                                                               */
   EGY("Egyptian (Ancient)"                                                              ), /** Ekajuk                                                                           */
   EKA("Ekajuk"                                                                          ), /** Greek, Modern (1453-)                                                            */
   ELL("Greek, Modern (1453-)"                                                           ), /** Elamite                                                                          */
   ELX("Elamite"                                                                         ), /** English                                                                          */
   ENG("English"                                                                         ), /** English, Middle (1100-1500)                                                      */
   ENM("English, Middle (1100-1500)"                                                     ), /** Esperanto                                                                        */
   EPO("Esperanto"                                                                       ), /** Spanish - obsolete                                                               */
   ESL("Spanish - obsolete"                                                              ), /** Estonian                                                                         */
   EST("Estonian"                                                                        ), /** Basque                                                                           */
   EUS("Basque"                                                                          ), /** Ewe                                                                              */
   EWE("Ewe"                                                                             ), /** Ewondo                                                                           */
   EWO("Ewondo"                                                                          ), /** Fang                                                                             */
   FAN("Fang"                                                                            ), /** Faroese                                                                          */
   FAO("Faroese"                                                                         ), /** Persian                                                                          */
   FAS("Persian"                                                                         ), /** Fanti                                                                            */
   FAT("Fanti"                                                                           ), /** Fijian                                                                           */
   FIJ("Fijian"                                                                          ), /** Filipino; Pilipino                                                               */
   FIL("Filipino; Pilipino"                                                              ), /** Finnish                                                                          */
   FIN("Finnish"                                                                         ), /** Finno-Ugrian languages                                                           */
   FIU("Finno-Ugrian languages"                                                          ), /** Fon                                                                              */
   FON("Fon"                                                                             ), /** French                                                                           */
   FRA("French"                                                                          ), /** French                                                                           */
   FRE("French"                                                                          ), /** French, Middle (ca.1400-1600)                                                    */
   FRM("French, Middle (ca.1400-1600)"                                                   ), /** French, Old (842-ca.1400)                                                        */
   FRO("French, Old (842-ca.1400)"                                                       ), /** Northern Frisian                                                                 */
   FRR("Northern Frisian"                                                                ), /** Eastern Frisian                                                                  */
   FRS("Eastern Frisian"                                                                 ), /** Western Frisian                                                                  */
   FRY("Western Frisian"                                                                 ), /** Fulah                                                                            */
   FUL("Fulah"                                                                           ), /** Friulian                                                                         */
   FUR("Friulian"                                                                        ), /** Ga                                                                               */
   GAA("Ga"                                                                              ), /** Gayo                                                                             */
   GAY("Gayo"                                                                            ), /** Gbaya                                                                            */
   GBA("Gbaya"                                                                           ), /** Germanic languages                                                               */
   GEM("Germanic languages"                                                              ), /** Georgian                                                                         */
   GEO("Georgian"                                                                        ), /** German                                                                           */
   GER("German"                                                                          ), /** Geez                                                                             */
   GEZ("Geez"                                                                            ), /** Gilbertese                                                                       */
   GIL("Gilbertese"                                                                      ), /** Gaelic; Scottish Gaelic                                                          */
   GLA("Gaelic; Scottish Gaelic"                                                         ), /** Irish                                                                            */
   GLE("Irish"                                                                           ), /** Galician                                                                         */
   GLG("Galician"                                                                        ), /** Manx                                                                             */
   GLV("Manx"                                                                            ), /** German, Middle High (ca.1050-1500)                                               */
   GMH("German, Middle High (ca.1050-1500)"                                              ), /** German, Old High (ca.750-1050)                                                   */
   GOH("German, Old High (ca.750-1050)"                                                  ), /** Gondi                                                                            */
   GON("Gondi"                                                                           ), /** Gorontalo                                                                        */
   GOR("Gorontalo"                                                                       ), /** Gothic                                                                           */
   GOT("Gothic"                                                                          ), /** Grebo                                                                            */
   GRB("Grebo"                                                                           ), /** Greek, Ancient (to 1453)                                                         */
   GRC("Greek, Ancient (to 1453)"                                                        ), /** Greek, Modern (1453-)                                                            */
   GRE("Greek, Modern (1453-)"                                                           ), /** Guarani                                                                          */
   GRN("Guarani"                                                                         ), /** Swiss German; Alemannic; Alsatian                                                */
   GSW("Swiss German; Alemannic; Alsatian"                                               ), /** Gujarati                                                                         */
   GUJ("Gujarati"                                                                        ), /** Gwich'in                                                                         */
   GWI("Gwich'in"                                                                        ), /** Haida                                                                            */
   HAI("Haida"                                                                           ), /** Haitian; Haitian Creole                                                          */
   HAT("Haitian; Haitian Creole"                                                         ), /** Hausa                                                                            */
   HAU("Hausa"                                                                           ), /** Hawaiian                                                                         */
   HAW("Hawaiian"                                                                        ), /** Hebrew                                                                           */
   HEB("Hebrew"                                                                          ), /** Herero                                                                           */
   HER("Herero"                                                                          ), /** Hiligaynon                                                                       */
   HIL("Hiligaynon"                                                                      ), /** Himachali languages; Western Pahari languages                                    */
   HIM("Himachali languages; Western Pahari languages"                                   ), /** Hindi                                                                            */
   HIN("Hindi"                                                                           ), /** Hittite                                                                          */
   HIT("Hittite"                                                                         ), /** Hmong; Mong                                                                      */
   HMN("Hmong; Mong"                                                                     ), /** Hiri Motu                                                                        */
   HMO("Hiri Motu"                                                                       ), /** Croatian                                                                         */
   HRV("Croatian"                                                                        ), /** Upper Sorbian                                                                    */
   HSB("Upper Sorbian"                                                                   ), /** Hungarian                                                                        */
   HUN("Hungarian"                                                                       ), /** Hupa                                                                             */
   HUP("Hupa"                                                                            ), /** Armenian                                                                         */
   HYE("Armenian"                                                                        ), /** Iban                                                                             */
   IBA("Iban"                                                                            ), /** Igbo                                                                             */
   IBO("Igbo"                                                                            ), /** Icelandic                                                                        */
   ICE("Icelandic"                                                                       ), /** Ido                                                                              */
   IDO("Ido"                                                                             ), /** Sichuan Yi; Nuosu                                                                */
   III("Sichuan Yi; Nuosu"                                                               ), /** Ijo languages                                                                    */
   IJO("Ijo languages"                                                                   ), /** Inuktitut                                                                        */
   IKU("Inuktitut"                                                                       ), /** Interlingue; Occidental                                                          */
   ILE("Interlingue; Occidental"                                                         ), /** Iloko                                                                            */
   ILO("Iloko"                                                                           ), /** Interlingua (International Auxiliary Language Association)                       */
   INA("Interlingua (International Auxiliary Language Association)"                      ), /** Indic languages                                                                  */
   INC("Indic languages"                                                                 ), /** Indonesian                                                                       */
   IND("Indonesian"                                                                      ), /** Indo-European languages                                                          */
   INE("Indo-European languages"                                                         ), /** Ingush                                                                           */
   INH("Ingush"                                                                          ), /** Inupiaq                                                                          */
   IPK("Inupiaq"                                                                         ), /** Iranian languages                                                                */
   IRA("Iranian languages"                                                               ), /** Iroquoian languages                                                              */
   IRO("Iroquoian languages"                                                             ), /** Icelandic                                                                        */
   ISL("Icelandic"                                                                       ), /** Italian                                                                          */
   ITA("Italian"                                                                         ), /** Javanese                                                                         */
   JAV("Javanese"                                                                        ), /** Lojban                                                                           */
   JBO("Lojban"                                                                          ), /** Japanese                                                                         */
   JPN("Japanese"                                                                        ), /** Judeo-Persian                                                                    */
   JPR("Judeo-Persian"                                                                   ), /** Judeo-Arabic                                                                     */
   JRB("Judeo-Arabic"                                                                    ), /** Kara-Kalpak                                                                      */
   KAA("Kara-Kalpak"                                                                     ), /** Kabyle                                                                           */
   KAB("Kabyle"                                                                          ), /** Kachin; Jingpho                                                                  */
   KAC("Kachin; Jingpho"                                                                 ), /** Kalaallisut; Greenlandic                                                         */
   KAL("Kalaallisut; Greenlandic"                                                        ), /** Kamba                                                                            */
   KAM("Kamba"                                                                           ), /** Kannada                                                                          */
   KAN("Kannada"                                                                         ), /** Karen languages                                                                  */
   KAR("Karen languages"                                                                 ), /** Kashmiri                                                                         */
   KAS("Kashmiri"                                                                        ), /** Georgian                                                                         */
   KAT("Georgian"                                                                        ), /** Kanuri                                                                           */
   KAU("Kanuri"                                                                          ), /** Kawi                                                                             */
   KAW("Kawi"                                                                            ), /** Kazakh                                                                           */
   KAZ("Kazakh"                                                                          ), /** Kabardian                                                                        */
   KBD("Kabardian"                                                                       ), /** Khasi                                                                            */
   KHA("Khasi"                                                                           ), /** Khoisan languages                                                                */
   KHI("Khoisan languages"                                                               ), /** Central Khmer                                                                    */
   KHM("Central Khmer"                                                                   ), /** Khotanese; Sakan                                                                 */
   KHO("Khotanese; Sakan"                                                                ), /** Kikuyu; Gikuyu                                                                   */
   KIK("Kikuyu; Gikuyu"                                                                  ), /** Kinyarwanda                                                                      */
   KIN("Kinyarwanda"                                                                     ), /** Kirghiz; Kyrgyz                                                                  */
   KIR("Kirghiz; Kyrgyz"                                                                 ), /** Kimbundu                                                                         */
   KMB("Kimbundu"                                                                        ), /** Konkani                                                                          */
   KOK("Konkani"                                                                         ), /** Komi                                                                             */
   KOM("Komi"                                                                            ), /** Kongo                                                                            */
   KON("Kongo"                                                                           ), /** Korean                                                                           */
   KOR("Korean"                                                                          ), /** Kosraean                                                                         */
   KOS("Kosraean"                                                                        ), /** Kpelle                                                                           */
   KPE("Kpelle"                                                                          ), /** Karachay-Balkar                                                                  */
   KRC("Karachay-Balkar"                                                                 ), /** Karelian                                                                         */
   KRL("Karelian"                                                                        ), /** Kru languages                                                                    */
   KRO("Kru languages"                                                                   ), /** Kurukh                                                                           */
   KRU("Kurukh"                                                                          ), /** Kuanyama; Kwanyama                                                               */
   KUA("Kuanyama; Kwanyama"                                                              ), /** Kumyk                                                                            */
   KUM("Kumyk"                                                                           ), /** Kurdish                                                                          */
   KUR("Kurdish"                                                                         ), /** Kutenai                                                                          */
   KUT("Kutenai"                                                                         ), /** Ladino                                                                           */
   LAD("Ladino"                                                                          ), /** Lahnda                                                                           */
   LAH("Lahnda"                                                                          ), /** Lamba                                                                            */
   LAM("Lamba"                                                                           ), /** Lao                                                                              */
   LAO("Lao"                                                                             ), /** Latin                                                                            */
   LAT("Latin"                                                                           ), /** Latvian                                                                          */
   LAV("Latvian"                                                                         ), /** Lezghian                                                                         */
   LEZ("Lezghian"                                                                        ), /** Limburgan; Limburger; Limburgish                                                 */
   LIM("Limburgan; Limburger; Limburgish"                                                ), /** Lingala                                                                          */
   LIN("Lingala"                                                                         ), /** Lithuanian                                                                       */
   LIT("Lithuanian"                                                                      ), /** Mongo                                                                            */
   LOL("Mongo"                                                                           ), /** Lozi                                                                             */
   LOZ("Lozi"                                                                            ), /** Luxembourgish; Letzeburgesch                                                     */
   LTZ("Luxembourgish; Letzeburgesch"                                                    ), /** Luba-Lulua                                                                       */
   LUA("Luba-Lulua"                                                                      ), /** Luba-Katanga                                                                     */
   LUB("Luba-Katanga"                                                                    ), /** Ganda                                                                            */
   LUG("Ganda"                                                                           ), /** Luiseno                                                                          */
   LUI("Luiseno"                                                                         ), /** Lunda   lunda                                                                    */
   LUN("Lunda   lunda"                                                                   ), /** Luo (Kenya and Tanzania)                                                         */
   LUO("Luo (Kenya and Tanzania)"                                                        ), /** Lushai                                                                           */
   LUS("Lushai"                                                                          ), /** Macedonian                                                                       */
   MAC("Macedonian"                                                                      ), /** Madurese                                                                         */
   MAD("Madurese"                                                                        ), /** Magahi                                                                           */
   MAG("Magahi"                                                                          ), /** Marshallese                                                                      */
   MAH("Marshallese"                                                                     ), /** Maithili                                                                         */
   MAI("Maithili"                                                                        ), /** Makasar                                                                          */
   MAK("Makasar"                                                                         ), /** Malayalam                                                                        */
   MAL("Malayalam"                                                                       ), /** Mandingo                                                                         */
   MAN("Mandingo"                                                                        ), /** Maori                                                                            */
   MAO("Maori"                                                                           ), /** Austronesian languages                                                           */
   MAP("Austronesian languages"                                                          ), /** Marathi                                                                          */
   MAR("Marathi"                                                                         ), /** Masai                                                                            */
   MAS("Masai"                                                                           ), /** Malay                                                                            */
   MAY("Malay"                                                                           ), /** Moksha                                                                           */
   MDF("Moksha"                                                                          ), /** Mandar                                                                           */
   MDR("Mandar"                                                                          ), /** Mende                                                                            */
   MEN("Mende"                                                                           ), /** Irish, Middle (900-1200)                                                         */
   MGA("Irish, Middle (900-1200)"                                                        ), /** Mi'kmaq; Micmac                                                                  */
   MIC("Mi'kmaq; Micmac"                                                                 ), /** Minangkabau                                                                      */
   MIN("Minangkabau"                                                                     ), /** Uncoded languages                                                                */
   MIS("Uncoded languages"                                                               ), /** Macedonian                                                                       */
   MKD("Macedonian"                                                                      ), /** Mon-Khmer languages                                                              */
   MKH("Mon-Khmer languages"                                                             ), /** Malagasy                                                                         */
   MLG("Malagasy"                                                                        ), /** Maltese                                                                          */
   MLT("Maltese"                                                                         ), /** Manchu                                                                           */
   MNC("Manchu"                                                                          ), /** Manipuri                                                                         */
   MNI("Manipuri"                                                                        ), /** Manobo languages                                                                 */
   MNO("Manobo languages"                                                                ), /** Mohawk                                                                           */
   MOH("Mohawk"                                                                          ), /** Mongolian                                                                        */
   MON("Mongolian"                                                                       ), /** Mossi                                                                            */
   MOS("Mossi"                                                                           ), /** Maori                                                                            */
   MRI("Maori"                                                                           ), /** Malay                                                                            */
   MSA("Malay"                                                                           ), /** Multiple languages                                                               */
   MUL("Multiple languages"                                                              ), /** Munda languages                                                                  */
   MUN("Munda languages"                                                                 ), /** Creek                                                                            */
   MUS("Creek"                                                                           ), /** Mirandese                                                                        */
   MWL("Mirandese"                                                                       ), /** Marwari                                                                          */
   MWR("Marwari"                                                                         ), /** Burmese                                                                          */
   MYA("Burmese"                                                                         ), /** Mayan languages                                                                  */
   MYN("Mayan languages"                                                                 ), /** Erzya                                                                            */
   MYV("Erzya"                                                                           ), /** Nahuatl languages                                                                */
   NAH("Nahuatl languages"                                                               ), /** North American Indian languages                                                  */
   NAI("North American Indian languages"                                                 ), /** Neapolitan                                                                       */
   NAP("Neapolitan"                                                                      ), /** Nauru                                                                            */
   NAU("Nauru"                                                                           ), /** Navajo; Navaho                                                                   */
   NAV("Navajo; Navaho"                                                                  ), /** Ndebele, South; South Ndebele                                                    */
   NBL("Ndebele, South; South Ndebele"                                                   ), /** Ndebele, North; North Ndebele                                                    */
   NDE("Ndebele, North; North Ndebele"                                                   ), /** Ndonga                                                                           */
   NDO("Ndonga"                                                                          ), /** Low German; Low Saxon; German, Low; Saxon, Low                                   */
   NDS("Low German; Low Saxon; German, Low; Saxon, Low"                                  ), /** Nepali                                                                           */
   NEP("Nepali"                                                                          ), /** Nepal Bhasa; Newari                                                              */
   NEW("Nepal Bhasa; Newari"                                                             ), /** Nias                                                                             */
   NIA("Nias"                                                                            ), /** Niger-Kordofanian languages                                                      */
   NIC("Niger-Kordofanian languages"                                                     ), /** Niuean                                                                           */
   NIU("Niuean"                                                                          ), /** Dutch; Flemish                                                                   */
   NLD("Dutch; Flemish"                                                                  ), /** Norwegian Nynorsk; Nynorsk, Norwegian                                            */
   NNO("Norwegian Nynorsk; Nynorsk, Norwegian"                                           ), /** Bokm�l, Norwegian; Norwegian Bokm�l                                              */
   NOB("Bokm�l, Norwegian; Norwegian Bokm�l"                                             ), /** Nogai                                                                            */
   NOG("Nogai"                                                                           ), /** Norse, Old                                                                       */
   NON("Norse, Old"                                                                      ), /** Norwegian                                                                        */
   NOR("Norwegian"                                                                       ), /** N'Ko                                                                             */
   NQO("N'Ko"                                                                            ), /** Pedi; Sepedi; Northern Sotho                                                     */
   NSO("Pedi; Sepedi; Northern Sotho"                                                    ), /** Nubian languages                                                                 */
   NUB("Nubian languages"                                                                ), /** Classical Newari; Old Newari; Classical Nepal Bhasa                              */
   NWC("Classical Newari; Old Newari; Classical Nepal Bhasa"                             ), /** Chichewa; Chewa; Nyanja                                                          */
   NYA("Chichewa; Chewa; Nyanja"                                                         ), /** Nyamwezi                                                                         */
   NYM("Nyamwezi"                                                                        ), /** Nyankole                                                                         */
   NYN("Nyankole"                                                                        ), /** Nyoro                                                                            */
   NYO("Nyoro"                                                                           ), /** Nzima                                                                            */
   NZI("Nzima"                                                                           ), /** Occitan (post 1500)                                                              */
   OCI("Occitan (post 1500)"                                                             ), /** Ojibwa                                                                           */
   OJI("Ojibwa"                                                                          ), /** Oriya                                                                            */
   ORI("Oriya"                                                                           ), /** Oromo                                                                            */
   ORM("Oromo"                                                                           ), /** Osage                                                                            */
   OSA("Osage"                                                                           ), /** Ossetian; Ossetic                                                                */
   OSS("Ossetian; Ossetic"                                                               ), /** Turkish, Ottoman (1500-1928)                                                     */
   OTA("Turkish, Ottoman (1500-1928)"                                                    ), /** Otomian languages                                                                */
   OTO("Otomian languages"                                                               ), /** Papuan languages                                                                 */
   PAA("Papuan languages"                                                                ), /** Pangasinan                                                                       */
   PAG("Pangasinan"                                                                      ), /** Pahlavi                                                                          */
   PAL("Pahlavi"                                                                         ), /** Pampanga; Kapampangan                                                            */
   PAM("Pampanga; Kapampangan"                                                           ), /** Panjabi; Punjabi                                                                 */
   PAN("Panjabi; Punjabi"                                                                ), /** Papiamento                                                                       */
   PAP("Papiamento"                                                                      ), /** Palauan                                                                          */
   PAU("Palauan"                                                                         ), /** Persian, Old (ca.600-400 B.C.)                                                   */
   PEO("Persian, Old (ca.600-400 B.C.)"                                                  ), /** Persian                                                                          */
   PER("Persian"                                                                         ), /** Philippine languages                                                             */
   PHI("Philippine languages"                                                            ), /** Phoenician                                                                       */
   PHN("Phoenician"                                                                      ), /** Pali                                                                             */
   PLI("Pali"                                                                            ), /** Polish                                                                           */
   POL("Polish"                                                                          ), /** Pohnpeian                                                                        */
   PON("Pohnpeian"                                                                       ), /** Portuguese                                                                       */
   POR("Portuguese"                                                                      ), /** Prakrit languages                                                                */
   PRA("Prakrit languages"                                                               ), /** Proven�al, Old (to 1500Occitan, Old (to 1500)                                    */
   PRO("Proven�al, Old (to 1500Occitan, Old (to 1500)"                                   ), /** Pushto; Pashto                                                                   */
   PUS("Pushto; Pashto"                                                                  ), /** Quechua                                                                          */
   QUE("Quechua"                                                                         ), /** Rajasthani                                                                       */
   RAJ("Rajasthani"                                                                      ), /** Rapanui                                                                          */
   RAP("Rapanui"                                                                         ), /** Rarotongan; Cook Islands Maori                                                   */
   RAR("Rarotongan; Cook Islands Maori"                                                  ), /** Romance languages                                                                */
   ROA("Romance languages"                                                               ), /** Romansh                                                                          */
   ROH("Romansh"                                                                         ), /** Romany                                                                           */
   ROM("Romany"                                                                          ), /** Romanian; Moldavian; Moldovan                                                    */
   RON("Romanian; Moldavian; Moldovan"                                                   ), /** Romanian; Moldavian; Moldovan                                                    */
   RUM("Romanian; Moldavian; Moldovan"                                                   ), /** Rundi                                                                            */
   RUN("Rundi"                                                                           ), /** Aromanian; Arumanian; Macedo-Romanian                                            */
   RUP("Aromanian; Arumanian; Macedo-Romanian"                                           ), /** Russian                                                                          */
   RUS("Russian"                                                                         ), /** Sandawe                                                                          */
   SAD("Sandawe"                                                                         ), /** Sango                                                                            */
   SAG("Sango"                                                                           ), /** Yakut                                                                            */
   SAH("Yakut"                                                                           ), /** South American Indian languages                                                  */
   SAI("South American Indian languages"                                                 ), /** Salishan languages                                                               */
   SAL("Salishan languages"                                                              ), /** Samaritan Aramaic                                                                */
   SAM("Samaritan Aramaic"                                                               ), /** Sanskrit                                                                         */
   SAN("Sanskrit"                                                                        ), /** Sasak                                                                            */
   SAS("Sasak"                                                                           ), /** Santali                                                                          */
   SAT("Santali"                                                                         ), /** Sicilian                                                                         */
   SCN("Sicilian"                                                                        ), /** Scots                                                                            */
   SCO("Scots"                                                                           ), /** Selkup                                                                           */
   SEL("Selkup"                                                                          ), /** Semitic languages                                                                */
   SEM("Semitic languages"                                                               ), /** Irish, Old (to 900)                                                              */
   SGA("Irish, Old (to 900)"                                                             ), /** Sign Languages                                                                   */
   SGN("Sign Languages"                                                                  ), /** Shan                                                                             */
   SHN("Shan"                                                                            ), /** Sidamo                                                                           */
   SID("Sidamo"                                                                          ), /** Sinhala; Sinhalese                                                               */
   SIN("Sinhala; Sinhalese"                                                              ), /** Siouan languages                                                                 */
   SIO("Siouan languages"                                                                ), /** Sino-Tibetan languages                                                           */
   SIT("Sino-Tibetan languages"                                                          ), /** Slavic languages                                                                 */
   SLA("Slavic languages"                                                                ), /** Slovak                                                                           */
   SLK("Slovak"                                                                          ), /** Slovak                                                                           */
   SLO("Slovak"                                                                          ), /** Slovenian                                                                        */
   SLV("Slovenian"                                                                       ), /** Southern Sami                                                                    */
   SMA("Southern Sami"                                                                   ), /** Northern Sami                                                                    */
   SME("Northern Sami"                                                                   ), /** Sami languages                                                                   */
   SMI("Sami languages"                                                                  ), /** Lule Sami                                                                        */
   SMJ("Lule Sami"                                                                       ), /** Inari Sami                                                                       */
   SMN("Inari Sami"                                                                      ), /** Samoan                                                                           */
   SMO("Samoan"                                                                          ), /** Skolt Sami                                                                       */
   SMS("Skolt Sami"                                                                      ), /** Shona                                                                            */
   SNA("Shona"                                                                           ), /** Sindhi                                                                           */
   SND("Sindhi"                                                                          ), /** Soninke                                                                          */
   SNK("Soninke"                                                                         ), /** Sogdian                                                                          */
   SOG("Sogdian"                                                                         ), /** Somali                                                                           */
   SOM("Somali"                                                                          ), /** Songhai languages                                                                */
   SON("Songhai languages"                                                               ), /** Sotho, Southern                                                                  */
   SOT("Sotho, Southern"                                                                 ), /** Spanish; Castilian                                                               */
   SPA("Spanish; Castilian"                                                              ), /** Albanian                                                                         */
   SQI("Albanian"                                                                        ), /** Sardinian                                                                        */
   SRD("Sardinian"                                                                       ), /** Sranan Tongo                                                                     */
   SRN("Sranan Tongo"                                                                    ), /** Serbian                                                                          */
   SRP("Serbian"                                                                         ), /** Serer                                                                            */
   SRR("Serer"                                                                           ), /** Nilo-Saharan languages                                                           */
   SSA("Nilo-Saharan languages"                                                          ), /** Swati                                                                            */
   SSW("Swati"                                                                           ), /** Sukuma                                                                           */
   SUK("Sukuma"                                                                          ), /** Sundanese                                                                        */
   SUN("Sundanese"                                                                       ), /** Susu                                                                             */
   SUS("Susu"                                                                            ), /** Sumerian                                                                         */
   SUX("Sumerian"                                                                        ), /** Swahili                                                                          */
   SWA("Swahili"                                                                         ), /** Swedish                                                                          */
   SWE("Swedish"                                                                         ), /** Classical Syriac                                                                 */
   SYC("Classical Syriac"                                                                ), /** Syriac                                                                           */
   SYR("Syriac"                                                                          ), /** Tahitian                                                                         */
   TAH("Tahitian"                                                                        ), /** Tai languages                                                                    */
   TAI("Tai languages"                                                                   ), /** Tamil                                                                            */
   TAM("Tamil"                                                                           ), /** Tatar                                                                            */
   TAT("Tatar"                                                                           ), /** Telugu                                                                           */
   TEL("Telugu"                                                                          ), /** Timne                                                                            */
   TEM("Timne"                                                                           ), /** Tereno                                                                           */
   TER("Tereno"                                                                          ), /** Tetum                                                                            */
   TET("Tetum"                                                                           ), /** Tajik                                                                            */
   TGK("Tajik"                                                                           ), /** Tagalog                                                                          */
   TGL("Tagalog"                                                                         ), /** Thai                                                                             */
   THA("Thai"                                                                            ), /** Tibetan                                                                          */
   TIB("Tibetan"                                                                         ), /** Tigre                                                                            */
   TIG("Tigre"                                                                           ), /** Tigrinya                                                                         */
   TIR("Tigrinya"                                                                        ), /** Tiv                                                                              */
   TIV("Tiv"                                                                             ), /** Tokelau                                                                          */
   TKL("Tokelau"                                                                         ), /** Klingon; tlhIngan-Hol                                                            */
   TLH("Klingon; tlhIngan-Hol"                                                           ), /** Tlingit                                                                          */
   TLI("Tlingit"                                                                         ), /** Tamashek                                                                         */
   TMH("Tamashek"                                                                        ), /** Tonga (Nyasa)                                                                    */
   TOG("Tonga (Nyasa)"                                                                   ), /** Tonga (Tonga Islands)                                                            */
   TON("Tonga (Tonga Islands)"                                                           ), /** Tok Pisin                                                                        */
   TPI("Tok Pisin"                                                                       ), /** Tsimshian                                                                        */
   TSI("Tsimshian"                                                                       ), /** Tswana                                                                           */
   TSN("Tswana"                                                                          ), /** Tsonga                                                                           */
   TSO("Tsonga"                                                                          ), /** Turkmen                                                                          */
   TUK("Turkmen"                                                                         ), /** Tumbuka                                                                          */
   TUM("Tumbuka"                                                                         ), /** Tupi languages                                                                   */
   TUP("Tupi languages"                                                                  ), /** Turkish                                                                          */
   TUR("Turkish"                                                                         ), /** Altaic languages                                                                 */
   TUT("Altaic languages"                                                                ), /** Tuvalu                                                                           */
   TVL("Tuvalu"                                                                          ), /** Twi                                                                              */
   TWI("Twi"                                                                             ), /** Tuvinian                                                                         */
   TYV("Tuvinian"                                                                        ), /** Udmurt                                                                           */
   UDM("Udmurt"                                                                          ), /** Ugaritic                                                                         */
   UGA("Ugaritic"                                                                        ), /** Uighur; Uyghur                                                                   */
   UIG("Uighur; Uyghur"                                                                  ), /** Ukrainian                                                                        */
   UKR("Ukrainian"                                                                       ), /** Umbundu                                                                          */
   UMB("Umbundu"                                                                         ), /** Undetermined                                                                     */
   UND("Undetermined"                                                                    ), /** Urdu                                                                             */
   URD("Urdu"                                                                            ), /** Uzbek                                                                            */
   UZB("Uzbek"                                                                           ), /** Vai                                                                              */
   VAI("Vai"                                                                             ), /** Venda                                                                            */
   VEN("Venda"                                                                           ), /** Vietnamese                                                                       */
   VIE("Vietnamese"                                                                      ), /** Volap�k                                                                          */
   VOL("Volap�k"                                                                         ), /** Votic                                                                            */
   VOT("Votic"                                                                           ), /** Wakashan languages                                                               */
   WAK("Wakashan languages"                                                              ), /** Wolaitta; Wolaytta                                                               */
   WAL("Wolaitta; Wolaytta"                                                              ), /** Waray                                                                            */
   WAR("Waray"                                                                           ), /** Washo                                                                            */
   WAS("Washo"                                                                           ), /** Welsh                                                                            */
   WEL("Welsh"                                                                           ), /** Sorbian languages                                                                */
   WEN("Sorbian languages"                                                               ), /** Walloon                                                                          */
   WLN("Walloon"                                                                         ), /** Wolof                                                                            */
   WOL("Wolof"                                                                           ), /** Kalmyk; Oirat                                                                    */
   XAL("Kalmyk; Oirat"                                                                   ), /** Xhosa                                                                            */
   XHO("Xhosa"                                                                           ), /** Yao                                                                              */
   YAO("Yao"                                                                             ), /** Yapese                                                                           */
   YAP("Yapese"                                                                          ), /** Yiddish                                                                          */
   YID("Yiddish"                                                                         ), /** Yoruba                                                                           */
   YOR("Yoruba"                                                                          ), /** Yupik languages                                                                  */
   YPK("Yupik languages"                                                                 ), /** Zapotec                                                                          */
   ZAP("Zapotec"                                                                         ), /** Blissymbols; Blissymbolics; Bliss                                                */
   ZBL("Blissymbols; Blissymbolics; Bliss"                                               ), /** Zenaga                                                                           */
   ZEN("Zenaga"                                                                          ), /** Zhuang; Chuang                                                                   */
   ZHA("Zhuang; Chuang"                                                                  ), /** Chinese                                                                          */
   ZHO("Chinese"                                                                         ), /** Zande languages                                                                  */
   ZND("Zande languages"                                                                 ), /** Zulu                                                                             */
   ZUL("Zulu"                                                                            ), /** Zuni                                                                             */
   ZUN("Zuni"                                                                            ), /** No linguistic content; Not applicable                                            */
   ZXX("No linguistic content; Not applicable"                                           ), /** Zaza; Dimili; Dimli; Kirdki; Kirmanjki; Zazaki                                   */
   ZZA("Zaza; Dimili; Dimli; Kirdki; Kirmanjki; Zazaki"                                  );

   // data members
   private String code;
   private String name;

   /**
    * constructor.
    * @param name   the name of the language.
    */
   private Language(String name)
   {
      this.code = super.toString().toLowerCase();
      this.name = name;
   }

   /**
    * gets the <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language code.
    * @return the <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language code.
    */
   public String getCode()
   {
      return code;
   }

   /**
    * gets the three character <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language code as an array of bytes, using 1 byte for each character.
    * @return the three character <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language code as an array of bytes.
    */
   public byte[] getCodeBytes()
   {
      return code.getBytes(Encoding.ISO_8859_1.getCharacterSet());
   }

   /**
    * gets the name of the <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language.
    * @return the name of the language.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts a three character String language code value to its corresponding <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language enum.
    * @return an <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language enum corresponding to the string value.
    * @param code  string value of the language code to be converted to an <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language enum.
    * @throws IllegalArgumentException   if the string language code is not a valid <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language code.
    */
   public static Language getLanguage(String code)
   {
      if (code == null || code.length() != 3)
         throw new IllegalArgumentException("Invalid language " + code + ".");

      for (Language l : Language.values())
         if (code.equalsIgnoreCase(l.getCode()))
            return l;
      throw new IllegalArgumentException("Invalid language " + code + ".");
   }

   /**
    * gets a string representation of the <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language enum.
    * @return a string representation of the <a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">ISO-639-2</a> language enum.
    */
   public String toString()
   {
      return code + " - " + name;
   }
}
