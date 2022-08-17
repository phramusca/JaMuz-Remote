package com.beaglebuddy.mpeg.pojo;

import com.beaglebuddy.mpeg.enums.GainOriginator;
import com.beaglebuddy.mpeg.enums.GainType;
import com.beaglebuddy.util.Utility;




/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> header replay gain inforamtion.
 * The gain replay contains information which can be used to make all the .mp3 files in your collection the same loudness.
 * This " volume normalizing" process can be performed by tools like <a target="_blank" href="http://mp3gain.sourceforge.net/">MP3 Gain</a>.
 * @see com.beaglebuddy.mpeg.LAMEHeader
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html" target="_blank">LAME Header Format</a>
 */
public class ReplayGain
{
   // data members
   private GainType       type;           // type of replay gain
   private GainOriginator originator;     // who set the replay gain
   private boolean        sign;           // true = increase gain
   private int            gain;           // amount of gain to add/remove


   /**
    * constructor.
    * @param type        type of replay gain.
    * @param originator  who set the replay gain.
    * @param sign        true indicates an increase in volume, while false indicates a decrease in volume.
    * @param gain        amount of gain to add/remove.
    */
   public ReplayGain(GainType type, GainOriginator originator, boolean sign, int gain)
   {
      this.type       = type;
      this.originator = originator;
      this.sign       = sign;
      this.gain       = gain;
   }

   /**
    * get the type of replay gain adjustment.
    * @return the type of replay gain adjustment.
    */
   public GainType getType()
   {
      return type;
   }

   /**
    * get the originator of the replay gain adjustment.
    * @return the originator of the replay gain adjustment.
    */
   public GainOriginator getOriginator()
   {
      return originator;
   }

   /**
    * get whether the gain adjustment is an increase (true) or a decrease (false) in volume.
    * @return whether the gain adjustment is an increase (true) or a decrease (false) in volume.
    */
   public boolean isIncrease()
   {
      return sign;
   }

   /**
    * get whether the gain adjustment is an increase (true) or a decrease (false) in volume.
    * @return whether the gain adjustment is an increase (true) or a decrease (false) in volume.
    */
   public boolean isDecrease()
   {
      return sign == false;
   }

   /**
    * get the absolute vlaue of the gain adjustment.
    * @return the absolute vlaue of the gain adjustment.
    */
   public int getGain()
   {
      return gain;
   }

   /**
    * get a string representation of the <a target="_blank" href="http://lame.sourceforge.net/">LAME</a> replay gain.
    * @return a string representation of the Lame replay gain.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("type......: " + type                 +       "\n");
      buffer.append(Utility.pad(35) + "originator: " + originator           +       "\n");
      buffer.append(Utility.pad(35) + "direction.: " + (sign ? "in" : "de") + "crease\n");
      buffer.append(Utility.pad(35) + "gain......: " + gain                             );

      return buffer.toString();
   }
}
