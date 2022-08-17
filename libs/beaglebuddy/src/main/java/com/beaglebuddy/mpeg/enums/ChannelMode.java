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
 * The list of valid channel modes supported by the <a target="_blank" href="http://mpeg.chiariglione.org/standards/mpeg-1/audio">MPEG audio standard</a>.
 * @see com.beaglebuddy.mpeg.MPEGFrameHeader
 * @see <a href="http://en.wikipedia.org/wiki/Moving_Picture_Experts_Group" target="_blank">Moving Picture Experts Group</a>
 */
public enum ChannelMode
{                                              /** Stereo              */
   STEREO        ("Stereo"        , true ),    /** Joint Stereo        */
   JOINT_STEREO  ("Joint Stereo"  , true ),    /** Dual Channel Stereo */
   DUAL_CHANNEL  ("Dual Channel"  , true ),    /** Single channel Mono */
   SINGLE_CHANNEL("Single channel", false);

   // data members
   private String  name;
   private boolean stereo;

   /**
    * constructor.
    * @param name    name of the channel mode.
    * @param stereo  whether the channel mode is a stereo mode.
    */
   private ChannelMode(String name, boolean stereo)
   {
      this.name   = name;
      this.stereo = stereo;
   }

   /**
    * gets the name of the channel mode.
    * @return the name of the channel mode.
    */
   public String getName()
   {
      return name;
   }

   /**
    * gets whether the channel mode is stereo or not.
    * @return whether the channel mode is stereo or not.
    */
   public boolean isStereo()
   {
      return stereo;
   }

   /**
    * converts an integral value to its corresponding channel mode enum.
    * @return the channel mode enum corresponding to the integral value.
    * @param mode  integral value to be converted to an channel mode enum.
    * @throws IllegalArgumentException   if the value is not a valid channel mode.
    */
   public static ChannelMode valueOf(int mode) throws IllegalArgumentException
   {
      for (ChannelMode m : ChannelMode.values())
         if (mode == m.ordinal())
            return m;
      throw new IllegalArgumentException("Invalid channel mode " + mode + ".  It must be 0 <= channel mode <= 3.");
   }

   /**
    * gets a string representation of the channel mode enum.
    * @return a string representation of the channel mode enum.
    */
   public String toString()
   {
      return name;
   }
}
