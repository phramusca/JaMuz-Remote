package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.v24.Encoding;
import com.beaglebuddy.id3.enums.v24.FrameType;
import com.beaglebuddy.id3.pojo.v24.Level;





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
 * An ID3v2.4 <i>equalization</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#EQUALIZATION EQU2} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to specify points on an equalization (EQ)
 * curve.  You may specify as many {@link com.beaglebuddy.id3.pojo.v24.Level points} as you want, but typically you specify 16 or 32, much like the 32 band equalizers you can buy for your home stereo.
 * The <i>equalization</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Equalization Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link ID3v24FrameBodyEqualization.InterpolationMethod interpolation method}</td><td class="beaglebuddy">method to use when interpolating between points on the EQ curve.                     </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">identification                                                              </td><td class="beaglebuddy">uniquely identifies the situation and/or device when this eq curve should be applied.</td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.pojo.v24.Level levels}                           </td><td class="beaglebuddy">list of the points on the EQ curve.                                                  </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>equalization</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}, but only one with the same identification string.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyEqualization extends ID3v24FrameBody
{
   /** valid ID3v2.4 equalization interpolation methods */
   public enum InterpolationMethod
   {                  /**  No interpolation is made. A jump from one adjustment level to another occurs in the middle between two points on the EQ curve. */
      BAND  ,         /** Interpolation between adjustment points is linear.  That is, a straight line is drawn between two points on the EQ curve.       */
      LINEAR;

      /** @return a string representation of the Interpolation Method */
      public String toString()  {return "" + ordinal() + " - " + super.toString().toLowerCase();}

      /**
       * convert an integral value to its corresponding enum.
       * @param interpolationMethod   the integral value that is to be converted to an InterpolationMethod enum.
       * @return the InterpolationMethod enum whose ordinal value corresponds to the given integral value.
       * @throws IllegalArgumentException   if there is no InterpolationMethod enum whose ordinal value corresponds to the given integral value.
       */
      public static InterpolationMethod getInterpolationMethod(int interpolationMethod)
      {
         for (InterpolationMethod i : InterpolationMethod.values())
            if (interpolationMethod == i.ordinal())
               return i;
         throw new IllegalArgumentException("Invalid ID3v2.4 interpolation method " + interpolationMethod + ".  It must be either 0 or 1.");
      }
   }



   // data members
   private InterpolationMethod interpolationMethod; // method to use when interpolating between points on the EQ curve.
   private String              identification;      // identify the situation and/or device where this EQ curve should be applied.
   private List<Level>         levels;              // equalization levels defining the EQ curve.



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>use LINEAR interpolation between points on the EQ curve</li>
    *    <li>"standard" EQ curve</li>
    *    <li>no levels, ie, a flat EQ curve</li>
    * </ul>
    */
   public ID3v24FrameBodyEqualization()
   {
      this(InterpolationMethod.LINEAR, "standard", new Vector<Level>());
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param interpolationMethod   method to use when interpolating between points on the EQ curve.
    * @param identification        uniquely identifies the situation and/or device when this EQ curve should be applied.
    * @param levels                points comprising the equalization curve.
    */
   public ID3v24FrameBodyEqualization(InterpolationMethod interpolationMethod, String identification, Level[] levels)
   {
      super(FrameType.EQUALIZATION);

      setInterpolationMethod(interpolationMethod);
      setIdentification     (identification);
      setLevels             (levels);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param interpolationMethod   method to use when interpolating between points on the EQ curve.
    * @param identification        uniquely identifies the situation and/or device when this EQ curve should be applied.
    * @param levels                points comprising the equalization curve.
    */
   public ID3v24FrameBodyEqualization(InterpolationMethod interpolationMethod, String identification, List<Level> levels)
   {
      super(FrameType.EQUALIZATION);

      setInterpolationMethod(interpolationMethod);
      setIdentification     (identification);
      setLevels             (levels);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an equalization frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyEqualization(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.EQUALIZATION, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      setInterpolationMethod(InterpolationMethod.getInterpolationMethod(buffer[0]));
      nullTerminatorIndex = getNextNullTerminator(1, Encoding.ISO_8859_1);
      setIdentification(new String(buffer, 1, nullTerminatorIndex-1, Encoding.ISO_8859_1.getCharacterSet()).trim());

      int index  = nullTerminatorIndex + 1;
          levels = new Vector<Level>();

      while (index < buffer.length)
      {
         short frequency = bytesToShort(buffer, index);
         index += 2;
         short volume    = bytesToShort(buffer, index);
         index += 2;
         levels.add(new Level((short)(frequency / 2), volume * 512.0));
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * get the method to use when interpolating between points on the EQ curve.
    * @return  the method to use when interpolating between points on the EQ curve.
    * @see #setInterpolationMethod(InterpolationMethod)
    */
   public InterpolationMethod getInterpolationMethod()
   {
      return interpolationMethod;
   }

   /**
    * set the method to use when interpolating between points on the EQ curve.
    * @param interpolationMethod   method to use when interpolating between points on the EQ curve.
    * @see #getInterpolationMethod()
    */
   public void setInterpolationMethod(InterpolationMethod interpolationMethod)
   {
      this.interpolationMethod = interpolationMethod;
      this.dirty               = true;
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
      this.dirty          = true;
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
      this.levels = levels == null ? new Vector<Level>() : levels;
      this.dirty  = true;
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
         byte[] idenitificationBytes = stringToBytes(Encoding.ISO_8859_1, identification);
         int    index                = 0;
         buffer                      = new byte[1 + idenitificationBytes.length + levels.size() * 4];

         buffer[0] = (byte)interpolationMethod.ordinal();
         index = 1;
         System.arraycopy(idenitificationBytes, 0, buffer, index, idenitificationBytes.length);
         index += idenitificationBytes.length;

         for(Level level : levels)
         {
            System.arraycopy(shortToBytes((short)(level.getFrequency() * 2)), 0, buffer, index, 2);
            index += 2;
            System.arraycopy(shortToBytes((short)(level.getVolume() * 512 )), 0, buffer, index, 2);
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
      buffer.append("   bytes...............: " + this.buffer.length   + " bytes\n");
      buffer.append("                         " + hex(this.buffer, 25) + "\n");
      buffer.append("   interpolation method: " + interpolationMethod  + "\n");
      buffer.append("   identification......: " + identification       + "\n");
      buffer.append("   EQ levels...........: " + levels.size()        + "\n");
      for(Level level : levels)
         buffer.append("                         " + level             + "\n");

      return buffer.toString();
   }
}
