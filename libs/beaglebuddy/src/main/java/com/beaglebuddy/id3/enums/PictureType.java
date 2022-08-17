package com.beaglebuddy.id3.enums;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * valid ID3v2.x picture types.
 */
public enum PictureType
{                                                                                   /** Other                               */
   OTHER             ("Other"             , "Other"                              ), /** 32x32 pixels 'file icon' (PNG only) */
   SMALL_ICON        ("32 x 32 Icon"      , "32x32 pixels 'file icon' (PNG only)"), /** Other file icon                     */
   OTHER_ICON        ("Other Icon"        , "Other file icon"                    ), /** CD Cover (front)                    */
   FRONT_COVER       ("Front Cover"       , "Cover (front)"                      ), /** CD Cover (back)                     */
   BACK_COVER        ("Back Cover"        , "Cover (back)"                       ), /** CD leaflet page                     */
   LEAFLET_PAGE      ("Leaflet Page"      , "Leaflet page"                       ), /** Media (eg lable side of CD)         */
   MEDIA             ("Media"             , "Media (e.g. lable side of CD)"      ), /** Lead artist/lead performer/soloist  */
   LEAD_ARTIST       ("Lead Artist"       , "Lead artist/lead performer/soloist" ), /** Artist/performer                    */
   ARTIST            ("Artist"            , "Artist/performer"                   ), /** Conductor                           */
   CONDUCTOR         ("Conductor"         , "Conductor"                          ), /** Band/Orchestra                      */
   BAND              ("Band"              , "Band/Orchestra"                     ), /** Composer                            */
   COMPOSER          ("Composer"          , "Composer"                           ), /** Lyricist/text writer                */
   LYRICIST          ("Lyricist"          , "Lyricist/text writer"               ), /** Recording Location                  */
   RECORDING_LOCATION("Recording Location", "Recording Location"                 ), /** During recording                    */
   DURING_RECORDING  ("During Recording"  , "During recording"                   ), /** During performance                  */
   DURING_PERFORMANCE("During Performance", "During performance"                 ), /** Movie/video screen capture          */
   SCREEN_CAPTURE    ("Screen Capture"    , "Movie/video screen capture"         ), /** A bright coloured fish              */
   INVALID           ("Invalid"           , "A bright coloured fish"             ), /** Illustration                        */
   ILLUSTRATION      ("Illustration"      , "Illustration"                       ), /** Band/artist logotype                */
   BAND_LOGO         ("Band Logo"         , "Band/artist logotype"               ), /** Publisher/Studio logotype           */
   PUBLISHER_LOGO    ("Publisher Logo"    , "Publisher/Studio logotype"          );

   // data members
   private String name;
   private String description;

   /**
    * constructor.
    * @param name          picture type.
    * @param description   description of the picture type.
    */
   private PictureType(String name, String description)
   {
      this.name = name;
      this.description = description;
   }

   /**
    * gets the description of the picture type.
    * @return the description of the picture type.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * converts an integral value to its corresponding picture type enum.
    * @return a picture type enum corresponding to the integral value.
    * @param pictureType  integral value to be converted to a picture type enum.
    * @throws IllegalArgumentException   if the value is not a valid picture type.
    */
   public static PictureType valueOf(byte pictureType) throws IllegalArgumentException
   {
      for (PictureType p : PictureType.values())
         if (pictureType == p.ordinal())
            return p;
      throw new IllegalArgumentException("Invalid picture type " + pictureType + ".");
   }

   /**
    * gets a string representation of the picture type enum.
    * @return a string representation of the picture type enum.
    */
   @Override
   public String toString()
   {
      return "" + ordinal() + " - " + name;
   }
}
