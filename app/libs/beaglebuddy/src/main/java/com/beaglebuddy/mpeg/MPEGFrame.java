package com.beaglebuddy.mpeg;

import java.io.InputStream;
import java.io.IOException;

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
 * The actual audio data for an .mp3 file is stored in MPEG audio frames. Depending on how an .mp3 file was encoded (either {@link com.beaglebuddy.mpeg.enums.BitrateType CBR or VBR}),
 * the format of the first MPEG audio frame may be different.  All subsequent MPEG audio frames will have the same format regardless of how the .mp3 file was encoded.
 * The two formats are shown below:
 * </p>
 * <p>
 * <table border="0">
 *   <tbody>
 *      <tr>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/mpeg_frame_CBR_format.jpg" width="420" height="450" alt="CBR encoded mp3 file format" usemap="#cbr_map"/>
 *          </td>
 *          <td> &nbsp; &nbsp; &nbsp; </td>
 *          <td class="beaglebuddy_pic_align_top">
 *             <img src="../../../resources/mpeg_frame_VBR_format.jpg" width="500" height="580" alt="VBR encoded mp3 file format" usemap="#vbr_map"/>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * <map name="cbr_map">
 *    <area shape="rect" coords="  30,  58, 242, 105" href="../id3/v23/ID3v23Tag.html"     alt="ID3v2.x Tag"/>
 *    <area shape="rect" coords="  30, 106, 242, 152" href="MPEGFrameHeader.html"          alt="MPEG Audio Frame Header"/>
 *    <area shape="rect" coords="  30, 153, 242, 207" href="MPEGSideInformation.html"      alt="MPEG Side Information"/>
 *    <area shape="rect" coords="  30, 208, 242, 264" href="MPEGAudioSamples.html"         alt="MPEG Audio Samples"/>
 *    <area shape="rect" coords="  30, 265, 242, 307" href="MPEGFrameHeader.html"          alt="MPEG Audio Frame Header"/>
 *    <area shape="rect" coords="  30, 308, 242, 364" href="MPEGSideInformation.html"      alt="MPEG Side Information"/>
 *    <area shape="rect" coords="  30, 365, 242, 418" href="MPEGAudioSamples.html"         alt="MPEG Audio Samples"/>
 * </map>
 * <map name="vbr_map">
 *    <area shape="rect" coords="  28,  55, 239, 104" href="../id3/v23/ID3v23Tag.html"     alt="ID3v2.x Tag"/>
 *    <area shape="rect" coords="  28, 105, 239, 150" href="MPEGFrameHeader.html"          alt="MPEG Audio Frame Header"/>
 *    <area shape="rect" coords="  28, 151, 239, 204" href="MPEGSideInformation.html"      alt="MPEG Side Information"/>
 *    <area shape="rect" coords="  28, 205, 239, 254" href="XingHeader.html"               alt="Xing Header"/>
 *    <area shape="rect" coords="  28, 255, 239, 305" href="LAMEHeader.html"               alt="LAME Header"/>
 *    <area shape="rect" coords="  28, 306, 239, 362" href="VBRIHeader.html"               alt="VBRI Header"/>
 *    <area shape="rect" coords="  28, 363, 242, 408" href="MPEGAudioSamples.html"         alt="MPEG Audio Samples"/>
 *    <area shape="rect" coords="  28, 409, 239, 451" href="MPEGFrameHeader.html"          alt="MPEG Audio Frame Header"/>
 *    <area shape="rect" coords="  28, 452, 239, 505" href="MPEGSideInformation.html"      alt="MPEG Side Information"/>
 *    <area shape="rect" coords="  28, 506, 242, 563" href="MPEGAudioSamples.html"         alt="MPEG Audio Samples"/>
 * </map>
 * </p>
 * <p class="beaglebuddy">
 * All MPEG audio frames consists of a {@link MPEGFrameHeader header}, some {@link MPEGSideInformation side information}, followed by the actual {@link MPEGAudioSamples digital audio samples}
 * which is what you hear when you play an .mp3 file.  The first MPEG audio frame of a variable bitrate (VBR) encoded .mp3 may contain optional headers ({@link XingHeader Xing},
 * {@link LAMEHeader LAME}, and {@link VBRIHeader VBRI}) which are sandwiched between the MPEG side information and the MPEG audio samples as shown in the diagram above.
 * </p>
 * @see <a href="http://www.iso.org/iso/catalogue_detail.htm?csnumber=22412"                      target="_blank">ISO / IEC 11172-3</a>
 * @see <a href="http://mpgedit.org/mpgedit/mpeg_format/MP3Format.html"                           target="_blank">MPEG Format</a>
 * @see <a href="http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#MPEGAudioFrame" target="_blank">MPEG Format</a>
 */
