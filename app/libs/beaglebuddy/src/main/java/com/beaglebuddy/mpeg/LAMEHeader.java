package com.beaglebuddy.mpeg;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.mpeg.enums.GainOriginator;
import com.beaglebuddy.mpeg.enums.GainType;
import com.beaglebuddy.mpeg.enums.SourceFrequency;
import com.beaglebuddy.mpeg.enums.StereoMode;
import com.beaglebuddy.mpeg.enums.SurroundInfo;
import com.beaglebuddy.mpeg.enums.VBRMethod;
import com.beaglebuddy.mpeg.pojo.ReplayGain;
import com.beaglebuddy.mpeg.pojo.Version;
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
 * The LAME header is an extension of the {@link XingHeader Xing} header and is found in {@link com.beaglebuddy.mpeg.enums.BitrateType variable bit rate} encoded .mp3 files.
 * It is an optional header, and if present, will be found within the first MPEG audio frame directly following the {@link XingHeader Xing header}.
 * </p>
 * <p class="beaglebuddy">
 * When the LAME header was first added in the <a href="http://lame.sourceforge.net/">LAME</a> 3.12 encoder, it consisted of 20 bytes, with only the first 9 bytes being used
 * for a version string. In LAME 3.90, the lame tag was expanded to include additional information, and expanded again in version 3.94.  It is in version 3.94 that the Lame
 * encoder started adding the Xing/LAME headers to {@link com.beaglebuddy.mpeg.enums.BitrateType constant bit rate} encoded .mp3s, albeit with the {@link XingHeader Xing header}
 * id being set to "Info" instead of "Xing".  All of this is explained in the <a target="_blank" href="http://wiki.hydrogenaud.io/index.php?title=LAME#VBR_header_and_LAME_tag">VBR header and LAME tag</a>
 * article.
 * </p>
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
 * @see <a href="http://gabriel.mp3-tech.org/mp3infotag.html#versionstring"                       target="_blank">official LAME Header Format</a>
 * @see <a href="http://multimedia.cx/mp3extensions.txt"                                          target="_blank">LAME Header Format</a>
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=MP3#VBRI.2C_XING.2C_and_LAME_headers" target="_blank">VBRI, XING, and LAME headers</a>
 */
public class LAMEHeader
{
   // class mnemonics
   private static final int    HEADER_ID_SIZE                        = 4;
   private static final int    HEADER_VERSION_SIZE                   = 4;
   private static final int    HEADER_VERSION_FLAG_SIZE              = 1;
   private static final int    HEADER_LOWPASS_FILTER_FREQUENCY_SIZE  = 1;
   private static final int    HEADER_PEAK_SIGNAL_AMPLITUDE_SIZE     = 4;
   private static final int    HEADER_REPLAY_GAIN_SIZE               = 2;
   private static final int    HEADER_FLAGS_SIZE                     = 1;
   private static final int    HEADER_BITRATE_SIZE                   = 1;
   private static final int    HEADER_ENCODING_SIZE                  = 3;
   private static final int    HEADER_MP3_GAIN_SIZE                  = 1;
   private static final int    HEADER_PRESET_VALUE_SIZE              = 2;
   private static final int    HEADER_MUSIC_LENGTH_SIZE              = 4;
   private static final int    HEADER_MUSIC_CRC_SIZE                 = 2;
   private static final int    HEADER_CRC_SIZE                       = 2;      /** LAME headers prior to version 3.90 were 20 bytes long */
   public  static final int    HEADER_MIN_SIZE                       = 20;     /** LAME headers after    version 3.90 were 36 bytes long */
   public  static final int    HEADER_MAX_SIZE                       = 36;


   // byte masks
   private static final byte   HEADER_ATH_TYPE_MASK                  = (byte)0x0F;
   private static final byte   HEADER_PSYTUNE_MASK                   = (byte)0x10;
   private static final byte   HEADER_SAFE_JOINT_MASK                = (byte)0x20;
   private static final byte   HEADER_NO_GAP_NEXT_MASK               = (byte)0x40;
   private static final byte   HEADER_NO_GAP_PREV_MASK               = (byte)0x80;

   private static final byte   HEADER_NOISE_SHAPING_MASK             = (byte)0x03;
   private static final byte   HEADER_STEREO_MODE_MASK               = (byte)0x1C;
   private static final byte   HEADER_UNWISE_MASK                    = (byte)0x20;
   private static final byte   HEADER_SOURCE_SAMPLE_FREQUENCY_MASK   = (byte)0xC0;

