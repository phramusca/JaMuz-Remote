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
 * A VBRI header is found in {@link com.beaglebuddy.mpeg.enums.BitrateType variable bit rate} encoded .mp3 files that were encoded using the Fraunhofer encoder.
 * The VBRI header is an optional header, and if present, will be found within the first MPEG audio frame directly following the MPEG side information.  It is mutually
 * exclusive with the {@link XingHeader Xing} and {@link LAMEHeader LAME} headers.  That is, if an Xing or Xing/LAME header is found in the first MPEG audio frame, then
 * a VBRI header can not occur and vice versa.  If a VBRI header is found in the first MPEG audio frame, then neither an Xing or LAME header can be present.
 * The format of a variable bit rate encoded .mp3 file is shown below.
 * <br/><br/>
 * <img src="../../../resources/mpeg_frame_VBR_format.jpg" width="500" height="580" alt="VBR encoded mp3 file format" usemap="#vbr_map"/>
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
 * @see <a href="http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#VBRIHeader" target="_blank">VBRI Header</a>
 */
public class VBRIHeader
{
   // class mnemonics
   private static final int    HEADER_ID_SIZE                     = 4;        // size of the VBRI header fields
   private static final int    HEADER_VERSION_SIZE                = 2;
   private static final int    HEADER_DELAY_SIZE                  = 2;
   private static final int    HEADER_QUALITY_INDICATOR_SIZE      = 2;
   private static final int    HEADER_NUM_BYTES_SIZE              = 4;
   private static final int    HEADER_NUM_FRAMES_SIZE             = 4;
   private static final int    HEADER_TOC_NUM_ENTRIES_SIZE        = 2;
   private static final int    HEADER_TOC_ENTRY_SCALE_FACTOR_SIZE = 2;
   private static final int    HEADER_TOC_ENTRY_SIZE              = 2;
   private static final int    HEADER_TOC_ENTRY_NUM_FRAMES_SIZE   = 2;
   private static final int    HEADER_FIXED_SIZE                  = HEADER_ID_SIZE                     + HEADER_VERSION_SIZE                + HEADER_DELAY_SIZE       +
                                                                    HEADER_QUALITY_INDICATOR_SIZE      + HEADER_NUM_BYTES_SIZE              + HEADER_NUM_FRAMES_SIZE  +
                                                                    HEADER_TOC_NUM_ENTRIES_SIZE        + HEADER_TOC_ENTRY_SCALE_FACTOR_SIZE + HEADER_TOC_ENTRY_SIZE   +
                                                                    HEADER_TOC_ENTRY_NUM_FRAMES_SIZE;
                                                                    /** VBRI headers are at least 26 bytes long */
   public  static final int    HEADER_MIN_SIZE                    = HEADER_FIXED_SIZE;
   private static final String HEADER_ID_VBRI                     = "VBRI";


   // data members
   private String   id;                             // header id always "VBRI"
   private int      version;                        // VBRI version
   private int      delay;                          // number of samples added to the beginning of the audio data
   private int      quality;                        // 0 = best, 100 = worst
   private int      numBytes;                       // number of bytes in the .mp3 file
   private int      numFrames;                      // number of mpeg audio frames in the .mp3 file
   private int      numTOCEntries;                  // number of entries in the table of contents
   private int      scaleFactorTOCEntry;            // scale factor for the entries in the table of contents
   private int      sizeTOCEntry;                   // size of each entry in the table of contents
   private int      numFramesPerTOCEntry;           // number of frames per entry in the table of contents
   private byte[][] tableOfContents;                // table of content entries used for seeking other mpeg audio frames within the .mp3 file
   private int      size;                           // size (in bytes) of the variable length VBRI header




   /**
    * default constructor.
    */
   public VBRIHeader()
   {
      id                   = HEADER_ID_VBRI;
      version              = 0;
      delay                = 0;
      quality              = 0;
      numBytes             = 0;
      numFrames            = 0;
      numTOCEntries        = 0;
      scaleFactorTOCEntry  = 0;
      numFramesPerTOCEntry = 0;
      tableOfContents      = null;
      size                 = 0;
   }

