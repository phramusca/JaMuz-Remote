package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.pojo.v23.Level;





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
 * An <i>equalization</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#EQUALIZATION EQUA} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to specify points on an equalization (EQ)
 * curve.  You may specify as many points as you want, but typically you specify 16 or 32, much like the 32 band equalizers you can buy for your home stereo. The monkeys who came up with the
 * ID3v2.3 specification did quite a poor job on this one.  Instead of making a nice simple EQ based on the industry standard 32 band graphic equalizers, they tried to solve a problem that didn't exist
 * and made this as flexible as all humanly possible, which in turn made it unnecessarily complex.  The <i>equalization</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Equalization Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">numAdjustmentBits                                </td><td class="beaglebuddy">number of bits to use when specifying the levels on the EQ curve.  This determines the
 *                                                                                                                                                 scale used for the offsets of the points, ie, levels, from the midpoint on the EQ curve.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.pojo.v23.Level levels}</td><td class="beaglebuddy">list of the points on the EQ curve                                                      </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be only one <i>equalization</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyEqualization extends ID3v23FrameBody
{
   // class members
   private static byte DIRECTION_MASK = (byte)0x80;     // bit mask for adjustment direction
   private static byte FREQUENCY_MASK = (byte)0x7F;     // bit mask for frequency

   // data members
   private int         numAdjustmentBits;  // number of bits used for the representation of the adjustment
   private List<Level> levels;             // equalization levels



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>16 bit adjustments</li>
    *    <li>no levels, ie, a flat EQ curve</li>
    * </ul>
    */
   public ID3v23FrameBodyEqualization()
   {
      this(16, new Vector<Level>());
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param numAdjustmentBits   number of bits used for the representation of the adjustment in an equalization level.
    * @param levels              levels comprising the equalization curve.
    */
   public ID3v23FrameBodyEqualization(int numAdjustmentBits, Level[] levels)
   {
      super(FrameType.EQUALIZATION);

      setNumAdjustmentBits(numAdjustmentBits);
      setLevels           (levels);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param numAdjustmentBits   number of bits used for the representation of the adjustment in an equalization level.
    * @param levels              levels comprising the equalization curve.
    */
   public ID3v23FrameBodyEqualization(int numAdjustmentBits, List<Level> levels)
   {
      super(FrameType.EQUALIZATION);

      setNumAdjustmentBits(numAdjustmentBits);
      setLevels           (levels);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an equalization frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyEqualization(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.EQUALIZATION, frameBodySize);
   }

   /**
    * gets the number of bits used for the representation of an adjustment in an equalization level.
    * @return the number of bits used for the representation of an adjustment in an equalization level.
    * @see #setNumAdjustmentBits(int)
    */
   public int getNumAdjustmentBits()
   {
      return numAdjustmentBits;
   }

   /**
    * sets the number of bits used for the representation of an adjustment in an equalization level.
    * This determines the scale used for the offsets of the points, ie, levels, from the midpoint on the EQ curve.
    * @param numAdjustmentBits   the number of bits used for the representation of an adjustment in an equalization level.
    * @throws IllegalArgumentException  if the number of adjustment bits is less than or equal to 0.
    * @see #getNumAdjustmentBits()
    */
   public void setNumAdjustmentBits(int numAdjustmentBits) throws IllegalArgumentException
   {
      if (numAdjustmentBits <= 0 || numAdjustmentBits > 32)
         throw new IllegalArgumentException("The number of adjustment bits field in the " + frameType.getId() + " frame contains an invalid value, " + numAdjustmentBits + ".  It must be  0 < number of adjustment bits <= 32.");

      this.dirty             = true;
      this.numAdjustmentBits = numAdjustmentBits;
   }

   /**
    * rounds up the number of adjustment bits to the nearest byte.
    * @return the number of bytes needed to hold the specified number of adjustment bits.
    */
   private int getNumAdjustmentBytes()
   {
      return  numAdjustmentBits / 8 + (numAdjustmentBits % 8 == 0 ? 0 : 1);
   }

   /**
    * gets the levels comprising the equalization curve.
    * @return  the levels comprising the equalization curve.
    * @see #setLevels(List)
    */
   public List<Level> getLevels()
   {
      return levels;
   }

   /**
    * sets the levels comprising the equalization curve.
    * @param levels  the levels comprising the equalization curve.
    * @see #getLevels()
    */
   public void setLevels(Level[] levels)
   {
      Vector<Level> listLevels = new Vector<Level>();

      if (levels != null)
      {
         for(Level level : levels)
            listLevels.add(level);
      }
      setLevels(listLevels);
   }

   /**
    * sets the levels comprising the equalization curve.
    * @param levels  the levels comprising the equalization curve.
    * @see #getLevels()
    */
   public void setLevels(List<Level> levels)
   {
      int maxAdjusment = (int)Math.pow(2, numAdjustmentBits) - 1;
      for (Level level : levels)
         if (level.getAdjustment() > maxAdjusment)
            throw new IllegalArgumentException("The amount of adjustment field in the equalization level " + level + " in the " + frameType.getId() + " frame contains an invalid value, " + level.getAdjustment() + ".  It must be <= " + maxAdjusment + ".");

      this.dirty  = true;
      this.levels = levels;
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      setNumAdjustmentBits(buffer[0]);
      levels = new Vector<Level> ();

      byte[] data  = new byte[getNumAdjustmentBytes()];
      int    index = 1;

      while (index < buffer.length)
      {
         int   direction  = (buffer[index] & DIRECTION_MASK) >> 7;
         short frequency  = (short)((buffer[index] & FREQUENCY_MASK) << 8 + (buffer[index + 1] & 0xFF));
         int   adjustment = 0;
         index += 2;
         System.arraycopy(buffer, index, data, 0, data.length);

         switch (data.length)
         {
            case 4:
                 adjustment = ((data[index] & 0xFF) << 24) + ((data[index + 1] & 0xFF) << 16) + ((data[index + 2] & 0xFF) << 8) + (data[index + 3] & 0xFF);
            break;
            case 3:
                 adjustment = ((data[index] & 0xFF) << 16) + ((data[index + 1] & 0xFF) << 8 ) +  (data[index + 1] & 0xFF);
            break;
            case 2:
                 adjustment = ((data[index] & 0xFF) << 8 ) +  (data[index + 1] & 0xFF);
            break;
            case 1:
                 adjustment = (data[index] & 0xFF);
            break;
         }
         index += data.length;
         levels.add(new Level(Level.Direction.getDirection(direction), frequency, adjustment));
      }
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
         byte[] data   = new byte[2 + getNumAdjustmentBytes()];
                buffer = new byte[1 + levels.size() * data.length];
         int    index  = 1;

         buffer[0] = (byte)numAdjustmentBits;
         for(Level level : levels)
         {
            System.arraycopy(shortToBytes(level.getFrequency()), 0, data, 0, 2);
            data[0] = (byte)(level.getDirection().ordinal() == 1 ? data[0] | DIRECTION_MASK : data[0] & ~DIRECTION_MASK);
            System.arraycopy(intToBytes(level.getAdjustment()), 4 - getNumAdjustmentBytes(), data, 2, data.length - 2);
            System.arraycopy(data, 0, buffer, index, data.length);
         }
         dirty = false;    // data has already been saved
      }
   }

   /**
    * gets a string representation of the <i>equalization</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: equalization\n");
      buffer.append("   bytes..............: " + this.buffer.length    + " bytes\n");
      buffer.append("                        " + hex(this.buffer, 24)  + "\n");
      buffer.append("   num adjustment bits: " + numAdjustmentBits     + "\n");
      buffer.append("   EQ levels..........: " + levels.size()         + "\n");
      for(Level level : levels)
         buffer.append("                        " + level              + "\n");

      return buffer.toString();
   }
}
