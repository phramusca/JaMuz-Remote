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
 * The list of MPEG layers supported by the <a target="_blank" href="http://mpeg.chiariglione.org/standards/mpeg-1/audio">MPEG audio standard</a>.
 * @see com.beaglebuddy.mpeg.MPEGFrameHeader
 * @see <a href="http://en.wikipedia.org/wiki/Moving_Picture_Experts_Group" target="_blank">Moving Picture Experts Group</a>
 */
public enum Layer
{                            /** reserved for future use */
   RESERVED("Reserved" ),    /** layer III               */
   III     ("Layer III"),    /** layer II                */
   II      ("Layer II" ),    /** layer I                 */
   I       ("Layer I"  );

   // data members
   private String name;

   /**
    * constructor.
    * @param name  name of the audio format layer version.
    */
   private Layer(String name)
   {
      this.name = name;
   }

   /**
    * gets the name of the MPEG layer.
    * @return the name of the MPEG layer.
    */
   public String getName()
   {
      return name;
   }

   /**
    * converts an integral value to its corresponding MPEG layer enum.
    * @return the MPEG layer enum corresponding to the integral value.
    * @param layer  integral value to be converted to an MPEG layer enum.
    * @throws IllegalArgumentException   if the value is not a valid MPEG layer.
    */
   public static Layer valueOf(int layer) throws IllegalArgumentException
   {
      for (Layer l : Layer.values())
         if (layer == l.ordinal())
            return l;
      throw new IllegalArgumentException("Invalid MPEG layer " + layer + ".  It must be 0 <= layer <= 3.");
   }

   /**
    * gets a string representation of the MPEG layer enum.
    * @return a string representation of the MPEG layer enum.
    */
   public String toString()
   {
      return name;
   }
}
