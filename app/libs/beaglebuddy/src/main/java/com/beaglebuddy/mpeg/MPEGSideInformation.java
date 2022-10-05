package com.beaglebuddy.mpeg;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.util.Utility;



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
 * The MPEG side information follows the MPEG audio frame header in an MPEG audio frame.  It is a required part of an MPEG audio frame, and is used by mp3 players to
 * decode the mpeg audio samples.
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
 * @see <a href="http://www.bth.se/fou/cuppsats.nsf/all/857e49b9bfa2d753c125722700157b97/$file/Thesis%20report-%20MP3%20Decoder.pdf" target="_blank">Section 5.2, page 24 of "MP3 Decoder in Theory and Practice"</a>
 * @see <a href="http://www.mp3-tech.org/programmer/docs/mp3_theory.pdf"                                                             target="_blank">Section 5.1.2, page 13 of "The Theory Behind Mp3"</a>
 *
 */
public class MPEGSideInformation
{
   // class mnemonics
                                                                          /** side information is 17 bytes long for .mp3 files encoded with a single channel  (mono).   */
   public  static final int    HEADER_MIN_SIZE                = 17;       /** side information is 32 bytes long for .mp3 files encoded with   dual   channels (stereo). */
   public  static final int    HEADER_MAX_SIZE                = 32;

   // data members
   private byte[] data;



   /**
    * default constructor.
    */
   public MPEGSideInformation()
   {
      data = null;
   }

   /**
    * constructor.
    * @param bytes   the raw bytes of an MPEG audio frame.
    * @param size    size (in bytes) of the side information block in the mpeg audio frame.
    * @throws ParseException  if an invalid size is specified, or if the byte stream is smaller than the specified size.
    */
   public MPEGSideInformation(byte[] bytes, int size) throws ParseException
   {
      if (size != HEADER_MIN_SIZE && size != HEADER_MAX_SIZE)
         throw new ParseException("Invalid size specified for MPEG side information, " + size + ".  It must be either " + HEADER_MIN_SIZE + " or " + HEADER_MAX_SIZE + ".", bytes);
      if (bytes.length < size)
         throw new ParseException("MPEG audio frame size, " + bytes.length + ", is too small for the " + size + " byte MPEG side information.", bytes);

      data = new byte[size];
      System.arraycopy(bytes, 0, data, 0, size);
   }

   /**
    * constructor.
    * @param inputStream    input stream pointing to the side information data in an mpeg audio frame in an .mp3 file.
    * @param size           size (in bytes) of the side information block in the mpeg audio frame.
    * @throws IOException     if there is an error while reading the side information from the .mp3 file.
    * @throws ParseException  if an invalid size is specified, or if the byte stream is smaller than the specified size.
    */
   public MPEGSideInformation(InputStream inputStream, int size) throws IOException, ParseException
   {
      if (size != HEADER_MIN_SIZE && size != HEADER_MAX_SIZE)
         throw new ParseException("Invalid size specified for MPEG side information, " + size + ".  It must be either " + HEADER_MIN_SIZE + " or " + HEADER_MAX_SIZE + ".", new byte[0]);

      data = new byte[size];

      int numBytesRead = inputStream.read(data);

      if (numBytesRead != data.length)
      {
         if (numBytesRead == -1)
            throw new ParseException("EOF reached while reading the MPEG side information.", data);
         if (inputStream.read() == -1)
            throw new ParseException("EOF reached while reading the MPEG side information.", data);
         throw new IOException("Unable to read the side information from the mpeg audio frame in the mp3 file.");
      }
   }

   /**
    * gets the raw binary data stored in the side information block in an mpeg audio frame.
    * @return the raw binary data stored in the side information block in an mpeg audio frame.
    */
   public byte[] getData()
   {
      return data;
   }

   /**
    * gets the size (in bytes) of the side information block in an mpeg audio frame.
    * @return the size (in bytes) of the side information block in an mpeg audio frame.
    */
   public int getSize()
   {
      return data == null ? 0 : data.length;
   }

   /**
    * gets a string representation of the mpeg audio frame's side information.
    * @return a string representation of the mpeg audio frame's side information.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("mpeg audio frame side information\n");
      buffer.append("   size.: " + data.length + " bytes\n");
      buffer.append("   bytes: " + Utility.hex(data, 10));

      return buffer.toString();
   }
}
