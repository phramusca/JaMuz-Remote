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
 * The list of gain originators supported by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public enum GainOriginator
{                               /** not set                       */
   NOT_SET    ("not set"    ),  /** set by artist                 */
   ARTIST     ("artist"     ),  /** set by the user               */
   USER       ("user"       ),  /** set by a model                */
   MODEL      ("model"      ),  /** set by the simple RMS average */
   RMS_AVERAGE("RMS average");

   // data members
   private String name;

   /**
    * constructor.
    * @param name  the name of the LAME replay gain originator.
    */
   private GainOriginator(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the LAME replay gain originator.
    * @return the name of the LAME replay gain originator.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding LAME replay gain originator enum.
    * @return the LAME replay gain originator enum corresponding to the integral value.
    * @param originator  integral value to be converted to an LAME replay gain originator enum.
    * @throws IllegalArgumentException   if the value is not a valid LAME replay gain originator.
    */
   public static GainOriginator valueOf(int originator) throws IllegalArgumentException
   {
      for (GainOriginator o : GainOriginator.values())
         if (originator == o.ordinal())
            return o;
      throw new IllegalArgumentException("Invalid LAME replay gain originator, " + originator + ".  It must be 0 <= originator <= " + RMS_AVERAGE.ordinal() + ".");
   }

   /**
    * gets a string representation of the LAME replay gain originator enum.
    * @return a string representation of the LAME replay gain originator enum.
    */
   public String toString()
   {
      return name;
   }
}
