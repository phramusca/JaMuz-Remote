package com.beaglebuddy.mpeg;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.util.Utility;
import com.beaglebuddy.mpeg.enums.ChannelMode;
import com.beaglebuddy.mpeg.enums.Emphasis;
import com.beaglebuddy.mpeg.enums.Layer;
import com.beaglebuddy.mpeg.enums.MPEGVersion;



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
 * The audio portion of an .mp3 file is stored in MPEG audio frames as shown below. Each {@link MPEGFrame MPEG audio frame} begins with an MPEG frame header.
 * The header has a fixed length of 4 bytes, and optionally contains a 2 byte CRC directly following it, before the {@link MPEGSideInformation side information}.
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
 * @see <a href="http://www.iso.org/iso/catalogue_detail.htm?csnumber=22412"                            target="_blank">ISO / IEC 11172-3</a>
 * @see <a href="http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#MPEGAudioFrameHeader" target="_blank">MPEG Audio Frame Header</a>
 * @see <a href="http://mpgedit.org/mpgedit/mpeg_format/mpeghdr.htm"                                    target="_blank">MPEG Basics</a>
 * @see <a href="http://www.mp3-tech.org/programmer/frame_header.html"                                  target="_blank">MPEG Audio Layer I/II/III frame header</a>
 */
public class MPEGFrameHeader
{
   // class mnemonics
   private static final int FRAME_HEADER_WITH_CRC_SIZE = 6;  /** mpeg audio frame headers are 4 bytes (32 bits) in length */
   public  static final int FRAME_HEADER_SIZE          = 4;


   // data members
   private byte[]      buffer;                             // raw bytes read in from the .mp3 file
   private MPEGVersion mPEGVersion;                        // should be MPEG I for
   private Layer       layer;
   private boolean     protectedByCRC;                     // whether their is a 2 byte CRC at the end of the mpeg frame header
   private byte        bitrateIndex;                       // index into a table of bit rates   - see setBitrate()
   private byte        samplingRateFrequencyIndex;         // index into a table of frequencies - see setFrequency()
   private boolean     framePadded;                        // whether the mpeg audio frame has any padding bytes at the end of the audio samples
   private boolean     privateBit;                         //
   private ChannelMode channelMode;                        // mono or stereo
   private byte        modeExtension;                      //
   private boolean     copyrighted;                        // whether the .mp3 is copyrighted
   private boolean     originalMedia;                      //
   private Emphasis    emphasis;                           //

   private int         bitrate;                            // size of the corresponding mpeg audio frame
   private int         frequency;                          // frequency at which the .mp3 file was sampled
   private int         frameSize;                          // size of the corresponding mpeg audio frame
   private int         numSamples;                         // number of audio samples stored in the frame
   private int         sideInfoSize;                       // number of bytes in the side information block directly following the mpeg frame header.
   private byte[]      crc;                                // 2 byte CRC


   /**
    * default constructor.
    */
   public MPEGFrameHeader()
   {
      this.buffer                     = new byte[FRAME_HEADER_SIZE];
      this.mPEGVersion                = MPEGVersion.MPEG_2;
      this.layer                      = Layer.III;
      this.protectedByCRC             = false;
      this.bitrateIndex               = (byte)0;
      this.samplingRateFrequencyIndex = (byte)0;
      this.framePadded                = false;
      this.privateBit                 = false;
      this.channelMode                = ChannelMode.STEREO;
      this.modeExtension              = (byte)0;
      this.copyrighted                = true;
      this.originalMedia              = true;
      this.emphasis                   = Emphasis.NONE;
      this.bitrate                    = 0;
      this.frequency                  = 0;
      this.frameSize                  = 0;
      this.numSamples                 = 0;
      this.sideInfoSize               = 0;
      this.crc                        = null;
   }

