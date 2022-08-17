package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

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
 * An <i>audio seek pointer index</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#AUDIO_SEEK_POINT_INDEX ASPI} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to list the people who were involved
 * in the song.  The <i>audio seek pointer index</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Audio Seek Pointer Index</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">indexed data start         </td><td class="beaglebuddy">the byte offset of the indexed .mp3 audio data from the beginning of the .mp3 file.  This is typically the start of the audio
 *                                                                                                                          portion of the .mp3 file.                                                                                                      </td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">indexed data length        </td><td class="beaglebuddy">length (in bytes) of the .mp3 audio data being indexed.  This is typically the size (in bytes) of the .mp3 file - the size of
 *                                                                                                                          ID3v2.4 tag.                                                                                                                   </td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">num index points           </td><td class="beaglebuddy">the number of index points.  This specifies how many points within the .mp3 audio section you want create indexes for.         </td></tr>
 *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">bits per index point       </td><td class="beaglebuddy">the number of bits used to store an index point (either 8 bits or 16 bits).  Why this wasn't just defined as 16 bits you'll
 *                                                                                                                          have to ask the crazy drunk monkeys who created the ID3v2.4 spec.                                                               </td></tr>
 *       <tr><td class="beaglebuddy">5.</td><td class="beaglebuddy">list of fraction at indexes</td><td class="beaglebuddy">for each index point, the fraction at index is the numerator of the fraction representing a
 *                                                                                                                          relative position in the data.  The denominator is 2 ^ bits per index point.  Since the bits per index
 *                                                                                                                          point is either 8 or 16, the denominator is either 2 ^ 8 = 256 or 2 ^ 16 = 65536.
 *                                                                                                                          The fraction at index <i>i</i> is given by:                                                                           <br/>
 *                                                                                                                          f[i] = offset[i] / <indexed data length> * 2 ^ <bits per index point>  rounded down to the nearest integer            <br/>
 *                                                                                                                          where:                                                                                                                <br/>
 *                                                                                                                          offset[i] = the offset (in bytes) of the frame whose start is soonest after the point for which the time offset =
 *                                                                                                                          (i / <num index points> * <duration of the mp3 file in ms>).                                                          <br/>
 *                                                                                                                          The duration is obtained from the <i>TLEN</i> frame.                                                                           </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>audio seek pointer index</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"   target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"  target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyAudioSeekPointIndex extends ID3v24FrameBody
{
   /** valid ID3v2.4 bits per index point values */
   public enum BitsPerIndexPoint
   {                  /** 8  bits */
      SIZE_8_BITS ,   /** 16 bits */
      SIZE_16_BITS;

      /** @return a string representation of the BitsPerIndexPoint */
      public String toString()  {return "" + ordinal() + " - " + (this == SIZE_8_BITS ? "8" : "16") + " bits";}

      /**
       * convert an integral value to its corresponding enum.
       * @param bitsPerIndexPoint   the integral value that is to be converted to a BitsPerIndexPoint enum.
       * @return the BitsPerIndexPoint enum whose ordinal value corresponds to the given integral value.
       * @throws IllegalArgumentException   if there is no BitsPerIndexPoint enum whose ordinal value corresponds to the given integral value.
       */
      public static BitsPerIndexPoint getBitsPerIndexPoint(int bitsPerIndexPoint)
      {
         for (BitsPerIndexPoint b : BitsPerIndexPoint.values())
            if (bitsPerIndexPoint == b.ordinal())
               return b;
         throw new IllegalArgumentException("Invalid value bitsPerIndexPoint " + bitsPerIndexPoint + ".  It must be either 0 or 1.");
      }
   }

   // data members
   private int               indexedDataStart;
	private int               indexedDataLength;
   private BitsPerIndexPoint bitsPerIndexPoint;
   private List<Short>       fractionAtIndexes;



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>indexed data start  of 0</li>
    *    <li>indexed data length of 0</li>
    *    <li>16 bits per index point </li>
    *    <li>0 index points          </li>
    * </ul>
    */
   public ID3v24FrameBodyAudioSeekPointIndex()
   {
      this(0, 0, BitsPerIndexPoint.SIZE_16_BITS, new Vector<Short>());
   }

   /**
    * This constructor is called when creating a new frame.
    * @param indexedDataStart     byte offset from the beginning of the .mp3 file.
    * @param indexedDataLength    length (in bytes) of the audio data being indexed.
    * @param bitsPerIndexPoint    the number of bits used to store an index point (either 8 bits or 16 bits).
    * @param fractionAtIndexes    list of fraction indexes for the indexed points.  see {@link #setFractionAtIndexes(List)} for a fuller explanation.
    */
   public ID3v24FrameBodyAudioSeekPointIndex(int indexedDataStart, int indexedDataLength, BitsPerIndexPoint bitsPerIndexPoint, List<Short> fractionAtIndexes)
   {
      super(FrameType.AUDIO_SEEK_POINT_INDEX);

      setIndexedDataStart (indexedDataStart );
      setIndexedDataLength(indexedDataLength);
      setBitsPerIndexPoint(bitsPerIndexPoint);
      setFractionAtIndexes(fractionAtIndexes);

      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an audio seek pointer index frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyAudioSeekPointIndex(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.AUDIO_SEEK_POINT_INDEX, frameBodySize);
   }

	/**
    * gets the byte offset of the indexed .mp3 audio data from the beginning of the .mp3 file.  This is typically the start of the audio portion of the .mp3 file.
    * @return the byte offset of the indexed .mp3 audio data from the beginning of the .mp3 file.
    * @see #setIndexedDataStart(int)
    */
   public int getIndexedDataStart()
   {
   	return indexedDataStart;
   }

   /**
    * sets the byte offset of the indexed .mp3 audio data from the beginning of the .mp3 file.  This is typically the start of the audio portion of the .mp3 file.
    * @param indexedDataStart   the byte offset of the indexed .mp3 audio data from the beginning of the .mp3 file.
    * @see #getIndexedDataStart()
    */
   public void setIndexedDataStart(int indexedDataStart)
   {
      if (indexedDataLength < 0)
         throw new IllegalArgumentException("The indexed data start field in the ID3v2.4 " + frameType.getId() + " frame contains an invalid value, " + indexedDataStart + ".  It must be >= 0.");

      this.indexedDataStart = indexedDataStart;
      this.dirty            = true;
   }

	/**
    * gets the length (in bytes) of the .mp3 audio data being indexed.
    * @return the length (in bytes) of the .mp3 audio data being indexed.
    * @see #setIndexedDataLength(int)
    */
   public int getIndexedDataLength()
   {
   	return indexedDataLength;
   }

   /**
    * sets the length (in bytes) of the .mp3 audio data being indexed.  This is typically the entire audio portion of the .mp3 file, and hence is calculated by
    * subtracting the size of the ID3v2.4 tag from the file size of the .mp3 file.  For example, if you have an .mp3 file which has a size of 200,00 bytes, and
    * whose ID3v2.4 tag header is 35,000 bytes, then the index data length = 200,000 - 35,000 = 165,000 bytes
    * @param indexedDataLength    the length (in bytes) of the .mp3 audio data being indexed.
    * @see #getIndexedDataLength()
    */
   public void setIndexedDataLength(int indexedDataLength)
   {
      if (indexedDataLength <= 0)
         throw new IllegalArgumentException("The indexed data length field in the ID3v2.4 " + frameType.getId() + " frame contains an invalid value, " + indexedDataLength + ".  It must be > 0.");

      this.indexedDataLength = indexedDataLength;
      this.dirty             = true;
   }

	/**
    * gets the number of index points.
    * @return the number of index points.
    * @see #getFractionAtIndexes()
    */
   public short getNumIndexPoints()
   {
      return (short)fractionAtIndexes.size();
   }

	/**
    * gets the number of bits used to store an index point (either 8 bits or 16 bits).
    * @return the number of bits used to store an index point (either 8 bits or 16 bits).
    * @see #setBitsPerIndexPoint(BitsPerIndexPoint)
    */
   public BitsPerIndexPoint getBitsPerIndexPoint()
   {
   	return bitsPerIndexPoint;
   }

   /**
    * @param bitsPerIndexPoint the bitsPerIndexPoint to set
    */
   public void setBitsPerIndexPoint(BitsPerIndexPoint bitsPerIndexPoint)
   {
      this.bitsPerIndexPoint = bitsPerIndexPoint;
      this.dirty             = true;
   }

   /**
    * gets the list of fraction indexes for the indexed points.
    * @return the list of fraction indexes for the indexed points.
    * @see #setFractionAtIndexes(List)
    */
   public List<Short> getFractionAtIndexes()
   {
      return fractionAtIndexes;
   }

   /**
    * A fraction index represents the relative position within data, and is a fraction given by the following formula: <br/>
    * fraction index = offset / <data length> * 2 ^ <bits per index point>                                             <br/>
    * where:                                                                                                           <br/>
    *    <ul>
    *       <li>time offset = (index point / <num index points> * <duration of the mp3 file in ms>).</li>
    *       <li>duration is the length (in ms) of the mp3 file and is obtained from the <i>TLEN</i> frame.</li>
            <li>offset is the offset (in bytes) of the mp3 frame (not to be confused with frames in an ID3v2.x tag) whose start occurs soonest in the .mp3 file after the time offset.</li>
    *    </ul>
    * Here is an example to illustrate these concepts:
    * <pre class="beaglebuddy">
    *     You have an .mp3 file which is 1 minute and 20 seconds long, has a disk size of 200,000 bytes, and whose first 10,000 bytes are used for the ID3v2.4 tag.
    *     You are going to create 15 index points for the audio portion of the .mp3 file.
    *     You want to calculate the fraction at index 6.
    *     Using these values, you then have:
    *     1. indexed data start     10,000 bytes
    *     2. indexed data end       200,000 bytes
    *     3. indexed data length    190,000 bytes = 200,000 - 10,000
    *     4. duration               1 minute and 20 seconds = 80 seconds = 80,000 ms
    *     5. num index points       15
    *     6. bits per index point   16
    *     7. time offset[6]         6 / 15 * 80,000ms = 32,000ms
    *     8. offset[6]              is the offset (in bytes) of the mp3 frame (not to be confused with frames in an ID3v2.x tag) whose start occurs soonest after the time offset.
    *                               this value will be calculated by the .mp3 player software.
    *     8. fraction[6]            offset[6] / indexed data length * 2 ^ bits per index point
    *                               offset[6] / 190,000 * 65536
    *                               offset[6] * 0.34492631578947368421052631578947
    *     The fraction calculated in step 8 is rounded down to the nearest integer.
    * </pre>
    * Don't worry if you don't understand all of this.
    * The monkeys who made the ID3v2.4 spec did an exceptionally bad job with this frame, and their documentation has errors in it.
    * @param fractionAtIndexes the fractionAtIndexes to set
    */
   public void setFractionAtIndexes(List<Short> fractionAtIndexes)
   {
      this.fractionAtIndexes = fractionAtIndexes == null ? new Vector<Short>() : fractionAtIndexes;
      this.dirty             = true;
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {

      setIndexedDataStart  (bytesToInt  (buffer, 0));
      setIndexedDataLength (bytesToInt  (buffer, 4));
      int numIndexPoints = (bytesToShort(buffer, 8));
      setBitsPerIndexPoint(BitsPerIndexPoint.getBitsPerIndexPoint(buffer[10]));
      Vector<Short> fractions = new  Vector<Short>();

      if (bitsPerIndexPoint == BitsPerIndexPoint.SIZE_8_BITS)
      {
         for(int i=11; i<buffer.length; i++)
            fractions.add(new Short((short)((buffer[i] & 0xFF))));
      }
      else
      {
         for(int i=11; i<buffer.length; i+=2)
            fractions.add(new Short(bytesToShort(buffer, i)));
      }
      setFractionAtIndexes(fractions);

      if (fractionAtIndexes.size() != numIndexPoints)
         throw new IllegalArgumentException("The fractions at indexes field in the ID3v2.4 " + frameType.getId() + " frame contains " + fractionAtIndexes.size() + " values, while the number of index points field specifies that " + numIndexPoints + " values are expected.");

      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
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
         // calculate how many bytes we need to store all the involved people
         buffer = new byte[11 + fractionAtIndexes.size() * (bitsPerIndexPoint == BitsPerIndexPoint.SIZE_8_BITS ? 1 : 2)];
         System.arraycopy(intToBytes  (indexedDataStart               ), 0, buffer, 0, 4);
         System.arraycopy(intToBytes  (indexedDataLength              ), 0, buffer, 4, 4);
         System.arraycopy(shortToBytes((short)fractionAtIndexes.size()), 0, buffer, 8, 2);
         buffer[10] = (byte)bitsPerIndexPoint.ordinal();
         int index = 11;
         if (bitsPerIndexPoint == BitsPerIndexPoint.SIZE_8_BITS)
         {
            for(Short fraction : fractionAtIndexes)
            {
               buffer[index] = (byte)fraction.shortValue();
               index++;
            }
         }
         else
         {
            for(Short fraction : fractionAtIndexes)
            {
               System.arraycopy(shortToBytes(fraction.shortValue()), 0, buffer, index, 2);
               index+=2;
            }
         }
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>audio seek pointer index</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: audio seek point index\n");
      buffer.append("   bytes................: " + this.buffer.length      + " bytes\n");
      buffer.append("                          " + hex(this.buffer, 26)    + "\n");
      buffer.append("   indexed data start..: " + indexedDataStart         + "\n");
      buffer.append("   indexed data length.: " + indexedDataLength        + "\n");
      buffer.append("   num index points....: " + fractionAtIndexes.size() + "\n");
      buffer.append("   bits per index point: " + bitsPerIndexPoint        + "\n");
      buffer.append("   fraction at indexes.: "                            + "\n" );
      for(Short fractionAtIndex : fractionAtIndexes)
         buffer.append("                          " + fractionAtIndex.shortValue() + "\n");

      return buffer.toString();
   }
}