public class MPEGFrame
{
   // data members
   private MPEGFrameHeader     mpegFrameHeader;
   private MPEGSideInformation mpegSideInformation;
   private XingHeader          xingHeader;
   private LAMEHeader          lameHeader;
   private VBRIHeader          vbriHeader;
   private MPEGAudioSamples    mpegAudioSamples;
   private int                 filePosition;       // byte location within the .mp3 file where the mpeg audio frame starts




   /**
    * default constructor.
    */
   public MPEGFrame()
   {
      mpegFrameHeader      = null;
      mpegSideInformation  = null;
      xingHeader           = null;
      vbriHeader           = null;
      lameHeader           = null;
      mpegAudioSamples     = null;
      filePosition         = 0;
   }

   /**
    * constructor used to read in subsequent mpeg audio frames after the first frame from an .mp3 file.
    * @param inputStream   input stream to read in the binary .mp3 file.
    * @throws IOException      if there is an error while reading the mpeg audio frame.
    * @throws ParseException   if an invalid value is detected while parsing the mpeg frame's raw bytes.
    */
   public MPEGFrame(InputStream inputStream) throws IOException, ParseException
   {
      this.mpegFrameHeader = new MPEGFrameHeader(inputStream);

      // read in the bytes for the entire mpeg audio frame
      byte[] bytes        = new byte[mpegFrameHeader.getFrameSize() - mpegFrameHeader.getSize()];
      int    numBytesRead = inputStream.read(bytes);

      if (numBytesRead != bytes.length)
      {
         if (numBytesRead == -1)
            throw new ParseException("EOF", bytes);

         bytes = new byte[1];
         if (inputStream.read(bytes) == -1)
            throw new ParseException("EOF", bytes);
         throw new IOException("Unable to read mpeg audio frame");
      }
      this.mpegSideInformation = new MPEGSideInformation(bytes, mpegFrameHeader.getSideInfoSize());
      this.mpegAudioSamples    = new MPEGAudioSamples   (bytes, mpegFrameHeader.getSideInfoSize());
   }

   /**
    * constructor used to search for and read in the 1st mpeg audio frame from an .mp3 file or to re-synch the mpeg audio frames.
    * @param data          bytes read in from the .mp3 file while searching for the first mpeg audio frame.  These bytes will be checked to see if they are a valid MPEG audio frame header.
    * @param inputStream   input stream to read in the binary .mp3 file.
    * @throws IOException      if there is an error while reading the mpeg audio frame.
    * @throws ParseException   if an invalid value is detected while parsing the mpeg frame's raw bytes.
    */
   public MPEGFrame(byte[] data, InputStream inputStream) throws IOException, ParseException
   {
      this.mpegFrameHeader = new MPEGFrameHeader(data, inputStream);

      // read in the bytes for the entire mpeg audio frame
      byte[] bytes        = new byte[mpegFrameHeader.getFrameSize() - mpegFrameHeader.getSize()];
      int    numBytesRead = inputStream.read(bytes);

      if (numBytesRead != bytes.length)
      {
         if (numBytesRead == -1)
            throw new ParseException("EOF", bytes);

         bytes = new byte[1];
         if (inputStream.read(bytes) == -1)
            throw new ParseException("EOF", bytes);
         throw new IOException("Unable to read mpeg audio frame.");
      }

      // parse the side information
      this.mpegSideInformation = new MPEGSideInformation(bytes, mpegFrameHeader.getSideInfoSize());
      int index = mpegSideInformation.getSize();

      // see if there is an optional Xing header
      if (bytes.length - index >= XingHeader.HEADER_MIN_SIZE)
      {
         try
         {
            this.xingHeader = new XingHeader(bytes, index);
            index += xingHeader.getSize();
         }
         catch (ParseException ex)
         {
            // for some reason, when the MPEG frame header has the "protected by CRC", the 2 byte CRC that normally follows the MPEG frame header is sometimes not present.
            // perhaps this is because the "protected by CRC" is true when the "protected by CRC" bit is 0, and some encoders are incorrectly setting it to 0.
            // whatever the reason, we shift the index by two bytes to account for the CRC that was read in as part of the MPEG frame header and see if an Xing
            // header can be found.
            if (mpegFrameHeader.isProtectedByCRC())
            {
               try
               {
                  this.xingHeader = new XingHeader(bytes, index - 2);
                  index += xingHeader.getSize() - 2;
               }
               catch (ParseException pex)
               {
                  // nothing to do.  there is simply no optional Xing header
               }
            }
         }
      }

      if (xingHeader == null)
      {  // see if there is a VBRI header
         if (bytes.length - index >= VBRIHeader.HEADER_MIN_SIZE)
         {
            try
            {
               this.vbriHeader = new VBRIHeader(bytes, index);
               index += vbriHeader.getSize();
            }
            catch (ParseException ex)
            {
               // nothing to do.  there is simply no optional VBRI header
            }
         }
      }
      else
      {  // see if the xing header is part of a LAME header
         if (bytes.length - index >= LAMEHeader.HEADER_MIN_SIZE)
         {
            try
            {
               lameHeader = new LAMEHeader(bytes, index);
               index += lameHeader.getSize();
            }
            catch (ParseException ex)
            {
               // nothing to do.  there is simply no optional LAME header
            }
         }
      }

      // parse the mpeg audio samples
      this.mpegAudioSamples = new MPEGAudioSamples(bytes, index);
   }

