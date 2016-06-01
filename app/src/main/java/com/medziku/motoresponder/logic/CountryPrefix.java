package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;

import java.util.HashMap;


public class CountryPrefix {

    private ContactsUtility contactsUtility;
    private HashMap<Integer, String> mccToPrefixMap;

    public CountryPrefix(ContactsUtility contactsUtility) {
        this.initializeMap();
        this.contactsUtility = contactsUtility;
    }

    /**
     * Returns country prefix if possible, null if by some reason it's not possible.
     *
     * @return
     */
    public String getCountryPrefix() {
        Integer mcc = null;

        try {
            mcc = this.contactsUtility.readCurrentMobileCountryCode();
        } catch (Exception e) {
        }

        if (mcc != null) {
            String prefix = this.mccToPrefixMap.get(mcc);

            if (prefix != null) {
                return prefix;
            }
        }

        return null;
    }

    private void initializeMap() {
        HashMap<Integer, String> m = new HashMap<Integer, String>();

        /**
         * Copyrights:
         *
         * It's this list: https://github.com/musalbas/mcc-mnc-table
         * after some transformations.
         */

        m.put(202, "30"); // Greece
        m.put(204, "31"); // Netherlands
        m.put(206, "32"); // Belgium
        m.put(208, "33"); // France
        m.put(212, "377"); // Monaco
        m.put(213, "376"); // Andorra
        m.put(214, "34"); // Spain
        m.put(216, "36"); // Hungary
        m.put(218, "387"); // Bosnia & Herzegov.
        m.put(219, "385"); // Croatia
        m.put(220, "381"); // Serbia
        m.put(222, "39"); // Italy
        m.put(226, "40"); // Romania
        m.put(228, "41"); // Switzerland
        m.put(230, "420"); // Czech Rep.
        m.put(231, "421"); // Slovakia
        m.put(232, "43"); // Austria
        m.put(234, "44"); // United Kingdom
        m.put(235, "44"); // United Kingdom
        m.put(238, "45"); // Denmark
        m.put(240, "46"); // Sweden
        m.put(242, "47"); // Norway
        m.put(244, "358"); // Finland
        m.put(246, "370"); // Lithuania
        m.put(247, "371"); // Latvia
        m.put(248, "372"); // Estonia
        m.put(250, "79"); // Russian Federation
        m.put(255, "380"); // Ukraine
        m.put(257, "375"); // Belarus
        m.put(259, "373"); // Moldova
        m.put(260, "48"); // Poland
        m.put(262, "49"); // Germany
        m.put(266, "350"); // Gibraltar
        m.put(268, "351"); // Portugal
        m.put(270, "352"); // Luxembourg
        m.put(272, "353"); // Ireland
        m.put(274, "354"); // Iceland
        m.put(276, "355"); // Albania
        m.put(278, "356"); // Malta
        m.put(280, "357"); // Cyprus
        m.put(282, "995"); // Georgia
        m.put(283, "374"); // Armenia
        m.put(284, "359"); // Bulgaria
        m.put(286, "90"); // Turkey
        m.put(288, "298"); // Faroe Islands
        m.put(289, "7"); // Abkhazia
        m.put(290, "299"); // Greenland
        m.put(292, "378"); // San Marino
        m.put(293, "386"); // Slovenia
        m.put(294, "389"); // Macedonia
        m.put(295, "423"); // Liechtenstein
        m.put(297, "382"); // Montenegro
        m.put(302, "1"); // Canada
        m.put(308, "508"); // St. Pierre & Miquelon
        m.put(310, "1"); // United States
        m.put(311, "1"); // United States
        m.put(312, "1"); // United States
        m.put(316, "1"); // United States
        m.put(334, "52"); // Mexico
        m.put(338, "1876"); // Jamaica
        m.put(340, "596"); // Martinique (French Department of)
        m.put(342, "1246"); // Barbados
        m.put(344, "1268"); // Antigua and Barbuda
        m.put(346, "1345"); // Cayman Islands
        m.put(348, "284"); // British Virgin Islands
        m.put(350, "1441"); // Bermuda
        m.put(352, "1473"); // Grenada
        m.put(354, "1664"); // Montserrat
        m.put(356, "1869"); // Saint Kitts and Nevis
        m.put(358, "1758"); // Saint Lucia
        m.put(360, "1784"); // St. Vincent & Gren.
        m.put(362, "599"); // Netherlands Antilles
        m.put(363, "297"); // Aruba
        m.put(364, "1242"); // Bahamas
        m.put(365, "1264"); // Anguilla
        m.put(366, "1767"); // Dominica
        m.put(368, "53"); // Cuba
        m.put(370, "1809"); // Dominican Republic
        m.put(372, "509"); // Haiti
        m.put(374, "1868"); // Trinidad and Tobago
        m.put(376, "1340"); // Virgin Islands U.S.
        m.put(400, "994"); // Azerbaijan
        m.put(401, "7"); // Kazakhstan
        m.put(402, "975"); // Bhutan
        m.put(404, "91"); // India
        m.put(405, "91"); // India
        m.put(410, "92"); // Pakistan
        m.put(412, "93"); // Afghanistan
        m.put(413, "94"); // Sri Lanka
        m.put(414, "95"); // Myanmar (Burma)
        m.put(415, "961"); // Lebanon
        m.put(416, "962"); // Jordan
        m.put(417, "963"); // Syrian Arab Republic
        m.put(418, "964"); // Iraq
        m.put(419, "965"); // Kuwait
        m.put(420, "966"); // Saudi Arabia
        m.put(421, "967"); // Yemen
        m.put(422, "968"); // Oman
        m.put(424, "971"); // United Arab Emirates
        m.put(425, "970"); // Palestinian Territory
        m.put(426, "973"); // Bahrain
        m.put(427, "974"); // Qatar
        m.put(428, "976"); // Mongolia
        m.put(429, "977"); // Nepal
        m.put(430, "971"); // United Arab Emirates
        m.put(431, "971"); // United Arab Emirates
        m.put(432, "98"); // Iran
        m.put(434, "998"); // Uzbekistan
        m.put(436, "992"); // Tajikistan
        m.put(437, "996"); // Kyrgyzstan
        m.put(438, "993"); // Turkmenistan
        m.put(440, "81"); // Japan
        m.put(441, "81"); // Japan
        m.put(450, "82"); // Korea S Republic of
        m.put(452, "84"); // Viet Nam
        m.put(454, "852"); // Hongkong China
        m.put(455, "853"); // Macao China
        m.put(456, "855"); // Cambodia
        m.put(457, "856"); // Laos P.D.R.
        m.put(460, "86"); // China
        m.put(466, "886"); // Taiwan
        m.put(470, "880"); // Bangladesh
        m.put(472, "960"); // Maldives
        m.put(502, "60"); // Malaysia
        m.put(505, "61"); // Australia
        m.put(510, "62"); // Indonesia
        m.put(515, "63"); // Philippines
        m.put(520, "66"); // Thailand
        m.put(525, "65"); // Singapore
        m.put(528, "673"); // Brunei Darussalam
        m.put(530, "64"); // New Zealand
        m.put(537, "675"); // Papua New Guinea
        m.put(539, "676"); // Tonga
        m.put(540, "677"); // Solomon Islands
        m.put(541, "678"); // Vanuatu
        m.put(542, "679"); // Fiji
        m.put(544, "684"); // American Samoa
        m.put(545, "686"); // Kiribati
        m.put(546, "687"); // New Caledonia
        m.put(547, "689"); // French Polynesia
        m.put(548, "682"); // Cook Islands
        m.put(549, "685"); // Samoa
        m.put(550, "691"); // Micronesia
        m.put(552, "680"); // Palau (Republic of)
        m.put(555, "683"); // Niue
        m.put(602, "20"); // Egypt
        m.put(603, "213"); // Algeria
        m.put(604, "212"); // Morocco
        m.put(605, "216"); // Tunisia
        m.put(606, "218"); // Libya
        m.put(607, "220"); // Gambia
        m.put(608, "221"); // Senegal
        m.put(609, "222"); // Mauritania
        m.put(610, "223"); // Mali
        m.put(611, "224"); // Guinea
        m.put(612, "225"); // Ivory Coast
        m.put(613, "226"); // Burkina Faso
        m.put(614, "227"); // Niger
        m.put(615, "228"); // Togo
        m.put(616, "229"); // Benin
        m.put(617, "230"); // Mauritius
        m.put(618, "231"); // Liberia
        m.put(619, "232"); // Sierra Leone
        m.put(620, "233"); // Ghana
        m.put(621, "234"); // Nigeria
        m.put(622, "235"); // Chad
        m.put(623, "236"); // Central African Rep.
        m.put(624, "237"); // Cameroon
        m.put(625, "238"); // Cape Verde
        m.put(626, "239"); // Sao Tome & Principe
        m.put(627, "240"); // Equatorial Guinea
        m.put(628, "241"); // Gabon
        m.put(629, "242"); // Congo Republic
        m.put(630, "243"); // Congo Dem. Rep.
        m.put(631, "244"); // Angola
        m.put(633, "248"); // Seychelles
        m.put(634, "249"); // Sudan
        m.put(635, "250"); // Rwanda
        m.put(636, "251"); // Ethiopia
        m.put(637, "252"); // Somalia
        m.put(638, "253"); // Djibouti
        m.put(639, "254"); // Kenya
        m.put(640, "255"); // Tanzania
        m.put(641, "256"); // Uganda
        m.put(642, "257"); // Burundi
        m.put(643, "258"); // Mozambique
        m.put(645, "260"); // Zambia
        m.put(646, "261"); // Madagascar
        m.put(647, "262"); // Reunion
        m.put(648, "263"); // Zimbabwe
        m.put(649, "264"); // Namibia
        m.put(650, "265"); // Malawi
        m.put(651, "266"); // Lesotho
        m.put(652, "267"); // Botswana
        m.put(653, "268"); // Swaziland
        m.put(654, "269"); // Comoros
        m.put(655, "27"); // South Africa
        m.put(657, "291"); // Eritrea
        m.put(702, "501"); // Belize
        m.put(704, "502"); // Guatemala
        m.put(706, "503"); // El Salvador
        m.put(708, "504"); // Honduras
        m.put(710, "505"); // Nicaragua
        m.put(712, "506"); // Costa Rica
        m.put(714, "507"); // Panama
        m.put(716, "51"); // Peru
        m.put(722, "54"); // Argentina Republic
        m.put(724, "55"); // Brazil
        m.put(730, "56"); // Chile
        m.put(732, "57"); // Colombia
        m.put(734, "58"); // Venezuela
        m.put(736, "591"); // Bolivia
        m.put(738, "592"); // Guyana
        m.put(740, "593"); // Ecuador
        m.put(744, "595"); // Paraguay
        m.put(746, "597"); // Suriname
        m.put(748, "598"); // Uruguay
        m.put(750, "500"); // Falkland Islands (Malvinas)


        this.mccToPrefixMap = m;
    }

}
