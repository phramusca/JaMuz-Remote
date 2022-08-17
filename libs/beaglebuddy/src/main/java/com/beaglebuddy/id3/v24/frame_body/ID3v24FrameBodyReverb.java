package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v24.FrameType;






/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * A <i>reverb</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#REVERB RVRB} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to adjust echoes of different kinds when
 * the .mp3 file is played.   The <i>reverb</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Reverb Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">left                </td><td class="beaglebuddy">the delay between every bounce (in ms) on the left  channel                                                                              </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">right               </td><td class="beaglebuddy">the delay between every bounce (in ms) on the right channel                                                                              </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">leftBounces         </td><td class="beaglebuddy">the number of bounces that should be made for the reverb on the left  channel. (0x00 == 0 bounces, 0xFF == an infinite number of bounces)</td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">rightBounces        </td><td class="beaglebuddy">the number of bounces that should be made for the reverb on the right channel. (0x00 == 0 bounces, 0xFF == an infinite number of bounces)</td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">feedbackLeftToLeft  </td><td class="beaglebuddy">the volume that should be returned to the next echo bounce. 0x00 is 0%, 0xFF is 100%.                                                    </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">feedbackLeftToRight </td><td class="beaglebuddy">if this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.                   </td></tr>
 *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">feedbackRightToRight</td><td class="beaglebuddy">left to right means the first bounce is heard on the left channel, the second bounce on the right channel, and so on.                    </td></tr>
 *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">feedbackRightToLeft </td><td class="beaglebuddy">right to left means the first bounce is heard on the right channel, the second bounce on the left channel, and so on.                    </td></tr>
 *       <tr><td class="beaglebuddy">9. </td><td class="beaglebuddy">premixLeftToRight   </td><td class="beaglebuddy">the amount of left sound to be mixed in the right before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.                       </td></tr>
 *       <tr><td class="beaglebuddy">10.</td><td class="beaglebuddy">premixRightToLeft   </td><td class="beaglebuddy">setting both premix to 0xFF would result in a mono output (if the reverb is applied symmetric).                                          </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>reverb</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
