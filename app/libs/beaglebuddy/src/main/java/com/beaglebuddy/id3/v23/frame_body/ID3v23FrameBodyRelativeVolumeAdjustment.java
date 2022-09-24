package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.FrameType;




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
 * A <i>relative volume adjustment</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#RELATIVE_VOLUME_ADJUSTMENT RVAD} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame}
 * which is used to adjust the volume of .mp3 songs to a consistent level.  Since some songs are recorded at higher levels than others, this frame could allow a user to set a desired volume and the .mp3
 * players would adjust the output volume of the .mp3 song to the level specified by the user.
 * </p>
 * <p class="beaglebuddy">
 * The <i>relative volume adjustment</i> frame allows the user to specify both relative and peak volume settings for following 6 defined channels:
 * <ul>
 *    <li>front right channel</li>
 *    <li>front left  channel</li>
 *    <li>back  right channel</li>
 *    <li>back  left  channel</li>
 *    <li>center      channel</li>
 *    <li>bass        channel</li>
 * </ul>
 * </p>
 * <p class="beaglebuddy">
 * Once again, the monkey's who came up with the specification made this far more complex than it ever needed to be.  Instead of just using 16 bits to specify a volume,
 * they allowed implementers to choose the number of bits.  This forces implementers to do a lot of bit manipulation and makes the code incredibly messy. In ID3v2.4, they
 * fixed this and made the volume adjustments 16 bits.  Additionally, according to the <a href="http://id3.org/id3v2.3.0">ID3 v2.3 specification</a>, <i>the bits used for
 * volume adjustments field is normally 0x10 (16 bits) for MPEG 2 layer I, II and III and MPEG 2.5.</i>.  For these reasons, Beaglebuddy Software decided to only support
 * 16 bit volumes.
 * </p>
 * The <i>relative volume adjustment</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Relative Volume Adjustment Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">relative volume adjustment front right channel</td><td class="beaglebuddy">amount to adjust the volume of  the front right channel.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">relative volume adjustment front left  channel</td><td class="beaglebuddy">amount to adjust the volume of  the front left  channel.</td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">relative volume adjustment back  right channel</td><td class="beaglebuddy">amount to adjust the volume of  the back  right channel.</td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">relative volume adjustment back  left  channel</td><td class="beaglebuddy">amount to adjust the volume of  the back  left  channel.</td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">relative volume adjustment center      channel</td><td class="beaglebuddy">amount to adjust the volume of  the center      channel.</td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">relative volume adjustment bass        channel</td><td class="beaglebuddy">amount to adjust the volume of  the bass        channel.</td></tr>
 *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">peak     volume            front right channel</td><td class="beaglebuddy">the maximum volume allowed  for the front right channel.</td></tr>
 *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">peak     volume            front left  channel</td><td class="beaglebuddy">the maximum volume allowed  for the front left  channel.</td></tr>
 *       <tr><td class="beaglebuddy">9. </td><td class="beaglebuddy">peak     volume            back  right channel</td><td class="beaglebuddy">the maximum volume allowed  for the back  right channel.</td></tr>
 *       <tr><td class="beaglebuddy">10 </td><td class="beaglebuddy">peak     volume            back  left  channel</td><td class="beaglebuddy">the maximum volume allowed  for the back  left  channel.</td></tr>
 *       <tr><td class="beaglebuddy">11.</td><td class="beaglebuddy">peak     volume            center      channel</td><td class="beaglebuddy">the maximum volume allowed  for the center      channel.</td></tr>
 *       <tr><td class="beaglebuddy">12.</td><td class="beaglebuddy">peak     volume            bass        channel</td><td class="beaglebuddy">the maximum volume allowed  for the bass        channel.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>relative volume adjustment</i> frame.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyRelativeVolumeAdjustment extends ID3v23FrameBody
{
    /** valid ID3v2.3 volume directions */
   public enum Direction
   {                         /** decrease the volume on the specified channel */
      DECREASE_VOLUME,       /** increase the volume on the specified channel */
      INCREASE_VOLUME;

      /** @return a string representation of the Direction */
      public String toString()  {return "" + ordinal() + " - " + super.toString().toLowerCase();}

      /**
       * convert an integral value to its corresponding enum.
       * @param direction   the integral value that is to be converted to a Direction enum.
       * @return the Direction enum whose ordinal value corresponds to the given integral value.
       * @throws IllegalArgumentException   if there is no Direction enum whose ordinal value corresponds to the given integral value.
       */
      public static Direction getDirection(int direction)
      {
         for (Direction d : Direction.values())
            if (direction == d.ordinal())
               return d;
         throw new IllegalArgumentException("Invalid volume direction " + direction + ".");
      }
   }

   // class members
   private static int     MAX_VOLUME                         = 65536;          // using 16 bits for volume adjustment leads to a maximum value of 2 ^ 16.
   private static byte    DIRECTION_FRONT_RIGHT_CHANNEL_MASK = (byte)0x01;     // bit mask for the front right channel volume direction
   private static byte    DIRECTION_FRONT_LEFT_CHANNEL_MASK  = (byte)0x02;     // bit mask for the front left  channel volume direction
   private static byte    DIRECTION_BACK_RIGHT_CHANNEL_MASK  = (byte)0x03;     // bit mask for the back  right channel volume direction
   private static byte    DIRECTION_BACK_LEFT_CHANNEL_MASK   = (byte)0x04;     // bit mask for the back  left  channel volume direction
   private static byte    DIRECTION_CENTER_CHANNEL_MASK      = (byte)0x05;     // bit mask for the center      channel volume direction
   private static byte    DIRECTION_BASS_CHANNEL_MASK        = (byte)0x05;     // bit mask for the bass        channel volume direction


   // data members
   private int bitsUsedForVolumeAdjustments;                  // number of bits to use for the volume adjustments.  Beaglebuddy only supports a value of 16 bits.
   private int relativeVolumeAdjustmentFrontRightChannel;     // front  channel settings
   private int relativeVolumeAdjustmentFrontLeftChannel;
   private int peakVolumeFrontRightChannel;
   private int peakVolumeFrontLeftChannel;
   private int relativeVolumeAdjustmentBackRightChannel;      // back   channel settings
   private int relativeVolumeAdjustmentBackLeftChannel;
   private int peakVolumeBackRightChannel;
   private int peakVolumeBackLeftChannel;
   private int relativeVolumeAdjustmentCenterChannel;         // center channel settings
   private int peakVolumeCenterChannel;
   private int relativeVolumeAdjustmentBassChannel;           // bass   channel settings
   private int peakVolumeBassChannel;




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>0 relative volume adjustment front right channel </li>
    *    <li>0 relative volume adjustment front left channel  </li>
    *    <li>0 relative volume adjustment back  right channel </li>
    *    <li>0 relative volume adjustment back  left channel  </li>
    *    <li>0 relative volume adjustment center channel      </li>
    *    <li>0 relative volume adjustment bass   channel      </li>
    *    <li>0 peak volume front right channel                </li>
    *    <li>0 peak volume front left  channel                </li>
    *    <li>0 peak volume back  right channel                </li>
    *    <li>0 peak volume back  left  channel                </li>
    *    <li>0 peak volume center      channel                </li>
    *    <li>0 peak volume bass        channel                </li>
    * </ul>
    */
   public ID3v23FrameBodyRelativeVolumeAdjustment()
   {
      this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
   }

   /**
    * The default constructor is called when creating a new frame.
    * @param relativeVolumeAdjustmentFrontRightChannel   relative volume adjustment front right channel
    * @param relativeVolumeAdjustmentFrontLeftChannel    relative volume adjustment front left channel
    * @param relativeVolumeAdjustmentBackRightChannel    relative volume adjustment back  right channel
    * @param relativeVolumeAdjustmentBackLeftChannel     relative volume adjustment back  left channel
    * @param relativeVolumeAdjustmentCenterChannel       relative volume adjustment center channel
    * @param relativeVolumeAdjustmentBassChannel         relative volume adjustment bass   channel
    * @param peakVolumeFrontRightChannel                 peak volume front right channel
    * @param peakVolumeFrontLeftChannel                  peak volume front left  channel
    * @param peakVolumeBackRightChannel                  peak volume back  right channel
    * @param peakVolumeBackLeftChannel                   peak volume back  left  channel
    * @param peakVolumeCenterChannel                     peak volume center      channel
    * @param peakVolumeBassChannel                       peak volume bass        channel
    */
   public ID3v23FrameBodyRelativeVolumeAdjustment(int relativeVolumeAdjustmentFrontRightChannel, int relativeVolumeAdjustmentFrontLeftChannel,
                                                  int relativeVolumeAdjustmentBackRightChannel , int relativeVolumeAdjustmentBackLeftChannel ,
                                                  int relativeVolumeAdjustmentCenterChannel    , int relativeVolumeAdjustmentBassChannel     ,
                                                  int peakVolumeFrontRightChannel              , int peakVolumeFrontLeftChannel              ,
                                                  int peakVolumeBackRightChannel               , int peakVolumeBackLeftChannel               ,
                                                  int peakVolumeCenterChannel                  , int peakVolumeBassChannel                   )
   {
      super(FrameType.RELATIVE_VOLUME_ADJUSTMENT);

      setRelativeVolumeAdjustmentFrontRightChannel(relativeVolumeAdjustmentFrontRightChannel);
      setRelativeVolumeAdjustmentFrontLeftChannel (relativeVolumeAdjustmentFrontLeftChannel );
      setRelativeVolumeAdjustmentBackRightChannel (relativeVolumeAdjustmentBackRightChannel );
      setRelativeVolumeAdjustmentBackLeftChannel  (relativeVolumeAdjustmentBackLeftChannel  );
      setRelativeVolumeAdjustmentCenterChannel    (relativeVolumeAdjustmentCenterChannel    );
      setRelativeVolumeAdjustmentBassChannel      (relativeVolumeAdjustmentBassChannel      );
      setPeakVolumeFrontRightChannel              (peakVolumeFrontRightChannel              );
      setPeakVolumeFrontLeftChannel               (peakVolumeFrontLeftChannel               );
      setPeakVolumeBackRightChannel               (peakVolumeBackRightChannel               );
      setPeakVolumeBackLeftChannel                (peakVolumeBackLeftChannel                );
      setPeakVolumeCenterChannel                  (peakVolumeCenterChannel                  );
      setPeakVolumeBassChannel                    (peakVolumeBassChannel                    );

      dirty = true;
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a relative volume adjustment frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyRelativeVolumeAdjustment(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.RELATIVE_VOLUME_ADJUSTMENT, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      Direction volumeAdjustmentDirectionFrontRightChannel = (buffer[0] & DIRECTION_FRONT_RIGHT_CHANNEL_MASK) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME;
      Direction volumeAdjustmentDirectionFrontLeftChannel  = (buffer[0] & DIRECTION_FRONT_LEFT_CHANNEL_MASK ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME;
      Direction volumeAdjustmentDirectionBackRightChannel  = (buffer[0] & DIRECTION_BACK_RIGHT_CHANNEL_MASK ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME;
      Direction volumeAdjustmentDirectionBackLeftChannel   = (buffer[0] & DIRECTION_BACK_LEFT_CHANNEL_MASK  ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME;
      Direction volumeAdjustmentDirectionCenterChannel     = (buffer[0] & DIRECTION_CENTER_CHANNEL_MASK     ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME;
      Direction volumeAdjustmentDirectionBassChannel       = (buffer[0] & DIRECTION_BASS_CHANNEL_MASK       ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME;
      setBitsUsedForVolumeAdjustments(buffer[1]);

      setRelativeVolumeAdjustmentFrontRightChannel  ((((buffer[ 2] & 0xFF) << 8) + (buffer[ 3] & 0xFF)) * (volumeAdjustmentDirectionFrontRightChannel == Direction.INCREASE_VOLUME ? 1 : -1));
      setRelativeVolumeAdjustmentFrontLeftChannel   ((((buffer[ 4] & 0xFF) << 8) + (buffer[ 5] & 0xFF)) * (volumeAdjustmentDirectionFrontLeftChannel  == Direction.INCREASE_VOLUME ? 1 : -1));
      setPeakVolumeFrontRightChannel                ( ((buffer[ 6] & 0xFF) << 8) + (buffer[ 7] & 0xFF));
      setPeakVolumeFrontLeftChannel                 ( ((buffer[ 8] & 0xFF) << 8) + (buffer[ 9] & 0xFF));

      // if the optional back right and left channels have been specified, then parse them
      if (buffer.length >= 14)
      {
         setRelativeVolumeAdjustmentBackRightChannel((((buffer[10] & 0xFF) << 8) + (buffer[11] & 0xFF)) * (volumeAdjustmentDirectionBackRightChannel  == Direction.INCREASE_VOLUME ? 1 : -1));
         setRelativeVolumeAdjustmentBackLeftChannel ((((buffer[12] & 0xFF) << 8) + (buffer[13] & 0xFF)) * (volumeAdjustmentDirectionBackLeftChannel   == Direction.INCREASE_VOLUME ? 1 : -1));
      }
      if (buffer.length >= 18)
      {
         setPeakVolumeBackRightChannel              ( ((buffer[14] & 0xFF) << 8) + (buffer[15] & 0xFF));
         setPeakVolumeBackLeftChannel               ( ((buffer[16] & 0xFF) << 8) + (buffer[17] & 0xFF));
      }

      // if the optional center channel has been specified, then parse it
      if (buffer.length >= 20)
         setRelativeVolumeAdjustmentCenterChannel   ((((buffer[18] & 0xFF) << 8) + (buffer[19] & 0xFF)) * (volumeAdjustmentDirectionCenterChannel     == Direction.INCREASE_VOLUME ? 1 : -1));
      if (buffer.length >= 22)
         setPeakVolumeCenterChannel                 ( ((buffer[20] & 0xFF) << 8) + (buffer[21] & 0xFF));

      // if the optional bass channel has been specified, then parse it
      if (buffer.length >= 24)
         setRelativeVolumeAdjustmentBassChannel     ((((buffer[22] & 0xFF) << 8) + (buffer[23] & 0xFF)) * (volumeAdjustmentDirectionBassChannel       == Direction.INCREASE_VOLUME ? 1 : -1));
      if (buffer.length == 26)
         setPeakVolumeBassChannel                   ( ((buffer[24] & 0xFF) << 8) + (buffer[25] & 0xFF));
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * converts a unitless volume adjustment to decibels (db).
    * The <a href="http://id3.org/id3v2.3.0">ID3 v2.3 specification</a> does not specify what units the volume adjustments are in.  This is left up to the developers who
    * implement the spec.  iTunes apparently uses the following formula, which Beaglebuddy has also adopted, to convert the volume adjustments to decibels.
    * <code>
    * volume (db) = 20 * ln(((volume adjustment / 256) + 255 ) / 255) / ln(10)
    * <br/><br/>
    * Thus, for example, if the relative volume adjustment for the front right channel was -14135, then its volume in db would be: <br/>
    * <pre class="beaglebuddy">
    * volume (db) = 20 * ln((-14135 / 256 + 255 ) / 255) / ln(10)
    *             = 20 * ln((-55.215 + 255) / 255) / 2.30
    *             = 20 * ln( 199.78515625   / 255) / 2.30
    *             = 20 * ln(0.7834712) / 2.30
    *             = 20 * -0.244021 / 2.30
    *             = -2.11953925
    * </pre>
    * </code>
    * @return the specified adjustment volume in units of decibels (db).
    * @param volume   the unitless volume adjustment that is to be converted to decibels (db).
    * @see <a href="http://savannah.nongnu.org/support/?105294">Normalize Audio</a>
    */
   public double convertVolumeAdjustmentsToDecibels(int volume)
   {
      return 20.0 * Math.log((volume / 256.0 + 255.0) / 255.0) / Math.log(10.0);
   }

   /**
    * gets the number of bits to use for specifying the volume adjustments for each of the 6 supported channel.
    * @return the number of bits to use for specifying the volume adjustments for each of the 6 supported channel.
    * @see #setBitsUsedForVolumeAdjustments(int)
    */
   public int getBitsUsedForVolumeAdjustments()
   {
      return bitsUsedForVolumeAdjustments;
   }

   /**
    * sets the number of bits to use for specifying the volume adjustments for each of the 6 supported channel.
    * @param bitsUsedForVolumeAdjustments    the number of bits to use for specifying the volume adjustments for each of the 6 supported channel.
    * @see #getBitsUsedForVolumeAdjustments()
    */
   private void setBitsUsedForVolumeAdjustments(int bitsUsedForVolumeAdjustments)
   {
      if (bitsUsedForVolumeAdjustments != 16)
         throw new IllegalArgumentException("The bits used for volume adjustments field in the frame " + frameType.getId() + " contains an invalid value, " + bitsUsedForVolumeAdjustments + ". The Beaglebuddy MP3 library only supports a value of 16 bits.");

      this.bitsUsedForVolumeAdjustments = bitsUsedForVolumeAdjustments;
      this.dirty                        = true;
   }

   /**
    * gets the relative volume adjustment for the front right channel.
    * @return the relative volume adjustment for the front right channel.
    * @see #setRelativeVolumeAdjustmentFrontRightChannel(int)
    * @see #getPeakVolumeFrontRightChannel()
    */
   public int getRelativeVolumeAdjustmentFrontRightChannel()
   {
      return relativeVolumeAdjustmentFrontRightChannel;
   }

   /**
    * sets the relative volume adjustment for the front right channel.
    * @param volume   the relative volume adjustment for the front right channel.
    * @see #getRelativeVolumeAdjustmentFrontRightChannel()
    * @see #getPeakVolumeFrontRightChannel()
    */
   public void setRelativeVolumeAdjustmentFrontRightChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The relative volume adjustment front right channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < volume adjustment <= " + MAX_VOLUME + ".");

      this.relativeVolumeAdjustmentFrontRightChannel = volume;
      this.dirty                                     = true;
   }

   /**
    * gets the peak volume for the front right channel.
    * @return the peak volume for the front right channel.
    * @see #setPeakVolumeFrontRightChannel(int)
    * @see #getRelativeVolumeAdjustmentFrontRightChannel()
    */
   public int getPeakVolumeFrontRightChannel()
   {
      return peakVolumeFrontRightChannel;
   }

   /**
    * sets the peak volume for the front right channel.
    * @param volume   the peak volume for the front right channel.
    * @see #getPeakVolumeFrontRightChannel()
    * @see #getRelativeVolumeAdjustmentFrontRightChannel()
    */
   public void setPeakVolumeFrontRightChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The peak volume front right channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < peak volume <= " + MAX_VOLUME + ".");

      this.peakVolumeFrontRightChannel = volume;
      this.dirty                       = true;
   }

   /**
    * gets the relative volume adjustment for the front left channel.
    * @return the relative volume adjustment for the front left channel.
    * @see #setRelativeVolumeAdjustmentFrontLeftChannel(int)
    * @see #getPeakVolumeFrontLeftChannel()
    */
   public int getRelativeVolumeAdjustmentFrontLeftChannel()
   {
      return relativeVolumeAdjustmentFrontLeftChannel;
   }

   /**
    * sets the relative volume adjustment for the front left channel.
    * @param volume   the relative volume adjustment for the front left channel.
    * @see #getRelativeVolumeAdjustmentFrontLeftChannel()
    * @see #getPeakVolumeFrontLeftChannel()
    */
   public void setRelativeVolumeAdjustmentFrontLeftChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The relative volume adjustment front left channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < volume adjustment <= " + MAX_VOLUME + ".");

      this.relativeVolumeAdjustmentFrontLeftChannel = volume;
      this.dirty                                    = true;
   }

   /**
    * gets the peak volume for the front left channel.
    * @return the peak volume for the front left channel.
    * @see #setPeakVolumeFrontLeftChannel(int)
    * @see #getRelativeVolumeAdjustmentFrontLeftChannel()
    */
   public int getPeakVolumeFrontLeftChannel()
   {
      return peakVolumeFrontLeftChannel;
   }

   /**
    * sets the peak volume for the front left channel.
    * @param volume   the peak volume for the front left channel.
    * @see #getPeakVolumeFrontLeftChannel()
    * @see #getRelativeVolumeAdjustmentFrontLeftChannel()
    */
   public void setPeakVolumeFrontLeftChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The peak volume front left channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < peak volume <= " + MAX_VOLUME + ".");

      this.peakVolumeFrontLeftChannel = volume;
      this.dirty                      = true;
   }

   /**
    * gets the relative volume adjustment for the back right channel.
    * @return the relative volume adjustment for the back right channel.
    * @see #setRelativeVolumeAdjustmentBackRightChannel(int)
    * @see #getPeakVolumeBackRightChannel()
    */
   public int getRelativeVolumeAdjustmentBackRightChannel()
   {
      return relativeVolumeAdjustmentBackRightChannel;
   }

   /**
    * sets the relative volume adjustment for the back right channel.
    * @param volume   the relative volume adjustment for the back right channel.
    * @see #getRelativeVolumeAdjustmentBackRightChannel()
    * @see #getPeakVolumeBackRightChannel()
    */
   public void setRelativeVolumeAdjustmentBackRightChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The relative volume adjustment back right channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < volume adjustment <= " + MAX_VOLUME + ".");

      this.relativeVolumeAdjustmentBackRightChannel = volume;
      this.dirty                                    = true;
   }

   /**
    * gets the peak volume for the back right channel.
    * @return the peak volume for the back right channel.
    * @see #setPeakVolumeBackRightChannel(int)
    * @see #getRelativeVolumeAdjustmentBackRightChannel()
    */
   public int getPeakVolumeBackRightChannel()
   {
      return peakVolumeBackRightChannel;
   }

   /**
    * sets the peak volume for the back right channel.
    * @param volume   the peak volume for the back right channel.
    * @see #getPeakVolumeBackRightChannel()
    * @see #getRelativeVolumeAdjustmentBackRightChannel()
    */
   public void setPeakVolumeBackRightChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The peak volume back right channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < peak volume <= " + MAX_VOLUME + ".");

      this.peakVolumeBackRightChannel = volume;
      this.dirty                      = true;
   }

   /**
    * gets the relative volume adjustment for the back left channel.
    * @return the relative volume adjustment for the back left channel.
    * @see #setRelativeVolumeAdjustmentBackLeftChannel(int)
    * @see #getPeakVolumeBackLeftChannel()
    */
   public int getRelativeVolumeAdjustmentBackLeftChannel()
   {
      return relativeVolumeAdjustmentBackLeftChannel;
   }

   /**
    * sets the relative volume adjustment for the back left channel.
    * @param volume   the relative volume adjustment for the back left channel.
    * @see #getRelativeVolumeAdjustmentBackLeftChannel()
    * @see #getPeakVolumeBackLeftChannel()
    */
   public void setRelativeVolumeAdjustmentBackLeftChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The relative volume adjustment back left channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < volume adjustment <= " + MAX_VOLUME + ".");

      this.relativeVolumeAdjustmentBackLeftChannel = volume;
      this.dirty                                   = true;
   }

   /**
    * gets the peak volume for the back left channel.
    * @return the peak volume for the back left channel.
    * @see #setPeakVolumeBackLeftChannel(int)
    * @see #getRelativeVolumeAdjustmentBackLeftChannel()
    */
   public int getPeakVolumeBackLeftChannel()
   {
      return peakVolumeBackLeftChannel;
   }

   /**
    * sets the peak volume for the back left channel.
    * @param volume   the peak volume for the back left channel.
    * @see #getPeakVolumeBackLeftChannel()
    * @see #getRelativeVolumeAdjustmentBackLeftChannel()
    */
   public void setPeakVolumeBackLeftChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The peak volume back left channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < peak volume <= " + MAX_VOLUME + ".");

      this.peakVolumeBackLeftChannel = volume;
      this.dirty                     = true;
   }

   /**
    * gets the relative volume adjustment for the center channel.
    * @return the relative volume adjustment for the center channel.
    * @see #setRelativeVolumeAdjustmentCenterChannel(int)
    * @see #getPeakVolumeCenterChannel()
    */
   public int getRelativeVolumeAdjustmentCenterChannel()
   {
      return relativeVolumeAdjustmentCenterChannel;
   }

   /**
    * sets the relative volume adjustment for the center channel.
    * @param volume   the relative volume adjustment for the center channel.
    * @see #getRelativeVolumeAdjustmentCenterChannel()
    * @see #getPeakVolumeCenterChannel()
    */
   public void setRelativeVolumeAdjustmentCenterChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The relative volume adjustment center channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < volume adjustment <= " + MAX_VOLUME + ".");

      this.relativeVolumeAdjustmentCenterChannel = volume;
      this.dirty                                    = true;
   }

   /**
    * gets the peak volume for the center channel.
    * @return the peak volume for the center channel.
    * @see #setPeakVolumeCenterChannel(int)
    * @see #getRelativeVolumeAdjustmentCenterChannel()
    */
   public int getPeakVolumeCenterChannel()
   {
      return peakVolumeCenterChannel;
   }

   /**
    * sets the peak volume for the center channel.
    * @param volume   the peak volume for the center channel.
    * @see #getPeakVolumeCenterChannel()
    * @see #getRelativeVolumeAdjustmentCenterChannel()
    */
   public void setPeakVolumeCenterChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The peak volume center channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < peak volume <= " + MAX_VOLUME + ".");

      this.peakVolumeCenterChannel = volume;
      this.dirty                      = true;
   }

   /**
    * gets the relative volume adjustment for the bass channel.
    * @return the relative volume adjustment for the bass channel.
    * @see #setRelativeVolumeAdjustmentBassChannel(int)
    * @see #getPeakVolumeBassChannel()
    */
   public int getRelativeVolumeAdjustmentBassChannel()
   {
      return relativeVolumeAdjustmentBassChannel;
   }

   /**
    * sets the relative volume adjustment for the bass channel.
    * @param volume   the relative volume adjustment for the bass channel.
    * @see #getRelativeVolumeAdjustmentBassChannel()
    * @see #getPeakVolumeBassChannel()
    */
   public void setRelativeVolumeAdjustmentBassChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The relative volume adjustment bass channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < volume adjustment <= " + MAX_VOLUME + ".");

      this.relativeVolumeAdjustmentBassChannel = volume;
      this.dirty                               = true;
   }

   /**
    * gets the peak volume for the bass channel.
    * @return the peak volume for the bass channel.
    * @see #setPeakVolumeBassChannel(int)
    * @see #getRelativeVolumeAdjustmentBassChannel()
    */
   public int getPeakVolumeBassChannel()
   {
      return peakVolumeBassChannel;
   }

   /**
    * sets the peak volume for the bass channel.
    * @param volume   the peak volume for the bass channel.
    * @see #getPeakVolumeBassChannel()
    * @see #getRelativeVolumeAdjustmentBassChannel()
    */
   public void setPeakVolumeBassChannel(int volume)
   {
      if (volume < -MAX_VOLUME || volume > MAX_VOLUME)
         throw new IllegalArgumentException("The peak volume bass channel field in the frame " + frameType.getId() + " contains an invalid value, " + volume + ". It must be " + (-MAX_VOLUME) + " < peak volume <= " + MAX_VOLUME + ".");

      this.peakVolumeBassChannel = volume;
      this.dirty                 = true;
   }
   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         if (buffer.length != 26)
            buffer = new byte[26];

         buffer[0] = (byte)(relativeVolumeAdjustmentFrontRightChannel >= 0 ? buffer[0] | DIRECTION_FRONT_RIGHT_CHANNEL_MASK : buffer[0] & ~DIRECTION_FRONT_RIGHT_CHANNEL_MASK);
         buffer[0] = (byte)(relativeVolumeAdjustmentFrontLeftChannel  >= 0 ? buffer[0] | DIRECTION_FRONT_LEFT_CHANNEL_MASK  : buffer[0] & ~DIRECTION_FRONT_LEFT_CHANNEL_MASK );
         buffer[0] = (byte)(relativeVolumeAdjustmentBackRightChannel  >= 0 ? buffer[0] | DIRECTION_BACK_RIGHT_CHANNEL_MASK  : buffer[0] & ~DIRECTION_BACK_RIGHT_CHANNEL_MASK );
         buffer[0] = (byte)(relativeVolumeAdjustmentBackLeftChannel   >= 0 ? buffer[0] | DIRECTION_BACK_LEFT_CHANNEL_MASK   : buffer[0] & ~DIRECTION_BACK_LEFT_CHANNEL_MASK  );
         buffer[0] = (byte)(relativeVolumeAdjustmentCenterChannel     >= 0 ? buffer[0] | DIRECTION_CENTER_CHANNEL_MASK      : buffer[0] & ~DIRECTION_CENTER_CHANNEL_MASK     );
         buffer[0] = (byte)(relativeVolumeAdjustmentBassChannel       >= 0 ? buffer[0] | DIRECTION_BASS_CHANNEL_MASK        : buffer[0] & ~DIRECTION_BASS_CHANNEL_MASK       );
         buffer[1] = (byte)bitsUsedForVolumeAdjustments;
         System.arraycopy(intToBytes(Math.abs(relativeVolumeAdjustmentFrontRightChannel)), 2, buffer, 2 , 2);
         System.arraycopy(intToBytes(Math.abs(relativeVolumeAdjustmentFrontLeftChannel )), 2, buffer, 4 , 2);
         System.arraycopy(intToBytes(         peakVolumeFrontRightChannel               ), 2, buffer, 6 , 2);
         System.arraycopy(intToBytes(         peakVolumeFrontLeftChannel                ), 2, buffer, 8 , 2);
         System.arraycopy(intToBytes(Math.abs(relativeVolumeAdjustmentBackRightChannel )), 2, buffer, 10, 2);
         System.arraycopy(intToBytes(Math.abs(relativeVolumeAdjustmentBackLeftChannel  )), 2, buffer, 12, 2);
         System.arraycopy(intToBytes(         peakVolumeBackRightChannel                ), 2, buffer, 14, 2);
         System.arraycopy(intToBytes(         peakVolumeBackLeftChannel                 ), 2, buffer, 16, 2);
         System.arraycopy(intToBytes(Math.abs(relativeVolumeAdjustmentCenterChannel    )), 2, buffer, 18, 2);
         System.arraycopy(intToBytes(         peakVolumeCenterChannel                   ), 2, buffer, 20, 2);
         System.arraycopy(intToBytes(Math.abs(relativeVolumeAdjustmentBassChannel      )), 2, buffer, 22, 2);
         System.arraycopy(intToBytes(         peakVolumeBassChannel                     ), 2, buffer, 24, 2);
      }
      dirty = false;    // data has already been saved to the buffer
   }

   /**
    * gets a string representation of the <i>relative volume adjustment</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: relative volume adjustment\n");
      buffer.append("   bytes..........................................: " +  this.buffer.length                                   + " bytes\n");
      buffer.append("                                                    " +  hex(this.buffer, 52)                                 + "\n");
      buffer.append("   bits used for volume adjustments...............: " +  bitsUsedForVolumeAdjustments                         + "\n");
      buffer.append("   volume adjustment direction front right channel: " + ((super.buffer[0] & DIRECTION_FRONT_RIGHT_CHANNEL_MASK) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME) + "\n");
      buffer.append("   volume adjustment direction front left  channel: " + ((super.buffer[0] & DIRECTION_FRONT_LEFT_CHANNEL_MASK ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME) + "\n");
      buffer.append("   volume adjustment direction back  right channel: " + ((super.buffer[0] & DIRECTION_BACK_RIGHT_CHANNEL_MASK ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME) + "\n");
      buffer.append("   volume adjustment direction back  left  channel: " + ((super.buffer[0] & DIRECTION_BACK_LEFT_CHANNEL_MASK  ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME) + "\n");
      buffer.append("   volume adjustment direction center      channel: " + ((super.buffer[0] & DIRECTION_CENTER_CHANNEL_MASK     ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME) + "\n");
      buffer.append("   volume adjustment direction bass        channel: " + ((super.buffer[0] & DIRECTION_BASS_CHANNEL_MASK       ) == 0 ? Direction.DECREASE_VOLUME : Direction.INCREASE_VOLUME) + "\n");
      buffer.append("   relative volume adjustment  front right channel: " +  relativeVolumeAdjustmentFrontRightChannel            + "\n");
      buffer.append("   relative volume adjustment  front left  channel: " +  relativeVolumeAdjustmentFrontLeftChannel             + "\n");
      buffer.append("   relative volume adjustment  back  right channel: " +  relativeVolumeAdjustmentBackRightChannel             + "\n");
      buffer.append("   relative volume adjustment  back  left  channel: " +  relativeVolumeAdjustmentBackLeftChannel              + "\n");
      buffer.append("   relative volume adjustment  center      channel: " +  relativeVolumeAdjustmentCenterChannel                + "\n");
      buffer.append("   relative volume adjustment  bass        channel: " +  relativeVolumeAdjustmentBassChannel                  + "\n");
      buffer.append("   peak volume front right channel................: " +  peakVolumeFrontRightChannel                          + "\n");
      buffer.append("   peak volume front left  channel................: " +  peakVolumeFrontLeftChannel                           + "\n");
      buffer.append("   peak volume back  right channel................: " +  peakVolumeBackRightChannel                           + "\n");
      buffer.append("   peak volume back  left  channel................: " +  peakVolumeBackLeftChannel                            + "\n");
      buffer.append("   peak volume center      channel................: " +  peakVolumeCenterChannel                              + "\n");
      buffer.append("   peak volume bass        channel................: " +  peakVolumeBassChannel                                + "\n");

      return buffer.toString();
   }
}
