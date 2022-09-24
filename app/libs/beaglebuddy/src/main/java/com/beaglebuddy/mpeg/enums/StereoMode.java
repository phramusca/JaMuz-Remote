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
 * The list of stereo modes supported by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public enum StereoMode
{                                       /** mono                */
   MONO          ("mono"          ),    /** stereo              */
   STEREO        ("stereo"        ),    /** dual channel stereo */
   DUAL          ("dual"          ),    /** joint stereo        */
   JOINT         ("joint"         ),    /** force               */
   FORCE         ("force"         ),    /** intensity           */
   INTENSITY     ("intensity"     ),    /** undefined/different */
   UNDEFINED     ("undefined/different");

   // data members
   private String name;

   /**
    * constructor.
    * @param name    name of the stereo mode.
    */
   private StereoMode(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the stereo mode.
    * @return the name of the stereo mode.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding stereo mode enum.
    * @return the stereo mode enum corresponding to the integral value.
    * @param mode  integral value to be converted to an stereo mode enum.
    * @throws IllegalArgumentException   if the value is not a valid stereo mode.
    */
   public static StereoMode valueOf(int mode) throws IllegalArgumentException
   {
      for (StereoMode m : StereoMode.values())
         if (mode == m.ordinal())
            return m;
      throw new IllegalArgumentException("Invalid stereo mode " + mode + ".  It must be 0 <= stereo mode <= " + UNDEFINED.ordinal() + ".");
   }

   /**
    * gets a string representation of the stereo mode enum.
    * @return a string representation of the stereo mode enum.
    */
   public String toString()
   {
      return name;
   }
}