   /**
    * constructor.
    * @param bytes   the raw bytes of an MPEG audio frame.
    * @param index   the offset into the bytes of the MPEG audio frame where the VBRI header begins.
    * @throws ParseException  if an invalid value is detected while parsing the VBRI header's raw bytes.
    */
   public VBRIHeader(byte[] bytes, int index) throws ParseException
   {
      try
      {
         id = new String(bytes, index, HEADER_ID_SIZE);
         if (!id.equals(HEADER_ID_VBRI))
            throw new ParseException("Invalid id, " + id + ", in VBRI header.  It must be " + HEADER_ID_VBRI + ".");
         size += id.length();

         byte[] data = new byte[HEADER_VERSION_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         version = Utility.bytesToShort(data);
         size += data.length;

         data = new byte[HEADER_DELAY_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         delay = Utility.bytesToShort(data);
         size += data.length;

         data = new byte[HEADER_QUALITY_INDICATOR_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         quality = Utility.bytesToShort(data);
         size += data.length;

         data = new byte[HEADER_NUM_BYTES_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         numBytes = Utility.bytesToInt(data);
         size += data.length;

         data = new byte[HEADER_NUM_FRAMES_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         numFrames = Utility.bytesToInt(data);
         size += data.length;

         data = new byte[HEADER_TOC_NUM_ENTRIES_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         numTOCEntries = Utility.bytesToShort(data);
         size += data.length;

         data = new byte[HEADER_TOC_ENTRY_SCALE_FACTOR_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         scaleFactorTOCEntry = Utility.bytesToShort(data);
         size += data.length;

         data = new byte[HEADER_TOC_ENTRY_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         sizeTOCEntry = Utility.bytesToShort(data);
         size += data.length;

         data = new byte[HEADER_TOC_ENTRY_NUM_FRAMES_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         numFramesPerTOCEntry = Utility.bytesToShort(data);
         size += data.length;

         tableOfContents = new byte[numTOCEntries][];
         for(int i=0; i<numTOCEntries; ++i)
         {
            data = new byte[sizeTOCEntry];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            tableOfContents[i] = data;
            size += data.length;
         }
         if (size != HEADER_FIXED_SIZE + numTOCEntries * sizeTOCEntry)
           throw new ParseException("Something went wrong parsing the VBRI header.  Fix:  TODO");
      }
      catch (ArrayIndexOutOfBoundsException ex)
      {
         throw new ParseException("Insufficient bytes to parse the VBRI header.", bytes);
      }
   }

   /**
    * constructor.
    * @param bytes          bytes read in from the previous attempt to find the Xing header.
    * @param inputStream    input stream pointing to the VBRI header in an mpeg audio frame in an .mp3 file.
    * @throws IOException     if there is an error while reading the VBRI header from the .mp3 file.
    * @throws ParseException  if an invalid value is detected while parsing the VBRI header's raw bytes.
    */
   public VBRIHeader(byte[] bytes, InputStream inputStream) throws IOException, ParseException
   {
      String errorMessage = "Unable to read the VBRI header from the mpeg audio frame in the mp3 file.";

      if (bytes.length != HEADER_ID_SIZE)
         throw new ParseException("Invalid number of id bytes in VBRIHeader constructor");

      id = new String(bytes);
      if (!id.equals(HEADER_ID_VBRI))
         throw new ParseException("Invalid id, " + id + ", in VBRI header.  It must be " + HEADER_ID_VBRI + ".");

      byte[] data = new byte[HEADER_VERSION_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      version = Utility.bytesToShort(data);

      data = new byte[HEADER_DELAY_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      delay = Utility.bytesToShort(data);

      data = new byte[HEADER_QUALITY_INDICATOR_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      quality = Utility.bytesToShort(data);

      data = new byte[HEADER_NUM_BYTES_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      numBytes = Utility.bytesToInt(data);

      data = new byte[HEADER_NUM_FRAMES_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      numFrames = Utility.bytesToInt(data);

      data = new byte[HEADER_TOC_NUM_ENTRIES_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      numTOCEntries = Utility.bytesToShort(data);

      data = new byte[HEADER_TOC_ENTRY_SCALE_FACTOR_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      scaleFactorTOCEntry = Utility.bytesToShort(data);

      data = new byte[HEADER_TOC_ENTRY_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      sizeTOCEntry = Utility.bytesToShort(data);

      data = new byte[HEADER_TOC_ENTRY_NUM_FRAMES_SIZE];
      if (inputStream.read(data) != data.length)
         throw new IOException(errorMessage);
      numFramesPerTOCEntry = Utility.bytesToShort(data);

      tableOfContents = new byte[numTOCEntries][];
      for(int i=0; i<numTOCEntries; ++i)
      {
         data = new byte[sizeTOCEntry];
         if (inputStream.read(data) != data.length)
            throw new IOException(errorMessage);
         tableOfContents[i] = data;
      }
      size = HEADER_FIXED_SIZE + numTOCEntries * sizeTOCEntry;
   }

   /**
    * gets the size (in bytes) of the VBRI header.
    * @return the size (in bytes) of the VBRI header.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * gets the VBRI header id which is always "VBRI".
    * @return the VBRI header id.
    */
   public String getId()
   {
      return id;
   }

   /**
    * gets the VBRI header version.
    * @return the VBRI header version.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * gets the delay?
    * @return the delay of the mpeg audio frame.
    */
   public int getDelay()
   {
      return delay;
   }

   /**
    * gets the quality value, which has a range of [0 - 100], where 0 is the best and 100 is the worst.
    * @return the quality of the mpeg audio frame.
    */
   public int getQuality()
   {
      return quality;
   }

   /**
    * gets the number of bytes in the .mp3 file.
    * @return the number of bytes in the .mp3 file.
    */
   public int getNumBytes()
   {
      return numBytes;
   }

   /**
    * gets the number of mpeg audio frames in the .mp3 file.
    * @return the number of mpeg audio frames in the .mp3 file.
    */
   public int getNumFrames()
   {
      return numFrames;
   }

   /**
    * gets the number of entries in the table of contents.
    * @return the number of entries in the table of contents.
    */
   public int getNumTOCEntries()
   {
      return numTOCEntries;
   }

   /**
    * gets the scale factor for the entries in the table of contents.
    * @return the scale factor for the entries in the table of contents.
    */
   public int getTOCEntryScaleFactor()
   {
      return scaleFactorTOCEntry;
   }

   /**
    * gets the size of each entry in the table of contents.
    * @return the size of each entry in the table of contents.
    */
   public int getTOCEntrySize()
   {
      return sizeTOCEntry;
   }

   /**
    * gets the number of frames per entry in the table of contents.
    * @return the number of frames per entry in the table of contents.
    */
   public int getTOCEntryNumFrames()
   {
      return numFramesPerTOCEntry;
   }

   /**
    * gets the table of contents, which specifies entries used for seeking mpeg audio frames within the .mp3 file.
    * @return the table of contents used for seeking mpeg audio frames within the .mp3 file.
    */
   public byte[][] getTableOfContents()
   {
      return tableOfContents;
   }

   /**
    * @return a string representation of the VBRI header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("VBRI header" + "\n");
      buffer.append("size..................: " + size                 + "\n");
      buffer.append("id....................: " + id                   + "\n");
      buffer.append("version...............: " + version              + "\n");
      buffer.append("delay.................: " + delay                + "\n");
      buffer.append("quality...............: " + quality              + "\n");
      buffer.append("num bytes.............: " + numBytes             + "\n");
      buffer.append("num frames............: " + numFrames            + "\n");
      buffer.append("num TOC entries.......: " + numTOCEntries        + "\n");
      buffer.append("TOC entry scale factor: " + scaleFactorTOCEntry  + "\n");
      buffer.append("TOC entry size........: " + sizeTOCEntry         + "\n");
      buffer.append("TOC entry frames......: " + numFramesPerTOCEntry       );

      return buffer.toString();
   }
}