*/
public class ID3v24FrameBodyReverb extends ID3v24FrameBody
{
   // data members
   private int  left;                     // the delay between every bounce (in ms) on the left  channel
   private int  right;                    // the delay between every bounce (in ms) on the right channel
   private byte leftBounces;              // the number of bounces that should be made for the reverb on the left  channel. (0x00 == 0, 0xFF == an infinite number of bounces)
   private byte rightBounces;             // the number of bounces that should be made for the reverb on the right channel. (0x00 == 0, 0xFF == an infinite number of bounces)
   private byte feedbackLeftToLeft;       // the volume that should be returned to the next echo bounce. 0x00 is 0%, $FF is 100%.
   private byte feedbackLeftToRight;      // if this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
   private byte feedbackRightToRight;     // left to right means the first bounce is heard on the left channel, the second bounce on the right channel, and so on.
   private byte feedbackRightToLeft;      //
   private byte premixLeftToRight;        // the amount of left sound to be mixed in the right before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
   private byte premixRightToLeft;        // setting both premix to $FF would result in a mono output (if the reverb is applied symmetric).




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>0 left                   </li>
    *    <li>0 right                  </li>
    *    <li>0 left bounces           </li>
    *    <li>0 right bounces          </li>
    *    <li>0 feedback left to left  </li>
    *    <li>0 feedback left to right </li>
    *    <li>0 feedback right to right</li>
    *    <li>0 feedback right to left </li>
    *    <li>0 premix left to right   </li>
    *    <li>0 premix right to left   </li>
    * </ul>
    */
   public ID3v24FrameBodyReverb()
   {
      this(0, 0, (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param left                     the delay between every bounce (in ms) on the left  channel
    * @param right                    the delay between every bounce (in ms) on the right channel
    * @param leftBounces              the number of bounces that should be made for the reverb on the left  channel. (0x00 == 0, 0xFF == an infinite number of bounces)
    * @param rightBounces             the number of bounces that should be made for the reverb on the right channel. (0x00 == 0, 0xFF == an infinite number of bounces)
    * @param feedbackLeftToLeft       the volume that should be returned to the next echo bounce. 0x00 is 0%, $FF is 100%.
    * @param feedbackLeftToRight      if this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * @param feedbackRightToRight     left  to right means the first bounce is heard on the left  channel, the second bounce on the right channel, and so on.
    * @param feedbackRightToLeft      right to left  means the first bounce is heard on the right channel, the second bounce on the left  channel, and so on.
    * @param premixLeftToRight        the amount of left sound to be mixed in the right before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    * @param premixRightToLeft        setting both premix to $FF would result in a mono output (if the reverb is applied symmetric).
    */
   public ID3v24FrameBodyReverb(int left, int right, byte leftBounces, byte rightBounces, byte feedbackLeftToLeft, byte feedbackLeftToRight, byte feedbackRightToRight, byte feedbackRightToLeft,
                                byte premixLeftToRight, byte premixRightToLeft)
   {
      super(FrameType.REVERB);

      setLeft                (left);
      setRight               (right);
      setLeftBounces         (leftBounces);
      setRightBounces        (rightBounces);
      setFeedbackLeftToLeft  (feedbackLeftToLeft);
      setFeedbackLeftToRight (feedbackLeftToRight);
      setFeedbackRightToRight(feedbackRightToRight);
      setFeedbackRightToLeft (feedbackRightToLeft);
      setPremixLeftToRight   (premixLeftToRight);
      setPremixRightToLeft   (premixRightToLeft);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a reverb frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyReverb(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.REVERB, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      if (buffer.length != 12)
         throw new IllegalArgumentException("Invalid " + frameType + " frame body.  It must be 12 bytes in length, not " + buffer.length + ".");

      setLeft                (((buffer[0] & 0xFF) << 8) + (buffer[1] & 0xFF));
      setRight               (((buffer[2] & 0xFF) << 8) + (buffer[3] & 0xFF));
      setLeftBounces         (buffer[4]);
      setRightBounces        (buffer[5]);
      setFeedbackLeftToLeft  (buffer[6]);
      setFeedbackLeftToRight (buffer[7]);
      setFeedbackRightToRight(buffer[8]);
      setFeedbackRightToLeft (buffer[9]);
      setPremixLeftToRight   (buffer[10]);
      setPremixRightToLeft   (buffer[11]);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the amount of reverb in the left channel.
    * @return the amount of reverb in the left channel.
    * @see #setLeft(int)
    * @see #getRight()
    */
   public int getLeft()
   {
      return left;
   }

   /**
    * sets the delay between every bounce (in ms) on the left channel.
    * @param left   the amount of reverb in the left channel.
    * @see #getLeft()
    * @see #setRight(int)
    */
   public void setLeft(int left)
   {
      if (left < 0)
         throw new IllegalArgumentException("The left field in the " + frameType.getId() + " frame contains an invalid value, " + left + ".  It must >= 0.");

      this.left  = left;
      this.dirty = true;
   }

   /**
    * gets the amount of reverb in the right channel.
    * @return the amount of reverb in the right channel.
    * @see #setRight(int)
    * @see #getLeft()
    */
   public int getRight()
   {
      return right;
   }

   /**
    * sets the delay between every bounce (in ms) on the right channel.
    * @param right the amount of reverb in the right channel.
    * @see #getRight()
    * @see #setLeft(int)
    */
   public void setRight(int right)
   {
      if (right < 0)
         throw new IllegalArgumentException("The right field in the " + frameType.getId() + " frame contains an invalid value, " + right + ".  It must >= 0.");

      this.right = right;
      this.dirty = true;
   }

   /**
    * gets the number of bounces that should be made for the reverb on the left channel.
    * @return the number of bounces that should be made for the reverb on the left channel.
    * @see #setLeftBounces(byte)
    * @see #getRightBounces()
    */
   public byte getLeftBounces()
   {
      return leftBounces;
   }

   /**
    * sets the number of bounces that should be made for the reverb on the left  channel.
    * @param leftBounces the number of bounces where 0x00 == 0 bounces and 0xFF == an infinite number of bounces.
    * @see #getLeftBounces()
    * @see #setRightBounces(byte)
    */
   public void setLeftBounces(byte leftBounces)
   {
      if (leftBounces < 0)
         throw new IllegalArgumentException("The left bounces field in the " + frameType.getId() + " frame contains an invalid value, " + leftBounces + ".  It must >= 0.");

      this.leftBounces = leftBounces;
      this.dirty       = true;
   }

   /**
    * gets the number of bounces that should be made for the reverb on the right channel.
    * @return the number of bounces that should be made for the reverb on the right channel.
    * @see #setRightBounces(byte)
    * @see #getLeftBounces()
    */
   public byte getRightBounces()
   {
      return rightBounces;
   }

   /**
    * sets the number of bounces that should be made for the reverb on the right channel.
    * @param rightBounces the number of bounces where 0x00 == 0 bounces and 0xFF == an infinite number of bounces.
    * @see #getRightBounces()
    * @see #setLeftBounces(byte)
    */
   public void setRightBounces(byte rightBounces)
   {
      if (rightBounces < 0)
         throw new IllegalArgumentException("The right bounces field in the " + frameType.getId() + " frame contains an invalid value, " + rightBounces + ".  It must >= 0.");

      this.rightBounces = rightBounces;
      this.dirty        = true;
   }

   /**
    * gets the left to left feedback, which is the volume that should be returned to the next echo bounce on the left channel. 0x00 is 0%, 0x7F is 50%, and 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * @return the left to left feedback.
    * @see #setFeedbackLeftToLeft(byte)
    */
   public byte getFeedbackLeftToLeft()
   {
     return feedbackLeftToLeft;
   }

   /**
    * sets the left to left feedback, which is the volume that should be returned to the next echo bounce on the left channel. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * @param feedbackLeftToLeft the volume that should be returned to the next echo bounce on the left channel.
    * @see #getFeedbackLeftToRight()
    */
   public void setFeedbackLeftToLeft(byte feedbackLeftToLeft)
   {
      if (feedbackLeftToLeft < 0)
         throw new IllegalArgumentException("The feedback left to left field in the " + frameType.getId() + " frame contains an invalid value, " + feedbackLeftToLeft + ".  It must >= 0.");

      this.feedbackLeftToLeft = feedbackLeftToLeft;
      this.dirty              = true;
   }

   /**
    * gets the left to right feedback, which is the volume that should be returned to the next echo bounce. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * left to right means the first bounce is heard on the left  channel, the second bounce on the right channel, and so on.
    * @return the left to right feedback.
    * @see #setFeedbackLeftToRight(byte)
    */
   public byte getFeedbackLeftToRight()
   {
      return feedbackLeftToRight;
   }

   /**
    * sets the left to right feedback, which is the volume that should be returned to the next echo bounce. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * left to right means the first bounce is heard on the left  channel, the second bounce on the right channel, and so on.
    * @param feedbackLeftToRight the volume that should be returned to the next echo bounce. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    * @see #getFeedbackLeftToRight()
    */
   public void setFeedbackLeftToRight(byte feedbackLeftToRight)
   {
      if (feedbackLeftToRight < 0)
         throw new IllegalArgumentException("The feedback left to right field in the " + frameType.getId() + " frame contains an invalid value, " + feedbackLeftToRight + ".  It must >= 0.");

      this.feedbackLeftToRight = feedbackLeftToRight;
      this.dirty               = true;
   }

   /**
    * sets the right to right feedback, which is the volume that should be returned to the next echo bounce on the right channel. 0x00 is 0%, 0x7F is 50%, and 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * @return the right to right feedback.
    * @see #setFeedbackRightToRight(byte)
    */
   public byte getFeedbackRightToRight()
   {
      return feedbackRightToRight;
   }

   /**
    * sets the right to right feedback, which is the volume that should be returned to the next echo bounce on the right channel. 0x00 is 0%, 0xFF is 100%.
    * @param feedbackRightToRight the volume that should be returned to the next echo bounce on the right channel. 0x00 is 0%, $FF is 100%.
    * @see #getFeedbackRightToRight()
    */
   public void setFeedbackRightToRight(byte feedbackRightToRight)
   {
      if (feedbackRightToRight < 0)
         throw new IllegalArgumentException("The feedback right to right field in the " + frameType.getId() + " frame contains an invalid value, " + feedbackRightToRight + ".  It must >= 0.");

      this.feedbackRightToRight = feedbackRightToRight;
      this.dirty                = true;
   }

   /**
    * gets the right to left feedback, which is the volume that should be returned to the next echo bounce. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * right to left means the first bounce is heard on the right channel, the second bounce on the left channel, and so on.
    * @return the right to left feedback.
    */
   public byte getFeedbackRightToLeft()
   {
      return feedbackRightToLeft;
   }

   /**
    * sets the right to left feedback, which is the volume that should be returned to the next echo bounce. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    * If this value were 0x7F, there would be 50% volume reduction on the first bounce, 50% of that on the second and so on.
    * right to left means the first bounce is heard on the right channel, the second bounce on the left channel, and so on.
    * @param feedbackRightToLeft the volume that should be returned to the next echo bounce. 0x00 is 0%, 0x7F is 50%, 0xFF is 100%.
    */
   public void setFeedbackRightToLeft(byte feedbackRightToLeft)
   {
      if (feedbackRightToLeft < 0)
         throw new IllegalArgumentException("The feedback right to left field in the " + frameType.getId() + " frame contains an invalid value, " + feedbackRightToLeft + ".  It must >= 0.");

      this.feedbackRightToLeft = feedbackRightToLeft;
      this.dirty               = true;
   }

   /**
    * gets the amount of left sound to be mixed in the right before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    * setting both premix to 0xFF would result in a mono output (if the reverb is applied symmetric).
    * @return the amount of left sound to be mixed in the right before any reverb is applied.
    */
   public byte getPremixLeftToRight()
   {
      return premixLeftToRight;
   }

   /**
    * sets the amount of left sound to be mixed in the right before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    * setting both premix to 0xFF would result in a mono output (if the reverb is applied symmetric).
    * @param premixLeftToRight the amount of left sound to be mixed in the right before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    */
   public void setPremixLeftToRight(byte premixLeftToRight)
   {
      if (premixLeftToRight < 0)
         throw new IllegalArgumentException("The premix left to right field in the " + frameType.getId() + " frame contains an invalid value, " + premixLeftToRight + ".  It must >= 0.");

      this.premixLeftToRight = premixLeftToRight;
      this.dirty             = true;
   }

   /**
    * gets the amount of right sound to be mixed in the left before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    * setting both premix to 0xFF would result in a mono output (if the reverb is applied symmetric).
    * @return the amount of right sound to be mixed in the left before any reverb is applied.
    */
   public byte getPremixRightToLeft()
   {
      return premixRightToLeft;
   }

   /**
    * gets the amount of right sound to be mixed in the left before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    * setting both premix to 0xFF would result in a mono output (if the reverb is applied symmetric).
    * @param premixRightToLeft the amount of right sound to be mixed in the left before any reverb is applied, where 0x00 id 0% and 0xFF is 100%.
    */
   public void setPremixRightToLeft(byte premixRightToLeft)
   {
      if (premixRightToLeft < 0)
         throw new IllegalArgumentException("The premix right to left field in the " + frameType.getId() + " frame contains an invalid value, " + premixRightToLeft + ".  It must >= 0.");

      this.premixRightToLeft = premixRightToLeft;
      this.dirty             = true;
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      if (isDirty())
      {
         System.arraycopy(shortToBytes(left ), 0, buffer, 0, 2);
         System.arraycopy(shortToBytes(right), 0, buffer, 2, 2);
         buffer[4]  = leftBounces;
         buffer[5]  = rightBounces;
         buffer[6]  = feedbackLeftToLeft;
         buffer[7]  = feedbackLeftToRight;
         buffer[8]  = feedbackRightToRight;
         buffer[9]  = feedbackRightToLeft;
         buffer[10] = premixLeftToRight;
         buffer[11] = premixRightToLeft;
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>reverb</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: reverb\n");
      buffer.append("   left...................: " + left                 + "\n");
      buffer.append("   right..................: " + right                + "\n");
      buffer.append("   left  bounces..........: " + leftBounces          + "\n");
      buffer.append("   right bounces..........: " + rightBounces         + "\n");
      buffer.append("   feedback left  to left.: " + feedbackLeftToLeft   + "\n");
      buffer.append("   feedback left  to right: " + feedbackLeftToRight  + "\n");
      buffer.append("   feedback right to right: " + feedbackRightToRight + "\n");
      buffer.append("   feedback right to left.: " + feedbackRightToLeft  + "\n");
      buffer.append("   premix   left  to right: " + premixLeftToRight    + "\n");
      buffer.append("   premix   right to left.: " + premixRightToLeft    + "\n");

      return buffer.toString();
   }
}
