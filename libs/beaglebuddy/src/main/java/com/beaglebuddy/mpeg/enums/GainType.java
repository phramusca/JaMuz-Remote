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
 * The list of gain types supported by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public enum GainType
{                             /** not set                           */
   NOT_SET   ("not set"   ),  /** replay gain set for radio airplay */
   RADIO     ("radio"     ),  /** replay gain set for audiophile    */
   AUDIOPHILE("audiophile");

   // data members
   private String name;

   /**
    * constructor.
    * @param name  the name of the LAME replay gain type.
    */
   private GainType(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the LAME replay gain type.
    * @return the name of the LAME replay gain type.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding LAME replay gain type enum.
    * @return the LAME replay gain type enum corresponding to the integral value.
    * @param type  integral value to be converted to an LAME replay gain type enum.
    * @throws IllegalArgumentException   if the value is not a valid LAME replay gain type.
    */
   public static GainType valueOf(int type) throws IllegalArgumentException
   {
      for (GainType t : GainType.values())
         if (type == t.ordinal())
            return t;
      throw new IllegalArgumentException("Invalid LAME replay gain type, " + type + ".  It must be 0 <= type <= " + AUDIOPHILE.ordinal() + ".");
   }

   /**
    * gets a string representation of the LAME replay gain type enum.
    * @return a string representation of the LAME replay gain type enum.
    */
   public String toString()
   {
      return name;
   }
}