   /**
    * constructor used for parsing existing .mp3 files.
    * This constructor is called when reading in an existing mpeg frame header from an .mp3 file.
    * It parses the raw binary data of the mpeg frame header and stores the values in the corresponding data members.
    * @param inputStream   input stream to read in the mpeg audio frame from the binary .mp3 file.
    * @throws IOException      if there is an error while reading the mpeg frame header.
    * @throws ParseException   if an invalid value is detected while parsing the mpeg frame header's raw bytes.
    * @see <a href="http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#MPEGAudioFrameHeader">MPEG Audio Frame Header</a>
    * @see <a href="http://oreilly.com/catalog/mp3/chapter/ch02.html#71109">The Anatomy of an MP3 File</a>
    */
   public MPEGFrameHeader(InputStream inputStream) throws IOException, ParseException
   {
      this(readBytes(inputStream), inputStream);
   }

   /**
    * constructor used for parsing existing .mp3 files.
    * This constructor is called when searching for the 1st mpeg audio frame header from an .mp3 file or when re-synching the audio stream.
    * It parses the raw binary data of the mpeg frame header and stores the values in the corresponding data members.
    * @param bytes         bytes read in from the previous attempt to find the mpeg frame header.
    * @param inputStream   input stream to read in the mpeg audio frame CRC from the binary .mp3 file.
    * @throws IOException      if there is an error while reading the mpeg frame header.
    * @throws ParseException   if an invalid value is detected while parsing the mpeg frame header's raw bytes.
    * @see <a href="http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header#MPEGAudioFrameHeader">MPEG Audio Frame Header</a>
    * @see <a href="http://oreilly.com/catalog/mp3/chapter/ch02.html#71109">The Anatomy of an MP3 File</a>
    */
   public MPEGFrameHeader(byte[] bytes, InputStream inputStream) throws IOException, ParseException
   {
      // make sure the right number if bytes have been read in
      if (bytes == null || (bytes.length != FRAME_HEADER_SIZE && bytes.length != FRAME_HEADER_WITH_CRC_SIZE))
         throw new IllegalArgumentException("Invalid number of bytes, " + (bytes == null ? 0 : bytes.length) + ", passed in to MPEGFrameHeader().");

      // an mpeg audio frame can, in principle, start at any byte within the audio stream
      if (bytes[0] != (byte)0xFF || (bytes[1] & (byte)0xE0) != (byte)0xE0)
         throw new ParseException("Invalid synch bytes in mpeg audio frame header", bytes);

      this.buffer = new byte[FRAME_HEADER_SIZE];
      buffer[0] = bytes[0];
      buffer[1] = bytes[1];
      buffer[2] = bytes[2];
      buffer[3] = bytes[3];

      byte data                  = (byte)((buffer[1] & 0x18) >> 3);
      mPEGVersion                = MPEGVersion.valueOf(data);
      data                       = (byte)((buffer[1] & 0x06) >> 1);
      layer                      = Layer.valueOf(data);
      protectedByCRC             = (byte) (buffer[1] & 0x01) == 0x00;
      bitrateIndex               = (byte)((buffer[2] & 0xF0) >> 4);
      samplingRateFrequencyIndex = (byte)((buffer[2] & 0x0C) >> 2);
      framePadded                = (byte) (buffer[2] & 0x02) != 0x00;
      privateBit                 = (byte) (buffer[2] & 0x01) != 0x00;
      data                       = (byte)((buffer[3] & 0xC0) >> 6);
      channelMode                = ChannelMode.valueOf(data);
      modeExtension              = (byte)((buffer[3] & 0x30) >> 4);
      copyrighted                = (byte) (buffer[3] & 0x08) != 0x00;
      originalMedia              = (byte) (buffer[3] & 0x04) != 0x00;
      data                       = (byte) (buffer[3] & 0x03);
      emphasis                   = Emphasis.valueOf(data);

      // verify that we have indeed found a valid mpeg audio frame header, and that we have not accidentally stumbled upon some audio bytes whose
      // raw bytes happen to match that of the synch bytes
      if (mPEGVersion == MPEGVersion.MPEG_RESERVED)
         throw new ParseException("Invalid MPEG version, " + mPEGVersion     + ", found in the mpeg audio frame header.", bytes);
      if (layer == Layer.RESERVED)
         throw new ParseException("Invalid layer, "        + layer           + ", found in the mpeg audio frame header.", bytes);
      if (emphasis == Emphasis.RESERVED)
         throw new ParseException("Invalid emphasis, "     + emphasis        + ", found in the mpeg audio frame header.", bytes);

      setBitrate  ();
      setFrequency();

      if (getBitrate() == 0 || getBitrate() == -1)
         throw new ParseException("Invalid bit rate "      + getBitrate()    + ", found in the mpeg audio frame header.", bytes);
      if (getFrequency() == -1)
         throw new ParseException("Invalid frequency "     + getFrequency()  + ", found in the mpeg audio frame header.", bytes);

      setFrameSize   ();
      setNumSamples  ();
      setSideInfoSize();

      if (isProtectedByCRC())
      {
         if (bytes.length == FRAME_HEADER_SIZE)
             setCRC(inputStream);
         else
             setCRC(bytes[4], bytes[5]);
      }
   }

