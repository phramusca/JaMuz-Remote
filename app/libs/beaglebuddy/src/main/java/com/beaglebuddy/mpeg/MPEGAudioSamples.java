package com.beaglebuddy.mpeg;

import com.beaglebuddy.exception.ParseException;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * The MPEG audio samples follows the MPEG side information in an MPEG audio frame.  It contains the actual digital audio samples used by mp3 players to produce the sound.
 * <br/><br/>
 * <img src="../../../resources/mpeg_frame_CBR_format.jpg" width="420" height="450" alt="CBR encoded mp3 file format" usemap="#cbr_map"/>
 * <map name="cbr_map">
 *    <area shape="rect" coords="  30,  58, 242, 105" href="../id3/v23/ID3v23Tag.html"     alt="ID3v2.x Tag"/>
 *    <area shape="rect" coords="  30, 106, 242, 152" href="MPEGFrameHeader.html"          alt="MPEG Audio Frame Header"/>
 *    <area shape="rect" coords="  30, 153, 242, 207" href="MPEGSideInformation.html"      alt="MPEG Side Information"/>
 *    <area shape="rect" coords="  30, 208, 242, 264" href="MPEGAudioSamples.html"         alt="MPEG Audio Samples"/>
 *    <area shape="rect" coords="  30, 265, 242, 307" href="MPEGFrameHeader.html"          alt="MPEG Audio Frame Header"/>
 *    <area shape="rect" coords="  30, 308, 242, 364" href="MPEGSideInformation.html"      alt="MPEG Side Information"/>
 *    <area shape="rect" coords="  30, 365, 242, 418" href="MPEGAudioSamples.html"         alt="MPEG Audio Samples"/>
 * </map>
 * </p>
 * @see MPEGFrame
 */
public class MPEGAudioSamples
{
   // data members
   private byte[] data;



   /**
    * default constructor.
    */
   public MPEGAudioSamples()
   {
      data = null;
   }

   /**
    * constructor.
    * @param bytes   the raw bytes of an MPEG audio frame.
    * @param index   the offset into the bytes of the MPEG audio frame where the audio samples begin.
    * @throws ParseException  if the offset into the byte stream is larger than the size of the mpeg audio frame.
    */
   public MPEGAudioSamples(byte[] bytes, int index) throws ParseException
   {
      if (index > bytes.length)
         throw new ParseException("MPEG audio frame size, " + bytes.length + ", is too small for the MPEG audio samples.", bytes);

      data = new byte[bytes.length - index];
      System.arraycopy(bytes, index, data, 0, data.length);
   }

   /**
    * gets the raw digital audio samples stored in the mpeg audio frame.
    * @return the raw digital audio samples stored in the mpeg audio frame.
    */
   public byte[] getData()
   {
      return data;
   }

   /**
    * gets the size (in bytes) of the raw binary data comprising the mpeg audio samples.
    * @return the size (in bytes) of the raw binary data comprising the mpeg audio samples.
    */
   public int getSize()
   {
      return data == null ? 0 : data.length;
   }

   /**
    * gets a string representation of the mpeg audio frame's audio samples.
    * @return a string representation of the mpeg audio frame's audio samples.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("mpeg audio frame audio samples\n");
      buffer.append("   size.: " + data.length + " bytes");

      return buffer.toString();
   }
}
