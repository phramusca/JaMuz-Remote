package com.beaglebuddy.mpeg;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.mpeg.enums.BitrateType;
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
 * The Xing header is found in {@link com.beaglebuddy.mpeg.enums.BitrateType variable bit rate} encoded .mp3 files and is identified with an id of "Xing".
 * It is an optional header, and if present, will be found within the first MPEG audio frame directly following the MPEG side information.
 * It can sometimes also be found in {@link com.beaglebuddy.mpeg.enums.BitrateType constant bit rate} encoded .mp3 files, but in this case it will have an
 * id of "Info" in order to distinguish it from its variable bit rate encoded counterpart.
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
 * @see <a href="http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#XINGHeader" target="_blank">Xing Header Format</a>
 */
public class XingHeader
{
   // class mnemonics
   private static final int    HEADER_ID_SIZE                 = 4;        // size of the Xing header fields
   private static final int    HEADER_FLAGS_SIZE              = 4;
   private static final int    HEADER_NUM_FRAMES_SIZE         = 4;
   private static final int    HEADER_NUM_BYTES_SIZE          = 4;
   private static final int    HEADER_TABLE_OF_CONTENTS_SIZE  = 100;
   private static final int    HEADER_QUALITY_INDICATOR_SIZE  = 4;        /** Xing headers are at least   8 bytes long */
   public  static final int    HEADER_MIN_SIZE                = 8;        /** Xing headers are at most  120 bytes long */
   public  static final int    HEADER_MAX_SIZE                = 120;

   private static final byte   HEADER_FLAG_BYTE_INDEX         = 7;
   private static final byte   HEADER_NUM_FRAMES_FLAG         = 0x01;
   private static final byte   HEADER_NUM_BYTES_FLAG          = 0x02;
   private static final byte   HEADER_TABLE_OF_CONTENTS_FLAG  = 0x04;
   private static final byte   HEADER_QUALITY_INDICATOR_FLAG  = 0x08;

   private static final String HEADER_ID_INFO                 = "Info";  // Xing header id for constant bit rate encoded .mp3 files
   private static final String HEADER_ID_XING                 = "Xing";  // Xing header id for variable bit rate encoded .mp3 files



   // data members
   private   String      id;                             // which of the Xing header id's was found
   private   boolean     numFramesPresent;               // whether the optional number of frames  field is present
   private   boolean     numBytesPresent;                // whether the optional number of bytes   field is present
   private   boolean     tableOfContentsPresent;         // whether the optional table of contents field is present
   private   boolean     qualityIndicatorPresent;        // whether the optional quality indicator field is present
   private   int         numFrames;                      // number of mpeg audio frames in the .mp3 file
   private   int         numBytes;                       // number of bytes in the .mp3 file
   private   byte[]      tableOfContents;                // 100 table of content entries used for seeking other mpeg audio frames within the .mp3 file
   private   int         quality;                        // 0 = best, 100 = worst

   private   int         size;                           // size (in bytes) of the Xing header
   private   byte[]      buffer;                         // raw bytes read of the Xing header



   /**
    * default constructor.
    */
   public XingHeader()
   {
      id                      = HEADER_ID_XING;
      numFramesPresent        = false;
      numBytesPresent         = false;
      tableOfContentsPresent  = false;
      qualityIndicatorPresent = false;
      numFrames               = 0;
      numBytes                = 0;
      tableOfContents         = null;
      quality                 = 0;
      size                    = 0;
      buffer                  = null;
   }

