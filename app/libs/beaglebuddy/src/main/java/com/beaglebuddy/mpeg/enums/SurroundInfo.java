package com.beaglebuddy.mpeg.enums;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * The list of surround information types supported by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public enum SurroundInfo
{                                       /** none                */
   NONE          ("none"          ),    /** DPL encoding        */
   DPL_ENCODING  ("DPL encoding"  ),    /** DPL2 encoding       */
   DPL2_ENCODING ("DPL2 encoding" ),    /** ambisonic encoding  */
   AMBISONIC     ("Ambisonic"     ),    /** unknown             */
   UNKNOWN_1     ("unknown"       ),    /** unknown             */
   UNKNOWN_2     ("unknown"       ),    /** unknown             */
   UNKNOWN_3     ("unknown"       ),    /** unknown             */
   UNKNOWN_4     ("unknown"       ),    /** unknown             */
   RESERVED      ("reserved"      );

   // data members
   private String name;

   /**
    * constructor.
    * @param name    name of the surround info.
    */
   private SurroundInfo(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the surround info.
    * @return the name of the surround info.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding LAME surround info enum.
    * @return the LAME surround info enum corresponding to the integral value.
    * @param info  integral value to be converted to a LAME surround info enum.
    * @throws IllegalArgumentException   if the value is not a valid LAME surround info type.
    */
   public static SurroundInfo valueOf(int info) throws IllegalArgumentException
   {
      for (SurroundInfo i : SurroundInfo.values())
         if (info == i.ordinal())
            return i;
      throw new IllegalArgumentException("Invalid surround info " + info + ".  It must be 0 <= surround info <= " + RESERVED.ordinal() + ".");
   }

   /**
    * gets a string representation of the surround info enum.
    * @return a string representation of the surround info enum.
    */
   public String toString()
   {
      return name;
   }
}