   /**
    * read in the bytes of the mpeg audio frame header from an .mp3 file.
    * @throws IOException      if there is an error while reading the mpeg audio frame header.
    * @throws ParseException   if the end of .mp3 file has been reached and thus there are no bytes to be parsed.
    */
   private static byte[] readBytes(InputStream inputStream) throws IOException, ParseException
   {
      byte[] bytes        = new byte[FRAME_HEADER_SIZE];
      int    numBytesRead = inputStream.read(bytes);

      if (numBytesRead != bytes.length)
      {
         if (numBytesRead == -1)
            throw new ParseException("EOF");
         if (inputStream.read() == -1)
            throw new ParseException("EOF", bytes);
         throw new IOException("Unable to read mpeg audio frame header.");
      }
      return bytes;
   }

   /**
    * gets the size of the mpeg audio frame header and includes the optional CRC if present.
    * @return the size of the mpeg audio frame header including the optional CRC if present.
    */
   public int getSize()
   {
      return MPEGFrameHeader.FRAME_HEADER_SIZE + (isProtectedByCRC() ? getCRC().length : 0);
   }

   /**
    * gets the MPEG version of this .mp3 file.
    * @return the MPEG version of this .mp3 file.
    */
   public MPEGVersion getMPEGVersion()
   {
      return mPEGVersion;
   }

   /**
    * gets the MPEG layer of this .mp3 file.
    * @return the MPEG layer of this .mp3 file.
    */
   public Layer getLayer()
   {
      return layer;
   }

   /**
    * gets the Codec used to encode the .mp3 file.
    * @return the Codec used to encode the .mp3 file.
    */
   public String getCodec()
   {
      return mPEGVersion + " " + layer;
   }

   /**
    * gets the bit rate (in kilobits per second - kbps), which specifies how many kb are used to represent the audio data for each second of playback.
    * @return the bite rate in kbit/s for this .mpeg audio frame.  A value of 0 means free format, while a value of -1 means an invalid value.
    *         All other values are actual valid bit rates.
    */
   public int getBitrate()
   {
      return bitrate;
   }

   /**
    * sets the bit rate (in kilobits per second - kbps), which specifies how many kb are used to represent the audio data for each second of playback.
    * Thus, a higher bit rate means that more information can be stored for each second of audio, and hence the sound quality will be higher for higher
    * bit rates.
    */
   private void setBitrate()
   {
      int[] v1l1    = {0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, -1},  // MPEG 1, layer I
            v1l2    = {0, 32, 48, 56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, 384, -1},  // MPEG 1, layer II
            v1l3    = {0, 32, 40, 48,  56,  64,  80,  96, 112, 128, 160, 192, 224, 256, 320, -1},  // MPEG 1, layer III
            v2l1    = {0, 32, 48, 56,  64,  80,  96, 112, 128, 144, 160, 176, 192, 224, 256, -1},  // MPEG 2, layer I
            v2l2    = {0,  8, 16, 24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, -1},  // MPEG 2, layer II
            v2l3    = {0,  8, 16, 24,  32,  40,  48,  56,  64,  80,  96, 112, 128, 144, 160, -1};  // MPEG 2, layer III

      switch (mPEGVersion)
      {
         case MPEG_1:
              switch (layer)
              {
                 case I:
                      bitrate = v1l1[bitrateIndex];
                 break;
                 case II:
                      bitrate = v1l2[bitrateIndex];
                 break;
                 case III:
                      bitrate = v1l3[bitrateIndex];
                 break;
                 case RESERVED:
                      bitrate = -1;
                 break;
              }
         break;
         case MPEG_2:
         case MPEG_25:
              switch (layer)
              {
                 case I:
                      bitrate = v2l1[bitrateIndex];
                 break;
                 case II:
                      bitrate = v2l2[bitrateIndex];
                 break;
                 case III:
                      bitrate = v2l3[bitrateIndex];
                 break;
                 case RESERVED:
                      bitrate = -1;
                 break;
              }
         break;
         case MPEG_RESERVED:
              bitrate = -1;
         break;
      }
   }