   /**
    * constructor.
    * @param inputStream   input stream pointing to the Xing header in an mpeg audio frame in an .mp3 file.
    * @throws IOException     if there is an error while reading the Xing header from the .mp3 file.
    * @throws ParseException  if an invalid value is detected while parsing the Xing header's raw bytes.
    */
   public XingHeader(InputStream inputStream) throws IOException, ParseException
   {
      buffer = new byte[HEADER_MAX_SIZE];

      byte[] data = readBytes(inputStream, HEADER_ID_SIZE);

      id = new String(data);
      if (!id.equals(HEADER_ID_INFO) && !id.equals(HEADER_ID_XING))
         throw new ParseException("Invalid id, " + id + ", in Xing header.  It must be " + HEADER_ID_INFO + " or " + HEADER_ID_XING + ".", data);

      data = readBytes(inputStream, HEADER_FLAGS_SIZE);
      numFramesPresent        = (data[3] & HEADER_NUM_FRAMES_FLAG        ) != 0x00;
      numBytesPresent         = (data[3] & HEADER_NUM_BYTES_FLAG         ) != 0x00;
      tableOfContentsPresent  = (data[3] & HEADER_TABLE_OF_CONTENTS_FLAG ) != 0x00;
      qualityIndicatorPresent = (data[3] & HEADER_QUALITY_INDICATOR_FLAG ) != 0x00;

      if (numFramesPresent)
      {
         data = readBytes(inputStream, HEADER_NUM_FRAMES_SIZE);
         numFrames = Utility.bytesToInt(data);
      }

      if (numBytesPresent)
      {
         data = readBytes(inputStream, HEADER_NUM_BYTES_SIZE);
         numBytes = Utility.bytesToInt(data);
      }

      if (tableOfContentsPresent)
         tableOfContents = readBytes(inputStream, HEADER_TABLE_OF_CONTENTS_SIZE);

      if (qualityIndicatorPresent)
      {
         data = readBytes(inputStream, HEADER_QUALITY_INDICATOR_SIZE);
         quality = Utility.bytesToInt(data);
         if (quality < 0 || quality > 100)
         {
            data = new byte[size];
            throw new ParseException("Invalid quality value, " + quality + ", in the Xing header.  It must be 0 <= qaulity <= 100.", data);
         }
      }
      if (size != buffer.length)
      {
         // re-size and copy all the raw bytes read in from the .mp3 file into the buffer
         byte[] temp = new byte[size];
         System.arraycopy(buffer, 0, temp, 0, size);
         buffer = new byte[size];
         System.arraycopy(temp, 0, buffer, 0, size);
      }
   }