   /**
    * get the mpeg audio frame header.
    * @return the mpeg audio frame header.
    */
   public MPEGFrameHeader getMPEGFrameHeader()
   {
      return mpegFrameHeader;
   }

   /**
    * get the optional xing header.  The Xing header is found in {@link com.beaglebuddy.mpeg.enums.BitrateType variable bitrate} encoded .mp3 files with an id of "Xing".
    * It can sometimes be found in {@link com.beaglebuddy.mpeg.enums.BitrateType constant bitrate} encoded .mp3 files with an id of "Info".  The Xing header is mutually
    * exlusive with the VBRI header.  That is, if an Xing header is present, then a VBRI header will not be present.  Conversely, if a VBRI header is present, then an
    * Xing header will not be present.  Only one of the two may appear in the first mpeg audio frame of a VBR encoded .mp3 file.
    * @return the optional xing header.
    */
   public XingHeader getXingHeader()
   {
      return xingHeader;
   }

   /**
    * get the optional LAME header.  The LAME header, if present, is found in {@link com.beaglebuddy.mpeg.enums.BitrateType variable bitrate} encoded .mp3 files directly following
    * an Xing header.
    * @return the optional LAME header.
    */
   public LAMEHeader getLAMEHeader()
   {
      return lameHeader;
   }

   /**
    * get the optional VBRI header.  The VBRI header is found only in {@link com.beaglebuddy.mpeg.enums.BitrateType variable bitrate} encoded .mp3 files.
    * The VBRI header is mutually exlusive with the Xing header.  That is, if an VBRI header is present, then an Xing header will not be present.  Conversely, if a Xing header is present,
    * then a VBRI header will not be present.  Only one of the two may appear in the first mpeg audio frame of a VBR encoded .mp3 file.
    * @return the optional VBRI header.
    */
   public VBRIHeader getVBRIHeader()
   {
      return vbriHeader;
   }

   /**
    * get the offset (in bytes) within the .mp3 file where the MPEG audio frame occurred.
    * @return the offset (in bytes) within the .mp3 file where the MPEG audio frame starts.
    */
   public int getFilePosition()
   {
      return filePosition;
   }

   /**
    * set the byte offset within the .mp3 file where the MPEG audio frame occurred.
    * @param filePosition   the byte offset within the .mp3 file where the MPEG audio frame starts.
    */
   public void setFilePosition(int filePosition)
   {
      this.filePosition = filePosition;
   }

   /**
    * get the size (in bytes) of the MPEG audio frame.
    * @return the size (in bytes) of the MPEG audio frame.
    */
   public int getSize()
   {
      return mpegFrameHeader.getFrameSize();
   }

   /**
    * gets a string representation of the mpeg audio frame.
    * @return a string representation of the mpeg audio frame.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("mpeg audio frame\n");
      buffer.append("   file position......: " + filePosition     + "\n");
      buffer.append(mpegFrameHeader                               + "\n");
      buffer.append(mpegSideInformation                           + "\n");
      if (xingHeader != null) buffer.append(xingHeader            + "\n");
      if (vbriHeader != null) buffer.append(vbriHeader            + "\n");
      if (lameHeader != null) buffer.append(lameHeader            + "\n");
      buffer.append(mpegAudioSamples                                    );

      return buffer.toString();
   }
}