   /**
    * gets the frequency (in herz - hz), which specifies how many times per second the audio is sampled and stored as a number in the .mp3 file.
    * CD audio is sampled at 44.1 khz, which means 44,100 samples per second.
    * @return the sampling frequency (in hz) used to convert the analog audio to a digital .mp3 file.  A value of -1 indicates an invalid value due to the MPEG version
    */
   public int getFrequency()
   {
      return frequency;
   }

   /**
    * sets the frequency (in herz - hz), which specifies how many times per second the audio is sampled and stored as a number in the .mp3 file.
    * CD audio is sampled at 44.1 khz, which means 44,100 samples per second.
    */
   private void setFrequency()
   {
      int[] v1        = {44100, 48000, 32000, -1},
            v2        = {22050, 24000, 16000, -1},
            v25       = {11025, 12000,  8000, -1};

      switch (mPEGVersion)
      {
         case MPEG_1:
              frequency = v1[samplingRateFrequencyIndex];
         break;
         case MPEG_2:
              frequency = v2[samplingRateFrequencyIndex];
         break;
         case MPEG_25:
              frequency = v25[samplingRateFrequencyIndex];
         break;
         case MPEG_RESERVED:
              frequency = -1;
         break;
      }
   }

   /**
    * gets the channel mode (mono or stereo) of this .mp3 file.
    * @return the channel mode (mono or stereo) of this .mp3 file.
    */
   public ChannelMode getChannelMode()
   {
      return channelMode;
   }

   /**
    * sets the description of the mode extension of this mpeg audio frame.
    * @return a description of the mode extension of this mpeg audio frame.
    */
   public String getModeExtension()
   {
      String   mode = null;
      String[] l1l2 = {"bands 4 - 31", "bands 8 - 31", "bands 12 - 31", "bands 16 - 31"},
               l3   = {"intensity stereo: off, MS stereo: off", "intensity stereo: on, MS stereo: off", "intensity stereo: off, MS stereo: on", "intensity stereo: on, MS stereo: on"};

      switch (layer)
      {
         case I:
         case II:
              mode = l1l2[modeExtension];
         break;
         case III:
              mode = l3  [modeExtension];
         break;
         case RESERVED:
              mode = null;
         break;
      }
      return mode;
   }

   /**
    * gets whether this mpeg audio frame has a CRC.
    * @return whether this mpeg audio frame has a CRC or not.
    */
   public boolean isProtectedByCRC()
   {
      return protectedByCRC;
   }
   /**
    * gets whether this audio is copyrighted.
    * @return whether this audio is copyrighted or not.
    */
   public boolean isCopyrighted()
   {
      return copyrighted;
   }

   /**
    * gets whether this .mp3 file was created from the original media.
    * @return whether this .mp3 file was created from the original media or whether it is a copy.
    */
   public boolean isOriginalMedia()
   {
      return originalMedia;
   }

   /**
    * gets the number of audio samples stored in the mpeg audio frame.
    * @return the number of audio samples stored in the mpeg audio frame.
    */
   public int getNumSamples()
   {
      return numSamples;
   }

   /**
    * sets the number of audio samples stored in the mpeg audio frame.
    */
   private void setNumSamples()
   {
      if (layer == Layer.I)
         numSamples = 384;
      else if (layer == Layer.II)
         numSamples = 1152;
      else if (layer == Layer.III)
         numSamples = (mPEGVersion == MPEGVersion.MPEG_1 ? 1152 : 576);
   }

