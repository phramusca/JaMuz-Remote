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
 * The list of valid emphasis types supported by the <a target="_blank" href="http://mpeg.chiariglione.org/standards/mpeg-1/audio">MPEG audio standard</a>.
 * @see com.beaglebuddy.mpeg.MPEGFrameHeader
 * @see <a href="http://en.wikipedia.org/wiki/Moving_Picture_Experts_Group" target="_blank">Moving Picture Experts Group</a>
 */
public enum Emphasis
{                             /** None      */
   NONE     ("None"     ),    /** 50/15 ms  */
   _50_15_MS("50/15 ms" ),    /** Reserved  */
   RESERVED ("Reserved" ),    /** CCIT J.17 */
   CCIT_J_17("CCIT J.17");

   // data members
   private String name;

   /**
    * constructor.
    * @param name  name of the emphasis.
    */
   private Emphasis(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the emphasis.
    * @return the name of the emphasis.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding emphasis enum.
    * @return the emphasis enum corresponding to the integral value.
    * @param emphasis  integral value to be converted to an emphasis enum.
    * @throws IllegalArgumentException   if the value is not a valid emphasis.
    */
   public static Emphasis valueOf(int emphasis) throws IllegalArgumentException
   {
      for (Emphasis e : Emphasis.values())
         if (emphasis == e.ordinal())
            return e;
      throw new IllegalArgumentException("Invalid emphasis " + emphasis + ".  It must be 0 <= layer <= 3.");
   }

   /**
    * gets a string representation of the emphasis enum.
    * @return a string representation of the emphasis enum.
    */
   public String toString()
   {
      return name;
   }
}