   /**
    * constructor.
    * @param bytes   the raw bytes of an MPEG audio frame.
    * @param index   the offset into the bytes of the MPEG audio frame where the Xing header begins.
    * @throws ParseException   if an invalid value is detected while parsing the Xing header's raw bytes.
    */
   public XingHeader(byte[] bytes, int index) throws ParseException
   {
      try
      {
         id = new String(bytes, index, HEADER_ID_SIZE);
         if (!id.equals(HEADER_ID_INFO) && !id.equals(HEADER_ID_XING))
            throw new ParseException("Invalid id, " + id + ", in Xing header.  It must be " + HEADER_ID_INFO + " or " + HEADER_ID_XING + ".", bytes);

         numFramesPresent        = (bytes[index + HEADER_FLAG_BYTE_INDEX] & HEADER_NUM_FRAMES_FLAG        ) != 0x00;
         numBytesPresent         = (bytes[index + HEADER_FLAG_BYTE_INDEX] & HEADER_NUM_BYTES_FLAG         ) != 0x00;
         tableOfContentsPresent  = (bytes[index + HEADER_FLAG_BYTE_INDEX] & HEADER_TABLE_OF_CONTENTS_FLAG ) != 0x00;
         qualityIndicatorPresent = (bytes[index + HEADER_FLAG_BYTE_INDEX] & HEADER_QUALITY_INDICATOR_FLAG ) != 0x00;

         size = HEADER_ID_SIZE + HEADER_FLAGS_SIZE;

         if (numFramesPresent)
         {
            byte[] data = new byte[HEADER_NUM_FRAMES_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            numFrames = Utility.bytesToInt(data);
            size += data.length;
         }

         if (numBytesPresent)
         {
            byte[] data = new byte[HEADER_NUM_BYTES_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            numBytes = Utility.bytesToInt(data);
            size += data.length;
         }

         if (tableOfContentsPresent)
         {
            tableOfContents = new byte[HEADER_TABLE_OF_CONTENTS_SIZE];
            System.arraycopy(bytes, index + size, tableOfContents, 0, tableOfContents.length);
            size += tableOfContents.length;
         }

         if (qualityIndicatorPresent)
         {
            byte[] data = new byte[HEADER_QUALITY_INDICATOR_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            quality = Utility.bytesToInt(data);
            size += data.length;
            if (quality < 0 || quality > 100)
               throw new ParseException("Invalid quality value, " + quality + ", in the Xing header.  It must be 0 <= qaulity <= 100.", new byte[0]);
         }
         buffer = new byte[size];
         System.arraycopy(bytes, index, buffer, 0, size);
      }
      catch (ArrayIndexOutOfBoundsException ex)
      {
         throw new ParseException("Insufficient bytes to parse the Xing header.", bytes);
      }
   }

   /**
    * reads in the specified number of raw bytes from the Xing header and increments the size variable which contains the total number of bytes read from the Xing header.
    * @return the raw bytes read from the Xing header.
    * @param inputStream   input stream pointing within the Xing header in an mpeg audio frame in an .mp3 file.
    * @param numBytes      number of bytes to read in from the Xing header.
    * @throws IOException   if there is an error while reading the raw bytes from Xing header.
    */
   private byte[] readBytes(InputStream inputStream, int numBytes) throws IOException
   {
      byte[] data = new byte[numBytes];

      if (inputStream.read(data) != data.length)
         throw new IOException("Unable to read the Xing header from the mpeg audio frame in the mp3 file.");
      System.arraycopy(data, 0, buffer, size, data.length);
      this.size += numBytes;

      return data;
   }

   /**
    * gets the size (in bytes) of the Xing header.
    * @return the size (in bytes) of the Xing header.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * gets the type of bit rate used to encode the .mp3 file.
    * @return the type of bit rate used to encode the .mp3 file.
    */
   public BitrateType getBitrateType()
   {
      return id.equals(HEADER_ID_INFO) ? BitrateType.CBR : BitrateType.VBR;
   }

   /**
    * gets the id, which is either "Info" or "Xing"
    * @return the Xing header id.
    */
   public String getId()
   {
      return id;
   }

   /**
    * gets whether the optional <i>number of frames</i> value is specified in the Xing header.  If so, then the {@link #getNumFrames()} method may be called.
    * @return whether the <i>number of frames</i> value is specified in the Xing header.
    */
   public boolean isNumFramesPresent()
   {
      return numFramesPresent;
   }

   /**
    * gets whether the optional <i>number of bytes</i> value is specified in the Xing header.  If so, then the {@link #getNumBytes()} method may be called.
    * @return whether the <i>number of bytes</i> value is specified in the Xing header.
    */
   public boolean isNumBytesPresent()
   {
      return numBytesPresent;
   }

   /**
    * gets whether the optional <i>table of contents</i> table is specified in the Xing header.  If so, then the {@link #getTableOfContents()} method may be called.
    * @return whether the <i>table of contents</i> value is specified in the Xing header.
    */
   public boolean isTableOfContentsPresent()
   {
      return tableOfContentsPresent;
   }

   /**
    * gets whether the optional <i>quality</i> value is specified in the Xing header.  If so, then the {@link #getQuality()} method may be called.
    * @return whether the <i>quality</i> value is specified in the Xing header.
    */
   public boolean isQualityIndicatorPresent()
   {
      return qualityIndicatorPresent;
   }

   /**
    * gets the optional <i>number of frames</i> value which specifies the total number of mpeg audio frames in the .mp3 file.
    * This method may only be called if the {@link #isNumFramesPresent()} method returns true.
    * @return the number of mpeg audio frames in the .mp3 file.
    * @throws IllegalStateException  if the method {@link #isNumFramesPresent()} does not return true.
    */
   public int getNumFrames() throws IllegalStateException
   {
      if (!numFramesPresent)
         throw new IllegalStateException("The method getNumFrames() may not be invoked as the number of frames present flag is false.");

      return numFrames;
   }

   /**
    * gets the optional <i>number of bytes</i> value which specifies the total number of bytes in the .mp3 file.
    * This method may only be called if the {@link #isNumBytesPresent()} method returns true.
    * @return the number of bytes in the .mp3 file.
    * @throws IllegalStateException  if the method {@link #isNumBytesPresent()} does not return true.
    */
   public int getNumBytes() throws IllegalStateException
   {
      if (!numBytesPresent)
         throw new IllegalStateException("The method getNumBytes() may not be invoked as the number of bytes present flag is false.");

      return numBytes;
   }

   /**
    * gets the optional <i>table of contents</i> which specifies entries used for seeking mpeg audio frames within the .mp3 file.
    * This method may only be called if the {@link #isTableOfContentsPresent()} method returns true.
    * @return the <i>table of contents</i> which specifies entries used for seeking mpeg audio frames within the .mp3 file.
    * @throws IllegalStateException  if the method {@link #isTableOfContentsPresent()} does not return true.
    */
   public byte[] getTableOfContents() throws IllegalStateException
   {
      if (!tableOfContentsPresent)
         throw new IllegalStateException("The method getTableOfContents() may not be invoked as the table of contents present flag is false.");

      return tableOfContents;
   }


   /**
    * Using the table of contents, this method returns the offset from the beginning of the .mp3 file where the desired mpeg audio frame is located.
    * In order to call this
    * @return the offset within the .mp3 file
    * @param entry  the desired entry in table of contents.  Since the table of contents has 100 entries, the entry must be 0 <= entry < 100.
    * @throws IllegalStateException     if the methods {@link #isTableOfContentsPresent()} and {@link #isNumBytesPresent()} do not both return true.
    * @throws IllegalArgumentException  if the specified entry is out of range, ie 0 <= n < 100.
    */
   public int getTOCOffset(int entry) throws IllegalStateException
   {
      if (!tableOfContentsPresent)
         throw new IllegalStateException("The method getTOCOffset() may not be invoked as the table of contents present flag is false.");
      if (!numBytesPresent)
         throw new IllegalStateException("The method getTOCOffset() may not be invoked as the number of bytes present flag is false.");

      return (int)(tableOfContents[entry] / 256.0 * numBytes);
   }

   /**
    * gets the optional <i>quality</i> value, which has a range of [0 - 100], where 0 is the best and 100 is the worst.
    * This method may only be called if the {@link #isQualityIndicatorPresent()} method returns true.
    * @return the <i>quality</i> of the mpeg audio frame.
    * @throws IllegalStateException   if the method {@link #isQualityIndicatorPresent()} does not return true.
    */
   public int getQuality() throws IllegalStateException
   {
      if (!qualityIndicatorPresent)
         throw new IllegalStateException("The method getQuality() may not be invoked as the quality indicator present flag is false.");

      return quality;
   }

   /**
    * get a string representation of the Xing header.
    * @return a string representation of the Xing header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("Xing header" + "\n");
      buffer.append("   size.....................: " + size                         + " bytes\n");
      buffer.append("   bytes....................: " + Utility.hex(this.buffer, 30) + "\n");
      buffer.append("   id.......................: " + id                           + "\n");
      buffer.append("   num frames present.......: " + numFramesPresent             + "\n");
      buffer.append("   num bytes present........: " + numBytesPresent              + "\n");
      buffer.append("   table of contents present: " + tableOfContentsPresent       + "\n");
      buffer.append("   quality present..........: " + qualityIndicatorPresent              );
      if (numFramesPresent)        buffer.append("\n   num frames...............: " + numFrames);
      if (numBytesPresent )        buffer.append("\n   num bytes................: " + numBytes );
      if (qualityIndicatorPresent) buffer.append("\n   quality..................: " + quality  );
      if (tableOfContentsPresent)  buffer.append("\n   num TOC entries..........: " + tableOfContents.length);
      if (tableOfContentsPresent)  buffer.append("\n   TOC entries..............: " + Utility.hex(tableOfContents, 30));

      return buffer.toString();
   }
}