   /**
    * gets the size (in bytes) of the mpeg audio frame.
    * @return the size of the mpeg audio frame. (in bytes).
    */
   public int getFrameSize()
   {
      return frameSize;
   }

   /**
    * calculate the size of the mpeg audio frame.
    */
   private void setFrameSize()
   {
      int bitrate    = getBitrate();
      int padding    = framePadded ? 1 : 0;
      int sampleRate = getFrequency();

      switch (layer)
      {
         case I:
              frameSize = (12 * bitrate * 1000/ sampleRate + padding)*4;
         break;
         case II:
         case III:     // num samples / 8 = 1152 / 8 = 144
              frameSize = 144 * bitrate * 1000/ sampleRate + padding;
         break;
         case RESERVED:
              frameSize = 72 * bitrate * 1000/ sampleRate + padding;
         break;
      }
   }

   /**
    * get the size (in bytes) of the {@link MPEGSideInformation side information} that directly follows the MPEG frame header in the {@link MPEGFrame mpeg audio frame}.
    * @return the size (in bytes) of the side information that directly follows the MPEG frame header in the MPEG audio frame.
    */
   public int getSideInfoSize()
   {
      return sideInfoSize;
   }

   /**
    * sets the size (in bytes) of the side information that directly follows the MPEG frame header in the mpeg audio frame.
    */
   private void setSideInfoSize()
   {
      if (mPEGVersion == MPEGVersion.MPEG_1)
         sideInfoSize = channelMode == ChannelMode.SINGLE_CHANNEL ? 17 : 32;
      else
         sideInfoSize = channelMode == ChannelMode.SINGLE_CHANNEL ? 9 : 17;
   }


   /**
    * gets the optional CRC for the mpeg audio frame.
    * @return the CRC for the mpeg audio frame if present, and null otherwise.
    */
   public byte[] getCRC()
   {
      return crc;
   }

   /**
    * read in the two byte CRC directlty following the mpeg audio frame header.
    * @throws IOException  if the CRC can not be read from the .mp3 file.
    */
   private void setCRC(InputStream inputStream) throws IOException
   {
      crc = new byte[2];

      if (inputStream.read(crc) != crc.length)
         throw new IOException("Error reading the CRC following the mpeg audio frame header.");
   }

   /**
    * use the last two bytes that have already been read in directly following the mpeg audio frame header for the CRC.
    * @throws IOException  if the CRC can not be read from the .mp3 file.
    */
   private void setCRC(byte crc1, byte crc2)
   {
      crc = new byte[2];

      crc[0] = crc1;
      crc[1] = crc2;
   }

   /**
    * gets a string representation of the mpeg audio frame header.
    * @return a string representation of the mpeg audio frame header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("mpeg audio frame header\n");
      buffer.append("   size............: " + this.buffer.length           + "\n");
      buffer.append("   bytes...........: " + Utility.hex(this.buffer, 17) + "\n");
      buffer.append("   MPEG version....: " + mPEGVersion                  + "\n");
      buffer.append("   MPEG layer......: " + layer                        + "\n");
      buffer.append("   protected.by CRC: " + protectedByCRC               + "\n");
      buffer.append("   bit rate........: " + bitrate                      + " kbit/s\n");
      buffer.append("   frequency.......: " + frequency                    + " hz\n");
      buffer.append("   frame is padded.: " + framePadded                  + "\n");
      buffer.append("   private bit.....: " + privateBit                   + "\n");
      buffer.append("   channel mode....: " + channelMode                  + "\n");
      buffer.append("   mode extension..: " + getModeExtension()           + "\n");
      buffer.append("   copyrighted.....: " + copyrighted                  + "\n");
      buffer.append("   original media..: " + originalMedia                + "\n");
      buffer.append("   emphasis........: " + emphasis                     + "\n");
      buffer.append("   frame size......: " + frameSize                    + " bytes\n");
      buffer.append("   num samples.....: " + numSamples                   + "\n");
      buffer.append("   side info size..: " + sideInfoSize                 + " bytes");

      return buffer.toString();
   }
}
