package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.v24.Encoding;
import com.beaglebuddy.id3.enums.v24.FrameType;
import com.beaglebuddy.id3.pojo.v24.RelativeVolume;



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
 * An ID3v2.4 <i>relative volume adjustment</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#RELATIVE_VOLUME_ADJUSTMENT RVA2} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame}
 * which is used to adjust the volume of .mp3 songs to a consistent level.  Since some songs are recorded at higher levels than others, this frame could allow a user to set a desired volume and the .mp3
 * players would adjust the output volume of the .mp3 song to the level specified by the user.
 * <p>
 * </p>
 * This new version of the relative volume adjustment frame is much simpler than the version found in the ID3v2.3 specification. The volume adjustments are now fixed 16 bits
 * integers.  This frame allows the user to say how much he wants to increase/decrease the volume on each channel when the file is played. The purpose is to be able to align
 * all files to a reference volume, so that you don't have to change the volume constantly. This frame may also be used to balance adjust the audio.
 * </p>
 * <p class="beaglebuddy">
 * The <i>relative volume adjustment</i> frame allows the user to specify both relative and peak volume settings for the following speaker channels:
 * <ul>
 *    <li>other               </li>
 *    <li>master volume       </li>
 *    <li>front right  channel</li>
 *    <li>front left   channel</li>
 *    <li>back  right  channel</li>
 *    <li>back  left   channel</li>
 *    <li>front center channel</li>
 *    <li>back  center channel</li>
 *    <li>sub woofer   channel</li>
 * </ul>
 *
 *
 * </p>
 * <p class="beaglebuddy">
 * While the ID3v2.4 specification allows the number of bits used to store corresponding peak volumes to vary between 0 and 255, the Beaglebuddy MP3 library only allows
 * 0 and 32 bit values.  The monkey's who came up with the ID3v2.x specs did some crazy stuff, and this is one of them.
 * </p>
 * The <i>relative volume adjustment</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Relative Volume Adjustment Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">identification                                             </td><td class="beaglebuddy">uniquely identifies the situation and/or device when this eq curve should be applied.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.pojo.v24.RelativeVolume volumes}</td><td class="beaglebuddy">list of the relative volume adjustments for the different speaker channels.          </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>relative volume adjustment</i> frame.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyRelativeVolumeAdjustment extends ID3v24FrameBody
{
   // data members
   private String               identification;      // identify the situation and/or device when this relative volume adjustment should be applied.
   private List<RelativeVolume> volumes;             // volume adjustments for each speaker channel




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>"standard" relative volume adjustment</li>
    *    <li>0 volume adjustments                 </li>
    * </ul>
    */
   public ID3v24FrameBodyRelativeVolumeAdjustment()
   {
      this("standard", new Vector<RelativeVolume>());
   }

   /**
    * This constructor is called when creating a new frame.
    * @param identification        uniquely identifies the situation and/or device when this relative volume adjustment should be applied.
    * @param volumes               relative volume adjustments for each speaker channel.
    */
   public ID3v24FrameBodyRelativeVolumeAdjustment(String identification, RelativeVolume[] volumes)
   {
      super(FrameType.RELATIVE_VOLUME_ADJUSTMENT);

      setIdentification(identification);
      setVolumes       (volumes);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param identification        uniquely identifies the situation and/or device when this relative volume adjustment should be applied.
    * @param volumes               relative volume adjustments for each speaker channel.
    */
   public ID3v24FrameBodyRelativeVolumeAdjustment(String identification, List<RelativeVolume> volumes)
   {
      super(FrameType.RELATIVE_VOLUME_ADJUSTMENT);

      setIdentification(identification);
      setVolumes       (volumes);

      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a relative volume adjustment frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyRelativeVolumeAdjustment(InputStream inputStream, int frameBodySize) throws IOException
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
      nullTerminatorIndex = getNextNullTerminator(0, Encoding.ISO_8859_1);
      setIdentification(new String(buffer, 0, nullTerminatorIndex-1, Encoding.ISO_8859_1.getCharacterSet()).trim());

      int index   = nullTerminatorIndex + 1;
          volumes = new Vector<RelativeVolume>();

      while (index < buffer.length)
      {
         RelativeVolume.Channel channel             = RelativeVolume.Channel.getChannel(buffer[index]);
         short                  relVolume           = bytesToShort(buffer, index);
         index += 2;
         int                    numBitsInPeakVolume = buffer[index];
         index++;

         RelativeVolume volume = null;

         if (numBitsInPeakVolume == 0)
         {
            volume = new RelativeVolume(channel, relVolume / 512.0, RelativeVolume.PeakVolumeSize.SIZE_NONE, 0);
         }
         else
         {
            int numBytes = numBitsInPeakVolume % 8 == 0 ? numBitsInPeakVolume / 8 : numBitsInPeakVolume / 8 + 1;
            // this is a hack used by Beaglebuddy Software to only allow 32 bit peak volume sizes
            int peakVolume = 0;
            if (numBytes == 1)
               peakVolume = buffer[index];
            else if (numBytes == 2)
               peakVolume = bytesToShort(buffer, index);
            else if (numBytes == 3)
               peakVolume = (buffer[index] & 0xFF) << 16 + (buffer[index + 1] & 0xFF) << 8  + (buffer[index + 2] & 0xFF);
            else if (numBytes == 4)
               peakVolume = bytesToInt(buffer, index);
            else
               peakVolume =  buffer[index + numBytes - 1];   // ignore all but the last byte

            index+=numBytes;
            volume = new RelativeVolume(channel, relVolume / 512.0, RelativeVolume.PeakVolumeSize.SIZE_32_BITS, peakVolume);
         }
         volumes.add(volume);
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * get the string which uniquely identifies the situation and/or device where this EQ curve should be applied.
    * @return  the string which uniquely identifies the situation and/or device where this EQ curve should be applied.
    * @see #setIdentification(String)
    */
   public String getIdentification()
   {
      return identification;
   }

   /**
    * set the string which uniquely identifies the situation and/or device where this EQ curve should be applied.
    * @param identification   string which uniquely identifies the situation and/or device where this EQ curve should be applied.
    * @see #getIdentification()
    */
   public void setIdentification(String identification)
   {
      this.identification = identification;
   }

   /**
    * gets the relative volume adjustment for the specified speaker channel.
    * @return if a relative volume adjustment has been specified for the given speaker channel, then return it.  Otherwise, return null.
    * @param channel   the speaker channel whose volume setting will be retrieved.
    * @see #getVolumes()
    * @see #setVolume(RelativeVolume)
    */
   public RelativeVolume getVolume(RelativeVolume.Channel channel)
   {
      for(RelativeVolume volume : volumes)
         if (volume.getChannel() == channel)
            return volume;

      return null;
   }

   /**
    * sets the relative volume setting for a specific speaker channel.
    * @param volume  the relative volume setting for a specific speaker channel.
    * @see #getVolume(RelativeVolume.Channel)
    * @see #getVolumes()
    */
   public void setVolume(RelativeVolume volume)
   {
      if (volume != null)
      {
         RelativeVolume old = getVolume(volume.getChannel());
         if (old != null)
            volumes.remove(old);
         volumes.add(volume);
      }
      this.dirty = true;
   }

   /**
    * gets the volume settings comprising the relative volume adjustment.
    * @return the volume settings comprising the relative volume adjustment.
    * @see #setVolume(RelativeVolume)
    * @see #setVolumes(List)
    */
   public List<RelativeVolume> getVolumes()
   {
      return volumes;
   }

   /**
    * sets the volume settings comprising the relative volume adjustment.
    * @param volumes  the volume settings comprising the relative volume adjustment.
    * @see #getVolumes()
    */
   public void setVolumes(RelativeVolume[] volumes)
   {
      Vector<RelativeVolume> listVolumes = new Vector<RelativeVolume>();

      if (volumes != null)
      {
         for(RelativeVolume level : volumes)
            listVolumes.add(level);
      }
      setVolumes(listVolumes);
   }

   /**
    * sets the volumes comprising the equalization curve.
    * @param volumes  the volumes comprising the equalization curve.
    * @see #getVolumes()
    */
   public void setVolumes(List<RelativeVolume> volumes)
   {
      this.volumes = volumes == null ? new Vector<RelativeVolume>() : volumes;
      this.dirty   = true;
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
         int totalVolumeSize = 0;
         for(RelativeVolume volume : volumes)
            totalVolumeSize += (volume.getPeakVolumeSize() == RelativeVolume.PeakVolumeSize.SIZE_NONE ? 4 : 8);

         byte[] idenitificationBytes = stringToBytes(Encoding.ISO_8859_1, identification);
         int    index                = 0;
         buffer                      = new byte[idenitificationBytes.length + totalVolumeSize];

         System.arraycopy(idenitificationBytes, 0, buffer, index, idenitificationBytes.length);
         index += idenitificationBytes.length;
         for(RelativeVolume volume : volumes)
         {
            buffer[index] = (byte)volume.getChannel().ordinal();
            index++;
            System.arraycopy(shortToBytes((short)(volume.getVolume() * 512)), 0, buffer, index, 2);
            index += 2;
            buffer[index] = (byte)(volume.getPeakVolumeSize() == RelativeVolume.PeakVolumeSize.SIZE_NONE ? 0 : 32);
            index++;
            if (volume.getPeakVolumeSize() == RelativeVolume.PeakVolumeSize.SIZE_32_BITS)
               System.arraycopy(intToBytes(volume.getPeakVolume()), 0, buffer, index, 4);
         }
      }
      dirty = false;    // data has already been saved to the buffer
   }

   /**
    * gets a string representation of the ID3v2.4 <i>relative volume adjustment</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: relative volume adjustment\n");
      buffer.append("   bytes......................: " + this.buffer.length   + " bytes\n");
      buffer.append("                                " + hex(this.buffer, 32) + "\n");
      buffer.append("   identification.............: " + identification       + "\n");
      buffer.append("   relative volume adjustments: " + volumes.size()       + "\n");
      for(RelativeVolume volume : volumes)
         buffer.append("                                " + volume            + "\n");

      return buffer.toString();
   }
}