   private static final byte   HEADER_SURROUND_SOUND_INFO_MASK       = (byte)0x38;
   private static final byte   HEADER_PRESET_MASK                    = (byte)0x07;

   private static final String HEADER_ID_LAME                        = "LAME";   // LAME headers are identified by LAME



   // data members
   private String          id;                      // always has the value of "LAME"
   private Version         version;                 // version of LAME encoder used to encode the .mp3 file
   private int             revision;                //
   private VBRMethod       vbrMethod;               // encoding method: CBR, ABR, VBR
   private int             lowpassFilterFrequency;  // in hz
   private float           peakSignalAmplitude;
   private ReplayGain      radioReplayGain;         // gain level set for general radio air play.
   private ReplayGain      audiophileReplayGain;    // gain level set for individual listener.
   private int             athType;                 // ATH type used
   private boolean         psytune;                 // LAME encoder used --nspsytune   option.
   private boolean         safejoint;               // LAME encoder used --nssafejoint option.
   private boolean         nogapNext;               // --nogap continued to   the next     track.   it is true for all but the last  track in a --nogap album.
   private boolean         nogapPrevious;           // --nogap continued from the previous track.   it is true for all but the first track in a --nogap album.
   private int             bitrate;                 // average bitrate for ABR, bitrate for CBR and minimal bitrate for VBR [in kbps] - 255 means 255 kbps or more.
   private int             encodingDelay;           // amount of silence added to the begining of the .mp3 file   (measured in samples)
   private int             encodingPadding;         // amount of silence added to the ending   of the .mp3 file   (measured in samples)
   private int             noiseShaping;            // type of noise shaping used
   private StereoMode      stereoMode;              // stereo mode
   private boolean         unwise;                  // unwise setting used
   private SourceFrequency sourceSampleFrequency;   // the original source sample frequency
   private byte            mp3Gain;                 // used by mp3 Gain tool to normalize volumes for mp3's
   private SurroundInfo    surroundInfo;            // surround information
   private int             preset;                  // preset - index into an internal LAME preset enum.
   private int             musicLength;             // length (in bytes) from LAME header to last mpeg audio frame.
   private byte[]          musicCRC;                // 16 bit CRC value of the mpeg audio frames
   private byte[]          crc;                     // 16 bit CRC value of the first 190 bytes of the

   private int             size;                    // When the header was first added in LAME 3.12, the LAME tag contained only a 20-byte LAME version string. In LAME 3.90, this region was expanded
   private byte[]          buffer;                  // raw binary bytes of the LAME header




   /**
    * default constructor.
    */
   public LAMEHeader()
   {
      id                     = HEADER_ID_LAME;
      version                = null;
      revision               = 0;
      vbrMethod              = null;
      lowpassFilterFrequency = 0;
      peakSignalAmplitude    = 0;
      radioReplayGain        = null;
      audiophileReplayGain   = null;
      athType                = 0;
      psytune                = false;
      safejoint              = false;
      nogapNext              = false;
      nogapPrevious          = false;
      bitrate                = 0;
      encodingDelay          = 0;
      encodingPadding        = 0;
      noiseShaping           = 0;
      stereoMode             = null;
      unwise                 = false;
      sourceSampleFrequency  = null;
      mp3Gain                = (byte)0x00;
      surroundInfo           = null;
      preset                 = 0;
      musicLength            = 0;
      musicCRC               = null;
      crc                    = null;
      size                   = 0;
      buffer                 = null;
   }

   /**
    * constructor.
    * @param inputStream   input stream pointing to the LAME header in an mpeg audio frame in an .mp3 file.
    * @throws IOException     if there is an error while reading the LAME header from the .mp3 file.
    * @throws ParseException  if an invalid value is detected while parsing the LAME header's raw bytes.
    *
    */
   public LAMEHeader(InputStream inputStream) throws IOException, ParseException
   {
      buffer = new byte[HEADER_MAX_SIZE];

      setId     (inputStream);
      setVersion(readBytes(inputStream, HEADER_VERSION_SIZE + HEADER_VERSION_FLAG_SIZE));

      // if the version < 3.90
      if (version.getMajor() < 3 || version.getMinor() < 90)
      {
         byte[] data = readBytes(inputStream, 11);
         byte[] temp = {buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], buffer[7], buffer[8]};

         buffer = new byte[20];
         System.arraycopy(temp, 0, buffer, 0          , temp.length);
         System.arraycopy(data, 0, buffer, temp.length, data.length);
      }
      else
      {
         byte[] data = readBytes(inputStream, HEADER_FLAGS_SIZE);
         setRevision ((data[0] & 0xF0) >> 4);
         setVBRMethod((data[0] & 0x0F));

         setLowpassFilterFrequency(readBytes(inputStream, HEADER_LOWPASS_FILTER_FREQUENCY_SIZE             ));
         setPeakSignalAmplitude   (readBytes(inputStream, HEADER_PEAK_SIGNAL_AMPLITUDE_SIZE                ));
         setReplayGains           (readBytes(inputStream, HEADER_REPLAY_GAIN_SIZE + HEADER_REPLAY_GAIN_SIZE));
         setFlags1                (readBytes(inputStream, HEADER_FLAGS_SIZE                                ));
         setBitrate               (readBytes(inputStream, HEADER_BITRATE_SIZE                              ));
         setEncoding              (readBytes(inputStream, HEADER_ENCODING_SIZE                             ));
         setFlags2                (readBytes(inputStream, HEADER_FLAGS_SIZE                                ));
         mp3Gain                 = readBytes(inputStream, HEADER_MP3_GAIN_SIZE)[0];
         setPreset                (readBytes(inputStream, HEADER_PRESET_VALUE_SIZE                         ));
         setMusicLength           (readBytes(inputStream, HEADER_MUSIC_LENGTH_SIZE                         ));
         musicCRC                = readBytes(inputStream, HEADER_MUSIC_CRC_SIZE                             );
         crc                     = readBytes(inputStream, HEADER_CRC_SIZE                                   );
      }
   }

   /**
    * constructor.
    * @param bytes   the raw bytes of an MPEG audio frame.
    * @param index   the offset into the bytes of the MPEG audio frame where the LAME header begins.
    * @throws ParseException  if an invalid value is detected while parsing the LAME header's raw bytes.
    */
   public LAMEHeader(byte[] bytes, int index) throws ParseException
   {
      try
      {
         id = new String(bytes, index, HEADER_ID_SIZE);
         if (!id.equals(HEADER_ID_LAME))
            throw new ParseException("Invalid id, " + id + ", in LAME header.  It must be " + HEADER_ID_LAME + ".", bytes);
         size = HEADER_ID_SIZE;

         byte[] data = new byte[HEADER_VERSION_SIZE + HEADER_VERSION_FLAG_SIZE];
         System.arraycopy(bytes, index + size, data, 0, data.length);
         setVersion(data);
         size += data.length;

         // if the version < 3.90
         if (version.getMajor() < 3 || version.getMinor() < 90)
         {
            buffer = new byte[HEADER_MIN_SIZE];
            System.arraycopy(bytes, index, buffer, 0, buffer.length);
            size = buffer.length;
         }
         else
         {
            data = new byte[HEADER_FLAGS_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setRevision ((data[0] & 0xF0) >> 4);
            setVBRMethod((data[0] & 0x0F));
            size += data.length;

            data = new byte[HEADER_LOWPASS_FILTER_FREQUENCY_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setLowpassFilterFrequency(data);
            size += data.length;

            data = new byte[HEADER_PEAK_SIGNAL_AMPLITUDE_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setPeakSignalAmplitude(data);
            size += data.length;

            data = new byte[HEADER_REPLAY_GAIN_SIZE + HEADER_REPLAY_GAIN_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setReplayGains(data);
            size += data.length;

            data = new byte[HEADER_FLAGS_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setFlags1(data);
            size += data.length;

            data = new byte[HEADER_BITRATE_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setBitrate(data);
            size += data.length;

            data = new byte[HEADER_ENCODING_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setEncoding(data);
            size += data.length;

            data = new byte[HEADER_FLAGS_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setFlags2(data);
            size += data.length;

            mp3Gain = bytes[index + size];
            size += HEADER_MP3_GAIN_SIZE;

            data = new byte[HEADER_PRESET_VALUE_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setPreset(data);
            size += data.length;

            data = new byte[HEADER_MUSIC_LENGTH_SIZE];
            System.arraycopy(bytes, index + size, data, 0, data.length);
            setMusicLength(data);
            size += data.length;

            musicCRC = new byte[HEADER_MUSIC_CRC_SIZE];
            System.arraycopy(bytes, index + size, musicCRC, 0, musicCRC.length);
            size += musicCRC.length;

            crc = new byte[HEADER_CRC_SIZE];
            System.arraycopy(bytes, index + size, crc, 0, crc.length);
            size += crc.length;
         }
         buffer = new byte[size];
         System.arraycopy(bytes, index, buffer, 0, size);
      }
      catch (ArrayIndexOutOfBoundsException ex)
      {
         throw new ParseException("Insufficient bytes to parse the LAME header.");
      }
   }

   /**
    * reads in the specified number of raw bytes from the LAME header and increments the size variable which contains the total number of bytes read from the LAME header.
    * @return the raw bytes read from the LAME header.
    * @param inputStream   input stream pointing within the LAME header in an mpeg audio frame in an .mp3 file.
    * @param numBytes      number of bytes to read in from the LAME header.
    * @throws IOException   if there is an error while reading the raw bytes from LAME header.
    */
   private byte[] readBytes(InputStream inputStream, int numBytes) throws IOException
   {
      byte[] data = new byte[numBytes];

      if (inputStream.read(data) != data.length)
         throw new IOException("Unable to read the LAME header from the mpeg audio frame in the mp3 file.");
      System.arraycopy(data, 0, buffer, size, data.length);
      this.size += numBytes;

      return data;
   }

   /**
    * get the LAME header id;
    * @return the LAME header id;
    */
   public String getId()
   {
      return id;
   }

   /**
    * read in the LAME header id bytes from the input stream and parse them.
    * @param inputStream   input stream pointing to the id in the LAME header.
    * @throws IOException      if there is an error while reading the raw bytes from LAME header.
    * @throws ParseException   if the id can not be parsed from the LAME header or if it contains an invalid value.
    * @see <a href="http://wiki.hydrogenaud.io/index.php?title=LAME_version_string">LAME version string</a>
    */
   private void setId(InputStream inputStream) throws IOException, ParseException
   {
      this.id = new String(readBytes(inputStream, HEADER_ID_SIZE));
      if (!id.equals(HEADER_ID_LAME))
      {
         byte[] bytes = new byte[this.size];
         throw new ParseException("Invalid id, " + id + ", in LAME header.  It must be " + HEADER_ID_LAME + ".", bytes);
      }
   }

   /**
    * get the LAME header version;
    * @return the LAME header version;
    */
   public Version getVersion()
   {
      return version;
   }

   /**
    * read in the LAME header version bytes from the input stream and parse them.
    * @throws ParseException   if the version can not be parsed from the LAME header
    * @see <a href="http://wiki.hydrogenaud.io/index.php?title=LAME_version_string">LAME version string</a>
    */
   private void setVersion(byte[] data) throws ParseException
   {
      String ver   = new String(data, 0, HEADER_VERSION_SIZE);
      int    major = data[0] - '0';
      int    minor = 0;

      try
      {
         // if the minor version is < 100, then the format of the version is d.dd
         if (ver.matches("\\d\\.\\d\\d"))
         {
            byte[] bytes = {data[2], data[3]};
            minor = Integer.parseInt(new String(bytes));
         }
         // otherwise, if the minor version is >= 100, then the format of the version is dddd, where the first digit is the major version and the last three digits are the minor version
         else if (ver.matches("\\d\\d\\d\\d"))
         {
            byte[] bytes = {data[1], data[2], data[3]};
            minor = Integer.parseInt(new String(bytes));
         }
         else
         {
            throw new ParseException("Invalid version, " + new String(data) + ", found in the LAME header.");
         }
      }
      catch (NumberFormatException ex)
      {  // this is impossible and can not happen
         ex.printStackTrace();
      }

      // parse the fifth byte that specifies the version flag, ie, alpha, beta, release, etc.
      this.version = new Version(major, minor, (char)data[HEADER_VERSION_SIZE]);
   }

   /**
    * get the LAME header revision;
    * @return the LAME header revision;
    */
   public int getRevision()
   {
      return revision;
   }

   /*
    * parse the revision in the LAME Header.
    * @param revision    revision value specified in the LAME header.
    * @throws ParseException   if the revision can not be parsed from the LAME header or if it contains an invalid value.
    */
   private void setRevision(int revision) throws ParseException
   {
      if (revision == 15)
      {
         byte[] bytes = new byte[size];
         throw new ParseException("Invalid revision, " + revision + ", in LAME header.", bytes);
      }
      this.revision = revision;
   }

   /**
    * get the bit rate encoding method used to encode the .mp3 file.
    * @return the bit rate encoding method used to encode the .mp3 file.
    */
   public VBRMethod getVBRMethod()
   {
      return vbrMethod;
   }

   /*
    * parse the LAME header VBR encoding method in the LAME Header.
    * @param method    VBR encoding method value specified in the LAME header.
    * @throws ParseException   if the VBR encoding method can not be parsed from the LAME header or if it contains an invalid value.
    */
   private void setVBRMethod(int method) throws ParseException
   {
      try
      {
         this.vbrMethod = VBRMethod.valueOf(method);
         if (this.vbrMethod == VBRMethod.RESERVED)
         {
            byte[] bytes = new byte[size];
            throw new ParseException("Invalid VBR encoding method, " + vbrMethod + ", in LAME header.", bytes);
         }
      }
      catch (IllegalArgumentException ex)
      {
         byte[] bytes = new byte[size];
         throw new ParseException(ex.getMessage(), bytes);
      }
   }

   /**
    * get the low pass filter frequency (in hz) setting used by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> to encode the .mp3 file.
    * @return the low pass filter frequency (in hz) setting used by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> to encode the .mp3 file.
    */
   public int getLowpassFilterFrequency()
   {
      return lowpassFilterFrequency;
   }

   /*
    * parse the low pass frequency in the LAME Header.
    * @param inputStream   input stream pointing to the low pass frequency in the LAME header.
    */
   private void setLowpassFilterFrequency(byte[] data)
   {
      this.lowpassFilterFrequency = (data[0] & 0xFF) * 100;
   }

   /**
    * get the peak signal amplitude that occurs in the digital audio samples in this frame.
    * @return the LAME header peak signal amplitude;
    */
   public float getPeakSignalAmplitude()
   {
      return peakSignalAmplitude;
   }

   /*
    * parse the peak signal amplitude in the LAME Header.
    * @param inputStream   input stream pointing to the peak signal amplitude in the LAME header.
    */
   private void setPeakSignalAmplitude(byte[] data)
   {
      this.peakSignalAmplitude = Float.intBitsToFloat(Utility.bytesToInt(data));
   }

   /**
    * get the gain (volume) setting specified for playing the .mp3 file on the radio.
    * @return the gain (volume) setting specified for playing the .mp3 file on the radio.
    */
   public ReplayGain getRadioReplayGain()
   {
      return radioReplayGain;
   }

   /**
    * get the gain (volume) setting specified by the user for playing the .mp3 file.
    * @return the gain (volume) setting specified by the user for playing the .mp3 file.
    */
   public ReplayGain getAudiophileReplayGain()
   {
      return audiophileReplayGain;
   }

   /*
    * parse the radio and audiophile replay gains in the LAME Header.
    * @param inputStream   input stream pointing to the replay gains in the LAME header.
    * @throws ParseException   if the radio and audiophile replay gains can not be parsed from the LAME header or if they contain an invalid value.
    */
   private void setReplayGains(byte[] data) throws ParseException
   {
      try
      {
         this.radioReplayGain      = new ReplayGain(GainType      .valueOf((data[0] & 0xD0) >> 5),
                                                    GainOriginator.valueOf((data[0] & 0x1C) >> 2),
                                                    (data[0] & 0x02) != 0, (data[0] & 0x01) == 0 ? 0 : 256 + data[1]);
         this.audiophileReplayGain = new ReplayGain(GainType      .valueOf((data[2] & 0xD0) >> 5),
                                                    GainOriginator.valueOf((data[2] & 0x1C) >> 2),
                                                    (data[0] & 0x02) != 0, (data[2] & 0x01) == 0 ? 0 : 256 + data[3]);
      }
      catch (IllegalArgumentException ex)
      {
         throw new ParseException(ex.getMessage());
      }
   }

   /**
    * get the ATH type.
    * @return the ATH type.
    */
   public int getAthType()
   {
      return athType;
   }

   /**
    * get whether the .mp3 file was encoded by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> with the --nspsytune option.
    * @return whether the .mp3 file was encoded by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> with the --nspsytune option.
    */
   public boolean usedPsytune()
   {
      return psytune;
   }

   /**
    * get whether the .mp3 file was encoded by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> with the --nssafejoint option.
    * @return whether the .mp3 file was encoded by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> with the --nssafejoint option.
    */
   public boolean usedSafejoint()
   {
      return safejoint;
   }

   /**
    * get whether the .mp3 file was encoded by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> with the --nogap option.
    * This indicates that the --nogap continued to the next track.
    * @return boolean indicating that the --nogap continued to the next track.   It is true for all but the last track in a --nogap album.
    */
   public boolean usedNoGapNext()
   {
      return nogapNext;
   }

   /**
    * get whether the .mp3 file was encoded by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder</a> with the --nogap option.
    * This indicates that the --nogap continued to the previous track.
    * @return boolean indicating that the --nogap continued to the previous track.   It is true for all but the first track in a --nogap album.
    */
   public boolean usedNoGapPrevious()
   {
      return nogapPrevious;
   }

   /*
    * parse the flags1 byte in the LAME Header.  The flags1 byte specifies the values for the following fields:
    * <table>
    *    <tr><th>Field           </th><th>Description</th></tr>
    *    <tr><td>ATH type        </td><td>ATH type used                                                                                           </td></tr>
    *    <tr><td>psytune         </td><td>LAME encoder used --nspsytune   option.                                                                 </td></tr>
    *    <tr><td>safejoint       </td><td>LAME encoder used --nssafejoint option.                                                                 </td></tr>
    *    <tr><td>no gap Next     </td><td>--nogap continued to   the next     track.   it is true for all but the last  track in a --nogap album. </td></tr>
    *    <tr><td>no gap Previous </td><td>--nogap continued from the previous track.   it is true for all but the first track in a --nogap album. </td></tr>
    * </table>
    * @param inputStream   input stream pointing to the flags1 byte in the LAME header.
    */
   private void setFlags1(byte[] data)
   {
      this.athType       = (data[0] & HEADER_ATH_TYPE_MASK   );
      this.psytune       = (data[0] & HEADER_PSYTUNE_MASK    ) != 0;
      this.safejoint     = (data[0] & HEADER_SAFE_JOINT_MASK ) != 0;
      this.nogapNext     = (data[0] & HEADER_NO_GAP_NEXT_MASK) != 0;
      this.nogapPrevious = (data[0] & HEADER_NO_GAP_PREV_MASK) != 0;
   }

   /**
    * get the bit rate used to encode the .mp3 file.  If the .mp3 file was encoded using an ABR method, then this method will return the specified average bit rate.
    *  If the .mp3 file was encoded using a CBR method, then this method will return the actual bit rate. For a variable rate encoded .mp3 file, the minimal bitrate
    * will returned.  A value of 255 means that a bit rate of 255 or higher was used to encode the .mp3 file.
    * @return the bit rate used to encode the .mp3 file.
    */
   public int getBitrate()
   {
      return bitrate;
   }

   /*
    * parse the bitrate in the LAME Header.  The bit rate (in kbps) specifies:
    * <ul>
    *    <li>the average bitrate for ABR</li>
    *    <li>the actual  bitrate for CBR</li>
    *    <li>the minimal bitrate for VBR</li>
    * </ul>
    * note: 255 means 255 kbps or more.
    * @param inputStream   input stream pointing to the bit rate in the LAME header.
    * @throws IOException      if there is an error while reading the raw bytes from LAME header.
    */
   private void setBitrate(byte[] data)
   {
      this.bitrate = data[0] & 0xFF;
   }

   /**
    * get the number of samples of silence added to the begining of the .mp3 file
    * @return the number of samples of silence used in the encoding delay.
    */
   public int getEncodingDelay()
   {
      return encodingDelay;
   }

   /**
    * get the number of samples of silence added to the end of the .mp3 file
    * @return the number of samples of silence used in the encoding padding.
    */
   public int getEncodingPadding()
   {
      return encodingPadding;
   }

   /*
    * parse the encoding delay and padding (measured in samples) in the LAME Header.
    * The encoding delay   specifies the amount of silence added to the begining of the .mp3 file, while
    * the encoding padding specifies the amount of silence added to the ending   of the .mp3 file.
    * @param inputStream   input stream pointing to the encodings in the LAME header.
    */
   private void setEncoding(byte[] data)
   {
      this.encodingDelay   = ((data[0] & 0xF0 ) << 4) + ((data[0] & 0x0F) << 4) + ((data[1] & 0xF0) >> 4);
      this.encodingPadding = ((data[1] & 0x0F ) << 8) +   data[2];
   }
   /**
    * get type of noise shaping used to encode the .mp3 file.
    * @return type of noise shaping used to encode the .mp3 file.
    */
   public int getNoiseShaping()
   {
      return noiseShaping;
   }

   /**
    * get the stereo mode used to encode the .mp3 file.
    * @return the stereo mode used to encode the .mp3 file.
    */
   public StereoMode getStereoMode()
   {
      return stereoMode;
   }

   /**
    * get whether the user used some settings which would likely damage quality in normal circumstances.
    * @return whether the user used some settings which would likely damage quality in normal circumstances.
    */
   public boolean usedUnwise()
   {
      return unwise;
   }

   /**
    * get the original audio source's sample frequency (in hz).
    * @return the original audio source's sample frequency (in hz).
    */
   public SourceFrequency getSourceSampleFrequency()
   {
      return sourceSampleFrequency;
   }

   /*
    * parse the flags2 byte in the LAME Header.  The flags2 byte specifies the values for the following fields:
    * <table>
    *    <tr><th>Field                  </th><th>Description                                </th></tr>
    *    <tr><td>noise shaping          </td><td>type of noise shaping used                 </td></tr>
    *    <tr><td>stereo mode            </td><td>stereo mode                                </td></tr>
    *    <tr><td>unwise                 </td><td>unwise setting used                        </td></tr>
    *    <tr><td>source sample frequency</td><td>the original source sample frequency in khz</td></tr>
    * </table>
    * @param inputStream   input stream pointing to the flags2 byte in the LAME header.
    * @throws ParseException   if the flags2 byte can not be parsed from the LAME header or if it contains an invalid value.
    */
   private void setFlags2(byte[] data) throws ParseException
   {
      this.noiseShaping = (data[0] & HEADER_NOISE_SHAPING_MASK);
      this.unwise       = (data[0] & HEADER_UNWISE_MASK       ) != 0;
      try
      {
         this.stereoMode = StereoMode.valueOf((data[0] & HEADER_STEREO_MODE_MASK) >> 2);
      }
      catch (IllegalArgumentException ex)
      {
         throw new ParseException(ex.getMessage());
      }
      try
      {
         this.sourceSampleFrequency = SourceFrequency.valueOf((data[0] & HEADER_SOURCE_SAMPLE_FREQUENCY_MASK) >> 6);
      }
      catch (IllegalArgumentException ex)
      {
         throw new ParseException(ex.getMessage());
      }
   }

   /**
    * get the gain byte used by tools like <a href="http://mp3gain.sourceforge.net" target="_blank">mp3 Gain</a> to normalize volumes for mp3's.
    * @return the gain settings used to normalize the sound of the .mp3 file.
    */
   public byte getMp3Gain()
   {
      return mp3Gain;
   }

   /**
    * get the surround info.
    * @return the surround info.
    */
   public SurroundInfo getSurroundInfo()
   {
      return surroundInfo;
   }

   /**
    * get the preset used internally by the <a target="_blank" href="http://lame.sourceforge.net/">LAME encoder/decoder</a> as an index into an internal LAME preset enum.
    * @return one of the 2047 presets allowed ey the LAME encoder.
    */
   public int getPreset()
   {
      return preset;
   }

   /*
    * parse the surround info and preset value in the LAME Header.
    * The preset value specifies an index into an internal LAME preset enum.
    * @param inputStream   input stream pointing to the surround info and preset bytes in the LAME header.
    * @throws ParseException   if the surround info can not be parsed from the LAME header or if it contains an invalid value.
    */
   private void setPreset(byte[] data) throws ParseException
   {
      try
      {
         this.surroundInfo = SurroundInfo.valueOf((data[0] & HEADER_SURROUND_SOUND_INFO_MASK) >> 3);
      }
      catch (IllegalArgumentException ex)
      {
         throw new ParseException(ex.getMessage());
      }
      data[0] = (byte)(data[0] & HEADER_PRESET_MASK);
      this.preset = Utility.bytesToShort(data);
   }

   /**
    * get length (in bytes) from the LAME header to the last byte of the last mpeg audio frame.
    * @return the length (in bytes) from the LAME header to the last byte of the last mpeg audio frame.
    */
   public int getMusicLength()
   {
      return musicLength;
   }

   /*
    * parse the length (in bytes) of the mpeg audio portion of the .mp3 file in the LAME Header.
    * @param inputStream   input stream pointing to the music length in the LAME header.
    */
   private void setMusicLength(byte[] data)
   {
      this.musicLength = Utility.bytesToInt(data);
   }

   /**
    * get the 16 bit CRC value of the complete .mp3 music data as originally made by LAME.
    * @return the 16 bit CRC value of the complete .mp3 music data as originally made by LAME.
    */
   public byte[] getMusicCRC()
   {
      return musicCRC;
   }

   /**
    * get the 16 bit CRC value of the first 190 bytes of the Info header frame.
    * @return the 16 bit CRC value of the first 190 bytes of the Info header frame.
    */
   public byte[] getCRC()
   {
      return crc;
   }

   /**
    * gets the size (in bytes) of the LAME header.
    * When the LAME header was first introduced with the LAME 3.12 encoder, it consisted of a 20-byte LAME version string.
    * This was actually comprised of only a nine byte version string, with the remaining 11 bytes unused.
    * In LAME 3.90, the lame tag was expanded to a 36 bytes in order to include numerous fields.
    * @return the size of the LAME header.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * get a string representation of the LAME header.
    * @return a string representation of the LAME header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("LAME header" + "\n");
      buffer.append("   size..........................: " + size                         + " bytes\n");
      buffer.append("   bytes.........................: " + Utility.hex(this.buffer, 35) + "\n");
      buffer.append("   id............................: " + id                           + "\n");
      buffer.append("   version.......................: " + version                      + "\n");
      buffer.append("   revision......................: " + revision                     + "\n");
      buffer.append("   vbr version...................: " + vbrMethod                    + "\n");
      buffer.append("   low pass filter freq..........: " + lowpassFilterFrequency       + " hz\n");
      buffer.append("   peak signal amplitude.........: " + peakSignalAmplitude          + "\n");
      buffer.append("   radio      replay gain........: " + radioReplayGain              + "\n");
      buffer.append("   audiophile replay gain........: " + audiophileReplayGain         + "\n");
      buffer.append("   ATH type......................: " + athType                      + "\n");
      buffer.append("   encoded using psytune.........: " + psytune                      + "\n");
      buffer.append("   encoded using safejoint.......: " + safejoint                    + "\n");
      buffer.append("   no gap continues to next track: " + nogapNext                    + "\n");
      buffer.append("   no gap continues to prev track: " + nogapPrevious                + "\n");
           if (vbrMethod == VBRMethod.CBR || vbrMethod == VBRMethod.CBR_2_PASS) buffer.append("   constant bit rate.............: ");
      else if (vbrMethod == VBRMethod.ABR || vbrMethod == VBRMethod.ABR_2_PASS) buffer.append("   average bit rate..............: ");
      else                                                                      buffer.append("   minimal variable bit rate.....: ");
      buffer.append(bitrate + "\n");
      buffer.append("   encoding delay................: " + encodingDelay                + " samples\n");
      buffer.append("   encoding padding..............: " + encodingPadding              + " samples\n");
      buffer.append("   noise shaping.................: " + noiseShaping                 + "\n");
      buffer.append("   stereo mode...................: " + stereoMode                   + "\n");
      buffer.append("   unwise settings used..........: " + unwise                       + "\n");
      buffer.append("   source sample frequency.......: " + sourceSampleFrequency        + "\n");
      buffer.append("   mp3 gain......................: " + Utility.hex(mp3Gain)         + "\n");
      buffer.append("   surround info.................: " + surroundInfo                 + "\n");
      buffer.append("   preset........................: " + preset                       + "\n");
      buffer.append("   music length..................: " + musicLength                  + " bytes\n");
      buffer.append("   music CRC.....................: " + Utility.hex(musicCRC)        + "\n");
      buffer.append("   CRC of first 190 bytes........: " + Utility.hex(crc)                   );

      return buffer.toString();
   }
}
